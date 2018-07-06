    package com.example.androidu.demo2;


    import android.hardware.SensorEvent;
    import android.location.Location;
    import android.opengl.GLES20;
    import android.os.Bundle;
    import android.util.Log;

    import edu.calstatela.jplone.arframework.graphics3d.camera.ARGLCamera;
    import edu.calstatela.jplone.arframework.graphics3d.drawable.Billboard;
    import edu.calstatela.jplone.arframework.graphics3d.drawable.BillboardMaker;
    import edu.calstatela.jplone.arframework.graphics3d.drawable.LitModel;
    import edu.calstatela.jplone.arframework.graphics3d.entity.Entity;
    import edu.calstatela.jplone.arframework.graphics3d.helper.MeshHelper;
    import edu.calstatela.jplone.arframework.graphics3d.projection.Projection;
    import edu.calstatela.jplone.arframework.graphics3d.scene.Scene;
    import edu.calstatela.jplone.arframework.landmark.MountainData;
    import edu.calstatela.jplone.arframework.sensor.ARGps;
    import edu.calstatela.jplone.arframework.sensor.ARSensor;
    import edu.calstatela.jplone.arframework.ui.ARActivity;
    import edu.calstatela.jplone.arframework.util.GeoMath;
    import edu.calstatela.jplone.arframework.util.Orientation;


    public class MountainDrawActivity extends ARActivity {

        private static final String TAG = "waka-shapes";

        private ARGps location;
        private float[] currentLocation = null;
        private ARSensor orientation;
        private float[] currentOrientation = null;

        private Projection projection;
        private ARGLCamera camera;
        private Scene scene;

        private float scalingFactor = 2;
        private float angle = 0;

        private Entity bb1, bb2, bb3, bb4, bb5;



        ////////////////////////////////////////////////////////////////////////////////////////////////
        //
        //      Activity Callbacks
        //
        ////////////////////////////////////////////////////////////////////////////////////////////////

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            location = new ARGps(this);
            location.addListener(locationListener);
            orientation = new ARSensor(this, ARSensor.ROTATION_VECTOR);
            orientation.addListener(orientationListener);
        }

        @Override
        protected void onPause() {
            super.onPause();
            location.stop();
            orientation.stop();
        }

        @Override
        protected void onResume() {
            super.onResume();
            location.start();
            orientation.start();
        }

        ////////////////////////////////////////////////////////////////////////////////////////////////
        //
        //      OpenGL Callbacks
        //
        ////////////////////////////////////////////////////////////////////////////////////////////////

        @Override
        public void GLInit() {
            super.GLInit();

            Log.d(TAG, "....... init .........");

            scene = null;
            currentLocation = null;
            currentOrientation = null;

            bb1 = bb2 = bb3 = bb4 = bb5 = null;

            projection = new Projection();
            camera = new ARGLCamera();
            camera.setPosition(0, 0, 0);
            scene = new Scene();






            GLES20.glClearColor(0, 0, 0, 0);
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        }

        @Override
        public void GLResize(int width, int height) {
            super.GLResize(width, height);

            projection.setPerspective(60, (float)width / height, 0.01f, 100000000f);
            GLES20.glViewport(0, 0, width, height);
        }

        @Override
        public void GLDraw() {
            super.GLDraw();

//            Log.d(TAG, "..... draw .....");
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

            /* Do camera stuff */
            if(currentOrientation != null && currentLocation != null) {
                camera.setOrientationVector(currentOrientation, 0);
                camera.setPositionLatLonAlt(currentLocation);
            }


            /* Update Entities/Scenes */
            if(bb1 == null && currentLocation != null){
                setup();
            }
            if(bb1 != null) {
                bb1.yaw(1);
                bb2.yaw(1);
                bb3.yaw(1);
                bb4.yaw(1);
                bb5.yaw(1);
            }

            /* Draw */
            scene.draw(projection.getProjectionMatrix(), camera.getViewMatrix());


            Log.d(TAG, "Orientation: " + Orientation.getOrientationAngle(this));
        }


        private void setup(){
            Entity e;
            Billboard b;
            float[] mountain;

            LitModel pyramidModel = new LitModel();
            pyramidModel.loadVertices(MeshHelper.pyramid());
            pyramidModel.loadNormals(MeshHelper.calculateNormals(MeshHelper.pyramid()));

            mountain = MountainData.mtWilson;
            e = scene.addDrawable(pyramidModel);
            e.setLatLonAlt(new float[]{mountain[0], mountain[1], 0});
            e.setScale(scalingFactor * mountain[2] / 2, scalingFactor * mountain[2], scalingFactor * mountain[2] / 2);
            b = BillboardMaker.make2(this, R.drawable.mountain_icon, "Mt Wilson", "elevation: " + (int)mountain[2]);
            bb1 = scene.addDrawable(b);
            bb1.setLatLonAlt(new float[]{mountain[0], mountain[1], mountain[2]*3});
            bb1.setScale(2000, 2000, 2000);

            mountain = MountainData.mtLukens;
            e = scene.addDrawable(pyramidModel);
            e.setLatLonAlt(new float[]{mountain[0], mountain[1], 0});
            e.setScale(scalingFactor * mountain[2] / 2, scalingFactor * mountain[2], scalingFactor * mountain[2] / 2);
            b = BillboardMaker.make2(this, R.drawable.mountain_icon, "Mt Lukens", "elevation: " + (int)mountain[2]);
            bb2 = scene.addDrawable(b);
            bb2.setLatLonAlt(new float[]{mountain[0], mountain[1], mountain[2]*3});
            bb2.setScale(2000, 2000, 2000);

            mountain = MountainData.sanGabrielPeak;
            e = scene.addDrawable(pyramidModel);
            e.setLatLonAlt(new float[]{mountain[0], mountain[1], 0});
            e.setScale(scalingFactor * mountain[2] / 2, scalingFactor * mountain[2], scalingFactor * mountain[2] / 2);
            b = BillboardMaker.make2(this, R.drawable.mountain_icon, "San Gabriel Peak", "elevation: " + (int)mountain[2]);
            bb3 = scene.addDrawable(b);
            bb3.setLatLonAlt(new float[]{mountain[0], mountain[1], mountain[2]*3});
            bb3.setScale(2000, 2000, 2000);


            mountain = MountainData.brownMountain;
            e = scene.addDrawable(pyramidModel);
            e.setLatLonAlt(new float[]{mountain[0], mountain[1], 0});
            e.setScale(scalingFactor * mountain[2] / 2, scalingFactor * mountain[2], scalingFactor * mountain[2] / 2);
            b = BillboardMaker.make2(this, R.drawable.mountain_icon, "Brown Mountain", "elevation: " + (int)mountain[2]);
            bb4 = scene.addDrawable(b);
            bb4.setLatLonAlt(new float[]{mountain[0], mountain[1], mountain[2]*3});
            bb4.setScale(2000, 2000, 2000);

            mountain = MountainData.hoytMountain;
            e = scene.addDrawable(pyramidModel);
            e.setLatLonAlt(new float[]{mountain[0], mountain[1], 0});
            e.setScale(scalingFactor * mountain[2] / 2, scalingFactor * mountain[2], scalingFactor * mountain[2] / 2);
            b = BillboardMaker.make2(this, R.drawable.mountain_icon, "Hoyt Mountain", "elevation: " + (int)mountain[2]);
            bb5 = scene.addDrawable(b);
            bb5.setLatLonAlt(new float[]{mountain[0], mountain[1], mountain[2]*3});
            bb5.setScale(2000, 2000, 2000);
        }

        ////////////////////////////////////////////////////////////////////////////////////////////////
        //
        //      Sensor Callbacks
        //
        ////////////////////////////////////////////////////////////////////////////////////////////////

        private ARGps.Listener locationListener = new ARGps.Listener(){
            @Override
            public void handleLocation(Location location){
                if(currentLocation == null){
                    currentLocation = new float[3];
                    currentLocation[0] = (float)location.getLatitude();
                    currentLocation[1] = (float)location.getLongitude();
                    currentLocation[2] = (float)location.getAltitude();
                    GeoMath.setReference(currentLocation);
                    return;
                }

                currentLocation[0] = (float)location.getLatitude();
                currentLocation[1] = (float)location.getLongitude();
                currentLocation[2] = (float)location.getAltitude();
            }
        };

        private ARSensor.Listener orientationListener = new ARSensor.Listener(){
            @Override
            public void onSensorEvent(SensorEvent event){
                if(currentOrientation == null){
                    currentOrientation = new float[3];
                }

                currentOrientation[0] = event.values[0];
                currentOrientation[1] = event.values[1];
                currentOrientation[2] = event.values[2];
            }
        };



    }
