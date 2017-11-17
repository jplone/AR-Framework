package edu.calstatela.jplone.ardemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button mSensorListButton = (Button) findViewById(R.id.btn_sensor_list);
        Button mSensorReadingsButton = (Button) findViewById(R.id.btn_sensor_readings);
        Button mMapButton = (Button) findViewById(R.id.btn_map);
        Button mCamera1Button = (Button) findViewById(R.id.btn_camera1);
        Button mOpenGL1Button = (Button) findViewById(R.id.btn_opengl1);
        Button mARViewButton = (Button) findViewById(R.id.btn_arview);

        mSensorListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(MainActivity.this, SensorListActivity.class);
                //startActivity(intent);
            }
        });

        mSensorReadingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(MainActivity.this, SensorReadingsActivity.class);
                //startActivity(intent);
            }
        });

        mMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(MainActivity.this, DisplayMapActivity.class);
                //startActivity(intent);
            }
        });

        mCamera1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                //startActivity(intent);
            }
        });

        mOpenGL1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DisplayLandmarkActivity.class);
                startActivity(intent);
            }
        });

        mARViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DisplayCompassActivity.class);
                startActivity(intent);
            }
        });
    }
}
