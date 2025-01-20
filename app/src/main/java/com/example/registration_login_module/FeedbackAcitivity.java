package com.example.registration_login_module;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class FeedbackAcitivity extends AppCompatActivity {

    Button backbtnfeedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_feedback);

        EditText emailInput = findViewById(R.id.email_input);
        EditText queryInput = findViewById(R.id.query_input);
        Button submitButton = findViewById(R.id.submit_button);

        backbtnfeedback = findViewById(R.id.backBtnFeedback);
        backbtnfeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to the previous screen
                Intent i = new Intent(FeedbackAcitivity.this, HomeActivity.class); // This will take you back to the previous screen in the stack
                startActivity(i);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailInput.getText().toString().trim();
                String query = queryInput.getText().toString().trim();

                if (email.isEmpty() || query.isEmpty()) {
                    Toast.makeText(FeedbackAcitivity.this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                } else {
                    // Send feedback email
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto", "speakup.mca2024@gmail.com", null));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "App Feedback");
                    emailIntent.putExtra(Intent.EXTRA_TEXT, "User Email: " + email + "\n\nQuery:\n" + query);

                    try {
                        startActivity(Intent.createChooser(emailIntent, "Send email..."));
                        Toast.makeText(FeedbackAcitivity.this, "Feedback sent", Toast.LENGTH_SHORT).show();
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(FeedbackAcitivity.this, "No email clients installed.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }
}