package com.djekgrif.alternativeradio.presenter;

import android.content.ComponentName;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.djekgrif.alternativeradio.R;
import com.djekgrif.alternativeradio.common.StreamService;
import com.djekgrif.alternativeradio.manager.ConfigurationManager;
import com.djekgrif.alternativeradio.manager.ImageLoader;
import com.djekgrif.alternativeradio.network.ApiService;
import com.djekgrif.alternativeradio.network.SimpleSubscriber;
import com.djekgrif.alternativeradio.network.model.Channel;
import com.djekgrif.alternativeradio.network.model.RecentlyItem;
import com.djekgrif.alternativeradio.network.model.SongInfoDetails;
import com.djekgrif.alternativeradio.network.model.StreamData;
import com.djekgrif.alternativeradio.ui.adapters.ItemSelectListener;
import com.djekgrif.alternativeradio.ui.utils.BundleKeys;
import com.djekgrif.alternativeradio.ui.utils.ToastUlils;
import com.djekgrif.alternativeradio.view.HomeFragmentView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import timber.log.Timber;

/**
 * Created by djek-grif on 5/27/16.
 */
public class HomeFragmentPresenterImp implements HomeFragmentPresenter {

    private static final int DELAY_TO_EXIT = 1000 * 3;

    private ApiService apiService;
    private MediaBrowserCompat mediaBrowserCompat;
    private ConfigurationManager configurationManager;

    private MediaControllerCompat mediaControllerCompat;
    private ImageLoader imageLoader;
    private boolean onBackPressedFlag;
    private HomeFragmentView homeFragmentView;
    private String searchMediaUrl;
    private Channel currentChannel;
    private StreamData currentStreamData;
    private Handler infoUpdaterHandler = new Handler();
    private Runnable updateInfoRunnable = new Runnable() {
        @Override
        public void run() {
            updateSoundInfo();
            infoUpdaterHandler.postDelayed(updateInfoRunnable, 1000 * 20);
        }
    };

