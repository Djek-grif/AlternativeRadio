package com.djekgrif.alternativeradio.network.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by djek-grif on 1/24/17.
 */

public class Channel {

    @SerializedName("streamUrls")
    private List<StreamData> streamUrls;
    @SerializedName("songInfoUrl")
    private String songInfoUrl;
    @SerializedName("recentlyInfoUrl")
    private String recentlyInfoUrl;
    @SerializedName("name")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Channel() {
        this.streamUrls = new ArrayList<>();
    }

    public List<StreamData> getStreamUrls() {
        return streamUrls;
    }

    public void setStreamUrls(List<StreamData> streamUrls) {
        this.streamUrls = streamUrls;
    }

    public String getSongInfoUrl() {
        return songInfoUrl;
    }

    public void setSongInfoUrl(String songInfoUrl) {
        this.songInfoUrl = songInfoUrl;
    }

    public String getRecentlyInfoUrl() {
        return recentlyInfoUrl;
    }

    public void setRecentlyInfoUrl(String recentlyInfoUrl) {
        this.recentlyInfoUrl = recentlyInfoUrl;
    }
}
