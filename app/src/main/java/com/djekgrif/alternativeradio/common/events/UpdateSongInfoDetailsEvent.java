package com.djekgrif.alternativeradio.common.events;

import com.djekgrif.alternativeradio.network.model.CurrentTrackInfo;

/**
 * Created by djek-grif on 4/21/17.
 */

public class UpdateSongInfoDetailsEvent {

    public CurrentTrackInfo currentTrackInfo;

    public UpdateSongInfoDetailsEvent(CurrentTrackInfo currentTrackInfo) {
        this.currentTrackInfo = currentTrackInfo;
    }
}
