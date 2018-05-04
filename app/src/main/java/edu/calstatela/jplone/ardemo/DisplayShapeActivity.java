package edu.calstatela.jplone.ardemo;

import android.app.FragmentTransaction;
import android.nfc.Tag;
import android.opengl.GLES20;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import edu.calstatela.jplone.arframework.ARData.ARGLMeshData;
import edu.calstatela.jplone.arframework.ARFragment;
import edu.calstatela.jplone.arframework.ARGL.ARGLCamera2;
import edu.calstatela.jplone.arframework.ARGL.ARGLEntity;
import edu.calstatela.jplone.arframework.ARGL.ARGLProjection;
import edu.calstatela.jplone.arframework.ARGL.ARGLScene;
import edu.calstatela.jplone.arframework.ARGL.Drawable.ARGLLitModel;
import edu.calstatela.jplone.arframework.Utils.AREvent;
import edu.calstatela.jplone.arframework.Utils.ARRenderCallback;

public class DisplayShapeActivity extends AppCompatActivity {
    ARFragment arFragment;

    private ARGLProjection projection;
    private ARGLCamera2 camera;

    private float[] currentOrientation = null;

    private ARGLLitModel model;
    private ARGLEntity entity1, entity2;
    private float angle1 = 0, angle2 = 0;
    private ARGLScene scene;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_ar);

        // hide the action bar (gets fullscreen)
        getSupportActionBar().hide();

        // setting up fragments
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        arFragment = ARFragment.newSimpleInstance();
        ft.add(R.id.ar_view_container, arFragment);

        ft.commit();

        arFragment.setCallback(new AREvent.Callback() {
            @Override
            public void onAREvent(AREvent e) {
                if(e.type == AREvent.TYPE_ORIENTATION)
                    currentOrientation = e.angles;
            }
        });

        // setting up render callbacks
        arFragment.setRenderCallback(new ARRenderCallback() {
            @Override
            public void onGLInit() {
                Log.d("ShapeActivity", "init");
                init();
            }

            @Override
            public void onGLResize(int width, int height) {
                resize(width, height);
            }

            @Override
            public void onGLDraw(float[] projection, float[] view) {
                draw(projection, view);
            }
        });
    }

    private void init() {
        projection = new ARGLProjection();
        camera = new ARGLCamera2();
        camera.setPosition(0, 0, 5);
        camera.setOrientation(0, 1, 0, 0);

        model = new ARGLLitModel();
        model.loadVertices(ARGLMeshData.pyramid());
        model.loadNormals(ARGLMeshData.calculateNormals(ARGLMeshData.pyramid()));

        float[] scale = {1, 1, 1};
        scene = new ARGLScene();
        entity1 = scene.addDrawable(model, scale, new float[]{1, 1, -1}, 0);
        scale[1] = 3;
        entity2 = scene.addDrawable(model, scale, new float[]{-1, 1, -1}, 0);

        GLES20.glClearColor(0, 0, 0, 0);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
    }

    private void resize(int width, int height) {
        projection.setPerspective(60, (float)width / height, 0.01f, 100000000f);
        GLES20.glViewport(0, 0, width, height);
    }

    private void draw(float[] projection_m, float[] view_m) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        if(currentOrientation != null)
            camera.setOrientationVector(currentOrientation, 0);

        /* Update Entities/Scenes */
        entity1.setRotation(angle1);
        entity2.setRotation(angle2);
        angle1 += 1;
        angle2 += 3;

        /* Draw */
        scene.draw(projection.getProjectionMatrix(), camera.getViewMatrix());
    }
}
