package me.aerovulpe.ninjarunner.scenes;

import org.anddev.andengine.engine.handler.physics.PhysicsHandler;
import org.anddev.andengine.engine.handler.timer.ITimerCallback;
import org.anddev.andengine.engine.handler.timer.TimerHandler;
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.entity.text.Text;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.util.MathUtils;

import java.util.ArrayList;

import me.aerovulpe.ninjarunner.GameConstants;
import me.aerovulpe.ninjarunner.MainActivity;
import me.aerovulpe.ninjarunner.customs.MySprite;
import me.aerovulpe.ninjarunner.customs.Ninja;
import me.aerovulpe.ninjarunner.customs.Obstacle;

import static me.aerovulpe.ninjarunner.GameConstants.HIGH_SCORE_KEY;

public class GameScene {

    public static boolean sJumping;
    public static boolean sSliding;
    // Last obstacle variable
    public static Obstacle sLastObstacle;
    // Other variables
    public static int sMin;
    public static int sMax;
    public static int sDist;
    public static int sLifeNum;
    public static int sScoreNum;
    public static float sVelocity;
    public static float sJumpTime;
    public static long sJumpDur;
    // Scene variables
    private static Scene sPlayScene;
    private static Scene sPausedScene;
    // Boolean flag variables
    private static boolean sIsPaused;
    private static boolean sIsOver;
    private static int sBestNum;
    private static PhysicsHandler sGround1Handler;
    private static PhysicsHandler sGround2Handler;
    private static MySprite sGround1;
    private static MySprite sGround2;


