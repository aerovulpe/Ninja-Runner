package me.aerovulpe.ninjarunner.customs;

import android.annotation.SuppressLint;

import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.util.GLHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

public class MySprite extends Sprite {

    private List<MySprite> mChilds = new ArrayList<MySprite>();
    private Sprite mParent;

    public MySprite(float x, float y, TextureRegion textureRegion) {
        super(x, y, textureRegion);
    }

    public void addEntity(MySprite sprite) {
        this.mChilds.add(sprite);
        sprite.setParent(this);
    }

    public void setParent(Sprite parent) {
        mParent = parent;
    }

    @SuppressLint("WrongCall")
    @Override
    protected void onManagedDraw(GL10 gL, Camera camera) {
        super.onManagedDraw(gL, camera);

        for (Iterator<MySprite> it = mChilds.iterator(); it.hasNext(); ) {
            it.next().onDraw(gL, camera);
        }
    }

    @Override
    protected void applyTranslation(GL10 gL) {
        if (mParent == null) {
            super.applyTranslation(gL);
        } else {
            float[] coor = mParent.convertLocalToSceneCoordinates(mX, mY);
            gL.glTranslatef(coor[0], coor[1], 0);
        }

    }

    @Override
    protected void applyRotation(GL10 gL) {
        if (mParent == null) {
            super.applyRotation(gL);
        } else {
            final float rotation = this.mParent.getRotation();
            if (rotation != 0) {
                gL.glRotatef(rotation, 0, 0, 1);
            }
        }
    }

    @Override
    protected void applyScale(GL10 gL) {
        if (mParent == null) {
            super.applyScale(gL);
        } else {
            final float scaleX = this.mScaleX * this.mParent.getScaleX();
            final float scaleY = this.mScaleY * this.mParent.getScaleY();
            if (scaleX != 1 || scaleY != 1) {
                gL.glScalef(scaleX, scaleY, 1);
            }
        }

    }

    protected void onInitDraw(final GL10 gL) {
        super.onInitDraw(gL);
        GLHelper.enableDither(gL);
    }

}