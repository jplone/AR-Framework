package edu.calstatela.jplone.arframework;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.Matrix;
import android.renderscript.RenderScript;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import edu.calstatela.jplone.arframework.ARCamera.ARCameraView;
import edu.calstatela.jplone.arframework.ARData.ARLandmark;
import edu.calstatela.jplone.arframework.ARGL.ARGLCamera;
import edu.calstatela.jplone.arframework.ARGL.Billboard.ARGLSizedBillboard;
import edu.calstatela.jplone.arframework.ARGL.Unit.ARGLPosition;
import edu.calstatela.jplone.arframework.ARGL.Unit.ARGLRenderJob;
import edu.calstatela.jplone.arframework.ARGL.Utils.ARGLBillboardMaker;
import edu.calstatela.jplone.arframework.ARSensors.ARLocationSensor;
import edu.calstatela.jplone.arframework.ARSensors.ARMotionSensor;
import edu.calstatela.jplone.arframework.Utils.AREvent;
import edu.calstatela.jplone.arframework.Utils.ARMath;
import edu.calstatela.jplone.arframework.Utils.ARRenderCallback;

/**
 * Created by bill on 11/7/17.
 */

public class ARView extends FrameLayout {
    private static final String TAG = "ARView";
    private ARCameraView arCameraView;
    private GLSurfaceView glSurfaceView;

    // sensors
    private ARMotionSensor arMotionSensor;
    private AREvent.Callback arCallback;

    private TextView arTxtView;
    private Context arContext;
    private ARGLCamera glCamera;
    private boolean activated;
    private boolean initialized;
    private ArrayList<ARGLRenderJob> renderAddList;
    private ArrayList<ARGLRenderJob> renderDelList;
    private boolean hasGPS;
    private DirectGLRenderer renderer;

    private int s_width = 0;
    private int s_height = 0;
    private ARRenderCallback renderCallback = null;

    public ARView(Context context, boolean hasGPS) {
        super(context);

        arCallback = null;
        arContext = context;
        renderAddList = new ArrayList<ARGLRenderJob>();
        renderDelList = new ArrayList<ARGLRenderJob>();

        glCamera = new ARGLCamera();

        arTxtView = new TextView(arContext);
        arTxtView.setText("It does not look like you have permissions enabled for our framework. Please make sure to enable permissions!");

        addView(arTxtView);

        activated = false;
        initialized = false;
        this.hasGPS = hasGPS;
    }

