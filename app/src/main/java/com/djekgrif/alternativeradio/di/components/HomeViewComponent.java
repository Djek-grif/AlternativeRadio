package com.djekgrif.alternativeradio.di.components;

import com.djekgrif.alternativeradio.di.PerView;
import com.djekgrif.alternativeradio.di.modules.HomeFragmentModule;
import com.djekgrif.alternativeradio.ui.fragment.HomeFragment;

import dagger.Component;

/**
 * Created by djek-grif on 10/18/16.
 */

@Component( dependencies = { RadioAppComponent.class }, modules = { HomeFragmentModule.class })
@PerView
public interface HomeViewComponent {
    void inject(HomeFragment homeFragment);
}
