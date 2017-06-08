package com.gred.waliexamp;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.LinearInterpolator;

import org.rajawali3d.Object3D;
import org.rajawali3d.animation.Animation;
import org.rajawali3d.animation.Animation3D;
import org.rajawali3d.animation.AnimationGroup;
import org.rajawali3d.animation.EllipticalOrbitAnimation3D;
import org.rajawali3d.animation.RotateOnAxisAnimation;
import org.rajawali3d.animation.ScaleAnimation3D;
import org.rajawali3d.bounds.IBoundingVolume;
import org.rajawali3d.cameras.ArcballCamera;
import org.rajawali3d.cameras.Camera;
import org.rajawali3d.cameras.ChaseCamera;
import org.rajawali3d.lights.DirectionalLight;
import org.rajawali3d.lights.PointLight;
import org.rajawali3d.loader.LoaderOBJ;
import org.rajawali3d.loader.ParsingException;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.methods.DiffuseMethod;
import org.rajawali3d.materials.methods.SpecularMethod;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.Texture;
import org.rajawali3d.math.Matrix4;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Cube;
import org.rajawali3d.primitives.Sphere;
import org.rajawali3d.renderer.RajawaliRenderer;
import org.rajawali3d.scene.RajawaliScene;
import org.rajawali3d.scenegraph.IGraphNode;
import org.rajawali3d.util.GLU;
import org.rajawali3d.util.ObjectColorPicker;
import org.rajawali3d.util.OnObjectPickedListener;
import org.rajawali3d.util.debugvisualizer.DebugVisualizer;
import org.rajawali3d.util.debugvisualizer.GridFloor;
import java.util.ArrayList;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by gred on 2017. 6. 2..
 */

public class Renderer extends RajawaliRenderer implements OnObjectPickedListener {

    public Context context;
    Parent3DObjectBoundingBox parent3DObjectBoundingBox;
    private DirectionalLight mDirectionalLight;
    private PointLight mPointLight;
    private Sphere pokeballSphere;
    private Cube cube;
    private Vector3 ballVector;
    private GridFloor gridFloor;
    private boolean mBoxIntersect = false;

    //snorlax obj
    private Object3D snorlaxObj;

    private Animation3D mCameraAnim, mLightAnim;
    private mTranslate mBallAnim;
    private Animation3D mScaleSnorlaxAnim, mScalePokeBallAnim;
    private ArrayList<Animation3D> mBallAnimArray;
    private ArrayList<IBoundingVolume> mVolumeArray;

    //object picker
    private ObjectColorPicker mPicker;
    private Object3D mSelectedObject;
    private int[] mViewport;
    private double[] mNearPos4;
    private double[] mFarPos4;
    private Vector3 mNearPos;
    private Vector3 mFarPos;
    private Vector3 mNewObjPos;
    private Matrix4 mViewMatrix;
    private Matrix4 mProjectionMatrix;

    //scene
    ChaseCamera chaseCamera;

    public Renderer(Context context) {
        super(context);
        this.context = context;
        setFrameRate(60);
    }

