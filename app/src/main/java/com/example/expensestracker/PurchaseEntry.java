package com.example.expensestracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PurchaseEntry extends AppCompatActivity {

    private static final int LAUNCH_CAMERA_ACTIVITY = 1;
    private static final int SELECT_IMAGE = 2;
    private EditText entryAmount, entryCurrency, entryNote;
    private RadioGroup typeRadioGroup;
    private ImageView photoViewer;
    MyDatabase db;
    String photoURI;
    Thread vibrateThread;
    private Vibrator vibrator;
    private Context context;
    private Handler handler;

    private ImageView homeClick;

    private SensorManager sensorManager;
    private Sensor lightSensor;
    private MySensorEventListener msel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_entry);
        // Context for UI runnable
        context = this;

        // Reference to UI elements
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        msel = new MySensorEventListener(getWindow());
        sensorManager.registerListener(msel, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);

        entryAmount = (EditText) findViewById(R.id.amount_edit_text);
        entryCurrency = (EditText) findViewById(R.id.currency_edit_text);
        entryNote = (EditText) findViewById(R.id.note_edit_text);
        typeRadioGroup = (RadioGroup) findViewById(R.id.typeRadioGroup);
        homeClick = (ImageView) findViewById((R.id.homeClick));
        photoViewer = (ImageView) findViewById(R.id.photoView);

        homeClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homePage = new Intent(PurchaseEntry.this, MainActivity.class);
                startActivity(homePage);
            }
        });

        // Initialize database object
        db = new MyDatabase(this);

        // Initialize vibrator service and thread handler
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        handler = new Handler();
    }



    public void addEntry(View v) {
        // Initialize string objects to store text view input
        String amount = entryAmount.getText().toString();
        String currency = entryCurrency.getText().toString();
        String note = entryNote.getText().toString();
        RadioButton selectedButton = findViewById(typeRadioGroup.getCheckedRadioButtonId());
        String type = selectedButton.getText().toString();
        // Debug help
//        Log.d("RADIO BUTTON", "Chosen radio btn: " + findViewById(typeRadioGroup.getCheckedRadioButtonId()));

        // Check if fields are not empty
        if (amount.equals("") || amount.equals("-") || currency.equals("")) {
            // If fields are empty, start vibrate thread to vibrate device and show error toast
            if (vibrateThread == null || !vibrateThread.isAlive()) {
                vibrateThread = new Thread(new VibrateComputation(handler));
                vibrateThread.start();
            }
            // Jump out of method
            return;
        }

        // Check whether entry is expense or income
        // Depending, add "-" or "+" before string
        if (selectedButton == findViewById(R.id.expenseRadioButton)) {
            amount = "-" + entryAmount.getText().toString();
        } else if (selectedButton == findViewById(R.id.incomeRadioButton)) {
            amount = "+" + entryAmount.getText().toString();
        }

        // Current date
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);

        // Check for image
        if (photoURI == null || photoURI.equals(null) || photoURI == "" || photoURI.equals("")) {
            photoURI = "-1";
        }

        Log.d("PHOTO_URI", photoURI);

        // Insert to database
        long id = db.insertData(amount, currency, type, note, photoURI, formattedDate);
        if (id < 0) {
            Toast.makeText(this, "fail", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "success", Toast.LENGTH_SHORT).show();
        }

        // Reset entries
        entryAmount.setText("");
        entryCurrency.setText("");
        entryNote.setText("");
        typeRadioGroup.clearCheck();

        // Return to main activity
        Toast.makeText(this, "Entry added", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    public void capturePhotoResult(View v) {
        // Start camera activity for result
        Intent i = new Intent(this, CameraActivity.class);
        startActivityForResult(i, LAUNCH_CAMERA_ACTIVITY);
    }

    public void selectPhotoResult(View v) {
        // Open file browser for result
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("image/*");
        startActivityForResult(i, SELECT_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Try catch safety
        try {
            super.onActivityResult(requestCode, resultCode, data);

            // If user is taking a photo, save photoURI location
            if (requestCode == LAUNCH_CAMERA_ACTIVITY) {
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, "Captured photo", Toast.LENGTH_SHORT).show();
                    photoURI = data.getStringExtra("photo");
                    Bitmap myBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(photoURI));
                    photoViewer.setImageBitmap(myBitmap);
                }
            } else if (requestCode == SELECT_IMAGE) {
                // If user is selecting photo from device storage, save photoURI location
                if (resultCode == Activity.RESULT_OK) {
                    // Double check if selection is empty
                    if (data == null) {
                        photoURI = "";
                        Toast.makeText(this, "Error selecting image", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    photoURI = data.getData().toString();
                    Log.d("PHOTO_URI", photoURI);
                    Toast.makeText(this, "Successfully selected image", Toast.LENGTH_SHORT).show();

                    photoViewer.setImageURI(null);
                    photoViewer.setImageURI(Uri.parse(photoURI));
                }
            }
        } catch (Exception e) {
            // If there is an error
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d("ERROR", e.getMessage());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        // Remove any alive/dead threads
        sensorManager.unregisterListener(msel);
        if (vibrateThread != null) { vibrateThread.interrupt(); }; vibrateThread = null;
        super.onPause();
    }

    private class VibrateComputation implements Runnable {
        // Handler to update UI with toast
        private Handler h;
        public VibrateComputation(Handler ha) {
            this.h = ha;
        }

        @Override
        public void run() {
            // If device has vibrator function
            if (vibrator.hasVibrator()) {
                vibrator.vibrate(100);
            }

            h.post(new Runnable() {
                @Override
                public void run() {
                    // Make toast on UI
                    Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}