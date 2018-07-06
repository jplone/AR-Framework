package edu.calstatela.jplone.arframework.graphics3d.projection;

import android.opengl.Matrix;

import edu.calstatela.jplone.arframework.util.MatrixMath;
import edu.calstatela.jplone.arframework.util.VectorMath1;

public class Projection {

    private final float[] projectionMatrix = new float[16];

    public Projection(){
        VectorMath1.copyVec(MatrixMath.IDENTITY_MATRIX, projectionMatrix, 16);
    }

    public void setPerspective(float viewAngle, float aspectRatio, float nearDistance, float farDistance){
        Matrix.perspectiveM(projectionMatrix, 0, viewAngle, aspectRatio, nearDistance, farDistance);
    }

    public float[] getProjectionMatrix(){
        return projectionMatrix;
    }

}
