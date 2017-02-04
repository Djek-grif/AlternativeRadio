package com.djekgrif.alternativeradio.ui.adapters;

import android.view.View;

/**
 * Created by djek-grif on 1/8/17.
 */

public interface ItemSelectListener<T> {
    void onItemClick(View view, T dataItem);
}
