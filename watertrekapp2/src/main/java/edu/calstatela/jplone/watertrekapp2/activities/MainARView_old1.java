package edu.calstatela.jplone.watertrekapp2.activities;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import edu.calstatela.jplone.arframework.graphics3d.camera.ARGLCamera;
import edu.calstatela.jplone.arframework.graphics3d.drawable.Billboard;
import edu.calstatela.jplone.arframework.graphics3d.drawable.BillboardMaker;
import edu.calstatela.jplone.arframework.graphics3d.entity.Entity;
import edu.calstatela.jplone.arframework.graphics3d.entity.ModelMatrix2;
import edu.calstatela.jplone.arframework.graphics3d.projection.Projection;
import edu.calstatela.jplone.arframework.graphics3d.scene.Scene;
import edu.calstatela.jplone.arframework.ui.ARView;
import edu.calstatela.jplone.arframework.util.GeoMath;
import edu.calstatela.jplone.watertrekapp2.R;

public class MainARView_old1 extends ARView {
    private static final String TAG = "waka_MainARView";

    private Context context;

    private ARGLCamera mCamera;
    private Projection mProjection;
    private Billboard bb;
    private ModelMatrix2 mModelMatrix;

    private float startX = 4354, startY = -345345, startZ = -3543;


    public MainARView_old1(Context context){
        super(context);
        this.context = context;
    }

    @Override
    public void GLInit() {
        super.GLInit();
        Billboard.init();

        GLES20.glClearColor(0, 0, 0, 0);


        bb = BillboardMaker.make(context, R.drawable.ara_icon, "BILLBOARD", "billboard");

        mModelMatrix = new ModelMatrix2();
        mModelMatrix.setPosition(startX, startY, startZ);


        mCamera = new ARGLCamera();
        mCamera.setPosition(startX, startY, startZ);
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

    private void loadCompass(Scene scene, float[] location){
        Billboard north = BillboardMaker.make(context, R.drawable.ara_icon, "North", "A compass direction");
        Billboard east = BillboardMaker.make(context, R.drawable.ara_icon, "East", "A compass direction");
        Billboard south = BillboardMaker.make(context, R.drawable.ara_icon, "South", "A compass direction");
        Billboard west = BillboardMaker.make(context, R.drawable.ara_icon, "West", "A compass direction");

        Entity northBB = scene.addDrawable(north);
        Entity eastBB = scene.addDrawable(east);
        Entity southBB = scene.addDrawable(south);
        Entity westBB = scene.addDrawable(west);

        float[] xyz = new float[3];
        GeoMath.latLonAltToXYZ(location, xyz);

        northBB.yaw(0); northBB.move(xyz[0], xyz[1], xyz[2] - 5); northBB.setScale(2, 1, 1);
        eastBB.yaw(-90); eastBB.move(xyz[0] + 5, xyz[1], xyz[2]); eastBB.setScale(2, 1, 1);
        southBB.yaw(180); southBB.move(xyz[0], xyz[1], xyz[2] + 5); southBB.setScale(2, 1, 1);
        westBB.yaw(90); westBB.move(xyz[0] - 5, xyz[1], xyz[2]); westBB.setScale(2, 1, 1);

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

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        if(getOrientation() != null)
            mCamera.setOrientationVector(getOrientation(), 0);

        bb.draw(mProjection.getProjectionMatrix(), mCamera.getViewMatrix(), mModelMatrix.getModelMatrix());
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction() != MotionEvent.ACTION_DOWN)
            return false;

        int width = this.getWidth();
        int height = this.getHeight();
        int touchX = (int)event.getX();
        int touchY = (int)event.getY();

        if(touchY < height / 4){
            if(touchX < width / 2){
                mModelMatrix.slide(0, -1, 0);
            }
            else{
                mModelMatrix.slide(0, 1, 0);
            }
            Log.d(TAG, "slide z");
        }
        else if (touchY < height / 2){
            if(touchX < width / 2){
                mModelMatrix.pitch(-15);
            }
            else{
                mModelMatrix.pitch(15);
            }
            Log.d(TAG, "pitch");
        }
        else if (touchY < 3 * height / 4){
            if(touchX < width / 2){
                mModelMatrix.yaw(-15);
            }
            else{
                mModelMatrix.yaw(15);
            }
            Log.d(TAG, "yaw");
        }
        else{
            if(touchX < width / 2){
                mModelMatrix.roll(-15);
            }
            else{
                mModelMatrix.roll(15);
            }
            Log.d(TAG, "roll");
        }


        return true;
    }
}
