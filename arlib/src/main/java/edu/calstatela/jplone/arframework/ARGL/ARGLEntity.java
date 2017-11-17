package edu.calstatela.jplone.arframework.ARGL;

import android.opengl.Matrix;

import edu.calstatela.jplone.arframework.Utils.ARMath;

/**
 * Created by bill on 11/2/17.
 */

public class ARGLEntity {
    private ARGLMesh mModel = null;
    private float[] mModelMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];
    private float[] mColor = {0, 1, 0, 1};

    public ARGLEntity(){
        Matrix.setIdentityM(mModelMatrix, 0);
    }

    public void setModel(ARGLMesh model){
        mModel = model;
    }

    public void setModelMatrix(float[] matrix){
        ARMath.copyVec(matrix, mModelMatrix, 16);
    }

    public float[] getModelMatrix(){
        return mModelMatrix;
    }

    public void setColor(float[] color){
        mColor = color;
    }

    public void draw(float[] VPMatrix){
        Matrix.multiplyMM(mMVPMatrix, 0, VPMatrix, 0, mModelMatrix, 0);
        mModel.setColor(mColor);
        mModel.draw(mMVPMatrix);
    }
}
