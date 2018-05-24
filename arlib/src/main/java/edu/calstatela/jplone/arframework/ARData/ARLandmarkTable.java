package edu.calstatela.jplone.arframework.ARData;

import java.util.ArrayList;

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
        add(new ARLandmark("Swimming Pool", "CalstateLA Facility", 34.065949f, -118.167653f, 100.0f));
        add(new ARLandmark("Salazar Hall", "CalstateLA Facility", 34.063865f, -118.169829f, 100.0f));
        add(new ARLandmark("Sbaro", "CalstateLA Facility", 34.067943f, -118.168698f, 100.0f));
        add(new ARLandmark("Student Housing", "CalstateLA Facility", 34.069796f, -118.165382f, 100.0f));
        add(new ARLandmark("JFK Library  ", "CalstateLA Facility", 34.067600f, -118.167414f, 100.0f));
        add(new ARLandmark("Bookstore  ", "CalstateLA Facility", 34.067397f, -118.168369f, 100.0f));
        add(new ARLandmark("The far lot  ", "CalstateLA Facility", 34.071341f, -118.166928f, 100.0f));
        add(new ARLandmark("Bio Science Building  ", "CalstateLA Facility", 34.065553f, -118.169075f, 100.0f));
        add(new ARLandmark("Campus Security  ", "CalstateLA Facility", 34.064097f, -118.172845f, 100.0f));
        add(new ARLandmark("Annenberg Science Building  ", "CalstateLA Facility", 34.064836f, -118.168130f, 100.0f));
        add(new ARLandmark("Tennis Courts  ", "CalstateLA Facility", 34.063847f, -118.166313f, 100.0f));
        add(new ARLandmark("Fine Arts Building  ", "CalstateLA Facility", 34.067204f, -118.166618f, 100.0f));
        add(new ARLandmark("Well # 1  ", "A Well", 34.092500f, -118.130400f, 100.0f));
    }

    public void loadCities(){
        add(new ARLandmark("South Pasadena", "City", 34.117817f, -118.159151f, 100.0f));
        add(new ARLandmark("Alhambra", "City", 34.096963f, -118.128171f, 100.0f));
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
