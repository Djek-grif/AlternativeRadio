package com.djekgrif.alternativeradio.manager;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.djekgrif.alternativeradio.ui.model.LastAppState;
import com.google.gson.Gson;

/**
 * Created by djek-grif on 2/19/17.
 */

public class PreferencesManager implements Preferences{

    private static final String PREFERENCES_NAME = "radio_preferences";
    private static final String KEY_LAST_APP_STATE = "key_last_app_state";

    private SharedPreferences preferences;
    private Gson gson;

    public PreferencesManager(Application application, Gson gson) {
        this.preferences = application.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        this.gson = gson;
    }

    public void saveLastAppState(LastAppState lastAppState){
        preferences.edit().putString(KEY_LAST_APP_STATE, gson.toJson(lastAppState)).apply();
    }

    public LastAppState getLastAppState(){
        String updateResultString = preferences.getString(KEY_LAST_APP_STATE, null);
        return updateResultString != null ? gson.fromJson(updateResultString, LastAppState.class) : null;
    }
}