    @Override
    protected void initScene() {

        mViewport = new int[] { 0, 0, getViewportWidth(), getViewportHeight() };
        mNearPos4 = new double[4];
        mFarPos4 = new double[4];
        mNearPos = new Vector3();
        mFarPos = new Vector3();
        mNewObjPos = new Vector3();
        mPicker = new ObjectColorPicker(this);
        mPicker.setOnObjectPickedListener(this);

        mDirectionalLight = new DirectionalLight(1f, .2f, -1.0f);
        mDirectionalLight.setColor(1.0f, 1.0f, 1.0f);
        mDirectionalLight.setPower(2);
        getCurrentScene().addLight(mDirectionalLight);

        mPointLight = new PointLight();
        mPointLight.setPosition(0, 0, 30);
        mPointLight.setColor(1, 1, 1);
        mPointLight.setPower(3);

        getCurrentScene().addLight(mPointLight);

        ArcballCamera arcball = new ArcballCamera(mContext, ((Activity) mContext).findViewById(R.id.main_layout));
        arcball.setPosition(0, 60, 60);
        arcball.setFarPlane(1000);
        getCurrentScene().replaceAndSwitchCamera(getCurrentCamera(), arcball);

        getCurrentCamera().setPosition(0, 20, 100);
        getCurrentCamera().setFarPlane(1000);

        try {
            getCurrentScene().setSkybox(R.drawable.posx,R.drawable.negx,R.drawable.posy,R.drawable.negy
                    ,R.drawable.posz, R.drawable.negz);
        } catch (ATexture.TextureException e) {
            e.printStackTrace();
            Log.e("oom","oom");
        }
//
        DebugVisualizer debugViz = new DebugVisualizer(this);
        gridFloor = new GridFloor(100);
        debugViz.addChild(gridFloor);
        getCurrentScene().addChild(debugViz);

        //draw pokebmon all
        Material ballMaterial = new Material();

        ballMaterial.enableLighting(true);
        ballMaterial.setDiffuseMethod(new DiffuseMethod.Lambert());
        ballMaterial.setColor(0);

        Texture ballTexture = new Texture("Earth", R.drawable.pokeball);
        try {
            ballMaterial.addTexture(ballTexture);

        } catch (ATexture.TextureException error) {
            Log.d("DEBUG", "TEXTURE ERROR");
        }

        pokeballSphere = new Sphere(1, 20, 20);
        pokeballSphere.setMaterial(ballMaterial);
        pokeballSphere.setPosition(0, 5, 40);
//        pokeballSphere.setShowBoundingVolume(true);

        mCameraAnim = new RotateOnAxisAnimation(Vector3.Axis.Y, 360);
        mCameraAnim.setDurationMilliseconds(2000);
        mCameraAnim.setRepeatMode(Animation.RepeatMode.INFINITE);
        mCameraAnim.setTransformable3D(pokeballSphere);

        pokeballSphere.setRenderChildrenAsBatch(true);
        mPicker.registerObject(pokeballSphere);
        getCurrentScene().addChild(pokeballSphere);

        mBallAnimArray = new ArrayList<>();
        mVolumeArray = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Object3D object3D = pokeballSphere.clone(true);
            object3D.setPosition(0, 5, 40);
//            object3D.setShowBoundingVolume(true);

            IBoundingVolume boundBall = object3D.getGeometry().getBoundingSphere();
            boundBall.transform(object3D.getModelMatrix());
            mVolumeArray.add(boundBall);

            Random random = new Random();
            // random velocity and random position
            double velocity = random.nextDouble() * 70 + 1;
            double xPosition = (random.nextDouble() * 55) - 27;   // x -26~26
            double zPosition = (random.nextDouble() * 30) - 15;    // z -14~14

            Animation3D mBallAnim = new mTranslate(new Vector3(xPosition, 0, zPosition), velocity);
            mBallAnim.setDurationMilliseconds(1000);
            mBallAnim.setRepeatMode(Animation.RepeatMode.NONE);
            mBallAnim.setTransformable3D(object3D);


            mBallAnimArray.add(mBallAnim);
            mPicker.registerObject(object3D);
            getCurrentScene().addChild(object3D);
        }

        Random random = new Random();
        // random velocity and random position
        double velocity = 60;
        double xPosition = 0;   // x -26~26
        double zPosition = -10;    // z -14~14


        mBallAnim = new mTranslate(new Vector3(xPosition, 0, zPosition), velocity);
        mBallAnim.setDurationMilliseconds(2000);
        mBallAnim.setRepeatMode(Animation.RepeatMode.NONE);
        mBallAnim.setTransformable3D(pokeballSphere);

