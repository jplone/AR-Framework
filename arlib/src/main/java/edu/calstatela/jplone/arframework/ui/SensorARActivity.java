package edu.calstatela.jplone.arframework.ui;

import android.hardware.SensorEvent;
import android.location.Location;
import android.os.Bundle;

import edu.calstatela.jplone.arframework.sensor.ARGps;
import edu.calstatela.jplone.arframework.sensor.ARSensor;
import edu.calstatela.jplone.arframework.util.GeoMath;

public class SensorARActivity extends ARActivity {

    private ARSensor orientationSensor;
    private ARGps locationSensor;
    private float[] currentOrientation = null;
    private float[] currentLocation = null;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      Activity Callbacks
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        orientationSensor = new ARSensor(this, ARSensor.ROTATION_VECTOR);
        orientationSensor.addListener(orientationListener);
        locationSensor = new ARGps(this);
        locationSensor.addListener(locationListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        orientationSensor.stop();
        locationSensor.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        orientationSensor.start();
        locationSensor.start();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      Sensor Callbacks
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private ARGps.Listener locationListener = new ARGps.Listener(){
        @Override
        public void handleLocation(Location location){
            if(currentLocation == null){
                currentLocation = new float[3];
                currentLocation[0] = (float)location.getLatitude();
                currentLocation[1] = (float)location.getLongitude();
                currentLocation[2] = (float)location.getAltitude();
                GeoMath.setReference(currentLocation);
                return;
            }

            currentLocation[0] = (float)location.getLatitude();
            currentLocation[1] = (float)location.getLongitude();
            currentLocation[2] = (float)location.getAltitude();
        }
    };

    private ARSensor.Listener orientationListener = new ARSensor.Listener(){
        @Override
        public void onSensorEvent(SensorEvent event){
            if(currentOrientation == null){
                currentOrientation = new float[3];
            }

            currentOrientation[0] = event.values[0];
            currentOrientation[1] = event.values[1];
            currentOrientation[2] = event.values[2];
        }
    };


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      Sensor Access Methods
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public float[] getOrientation(){
        return currentOrientation;
    }

    public float[] getLocation(){
        return currentLocation;
    }


}
