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
        preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        editor = preferences.edit();

        balanceTextView = findViewById(R.id.balanceTextView);
        addPurchaseButton = findViewById(R.id.add_purchase_button);
        viewPurchasesButton = findViewById(R.id.view_purchases_button);
        logoutBtn = findViewById(R.id.logoutBtn);
        budgetBtn = findViewById(R.id.set_budget_button);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.clear();
                editor.commit();
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

  budgetBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
          Intent intent = new Intent(MainActivity.this,budgetActivity.class);
          startActivity(intent);
      }
  });

//        calculateBalance();
    }

    @Override
    public void onResume() {
        calculateBalance();
        super.onResume();
    }

    public void addPurchase(View v) {
        Intent i = new Intent(this, PurchaseEntry.class);
        startActivity(i);
    }

    public void viewPurchases(View v) {
        Intent i = new Intent(this, PurchaseList.class);
        startActivity(i);
    }

    private void calculateBalance() {
        MyDatabase db = new MyDatabase(this);
        List<Purchase> purchaseList = db.getAllPurchases();
        double totalBalance = 0;

        for (Purchase entry : purchaseList) {
            double amt = entry.getAmount();
            Log.d("ENTRY TYPE", "Type: " + entry.getType());
            Log.d("ENTRY AMOUNT", "Amt: " + entry.getAmount());

//            if (entry.getType() == "Expense") {
//                Log.d("CURRENT POSITION", "Subtracting expense");
//                totalBalance -= amt;
//            } else if (entry.getType() == "Income") {
//                Log.d("CURRENT POSITION", "Adding income");
//                totalBalance += amt;
//            }

            if (entry.getType() == "Income") {
                totalBalance -= amt;
            } else {
                totalBalance += amt;
            }
        }

        String totalBalanceText = String.format(Locale.getDefault(), "%.2f", totalBalance);
        balanceTextView.setText(totalBalanceText);
        Log.d("Balance", "Current balance: " + totalBalanceText);
    }
}