package com.djekgrif.alternativeradio.common;

import com.djekgrif.alternativeradio.network.model.ConfigurationData;
import com.djekgrif.alternativeradio.network.model.RecentlyItem;
import com.djekgrif.alternativeradio.network.model.SongInfoDetails;

import java.util.List;

/**
 * Created by djek-grif on 2/19/17.
 */

public interface StreamDataUpdaterListener {

    void updateConfiguration(ConfigurationData configurationData);
    void updateData(SongInfoDetails songInfoDetails);
    void updateRecentlyList(List<RecentlyItem> recentlyItems);
}
