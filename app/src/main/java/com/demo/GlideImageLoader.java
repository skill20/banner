package com.demo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.base.banner.loader.ImageLoaderInterface;

public class GlideImageLoader implements ImageLoaderInterface<View> {
    @Override
    public void displayImage(Context context, Object path, View view) {
        ImageView imageView = view.findViewById(R.id.img);
        Glide.with(context)
                .load(path)
                .into(imageView);
    }

    @Override
    public View createImageView(Context context) {
        return LayoutInflater.from(context).inflate(R.layout.holder_image,null,false);
    }
}
