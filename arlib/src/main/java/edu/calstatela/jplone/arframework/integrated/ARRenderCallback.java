package edu.calstatela.jplone.arframework.integrated;

/**
 * Created by bill on 5/3/18.
 */

public interface ARRenderCallback {
    public void onGLInit();
    public void onGLResize(int width, int height);
    public void onGLDraw(float[] projection, float[] view);
}
