package com.demo;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.base.banner.Banner;
import com.base.banner.BannerConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BannerStyleActivity extends AppCompatActivity implements  AdapterView.OnItemSelectedListener {
    Banner banner;
    Spinner spinnerStyle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_style);
        banner = (Banner) findViewById(R.id.banner);
        spinnerStyle = (Spinner) findViewById(R.id.spinnerStyle);
        spinnerStyle.setOnItemSelectedListener(this);


        String[] urls = getResources().getStringArray(R.array.url);        //默认是CIRCLE_INDICATOR
        List list = Arrays.asList(urls);

        String[] titles = getResources().getStringArray(R.array.title);        //默认是CIRCLE_INDICATOR
        List list2 = Arrays.asList(titles);

        banner.setImageList(new ArrayList<>(list))
                .setBannerTitles(new ArrayList<String>(list2))
                .setImageLoader(new GlideImageLoader())
                .start();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position){
            case 0:
                banner.updateBannerStyle(BannerConfig.NOT_INDICATOR);
                break;
            case 1:
                banner.updateBannerStyle(BannerConfig.CIRCLE_INDICATOR);
                break;
            case 2:
                banner.updateBannerStyle(BannerConfig.NUM_INDICATOR);
                break;
            case 3:
                banner.updateBannerStyle(BannerConfig.NUM_INDICATOR_TITLE);
                break;
            case 4:
                banner.setIndicatorGravity(BannerConfig.CENTER);
                banner.updateBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE);
                break;
            case 5:
                banner.setIndicatorGravity(BannerConfig.RIGHT);
                banner.updateBannerStyle(BannerConfig.CIRCLE_NUM_INDICATOR_TITLE);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
