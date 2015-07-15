package com.dealfaro.luca.serviceexample;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Random;

import de.greenrobot.event.EventBus;

/**
 * Created by luca on 7/5/2015.
 */
public class MyServiceTask implements Runnable {

    public static final String LOG_TAG = "MyService";
    private boolean running;
    private Context context;
    float accelx,accely;
    Calendar rightNow;
    long first_accel_time;
    long start_time;
    public static boolean moved=false;

    public MyServiceTask(Context _context) {
        context = _context;
        // Put here what to do at creation.

    }

    @Override
    public void run() {
        running = true;
        Random rand = new Random();

        //accelerometer
        ((SensorManager) context.getSystemService(Context.SENSOR_SERVICE)).registerListener(
                new SensorEventListener() {
                    @Override
                    public void onSensorChanged(SensorEvent event) {
                        // I hope I got the signs right.  If not, experiment with this.
                        accelx = -event.values[0];
                        accely = event.values[1];
                        //Log.i("accelx,accely", "x:" + String.valueOf(accelx) + "y:" + String.valueOf(accely));

                     //did it move code
                        rightNow=Calendar.getInstance();
                        start_time=rightNow.getTimeInMillis();
                        if(start_time-MainActivity.currentTime>30000){
                            if(Math.abs(accelx)>2||Math.abs(accely)>2&&moved==false){
                                first_accel_time=rightNow.getTimeInMillis();
                                moved=true;
                                Log.i("is the phone moved?","yes");
                            }
                            if(moved==true&&(rightNow.getTimeInMillis()-first_accel_time)>30000){
                                EventBus.getDefault().post("The phone moved");
                            }
                        }

                    }

                    @Override
                    public void onAccuracyChanged(Sensor sensor, int accuracy) {
                    } //ignore
                },
                ((SensorManager) context.getSystemService(Context.SENSOR_SERVICE))
                        .getSensorList(Sensor.TYPE_ACCELEROMETER).get(0), SensorManager.SENSOR_DELAY_GAME);


    }

    public void stopProcessing() {
        running = false;
    }

    public void setTaskState(boolean b) {
        // Do something with b.
    }


}
