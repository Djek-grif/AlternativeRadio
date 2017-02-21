package com.djekgrif.alternativeradio.manager;

import com.djekgrif.alternativeradio.ui.model.LastAppState;

/**
 * Created by djek-grif on 2/19/17.
 */

public interface Preferences {

    LastAppState getLastAppState();
    void saveLastAppState(LastAppState lastAppState);
}
