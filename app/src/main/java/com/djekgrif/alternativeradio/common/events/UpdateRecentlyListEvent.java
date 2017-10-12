package com.djekgrif.alternativeradio.common.events;

import com.djekgrif.alternativeradio.ui.model.HomeListItem;

import java.util.List;

/**
 * Created by djek-grif on 4/21/17.
 */

public class UpdateRecentlyListEvent {

    public List<HomeListItem> recentlyItemList;

    public UpdateRecentlyListEvent(List<HomeListItem> recentlyItemList) {
        this.recentlyItemList = recentlyItemList;
    }
}
