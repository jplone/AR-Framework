package edu.calstatela.jplone.arframework.ARSensors;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;


public class ARLocationSensor {
    private Context arContext;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    //private ArrayList<Listener> mListenerList = new ArrayList<Listener>();
    private Listener arListener;
    private boolean started;

    public ARLocationSensor(Context context, Listener listener){
        arContext = context;
        arListener = listener;
        started = false;
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(arContext);
    }

    public boolean start(){
        boolean havePermission = ContextCompat.checkSelfPermission(arContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        if(havePermission){
            try {
                //mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(arContext, mLocationSuccessListener);
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
        return false;
    }

    public void stop(){
        if(started) {
            mFusedLocationProviderClient.removeLocationUpdates(mLocationUpdatesCallback);
            started = false;
        }
    }

    /*
    public void addListener(Listener listener){
        mListenerList.add(listener);
    }

    public void removeListener(Listener listener) {
        if(mListenerList.contains(listener))
            mListenerList.remove(listener);
    }
    */

    public interface Listener{
        public void handleLocation(Location location);
    }

    private OnSuccessListener<Location> mLocationSuccessListener = new OnSuccessListener<Location>() {
        @Override
        public void onSuccess(Location location) {
            arListener.handleLocation(location);
            //for(Listener listener : mListenerList)
            //    listener.handleLocation(location);
        }
    };

    private LocationCallback mLocationUpdatesCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            for(Location location : locationResult.getLocations())
                arListener.handleLocation(location);
                //for(Listener listener : mListenerList)
                //    listener.handleLocation(location);
        }
    };
}
