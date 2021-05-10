package com.djekgrif.alternativeradio.network;

import android.app.Application;
import android.text.TextUtils;

import com.djekgrif.alternativeradio.common.Logger;
import com.djekgrif.alternativeradio.network.model.ConfigurationData;
import com.djekgrif.alternativeradio.network.model.CurrentTrackInfo;
import com.djekgrif.alternativeradio.network.model.SongInfo;
import com.djekgrif.alternativeradio.network.model.SearchInfoDetails;
import com.djekgrif.alternativeradio.ui.utils.StringUtils;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.URLEncoder;

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
    public Observable<CurrentTrackInfo> getCurrentSoundInfo(String radioInfoUrl, String searchUrl) {
        return radioInfoService.getSoundInfo(radioInfoUrl)
                .doOnUnsubscribe(() -> Logger.d("Unsubscribing subscription", Logger.LIFECYCLE))
                .flatMap(songInfo -> TextUtils.isEmpty(songInfo.getSong()) ?
                        getSongInfoDetailsWithImage(songInfo, searchUrl)
                        :
                        parseSongInfo(songInfo, radioInfoUrl)
                )
                .retry(2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Observable<CurrentTrackInfo> parseSongInfo(SongInfo songInfo, String radioInfoUrl) {
        String baseImageLink = radioInfoUrl.contains("/current.json") ? radioInfoUrl.replace("current.json", "") : "";
        return Observable.just(songInfo)
                .map(info -> {
                    CurrentTrackInfo currentTrackInfo = new CurrentTrackInfo();
                    currentTrackInfo.setArtistName(info.getArtist());
                    currentTrackInfo.setTrackName(info.getSong());
                    if(info.getCover() != null) {
                        currentTrackInfo.setImageBig((info.getCover().contains(HTTP) || info.getCover().contains(HTTPS)) ? info.getCover() : baseImageLink + info.getCover());
                    }
                    currentTrackInfo.setItunesUrl(info.getItunesUrl());
                    currentTrackInfo.setAlbum(info.getAlbum());
                    currentTrackInfo.setYoutubeUrl(info.getYoutubeUrl());
                    currentTrackInfo.setPrevTracks(info.getPrevTracks());
                    currentTrackInfo.setMetadata(info.getMetadata());
                    return currentTrackInfo;
                })
                .flatMap(result -> Observable.from(result.getPrevTracks())
                        .map(item -> {
                            if(item.getCover() != null) {
                                item.setCover((item.getCover().contains(HTTP) || item.getCover().contains(HTTPS)) ? item.getCover() : baseImageLink + item.getCover());
                            }
                            return item;
                        })
                        .toList()
                        .map(listWithImgs -> {
                            result.setPrevTracks(listWithImgs);
                            return result;
                        })
                );
    }

    private Observable<CurrentTrackInfo> getSongInfoDetailsWithImage(SongInfo songInfo, String searchUrl) {
        return Observable.zip(Observable.just(songInfo), songInfoService.getSongDetailsInfo(String.format(searchUrl, URLEncoder.encode(StringUtils.cleanSongInfoString(songInfo.getArtist()) + " " + StringUtils.cleanSongInfoString(songInfo.getSong())))).doOnError(e -> Logger.e("Error of getting info")), (songInfoItem, songInfoList) -> {
            CurrentTrackInfo currentTrackInfo = new CurrentTrackInfo();
            if (songInfoList != null && songInfoList.getResults() != null && !songInfoList.getResults().isEmpty()) {
                SearchInfoDetails songInfoDetails = songInfoList.getResults().get(0);
                currentTrackInfo.setImageBig(songInfoDetails.getArtworkUrl100());
                currentTrackInfo.setArtistName(TextUtils.isEmpty(songInfoDetails.getArtistName()) ? songInfo.getArtist() : songInfoDetails.getArtistName());
                currentTrackInfo.setTrackName(TextUtils.isEmpty(songInfoDetails.getTrackName()) ? songInfo.getSong() : songInfoDetails.getTrackName());
            } else {
                Logger.w("Bad search request for " + StringUtils.cleanSongInfoString(songInfo.getArtist()), Logger.SONG_INFO);
                currentTrackInfo.setArtistName(songInfo.getArtist());
                currentTrackInfo.setTrackName(songInfo.getSong());
            }
            return currentTrackInfo;
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
