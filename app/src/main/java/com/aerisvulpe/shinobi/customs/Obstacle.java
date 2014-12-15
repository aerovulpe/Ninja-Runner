package com.aerisvulpe.shinobi.customs;

import org.anddev.andengine.engine.handler.physics.PhysicsHandler;
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.util.MathUtils;

import com.aerisvulpe.shinobi.GameConstants;
import com.aerisvulpe.shinobi.Main;
import com.aerisvulpe.shinobi.scenes.GameScene;
import com.aerisvulpe.shinobi.scenes.MainMenuScene;

public class Obstacle{
	
	private MySprite obs;
	private PhysicsHandler obsHandler;
	private boolean isAbove;
	private float posY;
	
	// This is custom object for obstacles in the game
	// Generates obstacles in loop
	
	public Obstacle(float pX, final Boy boy){
		obs= new MySprite(0, 0, Main.obstacleRegion){
			private float tempVel= GameScene.velocity;
			protected void onManagedUpdate(float pSecondsElapsed){
				if(isAbove){
					if(boy.canCollide() && !boy.isSliding() && this.collidesWith(boy.getCollider())){
						boy.fall();
					}
				}else{
					if(boy.canCollide() && this.collidesWith(boy.getCollider())){
						boy.fall();
					}
				}
				if(this.getX()+ this.getWidth()< -10){
					final float pY;
					final int randPosY= MathUtils.random(0, 8);
					if(randPosY<= 2){
						isAbove= false;
						pY= posY;
					}else if(randPosY> 2 && randPosY<= 5){
						isAbove= true;
						pY= posY- 50;
					}else{
						isAbove= false;
						pY= posY- 25;
					}
					this.setPosition(GameScene.lastObstacle.get().getX()+ MathUtils.random(GameScene.min, GameScene.max)- GameScene.dist, pY);
					GameScene.lastObstacle= Obstacle.this;
				}else{
					if(tempVel!= GameScene.velocity){
						tempVel= GameScene.velocity;
						obsHandler.setVelocityX(GameScene.velocity);
					}
				}
				super.onManagedUpdate(pSecondsElapsed);
			}
		};
		
		obs.setColor(MainMenuScene.cRed, MainMenuScene.cGreen, MainMenuScene.cBlue);
		
		posY= GameConstants.CAMERA_HEIGHT- 70- obs.getHeight();
		final float pY;
		final int randPosY= MathUtils.random(0, 8);
		if(randPosY<= 2){
			isAbove= false;
			pY= posY;
		}else if(randPosY> 2 && randPosY<= 5){
			isAbove= true;
			pY= posY- 50;
		}else{
			isAbove= false;
			pY= posY- 25;
		}
		obs.setPosition(pX, pY);
		
		obsHandler= new PhysicsHandler(obs);
		obs.registerUpdateHandler(obsHandler);
		
		obsHandler.setVelocityX(GameScene.velocity);
		
	}
	
	public void addToScene(Scene scene){
		scene.attachChild(obs);
	}
	
	public MySprite get(){
		return obs;
	}
	
	public PhysicsHandler getHandler(){
		return obsHandler;
	}
	
	public float getX(){
		return obs.getX()+ obs.getWidth();
	}
	
}







