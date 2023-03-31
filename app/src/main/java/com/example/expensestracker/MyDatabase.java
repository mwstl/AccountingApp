package com.example.expensestracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MyDatabase {
    private SQLiteDatabase db;
    private Context context;
    private final MyHelper helper;

    public MyDatabase (Context c){
        context = c;
        helper = new MyHelper(context);
    }

    public long insertData (String amount, String currency, String type, String note, String img, String date) {
        // Adding data to database
        db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.AMOUNT, amount);
        contentValues.put(Constants.CURRENCY, currency);
        contentValues.put(Constants.TYPE, type);
        contentValues.put(Constants.NOTE, note);
        contentValues.put(Constants.IMAGE, img);
        contentValues.put(Constants.DATE, date);
        long id = db.insert(Constants.TABLE_NAME, null, contentValues);
        return id;
    }

    public void deleteData(int id) {
        // Delete database entry based on id
        db = helper.getWritableDatabase();
        String query = "DELETE FROM " + Constants.TABLE_NAME + " WHERE "
                + Constants.UID + " = '" + id + "'";
        db.execSQL(query);
    }

    public Cursor getData() {
        // Retrieve database data
        SQLiteDatabase db = helper.getWritableDatabase();

        String[] columns = {Constants.UID, Constants.AMOUNT, Constants.CURRENCY, Constants.TYPE, Constants.NOTE, Constants.IMAGE, Constants.DATE};
        Cursor cursor = db.query(Constants.TABLE_NAME, columns, null, null, null, null, null);
        return cursor;
    }

    public Cursor getIdData(int id) {
        // Retrieve data based on id
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] columns = {Constants.UID, Constants.AMOUNT, Constants.CURRENCY, Constants.TYPE, Constants.NOTE, Constants.IMAGE, Constants.DATE};
        String selection = Constants.UID + "='" + id + "'";
        Cursor cursor = db.query(Constants.TABLE_NAME, columns, selection, null, null, null, null);
        return cursor;
    }

    public Cursor getSelectedDataCursor(String type) {
        // Retrieve data based on purchase type
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] columns = {Constants.UID, Constants.AMOUNT, Constants.CURRENCY, Constants.TYPE, Constants.NOTE, Constants.IMAGE, Constants.DATE};

        String selection = Constants.TYPE + "='" +type+ "'";  //Constants.TYPE = 'type'
        Cursor cursor = db.query(Constants.TABLE_NAME, columns, selection, null, null, null, null);
        return cursor;
    }

    public List<Purchase> getAllPurchases() {
        // Array list of all purchases
        List<Purchase> purchaseList = new ArrayList<>();
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] columns = {Constants.UID, Constants.AMOUNT, Constants.CURRENCY, Constants.TYPE, Constants.NOTE, Constants.IMAGE, Constants.DATE};
        Cursor cursor = db.query(Constants.TABLE_NAME, columns, null, null, null, null, null);

        // Use cursor to move through database
        while (cursor.moveToNext()) {
            // Retrieve entry amount and type
            int idx1 = cursor.getColumnIndex(Constants.AMOUNT);
            int idx2 = cursor.getColumnIndex(Constants.TYPE);
            String amount = cursor.getString(idx1);
            double amt = Double.parseDouble(amount);
            String type = cursor.getString(idx2);

            // Add to array list
            Purchase entry = new Purchase(amt, type);
            purchaseList.add(entry);
        }
        cursor.close();
        return purchaseList;
    }

}

