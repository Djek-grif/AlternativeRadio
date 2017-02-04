package com.djekgrif.alternativeradio.network.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by djek-grif on 1/6/17.
 */

public class SongInfoList {

    @SerializedName("resultCount")
    private int resultCount;
    @SerializedName("results")
    private List<SongInfoDetails> results;

    public int getResultCount() {
        return resultCount;
    }

    public List<SongInfoDetails> getResults() {
        return results;
    }
}
