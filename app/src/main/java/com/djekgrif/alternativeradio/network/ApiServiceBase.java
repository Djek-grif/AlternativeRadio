package com.djekgrif.alternativeradio.network;

import android.text.TextUtils;

import com.djekgrif.alternativeradio.common.Logger;
import com.djekgrif.alternativeradio.network.model.ConfigurationData;
import com.djekgrif.alternativeradio.network.model.RecentlyItem;
import com.djekgrif.alternativeradio.network.model.SongInfoDetails;
import com.djekgrif.alternativeradio.ui.model.HomeListItem;
import com.djekgrif.alternativeradio.ui.utils.StringUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by djek-grif on 5/30/16.
 */
public class ApiServiceBase implements ApiService {

    private RadioInfoService radioInfoService;
    private SongInfoService songInfoService;
    protected FirebaseDatabase firebaseDatabase;

    public ApiServiceBase(RadioInfoService radioInfoService, SongInfoService songInfoService) {
        this.radioInfoService = radioInfoService;
        this.songInfoService = songInfoService;
        this.firebaseDatabase = FirebaseDatabase.getInstance();
        this.firebaseDatabase.setPersistenceEnabled(false);
    }

    @Override
    public Observable<SongInfoDetails> getCurrentSoundInfo(String radioInfoUrl, String searchUrl) {
        return radioInfoService.getSoundInfo(radioInfoUrl)
                .doOnUnsubscribe(() -> Logger.d("Unsubscribing subscription from onCreate()", Logger.LIFECYCLE))
                .flatMap(songInfo -> Observable.zip(Observable.just(songInfo), songInfoService.getSongDetailsInfo(
                        String.format(searchUrl, URLEncoder.encode(StringUtils.cleanSongInfoString(songInfo.getArtist()) + " " + StringUtils.cleanSongInfoString(songInfo.getSong())))), (songInfoItem, songInfoList) -> {
                    SongInfoDetails songInfoDetails = null;
                    if (songInfoList != null && songInfoList.getResults() != null && !songInfoList.getResults().isEmpty()) {
                        songInfoDetails = songInfoList.getResults().get(0);
                    } else {
                        Logger.w("Bad search request for " + StringUtils.cleanSongInfoString(songInfo.getArtist()), Logger.SONG_INFO);
                        songInfoDetails = new SongInfoDetails();
                    }
                    songInfoDetails.setArtistName(songInfo.getArtist());
                    songInfoDetails.setTrackName(songInfo.getSong());
                    return songInfoDetails;
                }))
                .retry(2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<List<HomeListItem>> getRecentlyList(String radioInfoUrl) {
        return radioInfoService.getRecentlyList(radioInfoUrl)
                .retry(2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(response -> {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.byteStream()));
                    List<HomeListItem> recentlyItems = new ArrayList<>();
                    String line;
                    try {
                        while ((line = reader.readLine()) != null) {
                            line = line.replace("<li>", "").replace("</li>", "");
                            int indexFirstSpace = line.indexOf(" ");
                            String time = line.substring(0, indexFirstSpace);
                            line = line.substring(indexFirstSpace, line.length());
                            if(line.contains("-")) {
                                int indexDash = line.indexOf("-");
                                String name = indexDash > 0 ? line.substring(0, indexDash) : line;
                                String track = line.substring(indexDash + 1, line.length());
                                recentlyItems.add(new RecentlyItem(name, track, time));
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return recentlyItems;
                });
    }

    public <T> void getDataSnapshot(String nodeRef, Class classType, Action1<T> listener) {
        DatabaseReference reference = firebaseDatabase.getReference(nodeRef);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.call((T) dataSnapshot.getValue(classType));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Logger.e(databaseError.toException(), "Error of get data from remote DB", Logger.DB_REMOTE);
                listener.call(null);
            }
        });
    }

    @Override
    public void getConfigurationData(Action1<ConfigurationData> action1) {
        getDataSnapshot(FIREBASE_DATABASE_NODE_DATA, ConfigurationData.class, (Action1<ConfigurationData>) configurationData -> {
            if (configurationData != null) {
                action1.call(configurationData);
                Logger.d("Configuration data is received from remote DB", Logger.DB_REMOTE);
            }
        });
    }
}
