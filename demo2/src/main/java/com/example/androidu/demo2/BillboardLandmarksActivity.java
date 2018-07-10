package com.example.androidu.demo2;

import android.graphics.Bitmap;
import android.hardware.SensorEvent;
import android.location.Location;
import android.opengl.GLES20;
import android.opengl.GLException;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;

import java.nio.IntBuffer;

import edu.calstatela.jplone.arframework.graphics3d.camera.ARGLCamera;
import edu.calstatela.jplone.arframework.graphics3d.drawable.Billboard;
import edu.calstatela.jplone.arframework.graphics3d.drawable.BillboardMaker;
import edu.calstatela.jplone.arframework.graphics3d.entity.Entity;
import edu.calstatela.jplone.arframework.graphics3d.entity.ScaleObject;
import edu.calstatela.jplone.arframework.graphics3d.projection.Projection;
import edu.calstatela.jplone.arframework.graphics3d.scene.CircleScene;
import edu.calstatela.jplone.arframework.graphics3d.scene.LandmarkScene;
import edu.calstatela.jplone.arframework.graphics3d.scene.ScalingCircleScene;
import edu.calstatela.jplone.arframework.landmark.Landmark;
import edu.calstatela.jplone.arframework.landmark.LandmarkTable;
import edu.calstatela.jplone.arframework.sensor.ARGps;
import edu.calstatela.jplone.arframework.sensor.ARSensor;
import edu.calstatela.jplone.arframework.ui.ARActivity;
import edu.calstatela.jplone.arframework.ui.SensorARActivity;
import edu.calstatela.jplone.arframework.util.GeoMath;
import edu.calstatela.jplone.arframework.util.VectorMath;


public class BillboardLandmarksActivity extends SensorARActivity {
    static final String TAG = "waka_BBLandmarks";


    LandmarkScene mScene;
    ARGLCamera mCamera;
    Projection mProjection;


    LandmarkTable landmarkTable = new LandmarkTable();

    float x = -1, y = -1;



    //////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      OpenGL Callbacks
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void GLInit() {
        super.GLInit();
        Billboard.init();

        GLES20.glClearColor(0, 0, 0, 0);

        mScene = new LandmarkScene();
        mScene.setScale(0.2f);
        mCamera = new ARGLCamera();
        mProjection = new Projection();

        landmarkTable = new LandmarkTable();
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
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

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        if(mScene.isEmpty() && getLocation() != null)
            setupBillboards();

        if(getLocation() != null && getOrientation() != null){
            mCamera.setPositionLatLonAlt(getLocation());
            mCamera.setOrientationVector(getOrientation(), 0);
            mScene.setCenterLatLonAlt(getLocation());
            mScene.update();
        }

        mScene.draw(mProjection.getProjectionMatrix(), mCamera.getViewMatrix());

        if(x >= 0){
            float[] position = new float[4];
            GeoMath.latLonAltToXYZ(getLocation(), position);
            int closestIndex = mScene.findClosestEntity(x, y, getARView().getWidth(), getARView().getHeight(), 0.2f, mProjection.getProjectionMatrix(), mCamera.getViewMatrix(), position);

            if(closestIndex >= 0 && closestIndex < landmarkTable.size()){
                Landmark l = landmarkTable.get(closestIndex);
                Log.d(TAG, l.title + "  " + l.description);
            }

            x = -1;
            y = -1;
        }
    }



    //////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      Drawing Helper Functions
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
    private void setupBillboards(){

        landmarkTable = new LandmarkTable();
        landmarkTable.loadCities();

        for(Landmark l : landmarkTable){
            Billboard bb = BillboardMaker.make(this, R.drawable.ara_icon, l.title, l.description);
            ScaleObject sbb = new ScaleObject(bb, 2, 1, 1);
            Entity entity = mScene.addDrawable(sbb);
            entity.setLatLonAlt(new float[]{l.latitude, l.longitude, l.altitude});
        }

        Log.d(TAG, "Location: " + VectorMath.vecToString(getLocation()));
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            x = event.getX();
            y = event.getY();
        }

        return true;
    }
}
