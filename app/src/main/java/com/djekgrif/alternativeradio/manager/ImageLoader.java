package com.djekgrif.alternativeradio.manager;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import rx.functions.Action1;

/**
 * Created by djek-grif on 7/17/16.
 */
public interface ImageLoader {

    void loadDefault(String url, ImageView imageView);
    void loadDefault(String url, ImageView imageView, int placeholder);
    void loadDefault(String url, ImageView imageView, Drawable placeholder);
    void loadBitmap(String url, int with, int height, Action1<Bitmap> listener);
}
