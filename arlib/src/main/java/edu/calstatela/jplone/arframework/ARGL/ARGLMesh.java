package edu.calstatela.jplone.arframework.ARGL;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;


public class ARGLMesh {

    private FloatBuffer mBuffer = null;
    private float[] mColor = {0.0f, 0.8f, 0.0f, 0.1f};

    private int mShaderProgram;
    private int mNumVertices = 0;

    private static final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "uniform mat4 uMVPMatrix;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";

    private static final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    public ARGLMesh(){
        mShaderProgram = loadProgram(vertexShaderCode, fragmentShaderCode);
    }

    public void draw(){
        float[] matrix = new float[16];
        Matrix.setIdentityM(matrix, 0);
        draw(matrix);
    }

    public void draw(float[] MVPMatrix){
        if(mBuffer == null)
            return;

        GLES20.glUseProgram(mShaderProgram);

        int positionAttrib = GLES20.glGetAttribLocation(mShaderProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(positionAttrib);
        GLES20.glVertexAttribPointer(positionAttrib, 3, GLES20.GL_FLOAT, false, 3 * 4, mBuffer);

        int matrixUniform = GLES20.glGetUniformLocation(mShaderProgram, "uMVPMatrix");
        GLES20.glUniformMatrix4fv(matrixUniform, 1, false, MVPMatrix, 0);

        int colorUniform = GLES20.glGetUniformLocation(mShaderProgram, "vColor");
        GLES20.glUniform4fv(colorUniform, 1, mColor, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3); // 3 should be mNumVertices

        GLES20.glDisableVertexAttribArray(positionAttrib);
    }

    public void setColor(float[] rgbaVec){
        if(rgbaVec != null || rgbaVec.length == 4)
            mColor = rgbaVec;
    }



    public void loadTriangle(){
        float[] vertices = {
                -0.1f, -0.8f, 0f,
                0.1f, -0.8f, 0f,
                0f, 0.5f, 0f
        };

        loadVertices(vertices);
    }

    public void loadSquare(){
        float[] vertices = {
                -0.5f, -0.5f, 0f,
                0.5f, -0.5f, 0f,
                0.5f, 0.5f, 0f,
                -0.5f, -0.5f, 0f,
                0.5f, 0.5f, 0f,
                -0.5f, 0.5f, 0f
        };

        loadVertices(vertices);
    }

    public void loadVertices(float[] vertexList){

        mNumVertices = vertexList.length;
        ByteBuffer bb = ByteBuffer.allocateDirect(vertexList.length * 4);
        bb.order(ByteOrder.nativeOrder());
        mBuffer = bb.asFloatBuffer();
        mBuffer.put(vertexList);
        mBuffer.position(0);
    }

    private static int loadShader(int type, String code){
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, code);
        GLES20.glCompileShader(shader);
        return shader;
    }

    private static int loadProgram(String vertexCode, String fragmentCode){
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentCode);
        int program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);
        GLES20.glLinkProgram(program);
        return program;
    }

}
