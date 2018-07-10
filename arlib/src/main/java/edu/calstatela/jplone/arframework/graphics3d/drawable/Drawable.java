package edu.calstatela.jplone.arframework.graphics3d.drawable;

public interface Drawable {

    public void draw(float[] projectionMatrix, float[] viewMatrix, float[] modelMatrix);

    public void draw(float[] matrix);
}
