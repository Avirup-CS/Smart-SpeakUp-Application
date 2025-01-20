package com.example.registration_login_module;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText inputUsername, inputEmail, inputPhone;
    private Button btnRetrievePassword, btnBackToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        inputUsername = findViewById(R.id.inputUsername);
        inputEmail = findViewById(R.id.inputEmail);
        inputPhone = findViewById(R.id.inputPhone);
        btnRetrievePassword = findViewById(R.id.btnRetrievePassword);
        btnBackToLogin = findViewById(R.id.btnBackToLogin);

        // Set up "Back to Login" button click listener
        btnBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new android.os.Handler().postDelayed(() -> {
                    finish();
                },0); // Close this activity and go back to LoginActivity
            }
        });

        // Set up "Retrieve Password" button click listener
        btnRetrievePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retrievePassword();
            }
        });
    }

    private void retrievePassword() {
        String username = inputUsername.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        String phone = inputPhone.getText().toString().trim();

        if (username.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            Toast.makeText(ForgotPasswordActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        DBHelper dbHelper = new DBHelper(this);
        Cursor cursor = dbHelper.getUserByEmailAndPhone(email, phone);

        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") String dbUsername = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NAME));
            @SuppressLint("Range") String dbPassword = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_PASSWORD));

            if (username.equals(dbUsername)) {
                // Password retrieval success
                Toast.makeText(ForgotPasswordActivity.this, "Your password is: " + dbPassword, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(ForgotPasswordActivity.this, "Username does not match", Toast.LENGTH_SHORT).show();
            }
            cursor.close();
        } else {
            Toast.makeText(ForgotPasswordActivity.this, "No matching user found", Toast.LENGTH_SHORT).show();
        }
    }
}
