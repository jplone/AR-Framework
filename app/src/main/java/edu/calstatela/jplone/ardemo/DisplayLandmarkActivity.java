package edu.calstatela.jplone.ardemo;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import edu.calstatela.jplone.arframework.ARData.ARLandmark;
import edu.calstatela.jplone.arframework.ARData.ARLandmarkTable;
import edu.calstatela.jplone.arframework.ARFragment;
import edu.calstatela.jplone.arframework.ARGL.Unit.ARGLRenderJob;

/**
 * Created by bill on 11/16/17.
 */

public class DisplayLandmarkActivity extends AppCompatActivity {
    ARFragment arFragment;
    ARLandmarkTable arLandmarkTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_ar);

        // hide the action bar (gets fullscreen)
        getSupportActionBar().hide();

        // setting up fragments
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        arFragment = ARFragment.newGPSInstance();
        ft.add(R.id.ar_view_container, arFragment);

        ft.commit();

        // build the compass and add them to AR fragment to be displayed
        buildLandmarks();
    }

    void buildLandmarks() {
        int ara_icon = edu.calstatela.jplone.arframework.R.drawable.ara_icon;
        arLandmarkTable = new ARLandmarkTable();
        arLandmarkTable.loadCities();

        for(int i=0; i<arLandmarkTable.size(); i++) {
            ARLandmark current = arLandmarkTable.get(i);
            arFragment.addJob(ARGLRenderJob.makeBillboard(5, ara_icon, current));
        }
    }
}
