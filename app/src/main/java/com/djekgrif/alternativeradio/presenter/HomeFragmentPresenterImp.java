package com.djekgrif.alternativeradio.presenter;

import android.content.ComponentName;
import android.os.Bundle;
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
import com.djekgrif.alternativeradio.common.events.ClickActionButtonEvent;
import com.djekgrif.alternativeradio.common.events.StartPlayEvent;
import com.djekgrif.alternativeradio.common.events.StopPlayEvent;
import com.djekgrif.alternativeradio.common.events.StreamChangedEvent;
import com.djekgrif.alternativeradio.common.events.UpdateConfigurationDataEvent;
import com.djekgrif.alternativeradio.common.events.UpdateRecentlyListEvent;
import com.djekgrif.alternativeradio.common.events.UpdateSongInfoDetailsEvent;
import com.djekgrif.alternativeradio.common.events.UpdateStationsStateEvent;
import com.djekgrif.alternativeradio.manager.ImageLoader;
import com.djekgrif.alternativeradio.network.model.Channel;
import com.djekgrif.alternativeradio.network.model.SongInfoDetails;
import com.djekgrif.alternativeradio.ui.adapters.ItemSelectListener;
import com.djekgrif.alternativeradio.ui.utils.ToastUlils;
import com.djekgrif.alternativeradio.view.HomeFragmentView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

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

    public HomeFragmentPresenterImp(HomeFragmentView homeFragmentView, ImageLoader imageLoader) {
        this.homeFragmentView = homeFragmentView;
        this.imageLoader = imageLoader;
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
                        EventBus.getDefault().post(new UpdateConfigurationDataEvent());
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
                            Timber.e(e, "Error create MediaControllerCompat");
                        }
                    }
                }, homeFragmentView.getIntent().getExtras());

    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        this.mediaBrowserCompat.connect();
    }

    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
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
            EventBus.getDefault().post(new StreamChangedEvent(channel));
            homeFragmentView.invalidateOptionsMenu();
            homeFragmentView.closeDrawer();
        };
    }

    @Override
    public void onClickActionButton() {
        EventBus.getDefault().post(new ClickActionButtonEvent());
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

    @Subscribe
    public void onUpdateSongInfoDetailsEvent(UpdateSongInfoDetailsEvent updateSongInfoDetails){
        SongInfoDetails songInfoDetails = updateSongInfoDetails.songInfoDetails;
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
    }

    @Subscribe
    public void onUpdateStationsState(UpdateStationsStateEvent updateStationsState){
        homeFragmentView.updateStationList(updateStationsState.stationDataList);
        homeFragmentView.setUpUI();
    }

    @Subscribe
    public void onUpdateRecentlyList(UpdateRecentlyListEvent updateRecentlyList){
        homeFragmentView.updateRecentlyList(updateRecentlyList.recentlyItemList);
    }

    @Subscribe
    public void onStartPlay(StartPlayEvent startPlayEvent){
        homeFragmentView.updateActionButton(PlaybackStateCompat.STATE_PLAYING);
    }

    @Subscribe
    public void onStopPlay(StopPlayEvent stopPlayEvent){
        homeFragmentView.updateActionButton(PlaybackStateCompat.STATE_STOPPED);
    }
}
