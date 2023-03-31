package com.example.expensestracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.Locale;

public class PurchaseEntryDetails extends AppCompatActivity {

    private TextView amountTextView, currencyTextView, typeTextView, noteTextView, dateTextView;
    private ImageView photoViewer;
    int purchaseID;
    private MyDatabase db;
    private ImageView homeClick;
    private SensorManager sensorManager;
    private Sensor lightSensor;
    private MySensorEventListener msel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_entry_details);

        // References to UI elements
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        msel = new MySensorEventListener(getWindow());
        sensorManager.registerListener(msel, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);

        amountTextView = findViewById(R.id.purchaseAmount);
        currencyTextView = findViewById(R.id.purchaseCurrency);
        typeTextView = findViewById(R.id.purchaseType);
        noteTextView = findViewById(R.id.purchaseNote);
        dateTextView = findViewById(R.id.purchaseDate);
        photoViewer = findViewById(R.id.entryImage);
        homeClick = (ImageView) findViewById((R.id.homeClick));

        // Retrieve purchase id from intent extra
        homeClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homePage = new Intent(PurchaseEntryDetails.this, MainActivity.class);
                startActivity(homePage);
            }
        });

        purchaseID = getIntent().getIntExtra("purchase_id", 1);
        // Debug help
//        Log.d("PURCHASE ID", "ID: " + purchaseID);

        // Initialize database and cursor
        db = new MyDatabase(this);
        Cursor cursor;
        cursor = db.getIdData(purchaseID);

        // Cursor index help
        int idx1 = cursor.getColumnIndex(Constants.AMOUNT);
        int idx2 = cursor.getColumnIndex(Constants.CURRENCY);
        int idx3 = cursor.getColumnIndex(Constants.TYPE);
        int idx4 = cursor.getColumnIndex(Constants.NOTE);
        int idx5 = cursor.getColumnIndex(Constants.DATE);
        int idx6 = cursor.getColumnIndex(Constants.IMAGE);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String amt = cursor.getString(idx1);
            float num = Float.parseFloat(amt.substring(1));
            String cur = cursor.getString(idx2);
            String typ = cursor.getString(idx3);
            String note = cursor.getString(idx4);
            String date = cursor.getString(idx5);
            String image = cursor.getString(idx6);

            // Format amount of purchase to 2 decimal float
            amountTextView.setText(String.format(Locale.getDefault(), "%.2f", num));
            currencyTextView.setText(cur);
            typeTextView.setText(typ);
            noteTextView.setText(note);
            dateTextView.setText(date);

            // Check if there is an image URI
            if (!image.equals("-1") || image != "-1" || image != "" || !image.equals("")) {
                // Redundant safety remove any existing image view uri
                photoViewer.setImageURI(null);
                Bitmap myBitmap = null;
                try {
                    // Image view from bitmap
                    myBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(image));
                    Log.d("BITMAP", "SUCCESSFULLY RETRIEVED BITMAP");
                    photoViewer.setImageBitmap(myBitmap);
                } catch (IOException e) {
                    // Image view from URI
                    Log.d("BITMAP ERROR", e.getMessage());
                    photoViewer.setImageURI(Uri.parse(image));
                }
            }

            cursor.moveToNext();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(msel);
    }
}