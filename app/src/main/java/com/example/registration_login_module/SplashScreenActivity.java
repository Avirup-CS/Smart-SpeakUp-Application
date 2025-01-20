package com.example.registration_login_module;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SplashScreenActivity extends AppCompatActivity {

    ImageView imgLogo, imgText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        imgLogo = findViewById(R.id.imgLogo);
        imgText = findViewById(R.id.imgText);

        imgLogo.animate().translationY(4000).setDuration(1000).setStartDelay(2000);
        imgText.animate().translationY(4000).setDuration(1000).setStartDelay(2000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences shpf = getSharedPreferences("Login",MODE_PRIVATE);
                Boolean check = shpf.getBoolean("flag",false);
                Intent iHome;
                if(check)
                {
                    iHome = new Intent(SplashScreenActivity.this, HomeActivity.class);
                }
                else
                {
                    iHome=new Intent(SplashScreenActivity.this,LoginActivity.class);
                }
                startActivity(iHome);
                finish();
            }
        }, 3000);
    }
}