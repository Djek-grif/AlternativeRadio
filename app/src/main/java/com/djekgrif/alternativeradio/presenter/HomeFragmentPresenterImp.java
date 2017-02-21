package com.djekgrif.alternativeradio.presenter;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.djekgrif.alternativeradio.R;
import com.djekgrif.alternativeradio.common.StreamService;
import com.djekgrif.alternativeradio.manager.ImageLoader;
import com.djekgrif.alternativeradio.network.model.Channel;
import com.djekgrif.alternativeradio.network.model.SongInfoDetails;
import com.djekgrif.alternativeradio.ui.adapters.ItemSelectListener;
import com.djekgrif.alternativeradio.ui.utils.BundleKeys;
import com.djekgrif.alternativeradio.ui.utils.ToastUlils;
import com.djekgrif.alternativeradio.utils.IntentUtils;
import com.djekgrif.alternativeradio.view.HomeFragmentView;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import timber.log.Timber;

/**
 * Created by djek-grif on 5/27/16.
 */
public class HomeFragmentPresenterImp implements HomeFragmentPresenter {

    private static final int DELAY_TO_EXIT = 1000 * 3;

    private MediaBrowserCompat mediaBrowserCompat;
    private MediaControllerCompat mediaControllerCompat;
    private ImageLoader imageLoader;
    private boolean onBackPressedFlag;
    private HomeFragmentView homeFragmentView;

    private BroadcastReceiver actionsBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                switch (intent.getAction()) {
                    case IntentUtils.UPDATE_SONG_INFO_DETAILS:
                        SongInfoDetails songInfoDetails = intent.getExtras().getParcelable(BundleKeys.SONG_INFO_DETAILS);
                        StringBuilder songInfoString = new StringBuilder();
                        if (!TextUtils.isEmpty(songInfoDetails.getArtistName())) {
                            songInfoString.append(songInfoDetails.getArtistName());
                        }
                        if (!TextUtils.isEmpty(songInfoDetails.getTrackName())) {
                            songInfoString.append(" - ");
                            songInfoString.append(songInfoDetails.getTrackName());
                        }
                        homeFragmentView.updateSoundInfo(songInfoString.toString());
                        homeFragmentView.updateImage(imageLoader, songInfoDetails.getArtworkUrl100());
                        break;
                    case IntentUtils.UPDATE_STATIONS_STATE:
                        homeFragmentView.updateStationList(intent.getParcelableArrayListExtra(BundleKeys.STATION_LIST));
                        homeFragmentView.setUpUI();
                        break;
                    case IntentUtils.UPDATE_RECENTLY_LIST:
                        homeFragmentView.updateRecentlyList(intent.getParcelableArrayListExtra(BundleKeys.RECENTLY_LIST));
                        break;
                    case IntentUtils.START_PLAY:
                        homeFragmentView.updateActionButton(PlaybackStateCompat.STATE_PLAYING);
                        break;
                    case IntentUtils.STOP_PLAY:
                        homeFragmentView.updateActionButton(PlaybackStateCompat.STATE_STOPPED);
                        break;
                }

            }
        }
    };

    public HomeFragmentPresenterImp(HomeFragmentView homeFragmentView, ImageLoader imageLoader) {
        this.homeFragmentView = homeFragmentView;
        this.imageLoader = imageLoader;
//        this.configurationManager = configurationManager;
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
//        configurationManager.updateConfigurationData(configurationData -> {
//            Channel channel = configurationData.getStations().get(0).getChannels().get(0);
//            StreamData streamData = channel.getStreamUrls().get(channel.getStreamUrls().size() > 1 ? 1 : 0);
//            updateCurrentChannel(channel, streamData);
//            searchMediaUrl = configurationData.getSearchMediaUrl();
//            homeFragmentView.setUpUI();
//        });
    }

    @Override
    public void onPause() {
    }

    @Override
    public void onResume() {
    }

    @Override
    public void onStart() {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(homeFragmentView.getContext());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(IntentUtils.START_PLAY);
        intentFilter.addAction(IntentUtils.STOP_PLAY);
        intentFilter.addAction(IntentUtils.UPDATE_SONG_INFO_DETAILS);
        intentFilter.addAction(IntentUtils.UPDATE_PROGRESS_STATE);
        intentFilter.addAction(IntentUtils.UPDATE_STATIONS_STATE);
        intentFilter.addAction(IntentUtils.UPDATE_RECENTLY_LIST);
        localBroadcastManager.registerReceiver(actionsBroadcastReceiver, intentFilter);
    }

    @Override
    public void onStop() {
        LocalBroadcastManager.getInstance(homeFragmentView.getContext()).unregisterReceiver(actionsBroadcastReceiver);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
//        changeStream();
        return true;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
//        if (currentChannel != null) {
//            for (int i = 0; i < currentChannel.getStreamUrls().size(); i++) {
//                int key = currentChannel.getStreamUrls().get(i).getQuality();
//                menu.add(0, i, Menu.NONE, String.format(homeFragmentView.getContext().getString(R.string.kbs), String.valueOf(key)));
//            }
//        }
    }


    @Override
    public ItemSelectListener<Channel> getChannelItemListener() {
        return (view, channel) -> {
            Bundle bundle = new Bundle();
            bundle.putParcelable(BundleKeys.CHANNEL, channel);
            homeFragmentView.getSupportMediaController().getTransportControls().sendCustomAction(StreamService.CUSTOM_STREAM_CHANGED, bundle);
            homeFragmentView.invalidateOptionsMenu();
        };
    }

    @Override
    public void onClickActionButton() {
        homeFragmentView.getSupportMediaController().getTransportControls().sendCustomAction(StreamService.CUSTOM_CHANGE_STREAM_STATE, null);
//        if (homeFragmentView.getSupportMediaController().getPlaybackState() != null &&
//                homeFragmentView.getSupportMediaController().getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING) {
//            Timber.d("Changing state to pause");
//            homeFragmentView.getSupportMediaController().getTransportControls().stop();
//            homeFragmentView.updateActionButton(PlaybackStateCompat.STATE_STOPPED);
//        } else {
//            Uri linkUri = Uri.parse(currentStreamData.getUrl());
//            homeFragmentView.getSupportMediaController().getTransportControls().playFromUri(linkUri, null);
//            Timber.d("Changing state to play with resource: %s", linkUri.toString());
//            homeFragmentView.getSupportMediaController().getTransportControls().play();
//            homeFragmentView.updateActionButton(PlaybackStateCompat.STATE_PLAYING);
//        }
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
    }
}
