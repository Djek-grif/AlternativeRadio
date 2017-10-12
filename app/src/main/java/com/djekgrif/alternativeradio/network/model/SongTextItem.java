package com.djekgrif.alternativeradio.network.model;

import com.djekgrif.alternativeradio.ui.model.HomeListItem;

/**
 * Created by djek-grif on 7/9/17.
 */

public class SongTextItem implements HomeListItem{
    private String title;
    private String text;

    public SongTextItem(String title, String text) {
        this.title = title;
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    @Override
    public int getType() {
        return TEXT_ITEM;
    }
}
