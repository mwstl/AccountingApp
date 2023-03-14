package com.example.expensestracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class PurchaseList extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private RecyclerView myRecycler;
    private MyDatabase db;
    private CustomAdapter customAdapter;
    private MyHelper helper;
    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_list);

        myRecycler = (RecyclerView) findViewById(R.id.purchaseRecyclerView);
        db = new MyDatabase(this);
        helper = new MyHelper(this);

        Cursor cursor;

        if (getIntent().getBooleanExtra("query", false) != true) {
            cursor = db.getData();
            Log.d("QUERY_BOOLEAN", "Get data");
        } else {
            String selection = getIntent().getStringExtra("selection");
            cursor = db.getSelectedDataCursor(selection);
            Log.d("QUERY_BOOLEAN", "get Selected data");
        }

        int index1 = cursor.getColumnIndex(Constants.AMOUNT);
        int index2 = cursor.getColumnIndex(Constants.CURRENCY);
        int index3 = cursor.getColumnIndex(Constants.TYPE);
        int index4 = cursor.getColumnIndex(Constants.NOTE);

        ArrayList<String> mArrayList = new ArrayList<String>();
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            String amount = cursor.getString(index1);
            String currency = cursor.getString(index2);
            String type = cursor.getString(index3);
            String note = cursor.getString(index4);
            String s = amount +"," + currency + "," + type + "," + note;
            mArrayList.add(s);
            cursor.moveToNext();
        }

        customAdapter = new CustomAdapter(mArrayList);
        myRecycler.setAdapter(customAdapter);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        myRecycler.setLayoutManager(mLayoutManager);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        LinearLayout clickedRow = (LinearLayout) view;
        TextView purchaseAmount = (TextView) view.findViewById(R.id.amountList);
        TextView purchaseCurrency = (TextView) view.findViewById(R.id.currencyList);
        TextView purchaseType = (TextView) view.findViewById(R.id.typeList);
        Toast.makeText(this,
                "row " + (1+position) + ":  " + purchaseAmount.getText() +" "+purchaseCurrency.getText() + " " + purchaseType.getText(),
                Toast.LENGTH_LONG).show();
    }
}