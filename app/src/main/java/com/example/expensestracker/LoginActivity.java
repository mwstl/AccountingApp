package com.example.expensestracker;

import static android.graphics.Color.rgb;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity implements SensorEventListener {


    EditText name, psd;
    Button loginBtn;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private SensorManager sensorManager;
    private Sensor lightSensor;
    Thread vibrateThread;
    private Vibrator vibrator;
    private WindowManager.LayoutParams layoutParams;
    private Context context;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        layoutParams = getWindow().getAttributes();
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        handler = new Handler();

       // Log.d("LoginActivity", "Logging enabled");

//        if(preferences.contains("saved_name")){
//            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//            startActivity(intent);
//        }else {

            name = findViewById(R.id.usernameEditText);
            psd = findViewById(R.id.passwordEditText);
            loginBtn = findViewById(R.id.loginBtn);


            loginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    String my_name = name.getText().toString();
//                    String my_password = psd.getText().toString();
//                    editor.putString("saved_name", my_name);
//                    editor.putString("saved_password", my_password);
//                    editor.commit();
                    preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
//                    editor = preferences.edit();
                    String savedUsername = preferences.getString("saved_name", "DEFAULT");
                    String savedPassword = preferences.getString("saved_password", "DEFAULT");

                    Log.d("LoginActivity", "savedUsername: " + savedUsername);
                    Log.d("LoginActivity", "savedPassword: " + savedPassword);
                    if (name.getText().toString().equals("") || psd.getText().toString().equals("")){
                        Toast.makeText(LoginActivity.this, "Uername and password can not be empty",Toast.LENGTH_SHORT).show();
                    }else {
                        if (name.getText().toString().equals(savedUsername) && psd.getText().toString().equals(savedPassword)) {
                            Intent mainPage = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(mainPage);
                        } else {
                            //error vibrate code
//                            if (vibrateThread == null || !vibrateThread.isAlive()) {
//                                vibrateThread = new Thread(new VibrateComputation(handler));
//                                vibrateThread.start();
//                            }

                            Intent registerPage = new Intent(LoginActivity.this, RegisterActivity.class);
                            startActivity(registerPage);
                        }

                    }


                }
            });


    }

    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        if (vibrateThread != null) { vibrateThread.interrupt(); }; vibrateThread = null;
    }



    @Override
    public void onSensorChanged(SensorEvent event) {
        float lightValue = event.values[0];

        if (lightValue < 10) {
            // Brightness is less than 10
            layoutParams.screenBrightness = 0;
            getWindow().setAttributes(layoutParams);
            getWindow().getDecorView().setBackgroundColor(Color.rgb(132,147,169));
        } else {
//            if (lightValue >= 10 && lightValue < 100) {
            // Brightness is greater than 10
            layoutParams.screenBrightness = 1;
            getWindow().setAttributes(layoutParams);
            getWindow().getDecorView().setBackgroundColor(Color.rgb(199,219,248));
        }
//        else {
//            // Brightness is greater than or equal to 100
//            getWindow().getDecorView().setBackgroundColor(Color.YELLOW);
//        }
   }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private class VibrateComputation implements Runnable {
        private Handler h;
        public VibrateComputation(Handler ha) {
            this.h = ha;
        }

        @Override
        public void run() {
            if (vibrator.hasVibrator()) {
                vibrator.vibrate(100);
            }

            h.post(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(context, "Wrong username/password", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}