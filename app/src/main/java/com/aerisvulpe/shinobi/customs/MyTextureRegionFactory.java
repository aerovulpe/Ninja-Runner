package com.aerisvulpe.shinobi.customs;

import org.anddev.andengine.opengl.texture.atlas.ITextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.atlas.bitmap.source.AssetBitmapTextureAtlasSource;
import org.anddev.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.source.ITextureAtlasSource;

import android.content.Context;
import android.util.Log;

public class MyTextureRegionFactory extends BitmapTextureAtlasTextureRegionFactory {
       
        public static TextureRegion createFromAsset(final BitmapTextureAtlas pBitmapTextureAtlas, final Context pContext, final String pAssetPath, final int pTexturePositionX, final int pTexturePositionY, final int padding) {
                Log.e("fsdfds", " daar isieee!");
                final IBitmapTextureAtlasSource bitmapTextureAtlasSource = new AssetBitmapTextureAtlasSource(pContext, "" + pAssetPath);
                return createFromSource(pBitmapTextureAtlas, bitmapTextureAtlasSource, pTexturePositionX, pTexturePositionY, padding);
        }
       
        public static TextureRegion createFromSource(final BitmapTextureAtlas pBitmapTextureAtlas, final IBitmapTextureAtlasSource pBitmapTextureAtlasSource, final int pTexturePositionX, final int pTexturePositionY, final int padding) {
                return createFromSourcePadded(pBitmapTextureAtlas, pBitmapTextureAtlasSource, pTexturePositionX, pTexturePositionY, false, padding);
        }
       
        public static <T extends ITextureAtlasSource> TextureRegion createFromSourcePadded(final ITextureAtlas<T> pTextureAtlas, final T pTextureAtlasSource, final int pTexturePositionX, final int pTexturePositionY, final boolean pCreateTextureRegionBuffersManaged, final int padding) {
                final TextureRegion textureRegion = new TextureRegion(pTextureAtlas, pTexturePositionX+padding, pTexturePositionY+padding, pTextureAtlasSource.getWidth()-padding*2, pTextureAtlasSource.getHeight()-padding*2);
                pTextureAtlas.addTextureAtlasSource(pTextureAtlasSource, textureRegion.getTexturePositionX()-padding, textureRegion.getTexturePositionY()-padding);
                textureRegion.setTextureRegionBufferManaged(pCreateTextureRegionBuffersManaged);
                return textureRegion;
        }
       
}