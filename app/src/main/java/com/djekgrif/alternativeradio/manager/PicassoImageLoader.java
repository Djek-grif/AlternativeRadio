package com.djekgrif.alternativeradio.manager;

import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView;

import com.djekgrif.alternativeradio.App;
import com.djekgrif.alternativeradio.BuildConfig;
import com.squareup.picasso.Picasso;

/**
 * Created by djek-grif on 7/17/16.
 */
public class PicassoImageLoader implements ImageLoader{

    private Picasso picasso;

    public PicassoImageLoader() {
        picasso = new Picasso.Builder(App.getInstance()).loggingEnabled(BuildConfig.IS_INTERNAL_BUILD).listener(new Picasso.Listener() {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                exception.printStackTrace();
            }
        }).build();
    }

    @Override
    public void loadDefault(String url, final ImageView imageView) {
        picasso.load(url).config(Bitmap.Config.RGB_565).fit().centerInside().into(imageView);
    }
}
