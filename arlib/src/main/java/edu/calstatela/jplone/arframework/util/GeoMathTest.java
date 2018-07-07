package edu.calstatela.jplone.arframework.util;

public class GeoMathTest {

    static float[] pt1 = {0, 0, 0};
    static float[] pt2 = {0, 0, 5};
    static float[] pt3 = {5, 0, 5};
    static float[] pt4 = {5, 0, 0};

    public static void main(String[] args){

        addToPts(2200, 3577, -663);
//        float angle12 = GeoMath.xyzBearing(pt1, pt2);
//        float angle13 = GeoMath.xyzBearing(pt1, pt3);


        convertToLLA();
        float angle12 = GeoMath.llaBearing(pt1, pt2);
        float angle13 = GeoMath.llaBearing(pt1, pt3);

        System.out.println("Angle12: " + angle12);
        System.out.println("Angle13: " + angle13);

    }

    public static void addToPts(float x, float y, float z){
        pt1[0] += x; pt1[1] += y; pt1[2] += z;
        pt2[0] += x; pt2[1] += y; pt2[2] += z;
        pt3[0] += x; pt3[1] += y; pt3[2] += z;
        pt4[0] += x; pt4[1] += y; pt4[2] += z;
    }

    public static void convertToLLA(){
        float[] temp = new float[3];

        VectorMath.copyVec(pt1, temp, 3);
        System.out.println(VectorMath.vecToString(temp));
        GeoMath.xyzToLatLonAlt(temp, pt1);
        System.out.println(VectorMath.vecToString(pt1));

        VectorMath.copyVec(pt2, temp, 3);
        System.out.println(VectorMath.vecToString(temp));
        GeoMath.xyzToLatLonAlt(temp, pt2);
        System.out.println(VectorMath.vecToString(pt2));

        VectorMath.copyVec(pt3, temp, 3);
        System.out.println(VectorMath.vecToString(temp));
        GeoMath.xyzToLatLonAlt(temp, pt3);
        System.out.println(VectorMath.vecToString(pt3));

        VectorMath.copyVec(pt4, temp, 3);
        System.out.println(VectorMath.vecToString(temp));
        GeoMath.xyzToLatLonAlt(temp, pt4);
        System.out.println(VectorMath.vecToString(pt3));
    }
}
