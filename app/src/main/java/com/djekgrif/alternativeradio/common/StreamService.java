package com.djekgrif.alternativeradio.common;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.djekgrif.alternativeradio.App;
import com.djekgrif.alternativeradio.manager.ConfigurationManager;
import com.djekgrif.alternativeradio.manager.ImageLoader;
import com.djekgrif.alternativeradio.manager.NotificationManager;
import com.djekgrif.alternativeradio.manager.Preferences;
import com.djekgrif.alternativeradio.network.ApiService;
import com.djekgrif.alternativeradio.network.model.Channel;
import com.djekgrif.alternativeradio.network.model.ConfigurationData;
import com.djekgrif.alternativeradio.network.model.RecentlyItem;
import com.djekgrif.alternativeradio.network.model.SongInfoDetails;
import com.djekgrif.alternativeradio.ui.utils.BundleKeys;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
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

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Created by djek-grif on 10/20/16.
 */

public class StreamService extends BaseStreamService {

    public static final String CUSTOM_EXIT_ACTION = "com.djekgrif.alternativeradio_EXIT_ACTION";
    public static final String CUSTOM_UPDATE_NOTIFICATION_DATA_ACTION = "com.djekgrif.alternativeradio_UPDATE_NOTIFICATION_DATA_ACTION";
    public static final String CUSTOM_STREAM_CHANGED = "com.djekgrif.alternativeradio_CUSTOM_STREAM_CHANGED";
    public static final String CUSTOM_CHANGE_STREAM_STATE = "com.djekgrif.alternativeradio_CUSTOM_CHANGE_STREAM_STATE";

    private MediaSessionCompat mediaSessionCompat;
    private StreamDataUpdater streamDataUpdater;

    @Inject
    protected ImageLoader imageLoader;
    @Inject
    protected ApiService apiService;
    @Inject
    protected ConfigurationManager configurationManager;
    @Inject
    protected Preferences preferences;

