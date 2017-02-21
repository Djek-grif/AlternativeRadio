package com.djekgrif.alternativeradio.di.modules;

import android.app.Application;

import com.djekgrif.alternativeradio.manager.ConfigurationManager;
import com.djekgrif.alternativeradio.manager.ImageLoader;
import com.djekgrif.alternativeradio.manager.PicassoImageLoader;
import com.djekgrif.alternativeradio.manager.Preferences;
import com.djekgrif.alternativeradio.manager.PreferencesManager;
import com.djekgrif.alternativeradio.network.ApiService;
import com.djekgrif.alternativeradio.network.adapters.DateTimeTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.joda.time.DateTime;

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

    @Provides
    @Singleton
    Gson provideGson() {
        return new GsonBuilder()
                .registerTypeAdapter(DateTime.class, new DateTimeTypeAdapter())
                .create();
    }

    @Provides
    @Singleton
    public Preferences providePreferencesManager(Application application, Gson gson){
        return new PreferencesManager(application, gson);
    }
}
