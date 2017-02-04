package com.djekgrif.alternativeradio.di.modules;

import com.djekgrif.alternativeradio.di.PerView;
import com.djekgrif.alternativeradio.manager.ConfigurationManager;
import com.djekgrif.alternativeradio.manager.ImageLoader;
import com.djekgrif.alternativeradio.network.ApiService;
import com.djekgrif.alternativeradio.presenter.HomeFragmentPresenter;
import com.djekgrif.alternativeradio.presenter.HomeFragmentPresenterImp;
import com.djekgrif.alternativeradio.view.HomeFragmentView;

import dagger.Module;
import dagger.Provides;

/**
 * Created by djek-grif on 5/25/16.
 */
@Module
public class HomeFragmentModule {

    private HomeFragmentView homeFragmentView;

    public HomeFragmentModule(HomeFragmentView homeFragmentView) {
        this.homeFragmentView = homeFragmentView;
    }

    @PerView
    @Provides
    public HomeFragmentPresenter provideHomeActivityPresenter(ImageLoader imageLoader, ApiService apiService, ConfigurationManager configurationManager){
        return new HomeFragmentPresenterImp(homeFragmentView, apiService, imageLoader, configurationManager);
    }
}
