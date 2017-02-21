package com.djekgrif.alternativeradio.common;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.text.TextUtils;

import com.djekgrif.alternativeradio.R;
import com.djekgrif.alternativeradio.network.model.RecentlyItem;
import com.djekgrif.alternativeradio.network.model.SongInfoDetails;
import com.djekgrif.alternativeradio.network.model.StationData;
import com.djekgrif.alternativeradio.ui.utils.BundleKeys;
import com.djekgrif.alternativeradio.utils.IntentUtils;
import com.google.android.exoplayer2.SimpleExoPlayer;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by djek-grif on 2/21/17.
 */

public class BaseStreamService extends MediaBrowserServiceCompat {

    protected SimpleExoPlayer player;

    protected AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            Timber.i("focusChange to: %d", focusChange);
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_LOSS: {
                    if (player.getPlayWhenReady()) {
                        player.setPlayWhenReady(false);
                    }
                    break;
                }
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT: {
                    player.setPlayWhenReady(false);
                    break;
                }
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK: {
                    if (player != null) {
                        player.setVolume(0.3f);
                    }
                    break;
                }
                case AudioManager.AUDIOFOCUS_GAIN: {
                    if (player != null) {
                        if (!player.getPlayWhenReady()) {
                            player.setPlayWhenReady(true);
                        }
                        player.setVolume(1.0f);
                    }
                    break;
                }
            }
        }
    };

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        if (TextUtils.equals(clientPackageName, getPackageName())) {
            return new BrowserRoot(getString(R.string.app_name), null);
        }
        return null;
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        result.sendResult(null);
    }

    protected void sendStationsAction(List<StationData> stations){
        Intent intent = new Intent(IntentUtils.UPDATE_STATIONS_STATE);
        intent.putParcelableArrayListExtra(BundleKeys.STATION_LIST, (ArrayList<StationData>) stations);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    protected void sendUpdateInfoAction(SongInfoDetails songInfoDetails){
        Intent intent = new Intent(IntentUtils.UPDATE_SONG_INFO_DETAILS);
        intent.putExtra(BundleKeys.SONG_INFO_DETAILS, songInfoDetails);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    protected void sendRecentlyInfoAction(List<RecentlyItem> recentlyItems){
        Intent intent = new Intent(IntentUtils.UPDATE_RECENTLY_LIST);
        intent.putParcelableArrayListExtra(BundleKeys.RECENTLY_LIST, (ArrayList<RecentlyItem>) recentlyItems);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    protected void sendStartPlayAction(){
        Intent intent = new Intent(IntentUtils.START_PLAY);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    protected void sendStopPlayAction(){
        Intent intent = new Intent(IntentUtils.STOP_PLAY);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

}