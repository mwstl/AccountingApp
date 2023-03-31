package com.example.expensestracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class budgetActivity extends AppCompatActivity {
    private EditText etBudget;
    private TextView tvBudgetRemaining;
    private ProgressBar progressBar;

    private int budget = 0;
    private int budgetRemaining = 0;
    private int progress = 0;
    private ImageView homeClick;

    private SensorManager sensorManager;
    private Sensor lightSensor;
    private MySensorEventListener msel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);
        etBudget = findViewById(R.id.et_budget);
        tvBudgetRemaining = findViewById(R.id.tv_budget_remaining);
        progressBar = findViewById(R.id.progress_bar);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        msel = new MySensorEventListener(getWindow());
        sensorManager.registerListener(msel, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        etBudget.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    budget = Integer.parseInt(s.toString());
                    budgetRemaining = budget;
                    tvBudgetRemaining.setText("Budget remaining: $" + budgetRemaining);
                    progress = 0;
                    progressBar.setProgress(progress);
                    updateBudget();
                } catch (NumberFormatException e) {
                    budget = 0;
                    budgetRemaining = 0;
                    tvBudgetRemaining.setText("Budget remaining: $0");
                    progress = 0;
                    progressBar.setProgress(progress);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        homeClick = (ImageView) findViewById((R.id.homeClick));
        homeClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homePage = new Intent(budgetActivity.this, MainActivity.class);
                startActivity(homePage);
            }
        });

    }
    public void updateBudget() {
        MyHelper dbHelper = new MyHelper(this);
        float expense = dbHelper.getTotalExpense();
        System.out.println(expense);
        if (expense != -1) {
            budgetRemaining += expense;
            tvBudgetRemaining.setText("Budget remaining: $" + budgetRemaining);
            progress = ((budget - budgetRemaining) * 100) / budget;
            progressBar.setProgress(progress);

            if (budgetRemaining < (budget / 3)) {
//                progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.yellow_circle));
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

    public void onResume() {
        super.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(msel);
    }

}