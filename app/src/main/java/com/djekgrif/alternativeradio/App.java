package com.djekgrif.alternativeradio;

import android.app.Application;

import com.djekgrif.alternativeradio.common.Logger;
import com.djekgrif.alternativeradio.di.components.DaggerRadioAppComponent;
import com.djekgrif.alternativeradio.di.components.RadioAppComponent;
import com.djekgrif.alternativeradio.di.modules.RadioAppModule;
import com.google.firebase.FirebaseApp;

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
        if(BuildConfig.DEBUG){
            Logger.setCurrentLevel(Logger.Level.DETAILS);
        }
    }

    public static App getInstance() {
        return instance;
    }

    public RadioAppComponent getAppComponent() {
        return appComponent;
    }

    private void buildGraphAndInject() {
        appComponent = DaggerRadioAppComponent
                .builder()
                .radioAppModule(new RadioAppModule(this))
                .build();
        appComponent.inject(this);
    }

}
