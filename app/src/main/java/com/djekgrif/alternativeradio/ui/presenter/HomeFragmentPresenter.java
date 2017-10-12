package com.djekgrif.alternativeradio.ui.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;

import com.djekgrif.alternativeradio.network.model.Channel;
import com.djekgrif.alternativeradio.ui.adapters.ItemSelectListener;

/**
 * Created by djek-grif on 5/27/16.
 */
public interface HomeFragmentPresenter extends BasePresenter{
    void onActivityCreated(@Nullable Bundle savedInstanceState);
    void onClickActionButton();
    void onClickShare(String songInfo);
    void onClickTextButton();
    boolean onBackPressed();
    void onDestroy();
    boolean onOptionsItemSelected(MenuItem menuItem);
    void onPrepareOptionsMenu(Menu menu);
    ItemSelectListener<Channel> getChannelItemListener();
}
