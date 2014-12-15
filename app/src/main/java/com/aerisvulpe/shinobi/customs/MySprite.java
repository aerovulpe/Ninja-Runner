package com.aerisvulpe.shinobi.customs;

import android.annotation.SuppressLint;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.util.GLHelper;

public class MySprite extends Sprite{
	
	private List<MySprite> mChilds = new ArrayList<MySprite>();
    private Sprite mParent;
 
    public MySprite(float pX, float pY, TextureRegion pTextureRegion) {
        super(pX, pY, pTextureRegion);
    }

    public void addEntity(MySprite pSprite) {
        this.mChilds.add(pSprite);
        pSprite.setParent(this);
    }
 
    public void setParent(Sprite pParent) {
        this.mParent = pParent;
    }
 
    @SuppressLint("WrongCall") @Override
    protected void onManagedDraw(GL10 pGL, Camera pCamera) {
        super.onManagedDraw(pGL, pCamera);
        for (Iterator<MySprite> it = mChilds.iterator(); it.hasNext();) {
            it.next().onDraw(pGL, pCamera);
        }
    }
 
    @Override
    protected void applyTranslation(GL10 pGL) {
        if (mParent == null) {
            super.applyTranslation(pGL);
        } else {
            float[] coor = mParent.convertLocalToSceneCoordinates(mX, mY);
            pGL.glTranslatef(coor[0], coor[1], 0);
        }
 
    }
 
    @Override
    protected void applyRotation(GL10 pGL) {
        if (mParent == null) {
            super.applyRotation(pGL);
        } else {
            final float rotation = this.mParent.getRotation();
            if (rotation != 0) {
                pGL.glRotatef(rotation, 0, 0, 1);
            }
        }
    }
 
    @Override
    protected void applyScale(GL10 pGL) {
        if (mParent == null) {
            super.applyScale(pGL);
        } else {
            final float scaleX = this.mScaleX * this.mParent.getScaleX();
            final float scaleY = this.mScaleY * this.mParent.getScaleY();
            if (scaleX != 1 || scaleY != 1) {
                pGL.glScalef(scaleX, scaleY, 1);
            }
        }
 
    }
    
	protected void onInitDraw(final GL10 pGL)
    {
       super.onInitDraw(pGL);
       GLHelper.enableDither(pGL);
    }

}