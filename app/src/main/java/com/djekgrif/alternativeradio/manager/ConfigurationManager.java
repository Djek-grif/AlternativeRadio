package com.djekgrif.alternativeradio.manager;

import com.djekgrif.alternativeradio.network.ApiService;
import com.djekgrif.alternativeradio.network.model.ConfigurationData;

import rx.functions.Action1;

/**
 * Created by djek-grif on 2/1/17.
 */

public class ConfigurationManager {

    private ApiService apiService;
    private ConfigurationData configurationData;

    public ConfigurationManager(ApiService apiService) {
        this.apiService = apiService;
    }

    public ConfigurationData getConfigurationData() {
        return configurationData;
    }

    public void updateConfigurationData(Action1<ConfigurationData> configurationListener) {
        if(configurationData != null){
            configurationListener.call(configurationData);
        }else{
            apiService.getConfigurationData(configuration -> {
                configurationData = configuration;
                if (configurationListener != null) {
                    configurationListener.call(configurationData);
                }
            });
        }
    }

}
