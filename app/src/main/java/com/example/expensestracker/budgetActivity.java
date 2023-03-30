package com.example.expensestracker;

import androidx.appcompat.app.AppCompatActivity;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class budgetActivity extends AppCompatActivity {
    private EditText etBudget;
    private TextView tvBudgetRemaining;
    private ProgressBar progressBar;
    private int budget = 0;
    private int budgetRemaining = 0;
    private int progress = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);

        // Reference to XML
        etBudget = findViewById(R.id.et_budget);
        tvBudgetRemaining = findViewById(R.id.tv_budget_remaining);
        progressBar = findViewById(R.id.progress_bar);

        etBudget.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            // When user types in edit text
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    //
                    budget = Integer.parseInt(s.toString());
                    budgetRemaining = budget;
                    tvBudgetRemaining.setText("Budget remaining: $" + budgetRemaining);
                    progress = 0;
                    progressBar.setProgress(progress);
                    updateBudget();
                } catch (NumberFormatException e) {
                    // If there is an error, default to 0
                    budget = 0;
                    budgetRemaining = 0;
                    tvBudgetRemaining.setText("Budget remaining: $0");
                    progress = 0;
                    progressBar.setProgress(progress);
                    // Print error details to console
                    System.out.println("Error " + e.getMessage());
                    return;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

    }
    public void updateBudget() {
        // Initialize database helper
        MyHelper dbHelper = new MyHelper(this);
        // Expense float to retrieve all expenses
        float expense = dbHelper.getTotalExpense();

        // Debug help
//        System.out.println(expense);

        // If expense is not -1 from helper class
        if (expense != -1) {
            // Calculate remaining budget based on user input
            budgetRemaining += expense;
            tvBudgetRemaining.setText("Budget remaining: $" + budgetRemaining);
            progress = ((budget - budgetRemaining) * 100) / budget;
            progressBar.setProgress(progress);

            // Change budget progress feedback based on remaining budget
            if (budgetRemaining < (budget / 3)) {
                 progressBar.getProgressDrawable().setColorFilter(
                        Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
            } else if (budgetRemaining < (budget / 2)) {
                progressBar.getProgressDrawable().setColorFilter(
                        Color.BLUE, android.graphics.PorterDuff.Mode.SRC_IN);
            }else{
                progressBar.getProgressDrawable().setColorFilter(
                        Color.GREEN, android.graphics.PorterDuff.Mode.SRC_IN);
            }
        }
    }

}