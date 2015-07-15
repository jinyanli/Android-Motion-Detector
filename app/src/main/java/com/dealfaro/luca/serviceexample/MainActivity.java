package com.dealfaro.luca.serviceexample;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.dealfaro.luca.serviceexample.MyService.MyBinder;

import java.util.Calendar;
import java.util.Date;

import de.greenrobot.event.EventBus;

public class MainActivity extends ActionBarActivity {

    public static final int DISPLAY_NUMBER = 10;
    private Handler mUiHandler;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private Rect mSurfaceSize;
    Calendar rightNow;
    //the app start time
    public static long currentTime;
    private static final String LOG_TAG = "MainActivity";

    // Service connection variables.
    private boolean serviceBound;
    private MyService myService;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        serviceBound = false;

        //get app start time
        rightNow = Calendar.getInstance();
        currentTime=rightNow.getTimeInMillis();

         //start the service
          Intent intent = new Intent(this, MyService.class);
          startService(intent);
          bindMyService();



        Log.i("current time", String.valueOf(rightNow.getTimeInMillis()));
        // Prevents the screen from dimming and going to sleep.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);



    }

    @Override
    protected void onResume() {
        super.onResume();
        // Starts the service, so that the service will only stop when explicitly stopped.

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    private void bindMyService() {
        // We are ready to show images, and we should start getting the bitmaps
        // from the motion detection service.
        // Binds to the service.
        Log.i(LOG_TAG, "Starting the service");
        Intent intent = new Intent(this, MyService.class);
        Log.i("LOG_TAG", "Trying to bind");
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }


    // Service connection code.
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder serviceBinder) {
            // We have bound to the service.
            MyBinder binder = (MyBinder) serviceBinder;
            myService = binder.getService();
            serviceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            serviceBound = false;
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        /*
        if (serviceBound) {
            Log.i("MyService", "Unbinding");
            unbindService(serviceConnection);
            serviceBound = false;
            // If we like, stops the service.

            if (true) {
                Log.i(LOG_TAG, "Stopping.");
                Intent intent = new Intent(this, MyService.class);
                stopService(intent);
                Log.i(LOG_TAG, "Stopped.");
            }

        }

        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
       */
    }

    //clear button
    public void clear(View v){
        TextView tv = (TextView) findViewById(R.id.MovedView);
        tv.setText("Everything was quite");

        //update current time
        rightNow=Calendar.getInstance();
        currentTime=rightNow.getTimeInMillis();

        MyServiceTask.moved=false;
        Log.i("current time", String.valueOf(currentTime));
    }

    //exit button
    public void exit(View v){
        Log.i("MyService", "Unbinding");

        //quite the service thread and app
        unbindService(serviceConnection);
        serviceBound = false;
        Log.i(LOG_TAG, "Stopping.");
        Intent intent = new Intent(this, MyService.class);
        stopService(intent);
        Log.i(LOG_TAG, "Stopped.");
        finish();
    }

    //event bus for receiving message
    public void onEventMainThread(String result) {
        //Log.i(LOG_TAG, "Displaying: " + result);
        TextView tv = (TextView) findViewById(R.id.MovedView);
        tv.setText(result);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
