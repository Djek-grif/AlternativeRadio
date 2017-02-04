package com.djekgrif.alternativeradio.utils;

import android.os.Build;

/**
 * Created by djek-grif on 11/15/16.
 */

public class DeviceUtils {

    public static boolean isLollipopOrHigher(){
        return android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }
}
