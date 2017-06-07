package com.djekgrif.alternativeradio.common.events;

import com.djekgrif.alternativeradio.network.model.Channel;

/**
 * Created by djek-grif on 5/26/17.
 */

public class StreamChangedEvent {

    private Channel channel;

    public StreamChangedEvent(Channel channel) {
        this.channel = channel;
    }

    public Channel getChannel() {
        return channel;
    }
}
