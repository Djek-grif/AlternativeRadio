package com.djekgrif.alternativeradio.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.djekgrif.alternativeradio.App;
import com.djekgrif.alternativeradio.ui.activity.CopyToClipboardActivity;

/**
 * Created by djek-grif on 11/15/16.
 */

public class DeviceUtils {

    public static boolean isLollipopOrHigher(){
        return android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static boolean isConnection() {
        ConnectivityManager cm = (ConnectivityManager) App.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static void openShare(String text){
        try {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, text);
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, text);

            Intent clipboardIntent = new Intent(App.getInstance(), CopyToClipboardActivity.class);
            clipboardIntent.setData(Uri.parse(text));

            Intent chooserIntent = Intent.createChooser(sharingIntent, "Share with");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] { clipboardIntent });

            chooserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            App.getInstance().startActivity(chooserIntent);
        } catch (ActivityNotFoundException e) {
            Log.e("DeviceUtils", "Error open app for share link", e);
        }
    }
}
