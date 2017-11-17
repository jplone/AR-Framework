package edu.calstatela.jplone.ardemo;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import edu.calstatela.jplone.arframework.ARFragment;
import edu.calstatela.jplone.arframework.ARGL.Unit.ARGLPosition;
import edu.calstatela.jplone.arframework.ARGL.Unit.ARGLRenderJob;

/**
 * Created by bill on 11/14/17.
 */

public class DisplayCompassActivity extends AppCompatActivity {
    ARFragment arFragment;

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

        // build the compass and add them to AR fragment to be displayed
        buildCompass();
    }

    void buildCompass() {
        int ara_icon = edu.calstatela.jplone.arframework.R.drawable.ara_icon;

        ARGLPosition mNorthPos = new ARGLPosition(0, 0, -10, 0, 0, 1, 0);
        ARGLPosition mEastPos  = new ARGLPosition(0, 0, -10, -90, 0, 1, 0);
        ARGLPosition mSouthPos = new ARGLPosition(0, 0, -10, -180, 0, 1, 0);
        ARGLPosition mWestPos  = new ARGLPosition(0, 0, -10, -270, 0, 1, 0);

        arFragment.addJob(ARGLRenderJob.makeBillboard(5, ara_icon, "North", "A compass direction", mNorthPos));
        arFragment.addJob(ARGLRenderJob.makeBillboard(5, ara_icon, "East", "A compass direction", mEastPos));
        arFragment.addJob(ARGLRenderJob.makeBillboard(5, ara_icon, "South", "A compass direction", mSouthPos));
        arFragment.addJob(ARGLRenderJob.makeBillboard(5, ara_icon, "West", "A compass direction", mWestPos));
    }
}
