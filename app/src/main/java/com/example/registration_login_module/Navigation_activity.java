package com.example.registration_login_module;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;


public class Navigation_activity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ImageButton buttonDrawerToggle;
    NavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_navigation);

        drawerLayout = findViewById(R.id.drawerlayout);
        buttonDrawerToggle = findViewById(R.id.buttonDrawer);
        navView = findViewById(R.id.navView);

        buttonDrawerToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.open();
            }
        });

        // This is for retrieving the data
        View headerView = navView.getHeaderView(0);
        ImageView userImage = headerView.findViewById(R.id.userImage);
        TextView userName = headerView.findViewById(R.id.userName);

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Navigation_activity.this, userName.getText(),Toast.LENGTH_SHORT).show();
            }
        });



    }
}

