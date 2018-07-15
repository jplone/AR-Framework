package edu.calstatela.jplone.arframework.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.hardware.SensorEvent;
import android.location.Location;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import edu.calstatela.jplone.arframework.sensor.ARGps;
import edu.calstatela.jplone.arframework.sensor.ARSensor;
import edu.calstatela.jplone.arframework.util.GeoMath;
import edu.calstatela.jplone.arframework.util.Permissions;


// TODO: Create ways to customize ARVIEW (buffer depth, gl version, etc)
// TODO: Make it so that camera view is released in onPause() and reconnected in onResume()

public class ARView extends FrameLayout {

    private GLSurfaceView glSurfaceView;
    private CameraView cameraView;

    private ARSensor orientationSensor;
    private ARGps locationSensor;
    private float[] currentOrientation;
    private float[] currentLocation;

    public ARView(Context context){
        super(context);

        glSurfaceView = new GLSurfaceView(context);
        glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 24, 0);
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

        orientationSensor = new ARSensor(context, ARSensor.ROTATION_VECTOR);
        orientationSensor.addListener(orientationListener);
        locationSensor = new ARGps(context);
        locationSensor.addListener(locationListener);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      Sensor Access Methods
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public float[] getOrientation(){
        return currentOrientation;
    }

    public float[] getLocation(){
        return currentLocation;
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      Utility Methods
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private Bitmap viewToBitmap(View v){
        Bitmap bmp = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        v.draw(c);
        return bmp;
    }



    public View getGLView(){
        return glSurfaceView;
    }

    public Bitmap getGLBitmap(){
        return viewToBitmap(getGLView());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      Overridable Methods
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void onPause(){
        glSurfaceView.onPause();
        orientationSensor.stop();
        locationSensor.stop();
    }

    public void onResume(){
        glSurfaceView.onResume();
        orientationSensor.start();
        locationSensor.start();
    }

    public void GLInit(){ }

    public void GLResize(int width, int height){ }

    public void GLDraw(){ }

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

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      Sensor Callbacks
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private ARGps.Listener locationListener = new ARGps.Listener(){
        @Override
        public void handleLocation(Location location){
            if(currentLocation == null){
                currentLocation = new float[3];
                currentLocation[0] = (float)location.getLatitude();
                currentLocation[1] = (float)location.getLongitude();
                currentLocation[2] = (float)location.getAltitude();
                GeoMath.setReference(currentLocation);
                return;
            }

            currentLocation[0] = (float)location.getLatitude();
            currentLocation[1] = (float)location.getLongitude();
            currentLocation[2] = (float)location.getAltitude();
        }
    };

    private ARSensor.Listener orientationListener = new ARSensor.Listener(){
        @Override
        public void onSensorEvent(SensorEvent event){
            if(currentOrientation == null){
                currentOrientation = new float[3];
            }

            currentOrientation[0] = event.values[0];
            currentOrientation[1] = event.values[1];
            currentOrientation[2] = event.values[2];
        }
    };



}
