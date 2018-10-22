package com.djekgrif.alternativeradio.network.model;

import com.google.gson.annotations.SerializedName;

public class PrevSongInfo {

    @SerializedName("artist")
    private String artist;
    @SerializedName("title")
    private String song;
    @SerializedName("metadata")
    private String metadata;
    @SerializedName("album")
    private String album;
    @SerializedName("cover")
    private String cover;
    @SerializedName("itunes_url")
    private String itunesUrl;
    @SerializedName("google_url")
    private String googleUrl;
    @SerializedName("youtube_url")
    private String youtubeUrl;
    @SerializedName("last_modified")
    private String lastModified;

    public String getArtist() {
        return artist;
    }

    public String getSong() {
        return song;
    }

    public String getMetadata() {
        return metadata;
    }

    public String getAlbum() {
        return album;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getItunesUrl() {
        return itunesUrl;
    }

    public String getGoogleUrl() {
        return googleUrl;
    }

    public String getYoutubeUrl() {
        return youtubeUrl;
    }

    public String getLastModified() {
        return lastModified;
    }
}
