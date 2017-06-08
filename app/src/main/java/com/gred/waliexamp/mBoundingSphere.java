package com.gred.waliexamp;

import org.rajawali3d.bounds.BoundingBox;
import org.rajawali3d.bounds.BoundingSphere;
import org.rajawali3d.bounds.IBoundingVolume;
import org.rajawali3d.math.vector.Vector3;

/**
 * Created by gred on 2017. 6. 6..
 */

public class mBoundingSphere extends BoundingSphere {
//    @Override
//    public boolean intersectsWith(IBoundingVolume boundingVolume) {
//        BoundingSphere boundingSphere = null;
//        BoundingBox boundingBox = null;
//
//        if(!(boundingVolume instanceof BoundingSphere)) {
//            boundingBox = (BoundingBox)boundingVolume;
//
//            Vector3 otherMin = boundingBox.getTransformedMin();
//            Vector3 otherMax = boundingBox.getTransformedMax();
//            Vector3 min = mTransformedMin;
//            Vector3 max = mTransformedMax;
//
//            return (min.x < otherMax.x) && (max.x > otherMin.x) &&
//                    (min.y < otherMax.y) && (max.y > otherMin.y) &&
//                    (min.z < otherMax.z) && (max.z > otherMin.z);
//
//        } else {
//            boundingSphere = (BoundingSphere) boundingVolume;
//            mTmpPos.setAll(mPosition);
//            mTmpPos.subtract(boundingSphere.getPosition());
//
//            mDist = mTmpPos.x * mTmpPos.x + mTmpPos.y * mTmpPos.y + mTmpPos.z * mTmpPos.z;
//            mMinDist = mRadius * mScale + boundingSphere.getRadius() * boundingSphere.getScale();
//
//            return mDist < mMinDist * mMinDist;
//        }
//    }
}
