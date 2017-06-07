package com.djekgrif.alternativeradio.di.modules;

import com.djekgrif.alternativeradio.di.ActivityScope;
import com.djekgrif.alternativeradio.manager.ImageLoader;
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

    @Provides
    @ActivityScope
    public HomeFragmentPresenter provideHomeFragmentPresenter(ImageLoader imageLoader){
        return new HomeFragmentPresenterImp(homeFragmentView, imageLoader);
    }
}
