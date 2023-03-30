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
    private WindowManager.LayoutParams layoutParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize sensor manager and light sensor
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        // Window layout parameters object for brightness control
        layoutParams = getWindow().getAttributes();

        // References to UI elements
        name = findViewById(R.id.usernameEditText);
        psd = findViewById(R.id.passwordEditText);
        loginBtn = findViewById(R.id.loginBtn);

        // Log in button listener
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve sharedPrefs username and password
                preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
                String savedUsername = preferences.getString("saved_name", "DEFAULT");
                String savedPassword = preferences.getString("saved_password", "DEFAULT");

                // Debug help
                Log.d("LoginActivity", "savedUsername: " + savedUsername);
                Log.d("LoginActivity", "savedPassword: " + savedPassword);

                // Check sharedPrefs
                if (name.getText().toString().equals("") || psd.getText().toString().equals("")){
                    // If no text has been entered, but log in button is clicked, show toast with hint
                    Toast.makeText(LoginActivity.this, "Username and password can not be empty",Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, text is in text views
                    if (name.getText().toString().equals(savedUsername) && psd.getText().toString().equals(savedPassword)) {
                        // If the entered username and password match the sharedPrefs, log in
                        Intent mainPage = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(mainPage);
                    } else {
                        // If the entered username and password are wrong, go to user Register Activity
                        Intent registerPage = new Intent(LoginActivity.this, RegisterActivity.class);
                        startActivity(registerPage);
                    }
                }
            }
        });
    }

    protected void onResume() {
        super.onResume();
        // Register light sensor
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Release sensor, free resources
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Get light values from light sensor
        float lightValue = event.values[0];

        if (lightValue < 10) {
            // Brightness is less than 10, set screen brightness to darkest
            layoutParams.screenBrightness = 0;
            getWindow().setAttributes(layoutParams);
            getWindow().getDecorView().setBackgroundColor(Color.rgb(132,147,169));
        } else {
            // Brightness is greater than 10, set brightness to brightest
            layoutParams.screenBrightness = 1;
            getWindow().setAttributes(layoutParams);
            getWindow().getDecorView().setBackgroundColor(Color.rgb(199,219,248));
        }
   }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

}