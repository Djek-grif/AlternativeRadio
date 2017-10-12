package com.djekgrif.alternativeradio.common.events;

import com.djekgrif.alternativeradio.network.model.SongInfoDetails;
import com.djekgrif.alternativeradio.network.model.SongTextItem;

/**
 * Created by djek-grif on 7/9/17.
 */

public class SongTextItemEvent {

    private SongTextItem songTextItem;
    private SongInfoDetails songInfoDetails;

    public SongTextItemEvent(SongTextItem songTextItem, SongInfoDetails songInfoDetails) {
        this.songTextItem = songTextItem;
        this.songInfoDetails = songInfoDetails;
    }

    public SongTextItem getSongTextItem() {
        return songTextItem;
    }

    public SongInfoDetails getSongInfoDetails() {
        return songInfoDetails;
    }
}
