package com.example.expensestracker;

public class Constants {
    public static final String DATABASE_NAME = "purchasebase";
    public static final String TABLE_NAME = "PURCHASES";
    public static final String UID = "_id";
    public static final String AMOUNT = "Amount";
    public static final String CURRENCY = "Currency";
    public static final String TYPE = "Type";
    public static final String NOTE = "Note";
    public static final String IMAGE = "Image";
    public static final String DATE = "Date";

    //if modifying schema ie. add column/field -> upgrade database version
    //can be anything as long as larger than previous
    public static final int DATABASE_VERSION = 10;
}
