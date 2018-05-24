package edu.calstatela.jplone.arframework.graphics3d.camera;

import android.opengl.Matrix;

import edu.calstatela.jplone.arframework.util.VectorMath;
import edu.calstatela.jplone.arframework.util.GeoMath;

public class ARGLCamera {

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

    // TODO - Camera: Fix so that setLookAt(...) can be used with other movement functions.
    // This function works, but it does not update the front, right and up vectors. Therefore
    // this function cannot be mixed with other camera movement functions.
    public void setLookAt(float[] eye, float[] look, float[] up){
        Matrix.setLookAtM(viewMatrix, 0, eye[0], eye[1], eye[2], look[0], look[1], look[2], up[0], up[1], up[2]);
        matrixIsClean = true;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////

    public void reset(){
        resetVectors();
        matrixIsClean = false;
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
        resetVectors();

        Matrix.setRotateM(scratchMat, 0, angle, axisX, axisY, axisZ);
        Matrix.multiplyMV(scratchVec, 0, scratchMat, 0, front, 0); VectorMath.copyVec(scratchVec, front, 4);
        Matrix.multiplyMV(scratchVec, 0, scratchMat, 0, up, 0); VectorMath.copyVec(scratchVec, up, 4);
        Matrix.multiplyMV(scratchVec, 0, scratchMat, 0, right, 0); VectorMath.copyVec(scratchVec, right, 4);
        matrixIsClean = false;
    }

    public void rotate(float angle, float axisX, float axisY, float axisZ){
        Matrix.setRotateM(scratchMat, 0, angle, axisX, axisY, axisZ);
        Matrix.multiplyMV(scratchVec, 0, scratchMat, 0, front, 0); VectorMath.copyVec(scratchVec, front, 4);
        Matrix.multiplyMV(scratchVec, 0, scratchMat, 0, up, 0); VectorMath.copyVec(scratchVec, up, 4);
        Matrix.multiplyMV(scratchVec, 0, scratchMat, 0, right, 0); VectorMath.copyVec(scratchVec, right, 4);
        matrixIsClean = false;
    }

    public void setRotationMatrix(float[] matrix){
        resetVectors();

        Matrix.multiplyMV(scratchVec, 0, scratchMat, 0, front, 0); VectorMath.copyVec(scratchVec, front, 4);
        Matrix.multiplyMV(scratchVec, 0, scratchMat, 0, up, 0); VectorMath.copyVec(scratchVec, up, 4);
        Matrix.multiplyMV(scratchVec, 0, scratchMat, 0, right, 0); VectorMath.copyVec(scratchVec, right, 4);
        matrixIsClean = false;
    }

    private void updateViewMatrix(){
        Matrix.setLookAtM(viewMatrix, 0, pos[0], pos[1], pos[2], pos[0] + front[0], pos[1] + front[1], pos[2] + front[2], up[0], up[1], up[2]);
        matrixIsClean = true;
    };

    private void resetVectors(){
        front[0] = 0;   front[1] = 0;   front[2] = -1;
        right[0] = 1;   right[1] = 0;   right[2] = 0;
        up[0]    = 0;   up[1]    = 1;   up[2]    = 0;
    }
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
        GeoMath.latLonAltToXYZ(latLonAlt, xyz);
        setPosition(xyz[0], xyz[1], xyz[2]);
        matrixIsClean = false;
    }


    // FIX NEEDED!!!
    // This needs to be fixed so that 'deviceRotation' can adjust for orientations
    // other than portrait.
    public void setOrientationVector(float[] rotationQuaternion, int deviceRotation){
        float magnitude = VectorMath.magnitude(rotationQuaternion);
        float angle = VectorMath.radToDegrees( (float)Math.asin(magnitude) * 2 );
        float x = rotationQuaternion[0] / magnitude;
        float y = rotationQuaternion[1] / magnitude;
        float z = rotationQuaternion[2] / magnitude;

        Matrix.setRotateM(scratchMat, 0, angle, x, y, z);

        resetVectors();

        Matrix.multiplyMV(scratchVec, 0, scratchMat, 0, front, 0); VectorMath.copyVec(scratchVec, front, 4);
        Matrix.multiplyMV(scratchVec, 0, scratchMat, 0, up, 0); VectorMath.copyVec(scratchVec, up, 4);
        Matrix.multiplyMV(scratchVec, 0, scratchMat, 0, right, 0); VectorMath.copyVec(scratchVec, right, 4);


        Matrix.setRotateM(scratchMat, 0, -90, 1, 0, 0);

        Matrix.multiplyMV(scratchVec, 0, scratchMat, 0, front, 0); VectorMath.copyVec(scratchVec, front, 4);
        Matrix.multiplyMV(scratchVec, 0, scratchMat, 0, up, 0); VectorMath.copyVec(scratchVec, up, 4);
        Matrix.multiplyMV(scratchVec, 0, scratchMat, 0, right, 0); VectorMath.copyVec(scratchVec, right, 4);
        matrixIsClean = false;
    }

}
