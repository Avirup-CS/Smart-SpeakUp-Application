package com.example.registration_login_module;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private TextView profileName, profileEmail, profileMobileNo, profilePlace, titleUsername;
    private ProgressBar progressBar;
    private Button editButton, backBtnProfile,logoutbtn;
    private DBHelper dbHelper;
    private int userId;

    // ActivityResultLauncher to replace startActivityForResult
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
        setContentView(R.layout.activity_profile);

        // Initialize views
        profileName = findViewById(R.id.profileName);
        profileEmail = findViewById(R.id.profileEmail);
        profileMobileNo = findViewById(R.id.profileMobileNo);
        titleUsername = findViewById(R.id.titleUsername);
        profilePlace = findViewById(R.id.profilePlace);
        progressBar = findViewById(R.id.progressBar);
        editButton = findViewById(R.id.editButton);
        backBtnProfile = findViewById(R.id.backBtnProfile);
        logoutbtn=findViewById(R.id.logoutButton);

        dbHelper = new DBHelper(this);

        // Get user ID from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("USER_ID", -1);

        // Fetch user details if a valid ID is provided
        if (userId != -1) {
            fetchUserDetails(userId);
        } else {
            showToast(getString(R.string.invalid_user_id));
        }

        // Handle Edit Profile button click
        editButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            // Pass gender to EditProfileActivity
            //intent.putExtra("USER_GENDER", profileGender.getText().toString());
            editProfileLauncher.launch(intent);
        });

        backBtnProfile.setOnClickListener(v -> {
            new android.os.Handler().postDelayed(() -> {
                finish();
            },0);
        });

        logoutbtn.setOnClickListener(v ->{
            SharedPreferences shpf=getSharedPreferences("Login",MODE_PRIVATE);
            SharedPreferences.Editor editor=shpf.edit();
            editor.putBoolean("flag",false);
            editor.apply();
            Intent iNext = new Intent(ProfileActivity.this, LoginActivity.class);
            iNext.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(iNext);
            finish();
        });
    }

    private void fetchUserDetails(int userId) {
        progressBar.setVisibility(View.VISIBLE);
        Cursor cursor = null;
        try {
            cursor = dbHelper.getUserById(userId);
            if (cursor != null && cursor.moveToFirst()) {
                String name = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NAME));
                String email = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_EMAIL));
                String mobile = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_PHONE));
                String place = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_PLACE));

                // Update UI with fetched data
                profileName.setText(name);
                profileEmail.setText(email);
                profileMobileNo.setText(mobile);
                profilePlace.setText(place);
                titleUsername.setText(name); // Assuming username is the same as the name
            } else {
                showToast(getString(R.string.user_not_found));
            }
        } catch (Exception e) {
            showToast(getString(R.string.error_fetching));
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            progressBar.setVisibility(View.GONE);
        }
    }

    private void showToast(String message) {
        Toast.makeText(ProfileActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
