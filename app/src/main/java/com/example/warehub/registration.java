package com.example.warehub;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class registration extends AppCompatActivity {

    EditText name, email, companyname, phone, username, password, repassword;
    Button signup, signin;
    DBHelper DB;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);  // Correct layout file reference

        // Initialize UI elements
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        companyname = findViewById(R.id.companyname);
        phone = findViewById(R.id.phone);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        repassword = findViewById(R.id.repassword);
        signup = findViewById(R.id.btnsignup);
        signin = findViewById(R.id.btnsignin);

        // Initialize the database helper
        DB = new DBHelper(this);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = username.getText().toString();
                String userEmail = email.getText().toString();
                String userCompany = companyname.getText().toString();
                String userPhone = phone.getText().toString();
                String userPassword = password.getText().toString();
                String userRePassword = repassword.getText().toString();
                String fullName = name.getText().toString();

                // Validation: Check if any field is empty
                if (fullName.isEmpty() || userName.isEmpty() || userEmail.isEmpty() || userPhone.isEmpty() || userPassword.isEmpty() || userRePassword.isEmpty()) {
                    Toast.makeText(registration.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Email validation
                if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                    Toast.makeText(registration.this, "Invalid email format", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Phone number validation (assuming 10 digits)
                if (!userPhone.matches("\\d{10}")) {
                    Toast.makeText(registration.this, "Invalid phone number", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Password validation (minimum 6 characters)
                if (userPassword.length() < 6) {
                    Toast.makeText(registration.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if passwords match
                if (!userPassword.equals(userRePassword)) {
                    Toast.makeText(registration.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if the username already exists
                Boolean checkUser = DB.checkusername(userName);
                if (!checkUser) {
                    // Insert user data into the database
                    Boolean insert = DB.insertData(userName, userPassword, userEmail, fullName, userPhone, userCompany);
                    if (insert) {
                        Toast.makeText(registration.this, "Registered successfully", Toast.LENGTH_SHORT).show();
                        // Redirect to MainActivity on success
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(registration.this, "Registration failed", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(registration.this, "User already exists! Please log in", Toast.LENGTH_SHORT).show();
                }
            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Redirect to login page
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
