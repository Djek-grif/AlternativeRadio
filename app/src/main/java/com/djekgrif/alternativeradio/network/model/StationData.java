package com.djekgrif.alternativeradio.network.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.bignerdranch.expandablerecyclerview.model.Parent;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by djek-grif on 1/31/17.
 */

public class StationData implements Parent<Channel>, Parcelable{

    public static final int PUBLIC_STATUS = 1;
    public static final int PENDING_STATUS = 0;

    @SerializedName("id")
    private long id;
    @SerializedName("channels")
    private ArrayList<Channel> channels;
    @SerializedName("name")
    private String name;
    @SerializedName("iconUrl")
    private String iconUrl;
    @SerializedName("status")
    private int status;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setChannels(ArrayList<Channel> channels) {
        this.channels = channels;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public String getName() {
        return name;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isPublic(){
        return status == PUBLIC_STATUS;
    }

    @Override
    public List<Channel> getChildList() {
        return channels;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StationData that = (StationData) o;

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
        dest.writeList(this.channels);
        dest.writeString(this.name);
    }

    public StationData() {
    }

    protected StationData(Parcel in) {
        this.channels = new ArrayList<Channel>();
        in.readList(this.channels, Channel.class.getClassLoader());
        this.name = in.readString();
    }

    public static final Creator<StationData> CREATOR = new Creator<StationData>() {
        @Override
        public StationData createFromParcel(Parcel source) {
            return new StationData(source);
        }

        @Override
        public StationData[] newArray(int size) {
            return new StationData[size];
        }
    };
}
