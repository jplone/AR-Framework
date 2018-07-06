package edu.calstatela.jplone.arframework.matrix;

public class ModelMatrix {

    private float[] mRotationMatrix = new float[16];
    private float[] mScaleMatrix = new float[16];
    private float[] mTranslateMatrix = new float[16];
    private float[] mMatrix = new float[16];
    private boolean mMatrixIsClean = false;

    private float[] mScratchMatrix = new float[16];
    private float[] mScratchVector1 = new float[4];
    private float[] mScratchVector2 = new float[4];

    public ModelMatrix(){
        MatrixMath.setIdentity(mRotationMatrix);
        MatrixMath.setIdentity(mScaleMatrix);
        MatrixMath.setIdentity(mTranslateMatrix);
    }

    public void setScale(float x, float y, float z){
        mScaleMatrix[0] = x;
        mScaleMatrix[5] = y;
        mScaleMatrix[10] = z;
        mMatrixIsClean = false;
    }

    public void setPosition(float x, float y, float z){
        mTranslateMatrix[3] = x;
        mTranslateMatrix[7] = y;
        mTranslateMatrix[11] = z;
        mMatrixIsClean = false;
    }

    public void move(float dx, float dy, float dz){
        mTranslateMatrix[3] += dx;
        mTranslateMatrix[7] += dy;
        mTranslateMatrix[11] += dz;
        mMatrixIsClean = false;
    }

    public void slide(float dx, float dy, float dz){
        mScratchVector1[0] = dx;
        mScratchVector1[1] = dy;
        mScratchVector1[2] = dz;
        mScratchVector1[3] = 0;

        MatrixMath.multiplyMatrixVec(mScratchVector2, mRotationMatrix, mScratchVector1);

        mTranslateMatrix[3] += mScratchVector2[0];
        mTranslateMatrix[7] += mScratchVector2[1];
        mTranslateMatrix[11] += mScratchVector2[2];
        mMatrixIsClean = false;
    }

    public void pitch(float angle){

    }

    public void roll(float angle){}
    public void yaw(float angle){}

    public void setRotationQuaternion(float[] quaternion){}

    public void setLookAt(float[] position, float[] lookPos, float[] upVec){}

    public float[] getArray(){
        if(!mMatrixIsClean){
            MatrixMath.multiply3Matrices(mMatrix, mTranslateMatrix, mRotationMatrix, mScaleMatrix);
            mMatrixIsClean = true;
        }

        return mMatrix;
    }
}
