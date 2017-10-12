package com.djekgrif.alternativeradio.ui.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;

import com.djekgrif.alternativeradio.R;
import com.djekgrif.alternativeradio.ui.fragment.BaseFragment;
import com.djekgrif.alternativeradio.ui.utils.BundleKeys;

/**
 * Created by djek-grif on 7/17/16.
 */
public abstract class BaseMultiFragmentActivity extends BaseActivity {

    protected String currentFragmentTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_multi);

        restoreInstanceState(savedInstanceState);

        BaseFragment fragment = (BaseFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            fragment = getBaseFragment(currentFragmentTag);
            replaceFragment(fragment, true);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(BundleKeys.CURRENT_FRAGMENT_TAG, currentFragmentTag);
    }

    protected void restoreInstanceState(Bundle savedInstanceState){
        if (savedInstanceState != null
                && savedInstanceState.containsKey(BundleKeys.CURRENT_FRAGMENT_TAG)
                && TextUtils.isEmpty(currentFragmentTag)){
            currentFragmentTag = savedInstanceState.getString(BundleKeys.CURRENT_FRAGMENT_TAG);
        }
    }

    protected void replaceFragment(BaseFragment fragment, boolean isAddToBackStack) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        currentFragmentTag = fragment.getClass().getSimpleName();
        fragmentTransaction.replace(R.id.fragment_container, fragment, currentFragmentTag);
        if (isAddToBackStack) {
            fragmentTransaction.addToBackStack(currentFragmentTag);
        }
        fragmentTransaction.commit();
    }

    protected void removeCurrentFragment() {
        android.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Fragment currentFragment = getFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment != null) {
            currentFragmentTag = currentFragment.getClass().getSimpleName();
            transaction.remove(currentFragment);
        }
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        BaseFragment fragment = (BaseFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment != null && !fragment.onBackPressed()) {
            return;
        }
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            if(getFragmentManager().getBackStackEntryCount() == 1){
                super.onBackPressed();
            }else {
                getFragmentManager().popBackStack();
                removeCurrentFragment();
            }
        } else {
            super.onBackPressed();
        }
    }

    protected abstract BaseFragment getBaseFragment(String currentFragmentTag);
}
