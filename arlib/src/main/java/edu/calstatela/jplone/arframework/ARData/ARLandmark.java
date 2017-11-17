package edu.calstatela.jplone.arframework.ARData;

import android.location.Location;

/**
 * Created by bill on 11/16/17.
 */

public class ARLandmark {
    public String title;
    public String description;
    public float latitude;
    public float longitude;
    public float elevation;

    public ARLandmark(String title, String description, float latitude, float longitude, float elevation){
        this.title = title;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.elevation = elevation;
    }

    public float distance(ARLandmark other){
        l1.setLatitude(latitude);
        l1.setLongitude(longitude);
        l2.setLatitude(other.latitude);
        l2.setLongitude(other.longitude);
        return l1.distanceTo(l2);
    }

    public float compassDirection(ARLandmark other){
        l1.setLatitude(latitude);
        l1.setLongitude(longitude);
        l2.setLatitude(other.latitude);
        l2.setLongitude(other.longitude);
        return l1.bearingTo(l2);
    }

    private static Location l1 = new Location("");
    private static Location l2 = new Location("");
}
