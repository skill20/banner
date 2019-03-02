package com.base.banner.loader;

import android.content.Context;
import android.view.View;

public interface ImageLoaderInterface<T extends View> {

    void displayImage(Context context, Object path, T view);

    T createImageView(Context context);
}
