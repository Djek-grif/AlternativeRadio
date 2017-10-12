package com.djekgrif.alternativeradio.manager;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.djekgrif.alternativeradio.App;
import com.djekgrif.alternativeradio.BuildConfig;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import rx.functions.Action1;

/**
 * Created by djek-grif on 7/17/16.
 */
public class PicassoImageLoader implements ImageLoader{

    private Picasso picasso;

    public PicassoImageLoader() {
        picasso = new Picasso.Builder(App.getInstance()).loggingEnabled(BuildConfig.DEBUG).listener((picasso1, uri, exception) -> exception.printStackTrace()).build();
    }

    @Override
    public void loadDefault(String url, final ImageView imageView) {
        picasso.load(url)
                .config(Bitmap.Config.RGB_565)
                .fit()
                .centerInside()
                .into(imageView);
    }

    @Override
    public void loadDefault(String url, ImageView imageView, int placeholder) {
        picasso.load(url).config(Bitmap.Config.RGB_565).fit().centerInside().placeholder(placeholder).into(imageView);
    }

    @Override
    public void loadDefault(String url, ImageView imageView, Drawable placeholder) {
        picasso.load(url)
                .config(Bitmap.Config.RGB_565)
                .fit()
                .centerInside()
                .placeholder(placeholder)
                .into(imageView);
    }

    @Override
    public void loadBitmap(String url, int with, int height, Action1<Bitmap> listener) {
        picasso.load(url).config(Bitmap.Config.RGB_565).resize(with, height).centerInside().into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                listener.call(bitmap);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                listener.call(null);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {}
        });
    }
}
