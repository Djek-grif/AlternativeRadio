package com.djekgrif.alternativeradio.common;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;

import com.djekgrif.alternativeradio.R;
import com.djekgrif.alternativeradio.manager.NotificationManager;
import com.djekgrif.alternativeradio.ui.utils.BundleKeys;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.List;

import timber.log.Timber;

/**
 * Created by djek-grif on 10/20/16.
 */

public class StreamService extends MediaBrowserServiceCompat {

    public static final String CUSTOM_EXIT_ACTION = "com.djekgrif.alternativeradio_EXIT_ACTION";
    public static final String CUSTOM_UPDATE_NOTIFICATION_DATA_ACTION = "com.djekgrif.alternativeradio_UPDATE_NOTIFICATION_DATA_ACTION";

    private SimpleExoPlayer player;
    private MediaSessionCompat mediaSessionCompat;
    private Uri currentUrlLink;

    private AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
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

    @Override
    public void onCreate() {
        super.onCreate();
        initPlayer();
        initMediaSession();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        player.stop();
        player.release();
        NotificationManager.removeNotifications();
        Timber.d("Destroy Stream service");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MediaButtonReceiver.handleIntent(mediaSessionCompat, intent);
        return super.onStartCommand(intent, flags, startId);
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

    private void initPlayer() {
        TrackSelector trackSelector = new DefaultTrackSelector(new Handler());
        player = ExoPlayerFactory.newSimpleInstance(getApplication(), trackSelector, new DefaultLoadControl());
        PlayerLogger eventLogger = new PlayerLogger();
        player.setAudioDebugListener(eventLogger);
//        player.setVideoDebugListener(eventLogger);
//        player.setId3Output(eventLogger);
        player.addListener(eventLogger);
        player.addListener(new ExoPlayer.EventListener() {
            @Override
            public void onLoadingChanged(boolean isLoading) {
                Timber.d("Loading: %s", String.valueOf(isLoading));
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

            }

            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPositionDiscontinuity() {

            }
        });
    }

    private void initMediaSession() {
        ComponentName mediaButtonReceiver = new ComponentName(getApplicationContext(), MediaButtonReceiver.class);
        mediaSessionCompat = new MediaSessionCompat(getApplicationContext(), "Tag", mediaButtonReceiver, null);
        mediaSessionCompat.setCallback(new MediaSessionCompat.Callback() {

            @Override
            public void onPlay() {
                super.onPlay();
                Timber.d("MediaSession Callback call play");
                if (successfullyRetrievedAudioFocus() && currentUrlLink != null) {

                    mediaSessionCompat.setActive(true);
                    setMediaPlaybackState(PlaybackStateCompat.STATE_PLAYING);
                    NotificationManager.initMediaSessionMetadata(mediaSessionCompat, "", "");
                    NotificationManager.showPlayingNotification(mediaSessionCompat);
                    preparePlayer(currentUrlLink);
                    player.setPlayWhenReady(true);
                }
            }

            @Override
            public void onPlayFromUri(Uri uri, Bundle extras) {
                super.onPlayFromUri(uri, extras);
                Timber.d("Change Url link to: %s", String.valueOf(uri));
                currentUrlLink = uri;
            }

            @Override
            public void onStop() {
                super.onStop();
                Timber.d("MediaSession Callback call onStop");
                if (player.getPlayWhenReady()) {
                    NotificationManager.showStopNotification(mediaSessionCompat);
                    setMediaPlaybackState(PlaybackStateCompat.STATE_STOPPED);
                    player.stop();
                }
            }

            @Override
            public void onCustomAction(String action, Bundle extras) {
                super.onCustomAction(action, extras);
                Timber.d("CustomAction:%s", action);
                if (CUSTOM_EXIT_ACTION.equals(action)) {
                    stopSelf();
                } else if (CUSTOM_UPDATE_NOTIFICATION_DATA_ACTION.equals(action) && extras != null) {
                    String songInfoDetails = extras.getString(BundleKeys.SONG_INFO_DETAILS);
                    String artistName = extras.getString(BundleKeys.ARTIST_NAME);
                    NotificationManager.initMediaSessionMetadata(mediaSessionCompat, artistName, songInfoDetails);
                    if (player.getPlayWhenReady()) {
                        NotificationManager.showPlayingNotification(mediaSessionCompat);
                    }else{
                        NotificationManager.showStopNotification(mediaSessionCompat);
                    }
                }
            }

            @Override
            public void onCommand(String command, Bundle extras, ResultReceiver cb) {
                super.onCommand(command, extras, cb);
                Timber.d("Command:%s", command);
            }

            @Override
            public void onSeekTo(long pos) {
                super.onSeekTo(pos);
            }
        });
        mediaSessionCompat.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        mediaButtonIntent.setClass(this, MediaButtonReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, mediaButtonIntent, 0);
        mediaSessionCompat.setMediaButtonReceiver(pendingIntent);
        setSessionToken(mediaSessionCompat.getSessionToken());

    }

    private void setMediaPlaybackState(int state) {
        PlaybackStateCompat.Builder playbackstateBuilder = new PlaybackStateCompat.Builder();
        if (state == PlaybackStateCompat.STATE_PLAYING) {
            playbackstateBuilder.setActions(PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_STOP);
        } else {
            playbackstateBuilder.setActions(PlaybackStateCompat.ACTION_STOP | PlaybackStateCompat.ACTION_PLAY);
        }
        playbackstateBuilder.setState(state, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 0);
        mediaSessionCompat.setPlaybackState(playbackstateBuilder.build());
    }

    private void preparePlayer(Uri uri) {
        // Measures bandwidth during playback. Can be null if not required.
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        // Produces DataSource instances through which media data is loaded.
        DefaultHttpDataSourceFactory defaultHttpDataSourceFactory = new DefaultHttpDataSourceFactory(Util.getUserAgent(this, "TestApp"), bandwidthMeter, DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS, DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS, true);
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, bandwidthMeter, defaultHttpDataSourceFactory);
        // Produces Extractor instances for parsing the media data.
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        // This is the MediaSource representing the media to be played.
        MediaSource mediaSource = new ExtractorMediaSource(uri, dataSourceFactory, extractorsFactory, null, null);
        player.prepare(mediaSource);
    }

    private boolean successfullyRetrievedAudioFocus() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.abandonAudioFocus(onAudioFocusChangeListener);
        int result = audioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        return result == AudioManager.AUDIOFOCUS_GAIN;
    }


}
