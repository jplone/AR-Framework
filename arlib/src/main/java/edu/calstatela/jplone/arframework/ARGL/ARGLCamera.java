package edu.calstatela.jplone.arframework.ARGL;

import android.opengl.Matrix;

import edu.calstatela.jplone.arframework.Utils.ARMath;

public class ARGLCamera {
    private static final String TAG = "waka_Camera3D";

    private static final float[] INITIAL_POSITION = {0,0,0,1};
    private static final float[] INITIAL_LOOK_VECTOR = {0,0,-1,0};
    private static final float[] INITIAL_UP_VECTOR = {0,1,0,0};
    private static final float[] INITIAL_RIGHT_VECTOR = {1,0,0,0};

    private float[] mPosition = new float[]{0, 0, 2, 1};
    private float[] mFrontVec = new float[]{0, 0, -1, 0};
    private float[] mUpVec = new float[]{0, 1, 0, 0};
    private float[] mRightVec = new float[]{1, 0, 0, 0};

    private float[] mViewMatrix = new float[16];
    private float[] mProjectionMatrix = new float[16];
    private float[] mViewProjectionMatrix = new float[16];

    private static final float[] tempMatrix = new float[16]; // used for calculations

    public void setByMatrix(float[] matrix){
        Matrix.multiplyMV(mFrontVec, 0, matrix, 0, INITIAL_LOOK_VECTOR, 0);
        Matrix.multiplyMV(mUpVec, 0, matrix, 0, INITIAL_UP_VECTOR, 0);
        Matrix.multiplyMV(mRightVec, 0, matrix, 0, INITIAL_RIGHT_VECTOR, 0);
    }

    public void set(float[] position, float[] frontVec, float[] upVec){
        ARMath.copyVec(position, mPosition, 3);                                 mPosition[3] =  1;
        ARMath.copyVec(frontVec, mFrontVec, 3);                                 mFrontVec[3] =  0;
        ARMath.copyVec(upVec, mUpVec, 3);                                       mUpVec[3]    =  0;
        ARMath.copyVec(ARMath.crossProduct(mFrontVec, mUpVec), mRightVec, 3);   mRightVec[3] =  0;
    }


    public void updateViewMatrix(){

        Matrix.setLookAtM(mViewMatrix, 0,
                mPosition[0], mPosition[1], mPosition[2],
                mPosition[0] + mFrontVec[0], mPosition[1] + mFrontVec[1], mPosition[2] + mFrontVec[2],
                mUpVec[0], mUpVec[1], mUpVec[2]);

        Matrix.multiplyMM(mViewProjectionMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
    }


    public void setPerspective(float viewAngle, float aspectRatio, float nearD, float farD){
        Matrix.perspectiveM(mProjectionMatrix, 0, viewAngle, aspectRatio, nearD, farD);
        Matrix.multiplyMM(mViewProjectionMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
    }


    public void pitch(float angle){
        Matrix.setRotateM(tempMatrix, 0, angle, mRightVec[0], mRightVec[1], mRightVec[2]);
        Matrix.multiplyMV(mFrontVec, 0, tempMatrix, 0, mFrontVec, 0);
        Matrix.multiplyMV(mUpVec, 0, tempMatrix, 0, mUpVec, 0);
    }


    public void roll(float angle){
        Matrix.setRotateM(tempMatrix, 0, angle, mFrontVec[0], mFrontVec[1], mFrontVec[2]);
        Matrix.multiplyMV(mRightVec, 0, tempMatrix, 0, mRightVec, 0);
        Matrix.multiplyMV(mUpVec, 0, tempMatrix, 0, mUpVec, 0);
    }


    public void yaw(float angle){
        Matrix.setRotateM(tempMatrix, 0, angle, mUpVec[0], mUpVec[1], mUpVec[2]);
        Matrix.multiplyMV(mFrontVec, 0, tempMatrix, 0, mFrontVec, 0);
        Matrix.multiplyMV(mRightVec, 0, tempMatrix, 0, mRightVec, 0);
    }


    public void slide(float dRight, float dUp, float dFront){
        mPosition[0] += dRight * mRightVec[0] + dUp * mUpVec[0] + dFront * mFrontVec[0];
        mPosition[1] += dRight * mRightVec[1] + dUp * mUpVec[1] + dFront * mFrontVec[1];
        mPosition[2] += dRight * mRightVec[2] + dUp * mUpVec[2] + dFront * mFrontVec[2];
//        Log.d(TAG, "position: " + MyMath.vecToString(mFrontVec));
//        Log.d(TAG, "dRight: " + dRight + "   dUp: " + dUp + "   dFront: " + dFront);
    }


    public void move(float dX, float dY, float dZ){
        mPosition[0] += dX;
        mPosition[1] += dY;
        mPosition[2] += dZ;
    }


    public void setPosition(float x, float y, float z){
        mPosition[0] = x;
        mPosition[1] = y;
        mPosition[2] = z;
    }


    public float[] getViewMatrix(){
        return mViewMatrix;
    }


    public float[] getProjectionMatrix(){
        return mProjectionMatrix;
    }


    public float[] getViewProjectionMatrix(){
        return mViewProjectionMatrix;
    }


}
