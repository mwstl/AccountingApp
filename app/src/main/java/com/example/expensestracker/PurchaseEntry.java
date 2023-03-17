package com.example.expensestracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.List;

public class PurchaseEntry extends AppCompatActivity {

    private static final int LAUNCH_CAMERA_ACTIVITY = 1;
    private static final int SELECT_IMAGE = 2;
    private EditText entryAmount, entryCurrency, entryNote;
    private RadioGroup typeRadioGroup;
    MyDatabase db;
    String photoURI;
//    private StorageVolume storageVolume;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_entry);

        entryAmount = (EditText) findViewById(R.id.amount_edit_text);
        entryCurrency = (EditText) findViewById(R.id.currency_edit_text);
        entryNote = (EditText) findViewById(R.id.note_edit_text);
        typeRadioGroup = (RadioGroup) findViewById(R.id.typeRadioGroup);

        db = new MyDatabase(this);

//        StorageManager storageManager = (StorageManager) getSystemService(STORAGE_SERVICE);
//        List<StorageVolume> storageVolumes = storageManager.getStorageVolumes();
//        storageVolume = storageVolumes.get(0);
    }

    public void addEntry(View v) {
        String amount = entryAmount.getText().toString();
        String currency = entryCurrency.getText().toString();
        String note = entryNote.getText().toString();
        RadioButton selectedButton = findViewById(typeRadioGroup.getCheckedRadioButtonId());
        Log.d("RADIO BUTTON", "Chosen radio btn: " + findViewById(typeRadioGroup.getCheckedRadioButtonId()));
        String type = selectedButton.getText().toString();

        if (selectedButton == findViewById(R.id.expenseRadioButton)) {
            Log.d("SELECTED BUTTON", "Button: expenseRadioButton");
            amount = "-" + entryAmount.getText().toString();
        } else if (selectedButton == findViewById(R.id.incomeRadioButton)) {
            Log.d("SELECTED BUTTON", "Button: incomeRadioButton");
            amount = "+" + entryAmount.getText().toString();
        }

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
        Intent i = new Intent(this, CameraActivity.class);
        startActivity(i);
    }

    public void capturePhotoResult(View v) {
        Intent i = new Intent(this, CameraActivity.class);
        startActivityForResult(i, LAUNCH_CAMERA_ACTIVITY);
    }

    public void selectPhotoResult(View v) {
//        Uri uri = Uri.parse(storageVolume.getDirectory() + "")
//        Uri imageUri = Uri.parse(String.valueOf(Environment.getExternalStorageDirectory()));
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("image/*");
        startActivityForResult(i, SELECT_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == LAUNCH_CAMERA_ACTIVITY) {
                if (resultCode == Activity.RESULT_OK) {
                    photoURI = data.getStringExtra("photo");
                }
            } else if (requestCode == SELECT_IMAGE) {
                if (resultCode == Activity.RESULT_OK) {
                    if (data == null) {
                        Toast.makeText(this, "Error selecting image", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    photoURI = data.getData().toString();
                    Toast.makeText(this, "Successfully selected image", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}