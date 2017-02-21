package com.djekgrif.alternativeradio.ui.utils;

import android.content.res.Resources;
import android.support.annotation.DimenRes;
import android.util.TypedValue;

import com.djekgrif.alternativeradio.App;

/**
 * Created by djek-grif on 2/21/17.
 */

public class DimensUtils {

    public static int pxToDp(float px){
        Resources resources = App.getInstance().getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, resources.getDisplayMetrics());
    }

    public static int dpToPx(@DimenRes int dpResource){
        Resources resources = App.getInstance().getResources();
        return (int) resources.getDimensionPixelOffset(dpResource);
    }

    public static int dpToPx(float dp){
        Resources resources = App.getInstance().getResources();
        return (int) (dp * resources.getDisplayMetrics().density);
    }
}
