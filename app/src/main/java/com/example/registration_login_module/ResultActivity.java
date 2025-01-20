package com.example.registration_login_module;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ResultActivity extends AppCompatActivity {

    CardView home;
    TextView correctt, wrongt, resultinfo, resultscore;
    ImageView resultImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_result);

        home = findViewById(R.id.returnHome);
        correctt = findViewById(R.id.correctScore);
        wrongt = findViewById(R.id.wrongScore);
        resultinfo = findViewById(R.id.resultInfo);
        resultscore = findViewById(R.id.resultScore);
        resultImage = findViewById(R.id.resultImage);

        int correct = getIntent().getIntExtra("correct", 0);
        int wrong = getIntent().getIntExtra("wrong", 0);
        int score = correct * 1;

        correctt.setText("" + correct);
        wrongt.setText("" + wrong);
        resultscore.setText("Your Score: " + score);

        if (correct >= 0 && correct <= 2) {
            resultinfo.setText("You have to take the test again. . ");
            resultImage.setImageResource(R.drawable.ic_sad);
        } else if (correct >= 3 && correct <= 5) {
            resultinfo.setText("You have to try a little more . .");
            resultImage.setImageResource(R.drawable.ic_neutral);
        } else if (correct >= 6 && correct <= 8) {
            resultinfo.setText("You are pretty good.");
            resultImage.setImageResource(R.drawable.ic_smile);
        } else {
            resultinfo.setText("Congratulations! You are very good.");
            resultImage.setImageResource(R.drawable.ic_smile);
        }

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ResultActivity.this, HomeActivity.class));
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ResultActivity.this, HomeActivity.class));
        finish();

    }
}