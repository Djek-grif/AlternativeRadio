package com.djekgrif.alternativeradio.di.modules;

import com.djekgrif.alternativeradio.manager.ImageLoader;
import com.djekgrif.alternativeradio.ui.presenter.HomeFragmentPresenter;
import com.djekgrif.alternativeradio.ui.presenter.HomeFragmentPresenterImp;
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

    @Provides
    public HomeFragmentPresenter provideHomeFragmentPresenter(ImageLoader imageLoader){
        return new HomeFragmentPresenterImp(homeFragmentView, imageLoader);
    }
}
