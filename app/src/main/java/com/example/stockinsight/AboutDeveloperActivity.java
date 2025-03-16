package com.example.stockinsight;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import android.widget.ImageView;
import android.widget.TextView;

public class AboutDeveloperActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_developer);

        // Set up the toolbar with back navigation
        MaterialToolbar toolbar = findViewById(R.id.aboutDevToolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Set developer details
        TextView nameText = findViewById(R.id.devName);
        TextView addressText = findViewById(R.id.devAddress);
        TextView educationText = findViewById(R.id.devEducation);
        ImageView devImage = findViewById(R.id.devImage);

        nameText.setText("Jaid Rafik Tamboli");
        addressText.setText("Vita, Maharashtra");
        educationText.setText("B.Tech IT Third Year, GCEK");

        // Set developer image (Replace "R.drawable.dev_photo" with your actual image in res/drawable)
        devImage.setImageResource(R.drawable.dev_photo);
    }
}
