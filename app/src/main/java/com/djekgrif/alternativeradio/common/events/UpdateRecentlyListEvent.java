package com.djekgrif.alternativeradio.common.events;

import com.djekgrif.alternativeradio.network.model.RecentlyItem;

import java.util.List;

/**
 * Created by djek-grif on 4/21/17.
 */

public class UpdateRecentlyListEvent {

    public UpdateRecentlyListEvent(List<RecentlyItem> recentlyItemList) {
        this.recentlyItemList = recentlyItemList;
    }

    public List<RecentlyItem> recentlyItemList;
}