    @Override
    public void onCreate() {
        super.onCreate();
        App.getInstance().getAppComponent().inject(this);
        initPlayer();
        initMediaSession();
        streamDataUpdater = new StreamDataUpdater(new StreamDataUpdaterListener() {
            @Override
            public void updateConfiguration(ConfigurationData configurationData) {
                streamDataUpdater.startSoundInfoUpdater();
                sendStationsAction(configurationData.getStations());
                NotificationManager.initMediaSessionMetadata(mediaSessionCompat);
            }

            @Override
            public void updateData(SongInfoDetails songInfoDetails) {
                sendUpdateInfoAction(songInfoDetails);
                NotificationManager.updateMediaSessionMetadata(mediaSessionCompat,
                        songInfoDetails.getArtistName(), songInfoDetails.getTrackName(), songInfoDetails.getArtworkUrl100(), imageLoader);
                if(player.getPlayWhenReady()){
                    NotificationManager.showPlayingNotification(mediaSessionCompat);
                }else{
                    NotificationManager.showStopNotification(mediaSessionCompat);
                }
            }

            @Override
            public void updateRecentlyList(List<RecentlyItem> recentlyItems) {
                sendRecentlyInfoAction(recentlyItems);
            }
        }, apiService, configurationManager, preferences);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        player.stop();
        player.release();
        NotificationManager.removeNotifications();
        Timber.d("Destroy Stream service");
        streamDataUpdater.stopSoundInfoUpdater();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MediaButtonReceiver.handleIntent(mediaSessionCompat, intent);
        return super.onStartCommand(intent, flags, startId);
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
                Timber.d("StateChanged to: %s", String.valueOf(playbackState));
            }

            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest) {
                Timber.d("TimelineChanged: %s", String.valueOf(timeline));
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Timber.d(error.getRendererException(), "PlayerError: %s", String.valueOf(error));
            }

            @Override
            public void onPositionDiscontinuity() {
                Timber.d("PositionDiscontinuity");
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
                startPlay();
            }

            //
            @Override
            public void onStop() {
                super.onStop();
                stopPlay();
            }

            @Override
            public void onCustomAction(String action, Bundle extras) {
                super.onCustomAction(action, extras);
                Timber.d("CustomAction:%s", action);
                if (CUSTOM_EXIT_ACTION.equals(action)) {
                    NotificationManager.removeNotifications();
                    stopSelf();
                } else if (CUSTOM_UPDATE_NOTIFICATION_DATA_ACTION.equals(action) && extras != null) {
                    if (player.getPlayWhenReady()) {
                        NotificationManager.showPlayingNotification(mediaSessionCompat);
                    } else {
                        NotificationManager.showStopNotification(mediaSessionCompat);
                    }
                } else if (CUSTOM_STREAM_CHANGED.equals(action) && extras != null) {
                    stopPlay();
                    Channel channel = extras.getParcelable(BundleKeys.CHANNEL);
                    if (channel != null && channel.getStreamUrls() != null) {
                        streamDataUpdater.setCurrentChannel(channel);
                        streamDataUpdater.setCurrentStreamData(channel.getStreamUrls().get(channel.getStreamUrls().size() > 1 ? 1 : 0));
                        streamDataUpdater.startSoundInfoUpdater();
                        startPlay();
                        Timber.d("Channel changed to: %s", String.valueOf(channel.getName()));
                    }
                } else if (CUSTOM_CHANGE_STREAM_STATE.equals(action)) {
                    if (player.getPlayWhenReady()) {
                        stopPlay();
                    } else {
                        startPlay();
                    }
                }
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

    private void stopPlay() {
        Timber.d("MediaSession Callback call onStop");
        if (player.getPlayWhenReady()) {
            player.setPlayWhenReady(false);
            setMediaPlaybackState(PlaybackStateCompat.STATE_STOPPED);
            NotificationManager.showStopNotification(mediaSessionCompat);
            sendStopPlayAction();
        }
    }

    private void startPlay() {
        Timber.d("MediaSession Callback call play");
        if (isSuccessfullyRetrievedAudioFocus() && streamDataUpdater.isCurrentStreamDataValid()) {
            mediaSessionCompat.setActive(true);
            setMediaPlaybackState(PlaybackStateCompat.STATE_PLAYING);
            NotificationManager.showPlayingNotification(mediaSessionCompat);
            preparePlayer(streamDataUpdater.getUriFromCurrentStreamData());
            player.setPlayWhenReady(true);
            sendStartPlayAction();
        }
    }

    private void setMediaPlaybackState(int state) {
        PlaybackStateCompat.Builder playbackStateBuilder = new PlaybackStateCompat.Builder();
        if (state == PlaybackStateCompat.STATE_PLAYING) {
            playbackStateBuilder.setActions(PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_STOP);
        } else {
            playbackStateBuilder.setActions(PlaybackStateCompat.ACTION_STOP | PlaybackStateCompat.ACTION_PLAY);
        }
        playbackStateBuilder.setState(state, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 0);
        mediaSessionCompat.setPlaybackState(playbackStateBuilder.build());
    }

    private void preparePlayer(Uri uri) {
        // Measures bandwidth during playback. Can be null if not required.
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        // Produces DataSource instances through which media data is loaded.
        DefaultHttpDataSourceFactory defaultHttpDataSourceFactory = new DefaultHttpDataSourceFactory(Util.getUserAgent(this, "AlternativeRadio"), bandwidthMeter, DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS, DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS, true);
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, bandwidthMeter, defaultHttpDataSourceFactory);
        // Produces Extractor instances for parsing the media data.
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        // This is the MediaSource representing the media to be played.
        MediaSource mediaSource = new ExtractorMediaSource(uri, dataSourceFactory, extractorsFactory, null, null);
        player.prepare(mediaSource);
    }

    private boolean isSuccessfullyRetrievedAudioFocus() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.abandonAudioFocus(onAudioFocusChangeListener);
        int result = audioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        return result == AudioManager.AUDIOFOCUS_GAIN;
    }


}
