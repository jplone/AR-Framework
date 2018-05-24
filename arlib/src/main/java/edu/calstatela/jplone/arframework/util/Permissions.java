package edu.calstatela.jplone.arframework.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;


public class Permissions {
    public static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
    public static final String PERMISSION_WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public static final String PERMISSION_ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final int CAMERA_PERMISSION_REQUEST_CODE = 40279;
    public static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 40280;
    public static final int FINE_ACCESS_LOCATION_REQUEST_CODE = 40281;

    public static boolean havePermission(Context context, String permissionType){
        return ContextCompat.checkSelfPermission(context, permissionType) == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestPermission(Activity activity, String permissionType){
        int requestCode = 0;
        if(permissionType.equals(PERMISSION_CAMERA))
            requestCode = CAMERA_PERMISSION_REQUEST_CODE;
        else if(permissionType.equals(PERMISSION_WRITE_EXTERNAL_STORAGE))
            requestCode = WRITE_EXTERNAL_STORAGE_REQUEST_CODE;

        ActivityCompat.requestPermissions(activity, new String[] {permissionType}, requestCode);
    }
}
