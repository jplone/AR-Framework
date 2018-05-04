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

        Button mARCSULAButton = (Button) findViewById(R.id.btn_arcsula);
        Button mARCitiesButton = (Button) findViewById(R.id.btn_arcities);
        Button mARCompassButton = (Button) findViewById(R.id.btn_arcompass);
        Button mARShapeButton = (Button) findViewById(R.id.btn_arshape);

        mARCSULAButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DisplayLandmarkActivity.class);
                intent.putExtra("type", "csula");
                startActivity(intent);
            }
        });

        mARCitiesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DisplayLandmarkActivity.class);
                intent.putExtra("type", "cities");
                startActivity(intent);
            }
        });

        mARCompassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DisplayCompassActivity.class);
                startActivity(intent);
            }
        });

        mARShapeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DisplayShapeActivity.class);
                startActivity(intent);
            }
        });
    }
}
