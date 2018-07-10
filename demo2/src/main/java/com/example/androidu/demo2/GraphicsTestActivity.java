package com.example.androidu.demo2;

import android.opengl.GLES20;

import edu.calstatela.jplone.arframework.graphics3d.camera.ARGLCamera;
import edu.calstatela.jplone.arframework.graphics3d.drawable.Billboard;
import edu.calstatela.jplone.arframework.graphics3d.drawable.BillboardMaker;
import edu.calstatela.jplone.arframework.graphics3d.drawable.LitModel;
import edu.calstatela.jplone.arframework.graphics3d.entity.Entity;
import edu.calstatela.jplone.arframework.graphics3d.helper.MeshHelper;
import edu.calstatela.jplone.arframework.graphics3d.projection.Projection;
import edu.calstatela.jplone.arframework.graphics3d.scene.Scene;
import edu.calstatela.jplone.arframework.ui.SensorARActivity;
import edu.calstatela.jplone.arframework.util.Orientation;

public class GraphicsTestActivity extends SensorARActivity {
    private static final String TAG = "waka_graphics_test";


    private ARGLCamera mCamera;
    private Projection mProjection;
    private Scene mScene;
    private Scene mCompassScene;
    Entity mCube;


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      GL Callbacks
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void GLInit() {
        super.GLInit();
        Billboard.init();

        GLES20.glClearColor(0, 0, 0, 0);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        mScene = new Scene();
        mCompassScene = new Scene();

        //setupScene();
        setupCompassScene();

        mCamera = new ARGLCamera();
        mCamera.move(0, 0, -3);
        mProjection = new Projection();
    }

    private void setupScene(){
        LitModel cubeModel = new LitModel();
        cubeModel.loadVertices(MeshHelper.cube());
        cubeModel.loadNormals(MeshHelper.calculateNormals(MeshHelper.cube()));

        mCube = mScene.addDrawable(cubeModel);
        mCube.yaw(0); mCube.move(0, 0, -5);
    }

    private void setupCompassScene(){
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

        if(getOrientation() != null)
            mCamera.setOrientationVector(getOrientation(), Orientation.getOrientationAngle(this));

//        mCube.yaw(1);


//        mCamera.rotate(1, 0, 1, 0);

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        mScene.draw(mProjection.getProjectionMatrix(), mCamera.getViewMatrix());
        mCompassScene.draw(mProjection.getProjectionMatrix(), mCamera.getViewMatrix());
    }

}