        // draw snorlax
        Material snorlaxMaterial = new Material();
        snorlaxMaterial.enableLighting(true);
        snorlaxMaterial.setDiffuseMethod(new DiffuseMethod.Lambert());
        snorlaxMaterial.setSpecularMethod(new SpecularMethod.Phong());

        Texture mTextureDh = new Texture("snorlax", R.drawable.kabigondh);
        Texture mTextureMouthDh = new Texture("snorlaxmouth", R.drawable.kabigonmouthdh);
        Texture mTextureEyeDh = new Texture("snorlax", R.drawable.kabigoneyedh);

        try {
            snorlaxMaterial.addTexture(mTextureDh);
            snorlaxMaterial.addTexture(mTextureMouthDh);
            snorlaxMaterial.addTexture(mTextureEyeDh);

        } catch (ATexture.TextureException error) {
            Log.d("DEBUG", "TEXTURE ERROR");
        }


        LoaderOBJ objParser = new LoaderOBJ(mContext.getResources(), mTextureManager, R.raw.snorlax_obj);

        try {
            objParser.parse();
            snorlaxObj = objParser.getParsedObject();
            snorlaxObj.setMaterial(snorlaxMaterial);
            snorlaxObj.setPosition(0, 0, -10);
//            snorlaxObj.setShowBoundingVolume(true);
            getCurrentScene().addChild(snorlaxObj);

        } catch (ParsingException e) {
            e.printStackTrace();
        }

        Material cubeMaterial = new Material();
        cubeMaterial.setColor(0);

        cube = new Cube(10.5f);
        cube.setPosition(0, 10.5f, -10);
        cube.setTransparent(true);
//        cube.setShowBoundingVolume(true);
        cube.setMaterial(cubeMaterial);
        getCurrentScene().addChild(cube);

        mLightAnim = new EllipticalOrbitAnimation3D(new Vector3(),
                new Vector3(0, 10, 0), Vector3.getAxisVector(Vector3.Axis.Z), 0,
                360, EllipticalOrbitAnimation3D.OrbitDirection.CLOCKWISE);

        mLightAnim.setDurationMilliseconds(3000);
        mLightAnim.setRepeatMode(Animation.RepeatMode.NONE);
        mLightAnim.setTransformable3D(mPointLight);

        getCurrentScene().registerAnimation(mCameraAnim);
        getCurrentScene().registerAnimation(mBallAnim);
        getCurrentScene().registerAnimation(mLightAnim);
        for (int i = 0 ; i<10 ;i++) {
            getCurrentScene().registerAnimation(mBallAnimArray.get(i));
        }
//
//        mCameraAnim.play();
//        mBallAnim.play();
//        mLightAnim.play();
//        for (int i = 0 ; i<10 ;i++) {
//            mBallAnimArray.get(i).setDelayMilliseconds(3000*(i+1));
//            mBallAnimArray.get(i).play();
//        }

        mViewMatrix = getCurrentCamera().getViewMatrix();
        mProjectionMatrix = getCurrentCamera().getProjectionMatrix();

        chaseCamera = new ChaseCamera(new Vector3(0, 3, 16));
        // -- tell the camera which object to chase
        chaseCamera.setLinkedObject(pokeballSphere);
        // -- set the far plane to 1000 so that we actually see the sky sphere
        chaseCamera.setFarPlane(1000);


        mScaleSnorlaxAnim = new ScaleAnimation3D(new Vector3(0.01,0.01,0.01));
        mScaleSnorlaxAnim.setInterpolator(new LinearInterpolator());
        mScaleSnorlaxAnim.setDurationMilliseconds(2000);
        mScaleSnorlaxAnim.setRepeatMode(Animation.RepeatMode.NONE);
        mScaleSnorlaxAnim.setTransformable3D(snorlaxObj);

        getCurrentScene().registerAnimation(mScaleSnorlaxAnim);


