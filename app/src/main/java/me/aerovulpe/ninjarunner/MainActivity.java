package me.aerovulpe.ninjarunner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.ads.AdRequest;
import com.google.ads.AdView;
import com.ironsource.mobilcore.MobileCore;
import com.ironsource.mobilcore.MobileCore.LOG_TYPE;

import org.anddev.andengine.audio.music.Music;
import org.anddev.andengine.audio.music.MusicFactory;
import org.anddev.andengine.audio.sound.Sound;
import org.anddev.andengine.audio.sound.SoundFactory;
import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.engine.handler.timer.ITimerCallback;
import org.anddev.andengine.engine.handler.timer.TimerHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.font.FontFactory;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.ui.activity.LayoutGameActivity;

import java.io.IOException;

import me.aerovulpe.ninjarunner.customs.MySprite;
import me.aerovulpe.ninjarunner.customs.MyTextureRegionFactory;
import me.aerovulpe.ninjarunner.scenes.GameScene;
import me.aerovulpe.ninjarunner.scenes.MainMenuScene;
import me.aerovulpe.ninjarunner.scenes.SceneManager;

import static me.aerovulpe.ninjarunner.GameConstants.Scenes;

public class MainActivity extends LayoutGameActivity {

    private static final String LOG_TAG = MainActivity.class.getName();
    // Camera and Layout Variables
    public static Camera sCamera;
    public static LayoutGameActivity sContext;

    // Current scene
    public static Scenes sCurrentScene;

    // Sound check variable
    public static boolean sSoundOn = true;

    // Enable/Disable Ads
    public static boolean sMobileCoreEnabled = true;
    public static boolean sAdmobEnabled = true;
    public static TextureRegion sBgRegion;
    public static TextureRegion sGroundRegion;
    public static TextureRegion sObstacleRegion;
    public static TextureRegion sTitleRegion;
    public static TextureRegion sBtnPlayRegion;
    public static TextureRegion sBtnPauseRegion;
    public static TextureRegion sBtnQuitRegion;
    public static TextureRegion sBtnResumeRegion;
    public static TextureRegion sBtnRestartRegion;
    public static TextureRegion sBtnMenuRegion;
    public static TextureRegion sBtnOnRegion;
    public static TextureRegion sBtnOffRegion;
    public static TextureRegion sBtnShareRegion;
    public static TextureRegion sTextBestRegion;
    public static TextureRegion sTextScoreRegion;
    public static TextureRegion sTextShareRegion;
    public static TiledTextureRegion sNinjaRegion;
    // Font variable
    public static Font sTextFont;
    // Background music and sound variables
    public static Music sBackgroundMusic;
    public static Sound sClick;
    public static Sound sHurt;
    public static Sound sJump;
    public static Sound sSlide;
    public static Sound sOver;
    // Shared Preferences to save the state of Alert rating system.
    public static SharedPreferences sSettings;
    public static SharedPreferences.Editor sEditor;
    public static float sVolume;
    public static Handler sHandler = new Handler();
    // Game images Texture Region variables
    private static TextureRegion sSplashRegion;
    // Google Ad variable
    private static AdView sAdView;

    private boolean mLoaded;

