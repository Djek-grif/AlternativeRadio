package com.djekgrif.alternativeradio.ui.presenter;

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
import com.djekgrif.alternativeradio.common.Logger;
import com.djekgrif.alternativeradio.common.StreamService;
import com.djekgrif.alternativeradio.common.events.ClickActionButtonEvent;
import com.djekgrif.alternativeradio.common.events.SongTextItemEvent;
import com.djekgrif.alternativeradio.common.events.StartPlayEvent;
import com.djekgrif.alternativeradio.common.events.StopPlayEvent;
import com.djekgrif.alternativeradio.common.events.StreamChangedEvent;
import com.djekgrif.alternativeradio.common.events.UpdateConfigurationDataEvent;
import com.djekgrif.alternativeradio.common.events.UpdateRecentlyListEvent;
import com.djekgrif.alternativeradio.common.events.UpdateSongInfoDetailsEvent;
import com.djekgrif.alternativeradio.common.events.UpdateStationsStateEvent;
import com.djekgrif.alternativeradio.manager.DialogManager;
import com.djekgrif.alternativeradio.manager.ImageLoader;
import com.djekgrif.alternativeradio.manager.SongTextHelper;
import com.djekgrif.alternativeradio.network.model.Channel;
import com.djekgrif.alternativeradio.network.model.CurrentTrackInfo;
import com.djekgrif.alternativeradio.network.model.SongTextItem;
import com.djekgrif.alternativeradio.network.model.StationData;
import com.djekgrif.alternativeradio.ui.adapters.ItemSelectListener;
import com.djekgrif.alternativeradio.ui.utils.ToastUlils;
import com.djekgrif.alternativeradio.utils.DeviceUtils;
import com.djekgrif.alternativeradio.utils.ListUtils;
import com.djekgrif.alternativeradio.view.HomeFragmentView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.concurrent.TimeUnit;

import rx.Observable;

/**
 * Created by djek-grif on 5/27/16.
 */
public class HomeFragmentPresenterImp implements HomeFragmentPresenter {

    private static final int DELAY_TO_EXIT = 1000 * 3;

    private MediaBrowserCompat mediaBrowserCompat;
    private MediaControllerCompat mediaControllerCompat;
    private ImageLoader imageLoader;
    private boolean onBackPressedFlag;
    protected HomeFragmentView homeFragmentView;

    private CurrentTrackInfo currentTrackInfo;
    private SongTextItem currentSongTextItem;

    public HomeFragmentPresenterImp(HomeFragmentView homeFragmentView, ImageLoader imageLoader) {
        this.homeFragmentView = homeFragmentView;
        this.imageLoader = imageLoader;
        this.mediaBrowserCompat = new MediaBrowserCompat(homeFragmentView.getContext(), new ComponentName(homeFragmentView.getContext(), StreamService.class),
                new MediaBrowserCompat.ConnectionCallback() {

                    @Override
                    public void onConnectionFailed() {
                        super.onConnectionFailed();
                        Logger.e("Error of connection mediaBrowserCompat", Logger.PLAYER);
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
                                        Logger.d("Update action button to state:" + state.getState(), Logger.PLAYER);
                                        homeFragmentView.updateActionButton(state.getState());
                                    }
                                }
                            });
                            homeFragmentView.setSupportMediaController(mediaControllerCompat);
                            homeFragmentView.updateActionButton(mediaControllerCompat.getPlaybackState() != null &&
                                    mediaControllerCompat.getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING
                                    ? PlaybackStateCompat.STATE_PLAYING : PlaybackStateCompat.STATE_STOPPED);
                        } catch (RemoteException e) {
                            Logger.e(e, "Error create MediaControllerCompat", Logger.PLAYER);
                        }

                        loadConfiguration();
                    }
                }, homeFragmentView.getIntent().getExtras());

    }

    private void loadConfiguration(){
        if(DeviceUtils.isConnection()){
            EventBus.getDefault().post(new UpdateConfigurationDataEvent());
        } else {
            DialogManager.showDisconnectDialog(homeFragmentView.getContext(), this::loadConfiguration);
        }
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
            homeFragmentView.updateTitle(channel.getName());
            homeFragmentView.hideSongText();
            currentSongTextItem = null;
        };
    }

    @Override
    public void onClickActionButton() {
        EventBus.getDefault().post(new ClickActionButtonEvent());
    }

    @Override
    public void onClickTextButton() {
        if(!homeFragmentView.isSongTextOpen()) {
            if(currentTrackInfo != null) {
                if (currentSongTextItem == null
                        || currentSongTextItem.getTitle() == null
                        || !currentSongTextItem.getTitle().equalsIgnoreCase(currentTrackInfo.getTrackName())) {
                    SongTextHelper.searchTextOfSong(currentTrackInfo);
                    homeFragmentView.showTextButtonProgress();
                } else {
                    homeFragmentView.openSongText(currentSongTextItem);
                }
            }
        }else{
            homeFragmentView.hideSongText();
        }
    }

    @Override
    public void onClickShare(String songInfo) {
        if(!TextUtils.isEmpty(songInfo)) {
            DeviceUtils.openShare(songInfo);
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
                Logger.d("Press back", Logger.LIFECYCLE);
                return false;
            } else {
                Logger.d("Exit from App", Logger.LIFECYCLE);
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
    public void onUpdateSongInfoDetailsEvent(UpdateSongInfoDetailsEvent updateSongInfoDetails) {
        currentTrackInfo = updateSongInfoDetails.currentTrackInfo;
        StringBuilder songInfoString = new StringBuilder();
        if (!TextUtils.isEmpty(currentTrackInfo.getArtistName())) {
            songInfoString.append(currentTrackInfo.getArtistName());
        }
        if (!TextUtils.isEmpty(currentTrackInfo.getTrackName())) {
            songInfoString.append(" - ");
            songInfoString.append(currentTrackInfo.getTrackName());
        }
        homeFragmentView.updateSoundInfo(songInfoString.toString());
        homeFragmentView.updateImage(imageLoader, currentTrackInfo.getImageBig());
    }

    @Subscribe
    public void onUpdateStationsState(UpdateStationsStateEvent updateStationsState) {
        homeFragmentView.updateStationList(ListUtils.filter(updateStationsState.stationDataList, StationData::isPublic));
        homeFragmentView.setUpUI();
    }

    @Subscribe
    public void onUpdateSongTextItem(SongTextItemEvent itemEvent){
        currentSongTextItem = itemEvent.getSongTextItem();
        if(currentSongTextItem != null) {
            homeFragmentView.openSongText(currentSongTextItem);
        } else {
            homeFragmentView.failedSongText();
        }
        homeFragmentView.hideTextButtonProgress();
    }

    @Subscribe
    public void onUpdateRecentlyList(UpdateRecentlyListEvent updateRecentlyList) {
        homeFragmentView.updateRecentlyList(updateRecentlyList.recentlyItemList);
    }

    @Subscribe
    public void onStartPlay(StartPlayEvent startPlayEvent) {
        homeFragmentView.updateActionButton(PlaybackStateCompat.STATE_PLAYING);
    }

    @Subscribe
    public void onStopPlay(StopPlayEvent stopPlayEvent) {
        homeFragmentView.updateActionButton(PlaybackStateCompat.STATE_STOPPED);
    }
}
