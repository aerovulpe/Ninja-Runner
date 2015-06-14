package me.aerovulpe.ninjarunner.customs;

import org.anddev.andengine.engine.handler.physics.PhysicsHandler;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.util.MathUtils;

import me.aerovulpe.ninjarunner.GameConstants;
import me.aerovulpe.ninjarunner.MainActivity;
import me.aerovulpe.ninjarunner.scenes.GameScene;
import me.aerovulpe.ninjarunner.scenes.MainMenuScene;

public class Obstacle {

    private MySprite mObstacle;
    private PhysicsHandler mObstacleHandler;
    private boolean isAbove;
    private float posY;


    // This is custom object for obstacles in the game
    // Generates obstacles in loop
    public Obstacle(float x, final Ninja ninja) {
        mObstacle = new MySprite(0, 0, MainActivity.sObstacleRegion) {
            private float tempVel = GameScene.sVelocity;

            protected void onManagedUpdate(float secondsElapsed) {
                if (isAbove) {
                    if (ninja.canCollide() && !ninja.isSliding() &&
                            this.collidesWith(ninja.getCollider())) {
                        ninja.fall();
                    }
                } else {
                    if (ninja.canCollide() && this.collidesWith(ninja.getCollider())) {
                        ninja.fall();
                    }
                }
                if (this.getX() + this.getWidth() < -10) {
                    final float y;
                    final int randPosY = MathUtils.random(0, 8);
                    if (randPosY <= 2) {
                        isAbove = false;
                        y = posY;
                    } else if (randPosY > 2 && randPosY <= 5) {
                        isAbove = true;
                        y = posY - 50;
                    } else {
                        isAbove = false;
                        y = posY - 25;
                    }
                    this.setPosition(GameScene.sLastObstacle.getObstacle().getX() +
                            MathUtils.random(GameScene.sMin, GameScene.sMax) - GameScene.sDist, y);
                    GameScene.sLastObstacle = Obstacle.this;
                } else {
                    if (tempVel != GameScene.sVelocity) {
                        tempVel = GameScene.sVelocity;
                        mObstacleHandler.setVelocityX(GameScene.sVelocity);
                    }
                }
                super.onManagedUpdate(secondsElapsed);
            }
        };

        mObstacle.setColor(MainMenuScene.sRed, MainMenuScene.sGreen, MainMenuScene.sBlue);

        posY = GameConstants.CAMERA_HEIGHT - 70 - mObstacle.getHeight();
        final float y;
        final int randPosY = MathUtils.random(0, 8);
        if (randPosY <= 2) {
            isAbove = false;
            y = posY;
        } else if (randPosY > 2 && randPosY <= 5) {
            isAbove = true;
            y = posY - 50;
        } else {
            isAbove = false;
            y = posY - 25;
        }
        mObstacle.setPosition(x, y);

        mObstacleHandler = new PhysicsHandler(mObstacle);
        mObstacle.registerUpdateHandler(mObstacleHandler);

        mObstacleHandler.setVelocityX(GameScene.sVelocity);

    }

    public void addToScene(Scene scene) {
        scene.attachChild(mObstacle);
    }

    public MySprite getObstacle() {
        return mObstacle;
    }

    public PhysicsHandler getHandler() {
        return mObstacleHandler;
    }

    public float getX() {
        return mObstacle.getX() + mObstacle.getWidth();
    }
}