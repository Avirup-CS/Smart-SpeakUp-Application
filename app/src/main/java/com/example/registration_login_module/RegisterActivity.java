package com.example.registration_login_module;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    TextView btn;
    private EditText inputUsername, inputEmail, inputMobileno,inputPlace, inputPassword, inputConfirmPassword;
    private CheckBox showPassword;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btn = findViewById(R.id.alreadyHaveAccount);
        inputUsername = findViewById(R.id.inputUsername);
        inputEmail = findViewById(R.id.inputEmail);
        inputMobileno = findViewById(R.id.inputMobileno);
        inputPassword = findViewById(R.id.inputPassword);
        inputConfirmPassword = findViewById(R.id.inputConfirmPassword);
        showPassword = findViewById(R.id.showPassword);
        btnRegister = findViewById(R.id.btnRegister);
        inputPlace = findViewById(R.id.inputPlace);

        showPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    inputPassword.setTransformationMethod(null);
                    inputConfirmPassword.setTransformationMethod(null);
                } else {
                    inputPassword.setTransformationMethod(new PasswordTransformationMethod());
                    inputConfirmPassword.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkCredentials();
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });
    }

    private void checkCredentials() {
        String username = inputUsername.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        String mobileno = inputMobileno.getText().toString().trim();
        String place = inputPlace.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        String confirmPassword = inputConfirmPassword.getText().toString().trim();

        if (username.isEmpty() || username.length() < 7) {
            showError(inputUsername, "Your username is not valid!");
        } else if (email.isEmpty() || !email.contains("@") || !email.endsWith(".com")) {
            showError(inputEmail, "Email is not valid!");
        } else if (mobileno.isEmpty() || mobileno.length() != 10) {
            showError(inputMobileno, "Mobile No. must be 10 digits");
        } else if (password.isEmpty() || password.length() < 7) {
            showError(inputPassword, "Password must be at least 7 characters");
        } else if (confirmPassword.isEmpty() || !confirmPassword.equals(password)) {
            showError(inputConfirmPassword, "Passwords do not match!");
        } else {
            registerUser(username, mobileno, email, password, place);
        }
    }

    private void registerUser(String username, String mobileno, String email, String password, String place) {
        DBHelper dbHelper = new DBHelper(this);

        Cursor cursor = dbHelper.getUserByEmail(email);
        if (cursor != null && cursor.moveToFirst()) {
            // If email exists, show error message
            Toast.makeText(this, "Email already exists. Try a different one!", Toast.LENGTH_SHORT).show();
            cursor.close();
            return;
        }

        boolean isInserted = dbHelper.addRecord(username, mobileno, email, place, password);

        if (isInserted) {
            Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Email already exists. Try again!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showError(EditText input, String s) {
        input.setError(s);
        input.requestFocus();
    }
}
