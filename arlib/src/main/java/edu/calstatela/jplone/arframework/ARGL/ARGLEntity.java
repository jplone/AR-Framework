package edu.calstatela.jplone.arframework.ARGL;

import android.opengl.Matrix;

import edu.calstatela.jplone.arframework.ARGL.Drawable.ARGLDrawable;
import edu.calstatela.jplone.arframework.Utils.ARMath;

/**
 * Created by bill on 11/2/17.
 */

public class ARGLEntity {
    private float scaleX = 1, scaleY = 1, scaleZ = 1;
    private float posX, posY, posZ;
    private float rotationAngle;
    private float[] modelMatrix = new float[16];
    private ARGLDrawable drawable = null;

    private boolean matrixIsClean = false;

    public void setScale(float scaleX, float scaleY, float scaleZ){
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.scaleZ = scaleZ;
        matrixIsClean = false;
    }

    public void setPosition(float x, float y, float z){
        posX = x;
        posY = y;
        posZ = z;
        matrixIsClean = false;
    }

    public void setPositionLatLonAlt(float[] latLonAlt){
        float[] xyz = new float[3];
        ARMath.latLonAltToXYZ(latLonAlt, xyz);
        setPosition(xyz[0], xyz[1], xyz[2]);
        matrixIsClean = false;
    }

    public void setRotation(float angle){
        rotationAngle = angle;
        matrixIsClean = false;
    }

    private void updateModelMatrix(){
        // FIX THIS!!!
        // Why does this matrix order work? It should be scale, rotate, translate. Scale is acting funny too
        Matrix.scaleM(modelMatrix, 0, ARMath.IDENTITY_MATRIX, 0, scaleX, scaleY, scaleZ);
        Matrix.translateM(modelMatrix, 0, posX, posY, posZ);
        Matrix.rotateM(modelMatrix, 0, rotationAngle, 0, 1, 0);
        matrixIsClean = true;
    }

    public float[] getModelMatrix(){
        if(!matrixIsClean)
            updateModelMatrix();

        return modelMatrix;
    }

    public void setDrawable(ARGLDrawable d){
        drawable = d;
    }

    public void draw(float[] projection, float[] view, float[] model){
        if(drawable == null) return;
        drawable.draw(projection, view, model);
    }
}
