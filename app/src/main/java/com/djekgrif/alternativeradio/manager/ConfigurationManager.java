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
    private Action1<ConfigurationData> configurationListener;

    public ConfigurationManager(ApiService apiService) {
        this.apiService = apiService;
        initConfigurationData();
    }

    private void initConfigurationData() {
        apiService.getConfigurationData(configuration -> {
            configurationData = configuration;
            if (configurationListener != null) {
                configurationListener.call(configurationData);
            }
        });
    }

    public void getConfigurationData(Action1<ConfigurationData> configurationListener) {
        if(configurationData != null){
            configurationListener.call(configurationData);
        }else{
            this.configurationListener = configurationListener;
        }
    }

}
