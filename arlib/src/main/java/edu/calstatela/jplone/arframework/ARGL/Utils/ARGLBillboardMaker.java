package edu.calstatela.jplone.arframework.ARGL.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;

import edu.calstatela.jplone.arframework.ARGL.Billboard.ARGLSizedBillboard;
import edu.calstatela.jplone.arframework.ARGL.Unit.ARGLPosition;

public class ARGLBillboardMaker {
    private static Context openGLContext;

    public static void init(Context context) { // need to be called before anything else
        openGLContext = context;
        ARGLSizedBillboard.init(context);
    }

    public static void destroy() {
        ARGLSizedBillboard.destroy();
    }

    public static ARGLSizedBillboard make(int scale, int iconResourceId, String title, String text) {
        ARGLSizedBillboard billboard = new ARGLSizedBillboard();
        billboard.setScale(scale);

        Bitmap bitmap = Bitmap.createBitmap(400, 200, Bitmap.Config.ARGB_8888);
        Paint paint = new Paint();
        Canvas canvas = new Canvas(bitmap);

        canvas.drawARGB(255, 200, 200, 200);

        Bitmap icon = ARGLTextureHelper.bitmapFromResource(openGLContext, iconResourceId);
        canvas.drawBitmap(icon, new Rect(0, 0, 512, 512), new Rect(20, 60, 100, 140), paint);

        //Log.d("BillboardMaker", "bitmap: " + icon.toString());

        paint.setTextSize(30);
        paint.setFakeBoldText(true);
        canvas.drawText(title, 120, 80, paint);

        paint.setFakeBoldText(false);
        paint.setTextSize(20);
        canvas.drawText(text, 120, 120, paint);

        billboard.setBitmap(bitmap);
        bitmap.recycle();
        icon.recycle();

        return billboard;
    }
}
