package com.example.expensestracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Camera;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class PurchaseEntry extends AppCompatActivity {

    private EditText entryAmount, entryCurrency, entryNote;
    private RadioGroup typeRadioGroup;
    MyDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_entry);

        entryAmount = (EditText) findViewById(R.id.amount_edit_text);
        entryCurrency = (EditText) findViewById(R.id.currency_edit_text);
        entryNote = (EditText) findViewById(R.id.note_edit_text);
        typeRadioGroup = (RadioGroup) findViewById(R.id.typeRadioGroup);

        db = new MyDatabase(this);
    }

    public void addEntry(View v) {
        String amount = entryAmount.getText().toString();
        String currency = entryCurrency.getText().toString();
        String note = entryNote.getText().toString();
        RadioButton selectedButton = findViewById(typeRadioGroup.getCheckedRadioButtonId());
        String type = selectedButton.getText().toString();

        // Insert to database
        long id = db.insertData(amount, currency, type, note);
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

    public void takePhoto(View v) {
        // WORK IN PROGRESS
        Intent i = new Intent(this, CameraActivity.class);
        startActivity(i);
    }
}