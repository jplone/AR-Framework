package edu.calstatela.jplone.watertrekapp.MapUtil;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.graphics.Point;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import edu.calstatela.jplone.arframework.integrated.ARFragment;
import edu.calstatela.jplone.arframework.integrated.AREvent;

/**
 * Created by bill on 5/3/18.
 */

public class MapUtil {
    public static String TAG = "MapUtil";

    private static final float DEFAULT_ZOOM = (float) 10.8; // shows the neighborhood
    private static final float CAMERA_TILT = 89.5f; // shows 3D view

    public static void setupMapPanel(Activity a, int container_id, final ARFragment arFragment) {
        FragmentTransaction ft = a.getFragmentManager().beginTransaction();
        MapFragment mMapFragment = MapFragment.newInstance();
        mMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Log.d(TAG, "Google Maps fetched");
                final GoogleMap mMap = googleMap;
                mMap.getUiSettings().setMapToolbarEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mMap.getUiSettings().setCompassEnabled(false);

                // setup map
                mMap.setIndoorEnabled(true);
                mMap.setBuildingsEnabled(true);

                arFragment.setCallback(new AREvent.Callback() {
                    @Override
                    public void onAREvent(AREvent arEvent) {
                        if(arEvent.type == AREvent.TYPE_LOCATION) {
                            CameraPosition camPos = CameraPosition.builder(mMap.getCameraPosition())
                                    .target(new LatLng(arEvent.latitude, arEvent.longitude))
                                    .bearing((float)arEvent.bearing)
                                    .zoom(DEFAULT_ZOOM)
                                    .tilt(CAMERA_TILT)
                                    .build();
                            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(camPos));
                        }
                    }
                });
            }
        });
        ft.add(container_id, mMapFragment);

        ft.commit();

        // resize map fragment layout
        FrameLayout mapLayout = (FrameLayout) a.findViewById(container_id);
        ViewGroup.LayoutParams params = mapLayout.getLayoutParams();

        Point screenSize = new Point();
        a.getWindowManager().getDefaultDisplay().getSize(screenSize);
        final int min_dim = Math.min(screenSize.x, screenSize.y);

        params.height = (int) Math.round(min_dim / 2.15);
        params.width = (int) Math.round(min_dim / 2.15);
        mapLayout.setLayoutParams(params);
    }
}
