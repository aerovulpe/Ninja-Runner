package com.aerisvulpe.shinobi;

import java.io.IOException;

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
import org.anddev.andengine.util.Debug;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.widget.Toast;

import com.google.ads.AdRequest;
import com.google.ads.AdView;
import com.ironsource.mobilcore.MobileCore;
import com.ironsource.mobilcore.MobileCore.LOG_TYPE;
import com.aerisvulpe.shinobi.R;
import com.aerisvulpe.shinobi.customs.MySprite;
import com.aerisvulpe.shinobi.customs.MyTextureRegionFactory;
import com.aerisvulpe.shinobi.scenes.GameScene;
import com.aerisvulpe.shinobi.scenes.MainMenuScene;
import com.aerisvulpe.shinobi.scenes.SceneManager;


public class Main extends LayoutGameActivity {

	// Camera and Layout Variables
	public static Camera camera;
	public static LayoutGameActivity context;
	
	// Current scene variable
	public static String sceneName;
	
	// Sound check variable
	public static boolean soundOn= true;
	
	// Enable/Disable Ads
	public static boolean mobileCoreEnabled= true;
	public static boolean admobEnabled= true;
	
	// Game images Texture Region variables
	private static TextureRegion splashRegion;
	public static TextureRegion bgRegion;
	public static TextureRegion groundRegion;
	public static TextureRegion obstacleRegion;
	public static TextureRegion titleRegion;
	public static TextureRegion btnPlayRegion;
	public static TextureRegion btnPauseRegion;
	public static TextureRegion btnQuitRegion;
	public static TextureRegion btnResumeRegion;
	public static TextureRegion btnRestartRegion;
	public static TextureRegion btnMenuRegion;
	public static TextureRegion btnOnRegion;
	public static TextureRegion btnOffRegion;
	public static TextureRegion btnShareRegion;
	public static TextureRegion textBestRegion;
	public static TextureRegion textScoreRegion;
	public static TextureRegion textShareRegion;
	public static TiledTextureRegion stickmanRegion;
	
	// Font variable 
	public static Font textFont;
	
	// Background music and sound variables
	public static Music sLoop;
	public static Sound sClick;
	public static Sound sHurt;
	public static Sound sJump;
	public static Sound sSlide;
	public static Sound sOver;
		
	// Shared Preferences to save the state of Alert rating system.
    public static SharedPreferences settings;
    public static SharedPreferences.Editor editor;
    
    private boolean loaded;
    
    public static float volume;
	  
    public static Handler handler= new Handler();
    
    // Google Ad variable
	private static AdView adView;
	

	// Define and load the game engine 
	@Override
	public Engine onLoadEngine() {
		camera= new Camera(0, 0, GameConstants.CAMERA_WIDTH, GameConstants.CAMERA_HEIGHT);
		final EngineOptions eOptions= new EngineOptions(true, ScreenOrientation.LANDSCAPE, new FillResolutionPolicy(), camera);
		eOptions.setNeedsMusic(true);
		eOptions.setNeedsSound(true);
		eOptions.getTouchOptions().setRunOnUpdateThread(true);
		eOptions.getRenderOptions().disableExtensionVertexBufferObjects();
		final Engine engine= new Engine(eOptions);
		return engine;
	}

	@Override
	public void onLoadResources() {
		// Set resources location
		MyTextureRegionFactory.setAssetBasePath("gfx/");
		MusicFactory.setAssetBasePath("sounds/");
		SoundFactory.setAssetBasePath("sounds/");
        
		// load splash screen image
        final BitmapTextureAtlas splashTexture= new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        splashRegion= MyTextureRegionFactory.createFromAsset(splashTexture, this, "bgs/splash.png", 0, 0);
		getEngine().getTextureManager().loadTextures(splashTexture);

		SceneManager.init(this);
		context= this;
	}

