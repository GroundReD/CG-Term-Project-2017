package com.gred.waliexamp;

import android.util.Log;

import org.rajawali3d.animation.TranslateAnimation3D;
import org.rajawali3d.math.vector.Vector3;

import java.util.Random;

/**
 * Created by gred on 2017. 6. 6..
 */

public class mTranslate extends TranslateAnimation3D {

    final double gravity = -98;
    double beginYVelocity;
    double yVelocity;
    double yPosition;

    public mTranslate(Vector3 toPosition, double beginYVelocity) {
        super(toPosition);
        this.beginYVelocity =beginYVelocity;
        yVelocity = beginYVelocity + gravity*mInterpolatedTime;
        yPosition = beginYVelocity * mInterpolatedTime + gravity *mInterpolatedTime *mInterpolatedTime /2;
    }

    public mTranslate(Vector3 fromPosition, Vector3 toPosition, double beginYVelocity) {
        super(fromPosition, toPosition);
        this.beginYVelocity =beginYVelocity;
        yVelocity = beginYVelocity + gravity*mInterpolatedTime;
        yPosition = beginYVelocity * mInterpolatedTime + gravity *mInterpolatedTime *mInterpolatedTime /2;
    }

    @Override
    protected void applyTransformation() {
        if (mDiffPosition == null)
            mDiffPosition = Vector3.subtractAndCreate(mToPosition, mFromPosition);
        Vector3 v = null;

        mMultipliedPosition.scaleAndSet(mDiffPosition, mInterpolatedTime);
        mAddedPosition.addAndSet(mFromPosition, mMultipliedPosition);


        if ( mAddedPosition.y + yPosition < 0.0) {
            yVelocity = -yVelocity;
            beginYVelocity = yVelocity;
            yPosition = beginYVelocity * mInterpolatedTime + gravity *mInterpolatedTime *mInterpolatedTime /2;
        } else {
            yVelocity = beginYVelocity + gravity*mInterpolatedTime;
            yPosition = beginYVelocity * mInterpolatedTime + gravity *mInterpolatedTime *mInterpolatedTime /2;

        }
        v = new Vector3(0, yPosition, 0);
        mAddedPosition.add(v);

//        mAddedPosition.add(v);

        mTransformable3D.setPosition(mAddedPosition);

        if (mInterpolatedTime == 1.0){
            yPosition = 0;

            if (mTransformable3D.getPosition().y < 0) {
                double x = mTransformable3D.getPosition().x;
                double z = mTransformable3D.getPosition().z;

                mTransformable3D.setPosition(x,0,z);
            }
//            yVelocity = 0;
//            beginYVelocity = 0;
        }

//        Log.d("v", yVelocity+"");
//        Log.d("y position", v.toString());
//        Log.d("added position", mAddedPosition.toString());
    }
}
