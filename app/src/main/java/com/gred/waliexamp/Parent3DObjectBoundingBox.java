package com.gred.waliexamp;

import org.rajawali3d.Object3D;
import org.rajawali3d.bounds.BoundingBox;
import org.rajawali3d.math.vector.Vector3;

import java.nio.FloatBuffer;

/**
 * Created by gred on 2017. 6. 5..
 */

public class Parent3DObjectBoundingBox extends BoundingBox {

    public static BoundingBox createBoundingBox(Object3D root) {
        Vector3 min = new Vector3();
        Vector3 max = new Vector3();

        min.setAll(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
        max.setAll(-Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE);

        calFulLGeometry(root, min, max);

        Vector3[] tv = calculatePoints(min,max);
        BoundingBox temp = new BoundingBox(tv);

        return temp;
    }

    private static void calFulLGeometry(Object3D root, Vector3 min, Vector3 max) {
        for (int i = 0; i < root.getNumChildren(); i++) {
            if (root.getChildAt(i).getNumChildren() > 0)
                calFulLGeometry(root.getChildAt(i), min, max);
            else
                calGeometry(min, max, root.getChildAt(i));
        }
    }

    public static void calGeometry(Vector3 min, Vector3 max, Object3D obj) {
        FloatBuffer vertices = obj.getGeometry().getVertices();
        vertices.rewind();

        Vector3 vertex = new Vector3();

        while (vertices.hasRemaining()) {
            vertex.x = vertices.get();
            vertex.y = vertices.get();
            vertex.z = vertices.get();

            if (vertex.x < min.x) min.x = vertex.x;
            if (vertex.y < min.y) min.y = vertex.y;
            if (vertex.z < min.z) min.z = vertex.z;
            if (vertex.x > max.x) max.x = vertex.x;
            if (vertex.y > max.y) max.y = vertex.y;
            if (vertex.z > max.z) max.z = vertex.z;
        }
    }


    public static Vector3[] calculatePoints(Vector3 mMin, Vector3 mMax) {
        Vector3[] mPoints = new Vector3[8];
        for (int i = 0; i < 8; i++) {
            mPoints[i] = new Vector3();
        }

        // -- bottom plane
        // -- -x, -y, -z
        mPoints[0].setAll(mMin.x, mMin.y, mMin.z);
        // -- -x, -y,  z
        mPoints[1].setAll(mMin.x, mMin.y, mMax.z);
        // --  x, -y,  z
        mPoints[2].setAll(mMax.x, mMin.y, mMax.z);
        // --  x, -y, -z
        mPoints[3].setAll(mMax.x, mMin.y, mMin.z);

        // -- top plane
        // -- -x,  y, -z
        mPoints[4].setAll(mMin.x, mMax.y, mMin.z);
        // -- -x,  y,  z
        mPoints[5].setAll(mMin.x, mMax.y, mMax.z);
        // --  x,  y,  z
        mPoints[6].setAll(mMax.x, mMax.y, mMax.z);
        // --  x,  y, -z
        mPoints[7].setAll(mMax.x, mMax.y, mMin.z);

        return mPoints;
    }
}
