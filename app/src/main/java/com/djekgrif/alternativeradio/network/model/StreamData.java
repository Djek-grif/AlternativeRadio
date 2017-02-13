package com.djekgrif.alternativeradio.network.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by djek-grif on 2/1/17.
 */

public class StreamData {

    @SerializedName("url")
    private String url;
    @SerializedName("quality")
    private int quality;

    public String getUrl() {
        return url;
    }

    public int getQuality() {
        return quality;
    }
}
