package edu.calstatela.jplone.arframework.integrated;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.SensorEvent;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;
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

import edu.calstatela.jplone.arframework.graphics3d.camera.ARGLCamera;
import edu.calstatela.jplone.arframework.graphics3d.projection.Projection;
import edu.calstatela.jplone.arframework.ui.CameraView;
import edu.calstatela.jplone.arframework.landmark.Landmark;
import edu.calstatela.jplone.arframework.integrated.ARGLBillboard.ARGLSizedBillboard;
import edu.calstatela.jplone.arframework.integrated.Unit.ARGLPosition;
import edu.calstatela.jplone.arframework.integrated.Unit.ARGLRenderJob;
import edu.calstatela.jplone.arframework.integrated.ARGLBillboard.ARGLBillboardMaker;
import edu.calstatela.jplone.arframework.sensor.ARSensor;
import edu.calstatela.jplone.arframework.util.MatrixMath;
import edu.calstatela.jplone.arframework.util.VectorMath1;


public class ARView extends FrameLayout {
    private static final String TAG = "waka_ARView";
    private CameraView arCameraView;
    private GLSurfaceView glSurfaceView;

    // sensors
    private ARSensor arMotionSensor;
    private AREvent.Callback arCallback;

    private TextView arTxtView;
    private Context arContext;
    private ARGLCamera glCamera;
    private Projection projection;
    private boolean activated;
    private boolean initialized;
    private boolean renderAdding = false;
    private boolean renderDelete = false;
    private ArrayList<ARGLRenderJob> renderAddList;
    private ArrayList<ARGLRenderJob> deferredRenderAdd = new ArrayList<ARGLRenderJob>();
    private ArrayList<ARGLRenderJob> renderDelList;
    private ArrayList<ARGLRenderJob> deferredRenderDel = new ArrayList<ARGLRenderJob>();
    private boolean hasGPS;
    private DirectGLRenderer renderer;

    private int s_width = 0;
    private int s_height = 0;
    private ARRenderCallback renderCallback = null;

    private Activity parentActivity = null;

    public void setParentActivity(Activity activity){
        parentActivity = activity;
    }

    public ARView(Context context, boolean hasGPS) {
        super(context);

        arCallback = null;
        arContext = context;
        renderAddList = new ArrayList<ARGLRenderJob>();
        renderDelList = new ArrayList<ARGLRenderJob>();

        glCamera = new ARGLCamera();
        Log.d(TAG, "made new camera");

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

        arCameraView = new CameraView(arContext);
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

        arMotionSensor = new ARSensor(arContext, ARSensor.ROTATION_VECTOR);
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
        if(!renderAdding) // if the renderer is working on the add list, then defer add jobs until later
            renderAddList.add(job);
        else
            deferredRenderAdd.add(job);
    }

    public void removeJob(ARGLRenderJob job) {
        if(!renderDelete) // if the renderer is working on the delete list, then defer delete jobs until later
            renderDelList.add(job);
        else
            deferredRenderDel.add(job);
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
        Landmark last;
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
            renderAdding = true;

            for(ARGLRenderJob j : renderAddList)
                add(j);
            renderAddList.clear();

            renderAdding = false;

            if(deferredRenderAdd.size() > 0) {
                renderAddList = deferredRenderAdd;
                deferredRenderAdd = new ArrayList<ARGLRenderJob>();
                prepareAddList();
            }
        }

        private void prepareDelList() {
            renderDelete = true;

            for(ARGLRenderJob j : renderDelList)
                delete(j);
            renderDelList.clear();

            renderDelete = false;

            if(deferredRenderDel.size() > 0) {
                renderDelList = deferredRenderDel;
                deferredRenderDel = new ArrayList<ARGLRenderJob>();
                prepareDelList();
            }
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            GLES20.glClearColor(0, 0, 0, 0);
            projection = new Projection();
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
            projection.setPerspective(60.0f, aspect_ratio, 0.1f, 1000.0f);

            if(renderCallback != null)
                renderCallback.onGLResize(width, height);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

            if(currentOrientation != null) {
                glCamera.setOrientationVector(currentOrientation, 0);
            }


            // add what needs to be rendered
            prepareAddList();

            // delete what we don't need to render anymore
            prepareDelList();

            Landmark here = null;
            float[] vpm = new float[16];
            MatrixMath.multiply2Matrices(vpm, projection.getProjectionMatrix(), glCamera.getViewMatrix());
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
                here = new Landmark("", "", latLonAlt[0], latLonAlt[1], 100);

            for(ARGLSizedBillboard billboard : glSizedBillboards) {
                Landmark current = billboard.getLandmark();
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
                    float[] point = VectorMath1.convert3Dto2D(s_width, s_height, center, vpm);
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
                    billboard.draw((float[]) bbSet.get("matrix")); ///////////////////////////////////////////////////////////////////////
                    Log.d(TAG, "weird draw call");
                }
            }

            // call interaction with the chosen billboard
            if(touched_bb != null)
                touched_bb.interact();

            touching = false; // the touch event will only be processed once regardless of whether there was a match or not

            if(renderCallback != null)
                renderCallback.onGLDraw(projection.getProjectionMatrix(), glCamera.getViewMatrix());
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      Orientation Sensor Listener
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private float[] currentOrientation = null;

    ARSensor.Listener arMotionSensorListener = new ARSensor.Listener() {

        @Override
        public void onSensorEvent(SensorEvent event) {
            if(currentOrientation == null)
                currentOrientation = new float[3];

            currentOrientation[0] = event.values[0];
            currentOrientation[1] = event.values[1];
            currentOrientation[2] = event.values[2];



            if(arCallback != null) {
                double bearing = VectorMath1.compassBearing(event.values);
                if(latLonAlt != null)
                    arCallback.onAREvent(new AREvent(latLonAlt[0], latLonAlt[1], bearing));
                arCallback.onAREvent(new AREvent(event.values));
            }
        }

    };





    private float[] latLonAlt;

    public void updateLatLonAlt(float[] latLonAlt) {
        this.latLonAlt = latLonAlt.clone();
    }
}
