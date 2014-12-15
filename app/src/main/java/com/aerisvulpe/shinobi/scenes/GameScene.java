package com.aerisvulpe.shinobi.scenes;

import java.util.ArrayList;

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
import org.anddev.andengine.ui.activity.LayoutGameActivity;
import org.anddev.andengine.util.MathUtils;

import com.aerisvulpe.shinobi.GameConstants;
import com.aerisvulpe.shinobi.Main;
import com.aerisvulpe.shinobi.customs.Boy;
import com.aerisvulpe.shinobi.customs.MySprite;
import com.aerisvulpe.shinobi.customs.Obstacle;

public class GameScene{
	
	// Context and scene variables
	private static LayoutGameActivity context;
	private static Scene scene;
	private static Scene pausedScene;
	private static Scene overScene;
	
	// Boolean flag variables
	private static boolean isPaused;
	private static boolean isOver;
	public static boolean jumping;
	public static boolean sliding;

	// Array List for obstacles
	private static ArrayList<Obstacle> obs1;
	
	// Last obstacle variable
	public static Obstacle lastObstacle;

	// Other variables
	public static int min;
	public static int max;
	public static int dist;
	public static int lifeNum;
	public static int scoreNum;
	private static int bestNum;
	public static float velocity;
	public static float jumpTime;
	public static long jumpDur;
	private static PhysicsHandler ground1Handler;
	private static PhysicsHandler ground2Handler;
	private static MySprite ground1;
	private static MySprite ground2;

	
	
	public static void load(LayoutGameActivity context){
		GameScene.context= context;
	}
	
