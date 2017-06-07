package com.djekgrif.alternativeradio.common.events;

import com.djekgrif.alternativeradio.network.model.SongInfoDetails;

/**
 * Created by djek-grif on 4/21/17.
 */

public class UpdateSongInfoDetailsEvent {

    public UpdateSongInfoDetailsEvent(SongInfoDetails songInfoDetails) {
        this.songInfoDetails = songInfoDetails;
    }

    public SongInfoDetails songInfoDetails;
}
