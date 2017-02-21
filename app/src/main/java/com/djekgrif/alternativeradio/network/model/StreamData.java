package com.djekgrif.alternativeradio.network.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by djek-grif on 2/1/17.
 */

public class StreamData implements Parcelable {

    @SerializedName("id")
    private long id;
    @SerializedName("url")
    private String url;
    @SerializedName("quality")
    private int quality;

    public long getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public int getQuality() {
        return quality;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StreamData that = (StreamData) o;

        return id == that.id;

    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
        dest.writeInt(this.quality);
    }

    public StreamData() {
    }

    protected StreamData(Parcel in) {
        this.url = in.readString();
        this.quality = in.readInt();
    }

    public static final Parcelable.Creator<StreamData> CREATOR = new Parcelable.Creator<StreamData>() {
        @Override
        public StreamData createFromParcel(Parcel source) {
            return new StreamData(source);
        }

        @Override
        public StreamData[] newArray(int size) {
            return new StreamData[size];
        }
    };
}
