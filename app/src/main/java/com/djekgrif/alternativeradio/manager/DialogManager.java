package com.djekgrif.alternativeradio.manager;

import android.content.Context;
import android.support.v7.app.AlertDialog;

import com.djekgrif.alternativeradio.R;

import rx.functions.Action0;

/**
 * Created by djek-grif on 9/16/17.
 */

public class DialogManager {

    public static void showDisconnectDialog(Context context, Action0 retryAction) {
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(R.string.connection_problem)
                .setMessage(R.string.no_connection)
                .setCancelable(false)
                .setPositiveButton(R.string.retry, (dialog1, which) -> retryAction.call())
                .create();
        dialog.show();
    }
}