        mScalePokeBallAnim = new ScaleAnimation3D(new Vector3(4,4,4));
        mScalePokeBallAnim.setInterpolator(new LinearInterpolator());
        mScalePokeBallAnim.setDurationMilliseconds(2000);
        mScalePokeBallAnim.setRepeatMode(Animation.RepeatMode.NONE);
        mScalePokeBallAnim.setTransformable3D(pokeballSphere);
        getCurrentScene().registerAnimation(mScalePokeBallAnim);


    }


    @Override
    protected void onRender(long ellapsedRealtime, double deltaTime) {
        super.onRender(ellapsedRealtime, deltaTime);

        IBoundingVolume boundBall = pokeballSphere.getGeometry().getBoundingSphere();
        boundBall.transform(pokeballSphere.getModelMatrix());


        IBoundingVolume boundBox = cube.getGeometry().getBoundingSphere();
        boundBox.transform(cube.getModelMatrix());

//        IBoundingVolume boundGrid = gridFloor.getGeometry().getBoundingBox();

        mBoxIntersect = boundBall.intersectsWith(boundBox);
        Boolean mBoxIntersect1 = mVolumeArray.get(0).intersectsWith(boundBox);
        Boolean mBoxIntersect2 = mVolumeArray.get(1).intersectsWith(boundBox);
        Boolean mBoxIntersect3 = mVolumeArray.get(2).intersectsWith(boundBox);
        Boolean mBoxIntersect4 = mVolumeArray.get(3).intersectsWith(boundBox);
        Boolean mBoxIntersect5 = mVolumeArray.get(4).intersectsWith(boundBox);
        Boolean mBoxIntersect6 = mVolumeArray.get(5).intersectsWith(boundBox);
        Boolean mBoxIntersect7 = mVolumeArray.get(6).intersectsWith(boundBox);
        Boolean mBoxIntersect8 = mVolumeArray.get(7).intersectsWith(boundBox);
        Boolean mBoxIntersect9 = mVolumeArray.get(8).intersectsWith(boundBox);
        Boolean mBoxIntersect10 = mVolumeArray.get(9).intersectsWith(boundBox);


        if (mBoxIntersect) {
            Log.e("collision", mBoxIntersect + "");
            mBallAnim.pause();
            snorlaxObj.setY(10);
            mScaleSnorlaxAnim.play();
            mScalePokeBallAnim.play();

        } else if (mBoxIntersect1) {
            Log.e("collision", mBoxIntersect + "");
            getCurrentScene().setBackgroundColor(0xff00bfff);

        } else if (mBoxIntersect2) {
            Log.e("collision", mBoxIntersect + "");
            getCurrentScene().setBackgroundColor(0xff00bfff);

        } else if (mBoxIntersect3) {
            Log.e("collision", mBoxIntersect + "");
            getCurrentScene().setBackgroundColor(0xff00bfff);

        } else if (mBoxIntersect4) {
            Log.e("collision", mBoxIntersect + "");
            getCurrentScene().setBackgroundColor(0xff00bfff);

        } else if (mBoxIntersect5) {
            Log.e("collision", mBoxIntersect + "");
            getCurrentScene().setBackgroundColor(0xff00bfff);

        } else if (mBoxIntersect6) {
            Log.e("collision", mBoxIntersect + "");
            getCurrentScene().setBackgroundColor(0xff00bfff);

        } else if (mBoxIntersect7) {
            Log.e("collision", mBoxIntersect + "");
            getCurrentScene().setBackgroundColor(0xff00bfff);

        } else if (mBoxIntersect8) {
            Log.e("collision", mBoxIntersect + "");
            getCurrentScene().setBackgroundColor(0xff00bfff);

        } else if (mBoxIntersect9) {
            Log.e("collision", mBoxIntersect + "");
            getCurrentScene().setBackgroundColor(0xff00bfff);

        } else if (mBoxIntersect10) {
            Log.e("collision", mBoxIntersect + "");
            getCurrentScene().setBackgroundColor(0xff00bfff);

        } else {
            Log.e("collision", mBoxIntersect + "");
        }

//        mCameraAnim.play();
//        mLightAnim.play();
//        for (int i = 0 ; i<10 ;i++) {
//            mBallAnimArray.get(i).setDelayMilliseconds(3000*(i+1));
//            mBallAnimArray.get(i).play();
//        }
//        mBallAnim.setDelayMilliseconds(33000);
//        mBallAnim.play();

        mCameraAnim.play();
        mLightAnim.play();
    }

    @Override
    public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {

    }

    @Override
    public void onTouchEvent(MotionEvent event) {

    }


    public void onRenderSurfaceSizeChanged(GL10 gl, int width, int height) {
        super.onRenderSurfaceSizeChanged(gl, width, height);
        mViewport[2] = getViewportWidth();
        mViewport[3] = getViewportHeight();
        mViewMatrix = getCurrentCamera().getViewMatrix();
        mProjectionMatrix = getCurrentCamera().getProjectionMatrix();
    }

    public void getObjectAt(float x, float y) {
        mPicker.getObjectAt(x, y);
    }

    @Override
    public void onObjectPicked(Object3D object) {
        mSelectedObject = object;
//        Log.d("touch",mSelectedObject.getName());
    }

    public void moveSelectedObject(float x, float y) {
        if (mSelectedObject == null)
            return;

        //
        // -- unproject the screen coordinate (2D) to the camera's near plane
        //

        GLU.gluUnProject(x, getViewportHeight() - y, 0, mViewMatrix.getDoubleValues(), 0,
                mProjectionMatrix.getDoubleValues(), 0, mViewport, 0, mNearPos4, 0);

        //
        // -- unproject the screen coordinate (2D) to the camera's far plane
        //

        GLU.gluUnProject(x, getViewportHeight() - y, 1.f, mViewMatrix.getDoubleValues(), 0,
                mProjectionMatrix.getDoubleValues(), 0, mViewport, 0, mFarPos4, 0);

        //
        // -- transform 4D coordinates (x, y, z, w) to 3D (x, y, z) by dividing
        // each coordinate (x, y, z) by w.
        //

        mNearPos.setAll(mNearPos4[0] / mNearPos4[3], mNearPos4[1]
                / mNearPos4[3], mNearPos4[2] / mNearPos4[3]);
        mFarPos.setAll(mFarPos4[0] / mFarPos4[3],
                mFarPos4[1] / mFarPos4[3], mFarPos4[2] / mFarPos4[3]);

        //
        // -- now get the coordinates for the selected object
        //

        double factor = (Math.abs(mSelectedObject.getZ()) + mNearPos.z)
                / (getCurrentCamera().getFarPlane() - getCurrentCamera()
                .getNearPlane());

        mNewObjPos.setAll(mFarPos);
        mNewObjPos.subtract(mNearPos);
        mNewObjPos.multiply(factor);
        mNewObjPos.add(mNearPos);

        mSelectedObject.setX(mNewObjPos.x);
        mSelectedObject.setY(mNewObjPos.y);
    }

    public void stopMovingSelectedObject() {
        mSelectedObject = null;
        throwObject();
    }

    public void throwObject() {

        for (int i = 0 ; i<10 ;i++) {
            if ( i == 0 && !mBallAnimArray.get(0).isEnded()) {
                mBallAnimArray.get(i).play();
            }
            else if (i > 0 && mBallAnimArray.get(i-1).isEnded()) {
//                mBallAnimArray.get(i).setDelayMilliseconds(3000 * (i + 1));
                if (!mBallAnimArray.get(i).isEnded()) {
                    mBallAnimArray.get(i).play();
                }
            }
        }
        if (mBallAnimArray.get(9).isEnded()) {
//            mBallAnim.setDelayMilliseconds(33000);
            mBallAnim.setDelayMilliseconds(1000);
            mBallAnim.play();
            getCurrentScene().replaceAndSwitchCamera(chaseCamera, 0);
        }

    }
}