    public static Scene run() {
        // Create a sScene
        sPlayScene = new Scene();

        // If the background image doesn't load, this color will show
        sPlayScene.setBackground(new ColorBackground(GameConstants.bgRed / 255f,
                GameConstants.bgGreen / 255f, GameConstants.bgBlue / 255f));

        // Set current scene name as Game Scene
        MainActivity.sCurrentScene = GameConstants.Scenes.GAME_SCENE;

        // Hide admob banner
        MainActivity.hideAdmobBanner();

        // Play game music
        MainActivity.sBackgroundMusic.seekTo(0);
        MainActivity.sBackgroundMusic.setVolume(MainActivity.sVolume / 4);
        if (MainActivity.sSoundOn && !MainActivity.sBackgroundMusic.isPlaying()) {
            MainActivity.sBackgroundMusic.play();
        }

        // Initialize game variables
        sJumping = false;
        sSliding = false;
        sIsPaused = false;
        sIsOver = false;

        sLifeNum = 3;
        sScoreNum = 0;

        sVelocity = -200;
        sMin = 600;
        sMax = 650;
        sDist = 0;

        sJumpDur = 400;
        sJumpTime = 1.0f;

        // Load best value from Shared Preferences, which also acts as internal storage
        MainActivity.sSettings = MainActivity.sContext.getSharedPreferences(GameConstants.PREFS_NAME,
                MainActivity.MODE_PRIVATE);
        sBestNum = MainActivity.sSettings.getInt(HIGH_SCORE_KEY, 0);

        // Create full screen background from the background image
        final ArrayList<MySprite> background = new ArrayList<>();
        float bgStart = -10;
        for (int i = 0; i < 5; i++) {
            final int num = i;
            final MySprite bg = new MySprite(0, 0, MainActivity.sBgRegion) {
                protected void onManagedUpdate(float pSecondsElapsed) {
                    if ((this.getX() + this.getWidth() + 10) < 0) {
                        if (num > 0) {
                            this.setPosition(background.get(num - 1).getX() + this.getWidth() - 4,
                                    this.getY());
                        } else {
                            this.setPosition(background.get(4).getX() + this.getWidth() - 4,
                                    this.getY());
                        }
                    }
                    super.onManagedUpdate(pSecondsElapsed);
                }
            };
            bg.setPosition(bgStart, GameConstants.CAMERA_HEIGHT - bg.getHeight());
            bgStart += bg.getWidth() - 2;

            final PhysicsHandler bgHandler = new PhysicsHandler(bg);
            bg.registerUpdateHandler(bgHandler);
            bgHandler.setVelocityX(sVelocity / 1.5f);

            sPlayScene.attachChild(bg);
            background.add(bg);
        }

        // Create the ground line
        sGround1 = new MySprite(0, 400, MainActivity.sGroundRegion) {
            private float tempVel = GameScene.sVelocity;

            protected void onManagedUpdate(float pSecondsElapsed) {
                if (this.getX() + this.getWidth() < 0) {
                    this.setPosition(sGround2.getX() + sGround2.getWidth() - 4, this.getY());
                }
                if (tempVel != GameScene.sVelocity) {
                    tempVel = GameScene.sVelocity;
                    sGround1Handler.setVelocityX(GameScene.sVelocity);
                }
                super.onManagedUpdate(pSecondsElapsed);
            }
        };
        sGround2 = new MySprite(sGround1.getX() + sGround1.getWidth() - 4, 400,
                MainActivity.sGroundRegion.deepCopy()) {
            private float tempVel = GameScene.sVelocity;

            protected void onManagedUpdate(float pSecondsElapsed) {
                if (this.getX() + this.getWidth() < 0) {
                    this.setPosition(sGround1.getX() + sGround1.getWidth() - 4, this.getY());
                }
                if (tempVel != GameScene.sVelocity) {
                    tempVel = GameScene.sVelocity;
                    sGround2Handler.setVelocityX(GameScene.sVelocity);
                }
                super.onManagedUpdate(pSecondsElapsed);
            }
        };

        sGround1Handler = new PhysicsHandler(sGround1);
        sGround2Handler = new PhysicsHandler(sGround2);

        sGround1.registerUpdateHandler(sGround1Handler);
        sGround2.registerUpdateHandler(sGround2Handler);

        sGround1Handler.setVelocityX(sVelocity);
        sGround2Handler.setVelocityX(sVelocity);

        sPlayScene.attachChild(sGround1);
        sPlayScene.attachChild(sGround2);

        // Create the ninja and add him to the scene
        final Ninja ninja = new Ninja(MainActivity.sNinjaRegion);
        ninja.setPosition(150, 304);
        ninja.addToScene(sPlayScene);

        // Show info text
        final Text info = new Text(0, 0, MainActivity.sTextFont, "Tap LEFT side of screen to SLIDE!\n" +
                "Tap RIGHT side of screen to JUMP!");
        info.setPosition(GameConstants.CAMERA_WIDTH / 2 - info.getWidth() / 2, 130);
        info.setColor(0, 0, 0);
        sPlayScene.attachChild(info);

        // Set touch listener, left side for sliding and right for jumping
        sPlayScene.setOnSceneTouchListener(new IOnSceneTouchListener() {
                                               @Override
                                               public boolean onSceneTouchEvent(Scene pScene, TouchEvent event) {
                                                   if (event.getX() < 400) {
                                                       if (event.isActionDown() && !sJumping && !sSliding) {
                                                           sSliding = true;
                                                           ninja.slide();
                                                           if (info.isVisible()) {
                                                               info.setVisible(false);
                                                           }
                                                       }
                                                   } else {
                                                       if (event.isActionDown() && !sJumping && !sSliding) {
                                                           sJumping = true;
                                                           ninja.jump();
                                                           if (info.isVisible()) {
                                                               info.setVisible(false);
                                                           }
                                                       }
                                                   }
                                                   return false;
                                               }
                                           }
        );

        // Create and add obstacles to the sScene
        float x = 700;
        for (int i = 0; i < 5; i++) {
            final Obstacle obstacle = new Obstacle(x + MathUtils.random(sMin, sMax), ninja);
            if (i == 4) {
                sLastObstacle = obstacle;
            }
            x = obstacle.getX();
            obstacle.addToScene(sPlayScene);
        }

        // Increase score every 0.55 seconds
        sPlayScene.registerUpdateHandler(new TimerHandler(0.55f, true, new ITimerCallback() {
                    @Override
                    public void onTimePassed(TimerHandler pTimerHandler) {
                        sScoreNum += 1;
                    }
                })
        );

        // Create Text for score and add to sScene
        final ChangeableText scoreText = new ChangeableText(0, 7, MainActivity.sTextFont,
                "Score: 0123456789", "Score: 0123456789".length()) {
            private int tmpScore = sScoreNum;

            protected void onManagedUpdate(float pSecondsElapsed) {
                if (tmpScore != sScoreNum) {
                    tmpScore = sScoreNum;
                    this.setText("Score: " + sScoreNum);
                    this.setPosition(GameConstants.CAMERA_WIDTH / 2 - this.getWidth() / 2, 7);
                }
                super.onManagedUpdate(pSecondsElapsed);
            }
        };
        scoreText.setText("Score: 0123456789");
        scoreText.setText("Score: 0");
        scoreText.setPosition(GameConstants.CAMERA_WIDTH / 2 - scoreText.getWidth() / 2, 7);
        scoreText.setColor(64 / 255f, 59 / 255f, 29 / 255f);
        sPlayScene.attachChild(scoreText);

        // Pause Button
        final MySprite btnPause = new MySprite(0, 0, MainActivity.sBtnPauseRegion) {
            private boolean down = false;

            public boolean onAreaTouched(TouchEvent event, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (event.isActionDown()) {
                    this.setColor(0.7f, 0.7f, 0.7f);
                    down = true;
                } else if (down && event.isActionUp()) {
                    this.setColor(1, 1, 1);
                    down = false;
                    if (MainActivity.sSoundOn) {
                        MainActivity.sClick.play();
                    }
                    back();
                }
                return true;
            }
        };
        btnPause.setPosition(5, GameConstants.CAMERA_HEIGHT - btnPause.getHeight() - 5);
        sPlayScene.attachChild(btnPause);
        sPlayScene.registerTouchArea(btnPause);

        // Change difficulty of game every 15 seconds by increasing speed
        // and distance between the obstacles
        sPlayScene.registerUpdateHandler(new TimerHandler(15f, true, new ITimerCallback() {
                    int a = 0;

                    @Override
                    public void onTimePassed(TimerHandler pTimerHandler) {
                        a += 1;
                        if (a == 1) {
                            sVelocity -= 5;
                            sDist += 50;
                        } else if (a == 2) {
                            sVelocity -= 10;
                            sDist += 50;
                        } else if (a == 5) {
                            sVelocity -= -10;
                            sDist += 50;
                        } else if (a == 7) {
                            sVelocity -= 10;
                            sDist += 50;
                        } else if (a == 9) {
                            sVelocity -= 15;
                            sDist += 50;
                        } else if (a == 11) {
                            sVelocity -= 20;
                            sDist += 100;
                        } else if (a == 20) {
                            sVelocity -= 5;
                        }
                    }
                })
        );

        sPlayScene.setTouchAreaBindingEnabled(true);
        return sPlayScene;
    }

