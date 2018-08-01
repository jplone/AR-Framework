package edu.calstatela.jplone.watertrekapp2.activities;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import edu.calstatela.jplone.arframework.graphics3d.camera.Camera3D;
import edu.calstatela.jplone.arframework.graphics3d.drawable.Billboard;
import edu.calstatela.jplone.arframework.graphics3d.drawable.BillboardMaker;
import edu.calstatela.jplone.arframework.graphics3d.entity.Entity;
import edu.calstatela.jplone.arframework.graphics3d.entity.ScaleObject;
import edu.calstatela.jplone.arframework.graphics3d.matrix.Projection;
import edu.calstatela.jplone.arframework.graphics3d.scene.TouchScene;
import edu.calstatela.jplone.arframework.ui.ARView;
import edu.calstatela.jplone.arframework.ui.SensorARView;
import edu.calstatela.jplone.arframework.util.GeoMath;

public class BillboardView1 extends SensorARView {
    private static final String TAG = "waka_MainARView";

    private Context mContext;

    private Camera3D mCamera;
    private Projection mProjection;
    private TouchScene mScene;

    private ArrayList<BillboardData> mAddDataList = new ArrayList<>();
    private ArrayList<Integer> mRemoveIdList = new ArrayList<>();
    private ArrayList<BillboardData> mDataList = new ArrayList<>();

    private TouchCallback mTouchCallback = null;




    public BillboardView1(Context context){
        super(context);
        mContext = context;
    }

    @Override
    public void GLInit() {
        super.GLInit();
        Billboard.init();

        mScene = new TouchScene();
        mScene.setScale(0.2f);

        mCamera = new Camera3D();
        mProjection = new Projection();

        GLES20.glClearColor(0, 0, 0, 0);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
    }



    @Override
    public void GLResize(int width, int height) {
        super.GLResize(width, height);
        GLES20.glViewport(0, 0, width, height);
        mProjection.setPerspective(60, (float)width / height, 0.1f, 10000000000f);
    }


    @Override
    public void GLDraw() {
        super.GLDraw();

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);


        /* setup wells that are in well list */
        if(mScene.isEmpty() && getLocation() != null) {
            reprocessDataList();
        }

        /* setup wells that are in add list */
        if(getLocation() != null) {
            processAddList();
            processRemoveList();
        }

        if(getOrientation() != null)
            mCamera.setOrientationQuaternion(getOrientation(), 0);

        if(getLocation() != null) {
            mCamera.setPositionLatLonAlt(getLocation());
            mScene.setCenterLatLonAlt(getLocation());
            mScene.update();
//            Log.d(TAG, "location: " + VectorMath.vecToString(getLocation()));
        }

        mScene.draw(mProjection.getProjectionMatrix(), mCamera.getViewMatrix());

    }



   private void processAddList(){
        for(BillboardData data : mAddDataList){
            mDataList.add(data);
            makeBillboardFromData(data);
        }
        mAddDataList.clear();
   }

   private void makeBillboardFromData(BillboardData data){
        Billboard bb = BillboardMaker.make(mContext, data.iconId, data.title, data.text);
        ScaleObject sbb = new ScaleObject(bb, 2, 1, 1);
        Entity e = mScene.addDrawable(sbb);
        e.setLatLonAlt(new float[]{data.lat, data.lon, data.alt});
        mScene.addId(data.id);
   }

   private void reprocessDataList(){
        for(BillboardData data : mDataList){
            makeBillboardFromData(data);
        }
   }




    public void addBillboard(int id, int iconId, String title, String text, float lat, float lon, float alt){
        BillboardData data = new BillboardData(id, iconId, title, text, lat, lon, alt);
        mAddDataList.add(data);
        Log.d(TAG, "Added Billboard " + data.id + "    " + title);
    }

    public void removeBillboard(int id){
        mRemoveIdList.add(id);
    }

    private void processRemoveList(){
        for(Integer id : mRemoveIdList){
            int removeIndex = mScene.remove(id);
            mDataList.remove(removeIndex);
        }
        mRemoveIdList.clear();
    }


    class BillboardData{
        BillboardData(int id, int iconId, String title, String text, float lat, float lon, float alt){
            this.id = id; this.iconId = iconId; this.title = title; this.text = text;
            this.lat = lat; this.lon = lon; this.alt = alt;
        }
        public int id;
        public int iconId;
        public String title;
        public String text;
        public float lon;
        public float lat;
        public float alt;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction() != MotionEvent.ACTION_DOWN)
            return false;

        if(mScene == null)
            return false;


        float[] xyzLocation = new float[3];
        GeoMath.latLonAltToXYZ(getLocation(), xyzLocation);

        int id = mScene.findClosestEntity(event.getX(), event.getY(), v.getWidth(), v.getHeight(),
                0.2f, mProjection.getProjectionMatrix(), mCamera.getViewMatrix(), xyzLocation);



        if(id >= 0 && mTouchCallback != null){
            mTouchCallback.onTouch(id);
        }

        return true;
    }

    public interface TouchCallback{
        void onTouch(int id);
    }

    public void setTouchCallback(TouchCallback callback){
        mTouchCallback = callback;
    }
}
