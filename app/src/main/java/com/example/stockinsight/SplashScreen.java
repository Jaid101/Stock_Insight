package com.example.stockinsight;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.airbnb.lottie.LottieAnimationView;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Lottie Animation
        LottieAnimationView lottieAnimation = findViewById(R.id.lottieAnimation);
        lottieAnimation.playAnimation();

        // App Name & Slogan
        TextView appName = findViewById(R.id.appName);
        TextView slogan = findViewById(R.id.slogan);
        appName.setText("Stock Insight");
        slogan.setText("Your Smart Stock Market Assistant");

        // Delay for 2 seconds, then check login status
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);

            Intent nextScreen;
            if (isLoggedIn) {
                nextScreen = new Intent(SplashScreen.this, MainActivity.class);
            } else {
                nextScreen = new Intent(SplashScreen.this, LoginSignupActivity.class);
            }

            startActivity(nextScreen);
            finish();
        }, 9000); // 2-second delay
    }
}
