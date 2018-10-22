package com.djekgrif.alternativeradio.network.model;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CurrentTrackInfo {

    private String artistName;
    private String trackName;
    private String imageBig;
    private String imageSmall;
    private String metadata;
    private String album;
    private String itunesUrl;
    private String googleUrl;
    private String youtubeUrl;
    private List<PrevSongInfo> prevTracks;

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public String getImageBig() {
        return imageBig;
    }

    public void setImageBig(String imageBig) {
        this.imageBig = imageBig;
    }

    public String getImageSmall() {
        return imageSmall;
    }

    public void setImageSmall(String imageSmall) {
        this.imageSmall = imageSmall;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getItunesUrl() {
        return itunesUrl;
    }

    public void setItunesUrl(String itunesUrl) {
        this.itunesUrl = itunesUrl;
    }

    public String getGoogleUrl() {
        return googleUrl;
    }

    public void setGoogleUrl(String googleUrl) {
        this.googleUrl = googleUrl;
    }

    public String getYoutubeUrl() {
        return youtubeUrl;
    }

    public void setYoutubeUrl(String youtubeUrl) {
        this.youtubeUrl = youtubeUrl;
    }

    public List<PrevSongInfo> getPrevTracks() {
        return prevTracks;
    }

    public void setPrevTracks(List<PrevSongInfo> prevTracks) {
        this.prevTracks = prevTracks;
    }
}
