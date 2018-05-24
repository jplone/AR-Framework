package edu.calstatela.jplone.arframework.graphics3d.scene;


import edu.calstatela.jplone.arframework.graphics3d.entity.Entity;
import edu.calstatela.jplone.arframework.util.GeoMath;

public class CircleScene extends Scene{
    private static final String TAG = "waka-circleScene";

    private float mRadius = 10;
    private float[] mCenter = new float[3];

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void setRadius(float radius){
        mRadius = radius;
    }

    public void setCenterLatLonAlt(float[] latLonAlt){
        GeoMath.latLonAltToXYZ(latLonAlt, mCenter);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void update(){
        for(int i = 0; i < mEntityList.size(); i++){
            Entity entity = mEntityList.get(i);
            updateEntity(entity);
        }
    }

    private void updateEntity(Entity entity){
        float[] pos = entity.getPosition();
        float angle = GeoMath.xyzBearing(mCenter, pos);
        float distance = GeoMath.xyzDistance(mCenter, pos);
        float scale = distance / mRadius;
        entity.setYaw(-angle);
        entity.setScale(scale, scale, scale);
    }

}
