//  GLCameraActivity is a class that allows displaying what the camera sees, and allowing you to
//  draw on top of it using OpenGL calls.

// How to use:
//  + Make an Activity class that inherits from ARActivity
//  + Make sure to add Activity entry to Manifest
//  + Override GLInit(), GLDraw() and GLResize(int, int) methods as desired for doing openGL calls
//  + Use GLES20 class for your openGL calls, since this class currently only supports OpenGL ES 2
//  + If you want to place some Android UI Elements on top of the Camera and GL drawing, do the
//    following steps in your onCreate() method:
//      * Create the UI Elements you want and put them in a ViewGroup object
//      * Call the getTopFrameLayout() function to get ARActivity's FrameLayout
//      * Add your ViewGroup object to the FrameLayout
//  + Use the <android:screenOrientation=""> tag in the Manifest to control the Orientation of this
//    Activity.

package edu.calstatela.jplone.arframework;


import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import edu.calstatela.jplone.arframework.ARCamera.ARCameraView;
import edu.calstatela.jplone.arframework.Utils.ARPermissions;


public class ARActivity extends AppCompatActivity {

    private static final String TAG = "wakaGLCamActivity";

    private FrameLayout mFrameLayout;
    private GLSurfaceView mGLView;
    private ARCameraView mCameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();


        mFrameLayout = new FrameLayout(this);
        setContentView(mFrameLayout);


        mGLView = new GLSurfaceView(this);
        mGLView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        mGLView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        mGLView.setEGLContextClientVersion(2);
        mGLView.setRenderer(new BlankGLRenderer());
        mGLView.setOnTouchListener(touchListener);
        mFrameLayout.addView(mGLView);


        if(ARPermissions.havePermission(this, ARPermissions.PERMISSION_CAMERA)){
            mCameraView = new ARCameraView(this);
            mFrameLayout.addView(mCameraView);
        }
        else{
            ARPermissions.requestPermission(this, ARPermissions.PERMISSION_CAMERA);
        }


        mGLView.setZOrderMediaOverlay(true); // Ensures that mGLView shows up on top of mCameraView
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLView.onPause();
    }


    @Override
    protected void onResume() {
        super.onResume();
        mGLView.onResume();
    }


    public void setRenderer(GLSurfaceView.Renderer renderer){
        mGLView.setRenderer(renderer);
    }

    public FrameLayout getTopFrameLayout(){
        return mFrameLayout;
    }

    public void GLInit(){}

    public void GLResize(int width, int height){}

    public void GLDraw(){}





    public boolean onTouch(View v, MotionEvent event){return false;}

    View.OnTouchListener touchListener = new View.OnTouchListener(){

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return ARActivity.this.onTouch(v, event);
        }
    };

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      Blank Renderer Callback Object
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public class BlankGLRenderer implements GLSurfaceView.Renderer {

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
    }
}

//  Issues:
//      + Should make way to do 2D drawings with Canvas on top of both CameraView and GLView
//      + Allow the user to specify which version of OpenGL ES they want to use
//      + Make a way to control Activity options, such as orientation, hiding ActionBar, etc
//      + Might need to make a way to pause and resume the CameraView
//      + Add way to load ui components from layout.xml file