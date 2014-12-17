package me.aerovulpe.ninjarunner.scenes;

import me.aerovulpe.ninjarunner.GameConstants;
import me.aerovulpe.ninjarunner.MainActivity;
import me.aerovulpe.ninjarunner.customs.MySprite;
import me.aerovulpe.ninjarunner.customs.Ninja;

import org.anddev.andengine.engine.handler.timer.ITimerCallback;
import org.anddev.andengine.engine.handler.timer.TimerHandler;
import org.anddev.andengine.entity.modifier.PathModifier;
import org.anddev.andengine.entity.modifier.PathModifier.Path;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.ui.activity.LayoutGameActivity;

import static me.aerovulpe.ninjarunner.GameConstants.Scenes;

public class MainMenuScene {

    // Color values for the ground line and obstacles
    public static float sRed = 125 / 255f;
    public static float sGreen = 125 / 255f;
    public static float sBlue = 125 / 255f;
    // Context and Scene variables
    private static LayoutGameActivity sContext;
    private static Scene sScene;

    public static void setContext(LayoutGameActivity context) {
        MainMenuScene.sContext = context;
    }

    public static Scene run() {
        // Create a sScene
        sScene = new Scene();

        // Set current scene as Menu Scene
        MainActivity.sCurrentScene = Scenes.MENU_SCENE;

        // Show admob banner
        if (MainActivity.sAdmobEnabled) {
            MainActivity.showAdmobBanner();
        }

        // Play Game Music
        MainActivity.sBackgroundMusic.seekTo(0);
        MainActivity.sBackgroundMusic.setVolume(MainActivity.sVolume / 2);
        if (MainActivity.sSoundOn && !MainActivity.sBackgroundMusic.isPlaying()) {
            MainActivity.sBackgroundMusic.play();
        }

        // Create full screen background from the background image
        float backgroundStart = -50;
        for (int i = 0; i < 4; i++) {
            final MySprite background = new MySprite(0, 0, MainActivity.sBgRegion);
            background.setPosition(backgroundStart,
                    GameConstants.CAMERA_HEIGHT - background.getHeight());
            backgroundStart += background.getWidth() - 2;
            sScene.attachChild(background);
        }

        // Create the ground
        final MySprite ground = new MySprite(-5, 400, MainActivity.sGroundRegion);
        sScene.attachChild(ground);

        // Create and add Title image to sScene
        final MySprite title = new MySprite(0, 0, MainActivity.sTitleRegion);
        title.setPosition(GameConstants.CAMERA_WIDTH / 2 - title.getWidth() / 2, 30);
        sScene.attachChild(title);

        // Play Button
        final MySprite btnPlay = new MySprite(0, 0, MainActivity.sBtnPlayRegion) {
            private boolean down = false;

            public boolean onAreaTouched(TouchEvent event, float pTouchAreaLocalX,
                                         float pTouchAreaLocalY) {
                if (event.isActionDown()) {
                    this.setColor(0.7f, 0.7f, 0.7f);
                    down = true;
                } else if (down && event.isActionUp()) {
                    this.setColor(1, 1, 1);
                    down = false;
                    if (MainActivity.sSoundOn) {
                        MainActivity.sClick.play();
                    }
                    // Load game sScene on clicked
                    SceneManager.setScene(GameScene.run());
                }
                return true;
            }
        };
        btnPlay.setPosition(GameConstants.CAMERA_WIDTH / 2 - btnPlay.getWidth() / 2,
                title.getY() + title.getHeight() + 60);
        sScene.attachChild(btnPlay);
        sScene.registerTouchArea(btnPlay);

        // Quit Button
        final MySprite btnQuit = new MySprite(0, 0, MainActivity.sBtnQuitRegion) {
            private boolean down = false;

            public boolean onAreaTouched(TouchEvent event, float pTouchAreaLocalX,
                                         float pTouchAreaLocalY) {
                if (event.isActionDown()) {
                    this.setColor(0.7f, 0.7f, 0.7f);
                    down = true;
                } else if (down && event.isActionUp()) {
                    this.setColor(1, 1, 1);
                    down = false;

                    if (MainActivity.sSoundOn) {
                        MainActivity.sClick.play();
                    }
                    // Quit game
                    MainActivity.sContext.finish();
                }
                return true;
            }
        };
        btnQuit.setPosition(GameConstants.CAMERA_WIDTH / 2 - btnQuit.getWidth() / 2,
                btnPlay.getY() + btnPlay.getHeight() + 20);
        sScene.attachChild(btnQuit);
        sScene.registerTouchArea(btnQuit);

        // Sound On/ Off button
        final MySprite btnSoundOff = new MySprite(0, 0, MainActivity.sBtnOffRegion.deepCopy());
        btnSoundOff.setPosition(GameConstants.CAMERA_WIDTH - btnSoundOff.getWidth() - 10, 10);
        final MySprite btnSoundOn = new MySprite(0, 0, MainActivity.sBtnOnRegion.deepCopy()) {
            boolean down = false;

            public boolean onAreaTouched(TouchEvent event, float touchX, float touchY) {
                if (event.isActionDown()) {
                    down = true;
                    this.setColor(0.7f, 0.7f, 0.7f);
                } else if (event.isActionUp() && down) {
                    down = false;
                    this.setColor(1, 1, 1);
                    if (MainActivity.sSoundOn) {
                        MainActivity.sSoundOn = false;
                        this.setVisible(false);
                        btnSoundOff.setVisible(true);
                        MainActivity.sBackgroundMusic.pause();
                    } else {
                        MainActivity.sSoundOn = true;
                        this.setVisible(true);
                        btnSoundOff.setVisible(false);
                        MainActivity.sBackgroundMusic.play();
                    }
                    if (MainActivity.sSoundOn) {
                        MainActivity.sClick.play();
                    }
                }
                return true;
            }
        };
        btnSoundOn.setPosition(btnSoundOff.getX() + btnSoundOff.getWidth() - btnSoundOn.getWidth(),
                btnSoundOff.getY() + btnSoundOff.getHeight() - btnSoundOn.getHeight());
        if (MainActivity.sSoundOn) {
            btnSoundOn.setVisible(true);
            btnSoundOff.setVisible(false);
        } else {
            btnSoundOn.setVisible(false);
            btnSoundOff.setVisible(true);
        }
        sScene.attachChild(btnSoundOff);
        sScene.attachChild(btnSoundOn);
        sScene.registerTouchArea(btnSoundOn);

        // Add the ninja to the Scene
        final Ninja ninja = new Ninja(MainActivity.sNinjaRegion);
        ninja.setPosition(-150, 290);
        ninja.addToScene(sScene);

        // After 1 seconds, make the ninja appear from the left side of screen
        sScene.registerUpdateHandler(new TimerHandler(1f, false, new ITimerCallback() {
                    @Override
                    public void onTimePassed(TimerHandler pTimerHandler) {
                        sScene.unregisterUpdateHandler(pTimerHandler);
                        ninja.getOptions().registerEntityModifier(new PathModifier(0.7f, new Path(2).to(-75, 312).to(100, 312)));
                    }
                })
        );

        sScene.setTouchAreaBindingEnabled(true);
        return sScene;
    }

    public static void back() {
        sContext.finish();
    }

}





