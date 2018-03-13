package edu.calstatela.jplone.arframework;

import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.SensorEvent;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import edu.calstatela.jplone.arframework.ARCamera.ARCameraView;
import edu.calstatela.jplone.arframework.ARGL.ARGLCamera;
import edu.calstatela.jplone.arframework.ARGL.Billboard.ARGLSizedBillboard;
import edu.calstatela.jplone.arframework.ARGL.Unit.ARGLRenderJob;
import edu.calstatela.jplone.arframework.ARGL.Utils.ARGLBillboardMaker;
import edu.calstatela.jplone.arframework.ARSensors.ARLocationSensor;
import edu.calstatela.jplone.arframework.ARSensors.ARMotionSensor;
import edu.calstatela.jplone.arframework.Utils.ARMath;

/**
 * Created by bill on 11/7/17.
 */

public class ARView extends FrameLayout {
    private static final String TAG = "ARView";
    private ARCameraView arCameraView;
    private GLSurfaceView glSurfaceView;

    // sensors
    private ARMotionSensor arMotionSensor;

    private TextView arTxtView;
    private Context arContext;
    private ARGLCamera glCamera;
    private boolean activated;
    private boolean initialized;
    private ArrayList<ARGLRenderJob> renderAddList;
    private ArrayList<ARGLRenderJob> renderDelList;
    private boolean hasGPS;
    private DirectGLRenderer renderer;

    public ARView(Context context, boolean hasGPS) {
        super(context);

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
    //      Asynchronoous Render List Handlers
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

        if(initialized) {
            Log.d(TAG, "reinitialized glSurface ");
            glSurfaceView.onResume();
        }

        if(activated) // don't activate twice
            return;

        Log.d(TAG, "AR functionalities added and activated");
        initialize();
        arMotionSensor.start();

        activated = true;
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

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      Direct Renderer Object
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    class DirectGLRenderer implements GLSurfaceView.Renderer {
        ArrayList<ARGLSizedBillboard> glSizedBillboards;
        ArrayList<ARGLRenderJob> glRenderJobs;
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
            int index = -1;

            for(int i=0; i<glRenderJobs.size(); i++) {
                ARGLRenderJob j = glRenderJobs.get(i);
                // do something?
                if(j.compare(job))
                    index = i;
            }

            if(index < 0)
                return;

            glRenderJobs.remove(index);
            glSizedBillboards.remove(index);
        }

        private void prepareAddList() {
            while(!renderAddList.isEmpty()) {
                ARGLRenderJob j = (ARGLRenderJob) renderAddList.get(0);
                renderAddList.remove(0);
                add(j);
            }
        }

        private void prepareDelList() {
            while(!renderDelList.isEmpty()) {
                ARGLRenderJob j = (ARGLRenderJob) renderDelList.get(0);
                renderDelList.remove(0);
                delete(j);
            }
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            GLES20.glClearColor(0, 0, 0, 0);

            glSizedBillboards = new ArrayList<ARGLSizedBillboard>();
            glRenderJobs = new ArrayList<ARGLRenderJob>();

            ARGLBillboardMaker.init(arContext);

            surfaceCreated = true;
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            float aspect_ratio = width * 1.0f / height;

            GLES20.glViewport(0, 0, width, height);
            glCamera.setPerspective(60.0f, aspect_ratio, 0.1f, 1000.0f);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

            glCamera.updateViewMatrix();

            float[] scratch = new float[16];

            // add what needs to be rendered
            prepareAddList();

            // delete what we don't need to render anymore
            prepareDelList();

            for(ARGLSizedBillboard billboard : glSizedBillboards) {
                Matrix.multiplyMM(scratch, 0, glCamera.getViewProjectionMatrix(), 0, billboard.getMatrix(), 0);
                billboard.draw(scratch);
            }
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
            portraitMatrixFromRotation(matrix, event.values);

            if(glCamera != null)
                glCamera.setByMatrix(matrix);
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
