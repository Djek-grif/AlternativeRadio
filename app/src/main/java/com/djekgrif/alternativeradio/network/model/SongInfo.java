package com.djekgrif.alternativeradio.network.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by djek-grif on 5/31/16.
 */
public class SongInfo {

    @SerializedName("artist")
    private String artist;
    @SerializedName("song")
    private String song;

    public String getArtist() {
        return artist;
    }

    public String getSong() {
        return song;
    }

}
