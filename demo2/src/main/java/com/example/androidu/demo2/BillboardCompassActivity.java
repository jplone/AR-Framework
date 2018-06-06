package com.example.androidu.demo2;

import android.hardware.SensorEvent;
import android.opengl.GLES20;
import android.os.Bundle;

import edu.calstatela.jplone.arframework.graphics3d.camera.ARGLCamera;
import edu.calstatela.jplone.arframework.graphics3d.drawable.Billboard;
import edu.calstatela.jplone.arframework.graphics3d.drawable.BillboardMaker;
import edu.calstatela.jplone.arframework.graphics3d.entity.Entity;
import edu.calstatela.jplone.arframework.graphics3d.projection.Projection;
import edu.calstatela.jplone.arframework.graphics3d.scene.Scene;
import edu.calstatela.jplone.arframework.sensor.ARSensor;
import edu.calstatela.jplone.arframework.ui.ARActivity;


public class BillboardCompassActivity extends ARActivity {
    static final String TAG = "waka_BBCompass";

    private Scene mScene;
    private ARGLCamera mCamera;
    private Projection mProjection;

    private ARSensor mOrientationSensor;
    private float[] mOrientation = null;


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
    }

    @Override
    protected void onPause() {
        super.onPause();
        mOrientationSensor.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mOrientationSensor.start();
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

        mScene = new Scene();

        Billboard north = BillboardMaker.make(this, R.drawable.ara_icon, "North", "A compass direction");
        Billboard east = BillboardMaker.make(this, R.drawable.ara_icon, "East", "A compass direction");
        Billboard south = BillboardMaker.make(this, R.drawable.ara_icon, "South", "A compass direction");
        Billboard west = BillboardMaker.make(this, R.drawable.ara_icon, "West", "A compass direction");

        Entity northBB = mScene.addDrawable(north);
        Entity eastBB = mScene.addDrawable(east);
        Entity southBB = mScene.addDrawable(south);
        Entity westBB = mScene.addDrawable(west);

        northBB.yaw(0); northBB.move(0, 0, -5); northBB.setScale(2, 1, 1);
        eastBB.yaw(-90); eastBB.move(5, 0, 0); eastBB.setScale(2, 1, 1);
        southBB.yaw(180); southBB.move(0, 0, 5); southBB.setScale(2, 1, 1);
        westBB.yaw(90); westBB.move(-5, 0, 0); westBB.setScale(2, 1, 1);

        mCamera = new ARGLCamera();
        mProjection = new Projection();
    }

    @Override
    public void GLResize(int width, int height) {
        super.GLResize(width, height);

        GLES20.glViewport(0, 0, width, height);
        mProjection.setPerspective(60, (float)width / height, 0.1f, 1000f);
    }

    @Override
    public void GLDraw() {
        super.GLDraw();

        if(mOrientation != null)
            mCamera.setOrientationVector(mOrientation, 0);

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        mScene.draw(mProjection.getProjectionMatrix(), mCamera.getViewMatrix());
    }




    //////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      Orientation Listener
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
    ARSensor.Listener mOrientationListener = new ARSensor.Listener() {
        @Override
        public void onSensorEvent(SensorEvent event) {
            if (mOrientation == null) {
                mOrientation = new float[3];
            }

            mOrientation[0] = event.values[0];
            mOrientation[1] = event.values[1];
            mOrientation[2] = event.values[2];
        }
    };


}
