package com.example.expensestracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {


    EditText name, psd;
    Button loginBtn;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        editor = preferences.edit();
        Log.d("LoginActivity", "Logging enabled");

        if(preferences.contains("saved_name")){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }else {

            name = findViewById(R.id.usernameEditText);
            psd = findViewById(R.id.passwordEditText);
            loginBtn = findViewById(R.id.loginBtn);

//            String savedUsername = preferences.getString("saved_name", "");
//            String savedPassword = preferences.getString("saved_password", "");
//
//            Log.d("LoginActivity", "savedUsername: " + savedUsername);
//            Log.d("LoginActivity", "savedPassword: " + savedPassword);
//
//            if (savedUsername.isEmpty() || savedPassword.isEmpty()) {
//                // Username or password is missing
//                // Show an error message
//                Toast.makeText(this, "Please enter your username and password", Toast.LENGTH_SHORT).show();
//
//                // Disable the login button
//                loginBtn.setEnabled(false);
//                Log.d("LoginActivity", "Login button disabled");
//            } else {
//                // Username and password are both present
//                // Enable the login button
//                loginBtn.setEnabled(true);
//                Log.d("LoginActivity", "Login button enabled");
//            }

            loginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String my_name = name.getText().toString();
                    String my_password = psd.getText().toString();
                    editor.putString("saved_name", my_name);
                    editor.putString("saved_password", my_password);
                    editor.commit();


                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            });

        }
    }
}