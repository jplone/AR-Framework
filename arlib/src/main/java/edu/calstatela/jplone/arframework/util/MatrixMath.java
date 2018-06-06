package edu.calstatela.jplone.arframework.util;

import android.opengl.Matrix;

public class MatrixMath {
    public static void multiplyMatrices(float[] result, float[] projectionMatrix, float[] viewMatrix, float[] modelMatrix){
        Matrix.multiplyMM(result, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(result, 0, projectionMatrix, 0, result, 0);
    }

    public static void multiplyMatrices(float[] result, float[] leftMatrix, float[] rightMatrix){
        Matrix.multiplyMM(result, 0, leftMatrix, 0, rightMatrix, 0);
    }

    public static final float[] IDENTITY_MATRIX = {
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1
    };
}
