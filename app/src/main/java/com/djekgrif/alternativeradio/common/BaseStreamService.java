package com.djekgrif.alternativeradio.common;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.text.TextUtils;

import com.djekgrif.alternativeradio.R;
import com.google.android.exoplayer2.SimpleExoPlayer;

import java.util.List;

/**
 * Created by djek-grif on 2/21/17.
 */

public abstract class BaseStreamService extends MediaBrowserServiceCompat {

    protected SimpleExoPlayer player;
    protected AudioManager audioManager;

    @Override
    public void onCreate() {
        super.onCreate();
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    }

    protected AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            Logger.i("Audio focus changed to: " + focusChange, Logger.PLAYER);
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

    protected boolean isAudioFocus() {
        unregisterAudioFocusListener();
        int result = audioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        return result == AudioManager.AUDIOFOCUS_GAIN;
    }

    protected void unregisterAudioFocusListener(){
        audioManager.abandonAudioFocus(onAudioFocusChangeListener);
    }

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

}