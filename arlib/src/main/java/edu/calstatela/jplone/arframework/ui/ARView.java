package edu.calstatela.jplone.arframework.ui;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import edu.calstatela.jplone.arframework.util.Permissions;

public class ARView extends FrameLayout {

    GLSurfaceView glSurfaceView;
    CameraView cameraView;

    public ARView(Context context){
        super(context);

        glSurfaceView = new GLSurfaceView(context);
        glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        glSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setRenderer(renderer);
        glSurfaceView.setZOrderMediaOverlay(true);
        addView(glSurfaceView);

        if(Permissions.havePermission(context, Permissions.PERMISSION_CAMERA)) {
            cameraView = new CameraView(context);
            addView(cameraView);
        }

        setOnTouchListener(touchListener);
    }

    public void setRenderer(GLSurfaceView.Renderer renderer){
        glSurfaceView.setRenderer(renderer);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      Overridable Methods
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void onPause(){
        glSurfaceView.onPause();
    }

    public void onResume(){
        glSurfaceView.onResume();
    }

    public void GLInit(){

    }

    public void GLResize(int width, int height){

    }

    public void GLDraw(){

    }

    public boolean onTouch(View v, MotionEvent event){
        return false;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      Event Callbacks that call the handler methods of this class
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private GLSurfaceView.Renderer renderer = new GLSurfaceView.Renderer(){

        @Override
        public void onDrawFrame(GL10 gl) {
            GLDraw();
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            GLResize(width, height);
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            GLInit();
        }
    };

    private View.OnTouchListener touchListener = new View.OnTouchListener(){

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return ARView.this.onTouch(v, event);
        }
    };


}
