package com.example.expensestracker;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.view.Window;
import android.view.WindowManager;

public class MySensorEventListener implements SensorEventListener {

    public static float []  mLightSensor = null;

    private WindowManager.LayoutParams layoutParams;
    private Window window;

    public MySensorEventListener(Window window){

        layoutParams = window.getAttributes();
        this.window = window;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        float lightValue = event.values[0];

        if (lightValue < 20) {
            // Brightness is less than 20
            layoutParams.screenBrightness = 0;
            window.setAttributes(layoutParams);
            window.getDecorView().setBackgroundColor(Color.rgb(132,147,169));
        } else {
//            if (lightValue >= 10 && lightValue < 100) {
            // Brightness is greater than 20
            layoutParams.screenBrightness = 1;
            window.setAttributes(layoutParams);
            window.getDecorView().setBackgroundColor(Color.rgb(199,219,248));
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
