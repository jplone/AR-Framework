package edu.calstatela.jplone.ardemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import edu.calstatela.jplone.arframework.ARData.ARLandmark;
import edu.calstatela.jplone.arframework.ARData.ARLandmarkTable;

public class DisplayDataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_data);

        // hide the action bar (gets fullscreen)
        getSupportActionBar().hide();

        ARLandmarkTable table = new ARLandmarkTable();
        table.loadCities();

        int billboard_id = getIntent().getIntExtra("billboard_id", -1);

        if(billboard_id > -1) {
            ARLandmark landmark = table.get(billboard_id);

            TextView txtTitle = findViewById(R.id.txt_title);
            txtTitle.setText("Title: " + landmark.title);

            TextView txtDesc = findViewById(R.id.txt_desc);
            txtDesc.setText("Description: " + landmark.description);

            TextView txtLat = findViewById(R.id.txt_lat);
            txtLat.setText("Latitude: " + landmark.latitude);

            TextView txtLon = findViewById(R.id.txt_lon);
            txtLon.setText("Longitude: " + landmark.longitude);

            TextView txtElev = findViewById(R.id.txt_elev);
            txtElev.setText("Elevation: " + landmark.elevation);
        }
        else {
            TextView txtTitle = findViewById(R.id.txt_title);
            txtTitle.setText("An unknown error occurred!");
        }
    }
}
