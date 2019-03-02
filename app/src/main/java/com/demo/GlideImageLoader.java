package com.demo;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.base.banner.loader.ImageLoaderInterface;

public class GlideImageLoader implements ImageLoaderInterface<ImageView> {
    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        Glide.with(context)
                .load(path)
                .into(imageView);
    }

    @Override
    public ImageView createImageView(Context context) {
        return new ImageView(context);
    }
}
