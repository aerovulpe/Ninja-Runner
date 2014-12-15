package com.aerisvulpe.shinobi.customs;

import org.anddev.andengine.engine.handler.timer.ITimerCallback;
import org.anddev.andengine.engine.handler.timer.TimerHandler;
import org.anddev.andengine.entity.modifier.AlphaModifier;
import org.anddev.andengine.entity.modifier.PathModifier;
import org.anddev.andengine.entity.modifier.PathModifier.Path;
import org.anddev.andengine.entity.modifier.ScaleModifier;
import org.anddev.andengine.entity.modifier.SequenceEntityModifier;
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.entity.sprite.AnimatedSprite.IAnimationListener;
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.util.MathUtils;

import com.aerisvulpe.shinobi.Duration;
import com.aerisvulpe.shinobi.Main;
import com.aerisvulpe.shinobi.scenes.GameScene;

public class Boy{
	
	private AnimatedSprite boy;
	private Rectangle collider;
	private boolean canCollide;
	private boolean sliding;
	private Scene scene;
	
	// This custom object creates and loades method for stuck man
	
	public Boy(TiledTextureRegion boyRegion){
		canCollide= true;
		sliding= false;
		boy= new AnimatedSprite(0, 0, boyRegion);
		boy.animate(Duration.get(100, 4), 0, 3, true);
		collider= new Rectangle(boy.getX()+ 38, boy.getY()+ 16, 26, 86){
			protected void onManagedUpdate(float pSecondsElapsed){
				this.setPosition(boy.getX()+ 38, boy.getY()+ 16);
				super.onManagedUpdate(pSecondsElapsed);
			}
		};
		collider.setVisible(false);
		
	}
	
	public void setPosition(float pX, float pY){
		boy.setPosition(pX, pY);
	}
	
	public AnimatedSprite getOptions(){
		return boy;
	}
	
	public void addToScene(Scene scene){
		this.scene= scene;
		scene.attachChild(boy);
		scene.attachChild(collider);
	}	
	
	public Rectangle getCollider(){
		return collider;
	}
	
	// Method for fall when collides with obastacles
	public void fall(){
		canCollide= false;
		if(Main.soundOn){
			Main.sHurt.play();
		}
		boy.stopAnimation(5);
		boy.setPosition(150, 304);
		GameScene.velocity= 0;
		scene.registerUpdateHandler(new TimerHandler(0.5f, false, new ITimerCallback(){
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				scene.unregisterUpdateHandler(pTimerHandler);
				GameScene.showOver();
			}})
		);
	}
	
	public boolean canCollide(){
		return canCollide;
	}
	
	public boolean isSliding(){
		return sliding;
	}
	
	// Method to slide the boy
	public void slide(){
		if(Main.soundOn){
			Main.sSlide.play();
		}
		boy.stopAnimation(6);
		sliding= true;
		scene.registerUpdateHandler(new TimerHandler(0.7f, false, new ITimerCallback(){
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				scene.unregisterUpdateHandler(pTimerHandler);
				sliding= false;
				GameScene.sliding= false;
				boy.animate(Duration.get(100, 4), 0, 3, true);
			}})
		);
	}
	
	// Jump Method
	public void jump(){
		if(Main.soundOn){
			Main.sJump.play();
		}		
		boy.stopAnimation(4);
		boy.registerEntityModifier(new PathModifier(GameScene.jumpTime, new Path(3).to(boy.getX(), boy.getY()).to(boy.getX(), boy.getY()- 90).to(boy.getX(), 300)));
		scene.registerUpdateHandler(new TimerHandler(GameScene.jumpTime, false, new ITimerCallback(){
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				scene.unregisterUpdateHandler(pTimerHandler);
				GameScene.jumping= false;
				boy.animate(Duration.get(100, 4), 0, 3, true);
			}})
		);
	}
	
}







