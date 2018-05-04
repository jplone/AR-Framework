package edu.calstatela.jplone.arframework.ARGL.Unit;

import android.content.Context;
import android.opengl.Matrix;
import android.util.Log;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import edu.calstatela.jplone.arframework.ARData.ARLandmark;
import edu.calstatela.jplone.arframework.ARGL.Billboard.ARGLBillboard;
import edu.calstatela.jplone.arframework.ARGL.Billboard.ARGLSizedBillboard;
import edu.calstatela.jplone.arframework.ARGL.Utils.ARGLBillboardMaker;
import edu.calstatela.jplone.arframework.Utils.ARMath;

/**
 * Created by bill on 11/16/17.
 */

public class ARGLRenderJob {
    // arbitrary types for different models
    public static final int TYPE_BILLBOARD = 0x1C;
    public static final int TYPE_BILLBOARD_LANDMARK = 0x0E;

    // private class variables
    private int type;
    private Dictionary<String, Object> params;

    public ARGLRenderJob(int type, Dictionary<String, Object> params) {
        this.type = type;
        this.params = params;
    }

    public Object execute() {
        Object ret = null;

        switch(type) {
            case TYPE_BILLBOARD:
                int scale = (Integer) this.params.get("scale");
                int iconResourceId = (Integer) this.params.get("iconResId");
                String title = (String) this.params.get("title");
                String text = (String) this.params.get("text");
                ARGLPosition position = (ARGLPosition) this.params.get("position");
                ARGLSizedBillboard.Listener callback = (ARGLSizedBillboard.Listener) this.params.get("callback");

                ARGLSizedBillboard billboard = ARGLBillboardMaker.make(scale, iconResourceId, title, text);

                if(position != null)
                    billboard.setPosition(position);

                if(callback != null)
                    billboard.setCallback(callback);

                ret = billboard;
                break;
        }

        return ret;
    }

    public Object execute(float[] latLonAlt) {
        Object ret = null;
        ARLandmark here = new ARLandmark("", "", latLonAlt[0], latLonAlt[1], 100);

        switch(type) {
            case TYPE_BILLBOARD_LANDMARK:
                int scale = (Integer) this.params.get("scale");
                int iconResourceId = (Integer) this.params.get("iconResId");
                ARLandmark current = (ARLandmark) this.params.get("landmark");
                ARGLSizedBillboard.Listener callback = (ARGLSizedBillboard.Listener) this.params.get("callback");

                ARGLSizedBillboard billboard = ARGLBillboardMaker.make(scale, iconResourceId, current.title, current.description);

                if(callback != null)
                    billboard.setCallback(callback);

                billboard.setLandmark(current);
                ret = billboard;
                break;
        }

        return ret;
    }

    public boolean compare(ARGLRenderJob job) {
        boolean equal = true;

        switch(type) {
            case TYPE_BILLBOARD:
                String self_title = (String) this.params.get("title");
                String self_text = (String) this.params.get("text");
                ARGLPosition self_position = (ARGLPosition) this.params.get("position");

                String job_title = (String) job.params.get("title");
                String job_text = (String) job.params.get("text");
                ARGLPosition job_position = (ARGLPosition) job.params.get("position");

                if(!self_title.equals(job_title))
                    equal = false;
                if(!self_text.equals(job_text))
                    equal = false;
                if(!self_position.compare(job_position))
                    equal = false;
                break;

            case TYPE_BILLBOARD_LANDMARK:
                ARLandmark self_current = (ARLandmark) this.params.get("landmark");
                ARLandmark job_current = (ARLandmark) job.params.get("landmark");

                if(!self_current.compare(job_current))
                    equal = false;
                break;
        }

        return equal;
    }

    public ARGLRenderJob clone() {
        return new ARGLRenderJob(type, params);
    }

    // static helper functions
    public static ARGLRenderJob makeBillboard(int scale, int iconResourceId, String title, String text, ARGLPosition position, ARGLSizedBillboard.Listener callback) {
        Hashtable<String, Object> bbParams = new Hashtable<String, Object>();
        bbParams.put("scale", new Integer(scale));
        bbParams.put("iconResId", new Integer(iconResourceId));
        bbParams.put("title", title);
        bbParams.put("text", text);
        bbParams.put("position", position);
        bbParams.put("callback", callback);

        return new ARGLRenderJob(TYPE_BILLBOARD, bbParams);
    }

    public static ARGLRenderJob makeBillboard(int scale, int iconResourceId, ARLandmark landmark, ARGLSizedBillboard.Listener callback) {
        Hashtable<String, Object> bbParams = new Hashtable<String, Object>();
        bbParams.put("scale", new Integer(scale));
        bbParams.put("iconResId", new Integer(iconResourceId));
        bbParams.put("landmark", landmark);
        bbParams.put("callback", callback);

        return new ARGLRenderJob(TYPE_BILLBOARD_LANDMARK, bbParams);
    }
}
