package com.djekgrif.alternativeradio.network.model;

import java.util.List;

/**
 * Created by djek-grif on 1/31/17.
 */

public class ConfigurationData {

    private List<StationData> stations;
    private String searchMediaUrl;

    public void setStations(List<StationData> stations) {
        this.stations = stations;
    }

    public List<StationData> getStations() {
        return stations;
    }

    public String getSearchMediaUrl() {
        return searchMediaUrl;
    }

    public void setSearchMediaUrl(String searchMediaUrl) {
        this.searchMediaUrl = searchMediaUrl;
    }
}
