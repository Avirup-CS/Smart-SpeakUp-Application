package com.example.registration_login_module;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;

public class HomeActivity extends AppCompatActivity {

    private GridLayout gridLayout;
    private ProgressBar progressBar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageButton buttonDrawer;
    private DBHelper dbHelper;
    TextView navUserName,navUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        buttonDrawer = findViewById(R.id.buttonDrawer);

        View headerView = navigationView.getHeaderView(0);
        progressBar = findViewById(R.id.progressBar);

        // Find the TextViews in the header
        navUserName = headerView.findViewById(R.id.userName);
        navUserEmail = headerView.findViewById(R.id.userEmail);

        dbHelper = new DBHelper(this);

        // Get user ID from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("USER_ID", -1);

        // Fetch user details if a valid ID is provided
        if (userId != -1) {
            fetchUserDetails(userId);
        } else {
            showToast(getString(R.string.invalid_user_id));
        }

        // Set up drawer toggle
        buttonDrawer.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Handle navigation item clicks
        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navProfile) {
                Toast.makeText(this, "Profile Selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                intent.putExtra("USER_ID", 1); // Example user ID
                startActivity(intent);
            }
            else if(itemId == R.id.navProfile) {
                Intent iNext = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(iNext);
            }
            else if(itemId == R.id.navChngPassword) {
                Intent iNext = new Intent(HomeActivity.this, EditProfileActivity.class);
                startActivity(iNext);
            }
            else if(itemId == R.id.navFeedback) {
                Intent iNext = new Intent(HomeActivity.this, FeedbackAcitivity.class);
                startActivity(iNext);
            }
            else if(itemId == R.id.navLogout) {
                SharedPreferences shpf=getSharedPreferences("Login",MODE_PRIVATE);
                SharedPreferences.Editor editor=shpf.edit();
                editor.putBoolean("flag",false);
                editor.apply();
                Intent iNext = new Intent(HomeActivity.this, LoginActivity.class);
                iNext.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(iNext);
                finish();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // Initialize the GridLayout
        gridLayout = findViewById(R.id.gridLayout);

        // Setting up click listener for Profile CardView
        // Assuming Profile is the first card
        CardView profileVisitCard = findViewById(R.id.profileCard);
        profileVisitCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                intent.putExtra("USER_ID", 1); // Example user ID
                startActivity(intent);
            }
        });

        // Access the "About" card view (second card in the GridLayout)

        CardView aboutVisitCard = findViewById(R.id.aboutCard);// About is the second card
        aboutVisitCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent to navigate to the AboutActivity when the card is clicked
                Intent intent = new Intent(HomeActivity.this, AboutPageAcitivity.class);
                startActivity(intent); // Start the AboutActivity
            }
        });

//        CardView speakLearnCard = findViewById(R.id.speakLearnCard);
//        speakLearnCard.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Intent to navigate to Speak-Learn activity
//                Intent intent = new Intent(HomeActivity.this, SpeakLearnActivity.class);
//                startActivity(intent);
//            }
//        });

        CardView dictionaryCard = findViewById(R.id.dictionaryCard);
        dictionaryCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent to navigate to Dictionary activity
                Intent intent = new Intent(HomeActivity.this, DictionaryActivity.class);
                startActivity(intent);
            }
        });



        CardView learningPathCard = findViewById(R.id.learningCard);
        learningPathCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent to navigate to Learning Path activity
                Intent intent = new Intent(HomeActivity.this, LearningActivity.class);
                startActivity(intent);
            }
        });


        CardView feedbackCard = findViewById(R.id.feedbackCard);
        feedbackCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent to navigate to Feedback activity
                Intent intent = new Intent(HomeActivity.this, FeedbackAcitivity.class);
                startActivity(intent);
            }
        });



        CardView aispeakCard=findViewById(R.id.speakLearnCard);
        aispeakCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HomeActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });



        // Add other listeners as needed
    }

    private void fetchUserDetails(int userId) {
        progressBar.setVisibility(View.VISIBLE);
        Cursor cursor = null;
        try {
            cursor = dbHelper.getUserById(userId);
            if (cursor != null && cursor.moveToFirst()) {
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NAME));
                @SuppressLint("Range") String email = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_EMAIL));

                // Update UI with fetched data
                navUserName.setText(name);
                navUserEmail.setText(email);
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
        Toast.makeText(HomeActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
