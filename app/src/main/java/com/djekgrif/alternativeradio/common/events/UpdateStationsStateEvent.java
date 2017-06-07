package com.djekgrif.alternativeradio.common.events;

import com.djekgrif.alternativeradio.network.model.StationData;

import java.util.List;

/**
 * Created by djek-grif on 4/21/17.
 */

public class UpdateStationsStateEvent {

    public UpdateStationsStateEvent(List<StationData> stationDataList) {
        this.stationDataList = stationDataList;
    }

    public List<StationData> stationDataList;
}
