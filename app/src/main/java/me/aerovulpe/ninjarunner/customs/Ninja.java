package me.aerovulpe.ninjarunner.customs;

import me.aerovulpe.ninjarunner.Duration;
import me.aerovulpe.ninjarunner.MainActivity;
import me.aerovulpe.ninjarunner.scenes.GameScene;

import org.anddev.andengine.engine.handler.timer.ITimerCallback;
import org.anddev.andengine.engine.handler.timer.TimerHandler;
import org.anddev.andengine.entity.modifier.PathModifier;
import org.anddev.andengine.entity.modifier.PathModifier.Path;
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;

public class Ninja {

    private AnimatedSprite mNinja;
    private Rectangle mCollider;
    private boolean mCanCollide;
    private boolean mSliding;
    private Scene mScene;


    // This custom object creates and loads method for the ninja
    public Ninja(TiledTextureRegion ninjaRegion) {
        mCanCollide = true;
        mSliding = false;
        mNinja = new AnimatedSprite(0, 0, ninjaRegion);
        mNinja.animate(Duration.get(100, 4), 0, 3, true);
        mCollider = new Rectangle(mNinja.getX() + 38, mNinja.getY() + 16, 26, 86) {
            protected void onManagedUpdate(float pSecondsElapsed) {
                this.setPosition(mNinja.getX() + 38, mNinja.getY() + 16);
                super.onManagedUpdate(pSecondsElapsed);
            }
        };
        mCollider.setVisible(false);

    }

    public void setPosition(float x, float y) {
        mNinja.setPosition(x, y);
    }

    public AnimatedSprite getOptions() {
        return mNinja;
    }

    public void addToScene(Scene scene) {
        this.mScene = scene;
        scene.attachChild(mNinja);
        scene.attachChild(mCollider);
    }

    public Rectangle getCollider() {
        return mCollider;
    }

    // Method for fall when ninja collides with obstacles
    public void fall() {
        mCanCollide = false;
        if (MainActivity.sSoundOn) {
            MainActivity.sHurt.play();
        }
        mNinja.stopAnimation(5);
        mNinja.setPosition(150, 304);
        GameScene.sVelocity = 0;
        mScene.registerUpdateHandler(new TimerHandler(0.5f, false, new ITimerCallback() {
                    @Override
                    public void onTimePassed(TimerHandler pTimerHandler) {
                        mScene.unregisterUpdateHandler(pTimerHandler);
                        GameScene.showOver();
                    }
                })
        );
    }

    public boolean canCollide() {
        return mCanCollide;
    }

    public boolean isSliding() {
        return mSliding;
    }

    // Method to slide the ninja
    public void slide() {
        if (MainActivity.sSoundOn) {
            MainActivity.sSlide.play();
        }
        mNinja.stopAnimation(6);
        mSliding = true;
        mScene.registerUpdateHandler(new TimerHandler(0.7f, false, new ITimerCallback() {
                    @Override
                    public void onTimePassed(TimerHandler pTimerHandler) {
                        mScene.unregisterUpdateHandler(pTimerHandler);
                        mSliding = false;
                        GameScene.sSliding = false;
                        mNinja.animate(Duration.get(100, 4), 0, 3, true);
                    }
                })
        );
    }

    // Jump Method
    public void jump() {
        if (MainActivity.sSoundOn) {
            MainActivity.sJump.play();
        }
        mNinja.stopAnimation(4);
        mNinja.registerEntityModifier(new PathModifier(GameScene.sJumpTime, new Path(3).to(mNinja.getX(), mNinja.getY()).to(mNinja.getX(), mNinja.getY() - 90).to(mNinja.getX(), 300)));
        mScene.registerUpdateHandler(new TimerHandler(GameScene.sJumpTime, false, new ITimerCallback() {
                    @Override
                    public void onTimePassed(TimerHandler pTimerHandler) {
                        mScene.unregisterUpdateHandler(pTimerHandler);
                        GameScene.sJumping = false;
                        mNinja.animate(Duration.get(100, 4), 0, 3, true);
                    }
                })
        );
    }

}