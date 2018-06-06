package com.example.androidu.demo2;

import android.hardware.SensorEvent;
import android.location.Location;
import android.opengl.GLES20;
import android.os.Bundle;

import edu.calstatela.jplone.arframework.graphics3d.camera.ARGLCamera;
import edu.calstatela.jplone.arframework.graphics3d.drawable.Billboard;
import edu.calstatela.jplone.arframework.graphics3d.drawable.BillboardMaker;
import edu.calstatela.jplone.arframework.graphics3d.entity.Entity;
import edu.calstatela.jplone.arframework.graphics3d.projection.Projection;
import edu.calstatela.jplone.arframework.graphics3d.scene.CircleScene;
import edu.calstatela.jplone.arframework.landmark.Landmark;
import edu.calstatela.jplone.arframework.landmark.LandmarkTable;
import edu.calstatela.jplone.arframework.sensor.ARGps;
import edu.calstatela.jplone.arframework.sensor.ARSensor;
import edu.calstatela.jplone.arframework.ui.ARActivity;
import edu.calstatela.jplone.arframework.util.GeoMath;


public class BillboardLandmarksActivity extends ARActivity {
    static final String TAG = "waka_BBLandmarks";


    CircleScene mScene;
    ARGLCamera mCamera;
    Projection mProjection;

    ARSensor mOrientationSensor;
    ARGps mGps;


    //////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      Activity Callbacks
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mOrientationSensor = new ARSensor(this, ARSensor.ROTATION_VECTOR);
        mOrientationSensor.addListener(mOrientationListener);

        mGps = new ARGps(this);
        mGps.addListener(mGPSListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mOrientationSensor.stop();
        mGps.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mOrientationSensor.start();
        mGps.start();
    }


    //////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      OpenGL Callbacks
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void GLInit() {
        super.GLInit();

        GLES20.glClearColor(0, 0, 0, 0);

        mScene = new CircleScene();
        mScene.setRadius(20);
        mCamera = new ARGLCamera();
        mProjection = new Projection();

        setupBillboards();
    }

    @Override
    public void GLResize(int width, int height) {
        super.GLResize(width, height);

        GLES20.glViewport(0, 0, width, height);
        mProjection.setPerspective(60, (float)width / height, 0.1f, 100000000000f);
    }

    @Override
    public void GLDraw() {
        super.GLDraw();

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        if(latLonAlt != null && orientation != null){
            mCamera.setPositionLatLonAlt(latLonAlt);
            mCamera.setOrientationVector(orientation, 0);
            mScene.setCenterLatLonAlt(latLonAlt);
        }

        mScene.draw(mProjection.getProjectionMatrix(), mCamera.getViewMatrix());
    }


    //////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      Drawing Helper Functions
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
    private void setupBillboards(){

        LandmarkTable landmarkTable = new LandmarkTable();
        landmarkTable.loadCalstateLA();

        for(Landmark l : landmarkTable){
            Billboard bb = BillboardMaker.make(this, R.drawable.ara_icon, l.title, l.description);
            Entity entity = mScene.addDrawable(bb);
            entity.setLatLonAlt(new float[]{l.latitude, l.longitude, l.altitude});
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      Sensor Callbacks
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
    private float[] orientation = null;
    ARSensor.Listener mOrientationListener = new ARSensor.Listener() {
        @Override
        public void onSensorEvent(SensorEvent event) {

            if(orientation == null)
                orientation = new float[3];

            orientation[0] = event.values[0];
            orientation[1] = event.values[1];
            orientation[2] = event.values[2];
        }
    };


    private float[] latLonAlt = null;
    ARGps.Listener mGPSListener = new ARGps.Listener() {

        @Override
        public void handleLocation(Location location) {

            if(latLonAlt == null) {
                latLonAlt = new float[3];
                latLonAlt[0] = (float)location.getLatitude();
                latLonAlt[1] = (float)location.getLongitude();
                latLonAlt[2] = (float)location.getAltitude();
                GeoMath.setReference(latLonAlt);
            }

            latLonAlt[0] = (float)location.getLatitude();
            latLonAlt[1] = (float)location.getLongitude();
            latLonAlt[2] = (float)location.getAltitude();
        }
    };

}
