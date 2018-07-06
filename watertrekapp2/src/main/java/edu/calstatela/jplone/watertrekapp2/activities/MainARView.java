package edu.calstatela.jplone.watertrekapp2.activities;

import android.content.Context;
import android.opengl.GLES20;

import edu.calstatela.jplone.arframework.graphics3d.camera.ARGLCamera;
import edu.calstatela.jplone.arframework.graphics3d.drawable.Billboard;
import edu.calstatela.jplone.arframework.graphics3d.drawable.BillboardMaker;
import edu.calstatela.jplone.arframework.graphics3d.entity.Entity;
import edu.calstatela.jplone.arframework.graphics3d.projection.Projection;
import edu.calstatela.jplone.arframework.graphics3d.scene.Scene;
import edu.calstatela.jplone.arframework.ui.ARView;
import edu.calstatela.jplone.arframework.util.Orientation;
import edu.calstatela.jplone.watertrekapp2.R;

public class MainARView extends ARView {

    Context context;

    ARGLCamera mCamera;
    Projection mProjection;
    Scene mScene;

    public MainARView(Context context){
        super(context);
        this.context = context;
    }

    @Override
    public void GLInit() {
        super.GLInit();

        GLES20.glClearColor(0, 0, 0, 0);

        mScene = new Scene();
        loadCompass(mScene);

        mCamera = new ARGLCamera();
        mProjection = new Projection();
    }

    private void loadCompass(Scene scene){
        Billboard north = BillboardMaker.make(context, R.drawable.ara_icon, "North", "A compass direction");
        Billboard east = BillboardMaker.make(context, R.drawable.ara_icon, "East", "A compass direction");
        Billboard south = BillboardMaker.make(context, R.drawable.ara_icon, "South", "A compass direction");
        Billboard west = BillboardMaker.make(context, R.drawable.ara_icon, "West", "A compass direction");

        Entity northBB = scene.addDrawable(north);
        Entity eastBB = scene.addDrawable(east);
        Entity southBB = scene.addDrawable(south);
        Entity westBB = scene.addDrawable(west);

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
            mCamera.setOrientationVector(getOrientation(), 0);

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        mScene.draw(mProjection.getProjectionMatrix(), mCamera.getViewMatrix());
    }


}
