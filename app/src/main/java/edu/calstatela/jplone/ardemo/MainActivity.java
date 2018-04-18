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

        Button mARLandmarkButton = (Button) findViewById(R.id.btn_arlandmark);
        Button mARCompassButton = (Button) findViewById(R.id.btn_arcompass);

        mARLandmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DisplayLandmarkActivity.class);
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
    }
}
