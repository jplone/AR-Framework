package edu.calstatela.jplone.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import edu.calstatela.jplone.arframework.util.Orientation;
import edu.calstatela.jplone.arframework.util.Permissions;

public class MenuActivity extends AppCompatActivity {

    private static final String TAG = "waka-mainAct";

    Button compassButton;
    Button landmarksButton;
    Button shapeDrawingButton;
    Button circleSceneButton;
    Button mountainButton;
    Button graphicsTestButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        compassButton = (Button)findViewById(R.id.btn_billboard_compass);
        landmarksButton = (Button)findViewById(R.id.btn_landmarks);
        shapeDrawingButton = (Button)findViewById(R.id.btn_shape_draw);
        circleSceneButton = (Button)findViewById(R.id.btn_circle_scene_draw);
        mountainButton = (Button)findViewById(R.id.btn_mountain_draw);
        graphicsTestButton = (Button)findViewById(R.id.btn_graphics_test);

        compassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, BillboardCompassActivity.class);
                startActivity(intent);
            }
        });

        landmarksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, BillboardLandmarksActivity.class);
                startActivity(intent);
            }
        });

        shapeDrawingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, ShapeDrawActivity.class);
                startActivity(intent);
            }
        });

        circleSceneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, CircleSceneActivity.class);
                startActivity(intent);
            }
        });

        mountainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, MountainDrawActivity.class);
                startActivity(intent);
            }
        });

        graphicsTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, GraphicsTestActivity.class);
                startActivity(intent);
            }
        });

        if(!Permissions.havePermission(this, Permissions.PERMISSION_ACCESS_FINE_LOCATION)){
            Permissions.requestPermission(this, Permissions.PERMISSION_ACCESS_FINE_LOCATION);
        }
        if(!Permissions.havePermission(this, Permissions.PERMISSION_CAMERA)){
            Permissions.requestPermission(this, Permissions.PERMISSION_CAMERA);
        }


        Log.d(TAG, "Orientation: " + Orientation.getOrientationAngle(this));

    }
}
