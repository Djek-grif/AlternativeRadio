package com.djekgrif.alternativeradio.view;

import android.content.Context;
import android.content.Intent;
import android.support.v4.media.session.MediaControllerCompat;

import com.djekgrif.alternativeradio.manager.ImageLoader;
import com.djekgrif.alternativeradio.network.model.SongTextItem;
import com.djekgrif.alternativeradio.network.model.StationData;
import com.djekgrif.alternativeradio.ui.model.HomeListItem;

import java.util.List;

/**
 * Created by djek-grif on 5/25/16.
 */
public interface HomeFragmentView {
    void setUpUI();
    boolean closeDrawer();
    void updateTitle(String title);
    void updateSoundInfo(String string);
    void updateRecentlyList(List<HomeListItem> recentlyItemList);
    void updateImage(ImageLoader imageLoader, String imageUrl);
    void updateActionButton(int state);
    Context getContext();
    Intent getIntent();
    void finish();
    void setSupportMediaController(MediaControllerCompat mediaController);
    MediaControllerCompat getSupportMediaController();
    void invalidateOptionsMenu();
    void updateStationList(List<StationData> stationDataList);
    void openSongText(SongTextItem songTextItem);
    void hideSongText();
    void failedSongText();
    boolean isSongTextOpen();
    void hideTextButtonProgress();
    void showTextButtonProgress();
}
