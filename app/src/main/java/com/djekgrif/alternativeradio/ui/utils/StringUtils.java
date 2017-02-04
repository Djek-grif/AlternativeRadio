package com.djekgrif.alternativeradio.ui.utils;

import android.text.TextUtils;

/**
 * Created by djek-grif on 7/17/16.
 */
public class StringUtils {

    public static String getNotNullString(String string){
        return TextUtils.isEmpty(string) ? "" : string;
    }
}
