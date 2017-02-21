package com.djekgrif.alternativeradio.network.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by djek-grif on 1/24/17.
 */

public class Channel implements Parcelable {

    @SerializedName("id")
    private long id;
    @SerializedName("streamUrls")
    private List<StreamData> streamUrls;
    @SerializedName("songInfoUrl")
    private String songInfoUrl;
    @SerializedName("recentlyInfoUrl")
    private String recentlyInfoUrl;
    @SerializedName("name")
    private String name;

    public long getId() {
        return id;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Channel channel = (Channel) o;

        return id == channel.id;

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
        dest.writeList(this.streamUrls);
        dest.writeString(this.songInfoUrl);
        dest.writeString(this.recentlyInfoUrl);
        dest.writeString(this.name);
    }

    protected Channel(Parcel in) {
        this.streamUrls = new ArrayList<StreamData>();
        in.readList(this.streamUrls, StreamData.class.getClassLoader());
        this.songInfoUrl = in.readString();
        this.recentlyInfoUrl = in.readString();
        this.name = in.readString();
    }

    public static final Parcelable.Creator<Channel> CREATOR = new Parcelable.Creator<Channel>() {
        @Override
        public Channel createFromParcel(Parcel source) {
            return new Channel(source);
        }

        @Override
        public Channel[] newArray(int size) {
            return new Channel[size];
        }
    };
}
