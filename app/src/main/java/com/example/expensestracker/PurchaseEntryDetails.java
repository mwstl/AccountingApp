package com.example.expensestracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Locale;

public class PurchaseEntryDetails extends AppCompatActivity {

    private TextView amountTextView, currencyTextView, typeTextView, noteTextView;
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

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        msel = new MySensorEventListener(getWindow());
        sensorManager.registerListener(msel, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);

        amountTextView = findViewById(R.id.purchaseAmount);
        currencyTextView = findViewById(R.id.purchaseCurrency);
        typeTextView = findViewById(R.id.purchaseType);
        noteTextView = findViewById(R.id.purchaseNote);
        homeClick = (ImageView) findViewById((R.id.homeClick));

        homeClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homePage = new Intent(PurchaseEntryDetails.this, MainActivity.class);
                startActivity(homePage);
            }
        });

        purchaseID = getIntent().getIntExtra("purchase_id", 1);
        Log.d("PURCHASE ID", "ID: " + purchaseID);
        db = new MyDatabase(this);
        Cursor cursor;
        cursor = db.getIdData(purchaseID);

        int idx1 = cursor.getColumnIndex(Constants.AMOUNT);
        int idx2 = cursor.getColumnIndex(Constants.CURRENCY);
        int idx3 = cursor.getColumnIndex(Constants.TYPE);
        int idx4 = cursor.getColumnIndex(Constants.NOTE);

//        ArrayList<String> dataList = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String amt = cursor.getString(idx1);
            Log.d("DETAIL", "Amt: " + amt);
            float num = Float.parseFloat(amt.substring(1));
            String cur = cursor.getString(idx2);
            String typ = cursor.getString(idx3);
            String note = cursor.getString(idx4);

            amountTextView.setText(String.format(Locale.getDefault(), "%.2f", num));
            currencyTextView.setText(cur);
            typeTextView.setText(typ);
            noteTextView.setText(note);

            cursor.moveToNext();
        }

//        amountTextView.setText(String.format(Locale.getDefault(), "%.2f", entry.getAmount()));
//        currencyTextView.setText(entry.getCurrency());
//        noteTextView.setText(entry.getNote());
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