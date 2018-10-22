package com.djekgrif.alternativeradio.common.events;

import com.djekgrif.alternativeradio.network.model.CurrentTrackInfo;
import com.djekgrif.alternativeradio.network.model.SearchInfoDetails;
import com.djekgrif.alternativeradio.network.model.SongTextItem;

/**
 * Created by djek-grif on 7/9/17.
 */

public class SongTextItemEvent {

    private SongTextItem songTextItem;
    private CurrentTrackInfo currentTrackInfo;

    public SongTextItemEvent(SongTextItem songTextItem, CurrentTrackInfo currentTrackInfo) {
        this.songTextItem = songTextItem;
        this.currentTrackInfo = currentTrackInfo;
    }

    public SongTextItem getSongTextItem() {
        return songTextItem;
    }

    public CurrentTrackInfo getCurrentTrackInfo() {
        return currentTrackInfo;
    }
}
