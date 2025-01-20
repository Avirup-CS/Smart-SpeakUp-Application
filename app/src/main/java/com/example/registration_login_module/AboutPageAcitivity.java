package com.example.registration_login_module;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AboutPageAcitivity extends AppCompatActivity {

    Button backbtnabout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_aboutpage);

        backbtnabout = findViewById(R.id.backBtnAbout);
        backbtnabout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to the previous screen
                new android.os.Handler().postDelayed(() -> {
                    finish(); // Close the current activity and return to the previous one
                },0);
            }
        });

    }
}