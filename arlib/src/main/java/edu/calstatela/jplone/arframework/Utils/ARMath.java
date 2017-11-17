package edu.calstatela.jplone.arframework.Utils;

/**
 * Created by bill on 11/2/17.
 */

public class ARMath {
    private static final String TAG = "ARMath";

    public static float[] crossProduct(float[] a, float[] b){
        float[] c = new float[3];

        c[0] = a[1] * b[2] - a[2] * b[1];
        c[1] = a[2] * b[0] - a[0] * b[2];
        c[2] = a[0] * b[1] - a[1] * b[0];

        return c;
    }

    public static float dotProduct(float[] a, float[] b){
        return a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
    }

    public static float magnitude(float[] vec){
        return (float) Math.sqrt(vec[0] * vec[0] + vec[1] * vec[1] + vec[2] * vec[2]);
    }

    public static float angle(float[] a, float[] b){
        float unitDotProduct = dotProduct(a, b) / magnitude(a) / magnitude(b);
        float angle = (float)Math.acos(unitDotProduct);

        return angle;
    }

    public static float[] normalize(float[] vec){
        float[] temp = new float[3];
        float mag = magnitude(vec);

        temp[0] = vec[0] / mag;
        temp[1] = vec[1] / mag;
        temp[2] = vec[2] / mag;

        return temp;
    }

    public static float radToDegrees(float radians){
        return (float)(radians / 2 / Math.PI * 360);
    }

    public static float degreesToRad(float degrees){
        return (float) (degrees / 360.0 * 2 * Math.PI);
    }

    public static float compassBearing(float[] gravityVec, float[] magnetVec, float[] cameraVec){
        // Information we have:
        //  + gravityVec: A vector representing the direction and magnitude of gravity in camera coordinate system
        //  + magnetVec: A vector representing the direction and magnitude of earth's magnetic field from camera coordinate system
        //  + cameraVec: A vector representing the direction of the camera in phone coordinate system

        // Algorithm for finding compass bearing:
        //  * Project magnetVec onto earth's xz plane -> xzMagnet
        //  * Project cameraVec onto earth's xz plane -> xzCamera
        //  * Use dot product to find angle between xzMagnet and xzCamera

        if(gravityVec[2] >= 9.0)
            cameraVec = new float[]{0, 1, 0};
        else if(gravityVec[2] <= -9.0)
            cameraVec = new float[]{0, -1, 0};

        float[] xzTemp = crossProduct(magnetVec, gravityVec);
        float[] xzMagnet = crossProduct(gravityVec, xzTemp);

        xzTemp = crossProduct(cameraVec, gravityVec);
        float[] xzCamera = crossProduct(gravityVec, xzTemp);

        float angle = angle(xzCamera, xzMagnet);
        angle = radToDegrees(angle);

        float[] xproduct = crossProduct(xzMagnet, xzCamera);
        float direction = dotProduct(xproduct, gravityVec);

        if(direction >=0)
            return angle;
        else
            return 360 - angle;
    }

    public static String vec2String(float[] vec){
        StringBuilder sb = new StringBuilder();
        sb.append("Vec: (");
        sb.append(vec[0]);
        for(int i = 1; i < vec.length; i++){
            sb.append(", ");
            sb.append(vec[i]);
        }
        sb.append(")");
        return sb.toString();
    }

    public static void copyVec(float[] src, float[] dest, int n){
        for(int i = 0; i < n; i++)
            dest[i] = src[i];
    }

    public static float landscapeTiltAngle(float[] gravityVec, float[] phoneUpVec){
        float[] xyGravityVec = {gravityVec[0], gravityVec[1], 0};
        float[] phoneFrontVec = {0, 0, -1};
        float unitDotProduct = dotProduct(phoneUpVec, xyGravityVec) / magnitude(xyGravityVec) / magnitude(phoneUpVec);
        float angle = (float)Math.acos(unitDotProduct);
        angle = radToDegrees(angle);
        float direction = dotProduct(crossProduct(phoneUpVec, xyGravityVec), phoneFrontVec);

        if(direction >= 0){
            return angle;
        }
        else
            return 360 - angle;
    }

    public static float portraitTiltAngle(float[] gravityVec, float[] magnetVec) {
        float[] zyGravityVec = {0, gravityVec[1], gravityVec[2]};
        float[] phoneUpVec = {0, -1, 0};
        float unitDotProduct = dotProduct(phoneUpVec, zyGravityVec) / magnitude(zyGravityVec) / magnitude(phoneUpVec);
        float angle = (float)Math.acos(unitDotProduct);
        angle = radToDegrees(angle * 2);
        float direction = dotProduct(crossProduct(phoneUpVec, zyGravityVec), new float[]{1, 0, 0});

        if(direction >= 0) {
            return angle;
        }
        else
            return 360 - angle;
    }

    // taken from http://blog.thomnichols.org/2011/08/smoothing-sensor-data-with-a-low-pass-filter

    /*
    * time smoothing constant for low-pass filter
    * 0 ≤ alpha ≤ 1 ; a smaller value basically means more smoothing
    * See: http://en.wikipedia.org/wiki/Low-pass_filter#Discrete-time_realization
    */
    static final float ALPHA = 0.09f;

    /**
     * @see http://en.wikipedia.org/wiki/Low-pass_filter#Algorithmic_implementation
     * @see http://developer.android.com/reference/android/hardware/SensorEvent.html#values
     */
    public static float[] lowPass( float[] input, float[] output ) {
        if ( output == null ) return input;

        for ( int i=0; i<input.length; i++ ) {
            output[i] = (float)((input[i] * ALPHA) + (output[i] * (1.0 - ALPHA)));
        }

        return output;
    }
}
