package com.example.expensestracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView balanceTextView;
    private Button addPurchaseButton, viewPurchasesButton,logoutBtn,budgetBtn;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // SharedPreferences for user log-in
        preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        editor = preferences.edit();

        // References to UI text views and buttons
        balanceTextView =  (TextView) findViewById(R.id.balanceTextView);
        addPurchaseButton = (Button) findViewById(R.id.add_purchase_button);
        viewPurchasesButton = (Button) findViewById(R.id.view_purchases_button);
        logoutBtn = (Button) findViewById(R.id.logoutBtn);
        budgetBtn = (Button) findViewById(R.id.set_budget_button);

        // Logout click listener
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.clear();
                editor.commit();
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        // To budget activity click listener
        budgetBtn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Intent intent = new Intent(MainActivity.this,budgetActivity.class);
              startActivity(intent);
          }
        });
    }

    @Override
    public void onResume() {
        // Calculate/update total sum from expense/income
        calculateBalance();
        super.onResume();
    }

    public void addPurchase(View v) {
        // To purchase activity -> through button onClick XML
        Intent i = new Intent(this, PurchaseEntry.class);
        startActivity(i);
    }

    public void viewPurchases(View v) {
        // To purchase list activity -> through button onClick XML
        Intent i = new Intent(this, PurchaseList.class);
        startActivity(i);
    }

    private void calculateBalance() {
        // Initialize database object
        MyDatabase db = new MyDatabase(this);
        // Use MyDatabase to add all purchases into arraylist
        List<Purchase> purchaseList = db.getAllPurchases();
        // Initialize total count to increment
        double totalBalance = 0;

        // Loop through purchase array list
        // For all Purchase objects named entry, loop through purchaseList
        for (Purchase entry : purchaseList) {
            // Retrieve the current array item's ("entry") amount
            double amt = entry.getAmount();

            // Debugging help
//            Log.d("ENTRY TYPE", "Type: " + entry.getType());
//            Log.d("ENTRY AMOUNT", "Amt: " + entry.getAmount());

            // If the current array item is Income
            if (entry.getType() == "Income") {
                // Subtract from the total balance
                totalBalance -= amt;
            } else {
                // It will be expense, so add to the total balance
                totalBalance += amt;
            }
        }

        // Format the totalBalance to 2 decimal float
        String totalBalanceText = String.format(Locale.getDefault(), "%.2f", totalBalance);
        // Set the balanceTextView to show the totalBalance
        balanceTextView.setText(totalBalanceText);

        //Debugging help
//        Log.d("Balance", "Current balance: " + totalBalanceText);
    }
}