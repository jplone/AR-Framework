package edu.calstatela.jplone.arframework.util;

import android.opengl.Matrix;

public class MatrixMath {

    public static void multiply3Matrices(float[] result, float[] projectionMatrix, float[] viewMatrix, float[] modelMatrix){
        Matrix.multiplyMM(result, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(result, 0, projectionMatrix, 0, result, 0);
    }

    public static void multiply2Matrices(float[] result, float[] leftMatrix, float[] rightMatrix){
        Matrix.multiplyMM(result, 0, leftMatrix, 0, rightMatrix, 0);
    }

    public static void multiplyMatrixVec(float[] resultVec, float[] matrix, float[]inputVec){
        resultVec[0] = matrix[0] * inputVec[0] + matrix[1] * inputVec[1] + matrix[2] * inputVec[2] + matrix[3] * inputVec[3];
        resultVec[1] = matrix[4] * inputVec[0] + matrix[5] * inputVec[1] + matrix[6] * inputVec[2] + matrix[7] * inputVec[3];
        resultVec[2] = matrix[8] * inputVec[0] + matrix[9] * inputVec[1] + matrix[10] * inputVec[2] + matrix[11] * inputVec[3];
        resultVec[3] = matrix[12] * inputVec[0] + matrix[13] * inputVec[1] + matrix[14] * inputVec[2] + matrix[15] * inputVec[3];
    }

    public static void setIdentity(float[] matrix){
        copy(MatrixMath.IDENTITY_MATRIX, matrix);
    }

    public static void copy(float[] srcMat, float[] destMat){
        VectorMath.copyVec(srcMat, destMat, 16);
    }

    public static final float[] IDENTITY_MATRIX = {
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1
    };

    public static String matrixToString(float[] matrix, int m, int n){
        StringBuilder sb = new StringBuilder();
        sb.append("\n======================================\n");

        for(int i = 0; i < m; i++){
            for(int j = 0; j < n; j++){
                sb.append(String.format("  % .2f  ", matrix[i * n + j]));
            }
            sb.append("\n");
        }

        sb.append("======================================\n");

        return sb.toString();
    }
}
