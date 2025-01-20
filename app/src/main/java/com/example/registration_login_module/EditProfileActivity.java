package com.example.registration_login_module;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class EditProfileActivity extends AppCompatActivity {

    private EditText editUsername, editEmail, editMobileNo,editPlace, currentPassword, newPassword, confirmNewPassword;
    private Button saveButton,backButton;
    private DBHelper dbHelper;
    private int userId;

    private final ActivityResultLauncher<Intent> editProfileLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    // Refresh user details after editing
                    fetchUserDetails(userId);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize views
        editUsername = findViewById(R.id.editUsername);
        editEmail = findViewById(R.id.editEmail);
        editMobileNo = findViewById(R.id.editMobileNo);
        editPlace = findViewById(R.id.editPlace);
        currentPassword = findViewById(R.id.currentPassword);
        newPassword = findViewById(R.id.newPassword);
        confirmNewPassword = findViewById(R.id.confirmNewPassword);
        saveButton = findViewById(R.id.saveButton);
        backButton = findViewById(R.id.backButton);


        dbHelper = new DBHelper(this);

        // Get user ID from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("USER_ID", -1);


        // Fetch user details and populate fields
        fetchUserDetails(userId);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserDetails();
            }
        });

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(EditProfileActivity.this, HomeActivity.class);
            editProfileLauncher.launch(intent);

        });
    }

    private void fetchUserDetails(int userId) {
        if (userId == -1) {
            showToast("Invalid User ID");
            return;
        }

        Cursor cursor = dbHelper.getUserById(userId);
        if (cursor != null && cursor.moveToFirst()) {
            editUsername.setText(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NAME)));
            editEmail.setText(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_EMAIL)));
            editMobileNo.setText(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_PHONE)));
            editPlace.setText(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_PLACE)));
            cursor.close();
        } else {
            showToast("User not found");
        }
    }

    private void updateUserDetails() {
        String newUsername = editUsername.getText().toString().trim();
        String newEmail = editEmail.getText().toString().trim();
        String newMobileNo = editMobileNo.getText().toString().trim();
        String newPlace = editPlace.getText().toString().trim();
        String currentPwd = currentPassword.getText().toString().trim();
        String newPwd = newPassword.getText().toString().trim();
        String confirmPwd = confirmNewPassword.getText().toString().trim();

        if (newUsername.isEmpty() || newEmail.isEmpty() || newMobileNo.isEmpty()) {
            showToast("All fields must be filled");
            return;
        }

        if (!newPwd.isEmpty() || !confirmPwd.isEmpty()) {
            if (newPwd.length() < 7) {
                showToast("New password must be at least 7 characters");
                return;
            }

            if (!newPwd.equals(confirmPwd)) {
                showToast("Passwords do not match");
                return;
            }

            if (!validateCurrentPassword(currentPwd)) {
                showToast("Current password is incorrect");
                return;
            }
        }

        int rowsUpdated = dbHelper.updateRecord(userId, newUsername, newMobileNo, newEmail,newPlace,!newPwd.isEmpty() ? newPwd : null);
        if (rowsUpdated > 0) {
            showToast("Profile updated successfully");
            setResult(RESULT_OK);  // Indicate successful update
            finish();
        } else {
            showToast("Failed to update profile");
        }
    }

    private boolean validateCurrentPassword(String currentPwd) {
        Cursor cursor = dbHelper.getUserById(userId);
        if (cursor != null && cursor.moveToFirst()) {
            String storedPassword = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_PASSWORD));
            cursor.close();
            return currentPwd.equals(storedPassword);
        }
        return false;
    }

    private void showToast(String message) {
        Toast.makeText(EditProfileActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
