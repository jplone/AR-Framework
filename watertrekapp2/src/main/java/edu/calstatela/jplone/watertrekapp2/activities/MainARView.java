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
import edu.calstatela.jplone.arframework.graphics3d.projection.Projection;
import edu.calstatela.jplone.arframework.graphics3d.scene.Scene;
import edu.calstatela.jplone.arframework.ui.ARView;
import edu.calstatela.jplone.arframework.util.GeoMath;
import edu.calstatela.jplone.watertrekapp2.R;

public class MainARView extends ARView {
    private static final String TAG = "waka_MainARView";

    private Context context;

    private ARGLCamera mCamera;
    private Projection mProjection;
    private Entity mEntity;

    private float startX = 234, startY = -213, startZ = -21312;
    private float x = 0, y = 0, z = 0, yaw;
    private float scale = 0.2f;


    public MainARView(Context context){
        super(context);
        this.context = context;
    }

    @Override
    public void GLInit() {
        super.GLInit();

        GLES20.glClearColor(0, 0, 0, 0);


        Billboard bb = BillboardMaker.make(context, R.drawable.ara_icon, "BILLBOARD", "billboard");
        mEntity = new Entity();
        mEntity.setDrawable(bb);
//        mEntity.setLookAt(-5, -2, 2, 0, 0, 0, 0, 1, 0);
//        mEntity.setPosition(x, y, z);

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


//        mEntity.setPosition(x, y, z);
//        mEntity.setYaw(yaw);
        mEntity.setLookAtWithConstantDistanceScale(startX + x, startY + y, startZ + z, startX, startY, startZ, 0, 1, 0, scale);

        mEntity.draw(mProjection.getProjectionMatrix(), mCamera.getViewMatrix(), mEntity.getModelMatrix());
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
//                mEntity.slide(-1, 0, 0);
                x -= 1;
            }
            else{
//                mEntity.slide(1, 0, 0);
                x += 1;
            }
            Log.d(TAG, "new x is " + x);
        }
        else if (touchY < height / 2){
            if(touchX < width / 2){
//                mEntity.slide(0, -1, 0);
                y -= 1;
            }
            else{
//                mEntity.slide(0, 1, 0);
                y += 1;
            }
            Log.d(TAG, "new y is " + y);
        }
        else if (touchY < 3 * height / 4){
            if(touchX < width / 2){
//                mEntity.slide(0, 0, -1);
                z -= 1;
            }
            else{
//                mEntity.slide(0, 0, 1);
                z += 1;
            }
            Log.d(TAG, "new z is " + z);
        }
        else{
            if(touchX < width / 2){
//                mEntity.yaw(-15);
//                yaw -= 15;
                scale -= 0.1f;
            }
            else{
//                mEntity.yaw(15);
//                yaw += 15;
                scale += 0.1f;
            }
            Log.d(TAG, "new scale is " + scale);
        }


        return true;
    }
}
