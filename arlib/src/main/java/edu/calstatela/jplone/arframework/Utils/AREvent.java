package edu.calstatela.jplone.arframework.Utils;

/**
 * Created by bill on 3/13/18.
 */

public class AREvent {
    public static int TYPE_LOCATION = 0;
    public static int TYPE_ORIENTATION = 1;

    public int type;

    public double latitude;
    public double longitude;
    public double bearing;
    public float[] angles;

    public AREvent(double latitude, double longitude, double bearing) {
        this.type = TYPE_LOCATION;
        this.latitude = latitude;
        this.longitude = longitude;
        this.bearing = bearing;
    }

    public AREvent(float[] angles) {
        this.type = TYPE_ORIENTATION;
        this.angles = angles.clone();
    }

    public interface Callback {
        public void onAREvent(AREvent e);
    }
}
