package com.djekgrif.alternativeradio.network.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.djekgrif.alternativeradio.ui.model.HomeListItem;

/**
 * Created by djek-grif on 1/8/17.
 */

public class RecentlyItem implements Parcelable, HomeListItem {

    private String artistName;
    private String trackName;
    private String image;

    public RecentlyItem(String artistName, String trackName, String image) {
        this.artistName = artistName;
        this.trackName = trackName;
        this.image = image;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getTrackName() {
        return trackName;
    }

    public String getImage() {
        return image;
    }


    @Override
    public int getType() {
        return RECENTLY_ITEM;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.artistName);
        dest.writeString(this.trackName);
        dest.writeString(this.image);
    }

    protected RecentlyItem(Parcel in) {
        this.artistName = in.readString();
        this.trackName = in.readString();
        this.image = in.readString();
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
