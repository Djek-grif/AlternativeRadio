package com.djekgrif.alternativeradio.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.djekgrif.alternativeradio.App;
import com.djekgrif.alternativeradio.R;
import com.djekgrif.alternativeradio.di.components.DaggerHomeViewComponent;
import com.djekgrif.alternativeradio.di.components.HomeViewComponent;
import com.djekgrif.alternativeradio.di.modules.HomeFragmentModule;
import com.djekgrif.alternativeradio.manager.ImageLoader;
import com.djekgrif.alternativeradio.network.model.RecentlyItem;
import com.djekgrif.alternativeradio.network.model.StationData;
import com.djekgrif.alternativeradio.presenter.HomeFragmentPresenter;
import com.djekgrif.alternativeradio.ui.adapters.RecentlyRecyclerViewAdapter;
import com.djekgrif.alternativeradio.ui.adapters.StationRecyclerViewAdapter;
import com.djekgrif.alternativeradio.view.HomeFragmentView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by djek-grif on 5/25/16.
 */
public class HomeFragment extends BaseFragment implements HomeFragmentView {

    @BindView(R.id.home_toolbar)
    Toolbar toolbar;
    @BindView(R.id.home_play_button)
    FloatingActionButton actionButton;
    @BindView(R.id.home_header_image)
    ImageView headerImage;
    @BindView(R.id.home_artist_image)
    ImageView artistImage;
    @BindView(R.id.home_header_image_title)
    TextView songInfo;
    @BindView(R.id.home_recently)
    RecyclerView recentlyList;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.home_drawer_stations)
    RecyclerView stationList;
    @BindView(R.id.home_progress)
    ProgressBar progressBar;


    @Inject
    HomeFragmentPresenter homeFragmentPresenter;

    private RecentlyRecyclerViewAdapter recentlyListAdapter;
    private StationRecyclerViewAdapter stationListAdapter;

    public static HomeFragment getInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setMenuVisibility(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, null);
        ButterKnife.bind(this, view);

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(), drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        homeFragmentPresenter.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        homeFragmentPresenter.onResume();
    }

    @Override
    public boolean onBackPressed() {
        return homeFragmentPresenter.onBackPressed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        homeFragmentPresenter.onDestroy();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        injectComponent();
        actionButton.setEnabled(false);
        actionButton.setOnClickListener(view -> homeFragmentPresenter.onClickActionButton());
        recentlyList.setLayoutManager(new LinearLayoutManager(getActivity()));
        recentlyList.setHasFixedSize(true);
        recentlyListAdapter = new RecentlyRecyclerViewAdapter();
        recentlyList.setAdapter(recentlyListAdapter);
//        navigationView.setNavigationItemSelectedListener(item -> {
//            return homeFragmentPresenter.navigationItemSelected(item);
//            Toast.makeText(getActivity(), "Fragment's listener", Toast.LENGTH_LONG).show();
//            drawer.closeDrawer(GravityCompat.START);
//            return true;
//        });
        stationList.setLayoutManager(new LinearLayoutManager(getActivity()));
        stationList.setHasFixedSize(true);
        stationListAdapter = new StationRecyclerViewAdapter(getActivity(), new ArrayList<>());
        stationList.setAdapter(stationListAdapter);
        stationListAdapter.setItemSelectedListener(homeFragmentPresenter.getChannelItemListener());
        homeFragmentPresenter.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        homeFragmentPresenter.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return homeFragmentPresenter.onOptionsItemSelected(item);
    }

    @Override
    public void invalidateOptionsMenu() {
        setMenuVisibility(true);
    }

    protected void injectComponent() {
        HomeViewComponent homeViewComponent = DaggerHomeViewComponent.builder()
                .radioAppComponent(App.getInstance().getAppComponent())
                .homeFragmentModule(new HomeFragmentModule(this))
                .build();
        homeViewComponent.inject(this);
    }

    @Override
    public void setUpUI() {
        actionButton.setEnabled(true);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void updateSoundInfo(String string) {
        songInfo.setText(string);
    }

    @Override
    public void updateRecentlyList(List<RecentlyItem> recentlyItemList) {
        recentlyListAdapter.updateData(recentlyItemList);
    }

    @Override
    public void updateStationList(List<StationData> stationDataList) {
        stationListAdapter.setParentList(stationDataList, false);
    }

    @Override
    public void updateImage(ImageLoader imageLoader, String imageUrl) {
        imageLoader.loadDefault(imageUrl, artistImage);
        imageLoader.loadDefault(imageUrl, headerImage);
    }

    @Override
    public void updateActionButton(int state) {
        actionButton.setImageResource(state == PlaybackStateCompat.STATE_PLAYING ? android.R.drawable.ic_media_pause : android.R.drawable.ic_media_play);
    }

    @Override
    public boolean closeDrawer() {
        if(drawer.isDrawerOpen(Gravity.LEFT)){
            drawer.closeDrawer(Gravity.LEFT);
            return true;
        }
        return false;
    }

    @Override
    public Intent getIntent() {
        return getActivity().getIntent();
    }

    @Override
    public void setSupportMediaController(MediaControllerCompat mediaController) {
        getActivity().setSupportMediaController(mediaController);
    }

    @Override
    public MediaControllerCompat getSupportMediaController(){
        return getActivity().getSupportMediaController();
    }

    @Override
    public void finish() {
        getActivity().finish();
    }

}
