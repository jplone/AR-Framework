package edu.calstatela.jplone.arframework.ARGL.Billboard;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.Matrix;

import edu.calstatela.jplone.arframework.ARGL.Unit.ARGLPosition;

/**
 * Created by bill on 11/14/17.
 */

public class ARGLSizedBillboard {
    protected static boolean initialized = false;
    ARGLBillboard mBillboard = new ARGLBillboard();
    float mScale = 1;
    float[] mScaleMatrix = new float[16];
    float[] mMatrix = new float[16];

    public static void init(Context context){
        if(initialized) // make sure this method is called only once
            return;
        ARGLBillboard.init(context);
        initialized = true;
    }

    public static void destroy() {
        ARGLBillboard.destroy();
        initialized = false;
    }

    public void setScale(int scale){
        if(scale > 0)
            mScale = scale;
    }

    public void setBitmap(Bitmap bitmap){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Matrix.setIdentityM(mScaleMatrix, 0);
        Matrix.scaleM(mScaleMatrix, 0, width * mScale / 500, height * mScale / 500, 1);

        mBillboard.setBitmap(bitmap);
    }

    public void draw(){
        Matrix.multiplyMM(mMatrix, 0, getMatrix(), 0, mScaleMatrix, 0);
        mBillboard.draw(mMatrix);

    }

    public void draw(float[] matrix){
        Matrix.multiplyMM(mMatrix, 0, matrix, 0, mScaleMatrix, 0);
        mBillboard.draw(mMatrix);
    }

    public float[] getMatrix(){
        return mBillboard.getMatrix();
    }

    public void setPosition(ARGLPosition position) {
        mBillboard.setPosition(position);
    }
}