    public HomeFragmentPresenterImp(HomeFragmentView homeFragmentView,
                                    ApiService apiService, ImageLoader imageLoader, ConfigurationManager configurationManager) {
        this.homeFragmentView = homeFragmentView;
        this.apiService = apiService;
        this.imageLoader = imageLoader;
        this.configurationManager = configurationManager;
        this.mediaBrowserCompat = new MediaBrowserCompat(homeFragmentView.getContext(), new ComponentName(homeFragmentView.getContext(), StreamService.class),
                new MediaBrowserCompat.ConnectionCallback() {

                    @Override
                    public void onConnectionFailed() {
                        super.onConnectionFailed();
                        Timber.e("Error of connection mediaBrowserCompat ");
                    }

                    @Override
                    public void onConnected() {
                        super.onConnected();
                        try {
                            mediaControllerCompat = new MediaControllerCompat(homeFragmentView.getContext(), mediaBrowserCompat.getSessionToken());
                            mediaControllerCompat.registerCallback(new MediaControllerCompat.Callback() {
                                @Override
                                public void onPlaybackStateChanged(PlaybackStateCompat state) {
                                    super.onPlaybackStateChanged(state);
                                    if (state != null) {
                                        Timber.d("Update action button to state: %d", state.getState());
                                        homeFragmentView.updateActionButton(state.getState());
                                    }
                                }
                            });
                            homeFragmentView.setSupportMediaController(mediaControllerCompat);
                            homeFragmentView.updateActionButton(mediaControllerCompat.getPlaybackState() != null &&
                                    mediaControllerCompat.getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING
                                    ? PlaybackStateCompat.STATE_PLAYING : PlaybackStateCompat.STATE_STOPPED);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }, homeFragmentView.getIntent().getExtras());

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        this.mediaBrowserCompat.connect();
        configurationManager.getConfigurationData(configurationData -> {
            Channel channel = configurationData.getStations().get(0).getChannels().get(0);
            StreamData streamData = channel.getStreamUrls().get(channel.getStreamUrls().size() > 1 ? 1 : 0);
            updateCurrentStation(channel, streamData);
            searchMediaUrl = configurationData.getSearchMediaUrl();
            homeFragmentView.updateStationList(configurationData.getStations());
            homeFragmentView.setUpUI();
        });
    }

    @Override
    public void onPause() {
        stopSoundInfoUpdater();
    }

    @Override
    public void onResume() {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        currentStreamData = currentChannel.getStreamUrls().get(menuItem.getItemId());
        changeStream();
        return true;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if (currentChannel != null) {
            for (int i = 0; i < currentChannel.getStreamUrls().size(); i++) {
                int key = currentChannel.getStreamUrls().get(i).getQuality();
                menu.add(0, i, Menu.NONE, String.format(homeFragmentView.getContext().getString(R.string.kbs), String.valueOf(key)));
            }
        }
    }

    private void startSoundInfoUpdater() {
        infoUpdaterHandler.removeCallbacks(updateInfoRunnable);
        infoUpdaterHandler.post(updateInfoRunnable);
    }

    private void stopSoundInfoUpdater() {
        infoUpdaterHandler.removeCallbacks(updateInfoRunnable);
    }

    private void updateSoundInfo() {
        apiService.getCurrentSoundInfo(currentChannel.getSongInfoUrl(), searchMediaUrl, new SimpleSubscriber<SongInfoDetails>() {
            @Override
            public void onNext(SongInfoDetails soundInfoResponse) {
                if (soundInfoResponse != null) {
                    StringBuilder songInfoString = new StringBuilder();
                    if (!TextUtils.isEmpty(soundInfoResponse.getArtistName())) {
                        songInfoString.append(soundInfoResponse.getArtistName());
                    }
                    if (!TextUtils.isEmpty(soundInfoResponse.getTrackName())) {
                        songInfoString.append(" - ");
                        songInfoString.append(soundInfoResponse.getTrackName());
                    }
                    homeFragmentView.updateSoundInfo(songInfoString.toString());
                    homeFragmentView.updateImage(imageLoader, soundInfoResponse.getArtworkUrl100());
                    if (homeFragmentView.getSupportMediaController() != null) {
                        Bundle bundle = new Bundle();
                        bundle.putString(BundleKeys.ARTIST_NAME, soundInfoResponse.getArtistName());
                        bundle.putString(BundleKeys.SONG_INFO_DETAILS, soundInfoResponse.getTrackName());
                        homeFragmentView.getSupportMediaController().getTransportControls().sendCustomAction(StreamService.CUSTOM_UPDATE_NOTIFICATION_DATA_ACTION, bundle);
                    }
                }
            }
        });
        apiService.getRecentlyList(currentChannel.getRecentlyInfoUrl(), new SimpleSubscriber<List<RecentlyItem>>() {
            @Override
            public void onNext(List<RecentlyItem> recentlyItems) {
                homeFragmentView.updateRecentlyList(recentlyItems);
            }
        });
    }

    private void updateCurrentStation(Channel channel, StreamData streamData) {
        currentChannel = channel;
        currentStreamData = streamData;
        startSoundInfoUpdater();
        homeFragmentView.invalidateOptionsMenu();
    }

    private void changeStream(){
        homeFragmentView.getSupportMediaController().getTransportControls().stop();
        homeFragmentView.updateActionButton(PlaybackStateCompat.STATE_STOPPED);
        Uri linkUri = Uri.parse(currentStreamData.getUrl());
        homeFragmentView.getSupportMediaController().getTransportControls().playFromUri(linkUri, null);
        Timber.d("Changing stream to: %s", linkUri.toString());
        homeFragmentView.getSupportMediaController().getTransportControls().play();
        homeFragmentView.updateActionButton(PlaybackStateCompat.STATE_PLAYING);
    }

    @Override
    public ItemSelectListener<Channel> getChannelItemListener() {
        return (view, dataItem) -> {
            updateCurrentStation(dataItem, dataItem.getStreamUrls().get(dataItem.getStreamUrls().size() > 1 ? 1 : 0));
            changeStream();
        };
    }

    @Override
    public void onClickActionButton() {
        if (homeFragmentView.getSupportMediaController().getPlaybackState() != null &&
                homeFragmentView.getSupportMediaController().getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING) {
            Timber.d("Changing state to pause");
            homeFragmentView.getSupportMediaController().getTransportControls().stop();
            homeFragmentView.updateActionButton(PlaybackStateCompat.STATE_STOPPED);
        } else {
            Uri linkUri = Uri.parse(currentStreamData.getUrl());
            homeFragmentView.getSupportMediaController().getTransportControls().playFromUri(linkUri, null);
            Timber.d("Changing state to play with resource: %s", linkUri.toString());
            homeFragmentView.getSupportMediaController().getTransportControls().play();
            homeFragmentView.updateActionButton(PlaybackStateCompat.STATE_PLAYING);
        }
    }

    @Override
    public boolean onBackPressed() {
        if (!homeFragmentView.closeDrawer()) {
            if (!onBackPressedFlag) {
                ToastUlils.showToast(R.string.press_again_exit);
                Observable.just(onBackPressedFlag = true)
                        .delay(DELAY_TO_EXIT, TimeUnit.MILLISECONDS)
                        .map(onBackFlag -> onBackPressedFlag = false)
                        .subscribe();
                Timber.d("Press back");
                return false;
            } else {
                Timber.d("Exit from App");
                homeFragmentView.getSupportMediaController().getTransportControls().sendCustomAction(StreamService.CUSTOM_EXIT_ACTION, null);
                homeFragmentView.finish();
            }

            // TODO press again to exit remove Notificateion stop service
        }
        return false;
    }

    @Override
    public void onDestroy() {
//        if (homeFragmentView.getSupportMediaController() != null && homeFragmentView.getSupportMediaController().getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING) {
//            homeFragmentView.getSupportMediaController().getTransportControls().pause();
//        }
        mediaBrowserCompat.disconnect();
        stopSoundInfoUpdater();
    }
}
