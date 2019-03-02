package com.demo;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.base.banner.Banner;
import com.base.banner.BannerConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom);

        Banner banner = findViewById(R.id.banner);

        String[] urls = getResources().getStringArray(R.array.url);        //默认是CIRCLE_INDICATOR
        List list = Arrays.asList(urls);

        banner.setImageList(new ArrayList<>(list))
                .setImageLoader(new GlideImageLoader())
                .start();
    }
}
