package com.example.registration_login_module;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.card.MaterialCardView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class LearningActivity extends AppCompatActivity {

    Button backButon;
    CardView vocabularycard,tensecard, prepositioncard, articlecard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_learning);

        vocabularycard = findViewById(R.id.vocabularyCard);
        tensecard = findViewById(R.id.tenseCard);
        prepositioncard = findViewById(R.id.prepositionCard);
        articlecard = findViewById(R.id.articleCard);

        vocabularycard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LearningActivity.this, BasicTest.class));
                finish();
            }
        });

        tensecard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LearningActivity.this, TenseTest.class));
                finish();
            }
        });

        prepositioncard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LearningActivity.this, PrepositionTest.class));
                finish();
            }
        });

        articlecard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LearningActivity.this, ArticleTest.class));
                finish();
            }
        });

        backButon=findViewById(R.id.BackBtn);
        backButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new android.os.Handler().postDelayed(() -> {
                    finish(); // Close the current activity and return to the previous one
                },0);
            }
        });

    }

    @Override
    public void onBackPressed() {

        MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(LearningActivity.this);

        materialAlertDialogBuilder.setTitle(R.string.app_name);

        materialAlertDialogBuilder.setMessage("Are you sure want to exit the app?");

        materialAlertDialogBuilder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        materialAlertDialogBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });

        materialAlertDialogBuilder.show();

    }
}