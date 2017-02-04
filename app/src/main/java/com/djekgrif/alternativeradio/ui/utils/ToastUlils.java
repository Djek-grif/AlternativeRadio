package com.djekgrif.alternativeradio.ui.utils;

import android.widget.Toast;

import com.djekgrif.alternativeradio.App;

/**
 * Created by djek-grif on 1/7/17.
 */

public class ToastUlils {

    public static void showToast(String message){
        Toast.makeText(App.getInstance(), message, Toast.LENGTH_LONG).show();
    }

    public static void showToast(int stringResource){
        Toast.makeText(App.getInstance(), stringResource, Toast.LENGTH_LONG).show();
    }
}
