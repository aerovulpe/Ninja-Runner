package com.aerisvulpe.shinobi.customs;
 
import org.anddev.andengine.opengl.util.FastFloatBuffer;
import org.anddev.andengine.opengl.vertex.RectangleVertexBuffer;
 
/**
 * (c) 2011 Natalia Pujol
 *
 * @author Natalia Pujol
 */
public class RectanglePackedVertexBuffer extends RectangleVertexBuffer {
        // ===========================================================
        // Constants
        // ===========================================================
 
        // ===========================================================
        // Fields
        // ===========================================================
 
        protected float mOffX = 0f;
        protected float mOffY = 0f;
        protected boolean mRotated = false;
       
        // ===========================================================
        // Constructors
        // ===========================================================
 
        public RectanglePackedVertexBuffer(final float pOffX, final float pOffY, boolean pRotated, final int pDrawType, final boolean pManaged) {
                super(pDrawType, pManaged);
                mOffX = pOffX;
                mOffY = pOffY;
                mRotated = pRotated;
        }
 
        // ===========================================================
        // Getter & Setter
        // ===========================================================
 
        public void setOffsetX(float pOffX) {
                mOffX = pOffX;
        }
       
        public void setOffsetY(float pOffY) {
                mOffY = pOffY;
        }
       
        // ===========================================================
        // Methods for/from SuperClass/Interfaces
        // ===========================================================
 
        // ===========================================================
        // Methods
        // ===========================================================
 
        @Override
        public synchronized void update(final float pWidth, final float pHeight) {
               
                final int[] bufferData = this.mBufferData;
 
                if (!mRotated) {
                        final int x = Float.floatToRawIntBits(mOffX);
                        final int y = Float.floatToRawIntBits(mOffY);
                        final int x2 = Float.floatToRawIntBits(mOffX+pWidth);
                        final int y2 = Float.floatToRawIntBits(mOffY+pHeight);
 
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
                        final int y = Float.floatToRawIntBits(mOffY+pHeight);
                        final int x2 = Float.floatToRawIntBits(mOffX+pWidth);
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
 
        // ===========================================================
        // Inner and Anonymous Classes
        // ===========================================================
}