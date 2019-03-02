package com.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,BannerStyleActivity.class));
            }
        });

        ImageView iv = findViewById(R.id.image);
        Glide.with(this)
                .load("http://bpic.588ku.com/element_origin_min_pic/00/00/05/115732f19cc0079.jpg")
                .into(iv);
    }
}
