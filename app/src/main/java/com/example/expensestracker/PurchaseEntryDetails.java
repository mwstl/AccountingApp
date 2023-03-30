package com.example.expensestracker;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

public class PurchaseEntryDetails extends AppCompatActivity {

    private TextView amountTextView, currencyTextView, typeTextView, noteTextView, dateTextView;
    private ImageView photoViewer;
    int purchaseID;
    private MyDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_entry_details);

        // References to UI elements
        amountTextView = findViewById(R.id.purchaseAmount);
        currencyTextView = findViewById(R.id.purchaseCurrency);
        typeTextView = findViewById(R.id.purchaseType);
        noteTextView = findViewById(R.id.purchaseNote);
        dateTextView = findViewById(R.id.purchaseDate);
        photoViewer = findViewById(R.id.entryImage);

        // Retrieve purchase id from intent extra
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
            // Debug help
//            Log.d("DETAIL", "Amt: " + amt);
            float num = Float.parseFloat(amt.substring(1));
            String cur = cursor.getString(idx2);
            String typ = cursor.getString(idx3);
            String note = cursor.getString(idx4);
            String date = cursor.getString(idx5);
//            String image = cursor.getString(idx6);

            // Format amount of purchase to 2 decimal float
            amountTextView.setText(String.format(Locale.getDefault(), "%.2f", num));
            currencyTextView.setText(cur);
            typeTextView.setText(typ);
            noteTextView.setText(note);
            dateTextView.setText(date);

//            if (image.equals("-1") || image != "-1" || image != "" || !image.equals("")) {
//                photoViewer.setImageURI(null);
//                photoViewer.setImageURI(Uri.parse(image));
//            }

            cursor.moveToNext();
        }
    }
}