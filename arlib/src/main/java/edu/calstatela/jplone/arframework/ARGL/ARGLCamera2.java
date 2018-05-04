package edu.calstatela.jplone.arframework.ARGL;

import android.opengl.Matrix;

import edu.calstatela.jplone.arframework.Utils.ARMath;

public class ARGLCamera2 {

    private static String TAG = "waka-Camera";

    private float[] viewMatrix = new float[16];

    private float[] pos = {0, 0, 0, 0};
    private float[] front = {0, 0, -1, 0};
    private float[] right = {1, 0, 0, 0};
    private float[] up = {0, 1, 0, 0};

    private float[] scratchMat = new float[16];  // temporary matrix for calculations
    private float[] scratchVec = new float[4];   // temporary vector for calculations

    private boolean matrixIsClean = false;

    /////////////////////////////////////////////////////////////////////////////////////////////////

    public void setLookAt(float[] eye, float[] look, float[] up){
        Matrix.setLookAtM(viewMatrix, 0, eye[0], eye[1], eye[2], look[0], look[1], look[2], up[0], up[1], up[2]);
        matrixIsClean = true;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////

    public void resetViewMatrix(){
        Matrix.setIdentityM(viewMatrix, 0);
    }

    public void setPosition(float x, float y, float z){
        pos[0] = x; pos[1] = y; pos[2] = z;
        matrixIsClean = false;
    }


    public void move(float dx, float dy, float dz){
        pos[0] += dx; pos[1] += dy; pos[2] += dz;
        matrixIsClean = false;
    }

    public void slide(float dx, float dy, float dz){
        pos[0] += right[0] * dx + right[1] * dy + right[2] * dz;
        pos[1] += up[0] * dx + up[1] * dy + up[2] * dz;
        pos[2] += front[0] * dx + front[1] * dy + front[2] * dz;
        matrixIsClean = false;
    }

    public void setOrientation(float angle, float axisX, float axisY, float axisZ){
        front[0] = 0; front[1] = 0; front[2] = -1;
        right[0] = 1; right[1] = 0; right[2] = 0;
        up[0] = 0; up[1] = 1; up[2] = 0;

        Matrix.setRotateM(scratchMat, 0, angle, axisX, axisY, axisZ);
        Matrix.multiplyMV(scratchVec, 0, scratchMat, 0, front, 0); ARMath.copyVec(scratchVec, front, 4);
        Matrix.multiplyMV(scratchVec, 0, scratchMat, 0, up, 0); ARMath.copyVec(scratchVec, up, 4);
        Matrix.multiplyMV(scratchVec, 0, scratchMat, 0, right, 0); ARMath.copyVec(scratchVec, right, 4);
        matrixIsClean = false;
    }

    public void rotate(float angle, float axisX, float axisY, float axisZ){
        Matrix.setRotateM(scratchMat, 0, angle, axisX, axisY, axisZ);
        Matrix.multiplyMV(scratchVec, 0, scratchMat, 0, front, 0); ARMath.copyVec(scratchVec, front, 4);
        Matrix.multiplyMV(scratchVec, 0, scratchMat, 0, up, 0); ARMath.copyVec(scratchVec, up, 4);
        Matrix.multiplyMV(scratchVec, 0, scratchMat, 0, right, 0); ARMath.copyVec(scratchVec, right, 4);
        matrixIsClean = false;
    }

    public void setRotationMatrix(float[] matrix){
        front[0] = 0; front[1] = 0; front[2] = -1;
        right[0] = 1; right[1] = 0; right[2] = 0;
        up[0] = 0; up[1] = 1; up[2] = 0;

        Matrix.multiplyMV(scratchVec, 0, scratchMat, 0, front, 0); ARMath.copyVec(scratchVec, front, 4);
        Matrix.multiplyMV(scratchVec, 0, scratchMat, 0, up, 0); ARMath.copyVec(scratchVec, up, 4);
        Matrix.multiplyMV(scratchVec, 0, scratchMat, 0, right, 0); ARMath.copyVec(scratchVec, right, 4);
        matrixIsClean = false;
    }

    private void updateViewMatrix(){
        Matrix.setLookAtM(viewMatrix, 0, pos[0], pos[1], pos[2], pos[0] + front[0], pos[1] + front[1], pos[2] + front[2], up[0], up[1], up[2]);
        matrixIsClean = true;
    };

    /////////////////////////////////////////////////////////////////////////////////////////////////


    public float[] getViewMatrix(){
        if(!matrixIsClean)
            updateViewMatrix();
        return viewMatrix;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      Implement these eventually
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////

            // public void pitch/yaw/roll(...)


    /////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      Advanced methods. Put these in subclass??
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////

    public void setPositionLatLonAlt(float[] latLonAlt){
        float[] xyz = new float[3];
        ARMath.latLonAltToXYZ(latLonAlt, xyz);
        setPosition(xyz[0], xyz[1], xyz[2]);
        matrixIsClean = false;
    }


    // FIX NEEDED!!!
    // This needs to be fixed so that 'deviceRotation' can adjust for orientations
    // other than portrait.
    public void setOrientationVector(float[] rotationQuaternion, int deviceRotation){
        float magnitude = ARMath.magnitude(rotationQuaternion);
        float angle = ARMath.radToDegrees( (float) Math.asin(magnitude) * 2 );
        float x = rotationQuaternion[0] / magnitude;
        float y = rotationQuaternion[1] / magnitude;
        float z = rotationQuaternion[2] / magnitude;

//        Log.d(TAG, String.format("%f   (%f, %f, %f)", angle, x, y, z));

        Matrix.setRotateM(scratchMat, 0, angle, x, y, z);

        front[0] = 0; front[1] = 0; front[2] = -1;
        right[0] = 1; right[1] = 0; right[2] = 0;
        up[0] = 0; up[1] = 1; up[2] = 0;

        Matrix.multiplyMV(scratchVec, 0, scratchMat, 0, front, 0); ARMath.copyVec(scratchVec, front, 4);
        Matrix.multiplyMV(scratchVec, 0, scratchMat, 0, up, 0); ARMath.copyVec(scratchVec, up, 4);
        Matrix.multiplyMV(scratchVec, 0, scratchMat, 0, right, 0); ARMath.copyVec(scratchVec, right, 4);


        Matrix.setRotateM(scratchMat, 0, -90, 1, 0, 0);

        Matrix.multiplyMV(scratchVec, 0, scratchMat, 0, front, 0); ARMath.copyVec(scratchVec, front, 4);
        Matrix.multiplyMV(scratchVec, 0, scratchMat, 0, up, 0); ARMath.copyVec(scratchVec, up, 4);
        Matrix.multiplyMV(scratchVec, 0, scratchMat, 0, right, 0); ARMath.copyVec(scratchVec, right, 4);
        matrixIsClean = false;
    }

}
