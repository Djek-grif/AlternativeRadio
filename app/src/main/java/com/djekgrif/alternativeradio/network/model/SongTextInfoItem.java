package com.djekgrif.alternativeradio.network.model;

/**
 * Created by djek-grif on 7/9/17.
 */

public class SongTextInfoItem {
    private String title;
    private String link;
    private String shortText;

    public SongTextInfoItem(String title, String link, String shortText) {
        this.title = title;
        this.link = link;
        this.shortText = shortText;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getShortText() {
        return shortText;
    }
}
