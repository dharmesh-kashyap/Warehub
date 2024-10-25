package com.example.warehub;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    EditText username, password;
    Button btnlogin, btnForgotPassword;
    Button signup;
    DBHelper DB;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if the user is already logged in
        SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);

        // If user is logged in, redirect to MainActivity2
        if (isLoggedIn) {
            Intent intent = new Intent(getApplicationContext(), MainActivity2.class);  // Redirect to MainActivity2
            startActivity(intent);
            finish();  // Close MainActivity (login screen)
            return;  // Exit onCreate to avoid loading login UI
        }

        setContentView(R.layout.activity_main);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        btnlogin = findViewById(R.id.btnsignin1);
        btnForgotPassword = findViewById(R.id.btnforgotpassword);
        signup = findViewById(R.id.signup8);

        DB = new DBHelper(this);

        // Forgot Password button click listener
        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        // Login button click listener
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingInflatedId")
            @Override
            public void onClick(View view) {
                String user = username.getText().toString();
                String pass = password.getText().toString();

                if (user.equals("") || pass.equals("")) {
                    Toast.makeText(MainActivity.this, "Please enter all the fields", Toast.LENGTH_SHORT).show();
                } else {
                    Boolean checkuserpass = DB.checkUsernameAndPassword(user, pass);
                    if (checkuserpass) {
                        // Save login state in SharedPreferences
                        SharedPreferences.Editor editor = getSharedPreferences("loginPrefs", MODE_PRIVATE).edit();
                        editor.putBoolean("isLoggedIn", true);
                        editor.apply();

                        Toast.makeText(MainActivity.this, "Sign in successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), MainActivity2.class);  // Redirect to MainActivity2
                        startActivity(intent);
                        finish();  // Close LoginActivity
                    } else {
                        Toast.makeText(MainActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Sign up button click listener
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), registration.class);
                startActivity(intent);
            }
        });
    }
}
