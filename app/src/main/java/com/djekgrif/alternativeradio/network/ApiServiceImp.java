package com.djekgrif.alternativeradio.network;

import com.djekgrif.alternativeradio.network.model.ConfigurationData;
import com.djekgrif.alternativeradio.network.model.RecentlyItem;
import com.djekgrif.alternativeradio.network.model.SongInfoDetails;
import com.djekgrif.alternativeradio.ui.utils.StringUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by djek-grif on 5/30/16.
 */
public class ApiServiceImp implements ApiService {

    public static final String RADIO_PLAYER_URL = "http://radiopleer.com";

    private RadioInfoService radioInfoService;
    private SongInfoService songInfoService;
    private FirebaseDatabase firebaseDatabase;

    public ApiServiceImp(RadioInfoService radioInfoService, SongInfoService songInfoService) {
        this.radioInfoService = radioInfoService;
        this.songInfoService = songInfoService;
        this.firebaseDatabase = FirebaseDatabase.getInstance();
        this.firebaseDatabase.setPersistenceEnabled(false);
    }

    @Override
    public Subscription getCurrentSoundInfo(String radioInfoUrl, String searchUrl, Subscriber<SongInfoDetails> subscriber) {
        return radioInfoService.getSoundInfo(radioInfoUrl)
                .doOnUnsubscribe(() -> Timber.d("Unsubscribing subscription from onCreate()"))
                .flatMap(songInfo -> Observable.zip(Observable.just(songInfo), songInfoService.getSongDetailsInfo(String.format(searchUrl, StringUtils.getNotNullString(songInfo.getArtist())
                        + " " + StringUtils.getNotNullString(songInfo.getSong()))), (songInfoItem, songInfoList) -> {
                    SongInfoDetails songInfoDetails = null;
                    if (songInfoList != null && songInfoList.getResults() != null && !songInfoList.getResults().isEmpty()) {
                        songInfoDetails = songInfoList.getResults().get(0);
                    } else {
                        Timber.w("Bad search for %s", StringUtils.getNotNullString(songInfo.getArtist()));
                        songInfoDetails = new SongInfoDetails();
                        songInfoDetails.setArtistName(songInfo.getArtist());
                        songInfoDetails.setTrackName(songInfo.getSong());
                    }
                    return songInfoDetails;
                }))
                .retry(2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    @Override
    public void getConfigurationData(Action1<ConfigurationData> action1) {
        DatabaseReference reference = firebaseDatabase.getReference(ApiServiceImp.FIREBASE_DATABASE_NODE_DATA);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ConfigurationData configurationData = dataSnapshot.getValue(ConfigurationData.class);
                action1.call(configurationData);
                Timber.d("Configuration data is received");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Timber.e(databaseError.toException(), "Error get Configuration :(");
            }
        });
    }

    @Override
    public Subscription getRecentlyList(String radioInfoUrl, Subscriber<List<RecentlyItem>> subscriber) {
        return radioInfoService.getRecentlyList(radioInfoUrl)
                .retry(2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(response -> {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.byteStream()));
                    List<RecentlyItem> recentlyItems = new ArrayList<>();
                    String line;
                    try {
                        while ((line = reader.readLine()) != null) {
                            line = line.replace("<li>", "").replace("</li>", "");
                            int indexFirstSpace = line.indexOf(" ");
                            String time = line.substring(0, indexFirstSpace);
                            line = line.substring(indexFirstSpace, line.length());
                            int indexDash = line.indexOf("-");
                            String name = line.substring(0, indexDash);
                            String track = line.substring(indexDash +1, line.length());
                            recentlyItems.add(new RecentlyItem(name, track, time));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return recentlyItems;
                })
                .subscribe(subscriber);


    }
}
