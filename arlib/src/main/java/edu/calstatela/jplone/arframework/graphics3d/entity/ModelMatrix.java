package edu.calstatela.jplone.arframework.graphics3d.entity;


import edu.calstatela.jplone.arframework.util.MatrixMath;
import edu.calstatela.jplone.arframework.util.VectorMath;

public class ModelMatrix {

    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      Private Member Variables
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
    private float[] position = new float[4];
    private float[] mMatrixArray = new float[16];

    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      Private Static Variables
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
    private static final String TAG = "waka-ModelMatrix";

    // temporary vectors for lookAt calculations
    private static float[] u = new float[3];
    private static float[] v = new float[3];
    private static float[] w = new float[3];
    private static float[] up = new float[3];

    // temporary matrices for lookAt calculations
    private static float[] rotationMatrix = new float[16];
    private static float[] translationMatrix = new float[16];



    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      Public Interface
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////
    public void lookAt(float eyeX, float eyeY, float eyeZ, float centerX, float centerY, float centerZ, float upX, float upY, float upZ){
        setPosition(eyeX, eyeY, eyeZ);

        // Find u, v, w axes of object (u == new x-axis, v == new y-axis, w == new z-axis)
            // w is Normalize(center - eye)
            // u is Normalize(up x w)
            // v is w x u
        w[0] = centerX - eyeX;
        w[1] = centerY - eyeY;
        w[2] = centerZ - eyeZ;

        if(w[0] == 0 && w[1] == 0 && w[2] == 0)
            w[1] = 1;
        else
            VectorMath.normalizeInPlace(w);

        up[0] = upX;
        up[1] = upY;
        up[2] = upZ;
        VectorMath.crossProduct(u, up, w);
        VectorMath.normalizeInPlace(u);

        VectorMath.crossProduct(v, w, u);

        // Use u, v, w to build matrix
            // [ ux  uy  uz  eyeX ]
            // [ vx  vy  vz  eyeY ]
            // [ wx  wy  wz  eyeZ ]
            // [ 0   0   0    1   ]
        mMatrixArray[0] = u[0];   mMatrixArray[4] = v[0];   mMatrixArray[8] = w[0];   mMatrixArray[12] = eyeX;
        mMatrixArray[1] = u[1];   mMatrixArray[5] = v[1];   mMatrixArray[9] = w[1];   mMatrixArray[13] = eyeY;
        mMatrixArray[2] = u[2];   mMatrixArray[6] = v[2];   mMatrixArray[10] = w[2];  mMatrixArray[14] = eyeZ;
        mMatrixArray[3] = 0;      mMatrixArray[7] = 0;      mMatrixArray[11] = 0;     mMatrixArray[15] = 1;
    }

    public void lookAt2(float eyeX, float eyeY, float eyeZ, float centerX, float centerY, float centerZ, float upX, float upY, float upZ){
        // Note: This is the original LookAt method.
        //  I am just keeping it around for now, just in case
        //  and will delete later.

        // Save the position
        setPosition(eyeX, eyeY, eyeZ);

        // Find u, v, w axes of object (u == new x-axis, v == new y-axis, w == new z-axis)
        // w is Normalize(center - eye)
        // u is Normalize(up x w)
        // v is w x u
        w[0] = centerX - eyeX;
        w[1] = centerY - eyeY;
        w[2] = centerZ - eyeZ;
        VectorMath.normalizeInPlace(w);

        up[0] = upX;
        up[1] = upY;
        up[2] = upZ;
        VectorMath.crossProduct(u, up, w);
        VectorMath.normalizeInPlace(u);

        VectorMath.crossProduct(v, w, u);

        // Use u, v, w to build rotation matrix
        // [ux  uy  uz  0]
        // [vx  vy  vz  0]
        // [wx  wy  wz  0]
        // [0   0   0   1]
        rotationMatrix[0] = u[0];   rotationMatrix[4] = v[0];   rotationMatrix[8] = w[0];   rotationMatrix[12] = 0;
        rotationMatrix[1] = u[1];   rotationMatrix[5] = v[1];   rotationMatrix[9] = w[1];   rotationMatrix[13] = 0;
        rotationMatrix[2] = u[2];   rotationMatrix[6] = v[2];   rotationMatrix[10] = w[2];  rotationMatrix[14] = 0;
        rotationMatrix[3] = 0;      rotationMatrix[7] = 0;      rotationMatrix[11] = 0;     rotationMatrix[15] = 1;


        // Use position to build translation matrix
        // [1   0   0   pos[0] ]
        // [0   1   0   pos[1] ]
        // [0   0   1   pos[2] ]
        // [0   0   0     1    ]
        translationMatrix[0] = 1;   translationMatrix[4] = 0;   translationMatrix[8] = 0;   translationMatrix[12] = position[0];
        translationMatrix[1] = 0;   translationMatrix[5] = 1;   translationMatrix[9] = 0;   translationMatrix[13] = position[1];
        translationMatrix[2] = 0;   translationMatrix[6] = 0;   translationMatrix[10] = 1;  translationMatrix[14] = position[2];
        translationMatrix[3] = 0;   translationMatrix[7] = 0;   translationMatrix[11] = 0;  translationMatrix[15] = 1;


        // matrix = translationMatrix * rotationMatrix
        MatrixMath.multiply2Matrices(mMatrixArray, translationMatrix, rotationMatrix);


        //      !!!!!!
        //
        //      Note: later... you should reconstruct this method to directly build the matrix,
        //      rather than building two matrices and multiplying them
        //
        //      !!!!!!
    }
    
    public void setPosition(float x, float y, float z){
        position[0] = x;
        position[1] = y;
        position[2] = z;
    }

    public float[] getPosition(){
        return position;
    }

    public void lookAtPoint(float centerX, float centerY, float centerZ){
        lookAt(position[0], position[1], position[2], centerX, centerY, centerZ, 0, 1, 0);
    }

    public void lookAtPointUpright(float centerX, float centerY, float centerZ){
        lookAt(position[0], position[1], position[2], centerX, position[1], centerZ, 0, 1, 0);
    }

    public float[] getModelMatrix(){
        return mMatrixArray;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      Add these methods later
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    // public void reset()
    // public void move(float x, float y, float z)
    // public void slide(float dx, float dy, float dz)
    // public void pitch(float angle)
    // public void yaw(float angle)
    // public void roll(float angle)
}
