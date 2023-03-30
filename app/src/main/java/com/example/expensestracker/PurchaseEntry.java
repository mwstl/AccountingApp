package com.example.expensestracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
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

import java.io.File;
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
//    private StorageVolume storageVolume;
    Thread vibrateThread;
    private Vibrator vibrator;
    private Context context;
    private Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_entry);
        // Context for UI runnable
        context = this;

        // Reference to UI elements
        entryAmount = (EditText) findViewById(R.id.amount_edit_text);
        entryCurrency = (EditText) findViewById(R.id.currency_edit_text);
        entryNote = (EditText) findViewById(R.id.note_edit_text);
        typeRadioGroup = (RadioGroup) findViewById(R.id.typeRadioGroup);
        photoViewer = (ImageView) findViewById(R.id.photoView);

        // Initialize database object
        db = new MyDatabase(this);

        // Initialize vibrator service and thread handler
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        handler = new Handler();

//        StorageManager storageManager = (StorageManager) getSystemService(STORAGE_SERVICE);
//        List<StorageVolume> storageVolumes = storageManager.getStorageVolumes();
//        storageVolume = storageVolumes.get(0);
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
//            Log.d("SELECTED BUTTON", "Button: expenseRadioButton");
            amount = "-" + entryAmount.getText().toString();
        } else if (selectedButton == findViewById(R.id.incomeRadioButton)) {
//            Log.d("SELECTED BUTTON", "Button: incomeRadioButton");
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

    public void takePhoto(View v) {
        Intent i = new Intent(this, CameraActivity.class);
        startActivity(i);
    }

    public void capturePhotoResult(View v) {
        // Start camera activity for result
        Intent i = new Intent(this, CameraActivity.class);
        startActivityForResult(i, LAUNCH_CAMERA_ACTIVITY);
    }
    private static int RESULT_IMAGE_CLICK = 1;
    private Uri cameraImageUri;
    public void photoMediaStore(View v) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraImageUri = getOutputMediaFileUri(1);

        // set the image file name
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
        startActivityForResult(intent, RESULT_IMAGE_CLICK);
    }

    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(int type) {

        return Uri.fromFile(getOutputMediaFile(type));
    }

    // Create a File for saving an image or video
    private static File getOutputMediaFile(int type) {

        // Check that the SDCard is mounted
        File mediaStorageDir = new File(
                Environment.getExternalStorageDirectory(), Environment.DIRECTORY_PICTURES);

        // Create the storage directory(MyCameraVideo) if it does not exist
        if (!mediaStorageDir.exists()) {

            if (!mediaStorageDir.mkdirs()) {

                Log.e("Item Attachment",
                        "Failed to create directory MyCameraVideo.");

                return null;
            }
        }
        java.util.Date date = new java.util.Date();
        long timestamp = System.currentTimeMillis();

        File mediaFile;

        if (type == 1) {

            // For unique video file name appending current timeStamp with file
            // name
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + timestamp + ".jpg");

        } else {
            return null;
        }

        return mediaFile;
    }

    public void selectPhotoResult(View v) {
        // Open file browser for result
//        Uri uri = Uri.parse(storageVolume.getDirectory() + "")
//        Uri imageUri = Uri.parse(String.valueOf(Environment.getExternalStorageDirectory()));
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
                }
            } else if (requestCode == RESULT_IMAGE_CLICK) {
                // Here you have the ImagePath which you can set to you image view
                Log.e("Image Name", cameraImageUri.getPath());

                Bitmap myBitmap = BitmapFactory.decodeFile(cameraImageUri.getPath());

                photoViewer.setImageBitmap(myBitmap);
            }

            photoViewer.setImageURI(null);
            photoViewer.setImageURI(Uri.parse(photoURI));
        } catch (Exception e) {
            // If there is an error
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d("ERROR", e.getMessage());
        }
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (photoURI != "" || photoURI != null) {
//            Log.d("PHOTOURI", photoURI);
////            photoViewer.setImageURI(Uri.parse(photoURI));
//        }
//    }

    @Override
    protected void onPause() {
        // Remove any alive/dead threads
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