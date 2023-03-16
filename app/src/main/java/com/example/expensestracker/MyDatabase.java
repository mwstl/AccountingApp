package com.example.expensestracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
//        helper.onUpgrade(helper.getWritableDatabase(), helper.getWritableDatabase().getVersion(), helper.getWritableDatabase().getVersion()+1);
    }

    public long insertData (String amount, String currency, String type, String note)
    {
        db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.AMOUNT, amount);
        contentValues.put(Constants.CURRENCY, currency);
        contentValues.put(Constants.TYPE, type);
        contentValues.put(Constants.NOTE, note);
        long id = db.insert(Constants.TABLE_NAME, null, contentValues);
        return id;
    }

    public Cursor getData()
    {
        SQLiteDatabase db = helper.getWritableDatabase();

        String[] columns = {Constants.UID, Constants.AMOUNT, Constants.CURRENCY, Constants.TYPE, Constants.NOTE};
        Cursor cursor = db.query(Constants.TABLE_NAME, columns, null, null, null, null, null);
        return cursor;
    }

    public Cursor getIdData(int id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] columns = {Constants.UID, Constants.AMOUNT, Constants.CURRENCY, Constants.TYPE, Constants.NOTE};
        String selection = Constants.UID + "='" + id + "'";
        Cursor cursor = db.query(Constants.TABLE_NAME, columns, selection, null, null, null, null);
        return cursor;
    }

    // getIdData
    public Purchase getDataID(int id) {
        SQLiteDatabase db = helper.getReadableDatabase();
        String selection = "_id = ?";
        String[] selectionArgs = {String.valueOf(id)};
        String[] columns = {Constants.UID, Constants.AMOUNT, Constants.CURRENCY, Constants.TYPE, Constants.NOTE};
        Cursor cursor = db.query(Constants.TABLE_NAME, columns, selection, selectionArgs, null, null, null);

        int idx1 = cursor.getColumnIndex(Constants.AMOUNT);
        int idx2 = cursor.getColumnIndex(Constants.CURRENCY);
        int idx3 = cursor.getColumnIndex(Constants.TYPE);
        int idx4 = cursor.getColumnIndex(Constants.NOTE);

        Purchase entry = new Purchase();

        if (cursor.moveToFirst()) {
            String entryAmount = cursor.getString(idx1);
            String entryCurrency = cursor.getString(idx2);
            String entryType = cursor.getString(idx3);
            String entryNote = cursor.getString(idx4);

            entry = new Purchase(id, entryAmount,entryCurrency, Integer.parseInt(entryType), entryNote);
        }

//        if (cursor != null) cursor.moveToFirst();
//        Purchase entry = new Purchase(Integer.parseInt(cursor.getString(0)),
//                cursor.getString(idx1), //amount
//                cursor.getString(idx2), //currency
//                Integer.parseInt(cursor.getString(idx3)), //type
//                cursor.getString(idx4)  //note
//                );
        cursor.close();
        return entry;
    }


    public String getSelectedData(String type)  //query button
    {
        //select plants from database of type 'herb'
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] columns = {Constants.AMOUNT, Constants.CURRENCY, Constants.TYPE, Constants.NOTE};

        String selection = Constants.TYPE + "='" +type+ "'";  //Constants.TYPE = 'type'
        Cursor cursor = db.query(Constants.TABLE_NAME, columns, selection, null, null, null, null);

        StringBuffer buffer = new StringBuffer();
        while (cursor.moveToNext()) {
            int index1 = cursor.getColumnIndex(Constants.AMOUNT);
            int index2 = cursor.getColumnIndex(Constants.TYPE);
            String amount = cursor.getString(index1);
            String entryType = cursor.getString(index2);
            buffer.append(amount + " " + entryType + "\n");
        }
        return buffer.toString();
    }

    public Cursor getSelectedDataCursor(String type) {
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] columns = {Constants.UID, Constants.AMOUNT, Constants.CURRENCY, Constants.TYPE, Constants.NOTE};

        String selection = Constants.TYPE + "='" +type+ "'";  //Constants.TYPE = 'type'
        Cursor cursor = db.query(Constants.TABLE_NAME, columns, selection, null, null, null, null);
        return cursor;
    }

    public List<Purchase> getAllPurchases() {
        List<Purchase> purchaseList = new ArrayList<>();
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] columns = {Constants.UID, Constants.AMOUNT, Constants.CURRENCY, Constants.TYPE, Constants.NOTE};
        Cursor cursor = db.query(Constants.TABLE_NAME, columns, null, null, null, null, null);
        while (cursor.moveToNext()) {
            int idx1 = cursor.getColumnIndex(Constants.AMOUNT);
            int idx2 = cursor.getColumnIndex(Constants.TYPE);
            String amount = cursor.getString(idx1);
            double amt = Double.parseDouble(amount);
            Log.d("DOUBLE PURCHASE TESTING", "" + amt);
            String type = cursor.getString(idx2);
            Purchase entry = new Purchase(amt, type);
            purchaseList.add(entry);
        }
        cursor.close();
        return purchaseList;
    }

}

