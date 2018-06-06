package edu.calstatela.jplone.arframework.sensor;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;


public class ARGps {
    private static final String TAG = "waka-ARGPS";
    private static final int PERMISSION_REQUEST_CODE = 79;
    private Context mActivity;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private ArrayList<Listener> mListenerList = new ArrayList<Listener>();
    private boolean started = false;

    public ARGps(Context activity){
        mActivity = activity;
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);
    }

    public boolean start(){
        boolean havePermission = ContextCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        if(havePermission){
            Log.d(TAG, "Have Permission");
            try {
                mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(mLocationSuccessListener);

                LocationRequest locationRequest = new LocationRequest();
                locationRequest.setInterval(5000);
                locationRequest.setFastestInterval(5000);
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

                mFusedLocationProviderClient.requestLocationUpdates(locationRequest, mLocationUpdatesCallback, null);
                started = true;
                return true;
            }
            catch(SecurityException e){
                e.printStackTrace();
            }
        }
        else{
            Log.d(TAG, "No Permission");
//            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        }
        return false;
    }

    public void stop(){
        if(started) {
            mFusedLocationProviderClient.removeLocationUpdates(mLocationUpdatesCallback);
            started = false;
        }
    }

    public void addListener(Listener listener){
        mListenerList.add(listener);
    }

    public interface Listener{
        public void handleLocation(Location location);
    }






    private OnSuccessListener<Location> mLocationSuccessListener = new OnSuccessListener<Location>() {
        @Override
        public void onSuccess(Location location) {
            for(Listener listener : mListenerList)
                listener.handleLocation(location);
        }
    };

    private LocationCallback mLocationUpdatesCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            for(Location location : locationResult.getLocations())
                for(Listener listener : mListenerList)
                    listener.handleLocation(location);
        }
    };
}
