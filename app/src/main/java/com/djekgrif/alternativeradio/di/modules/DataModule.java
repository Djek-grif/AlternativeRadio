package com.djekgrif.alternativeradio.di.modules;

import com.djekgrif.alternativeradio.manager.ConfigurationManager;
import com.djekgrif.alternativeradio.manager.ImageLoader;
import com.djekgrif.alternativeradio.manager.PicassoImageLoader;
import com.djekgrif.alternativeradio.network.ApiService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by djek-grif on 10/19/16.
 */

@Module(includes = RadioAppModule.class)
public class DataModule {

    @Provides
    @Singleton
    public ImageLoader provideImageLoader(){
        return new PicassoImageLoader();
    }

    @Provides
    @Singleton
    public ConfigurationManager provideConfigurationManager(ApiService apiService){
        return new ConfigurationManager(apiService);
    }
}
