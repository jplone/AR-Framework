package edu.calstatela.jplone.watertrekapp;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;


import java.util.ArrayList;
import java.util.Dictionary;

import edu.calstatela.jplone.arframework.landmark.Landmark;
import edu.calstatela.jplone.arframework.integrated.ARFragment;
import edu.calstatela.jplone.arframework.integrated.ARGLBillboard.ARGLSizedBillboard;
import edu.calstatela.jplone.arframework.integrated.Unit.ARGLRenderJob;
import edu.calstatela.jplone.arframework.integrated.AREvent;
import edu.calstatela.jplone.watertrekapp.Data.Reservoir;
import edu.calstatela.jplone.watertrekapp.Data.SoilMoisture;
import edu.calstatela.jplone.watertrekapp.Data.Well;
import edu.calstatela.jplone.watertrekapp.DataService.ReservoirService;
import edu.calstatela.jplone.watertrekapp.DataService.SoilMoistureService;
import edu.calstatela.jplone.watertrekapp.DataService.WellService;
import edu.calstatela.jplone.watertrekapp.NetworkUtils.NetworkTask;


public class MainActivity extends AppCompatActivity {

    RelativeLayout leftRL;
    DrawerLayout drawerLayout;

    private static final String TAG = "MainActivity";



    private ARFragment arFragment;
    private ArrayList<ARGLRenderJob> arJobs = new ArrayList<ARGLRenderJob>();
    private double[] location;
    private boolean setup = false;
    private boolean tMountain = false;
    private boolean tReservoir = false;
    private boolean tWell = true;
    private boolean tRiver = false;
    private boolean tSoil = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NetworkTask.updateWatertrekCredentials(this);

        leftRL = (RelativeLayout)findViewById(R.id.whatYouWantInLeftDrawer);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

        // set up AR fragment
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        arFragment = ARFragment.newGPSInstance();
        ft.add(R.id.ar_view_container, arFragment);
        ft.commit();

        arFragment.setCallback(new AREvent.Callback() {
            @Override
            public void onAREvent(AREvent arEvent) {
                if(arEvent.type == AREvent.TYPE_LOCATION) {
                    location = new double[]{arEvent.latitude, arEvent.longitude};
                    if(!setup) {
                        getData("well");
                        setup = true;
                    }
                }
            }
        });

        arFragment.setParentActivity(this);
    }

    public void onLeft(View view) {
        drawerLayout.openDrawer(leftRL);
    }

    public void toggleMountain(View v) {
        tMountain = !tMountain;

        if(tMountain)
            getData("mountain");
        else
            removeData("mountain");
    }

    public void toggleReservoir(View v) {
        tReservoir = !tReservoir;

        if(tReservoir)
            getData("reservoir");
        else
            removeData("reservoir");
    }

    public void toggleWell(View v) {
        tWell = !tWell;

        if(tWell)
            getData("well");
        else
            removeData("well");
    }

    public void toggleRiver(View v) {
        tRiver = !tRiver;

        if(tRiver)
            getData("river");
        else
            removeData("river");
    }

    public void toggleSoil(View v) {
        tSoil = !tSoil;

        if(tSoil)
            getData("soil");
        else
            removeData("soil");
    }

    // this function is called after the map has been set up and user GPS coordinates have been retrieved
    private void getData(String type) {
        double latitude = location[0];
        double longitude = location[1];

        if(type.equals("well")) {
            WellService.getWells(new NetworkTask.NetworkCallback() {
                @Override
                public void onResult(int type, String result) {
                    // handle the result on UI thread
                    if (result != null) {
                        ArrayList<Well> wells = (ArrayList<Well>) WellService.parseWells(result);
                        final int data_icon = R.drawable.well_bb_icon;

                        for (Well w : wells) {
                            double lat = Double.parseDouble(w.getLat());
                            double lon = Double.parseDouble(w.getLon());
                            String title = "Well " + w.getMasterSiteId();

                            if(tWell)
                                addData("well", title, w.toString(), lat, lon, data_icon);
                        }
                    }
                }
            }, latitude, longitude, 10.0);
        }
        else if(type.equals("reservoir")) {
            ReservoirService.getAllReservoir(new NetworkTask.NetworkCallback() {
                @Override
                public void onResult(int type, String result) {
                    if(result != null) {
                        ArrayList<Reservoir> reservoirs = (ArrayList<Reservoir>) ReservoirService.parseAllReservoirs(result);
                        final int data_icon = R.drawable.reservoir_bb_icon;

                        for(Reservoir r : reservoirs) {
                            double lat = Double.parseDouble(r.getLat());
                            double lon = Double.parseDouble(r.getLon());
                            String title = "Reservoir " + r.getSiteNo();

                            if(tReservoir)
                                addData("reservoir", title, r.toString(), lat, lon, data_icon);
                        }
                    }
                }
            });
        }
        else if(type.equals("soil")) {
            SoilMoistureService.getSoilMoistures(new NetworkTask.NetworkCallback() {
                @Override
                public void onResult(int type, String result) {
                    if(result != null) {
                        ArrayList<SoilMoisture> soils = (ArrayList<SoilMoisture>) SoilMoistureService.parseSoilMoistures(result);
                        final int data_icon = R.drawable.soil_bb_icon;

                        for(SoilMoisture s : soils) {
                            double lat = Double.parseDouble(s.getLat());
                            double lon = Double.parseDouble(s.getLon());
                            String title = "Soil " + s.getWbanno();

                            if(tSoil)
                                addData("soil", title, s.toString(), lat, lon, data_icon);
                        }
                    }
                }
            });
        }
    }

    public void removeData(String type) {
        ArrayList<ARGLRenderJob> removeList = new ArrayList<ARGLRenderJob>();
        Log.d(TAG, "removing " + type);

        for(ARGLRenderJob job : arJobs) {
            Dictionary<String, Object> params = job.getParams();
            Landmark lm = (Landmark) params.get("landmark");
            String title = lm.title;
            String data_type = title.split(" ")[0].toLowerCase();

            if(data_type.equals(type)) {
                Log.d(TAG, "removing " + ((Landmark)job.getParams().get("landmark")).title);
                removeList.add(job);
                arFragment.removeJob(job);
            }
        }

        for(ARGLRenderJob job : removeList) {
            arJobs.remove(job);
        }
    }

    public void addData(final String type, String title, final String data, double lat, double lon, int data_icon) {
        // setup well landmark
        Landmark arLandmark = new Landmark(title, "(" + lat + ", " + lon + ")", (float) lat, (float) lon, 100.0f);

        // add the landmark to the queue to be displayed
        ARGLRenderJob job = ARGLRenderJob.makeBillboard(5, data_icon, arLandmark, new ARGLSizedBillboard.Listener() {
            @Override
            public void interact(ARGLSizedBillboard arglSizedBillboard) {
                Log.d(TAG, "tapped on " + arglSizedBillboard.getLandmark().title);
                Intent i = new Intent(MainActivity.this, ViewActivity.class);
                i.putExtra("type", type);
                i.putExtra("data", data);
                startActivity(i);
            }
        });

        arJobs.add(job);
        arFragment.addJob(job);
    }
}
