package com.djekgrif.alternativeradio.ui.activity;

import android.os.Bundle;

import com.djekgrif.alternativeradio.R;
import com.djekgrif.alternativeradio.ui.fragment.BaseFragment;

/**
 * Created by djek-grif on 1/7/17.
 */

public abstract class BaseSingleFragmentActivity <T extends BaseFragment> extends BaseActivity {

    public static final String FRAGMENT_TAG = "_single_fragment_tag";

    private BaseFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContainerViewResId());

        fragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(getFragmentTag());
        if(fragment == null){
            fragment = onCreateFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment, getFragmentTag())
                    .commit();
        }
    }

    protected String getFragmentTag(){
        return getClass().getSimpleName() + FRAGMENT_TAG;
    }

    @Override
    public void onBackPressed() {
        if(fragment.onBackPressed()) {
            super.onBackPressed();
        }
    }

    protected int getContainerViewResId(){
        return R.layout.activity_single_fragment;
    }

    protected abstract T onCreateFragment();
}
