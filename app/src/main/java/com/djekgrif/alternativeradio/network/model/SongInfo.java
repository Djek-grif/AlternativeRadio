package com.djekgrif.alternativeradio.network.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by djek-grif on 5/31/16.
 */
public class SongInfo {

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
    @SerializedName("prev_tracks")
    private List<PrevSongInfo> prevTracks;
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

    public String getItunesUrl() {
        return itunesUrl;
    }

    public String getGoogleUrl() {
        return googleUrl;
    }

    public String getYoutubeUrl() {
        return youtubeUrl;
    }

    public List<PrevSongInfo> getPrevTracks() {
        return prevTracks;
    }

    public String getLastModified() {
        return lastModified;
    }
}
