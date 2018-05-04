package edu.calstatela.jplone.arframework.ARGL.Drawable;

import android.opengl.Matrix;

public class ARGLDrawable {

    public void draw(float[] projectionMatrix, float[] viewMatrix, float[] modelMatrix){
        float[] matrix = new float[16];

        Matrix.multiplyMM(matrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(matrix, 0, projectionMatrix, 0, matrix, 0);

        draw(matrix);
    }

    public void draw(float[] matrix){

    }
}
