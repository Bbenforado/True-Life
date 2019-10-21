package com.example.applicationsecond.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.applicationsecond.R;

public class FullScreenImageActivity extends AppCompatActivity {

    ImageView image;
    String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        url = getIntent().getStringExtra("image_url");
        image = findViewById(R.id.full_screen_image);
        Glide.with(this).load(url)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(image);
    }
}
