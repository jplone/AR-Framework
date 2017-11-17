package edu.calstatela.jplone.arframework.ARGL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;

import edu.calstatela.jplone.arframework.ARGL.Billboard.ARGLSizedBillboard;
import edu.calstatela.jplone.arframework.ARGL.Unit.ARGLPosition;
import edu.calstatela.jplone.arframework.ARGL.Unit.ARGLRenderJob;
import edu.calstatela.jplone.arframework.ARGL.Utils.ARGLBillboardMaker;
import edu.calstatela.jplone.arframework.ARGL.Utils.ARGLTextureHelper;

/**
 * Created by bill on 11/2/17.
 */

public class ARGLScene {
    private ArrayList<ARGLRenderJob> renderList;

    public ARGLScene() {
        renderList = new ArrayList<ARGLRenderJob>();
    }

    public void init(Context context) {
        ARGLBillboardMaker.init(context);
    }

    public void destroy() {
        ARGLBillboardMaker.destroy();
    }

    public void addJob(ARGLRenderJob job) {
        renderList.add(job);
    }
}
