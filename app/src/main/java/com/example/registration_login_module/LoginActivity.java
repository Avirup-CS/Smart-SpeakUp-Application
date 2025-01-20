package com.example.registration_login_module;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class LoginActivity extends AppCompatActivity {

    TextView Regbtn, forgotPassword;
    private EditText inputEmail, inputPassword;
    private CheckBox showPassword;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        Regbtn = findViewById(R.id.textViewSignUp);
        forgotPassword = findViewById(R.id.forgotPassword);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        showPassword = findViewById(R.id.showPassword);
        btnLogin = findViewById(R.id.btnlogin);

        // Toggle password visibility
        showPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    inputPassword.setTransformationMethod(null);
                } else {
                    inputPassword.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkCredentials();
                SharedPreferences shpf =getSharedPreferences("Login",MODE_PRIVATE);
                SharedPreferences.Editor editor=shpf.edit();
                editor.putBoolean("flag",true);
                editor.apply();

            }
        });

        Regbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
        });
    }

    private void checkCredentials() {
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        if (email.isEmpty() || !email.contains("@") || !email.endsWith(".com")) {
            showError(inputEmail, "Email is not valid!");
        } else if (password.isEmpty() || password.length() < 7) {
            showError(inputPassword, "Password must be at least 7 characters");
        } else {
            DBHelper dbHelper = new DBHelper(this);
            Cursor cursor = dbHelper.getUserByEmail(email);

            if (cursor != null && cursor.moveToFirst()) {
                int columnIndexPassword = cursor.getColumnIndex(DBHelper.COLUMN_PASSWORD);
                String dbPassword = cursor.getString(columnIndexPassword);
                if (password.equals(dbPassword)) {
                    // Store user ID in SharedPreferences
                    @SuppressLint("Range") int userId = cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_ID));
                    SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("USER_ID", userId);
                    editor.apply();

                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();

                    // Redirect to HomeActivity
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish(); // End LoginActivity
                } else {
                    Toast.makeText(this, "Invalid credentials!", Toast.LENGTH_SHORT).show();
                }
                cursor.close();
            } else {
                Toast.makeText(this, "User not found!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showError(EditText input, String s) {
        input.setError(s);
        input.requestFocus();
    }
}