	private void loadElements(){

		// Define and load Texture Atlases and Regions
		// for the images in game
		
        final BitmapTextureAtlas bgTexture= new BitmapTextureAtlas(256, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        bgRegion= MyTextureRegionFactory.createFromAsset(bgTexture, this, "bgs/bg.png", 0, 0);
		
        final BitmapTextureAtlas groundTexture= new BitmapTextureAtlas(1024, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        groundRegion= MyTextureRegionFactory.createFromAsset(groundTexture, this, "ground.png", 0, 0);
        
        final BitmapTextureAtlas obstacleTexture= new BitmapTextureAtlas(32, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        obstacleRegion= MyTextureRegionFactory.createFromAsset(obstacleTexture, this, "obs.png", 0, 0);
        
        final BitmapTextureAtlas stickmanTexture= new BitmapTextureAtlas(512, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        stickmanRegion= MyTextureRegionFactory.createTiledFromAsset(stickmanTexture, context, "stickman.png", 0, 0, 4, 2);
		
        final BitmapTextureAtlas titleTexture= new BitmapTextureAtlas(512, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        titleRegion= MyTextureRegionFactory.createFromAsset(titleTexture, context, "title-menu.png", 0, 0);
        
        final BitmapTextureAtlas btnPlayTexture= new BitmapTextureAtlas(128, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        btnPlayRegion= MyTextureRegionFactory.createFromAsset(btnPlayTexture, context, "btn-play.png", 0, 0);
        
        final BitmapTextureAtlas btnPauseTexture= new BitmapTextureAtlas(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        btnPauseRegion= MyTextureRegionFactory.createFromAsset(btnPauseTexture, context, "btn-pause.png", 0, 0);
        
        final BitmapTextureAtlas btnQuitTexture= new BitmapTextureAtlas(128, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        btnQuitRegion= MyTextureRegionFactory.createFromAsset(btnQuitTexture, context, "btn-quit.png", 0, 0);
        
        final BitmapTextureAtlas btnRestartTexture= new BitmapTextureAtlas(256, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        btnRestartRegion= MyTextureRegionFactory.createFromAsset(btnRestartTexture, context, "btn-restart.png", 0, 0);
        
        final BitmapTextureAtlas btnResumeTexture= new BitmapTextureAtlas(256, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        btnResumeRegion= MyTextureRegionFactory.createFromAsset(btnResumeTexture, context, "btn-resume.png", 0, 0);
        
        final BitmapTextureAtlas btnMenuTexture= new BitmapTextureAtlas(128, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        btnMenuRegion= MyTextureRegionFactory.createFromAsset(btnMenuTexture, context, "btn-menu.png", 0, 0);
        
        final BitmapTextureAtlas btnOnTexture= new BitmapTextureAtlas(64, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        btnOnRegion= MyTextureRegionFactory.createFromAsset(btnOnTexture, context, "btn-sOn.png", 0, 0);
        
        final BitmapTextureAtlas btnOffTexture= new BitmapTextureAtlas(64, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        btnOffRegion= MyTextureRegionFactory.createFromAsset(btnOffTexture, context, "btn-sOff.png", 0, 0);
        
        final BitmapTextureAtlas btnShareTexture= new BitmapTextureAtlas(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        btnShareRegion= MyTextureRegionFactory.createFromAsset(btnShareTexture, context, "btn-share.png", 0, 0);
        
        final BitmapTextureAtlas textShareTexture= new BitmapTextureAtlas(128, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        textShareRegion= MyTextureRegionFactory.createFromAsset(textShareTexture, context, "text-share.png", 0, 0);
        
        final BitmapTextureAtlas textBestTexture= new BitmapTextureAtlas(128, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        textBestRegion= MyTextureRegionFactory.createFromAsset(textBestTexture, context, "text-best.png", 0, 0);
        
        final BitmapTextureAtlas textScoreTexture= new BitmapTextureAtlas(128, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        textScoreRegion= MyTextureRegionFactory.createFromAsset(textScoreTexture, context, "text-score.png", 0, 0);
        
		getEngine().getTextureManager().loadTextures(bgTexture, groundTexture, obstacleTexture, titleTexture, btnPlayTexture, btnPauseTexture, btnQuitTexture,
				btnRestartTexture, btnResumeTexture, btnMenuTexture, btnOnTexture, btnOffTexture, btnShareTexture,
				textShareTexture, textBestTexture, textScoreTexture, stickmanTexture);
		
		// Define and load font
		final BitmapTextureAtlas fontTexture= new BitmapTextureAtlas(512, 512, TextureOptions.NEAREST_PREMULTIPLYALPHA);
		textFont= FontFactory.createFromAsset(fontTexture, context, "fonts/go3v2.ttf", 27, true, Color.WHITE);
		SceneManager.loadTexture(fontTexture);
		SceneManager.loadFont(textFont);

		// Load Background music
		try {
			sLoop = MusicFactory.createMusicFromAsset(getEngine().getMusicManager(), this, "main.mp3");
			volume= sLoop.getVolume();
			sLoop.setVolume(volume/2);
			sLoop.setLooping(true);
		}
		catch (final IOException e) {
			//Debug.e(e);
		}
		
		// Load game sounds
		try {
			sClick = SoundFactory.createSoundFromAsset(getEngine().getSoundManager(), this, "tap.mp3");
			sHurt = SoundFactory.createSoundFromAsset(getEngine().getSoundManager(), this, "hurt.mp3");
			sJump = SoundFactory.createSoundFromAsset(getEngine().getSoundManager(), this, "jump.mp3");
			sSlide = SoundFactory.createSoundFromAsset(getEngine().getSoundManager(), this, "slide.mp3");
			sOver = SoundFactory.createSoundFromAsset(getEngine().getSoundManager(), this, "over.mp3");
		} catch (final IOException e) {
			Debug.e(e);
		}
		
		MainMenuScene.load(this);
		GameScene.load(this);
		
	}
	
	
	@Override
	public Scene onLoadScene() {
		// Create and load scene for splash screen
		final Scene scene= new Scene();
		scene.setBackground(new ColorBackground(255, 255, 255));		
		
		loaded= false;

		// Define and load splash image
		final MySprite mSplash= new MySprite(GameConstants.CAMERA_WIDTH/2- splashRegion.getWidth()/2, 
				GameConstants.CAMERA_HEIGHT/2- splashRegion.getHeight()/2, splashRegion);
		
		scene.attachChild(mSplash);
		
		// show splash screen for at least 1.5 seconds
		scene.registerUpdateHandler(new TimerHandler(3f, false, new ITimerCallback(){
			@Override
			public void onTimePassed(TimerHandler arg0) {
				scene.unregisterUpdateHandler(arg0);
				loaded= true;
			}})
		);
		
		// Create a callback for Menu screen when resources are loaded
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
		            		if(loaded){
		            			// Show menu screen if resources loaded
		            			SceneManager.setScene(MainMenuScene.run());
		            		}
		                }
		        });
		}};
		final Runnable async= new Runnable(){
			@Override
			public void run() {
				new AsyncTaskLoader().execute(callback);
			}
		};
		handler.post(async);
		return scene;
	}

	@Override
	public void onBackPressed(){
		if(sceneName!= null){
			if(sceneName=="Menu"){
				MainMenuScene.back();
			}else if(sceneName=="Game"){ 
				GameScene.back();
			}
		}
	}
	
	public static void shareIt(String msg){
		Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);	
		sharingIntent.setType("text/plain");
		sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, GameConstants.EMAIL_SUBJECT);
		sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, msg+ GameConstants.EMAIN_MESSAGE);
		context.startActivity(Intent.createChooser(sharingIntent, "Share via"));
	}
		
	@Override
	public void onLoadComplete() {
		// Initialize Admob Ad
		adView = (AdView)this.findViewById(R.id.adView);
        adView.loadAd(new AdRequest());
        
        // Initialize MobileCore Ad
        MobileCore.init(this, getString(R.string.mobilecore_ID), LOG_TYPE.DEBUG);
	}


	// Use this method to show Mobilecore offerwall
	public static void showMobileCoreOfferWall(){
		MobileCore.showOfferWall(context, null);
	}
	
	// Use this method to show AdMob Banner
	public static void showAdmobBanner(){
		final Runnable rev= new Runnable(){
			@Override
			public void run() {
				adView.setVisibility(0);
			}
		};
		handler.post(rev);
	}
	
	// Use this method to hide AdMob Banner
	public static void HideAdmobBanner(){
		final Runnable rev= new Runnable(){
			@Override
			public void run() {
				adView.setVisibility(8);
			}
		};
		handler.post(rev);
	}
	
	@Override
	protected int getLayoutID() {
		return R.layout.activity_main;
	}

	@Override
	protected int getRenderSurfaceViewID() {
		return R.id.xmllayoutexample_rendersurfaceview;
	}
	
	public static boolean isOnline() {
	    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
	        return true;
	    }
	    return false;
	}
	
	public static void showError(){
		final Runnable err= new Runnable(){
			@Override
			public void run() {
				Toast.makeText(context, "Please check your internet connection !!", Toast.LENGTH_LONG).show();
			}
		};
		handler.post(err);
	}

	@Override
    public void onPauseGame(){
		if(sLoop!= null){
			if(sLoop.isPlaying()){
				sLoop.pause();
			}
		}
	}
	
	@Override
    public void onResumeGame(){
		if(sLoop!= null){
			if(soundOn && (sceneName=="Game")){
				if(!sLoop.isPlaying()){
					sLoop.play();
				}
			}
		}
	}
}








