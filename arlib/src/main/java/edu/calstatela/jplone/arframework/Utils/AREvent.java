package edu.calstatela.jplone.arframework.Utils;

/**
 * Created by bill on 3/13/18.
 */

public class AREvent {
    public double latitude;
    public double longitude;
    public double bearing;

    public AREvent(double latitude, double longitude, double bearing) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.bearing = bearing;
    }

    public interface Callback {
        public void onAREvent(AREvent e);
    }
}