    public static void shareIt(String msg) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, GameConstants.EMAIL_SUBJECT);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, msg + GameConstants.EMAIL_MESSAGE);
        sContext.startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    // Use this method to show Mobilecore offerwall
    public static void showMobileCoreOfferWall() {
        MobileCore.showOfferWall(sContext, null);
    }

    // Use this method to show AdMob Banner
    public static void showAdmobBanner() {
        final Runnable rev = new Runnable() {
            @Override
            public void run() {
                sAdView.setVisibility(View.VISIBLE);
            }
        };
        sHandler.post(rev);
    }

    // Use this method to hide AdMob Banner
    public static void hideAdmobBanner() {
        final Runnable rev = new Runnable() {
            @Override
            public void run() {
                sAdView.setVisibility(View.INVISIBLE);
            }
        };
        sHandler.post(rev);
    }

    public static boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) sContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public static void showError() {
        final Runnable err = new Runnable() {
            @Override
            public void run() {
                Toast.makeText(sContext, "Please check your internet connection !!", Toast.LENGTH_LONG).show();
            }
        };
        sHandler.post(err);
    }

    // Define and load the game engine
    @Override
    public Engine onLoadEngine() {
        sCamera = new Camera(0, 0, GameConstants.CAMERA_WIDTH, GameConstants.CAMERA_HEIGHT);
        final EngineOptions eOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE,
                new FillResolutionPolicy(), sCamera);
        eOptions.setNeedsMusic(true);
        eOptions.setNeedsSound(true);
        eOptions.getTouchOptions().setRunOnUpdateThread(true);
        eOptions.getRenderOptions().disableExtensionVertexBufferObjects();
        return new Engine(eOptions);
    }

    @Override
    public void onLoadResources() {
        // Set resources location
        MyTextureRegionFactory.setAssetBasePath("gfx/");
        MusicFactory.setAssetBasePath("sounds/");
        SoundFactory.setAssetBasePath("sounds/");

        // load splash screen image
        final BitmapTextureAtlas splashTexture = new BitmapTextureAtlas(256, 256,
                TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        sSplashRegion = MyTextureRegionFactory.createFromAsset(splashTexture, this,
                "bgs/splash.png", 0, 0);
        getEngine().getTextureManager().loadTextures(splashTexture);

        SceneManager.init(this);
        sContext = this;
    }

    private void loadElements() {

        // Define and load Texture Atlases and Regions
        // for the images in game

        final BitmapTextureAtlas bgTexture = new BitmapTextureAtlas(256, 512,
                TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        sBgRegion = MyTextureRegionFactory.createFromAsset(bgTexture, this,
                "bgs/bg.png", 0, 0);

        final BitmapTextureAtlas groundTexture = new BitmapTextureAtlas(1024, 256,
                TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        sGroundRegion = MyTextureRegionFactory.createFromAsset(groundTexture, this,
                "ground.png", 0, 0);

        final BitmapTextureAtlas obstacleTexture = new BitmapTextureAtlas(32, 32,
                TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        sObstacleRegion = MyTextureRegionFactory.createFromAsset(obstacleTexture, this,
                "obs.png", 0, 0);

        final BitmapTextureAtlas ninjaTexture = new BitmapTextureAtlas(512, 256,
                TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        sNinjaRegion = MyTextureRegionFactory.createTiledFromAsset(ninjaTexture, sContext,
                "ninja.png", 0, 0, 4, 2);

        final BitmapTextureAtlas titleTexture = new BitmapTextureAtlas(512, 128,
                TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        sTitleRegion = MyTextureRegionFactory.createFromAsset(titleTexture, sContext,
                "title-menu.png", 0, 0);

        final BitmapTextureAtlas btnPlayTexture = new BitmapTextureAtlas(128, 64,
                TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        sBtnPlayRegion = MyTextureRegionFactory.createFromAsset(btnPlayTexture, sContext,
                "btn-play.png", 0, 0);

        final BitmapTextureAtlas btnPauseTexture = new BitmapTextureAtlas(64, 64,
                TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        sBtnPauseRegion = MyTextureRegionFactory.createFromAsset(btnPauseTexture, sContext,
                "btn-pause.png", 0, 0);

        final BitmapTextureAtlas btnQuitTexture = new BitmapTextureAtlas(128, 64,
                TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        sBtnQuitRegion = MyTextureRegionFactory.createFromAsset(btnQuitTexture, sContext,
                "btn-quit.png", 0, 0);

        final BitmapTextureAtlas btnRestartTexture = new BitmapTextureAtlas(256, 64,
                TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        sBtnRestartRegion = MyTextureRegionFactory.createFromAsset(btnRestartTexture, sContext,
                "btn-restart.png", 0, 0);

        final BitmapTextureAtlas btnResumeTexture = new BitmapTextureAtlas(256, 64,
                TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        sBtnResumeRegion = MyTextureRegionFactory.createFromAsset(btnResumeTexture, sContext,
                "btn-resume.png", 0, 0);

        final BitmapTextureAtlas btnMenuTexture = new BitmapTextureAtlas(128, 64,
                TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        sBtnMenuRegion = MyTextureRegionFactory.createFromAsset(btnMenuTexture, sContext,
                "btn-menu.png", 0, 0);

        final BitmapTextureAtlas btnOnTexture = new BitmapTextureAtlas(64, 128,
                TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        sBtnOnRegion = MyTextureRegionFactory.createFromAsset(btnOnTexture, sContext,
                "btn-sOn.png", 0, 0);

        final BitmapTextureAtlas btnOffTexture = new BitmapTextureAtlas(64, 128,
                TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        sBtnOffRegion = MyTextureRegionFactory.createFromAsset(btnOffTexture, sContext,
                "btn-sOff.png", 0, 0);

        final BitmapTextureAtlas btnShareTexture = new BitmapTextureAtlas(64, 64,
                TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        sBtnShareRegion = MyTextureRegionFactory.createFromAsset(btnShareTexture, sContext,
                "btn-share.png", 0, 0);

        final BitmapTextureAtlas textShareTexture = new BitmapTextureAtlas(128, 64,
                TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        sTextShareRegion = MyTextureRegionFactory.createFromAsset(textShareTexture, sContext,
                "text-share.png", 0, 0);

        final BitmapTextureAtlas textBestTexture = new BitmapTextureAtlas(128, 64,
                TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        sTextBestRegion = MyTextureRegionFactory.createFromAsset(textBestTexture, sContext,
                "text-best.png", 0, 0);

        final BitmapTextureAtlas textScoreTexture = new BitmapTextureAtlas(128, 64,
                TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        sTextScoreRegion = MyTextureRegionFactory.createFromAsset(textScoreTexture, sContext,
                "text-score.png", 0, 0);

        getEngine().getTextureManager().loadTextures(bgTexture, groundTexture, obstacleTexture,
                titleTexture, btnPlayTexture, btnPauseTexture, btnQuitTexture,
                btnRestartTexture, btnResumeTexture, btnMenuTexture, btnOnTexture,
                btnOffTexture, btnShareTexture, textShareTexture, textBestTexture,
                textScoreTexture, ninjaTexture);

        // Define and load font
        final BitmapTextureAtlas fontTexture = new BitmapTextureAtlas(512, 512,
                TextureOptions.NEAREST_PREMULTIPLYALPHA);
        sTextFont = FontFactory.createFromAsset(fontTexture, sContext,
                "fonts/go3v2.ttf", 27, true, Color.WHITE);
        SceneManager.loadTexture(fontTexture);
        SceneManager.loadFont(sTextFont);

        // Load Background music
        try {
            sBackgroundMusic = MusicFactory.createMusicFromAsset(getEngine().getMusicManager(),
                    this, "main.mp3");
            sVolume = sBackgroundMusic.getVolume();
            sBackgroundMusic.setVolume(sVolume / 2);
            sBackgroundMusic.setLooping(true);
        } catch (final IOException e) {
            Log.e(LOG_TAG, "Error loading background music.", e);
        }

        // Load game sounds
        try {
            sClick = SoundFactory.createSoundFromAsset(getEngine().getSoundManager(), this, "tap.mp3");
            sHurt = SoundFactory.createSoundFromAsset(getEngine().getSoundManager(), this, "hurt.mp3");
            sJump = SoundFactory.createSoundFromAsset(getEngine().getSoundManager(), this, "jump.mp3");
            sSlide = SoundFactory.createSoundFromAsset(getEngine().getSoundManager(), this, "slide.mp3");
            sOver = SoundFactory.createSoundFromAsset(getEngine().getSoundManager(), this, "over.mp3");
        } catch (final IOException e) {
            Log.e(LOG_TAG, "Error loading game sounds.", e);
        }

        MainMenuScene.setContext(this);
    }

    @Override
    public Scene onLoadScene() {
        // Create and load scene for splash screen
        final Scene scene = new Scene();
        scene.setBackground(new ColorBackground(255, 255, 255));

        mLoaded = false;

        // Define and load splash image
        final MySprite mSplash = new MySprite(GameConstants.CAMERA_WIDTH / 2 - sSplashRegion.getWidth() / 2,
                GameConstants.CAMERA_HEIGHT / 2 - sSplashRegion.getHeight() / 2, sSplashRegion);

        scene.attachChild(mSplash);

        // show splash screen for at least 1.5 seconds
        scene.registerUpdateHandler(new TimerHandler(3f, false, new ITimerCallback() {
                    @Override
                    public void onTimePassed(TimerHandler arg0) {
                        scene.unregisterUpdateHandler(arg0);
                        mLoaded = true;
                    }
                })
        );

        // Create a callback for Menu screen when resources are mLoaded
        final IAsyncCallback callback = new IAsyncCallback() {

            public void workToDo() {
                loadElements();
            }

            public void onComplete() {
                scene.registerUpdateHandler(new IUpdateHandler() {

                    @Override
                    public void reset() {

                    }

                    @Override
                    public void onUpdate(float pSecondsElapsed) {
                        if (mLoaded) {
                            // Show menu screen if resources mLoaded
                            SceneManager.setScene(MainMenuScene.run());
                        }
                    }
                });
            }
        };
        final Runnable async = new Runnable() {
            @Override
            public void run() {
                new AsyncTaskLoader().execute(callback);
            }
        };
        sHandler.post(async);
        return scene;
    }

    @Override
    public void onBackPressed() {
        if (sCurrentScene != null) {
            if (sCurrentScene == Scenes.MENU_SCENE) {
                MainMenuScene.back();
            } else if (sCurrentScene == Scenes.GAME_SCENE) {
                GameScene.back();
            }
        }
    }

    @Override
    public void onLoadComplete() {
        // Initialize Admob Ad
        sAdView = (AdView) this.findViewById(R.id.adView);
        sAdView.loadAd(new AdRequest());

        // Initialize MobileCore Ad
        MobileCore.init(this, getString(R.string.mobilecore_ID), LOG_TYPE.DEBUG);
    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_main;
    }

    @Override
    protected int getRenderSurfaceViewID() {
        return R.id.rendersurfaceview;
    }

    @Override
    public void onPauseGame() {
        if (sBackgroundMusic != null) {
            if (sBackgroundMusic.isPlaying()) {
                sBackgroundMusic.pause();
            }
        }
    }

    @Override
    public void onResumeGame() {
        if (sBackgroundMusic != null) {
            if (sSoundOn && (sCurrentScene == Scenes.GAME_SCENE)) {
                if (!sBackgroundMusic.isPlaying()) {
                    sBackgroundMusic.play();
                }
            }
        }
    }
}