package com.djekgrif.alternativeradio.network;

import com.djekgrif.alternativeradio.network.model.ConfigurationData;
import com.djekgrif.alternativeradio.network.model.CurrentTrackInfo;

import rx.Observable;
import rx.functions.Action1;

/**
 * Created by djek-grif on 5/27/16.
 */
public interface ApiService {

    String FIREBASE_DATABASE_NODE_DATA = "data";
    String FIREBASE_DATABASE_NODE_STATIONS = "stations";
    String HTTP = "http://";
    String HTTPS = "http://";

    Observable<CurrentTrackInfo> getCurrentSoundInfo(String radioInfoUrl, String searchUrl);
    void getConfigurationData(Action1<ConfigurationData> subscriber);
}
