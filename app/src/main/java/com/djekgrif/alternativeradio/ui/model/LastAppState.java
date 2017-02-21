package com.djekgrif.alternativeradio.ui.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.djekgrif.alternativeradio.network.model.Channel;
import com.djekgrif.alternativeradio.network.model.StationData;
import com.djekgrif.alternativeradio.network.model.StreamData;
import com.google.gson.annotations.SerializedName;

/**
 * Created by djek-grif on 2/19/17.
 */

public class LastAppState implements Parcelable {

    @SerializedName("stationData")
    private StationData stationData;
    @SerializedName("channel")
    private Channel channel;
    @SerializedName("streamData")
    private StreamData streamData;

    public StationData getStationData() {
        return stationData;
    }

    public Channel getChannel() {
        return channel;
    }

    public StreamData getStreamData() {
        return streamData;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.stationData, flags);
        dest.writeParcelable(this.channel, flags);
        dest.writeParcelable(this.streamData, flags);
    }

    public LastAppState() {
    }

    protected LastAppState(Parcel in) {
        this.stationData = in.readParcelable(StationData.class.getClassLoader());
        this.channel = in.readParcelable(Channel.class.getClassLoader());
        this.streamData = in.readParcelable(StreamData.class.getClassLoader());
    }

    public static final Parcelable.Creator<LastAppState> CREATOR = new Parcelable.Creator<LastAppState>() {
        @Override
        public LastAppState createFromParcel(Parcel source) {
            return new LastAppState(source);
        }

        @Override
        public LastAppState[] newArray(int size) {
            return new LastAppState[size];
        }
    };
}
