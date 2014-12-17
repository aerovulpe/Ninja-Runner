package me.aerovulpe.ninjarunner.customs;

import org.anddev.andengine.opengl.util.FastFloatBuffer;
import org.anddev.andengine.opengl.vertex.RectangleVertexBuffer;

/**
 * (c) 2011 Natalia Pujol
 *
 * @author Natalia Pujol
 */
public class RectanglePackedVertexBuffer extends RectangleVertexBuffer {
    protected float mOffX = 0f;
    protected float mOffY = 0f;
    protected boolean mRotated = false;


    public RectanglePackedVertexBuffer(final float offX, final float offY, boolean rotated,
                                       final int drawType, final boolean managed) {
        super(drawType, managed);
        mOffX = offX;
        mOffY = offY;
        mRotated = rotated;
    }

    public void setOffsetX(float pOffX) {
        mOffX = pOffX;
    }

    public void setOffsetY(float pOffY) {
        mOffY = pOffY;
    }

    @Override
    public synchronized void update(final float width, final float height) {

        final int[] bufferData = mBufferData;

        if (!mRotated) {
            final int x = Float.floatToRawIntBits(mOffX);
            final int y = Float.floatToRawIntBits(mOffY);
            final int x2 = Float.floatToRawIntBits(mOffX + width);
            final int y2 = Float.floatToRawIntBits(mOffY + height);

            bufferData[0] = x;
            bufferData[1] = y;

            bufferData[2] = x;
            bufferData[3] = y2;

            bufferData[4] = x2;
            bufferData[5] = y;

            bufferData[6] = x2;
            bufferData[7] = y2;
        } else {
            final int x = Float.floatToRawIntBits(mOffX);
            final int y = Float.floatToRawIntBits(mOffY + height);
            final int x2 = Float.floatToRawIntBits(mOffX + width);
            final int y2 = Float.floatToRawIntBits(mOffY);

            bufferData[0] = x;
            bufferData[1] = y;

            bufferData[2] = x2;
            bufferData[3] = y;

            bufferData[4] = x;
            bufferData[5] = y2;

            bufferData[6] = x2;
            bufferData[7] = y2;
        }

        final FastFloatBuffer buffer = this.getFloatBuffer();
        buffer.position(0);
        buffer.put(bufferData);
        buffer.position(0);

        super.setHardwareBufferNeedsUpdate();
    }
}