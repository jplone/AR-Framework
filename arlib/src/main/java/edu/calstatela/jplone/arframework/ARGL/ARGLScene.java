package edu.calstatela.jplone.arframework.ARGL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;

import edu.calstatela.jplone.arframework.ARGL.Billboard.ARGLSizedBillboard;
import edu.calstatela.jplone.arframework.ARGL.Drawable.ARGLDrawable;
import edu.calstatela.jplone.arframework.ARGL.Unit.ARGLPosition;
import edu.calstatela.jplone.arframework.ARGL.Unit.ARGLRenderJob;
import edu.calstatela.jplone.arframework.ARGL.Utils.ARGLBillboardMaker;
import edu.calstatela.jplone.arframework.ARGL.Utils.ARGLTextureHelper;
import edu.calstatela.jplone.arframework.Utils.ARMath;

/**
 * Created by bill on 11/2/17.
 */

public class ARGLScene {
    protected ArrayList<ARGLEntity> mEntityList = new ArrayList<>();

    public void add(ARGLEntity entity){
        mEntityList.add(entity);
    }

    public void draw(float[] projectionMatrix, float[] viewMatrix){
        for(ARGLEntity e : mEntityList){
            e.draw(projectionMatrix, viewMatrix, e.getModelMatrix());
        }
    }

    public boolean isEmpty(){
        return mEntityList.isEmpty();
    }


    public ARGLEntity addDrawable(ARGLDrawable drawable, float[] scale, float[] xyz, float angle){
        ARGLEntity entity = new ARGLEntity();
        entity.setDrawable(drawable);
        entity.setScale(scale[0], scale[1], scale[2]);
        entity.setPosition(xyz[0], xyz[1], xyz[2]);
        entity.setRotation(angle);
        add(entity);
        return entity;
    }

    public void addMountain(ARGLDrawable drawable, float[] latLonAlt){
        ARGLEntity entity = new ARGLEntity();
        entity.setDrawable(drawable);
        float[] xyz = new float[3];
        ARMath.latLonAltToXYZ(latLonAlt, xyz);
        entity.setScale(xyz[1] / 2, xyz[1], xyz[1] / 2);
        entity.setPosition(xyz[0], 0, xyz[2]);
        add(entity);
    }
}
