package com.djekgrif.alternativeradio.network;

import com.djekgrif.alternativeradio.network.model.ConfigurationData;
import com.djekgrif.alternativeradio.network.model.RecentlyItem;
import com.djekgrif.alternativeradio.network.model.SongInfoDetails;

import java.util.List;

import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by djek-grif on 5/27/16.
 */
public interface ApiService {

    String FIREBASE_DATABASE_NODE_DATA = "data";

    Subscription getCurrentSoundInfo(String radioInfoUrl, String searchUrl, Subscriber<SongInfoDetails> subscriber);
    Subscription getRecentlyList(String radioInfoUrl, Subscriber<List<RecentlyItem>> subscriber);
    void getConfigurationData(Action1<ConfigurationData> subscriber);
}
