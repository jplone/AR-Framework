package edu.calstatela.jplone.arframework;

import android.app.Fragment;
import android.content.Context;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v13.app.FragmentCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import edu.calstatela.jplone.arframework.ARGL.Unit.ARGLRenderJob;
import edu.calstatela.jplone.arframework.ARSensors.ARLocationSensor;
import edu.calstatela.jplone.arframework.Utils.AREvent;
import edu.calstatela.jplone.arframework.Utils.ARMath;
import edu.calstatela.jplone.arframework.Utils.ARPermissions;
import edu.calstatela.jplone.arframework.Utils.ARRenderCallback;

/**
 * Created by bill on 11/2/17.
 */

public class ARFragment extends Fragment {
    public static final String TAG = "ARFragment";
    private static final int ARFRAGMENT_PERMISSION_REQUEST_CODE = 0x1234;
    private Context arContext;
    private ARView arView;
    private ArrayList<ARGLRenderJob> deferredRenderAddList;
    private ArrayList<ARGLRenderJob> deferredRenderDelList;
    private boolean useGPS;
    private ARLocationSensor arLocationSensor;
    private Fragment arFragmentInstance;

    private AREvent.Callback arCallback;
    private ARRenderCallback renderCallback;

    public ARFragment() {
        super();
        arFragmentInstance = this;
        deferredRenderAddList = new ArrayList<ARGLRenderJob>();
        deferredRenderDelList = new ArrayList<ARGLRenderJob>();
        this.useGPS = false;
    }

    public ARFragment(boolean useGPS) {
        super();
        arFragmentInstance = this;
        deferredRenderAddList = new ArrayList<ARGLRenderJob>();
        deferredRenderDelList = new ArrayList<ARGLRenderJob>();
        this.useGPS = useGPS;
    }

    public static ARFragment newSimpleInstance() {
        return new ARFragment(false);
    }
    public static ARFragment newGPSInstance() {
        return new ARFragment(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        arContext = this.getActivity().getBaseContext();
        arView = new ARView(arContext, useGPS);

        // if there are things in the deferred renderer list, copy them
        arView.mirrorRenderAddList(deferredRenderAddList);
        arView.mirrorRenderDelList(deferredRenderDelList);

        // then clear the list (garbage collect it)
        deferredRenderAddList.clear();

        // set up GPS
        if(useGPS)
            arLocationSensor = new ARLocationSensor(arContext, mGPSListener);

        if(arCallback != null) {
            arView.setCallback(arCallback);
            arCallback = null;
        }

        if(renderCallback != null) {
            arView.setRenderCallback(renderCallback);
            renderCallback = null;
        }

        return arView;
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        Log.d(TAG, "View Created!");
        if (!checkPermissions()) {
            Log.d(TAG, "no permissions!");
            requestPermissions();
        }
        else if(!useGPS || (useGPS && latLonAlt != null))
            start();
        else if(useGPS)
            arLocationSensor.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "resumed operations");
        if(useGPS)
            arLocationSensor.start();
        if(!useGPS || (useGPS && latLonAlt != null))
            start();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "paused operations");
        stop();
    }

    private void start() {
        Log.d(TAG, "started AR view");
        arContext = this.getActivity().getApplicationContext();
        arView.start(arContext);
    }

    private void stop() {
        Log.d(TAG, "stopped AR view");
        if(useGPS)
            arLocationSensor.stop();
        arView.stop();
    }

    public void addJob(ARGLRenderJob job) {
        if(arView != null)
            arView.addJob(job);
        else
            deferredRenderAddList.add(job);
    }

    public void removeJob(ARGLRenderJob job) {
        // if arView hasn't started then there is nothing to delete
        if(arView != null)
            arView.removeJob(job);
        else
            deferredRenderDelList.add(job);
    }

    public void setCallback(AREvent.Callback callback) {
        if(arView != null)
            arView.setCallback(callback);
        else
            this.arCallback = callback;
    }

    public void setRenderCallback(ARRenderCallback renderCallback) {
        if(arView != null)
            arView.setRenderCallback(renderCallback);
        else
            this.renderCallback = renderCallback;
    }

    public void updateLatLonAlt(float[] latLonAlt) {
        arView.updateLatLonAlt(latLonAlt);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      Permission Handling Functions
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d("ARFragment", "received permission result");
        if(requestCode == ARFRAGMENT_PERMISSION_REQUEST_CODE) {
            boolean all_passed = true;

            for(int i=0; i<grantResults.length; i++) {
                 if(grantResults[i] != PermissionChecker.PERMISSION_GRANTED)
                     all_passed = false;
            }

            if(all_passed) {
                arLocationSensor.start();
                if(!useGPS || (useGPS && latLonAlt != null))
                    start();
                Log.d("ARFragment", "permission granted!");
            }
        }
    }

    public void requestPermissions() {
        String[] permissions = new String[]{ARPermissions.PERMISSION_CAMERA};
        if(useGPS)
            permissions = new String[]{ARPermissions.PERMISSION_CAMERA, ARPermissions.PERMISSION_ACCESS_FINE_LOCATION};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.requestPermissions(permissions, ARFRAGMENT_PERMISSION_REQUEST_CODE);
        } else {
            FragmentCompat.requestPermissions(this, permissions, ARFRAGMENT_PERMISSION_REQUEST_CODE);
        }
    }

    public boolean checkPermissions() {
        if(ContextCompat.checkSelfPermission(this.getActivity(), ARPermissions.PERMISSION_CAMERA) == PermissionChecker.PERMISSION_GRANTED &&
           ((useGPS && ContextCompat.checkSelfPermission(this.getActivity(), ARPermissions.PERMISSION_ACCESS_FINE_LOCATION) == PermissionChecker.PERMISSION_GRANTED) ||
            !useGPS
           )
          )
            return true;
        return false;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      GPS Listener
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////

    Location mOriginalLoc = null;
    float[] mPosition = new float[3];
    float[] latLonAlt = null;

    ARLocationSensor.Listener mGPSListener = new ARLocationSensor.Listener() {
        @Override
        public void handleLocation(Location location) {
            if(mOriginalLoc == null){
                mOriginalLoc = new Location(location);
                latLonAlt = new float[3];

                start();
            }

            float distance = location.distanceTo(mOriginalLoc);
            float bearing = location.bearingTo(mOriginalLoc);

            mPosition[0] = distance * (float)Math.cos(ARMath.degreesToRad(bearing));
            mPosition[1] = distance * (float)Math.sin(ARMath.degreesToRad(bearing));
            mPosition[2] = (float)(location.getAltitude() - mOriginalLoc.getAltitude());

            latLonAlt[0] = (float)location.getLatitude();
            latLonAlt[1] = (float)location.getLongitude();
            latLonAlt[2] = (float)location.getAltitude();

            Log.d(TAG, String.format("gps: [%f, %f, %f]\n", latLonAlt[0], latLonAlt[1], latLonAlt[2]));
            updateLatLonAlt(latLonAlt);
        }
    };
}
