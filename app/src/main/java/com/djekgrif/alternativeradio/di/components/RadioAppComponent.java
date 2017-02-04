package com.djekgrif.alternativeradio.di.components;

import com.djekgrif.alternativeradio.App;
import com.djekgrif.alternativeradio.di.modules.ApiModule;
import com.djekgrif.alternativeradio.di.modules.DataModule;
import com.djekgrif.alternativeradio.di.modules.NetworkModule;
import com.djekgrif.alternativeradio.di.modules.RadioAppModule;
import com.djekgrif.alternativeradio.manager.ConfigurationManager;
import com.djekgrif.alternativeradio.manager.ImageLoader;
import com.djekgrif.alternativeradio.network.ApiService;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by djek-grif on 5/25/16.
 */

@Singleton
@Component(
        modules = {
                RadioAppModule.class, ApiModule.class, NetworkModule.class, DataModule.class
        }
)
public interface RadioAppComponent {
    void inject(App app);
    ImageLoader imageLoader();
    ApiService apiService();
    ConfigurationManager configurationManager();
}
