package com.example.expensestracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraProvider;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CameraActivity extends AppCompatActivity implements SensorEventListener {

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    PreviewView previewView;
    private CameraProvider cameraProvider;
    private Button captureButton;
    private ImageCapture imageCapture;

    private static final int img_id = 1;
    private Executor executor = Executors.newSingleThreadExecutor();
    private int REQUEST_CODE_PERMISSIONS = 1001;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA"};

    private Context context;
    private Handler handler;
    private SensorManager sensorManager;
    private Sensor lightSensor;
    float maxLight;
    Thread lightThread;

    String fileLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        context = this;

        captureButton = findViewById(R.id.captureCameraButton);
        previewView = findViewById(R.id.previewView);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lightSensor = (Sensor) sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        maxLight = lightSensor.getMaximumRange();

        handler = new Handler();

        if (allPermissionsGranted()) {
            startCamera(); //start camera if permission has been granted by user
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

    }

    private Executor getExecutor() {
        return ContextCompat.getMainExecutor(this);
    }

    private void startCamera() {

        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    bindPreview(cameraProvider);
                } catch (ExecutionException | InterruptedException e) {
                    // This should never be reached.
                }
            }
        }, ContextCompat.getMainExecutor(this));
    }

    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {

        cameraProvider.unbindAll();

        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        imageCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
    }

    public void capturePhoto(View v) {
        long timeStamp = System.currentTimeMillis();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, timeStamp);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");

        imageCapture.takePicture(
                new ImageCapture.OutputFileOptions.Builder(
                        getContentResolver(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        contentValues
                ).build(),
                getExecutor(),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        Toast.makeText(CameraActivity.this, "Saving...", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Toast.makeText(CameraActivity.this, "Error: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void capturePhotoButton(View v) {
        long timestamp = System.currentTimeMillis();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, timestamp);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");

        fileLocation = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString() + "/" + timestamp + ".jpeg";
//        Log.d("IMAGE LOCATION", "Media Location: " + fileLocation);

        imageCapture.takePicture(
                new ImageCapture.OutputFileOptions.Builder(
                        getContentResolver(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        contentValues
                ).build(),
                getExecutor(),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        Toast.makeText(CameraActivity.this, "Captured successfully", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Toast.makeText(CameraActivity.this, "Error: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    public void finishCapture(View v) {
        Intent i = new Intent(this, PurchaseEntry.class);
        startActivity(i);
    }

    public void returnCapture(View v) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("photo", fileLocation);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    public void selectImage(View v) {
        Intent selectedImage = new Intent(Intent.ACTION_GET_CONTENT);

    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (lightSensor != null) {
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onPause() {
        sensorManager.unregisterListener(this);
        if (lightThread != null) { lightThread.interrupt(); }; lightThread = null;
        super.onPause();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            float lightVal = event.values[0];

            //if light is 1/10 of maximum brightness and not already alive
            if (lightVal < maxLight*0.0001) {
                if (lightThread == null || !lightThread.isAlive()){
                    Log.d("LIGHT_MAX", "MaxLight: " + maxLight);
                    Log.d("LIGHT_VALS", "Light: " + lightVal);
                    lightThread = new Thread(new LightComputation(handler));
                    lightThread.start();
                }
            } else {
                if (lightThread != null) lightThread.interrupt();   // stop current thread
                lightThread = null; // remove reference to thread
            }
        }   //if Sensor.TYPE_LIGHT
    }   // onSensorChanged

    private class LightComputation implements Runnable {

        private Handler h;
        public LightComputation(Handler ha) { this.h = ha; }

        @Override
        public void run() {
            // make toast and wait before displaying another toast
            h.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Scene may be dark", Toast.LENGTH_SHORT).show();
                }
            });

            //wait seconds
            SystemClock.sleep(5000);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}
}