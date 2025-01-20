package com.example.registration_login_module;



import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private Button backButon;
    private EditText roleInputField; // Input field for role
    private Button continueButton; // Button to proceed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        roleInputField = findViewById(R.id.role_input); // Assume you have this field in XML
        continueButton = findViewById(R.id.continue_button); // Assume you have this button in XML

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String role = roleInputField.getText().toString().trim();
                if (!role.isEmpty()) {
                    // Pass the role to SelectRole activity
                    Intent intent = new Intent(MainActivity.this, SelectRole.class);
                    intent.putExtra("ROLE", role);
                    startActivity(intent);
                } else {
                    roleInputField.setError("Please enter a role");
                }
            }
        });

        backButon=findViewById(R.id.spback_btn);
        backButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new android.os.Handler().postDelayed(() -> {
                    finish(); // Close the current activity and return to the previous one
                }, 0);
            }
        });
    }
}
