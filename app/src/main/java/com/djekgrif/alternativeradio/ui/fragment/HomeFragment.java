package com.djekgrif.alternativeradio.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.graphics.drawable.VectorDrawableCompat;
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

import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;
import com.djekgrif.alternativeradio.App;
import com.djekgrif.alternativeradio.R;
import com.djekgrif.alternativeradio.di.modules.HomeFragmentModule;
import com.djekgrif.alternativeradio.manager.ImageLoader;
import com.djekgrif.alternativeradio.network.model.SongTextItem;
import com.djekgrif.alternativeradio.network.model.StationData;
import com.djekgrif.alternativeradio.ui.adapters.HomeRecyclerViewAdapter;
import com.djekgrif.alternativeradio.ui.adapters.StationRecyclerViewAdapter;
import com.djekgrif.alternativeradio.ui.model.HomeListItem;
import com.djekgrif.alternativeradio.ui.presenter.HomeFragmentPresenter;
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
    @BindView(R.id.toolbar_title)
    TextView toolBarTitle;
    @BindView(R.id.home_play_button)
    FloatingActionButton playButton;
    @BindView(R.id.home_text_btn)
    FloatingActionButton textButton;
    @BindView(R.id.home_text_progress)
    ProgressBar textProgressBar;
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
    @BindView(R.id.home_header_share)
    View shareButton;


    @Inject
    protected HomeFragmentPresenter homeFragmentPresenter;
    @Inject
    protected ImageLoader imageLoader;

    private HomeRecyclerViewAdapter recentlyListAdapter;
    private StationRecyclerViewAdapter stationListAdapter;

    public static HomeFragment getInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectComponent();
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
    public void onStart() {
        super.onStart();
        homeFragmentPresenter.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        homeFragmentPresenter.onStop();
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
        playButton.setEnabled(false);
        textButton.setEnabled(false);
        playButton.setOnClickListener(view -> homeFragmentPresenter.onClickActionButton());
        shareButton.setOnClickListener(vew -> homeFragmentPresenter.onClickShare(songInfo.getText().toString()));
        textButton.setOnClickListener(v -> homeFragmentPresenter.onClickTextButton());

        recentlyList.setLayoutManager(new LinearLayoutManager(getActivity()));
        recentlyList.setHasFixedSize(true);
        recentlyListAdapter = new HomeRecyclerViewAdapter(imageLoader);
        recentlyList.setAdapter(recentlyListAdapter);

        stationList.setLayoutManager(new LinearLayoutManager(getActivity()));
        stationList.setHasFixedSize(true);
        stationListAdapter = new StationRecyclerViewAdapter(getActivity(), imageLoader, new ArrayList<>());
        stationList.setAdapter(stationListAdapter);
        stationListAdapter.setItemSelectedListener(homeFragmentPresenter.getChannelItemListener());
        stationListAdapter.setExpandCollapseListener(new ExpandableRecyclerAdapter.ExpandCollapseListener() {
            @Override
            public void onParentExpanded(int parentPosition) {
                stationListAdapter.collapseAllParents();
                stationListAdapter.expandParent(parentPosition);
            }

            @Override
            public void onParentCollapsed(int parentPosition) {}
        });
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
        App.getInstance().getAppComponent().plus(new HomeFragmentModule(this)).inject(this);
    }

    @Override
    public void setUpUI() {
        playButton.setEnabled(true);
        textButton.setEnabled(true);
        progressBar.setVisibility(View.GONE);
        textButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void openSongText(SongTextItem songTextItem) {
        recentlyListAdapter.addSongTextItem(songTextItem);
        recentlyList.scrollToPosition(0);
    }

    @Override
    public void failedSongText() {
        Snackbar.make(playButton, R.string.sorry_no_results, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void hideSongText() {
        recentlyListAdapter.removeSongTextItem();
    }

    @Override
    public boolean isSongTextOpen() {
        return recentlyListAdapter.isSongTextItem();
    }

    @Override
    public void hideTextButtonProgress() {
        textProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void showTextButtonProgress() {
        textProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void updateTitle(String title) {
        toolBarTitle.setText(title);
    }

    @Override
    public void updateSoundInfo(String string) {
        songInfo.setText(string);
    }

    @Override
    public void updateRecentlyList(List<HomeListItem> recentlyItemList) {
        recentlyListAdapter.updateData(recentlyItemList);
    }

    @Override
    public void updateStationList(List<StationData> stationDataList) {
        stationListAdapter.setParentList(stationDataList, false);
    }

    @Override
    public void updateImage(ImageLoader imageLoader, String imageUrl) {
        imageLoader.loadDefault(imageUrl, artistImage, VectorDrawableCompat.create(getResources(), R.drawable.ic_guitar, null));
        imageLoader.loadDefault(imageUrl, headerImage, VectorDrawableCompat.create(getResources(), R.drawable.ic_guitar_panarama, null));
    }

    @Override
    public void updateActionButton(int state) {
        playButton.setImageResource(state == PlaybackStateCompat.STATE_PLAYING ? android.R.drawable.ic_media_pause : android.R.drawable.ic_media_play);
    }

    @Override
    public boolean closeDrawer() {
        if(drawer.isDrawerOpen(Gravity.START)){
            drawer.closeDrawer(Gravity.START);
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
        MediaControllerCompat.setMediaController(getActivity(), mediaController);
    }

    @Override
    public MediaControllerCompat getSupportMediaController(){
        return MediaControllerCompat.getMediaController(getActivity());
    }

    @Override
    public void finish() {
        getActivity().finish();
    }

}
