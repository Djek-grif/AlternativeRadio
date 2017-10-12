package com.djekgrif.alternativeradio.di.components;

import com.djekgrif.alternativeradio.di.modules.HomeFragmentModule;
import com.djekgrif.alternativeradio.ui.activity.HomeActivity;
import com.djekgrif.alternativeradio.ui.fragment.HomeFragment;

import dagger.Subcomponent;

/**
 * Created by djek-grif on 10/18/16.
 */
@Subcomponent(modules = HomeFragmentModule.class)
public interface HomeViewComponent {

    void inject(HomeFragment homeFragment);
    void inject(HomeActivity homeActivity);
}
