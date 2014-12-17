package me.aerovulpe.ninjarunner.customs;

import android.content.Context;

import org.anddev.andengine.opengl.texture.atlas.ITextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.atlas.bitmap.source.AssetBitmapTextureAtlasSource;
import org.anddev.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.source.ITextureAtlasSource;

public class MyTextureRegionFactory extends BitmapTextureAtlasTextureRegionFactory {

    public static TextureRegion createFromAsset(final BitmapTextureAtlas bitmapTextureAtlas,
                                                final Context context, final String assetPath,
                                                final int texturePositionX, final int texturePositionY,
                                                final int padding) {
        final IBitmapTextureAtlasSource bitmapTextureAtlasSource =
                new AssetBitmapTextureAtlasSource(context, "" + assetPath);
        return createFromSource(bitmapTextureAtlas, bitmapTextureAtlasSource, texturePositionX,
                texturePositionY, padding);
    }

    public static TextureRegion createFromSource(final BitmapTextureAtlas bitmapTextureAtlas,
                                                 final IBitmapTextureAtlasSource iBitmapTextureAtlasSource,
                                                 final int texturePositionX, final int texturePositionY,
                                                 final int padding) {
        return createFromSourcePadded(bitmapTextureAtlas, iBitmapTextureAtlasSource, texturePositionX,
                texturePositionY, false, padding);
    }

    public static <T extends ITextureAtlasSource> TextureRegion createFromSourcePadded(final ITextureAtlas<T> textureAtlas,
                                                                                       final T textureAtlasSource, final int texturePositionX,
                                                                                       final int texturePositionY, final boolean createTextureRegionBuffersManaged,
                                                                                       final int padding) {
        final TextureRegion textureRegion = new TextureRegion(textureAtlas, texturePositionX + padding,
                texturePositionY + padding, textureAtlasSource.getWidth() - padding * 2,
                textureAtlasSource.getHeight() - padding * 2);
        textureAtlas.addTextureAtlasSource(textureAtlasSource, textureRegion.getTexturePositionX() - padding,
                textureRegion.getTexturePositionY() - padding);
        textureRegion.setTextureRegionBufferManaged(createTextureRegionBuffersManaged);
        return textureRegion;
    }

}