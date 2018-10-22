package com.djekgrif.alternativeradio.network.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by djek-grif on 7/20/16.
 */
public class SearchInfoDetails implements Parcelable {

    @SerializedName("artistName")
    private String artistName;
    @SerializedName("trackName")
    private String trackName;
    @SerializedName("trackCensoredName")
    private String trackCensoredName;
    @SerializedName("country")
    private String country;
    @SerializedName("artworkUrl30")
    private String artworkUrl30;
    @SerializedName("artworkUrl60")
    private String artworkUrl60;
    @SerializedName("artworkUrl100")
    private String artworkUrl100;

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

    public String getTrackCensoredName() {
        return trackCensoredName;
    }

    public String getCountry() {
        return country;
    }

    public String getArtworkUrl30() {
        return artworkUrl30;
    }

    public String getArtworkUrl60() {
        return artworkUrl60;
    }

    public String getArtworkUrl100() {
        return artworkUrl100;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.artistName);
        dest.writeString(this.trackName);
        dest.writeString(this.trackCensoredName);
        dest.writeString(this.country);
        dest.writeString(this.artworkUrl30);
        dest.writeString(this.artworkUrl60);
        dest.writeString(this.artworkUrl100);
    }

    public SearchInfoDetails() {
    }

    protected SearchInfoDetails(Parcel in) {
        this.artistName = in.readString();
        this.trackName = in.readString();
        this.trackCensoredName = in.readString();
        this.country = in.readString();
        this.artworkUrl30 = in.readString();
        this.artworkUrl60 = in.readString();
        this.artworkUrl100 = in.readString();
    }

    public static final Parcelable.Creator<SearchInfoDetails> CREATOR = new Parcelable.Creator<SearchInfoDetails>() {
        @Override
        public SearchInfoDetails createFromParcel(Parcel source) {
            return new SearchInfoDetails(source);
        }

        @Override
        public SearchInfoDetails[] newArray(int size) {
            return new SearchInfoDetails[size];
        }
    };
}
