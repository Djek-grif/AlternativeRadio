package com.djekgrif.alternativeradio.ui.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import com.djekgrif.alternativeradio.ui.utils.ToastUlils;

/**
 * Created by djek-grif on 7/3/17.
 */

public class CopyToClipboardActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri uri = getIntent().getData();
        if (uri != null) {
            copyTextToClipboard(uri.toString());
            ToastUlils.showToast("Copied to clipboard");
        }
        finish();
    }

    private void copyTextToClipboard(String url) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("TEXT", url);
        clipboard.setPrimaryClip(clip);
    }
}
