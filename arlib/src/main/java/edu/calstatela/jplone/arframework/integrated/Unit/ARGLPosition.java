package edu.calstatela.jplone.arframework.integrated.Unit;

import android.opengl.Matrix;

/**
 * Created by bill on 11/14/17.
 */

public class ARGLPosition {
    public static final int AXIS_X = 0;
    public static final int AXIS_Y = 1;
    public static final int AXIS_Z = 2;

    private float[] matrix = new float[16];

    public ARGLPosition(float x, float y, float z) {
        Matrix.setIdentityM(matrix, 0);
        translate(x, y, z);
    }

    public ARGLPosition(float tx, float ty, float tz, float angle, float rx, float ry, float rz) {
        Matrix.setIdentityM(matrix, 0);
        translate(tx, ty, tz);
        rotate(angle, rx, ry, rz);
    }

    public boolean compare(ARGLPosition pos) {
        boolean equal = true;

        for(int i=0; i<16; i++)
            if(this.matrix[i] != pos.matrix[i])
                equal = false;

        return equal;
    }

    public void translate(float x, float y, float z) {
        Matrix.translateM(matrix, 0, x, y, z);
    }

    public void rotate(float angle, float x, float y, float z) {
        float[] scratch = new float[16];
        Matrix.setRotateM(scratch, 0, angle, x, y, z);
        Matrix.multiplyMM(matrix, 0, scratch, 0, matrix, 0);
    }

    public float[] getMatrix() {
        return matrix;
    }

    public float[] getCenter() {
        float[] point3D = new float[4];

        Matrix.multiplyMV(point3D, 0, matrix, 0, new float[]{0, 0, 0, 1}, 0);

        return point3D;
    }
}
