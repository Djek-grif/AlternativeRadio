package com.djekgrif.alternativeradio.network;

import com.djekgrif.alternativeradio.network.model.ConfigurationData;
import com.djekgrif.alternativeradio.network.model.SongInfoDetails;
import com.djekgrif.alternativeradio.ui.model.HomeListItem;

import java.util.List;

import rx.Observable;
import rx.functions.Action1;

/**
 * Created by djek-grif on 5/27/16.
 */
public interface ApiService {

    String FIREBASE_DATABASE_NODE_DATA = "data";
    String FIREBASE_DATABASE_NODE_STATIONS = "stations";

    Observable<SongInfoDetails> getCurrentSoundInfo(String radioInfoUrl, String searchUrl);
    Observable<List<HomeListItem>> getRecentlyList(String radioInfoUrl);
    void getConfigurationData(Action1<ConfigurationData> subscriber);
}
