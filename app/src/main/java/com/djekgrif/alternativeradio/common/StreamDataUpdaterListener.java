package com.djekgrif.alternativeradio.common;

import com.djekgrif.alternativeradio.network.model.ConfigurationData;
import com.djekgrif.alternativeradio.network.model.SongInfoDetails;
import com.djekgrif.alternativeradio.ui.model.HomeListItem;

import java.util.List;

/**
 * Created by djek-grif on 2/19/17.
 */

public interface StreamDataUpdaterListener {

    void updateConfiguration(ConfigurationData configurationData);
    void updateData(SongInfoDetails songInfoDetails);
    void updateRecentlyList(List<HomeListItem> recentlyItems);
}
