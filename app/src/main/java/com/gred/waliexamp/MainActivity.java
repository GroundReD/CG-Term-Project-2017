package com.gred.waliexamp;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.rajawali3d.surface.IRajawaliSurface;
import org.rajawali3d.surface.RajawaliSurfaceView;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    private static MediaPlayer mPlayer;
    Renderer renderer;
    FloatingActionButton fab;
    Boolean isBall = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPlayer = MediaPlayer.create(this, R.raw.battle);
        mPlayer.setLooping(true);
        mPlayer.start();
        final RajawaliSurfaceView surfaceView = new RajawaliSurfaceView(this);
        surfaceView.setFrameRate(60.0);
        surfaceView.setRenderMode(IRajawaliSurface.RENDERMODE_WHEN_DIRTY);

        surfaceView.setOnTouchListener(this);
        addContentView(surfaceView, new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT));

        renderer = new Renderer(this);
        surfaceView.setSurfaceRenderer(renderer);

    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menul, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();

        switch (id) {
            case R.id.menu_select:
                isBall = !isBall;
                if (isBall) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Ball Picking Mode", Toast.LENGTH_SHORT);
                    View view = toast.getView();
                    view.setBackgroundResource(android.R.color.transparent);
                    toast.setView(view);
                    toast.show();
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Camera Mode", Toast.LENGTH_SHORT);
                    View view = toast.getView();
                    view.setBackgroundResource(android.R.color.transparent);
                    toast.setView(view);
                    toast.show();
                }
        }
        return super.onOptionsItemSelected(menuItem);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.e("ball", "touch");
                (renderer).getObjectAt(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                (renderer).moveSelectedObject(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_UP:
                (renderer).stopMovingSelectedObject();
                break;
        }
        if (isBall) {
            return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlayer.stop();
    }
}
