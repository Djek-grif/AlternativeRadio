package com.djekgrif.alternativeradio.common;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.djekgrif.alternativeradio.App;
import com.djekgrif.alternativeradio.common.events.ClickActionButtonEvent;
import com.djekgrif.alternativeradio.common.events.StartPlayEvent;
import com.djekgrif.alternativeradio.common.events.StopPlayEvent;
import com.djekgrif.alternativeradio.common.events.StreamChangedEvent;
import com.djekgrif.alternativeradio.common.events.UpdateConfigurationDataEvent;
import com.djekgrif.alternativeradio.common.events.UpdateRecentlyListEvent;
import com.djekgrif.alternativeradio.common.events.UpdateSongInfoDetailsEvent;
import com.djekgrif.alternativeradio.common.events.UpdateStationsStateEvent;
import com.djekgrif.alternativeradio.manager.ConfigurationManager;
import com.djekgrif.alternativeradio.manager.ImageLoader;
import com.djekgrif.alternativeradio.manager.NotificationHelper;
import com.djekgrif.alternativeradio.manager.Preferences;
import com.djekgrif.alternativeradio.network.ApiService;
import com.djekgrif.alternativeradio.network.model.ConfigurationData;
import com.djekgrif.alternativeradio.network.model.CurrentTrackInfo;
import com.djekgrif.alternativeradio.network.model.SearchInfoDetails;
import com.djekgrif.alternativeradio.ui.model.HomeListItem;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by djek-grif on 10/20/16.
 */

public class StreamService extends BaseStreamService {

    public static final String CUSTOM_EXIT_ACTION = "com.djekgrif.alternativeradio_EXIT_ACTION";
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
        EventBus.getDefault().register(this);
        initPlayer();
        initMediaSession();
        streamDataUpdater = new StreamDataUpdater(apiService, configurationManager, preferences);
        streamDataUpdater.setUpdateSoundInfoListener(new StreamDataUpdaterListener() {

                    @Override
                    public void updateConfiguration(ConfigurationData configurationData) {
                        NotificationHelper.initMediaSessionMetadata(mediaSessionCompat);
//                        NotificationHelper.showStopNotification(mediaSessionCompat);
                        EventBus.getDefault().post(new UpdateStationsStateEvent(configurationData.getStations()));
                        streamDataUpdater.updateSoundInfo();
                    }

                    @Override
                    public void updateData(CurrentTrackInfo currentTrackInfo) {
                        NotificationHelper.updateMediaSessionMetadata(mediaSessionCompat, currentTrackInfo.getArtistName(), currentTrackInfo.getTrackName(), currentTrackInfo.getImageSmall(), imageLoader);
                        EventBus.getDefault().post(new UpdateSongInfoDetailsEvent(currentTrackInfo));
                        if (player.getPlayWhenReady()) {
                            NotificationHelper.showPlayingNotification(mediaSessionCompat, StreamService.this);
                        }
                    }

                    @Override
                    public void updateRecentlyList(List<HomeListItem> recentlyItems) {
                        EventBus.getDefault().post(new UpdateRecentlyListEvent(recentlyItems));
                    }
                });
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null && CUSTOM_EXIT_ACTION.equals(intent.getAction())){
            mediaSessionCompat.getController().getTransportControls().sendCustomAction(StreamService.CUSTOM_EXIT_ACTION, null);
        }else {
            MediaButtonReceiver.handleIntent(mediaSessionCompat, intent);
        }
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        player.stop();
        player.release();
        NotificationHelper.removeNotifications(this);
        streamDataUpdater.stopSoundInfoUpdater();
        Logger.d("Destroy Stream service", Logger.LIFECYCLE);
    }


    private void initPlayer() {
        TrackSelector trackSelector = new DefaultTrackSelector(new Handler());
        player = ExoPlayerFactory.newSimpleInstance(getApplication(), trackSelector, new DefaultLoadControl());//new DefaultLoadControl(new DefaultAllocator(true, C.DEFAULT_BUFFER_SEGMENT_SIZE)));
        PlayerLogger eventLogger = new PlayerLogger() {
            @Override
            public void onPlayerError(ExoPlaybackException error) {
                super.onPlayerError(error);
                stopPlay();
            }
        };
        player.setAudioDebugListener(eventLogger);
//        player.setVideoDebugListener(eventLogger);
//        player.setId3Output(eventLogger);
        player.addListener(eventLogger);
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

            @Override
            public void onStop() {
                super.onStop();
                stopPlay();
            }

            @Override
            public void onCustomAction(String action, Bundle extras) {
                super.onCustomAction(action, extras);
                if (CUSTOM_EXIT_ACTION.equals(action)) {
                    stopPlay();
                    NotificationHelper.removeNotifications(StreamService.this);
                    stopSelf();
                } else if (CUSTOM_CHANGE_STREAM_STATE.equals(action)) {
                    if (player.getPlayWhenReady()) {
                        stopPlay();
                    } else {
                        startPlay();
                    }
                }
            }

            @Override
            public void onCommand(String command, Bundle extras, ResultReceiver cb) {
                super.onCommand(command, extras, cb);
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
        if (player.getPlayWhenReady()) {
            unregisterAudioFocusListener();
            streamDataUpdater.stopSoundInfoUpdater();
            player.setPlayWhenReady(false);
            setMediaPlaybackState(PlaybackStateCompat.STATE_STOPPED);
//            NotificationHelper.showStopNotification(mediaSessionCompat);
            NotificationHelper.removeNotifications(this);
            EventBus.getDefault().post(new StopPlayEvent());
        }
    }

    private void startPlay() {
        if (isAudioFocus() && streamDataUpdater.isCurrentStreamDataValid()) {
            mediaSessionCompat.setActive(true);
            setMediaPlaybackState(PlaybackStateCompat.STATE_PLAYING);
            NotificationHelper.showPlayingNotification(mediaSessionCompat, this);
            preparePlayer(streamDataUpdater.getUriFromCurrentStreamData());
            player.setPlayWhenReady(true);
            EventBus.getDefault().post(new StartPlayEvent());
            streamDataUpdater.startSoundInfoUpdater();
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

    @Subscribe
    public void updateConfigurationData(UpdateConfigurationDataEvent event){
        streamDataUpdater.updateConfigurationData();
    }

    @Subscribe
    public void onClickActionButtonEvent(ClickActionButtonEvent clickActionButtonEvent) {
        if (player.getPlayWhenReady()) {
            stopPlay();
        } else {
            startPlay();
        }
    }

    @Subscribe
    public void onStreamChanged(StreamChangedEvent changedEvent){
        stopPlay();
        if (changedEvent.getChannel() != null && changedEvent.getChannel().getStreamUrls() != null) {
            streamDataUpdater.setCurrentChannel(changedEvent.getChannel());
            streamDataUpdater.setCurrentStreamData(changedEvent.getChannel().getStreamUrls().get(changedEvent.getChannel().getStreamUrls().size() > 1 ? 1 : 0));
            startPlay();
            Logger.d("Channel changed to: " + String.valueOf(changedEvent.getChannel().getName()), Logger.STREAM);
        }
    }


}