	public static Scene run(){
		// Create a scene
		scene= new Scene();
		
		// Background Color, change value from GameConstants.java file. If the background image doesn't load, this color will show
		scene.setBackground(new ColorBackground(GameConstants.bgRed/255f, GameConstants.bgGreen/255f, GameConstants.bgBlue/255f));
		
		// Set scene name as Game
		Main.sceneName= "Game";
		
		// Hide admob banner
		Main.HideAdmobBanner();
				
		// Play game music
		Main.sLoop.seekTo(0);
		Main.sLoop.setVolume(Main.volume/4);
		if(Main.soundOn && !Main.sLoop.isPlaying()){
			Main.sLoop.play();
		}
		
		
		// Initialize game variables
		
		jumping= false;
		sliding= false;
		isPaused= false;
		isOver= false;
		
		lifeNum= 3;
		scoreNum= 0;
		
		velocity= -200;
		min= 600;
		max= 650;
		dist= 0;
		
		jumpDur= 400;
		jumpTime= 1.0f;
		
		// Load best value from Shared Preferences, which also acts as internal storage
		Main.settings= Main.context.getSharedPreferences(GameConstants.PREFS_NAME, Main.MODE_WORLD_READABLE);
		bestNum= Main.settings.getInt("best", 0);
		
		// Create full screen background from the background image
		final ArrayList<MySprite> bgs= new ArrayList<MySprite>();
		float bgStart= -10;
		for(int i= 0; i<5; i++){
			final int num= i;
			final MySprite bg= new MySprite(0, 0, Main.bgRegion){
				protected void onManagedUpdate(float pSecondsElapsed){
					if((this.getX()+ this.getWidth()+ 10)< 0){
						if(num>0){
							this.setPosition(bgs.get(num-1).getX()+ this.getWidth()- 4, this.getY());
						}else{
							this.setPosition(bgs.get(4).getX()+ this.getWidth()- 4, this.getY());
						}
					}
					super.onManagedUpdate(pSecondsElapsed);
				}
			};
			bg.setPosition(bgStart, GameConstants.CAMERA_HEIGHT-bg.getHeight());
			bgStart+= bg.getWidth()-2;
			
			final PhysicsHandler bgHandler= new PhysicsHandler(bg);
			bg.registerUpdateHandler(bgHandler);
			bgHandler.setVelocityX(velocity/1.5f);
			
			scene.attachChild(bg);
			bgs.add(bg);
		}

		// Create the horizintal ground line
		ground1= new MySprite(0, 400, Main.groundRegion){
			private float tempVel= GameScene.velocity;
			protected void onManagedUpdate(float pSecondsElapsed){
				if(this.getX()+ this.getWidth() < 0){
					this.setPosition(ground2.getX()+ ground2.getWidth()- 4, this.getY());
				}
				if(tempVel!= GameScene.velocity){
						tempVel= GameScene.velocity;
						ground1Handler.setVelocityX(GameScene.velocity);
				}
				super.onManagedUpdate(pSecondsElapsed);
			}
		};
		ground2= new MySprite(ground1.getX()+ ground1.getWidth() - 4, 400, Main.groundRegion.deepCopy()){
			private float tempVel= GameScene.velocity;
			protected void onManagedUpdate(float pSecondsElapsed){
				if(this.getX()+ this.getWidth() < 0){
					this.setPosition(ground1.getX()+ ground1.getWidth()- 4, this.getY());
				}
				if(tempVel!= GameScene.velocity){
						tempVel= GameScene.velocity;
						ground2Handler.setVelocityX(GameScene.velocity);
				}
				super.onManagedUpdate(pSecondsElapsed);
			}
		};
		
		ground1Handler= new PhysicsHandler(ground1);
		ground2Handler= new PhysicsHandler(ground2);
		
		ground1.registerUpdateHandler(ground1Handler);
		ground2.registerUpdateHandler(ground2Handler);
		
		ground1Handler.setVelocityX(velocity);
		ground2Handler.setVelocityX(velocity);
		
		scene.attachChild(ground1);
		scene.attachChild(ground2);
		
//		final Rectangle ground= new Rectangle(0, 400, GameConstants.CAMERA_WIDTH, 35);
//		ground.setColor(cRed, cGreen, cBlue);
//		scene.attachChild(ground);
		
		// Create the stick man and add it to scene
		final Boy boy= new Boy(Main.stickmanRegion);
		boy.setPosition(150, 304);
		boy.addToScene(scene);
		
		// Show info text
		final Text info= new Text(0, 0, Main.textFont, "Tap LEFT side of screen to SLIDE!\nTap RIGHT side of screen to JUMP!");
		info.setPosition(GameConstants.CAMERA_WIDTH/2- info.getWidth()/2, 130);
		info.setColor(0, 0, 0);
		scene.attachChild(info);
		
		// Set touch listener, left side for sliding and right for jumping
		scene.setOnSceneTouchListener(new IOnSceneTouchListener(){
			@Override
			public boolean onSceneTouchEvent(Scene pScene, TouchEvent event) {
				if(event.getX()< 400){
					if(event.isActionDown() && !jumping && !sliding){
						sliding= true;
						boy.slide();
						if(info.isVisible()){
							info.setVisible(false);
						}
					}
				}else{
					if(event.isActionDown() && !jumping && !sliding){
						jumping= true;
						boy.jump();
						if(info.isVisible()){
							info.setVisible(false);
						}
					}
				}
				return false;
			}}
		);
		
		if(obs1!= null){
			obs1.clear();
		}else{
			obs1= new ArrayList<Obstacle>();
		}
		
		// Create and add obstacles to the scene
		float x= 700;
		for(int i=0; i<5; i++){
			final Obstacle obstacle= new Obstacle(x+ MathUtils.random(min, max), boy);
			if(i==4){
				lastObstacle= obstacle;
			}
			x= obstacle.getX();
			obstacle.addToScene(scene);
			obs1.add(obstacle);
		}
		
		// Increase score every 0.5 seconds
		scene.registerUpdateHandler(new TimerHandler(0.5f, true, new ITimerCallback(){
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				scoreNum+= 1;
			}})
		);
		
		// Create Text for score and add to scene
		final ChangeableText scoreText= new ChangeableText(0, 7, Main.textFont, "Score: 0123456789","Score: 0123456789".length()){
			private int tmpScore= scoreNum;
			protected void onManagedUpdate(float pSecondsElapsed){
				if(tmpScore!= scoreNum){
					tmpScore= scoreNum;
					this.setText("Score: "+ scoreNum);
					this.setPosition(GameConstants.CAMERA_WIDTH/2- this.getWidth()/2, 7);
				}
				super.onManagedUpdate(pSecondsElapsed);
			}
		};
		scoreText.setText("Score: 0123456789");
		scoreText.setText("Score: 0");
		scoreText.setPosition(GameConstants.CAMERA_WIDTH/2- scoreText.getWidth()/2, 7);
		scoreText.setColor(64/255f, 59/255f, 29/255f);
		scene.attachChild(scoreText);

		// Pause Button
		final MySprite btnPause= new MySprite(0, 0, Main.btnPauseRegion){
			private boolean down= false;
			public boolean onAreaTouched(TouchEvent event, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if(event.isActionDown()){
					this.setColor(0.7f, 0.7f, 0.7f);
					down= true;
				}else if(down && event.isActionUp()){
					this.setColor(1, 1, 1);
					down= false;
					if(Main.soundOn){
						Main.sClick.play();
					}
					back();
				} 
				return true;
			}
		};
		btnPause.setPosition(5, GameConstants.CAMERA_HEIGHT- btnPause.getHeight()- 5);
		scene.attachChild(btnPause);
		scene.registerTouchArea(btnPause);
		
