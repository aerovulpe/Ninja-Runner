package com.aerisvulpe.shinobi.scenes;

import org.anddev.andengine.engine.handler.timer.ITimerCallback;
import org.anddev.andengine.engine.handler.timer.TimerHandler;
import org.anddev.andengine.entity.modifier.PathModifier;
import org.anddev.andengine.entity.modifier.PathModifier.Path;
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.ui.activity.LayoutGameActivity;

import com.aerisvulpe.shinobi.GameConstants;
import com.aerisvulpe.shinobi.Main;
import com.aerisvulpe.shinobi.customs.Boy;
import com.aerisvulpe.shinobi.customs.MySprite;

public class MainMenuScene{
	
	// context and scene variables
	private static LayoutGameActivity context;
	private static Scene scene;
	
	// Color values for the ground line and obstacles
	public static float cRed= 125/255f;
	public static float cGreen= 125/255f;
	public static float cBlue= 125/255f;
	
	public static void load(LayoutGameActivity context){
		MainMenuScene.context= context;
	}
	
	public static Scene run(){
		// Create a scene
		scene= new Scene();
		
		// Set scene name as Menu
		Main.sceneName= "Menu";
		
		// Show admob banner
		if(Main.admobEnabled){
			Main.showAdmobBanner();
		}
		
		// Play Game Music
		Main.sLoop.seekTo(0);
		Main.sLoop.setVolume(Main.volume/2);
		if(Main.soundOn && !Main.sLoop.isPlaying()){
			Main.sLoop.play();
		}
		
		// Create full screen background from the background image
		float bgStart= -50;
		for(int i= 0; i<4; i++){
			final MySprite bg= new MySprite(0, 0, Main.bgRegion);
			bg.setPosition(bgStart, GameConstants.CAMERA_HEIGHT-bg.getHeight());
			bgStart+= bg.getWidth()-2;
			scene.attachChild(bg);
		}
		
		// Create the horizintal ground line
		final MySprite ground= new MySprite(-5, 400, Main.groundRegion);
		scene.attachChild(ground);
		
//		final Rectangle ground= new Rectangle(0, 400, GameConstants.CAMERA_WIDTH, 35);
//		ground.setColor(cRed, cGreen, cBlue);
//		scene.attachChild(ground);
		
		// Create and add Title image to scene
		final MySprite title= new MySprite(0, 0, Main.titleRegion);
		title.setPosition(GameConstants.CAMERA_WIDTH/2- title.getWidth()/2, 30);
		scene.attachChild(title);
		
		// Play Button
		final MySprite btnPlay= new MySprite(0, 0, Main.btnPlayRegion){
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
					SceneManager.setScene(GameScene.run()); // Load game scene on clicked
				} 
				return true;
			}
		};
		btnPlay.setPosition(GameConstants.CAMERA_WIDTH/2- btnPlay.getWidth()/2, title.getY()+ title.getHeight()+ 60);
		scene.attachChild(btnPlay);
		scene.registerTouchArea(btnPlay);
		
		// Quit Button
		final MySprite btnQuit= new MySprite(0, 0, Main.btnQuitRegion){
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
					Main.context.finish(); // Quit game
				} 
				return true;
			}
		};
		btnQuit.setPosition(GameConstants.CAMERA_WIDTH/2- btnQuit.getWidth()/2, btnPlay.getY()+ btnPlay.getHeight()+ 20);
		scene.attachChild(btnQuit);
		scene.registerTouchArea(btnQuit);
		
		// Sound On/ Off button
		final MySprite btnSoundOff= new MySprite(0, 0, Main.btnOffRegion.deepCopy());
		btnSoundOff.setPosition(GameConstants.CAMERA_WIDTH- btnSoundOff.getWidth()-10, 10);
		final MySprite btnSoundOn= new MySprite(0, 0, Main.btnOnRegion.deepCopy()){
			boolean down= false;
			public boolean onAreaTouched(TouchEvent event, float touchX, float touchY){
				if(event.isActionDown()){
					down= true;
					this.setColor(0.7f, 0.7f, 0.7f);
				}else if(event.isActionUp() && down){
					down= false;
					this.setColor(1, 1, 1);
					if(Main.soundOn){
						Main.soundOn= false;
						this.setVisible(false);
						btnSoundOff.setVisible(true);
						Main.sLoop.pause();
					}else{
						Main.soundOn= true;
						this.setVisible(true);
						btnSoundOff.setVisible(false);
						Main.sLoop.play();
					}
					if(Main.soundOn){
						Main.sClick.play();
					}
				}
				return true;
			}
		};
		btnSoundOn.setPosition(btnSoundOff.getX()+ btnSoundOff.getWidth()- btnSoundOn.getWidth(), btnSoundOff.getY()+ btnSoundOff.getHeight()- btnSoundOn.getHeight());
		if(Main.soundOn){
			btnSoundOn.setVisible(true);
			btnSoundOff.setVisible(false);
		}else{
			btnSoundOn.setVisible(false);
			btnSoundOff.setVisible(true);
		}
		scene.attachChild(btnSoundOff);
		scene.attachChild(btnSoundOn);
		scene.registerTouchArea(btnSoundOn);
		
		// Add a stick man to the scene
		final Boy boy= new Boy(Main.stickmanRegion);
		boy.setPosition(-150, 290);
		boy.addToScene(scene);
		
		// After 1 seconds, make the stick man appear from the left side of screen
		scene.registerUpdateHandler(new TimerHandler(1f, false, new ITimerCallback(){
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				scene.unregisterUpdateHandler(pTimerHandler);
				boy.getOptions().registerEntityModifier(new PathModifier(0.7f, new Path(2).to(-75, 312).to(100, 312)));
			}})
		);
		
		scene.setTouchAreaBindingEnabled(true);
		return scene;
	}
	
	public static void back(){
		context.finish();
	}
	
}





