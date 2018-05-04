package edu.calstatela.jplone.arframework.ARGL.Billboard;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.FloatBuffer;

import edu.calstatela.jplone.arframework.ARGL.Unit.ARGLPosition;
import edu.calstatela.jplone.arframework.ARGL.Utils.ARGLBufferHelper;
import edu.calstatela.jplone.arframework.ARGL.Utils.ARGLShaderHelper;
import edu.calstatela.jplone.arframework.ARGL.Utils.ARGLTextureHelper;

/**
 * Created by bill on 11/14/17.
 */

public class ARGLBillboard {
    protected static boolean initialized = false;
    private static final String TAG = "waka_Billboard";

    private static final int BYTES_PER_FLOAT = 4;
    private static final int FLOATS_PER_VERTEX = 3;
    private static final int FLOATS_PER_COLOR = 4;
    private static final int FLOATS_PER_TEX_COORD = 2;

    private static FloatBuffer sVertexBuffer = null;
    private static FloatBuffer sTexCoordBuffer = null;
    private static FloatBuffer sColorBuffer = null;
    static int sGLProgramId = 0;

    private int mGLTextureId = 0;

    private float[] mMatrix = new float[16];

    private ARGLPosition position = null;

    public static void init(Context context){
        if(initialized) // make sure this method is called only once
            return;
        // Make sure shader is only loaded once
        if(!GLES20.glIsProgram(sGLProgramId))
//            sGLProgramId = ShaderHelper.buildShaderProgram(context, R.raw.vertex_shader, R.raw.fragment_shader);
            sGLProgramId = ARGLShaderHelper.buildShaderProgram(vertexShaderSource, fragmentShaderSource);

        // Make sure that buffers are only filled once
        if(sVertexBuffer == null)
            fillBuffers();

        initialized = true;
        Log.d("Billboard", "Shaders initialized!");
    }

    public static void destroy() {
        initialized = false;
    }

    public void setTexture(int glTextureId){
        // Make sure texture/bitmap is only set once per object
        if(mGLTextureId != 0)
            return;

        mGLTextureId = glTextureId;
    }

    public void setBitmap(Bitmap bitmap){
        // Make sure texture/bitmap is only set once per object
        if(mGLTextureId != 0)
            return;

        mGLTextureId = ARGLTextureHelper.glTextureFromBitmap(bitmap);
    }

    public float[] getMatrix(){
        return mMatrix;
    }

    public void setPosition(ARGLPosition position) {
        this.position = position;
        //Log.d("Billboard", "setPosition called");
        Matrix.setIdentityM(mMatrix, 0);
        Matrix.multiplyMM(mMatrix, 0, mMatrix, 0, position.getMatrix(), 0);
    }

    public ARGLPosition getPosition() {
        return this.position;
    }

    public void draw(){
        draw(mMatrix);
    }

    public void draw(float[] matrix){
        if(matrix == null || matrix.length != 16) {
            Log.d(TAG, "Billboard.draw() being called with improper mMatrix");
            return;
        }

        GLES20.glUseProgram(sGLProgramId);

        int positionAttribute = GLES20.glGetAttribLocation(sGLProgramId, "a_Position");
        GLES20.glEnableVertexAttribArray(positionAttribute);
        int colorAttribute = GLES20.glGetAttribLocation(sGLProgramId, "a_Color");
        GLES20.glEnableVertexAttribArray(colorAttribute);
        int texCoordAttribute = GLES20.glGetAttribLocation(sGLProgramId, "a_TexCoord");
        GLES20.glEnableVertexAttribArray(texCoordAttribute);

        int textureUniform = GLES20.glGetUniformLocation(sGLProgramId, "u_Texture");
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mGLTextureId);
        GLES20.glUniform1i(textureUniform, 0);

        int matrixUniform = GLES20.glGetUniformLocation(sGLProgramId, "u_Matrix");
        GLES20.glUniformMatrix4fv(matrixUniform, 1, false, matrix, 0);

        int vertexCount = rectangleVertexFloats.length / FLOATS_PER_VERTEX;
        GLES20.glVertexAttribPointer(positionAttribute, FLOATS_PER_VERTEX, GLES20.GL_FLOAT, false, FLOATS_PER_VERTEX * BYTES_PER_FLOAT, sVertexBuffer);
        GLES20.glVertexAttribPointer(colorAttribute, FLOATS_PER_COLOR, GLES20.GL_FLOAT, false, FLOATS_PER_COLOR * BYTES_PER_FLOAT, sColorBuffer);
        GLES20.glVertexAttribPointer(texCoordAttribute, FLOATS_PER_TEX_COORD, GLES20.GL_FLOAT, false, FLOATS_PER_TEX_COORD * BYTES_PER_FLOAT, sTexCoordBuffer);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);


        GLES20.glDisableVertexAttribArray(positionAttribute);
        GLES20.glDisableVertexAttribArray(colorAttribute);
        GLES20.glDisableVertexAttribArray(texCoordAttribute);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //          Drawing Helpers
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static void fillBuffers(){
        sVertexBuffer = ARGLBufferHelper.arrayToBuffer(rectangleVertexFloats);
        sColorBuffer = ARGLBufferHelper.arrayToBuffer(rectangleColorFloats);
        sTexCoordBuffer = ARGLBufferHelper.arrayToBuffer(rectangleTexCoordFloats);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //          Static Mesh Data
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////


    private static final float[] rectangleVertexFloats = {
            -0.5f,  -0.5f,  0.0f,
            0.5f,   -0.5f,  0.0f,
            0.5f,   0.5f,   0.0f,

            -0.5f,  -0.5f,  0.0f,
            0.5f,   0.5f,   0.0f,
            -0.5f,  0.5f,   0.0f
    };

    private static final float[] rectangleColorFloats = {
            1.0f, 0.0f, 0.0f, 1.0f,
            0.0f, 1.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f,

            1.0f, 0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f,
            0.2f, 0.5f, 0.0f, 1.0f
    };

    private static final float[] rectangleTexCoordFloats = {
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,

            0.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f
    };

    private static final String vertexShaderSource =
            "attribute vec4 a_Position;                 \n" +
                    "attribute vec4 a_Color;                    \n" +
                    "attribute vec2 a_TexCoord;                 \n" +
                    "                                           \n" +
                    "uniform mat4 u_Matrix;                     \n" +
                    "                                           \n" +
                    "varying vec2 v_TexCoord;                   \n" +
                    "varying vec4 v_Color;                      \n" +
                    "                                           \n" +
                    "void main()                                \n" +
                    "{                                          \n" +
                    "    gl_Position = u_Matrix * a_Position;   \n" +
                    "   v_Color = a_Color;                      \n" +
                    "   v_TexCoord = a_TexCoord;                \n" +
                    "}                                          \n";

    private static final String fragmentShaderSource =
            "precision mediump float;                                               \n" +
                    "                                                                       \n" +
                    "uniform sampler2D u_Texture;                                           \n" +
                    "varying vec4 v_Color;                                                  \n" +
                    "varying vec2 v_TexCoord;                                               \n" +
                    "                                                                       \n" +
                    "void main()                                                            \n" +
                    "{                                                                      \n" +
                    "   gl_FragColor = texture2D(u_Texture, v_TexCoord) + 0.3 * v_Color;    \n" +
                    "}                                                                      \n";
}
