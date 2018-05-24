package edu.calstatela.jplone.arframework.graphics3d.drawable;

import edu.calstatela.jplone.arframework.util.MatrixMath;

public class Drawable {
    // TODO - Separate Color stuff into a separate class/interface. (This requires adjusting subclasses as well)

    private static final float[] defaultColor = {0, 1, 0, 1};

    public void draw(float[] projectionMatrix, float[] viewMatrix, float[] modelMatrix) {
        float[] matrix = new float[16];
        MatrixMath.multiplyMatrices(matrix, projectionMatrix, viewMatrix, modelMatrix);
        draw(matrix);
    }

    public void draw(float[] matrix){}

    public void setColor(float[] color){}

    public float[] getColor(){return defaultColor;}

    public void drawColor(float[] projectionMatrix, float[] viewMatrix, float[] modelMatrix, float[] color){
        float[] tempColor = getColor();
        setColor(color);
        draw(projectionMatrix, viewMatrix, modelMatrix);
        setColor(tempColor);
    }

    public void drawColor(float[] matrix, float[] color){
        float[] tempColor = getColor();
        setColor(color);
        draw(matrix);
        setColor(tempColor);
    }
}
