package edu.calstatela.jplone.watertrekapp2.activities;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import edu.calstatela.jplone.arframework.ui.ARView;
import edu.calstatela.jplone.watertrekapp2.NetworkUtils.NetworkTask;
import edu.calstatela.jplone.watertrekapp2.R;


public class MainActivity extends AppCompatActivity {



    private static final String TAG = "MainActivity";


    private RelativeLayout drawerContentsLayout;
    private DrawerLayout mainDrawerLayout;

    private boolean tMountain = false;
    private boolean tReservoir = false;
    private boolean tWell = true;
    private boolean tRiver = false;
    private boolean tSoil = false;


    private MainARView arview;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      Activity Lifecycle
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NetworkTask.updateWatertrekCredentials(this);

        drawerContentsLayout = (RelativeLayout)findViewById(R.id.whatYouWantInLeftDrawer);
        mainDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

        arview = new MainARView(this);

        FrameLayout mainLayout = (FrameLayout)findViewById(R.id.ar_view_container);
        mainLayout.addView(arview);

    }

    @Override
    protected void onPause() {
        super.onPause();
        arview.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        arview.onResume();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      Drawer methods
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void onMenuButtonClicked(View view) {
        mainDrawerLayout.openDrawer(drawerContentsLayout);
    }

    public void toggleMountain(View v) {
        tMountain = !tMountain;

    }

    public void toggleReservoir(View v) {
        tReservoir = !tReservoir;

    }

    public void toggleWell(View v) {
        tWell = !tWell;

    }

    public void toggleRiver(View v) {
        tRiver = !tRiver;

    }

    public void toggleSoil(View v) {
        tSoil = !tSoil;

    }

}
