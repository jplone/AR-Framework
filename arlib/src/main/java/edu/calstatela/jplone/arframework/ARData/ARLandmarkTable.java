package edu.calstatela.jplone.arframework.ARData;

import java.util.ArrayList;

/**
 * Created by bill on 11/16/17.
 */

public class ARLandmarkTable extends ArrayList<ARLandmark> {
    ArrayList<Float> distances = new ArrayList<>();

    public ARLandmarkTable withinRadius(float latitude, float longitude, float radiusMeters){
        ARLandmarkTable newTable = new ARLandmarkTable();
        ARLandmark here = new ARLandmark("Here", "herer", latitude, longitude, 100.0f);

        for(int i = 0; i < size(); i++){
            ARLandmark currentLandmark = get(i);
            float distance = here.distance(currentLandmark);
            if (distance < radiusMeters)
                newTable.add(currentLandmark);
        }

        return newTable;
    }

    public void loadCalstateLA(){
        add(new ARLandmark("Hydrogen Station", "CalstateLA Facility", 34.066304f, -118.165378f, 100.0f));
        add(new ARLandmark("Senior Design B10", "CalstateLA Facility", 34.066223f, -118.166367f, 100.0f));
    }

    public void loadCities(){
        add(new ARLandmark("South Pasadena", "City", 34.117817f, -118.159151f, 100.0f));
        add(new ARLandmark("AlHambra", "City", 34.096963f, -118.128171f, 100.0f));
        add(new ARLandmark("CalstateLA", "City", 34.066223f, -118.166367f, 100.0f));
        add(new ARLandmark("San Diego", "City", 32.704840f, -117.151137f, 100.0f));
        add(new ARLandmark("Malibu", "City", 34.024068f, -118.779106f, 100.0f));
        add(new ARLandmark("Hollywood", "City", 34.096703f, -118.330328f, 100.0f));
        add(new ARLandmark("San Dimas", "City", 34.110945f, -117.816446f, 100.0f));
        add(new ARLandmark("Tijuana", "City", 32.518761f, -117.039820f, 100.0f));
        add(new ARLandmark("Santa Monica", "City", 34.014880f, -118.495709f, 100.0f));
        add(new ARLandmark("Pasadena", "City", 34.145597f, -118.145267f, 100.0f));
    }

    public void loadMountains(){

    }
}
