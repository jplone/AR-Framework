package edu.calstatela.jplone.arframework.ARGL;

import android.opengl.Matrix;

import edu.calstatela.jplone.arframework.Utils.ARMath;

public class ARGLProjection {

    private final float[] projectionMatrix = new float[16];

    public ARGLProjection(){
        ARMath.copyVec(ARMath.IDENTITY_MATRIX, projectionMatrix, 16);
    }

    public void setPerspective(float viewAngle, float aspectRatio, float nearDistance, float farDistance){
        Matrix.perspectiveM(projectionMatrix, 0, viewAngle, aspectRatio, nearDistance, farDistance);
    }

    public float[] getProjectionMatrix(){
        return projectionMatrix;
    }
}