    public void initialize() {
        if(initialized)
            return;
        this.removeAllViews(); // kill all subviews

        arCameraView = new ARCameraView(arContext);
        addView(arCameraView);

        renderer = new DirectGLRenderer();

        glSurfaceView = new GLSurfaceView(arContext);
        glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        glSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setRenderer(renderer);
        glSurfaceView.setDebugFlags(GLSurfaceView.DEBUG_LOG_GL_CALLS);
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        //glSurfaceView.setPreserveEGLContextOnPause(true);
        addView(glSurfaceView);

        arMotionSensor = new ARMotionSensor(arContext, ARMotionSensor.ROTATION_VECTOR);
        arMotionSensor.addListener(arMotionSensorListener);

        glSurfaceView.setZOrderMediaOverlay(true);

        initialized = true;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      Measurement Handlers
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // this gets the exact screen dimensions used by the view to do coordinate conversions later
        s_width = MeasureSpec.getSize(widthMeasureSpec);
        s_height = MeasureSpec.getSize(heightMeasureSpec);

        //Log.d(TAG, "screen [" + s_width + " x " + s_height + "]");

        setMeasuredDimension(s_width, s_height);

        for(int i=0; i<this.getChildCount(); i++) {
            View v = this.getChildAt(i);
            v.measure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      Touch Event Handlers
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    boolean touching = false;
    float touch_x;
    float touch_y;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event != null) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                touching = true; // set the flag
                touch_x = event.getX();
                touch_y = event.getY();
                //Log.d(TAG, "touched x=" + touch_x + " y=" + touch_y);
            }
        }

        return super.onTouchEvent(event);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      Asynchronous Render List Handlers
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void mirrorRenderAddList(ArrayList<ARGLRenderJob> list) {
        renderAddList = (ArrayList<ARGLRenderJob>) list.clone();
        Log.d(TAG, "copied " + renderAddList.size() + " objects into renderAddList");
    }

    public void mirrorRenderDelList(ArrayList<ARGLRenderJob> list) {
        renderDelList = (ArrayList<ARGLRenderJob>) list.clone();
        Log.d(TAG, "copied " + renderDelList.size() + " objects into renderDelList");
    }

    public void addJob(ARGLRenderJob job) {
        renderAddList.add(job);
    }

    public void removeJob(ARGLRenderJob job) {
        renderDelList.add(job);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      Operation Handlers
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean isActive() {
        return activated;
    }

    public void start(Context context) {
        arContext = context;

        if(activated) // don't activate twice
            return;

        if(initialized) {
            Log.d(TAG, "reinitialized glSurface ");
            glSurfaceView.onResume();
        }
        else initialize();

        arMotionSensor.start();

        activated = true;
        Log.d(TAG, "AR functionalities added and activated");
    }

    public void stop() {
        // should do something while stopping
        if(activated) {
            arMotionSensor.stop();
            ARGLBillboardMaker.destroy();
            glSurfaceView.onPause();
            activated = false;
        }
    }

    public void setCallback(AREvent.Callback arCallback) {
        this.arCallback = arCallback;
    }
    public void setRenderCallback(ARRenderCallback renderCallback) {
        Log.d(TAG, "setting callback for render!");
        this.renderCallback = renderCallback;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      Direct Renderer Object
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    class DirectGLRenderer implements GLSurfaceView.Renderer {
        ArrayList<ARGLSizedBillboard> glSizedBillboards;
        ArrayList<ARGLRenderJob> glRenderJobs = new ArrayList<ARGLRenderJob>();
        ARLandmark last;
        boolean surfaceCreated = false;

        public boolean started() {
            return surfaceCreated;
        }

        private void add(ARGLRenderJob job) {
            glRenderJobs.add(job);

            if(hasGPS)
                glSizedBillboards.add((ARGLSizedBillboard) job.execute(latLonAlt));
            else
                glSizedBillboards.add((ARGLSizedBillboard) job.execute());
        }

        private void delete(ARGLRenderJob job) {
            int index = glRenderJobs.indexOf(job);
            if(index < 0) return;
            glSizedBillboards.remove(index);
            glRenderJobs.remove(index);
        }

        private void prepareAddList() {
            while(!renderAddList.isEmpty()) {
                ARGLRenderJob j = (ARGLRenderJob) renderAddList.get(0);
                renderAddList.remove(0);
                add(j);
            }
        }

        private void prepareDelList() {
            for(ARGLRenderJob j : renderDelList)
                delete(j);

            renderDelList.clear();
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            GLES20.glClearColor(0, 0, 0, 0);

            glSizedBillboards = new ArrayList<ARGLSizedBillboard>();

            ARGLBillboardMaker.init(arContext);

            // recreate the billboards
            for(ARGLRenderJob job : glRenderJobs) {
                if(hasGPS)
                    glSizedBillboards.add((ARGLSizedBillboard) job.execute(latLonAlt));
                else
                    glSizedBillboards.add((ARGLSizedBillboard) job.execute());
            }

            surfaceCreated = true;

            if(renderCallback != null)
                renderCallback.onGLInit();
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            float aspect_ratio = width * 1.0f / height;

            GLES20.glViewport(0, 0, width, height);
            glCamera.setPerspective(60.0f, aspect_ratio, 0.1f, 1000.0f);

            if(renderCallback != null)
                renderCallback.onGLResize(width, height);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

            glCamera.updateViewMatrix();

            // add what needs to be rendered
            prepareAddList();

            // delete what we don't need to render anymore
            prepareDelList();

            ARLandmark here = null;
            float[] vpm = glCamera.getViewProjectionMatrix();
            int min_dim = Math.min(s_width, s_height);
            ARGLSizedBillboard touched_bb = null;
            float touched_dist = 10000.0f;

            PriorityQueue< HashMap<String, Object> > renderQ = null;
            if(glSizedBillboards != null && glSizedBillboards.size() > 0)
                renderQ = new PriorityQueue< HashMap<String, Object> >(glSizedBillboards.size(), new Comparator<HashMap<String, Object>>() {
                    @Override
                    public int compare(HashMap<String, Object> bbSet0, HashMap<String, Object> bbSet1) {
                        float dist0 = (Float) bbSet0.get("distance");
                        float dist1 = (Float) bbSet1.get("distance");

                        if(dist0 < dist1)
                            return -1;
                        else if(dist0 > dist1)
                            return 1;
                        return 0;
                    }
                });

            if(latLonAlt != null)
                here = new ARLandmark("", "", latLonAlt[0], latLonAlt[1], 100);

            for(ARGLSizedBillboard billboard : glSizedBillboards) {
                ARLandmark current = billboard.getLandmark();
                ARGLPosition position = billboard.getPosition();
                float distance = 0.0f;

                // update billboard positions based on GPS location (if there are any changes)
                if(current != null && here != null) {
                    distance = here.distance(current);
                    float angle = here.compassDirection(current);

                    position = new ARGLPosition(0, 0, -10 - distance * 0.00001f, -angle, 0, 1, 0);
                    billboard.setPosition(position);
                }

                // handle touch events
                if(touching) {
                    float[] center = position.getCenter();
                    float[] point = ARMath.convert3Dto2D(s_width, s_height, center, vpm);
                    float dist = (float) Math.sqrt(Math.pow(point[0] - touch_x, 2) + Math.pow(point[1] - touch_y, 2));

                    float[] world_center = new float[4];
                    Matrix.multiplyMV(world_center, 0, vpm, 0, center, 0);

                    // range of the touch is about 17% of the shortest screen dimension
                    // also we are guarding against touches that happen to an object that's behind you!
                    if(dist / min_dim <= 0.17 && world_center[2] >= 0) {
                        if(current == null)
                            distance = dist;

                        // if we are touching multiple objects, prioritize the closest one
                        // todo: differentiate between physical distance and touch distance!
                        if(touched_bb == null || distance < touched_dist) {
                            touched_bb = billboard;
                            touched_dist = dist;
                        }
                    }
                }

                // render the billboard
                float[] mvp = new float[16];
                Matrix.multiplyMM(mvp, 0, vpm, 0, billboard.getMatrix(), 0);

                HashMap<String, Object> bbSet = new HashMap<String, Object>();
                bbSet.put("distance", 0.0f);
                bbSet.put("billboard", billboard);
                bbSet.put("matrix", mvp);

                renderQ.add(bbSet);
            }

            if(renderQ != null) {
                while (!renderQ.isEmpty()) {
                    HashMap<String, Object> bbSet = renderQ.remove();
                    ARGLSizedBillboard billboard = (ARGLSizedBillboard) bbSet.get("billboard");
                    billboard.draw((float[]) bbSet.get("matrix"));
                }
            }

            // call interaction with the chosen billboard
            if(touched_bb != null)
                touched_bb.interact();

            touching = false; // the touch event will only be processed once regardless of whether there was a match or not

            if(renderCallback != null)
                renderCallback.onGLDraw(glCamera.getProjectionMatrix(), glCamera.getViewMatrix());
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      Orientation Sensor Listener
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////

    ARMotionSensor.Listener arMotionSensorListener = new ARMotionSensor.Listener() {
        @Override
        public void onSensorEvent(SensorEvent event) {
            float[] matrix = new float[16];
            //SensorManager.getRotationMatrixFromVector(matrix, event.values);
            portraitMatrixFromRotation(matrix, event.values);

            if(glCamera != null)
                glCamera.setByMatrix(matrix);

            if(arCallback != null) {
                double bearing = ARMath.compassBearing(event.values);
                if(latLonAlt != null)
                    arCallback.onAREvent(new AREvent(latLonAlt[0], latLonAlt[1], bearing));
                arCallback.onAREvent(new AREvent(event.values));
            }
        }

        private void portraitMatrixFromRotation(float[] matrix, float[] rotation){
            float[] rVec = {rotation[0], rotation[1], rotation[2]};
            float magnitude = ARMath.magnitude(rVec);
            rVec[0] /= magnitude;
            rVec[1] /= magnitude;
            rVec[2] /= magnitude;
            float angle = ARMath.radToDegrees(2 * (float)Math.asin(magnitude));

            Matrix.setRotateM(matrix, 0, angle, rVec[0], rVec[1], rVec[2]);

            float[] adjustMatrix = new float[16];
            Matrix.setRotateM(adjustMatrix, 0, 90, -1, 0, 0);
            Matrix.multiplyMM(matrix, 0, adjustMatrix, 0, matrix, 0);
        }

        private void landscapeMatrixFromRotation(float[] matrix, float[] rotation){
            float[] rVec = {rotation[0], rotation[1], rotation[2]};
            float magnitude = ARMath.magnitude(rVec);
            rVec[0] /= magnitude;
            rVec[1] /= magnitude;
            rVec[2] /= magnitude;
            float angle = ARMath.radToDegrees(2 * (float)Math.asin(magnitude));

            Matrix.setRotateM(matrix, 0, angle, rVec[0], rVec[1], rVec[2]);

            float[] adjustMatrix = new float[16];
            Matrix.setRotateM(adjustMatrix, 0, 90, 0, 0, 1);
            Matrix.rotateM(adjustMatrix, 0, 90, -1, 0, 0);
            Matrix.multiplyMM(matrix, 0, adjustMatrix, 0, matrix, 0);
        }
    };

    private float[] latLonAlt;

    public void updateLatLonAlt(float[] latLonAlt) {
        this.latLonAlt = latLonAlt.clone();
    }
}