		// Change difficulty of game every 5 seconds by increasing speed
		// and distance between the obstacles
		scene.registerUpdateHandler(new TimerHandler(5f, true, new ITimerCallback(){
			int a= 0;
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				a+= 1;
				if(a==1){
					velocity-= 5;
					dist+= 50;
				}else if(a==2){
					velocity-= 10;
					dist+= 50;
				}else if(a==5){
					velocity-= -10;
					dist+= 50;
				}else if(a==7){
					velocity-= 10;
					dist+= 50;
				}else if(a==9){
					velocity-= 15;
					dist+= 50;
				}else if(a==11){
					velocity-= 20;
					dist+= 100;
				}else if(a==20){
					velocity-= 5;
				}
			}})
		);
		
		
		scene.setTouchAreaBindingEnabled(true);
		return scene;
	}
	
	// Back method during game to show pause screen and activate/deactivate ads
	public static void back(){
		if(!isOver){
			if(!isPaused){
				pausedScene= createPauseScene();
				scene.setChildScene(pausedScene, false, true, true);
			}else{
				isPaused= false;
				pausedScene.back();
				Main.HideAdmobBanner();
				if(Main.soundOn && !Main.sLoop.isPlaying()){
					Main.sLoop.play();
				}
			}
		}
	}
	
	// Pause Screen Method
	private static Scene createPauseScene(){
		// Create and load scene
		final Scene pScene= new Scene();
		pScene.setBackgroundEnabled(false);
		
		if(Main.sLoop.isPlaying()){
			Main.sLoop.pause();
		}

		if(Main.admobEnabled){
			Main.showAdmobBanner();
		}
		if(Main.mobileCoreEnabled){
			Main.showMobileCoreOfferWall();
		}
		
		// Make background blurr
		final Rectangle blurr= new Rectangle(0, 0, GameConstants.CAMERA_WIDTH, GameConstants.CAMERA_HEIGHT);
		blurr.setColor(0, 0, 0, 0.7f);
		pScene.attachChild(blurr);
		
		// Resume Button
		final MySprite btnResume= new MySprite(0, 0, Main.btnResumeRegion){
			private boolean down= false;
			public boolean onAreaTouched(TouchEvent event, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if(event.isActionDown()){
					this.setColor(0.7f, 0.7f, 0.7f);
					down= true;
				}else if(down && event.isActionUp()){
					this.setColor(1, 1, 1);
					down= false;
					if(Main.soundOn){
						Main.sClick.play();
					}
					back();
				} 
				return true;
			}
		};
		btnResume.setPosition(GameConstants.CAMERA_WIDTH/2- btnResume.getWidth()/2, 150);
		pScene.attachChild(btnResume);
		pScene.registerTouchArea(btnResume);
		
		// Menu Button
		final MySprite btnMenu= new MySprite(0, 0, Main.btnMenuRegion){
			private boolean down= false;
			public boolean onAreaTouched(TouchEvent event, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if(event.isActionDown()){
					this.setColor(0.7f, 0.7f, 0.7f);
					down= true;
				}else if(down && event.isActionUp()){
					this.setColor(1, 1, 1);
					down= false;
					if(Main.soundOn){
						Main.sClick.play();
					}
					SceneManager.setScene(MainMenuScene.run());
				} 
				return true;
			}
		};
		btnMenu.setPosition(GameConstants.CAMERA_WIDTH/2- btnMenu.getWidth()/2, btnResume.getY()+ btnResume.getHeight()+ 20);
		pScene.attachChild(btnMenu);
		pScene.registerTouchArea(btnMenu);
		
		isPaused= true;
		
		pScene.setTouchAreaBindingEnabled(true);
		return pScene;
	}
	
	// Game over Scene Method
	private static Scene createOverScene(){
		// creat and load background
		final Scene oScene= new Scene();
		oScene.setBackgroundEnabled(false);
		
		// Stop music
		if(Main.sLoop.isPlaying()){
			Main.sLoop.pause();
		}
		
		if(Main.admobEnabled){
			Main.showAdmobBanner();
		}
		if(Main.mobileCoreEnabled){
			Main.showMobileCoreOfferWall();
		}
		
		// Make backgroud blurr
		final Rectangle blurr= new Rectangle(0, 0, GameConstants.CAMERA_WIDTH, GameConstants.CAMERA_HEIGHT);
		blurr.setColor(0, 0, 0, 0.7f);
		oScene.attachChild(blurr);
		
		// Save best score if greate than earlier saved data
		if(scoreNum> bestNum){
			bestNum= scoreNum;
			Main.settings= Main.context.getSharedPreferences(GameConstants.PREFS_NAME, Main.MODE_WORLD_WRITEABLE);
			Main.editor= Main.settings.edit();
			Main.editor.putInt("best", scoreNum);
			Main.editor.commit();
		}
		
		// Text for Current Score
		final MySprite textScore= new MySprite(250, 80, Main.textScoreRegion);
		oScene.attachChild(textScore);
		final Text score= new Text(0, 0, Main.textFont, ""+ scoreNum);
		score.setPosition(textScore.getX()+ textScore.getWidth()/2- score.getWidth()/2- 3, textScore.getY()+ textScore.getHeight()+ 15);
		oScene.attachChild(score);
		
		// Text For Best Score
		final MySprite textBest= new MySprite(500, 80, Main.textBestRegion);
		oScene.attachChild(textBest);
		final Text best= new Text(0, 0, Main.textFont, ""+ bestNum);
		best.setPosition(textBest.getX()+ textBest.getWidth()/2- best.getWidth()/2- 3, textBest.getY()+ textBest.getHeight()+ 15);
		oScene.attachChild(best);
		
		// Restart Button
		final MySprite btnRestart= new MySprite(0, 0, Main.btnRestartRegion){
			private boolean down= false;
			public boolean onAreaTouched(TouchEvent event, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if(event.isActionDown()){
					this.setColor(0.7f, 0.7f, 0.7f);
					down= true;
				}else if(down && event.isActionUp()){
					this.setColor(1, 1, 1);
					down= false;
					if(Main.soundOn){
						Main.sClick.play();
					}
					SceneManager.setScene(GameScene.run());
				} 
				return true;
			}
		};
		btnRestart.setPosition(GameConstants.CAMERA_WIDTH/2- btnRestart.getWidth()/2, 210);
		oScene.attachChild(btnRestart);
		oScene.registerTouchArea(btnRestart);
		
		// Menu Button
		final MySprite btnMenu= new MySprite(0, 0, Main.btnMenuRegion){
			private boolean down= false;
			public boolean onAreaTouched(TouchEvent event, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if(event.isActionDown()){
					this.setColor(0.7f, 0.7f, 0.7f);
					down= true;
				}else if(down && event.isActionUp()){
					this.setColor(1, 1, 1);
					down= false;
					if(Main.soundOn){
						Main.sClick.play();
					}
					SceneManager.setScene(MainMenuScene.run());
				} 
				return true;
			}
		};
		btnMenu.setPosition(GameConstants.CAMERA_WIDTH/2- btnMenu.getWidth()/2, btnRestart.getY()+ btnRestart.getHeight()+ 20);
		oScene.attachChild(btnMenu);
		oScene.registerTouchArea(btnMenu);
		
/*		// Share score text and button
		final MySprite textShare= new MySprite(GameConstants.CAMERA_WIDTH/2- 70, btnMenu.getY()+ btnMenu.getHeight()+ 50, Main.textShareRegion);
		oScene.attachChild(textShare);
		final MySprite btnShare= new MySprite(0, 0, Main.btnShareRegion){
			private boolean down= false;
			public boolean onAreaTouched(TouchEvent event, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if(event.isActionDown()){
					this.setColor(0.7f, 0.7f, 0.7f);
					down= true;
				}else if(down && event.isActionUp()){
					this.setColor(1, 1, 1);
					down= false;
					if(Main.soundOn){
						Main.sClick.play();
					}
					Main.shareIt("I just got a score of "+ scoreNum);
				} 
				return true;
			}
		};
		btnShare.setPosition(textShare.getX()+ textShare.getWidth()+ 15, textShare.getY()- 2);
		btnShare.setScaleCenter(0, 0);
		btnShare.setScale(0.7f);
		oScene.attachChild(btnShare);
		oScene.registerTouchArea(btnShare); */
		
		isOver= true;
		
		oScene.setTouchAreaBindingEnabled(true);
		return oScene;
	}
	
	public static void showOver(){
		overScene= createOverScene();
		if(Main.soundOn){
			Main.sOver.play();
		}
		scene.setChildScene(overScene, false, true, true);
	}
}








