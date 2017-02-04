package com.djekgrif.alternativeradio.di.modules;

import android.app.Application;

import com.djekgrif.alternativeradio.App;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by djek-grif on 5/25/16.
 */
@Module
public class RadioAppModule {

    private App app;

    public RadioAppModule(App app) {
        this.app = app;
    }

    @Provides
    @Singleton
    public Application provideApplication() {
        return app;
    }
}
