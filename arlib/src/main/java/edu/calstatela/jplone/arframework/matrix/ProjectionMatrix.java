package edu.calstatela.jplone.arframework.matrix;

import android.opengl.Matrix;

public class ProjectionMatrix {

    private final float[] projectionMatrix = new float[16];

    public ProjectionMatrix(){
        MatrixMath.setIdentity(projectionMatrix);
    }

    public void setPerspective(float viewAngle, float aspectRatio, float nearDistance, float farDistance){
        Matrix.perspectiveM(projectionMatrix, 0, viewAngle, aspectRatio, nearDistance, farDistance);
    }

    public float[] getArray(){
        return projectionMatrix;
    }
}
