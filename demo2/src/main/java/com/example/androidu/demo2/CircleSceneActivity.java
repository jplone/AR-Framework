    package com.example.androidu.demo2;


    import android.hardware.SensorEvent;
    import android.location.Location;
    import android.opengl.GLES20;
    import android.os.Bundle;
    import android.view.MotionEvent;
    import android.view.View;

    import edu.calstatela.jplone.arframework.graphics3d.camera.ARGLCamera;
    import edu.calstatela.jplone.arframework.graphics3d.drawable.Billboard;
    import edu.calstatela.jplone.arframework.graphics3d.drawable.BillboardMaker;
    import edu.calstatela.jplone.arframework.graphics3d.entity.Entity;
    import edu.calstatela.jplone.arframework.graphics3d.projection.Projection;
    import edu.calstatela.jplone.arframework.graphics3d.scene.CircleScene;
    import edu.calstatela.jplone.arframework.sensor.ARGps;
    import edu.calstatela.jplone.arframework.sensor.ARSensor;
    import edu.calstatela.jplone.arframework.ui.ARActivity;
    import edu.calstatela.jplone.arframework.ui.SensorARActivity;
    import edu.calstatela.jplone.arframework.util.GeoMath;


    public class CircleSceneActivity extends SensorARActivity {

        private static final String TAG = "waka-mountain";


        private Projection projection;
        private ARGLCamera camera;

        private Billboard mountainBB, riverBB, wellBB;
        private CircleScene scene;

        float moveFwd, moveRight, moveUp, turnRight;



        ////////////////////////////////////////////////////////////////////////////////////////////////
        //
        //      OpenGL Callbacks
        //
        ////////////////////////////////////////////////////////////////////////////////////////////////

        @Override
        public void GLInit() {
            super.GLInit();


            riverBB = BillboardMaker.make(this, R.drawable.river_icon);
            wellBB = BillboardMaker.make(this, R.drawable.well_icon);
            mountainBB = BillboardMaker.make(this, R.drawable.mountain_icon);



            scene = new CircleScene();
            scene.setRadius(10);

            Entity e;
            e = scene.addDrawable(riverBB); e.setPosition(0, 0, -40);
            e = scene.addDrawable(mountainBB); e.setPosition(7, 0, -3);
            e = scene.addDrawable(wellBB); e.setPosition(-7, 0.5f, -10);
            e = scene.addDrawable(riverBB); e.setPosition(77, 0, -15);
            e = scene.addDrawable(mountainBB); e.setPosition(4, 0, 3);
            e = scene.addDrawable(mountainBB); e.setPosition(8, 0, 33);
            e = scene.addDrawable(riverBB); e.setPosition(4, 0, -10);
            e = scene.addDrawable(wellBB); e.setPosition(1, 0.5f, -10);
            e = scene.addDrawable(riverBB); e.setPosition(1, 0, 5);
            e = scene.addDrawable(mountainBB); e.setPosition(12, 0, 0);
            e = scene.addDrawable(wellBB); e.setPosition(-4, 0.5f, 0);


            projection = new Projection();
            camera = new ARGLCamera();

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

            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

            /* Do camera stuff */
            if(getOrientation() != null && getLocation() != null) {
                camera.setOrientationVector(getOrientation(), 0);
            }

            /* Set up Entities when the conditions are right */

            /* Update Entities/Scenes */
            if(getLocation() != null) {
                scene.update();
            }


            /* Draw */
            scene.draw(projection.getProjectionMatrix(), camera.getViewMatrix());
        }






    }
