package com.djekgrif.alternativeradio;

import android.app.Application;

import com.djekgrif.alternativeradio.di.components.DaggerRadioAppComponent;
import com.djekgrif.alternativeradio.di.components.RadioAppComponent;

import net.danlew.android.joda.JodaTimeAndroid;

import timber.log.Timber;

/**
 * Created by djek-grif on 5/25/16.
 */
public class App extends Application {

    private RadioAppComponent appComponent;

    private static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        buildGraphAndInject();
        JodaTimeAndroid.init(this);
        if(BuildConfig.IS_INTERNAL_BUILD){
            Timber.plant(new Timber.DebugTree());
        }
    }

    public static App getInstance() {
        return instance;
    }

    public RadioAppComponent getAppComponent() {
        return appComponent;
    }

    private void buildGraphAndInject() {
        appComponent = DaggerRadioAppComponent.builder().build();
        appComponent.inject(this);
    }

}
