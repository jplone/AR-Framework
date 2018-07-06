package edu.calstatela.jplone.arframework.matrix;

public class ViewMatrix {

    public ViewMatrix(){}

    public void setPosition(float x, float y, float z){}
    public void move(float dx, float dy, float dz){}
    public void slide(float dx, float dy, float dz){}

    public void pitch(float angle){}
    public void roll(float angle){}
    public void yaw(float angle){}

    public void setRotationQuaternion(float[] quaternion){}

    public void setLookAt(float[] position, float[] lookPos, float[] upVec){}

    public float[] getArray(){return null;}
}
