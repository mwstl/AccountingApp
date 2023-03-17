package com.example.expensestracker;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Locale;

public class PurchaseEntryDetails extends AppCompatActivity {

    private TextView amountTextView, currencyTextView, typeTextView, noteTextView;
    int purchaseID;
    private MyDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_entry_details);

        amountTextView = findViewById(R.id.purchaseAmount);
        currencyTextView = findViewById(R.id.purchaseCurrency);
        typeTextView = findViewById(R.id.purchaseType);
        noteTextView = findViewById(R.id.purchaseNote);

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
}