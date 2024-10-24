package com.example.warehub;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import android.content.Intent;  // Add this import for Intent

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText username, newPassword;
    Button btnResetPassword;
    DBHelper DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        username = (EditText) findViewById(R.id.username);
        newPassword = (EditText) findViewById(R.id.newPassword);
        btnResetPassword = (Button) findViewById(R.id.btnResetPassword);
        DB = new DBHelper(this);

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = username.getText().toString();
                String newPass = newPassword.getText().toString();

                if(user.equals("") || newPass.equals(""))
                    Toast.makeText(ForgotPasswordActivity.this, "Please enter all the fields", Toast.LENGTH_SHORT).show();
                else{
                    Boolean checkUser = DB.checkusername(user);
                    if(checkUser == true){
                        Boolean update = DB.updatepassword(user, newPass);
                        if(update == true){
                            Toast.makeText(ForgotPasswordActivity.this, "Password Reset Successfully", Toast.LENGTH_SHORT).show();
                            // After the success message, go to the login page
                            Intent intent = new Intent(ForgotPasswordActivity.this, registration.class);
                            startActivity(intent);
                            finish();  // Optional: This will close the ForgotPasswordActivity
                        } else {
                            Toast.makeText(ForgotPasswordActivity.this, "Password Reset Failed", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this, "User does not exist", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
