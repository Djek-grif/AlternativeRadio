package com.djekgrif.alternativeradio.ui.activity;

import com.djekgrif.alternativeradio.ui.fragment.HomeFragment;

public class HomeActivity extends BaseSingleFragmentActivity<HomeFragment> {

    @Override
    protected HomeFragment onCreateFragment() {
        return HomeFragment.getInstance();
    }

}
