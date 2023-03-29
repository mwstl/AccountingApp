package com.example.expensestracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    private Button btnOK, btnCancel;
    private TextView userName, password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnOK = findViewById(R.id.btnOK);
        btnCancel = findViewById(R.id.btnCancel);

        userName = findViewById(R.id.edRegisterUsername);
        password = findViewById(R.id.edRegisterPassword);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userName.getText().toString().equals("")){
                    Toast.makeText(RegisterActivity.this, "User name can't be empty!", Toast.LENGTH_LONG).show();
                }else if (password.getText().toString().equals("")){
                    Toast.makeText(RegisterActivity.this, "Pasword can't be empty!", Toast.LENGTH_LONG).show();
                }else{
                    SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    Toast.makeText(RegisterActivity.this, userName.getText().toString().length() + "", Toast.LENGTH_LONG).show();
                    editor.putString("saved_name", userName.getText().toString());
                    editor.putString("saved_password", password.getText().toString());
                    editor.commit();
                    Toast.makeText(RegisterActivity.this, "Successful", Toast.LENGTH_LONG).show();
                    finish();
                }

            }
        });
    }
}