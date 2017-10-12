package com.djekgrif.alternativeradio.ui.utils;

import android.text.TextUtils;

/**
 * Created by djek-grif on 7/17/16.
 */
public class StringUtils {

    public static String getNotNull(String string){
        return string == null ? "" : string;
    }

    public static String cleanSongInfoString(String src){
        if(!TextUtils.isEmpty(src)){
            src = src.contains("(Live") ? src.substring(0, src.indexOf("(Live")) : src;
            src = src.contains("(live") ? src.substring(0, src.indexOf("(live")) : src;
            src = src.contains("(") && src.contains("mix") ? src.substring(0, src.indexOf("(")) : src;
            src = src.contains("_") ? src.replace("_", " ") : src;
            return src;
        }
        return "";
    }

}