    // Back method during game to show pause screen and activate/deactivate ads
    public static void back() {
        if (!sIsOver) {
            if (!sIsPaused) {
                sPausedScene = createPauseScene();
                sPlayScene.setChildScene(sPausedScene, false, true, true);
            } else {
                sIsPaused = false;
                sPausedScene.back();
                MainActivity.hideAdmobBanner();
                if (MainActivity.sSoundOn && !MainActivity.sBackgroundMusic.isPlaying()) {
                    MainActivity.sBackgroundMusic.play();
                }
            }
        }
    }

    // Pause Screen Method
    private static Scene createPauseScene() {
        // Create and load scene
        final Scene pScene = new Scene();
        pScene.setBackgroundEnabled(false);

        if (MainActivity.sBackgroundMusic.isPlaying()) {
            MainActivity.sBackgroundMusic.pause();
        }

        if (MainActivity.sAdmobEnabled) {
            MainActivity.showAdmobBanner();
        }
        if (MainActivity.sMobileCoreEnabled) {
            MainActivity.showMobileCoreOfferWall();
        }

        // Make background blur
        final Rectangle blur = new Rectangle(0, 0, GameConstants.CAMERA_WIDTH,
                GameConstants.CAMERA_HEIGHT);
        blur.setColor(0, 0, 2.25f, 0.15f);
        pScene.attachChild(blur);

        // Resume Button
        final MySprite btnResume = new MySprite(0, 0, MainActivity.sBtnResumeRegion) {
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
                    back();
                }
                return true;
            }
        };
        btnResume.setPosition(GameConstants.CAMERA_WIDTH / 2 - btnResume.getWidth() / 2, 150);
        pScene.attachChild(btnResume);
        pScene.registerTouchArea(btnResume);

        // Menu Button
        final MySprite btnMenu = new MySprite(0, 0, MainActivity.sBtnMenuRegion) {
            private boolean down = false;

            public boolean onAreaTouched(TouchEvent event, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (event.isActionDown()) {
                    this.setColor(0.7f, 0.7f, 0.7f);
                    down = true;
                } else if (down && event.isActionUp()) {
                    this.setColor(1, 1, 1);
                    down = false;
                    if (MainActivity.sSoundOn) {
                        MainActivity.sClick.play();
                    }
                    SceneManager.setScene(MainMenuScene.run());
                }
                return true;
            }
        };
        btnMenu.setPosition(GameConstants.CAMERA_WIDTH / 2 - btnMenu.getWidth() / 2, btnResume.getY() + btnResume.getHeight() + 20);
        pScene.attachChild(btnMenu);
        pScene.registerTouchArea(btnMenu);

        sIsPaused = true;

        pScene.setTouchAreaBindingEnabled(true);
        return pScene;
    }

    // Game over Scene Method
    private static Scene createOverScene() {
        // create and load background
        final Scene overScene = new Scene();
        overScene.setBackgroundEnabled(false);

        // Stop music
        if (MainActivity.sBackgroundMusic.isPlaying()) {
            MainActivity.sBackgroundMusic.pause();
        }

        if (MainActivity.sAdmobEnabled) {
            MainActivity.showAdmobBanner();
        }
        if (MainActivity.sMobileCoreEnabled) {
            MainActivity.showMobileCoreOfferWall();
        }

        // Make background blur
        final Rectangle blur = new Rectangle(0, 0, GameConstants.CAMERA_WIDTH,
                GameConstants.CAMERA_HEIGHT);
        blur.setColor(0, 0, 2.25f, 0.15f);
        overScene.attachChild(blur);

        // Save best score if greater than earlier saved data
        if (sScoreNum > sBestNum) {
            sBestNum = sScoreNum;
            MainActivity.sSettings = MainActivity.sContext.getSharedPreferences(GameConstants.PREFS_NAME, MainActivity.MODE_PRIVATE);
            MainActivity.sEditor = MainActivity.sSettings.edit();
            MainActivity.sEditor.putInt(HIGH_SCORE_KEY, sScoreNum);
            MainActivity.sEditor.commit();
        }

        // Text for Current Score
        final MySprite textScore = new MySprite(250, 80, MainActivity.sTextScoreRegion);
        overScene.attachChild(textScore);
        final Text score = new Text(0, 0, MainActivity.sTextFont, "" + sScoreNum);
        score.setPosition(textScore.getX() + textScore.getWidth() / 2 - score.getWidth() / 2 - 3,
                textScore.getY() + textScore.getHeight() + 15);
        overScene.attachChild(score);

        // Text For Best Score
        final MySprite textBest = new MySprite(500, 80, MainActivity.sTextBestRegion);
        overScene.attachChild(textBest);
        final Text best = new Text(0, 0, MainActivity.sTextFont, "" + sBestNum);
        best.setPosition(textBest.getX() + textBest.getWidth() / 2 - best.getWidth() / 2 - 3,
                textBest.getY() + textBest.getHeight() + 15);
        overScene.attachChild(best);

        // Restart Button
        final MySprite btnRestart = new MySprite(0, 0, MainActivity.sBtnRestartRegion) {
            private boolean down = false;

            public boolean onAreaTouched(TouchEvent event, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (event.isActionDown()) {
                    this.setColor(0.7f, 0.7f, 0.7f);
                    down = true;
                } else if (down && event.isActionUp()) {
                    this.setColor(1, 1, 1);
                    down = false;
                    if (MainActivity.sSoundOn) {
                        MainActivity.sClick.play();
                    }
                    SceneManager.setScene(GameScene.run());
                }
                return true;
            }
        };
        btnRestart.setPosition(GameConstants.CAMERA_WIDTH / 2 - btnRestart.getWidth() / 2, 210);
        overScene.attachChild(btnRestart);
        overScene.registerTouchArea(btnRestart);

        // Menu Button
        final MySprite btnMenu = new MySprite(0, 0, MainActivity.sBtnMenuRegion) {
            private boolean down = false;

            public boolean onAreaTouched(TouchEvent event, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (event.isActionDown()) {
                    this.setColor(0.7f, 0.7f, 0.7f);
                    down = true;
                } else if (down && event.isActionUp()) {
                    this.setColor(1, 1, 1);
                    down = false;
                    if (MainActivity.sSoundOn) {
                        MainActivity.sClick.play();
                    }
                    SceneManager.setScene(MainMenuScene.run());
                }
                return true;
            }
        };
        btnMenu.setPosition(GameConstants.CAMERA_WIDTH / 2 - btnMenu.getWidth() / 2, btnRestart.getY()
                + btnRestart.getHeight() + 20);
        overScene.attachChild(btnMenu);
        overScene.registerTouchArea(btnMenu);

/*		// Share score text and button
        final MySprite textShare = new MySprite(GameConstants.CAMERA_WIDTH / 2- 70, btnMenu.getY()
		+ btnMenu.getHeight() + 50, MainActivity.sTextShareRegion);
		overScene.attachChild(textShare);
		final MySprite btnShare = new MySprite(0, 0, MainActivity.sBtnShareRegion){
			private boolean down = false;
			public boolean onAreaTouched(TouchEvent event, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if(event.isActionDown()){
					this.setColor(0.7f, 0.7f, 0.7f);
					down = true;
				}else if(down && event.isActionUp()){
					this.setColor(1, 1, 1);
					down = false;
					if(MainActivity.sSoundOn){
						MainActivity.sClick.play();
					}
					MainActivity.shareIt("I just got a score of " + sScoreNum);
				} 
				return true;
			}
		};
		btnShare.setPosition(textShare.getX() + textShare.getWidth()+ 15, textShare.getY()- 2);
		btnShare.setScaleCenter(0, 0);
		btnShare.setScale(0.7f);
		overScene.attachChild(btnShare);
		overScene.registerTouchArea(btnShare); */

        sIsOver = true;
        overScene.setTouchAreaBindingEnabled(true);
        return overScene;
    }

    public static void showOver() {
        Scene sOverScene = createOverScene();
        if (MainActivity.sSoundOn) {
            MainActivity.sOver.play();
        }
        sPlayScene.setChildScene(sOverScene, false, true, true);
    }
}