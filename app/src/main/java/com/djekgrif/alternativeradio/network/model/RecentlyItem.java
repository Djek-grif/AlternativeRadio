package com.djekgrif.alternativeradio.network.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by djek-grif on 1/8/17.
 */

public class RecentlyItem implements Parcelable {

    private String artistName;
    private String trackName;
    private String time;

    public RecentlyItem(String artistName, String trackName, String time) {
        this.artistName = artistName;
        this.trackName = trackName;
        this.time = time;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getTrackName() {
        return trackName;
    }

    public String getTime() {
        return time;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.artistName);
        dest.writeString(this.trackName);
        dest.writeString(this.time);
    }

    protected RecentlyItem(Parcel in) {
        this.artistName = in.readString();
        this.trackName = in.readString();
        this.time = in.readString();
    }

    public static final Parcelable.Creator<RecentlyItem> CREATOR = new Parcelable.Creator<RecentlyItem>() {
        @Override
        public RecentlyItem createFromParcel(Parcel source) {
            return new RecentlyItem(source);
        }

        @Override
        public RecentlyItem[] newArray(int size) {
            return new RecentlyItem[size];
        }
    };
}
