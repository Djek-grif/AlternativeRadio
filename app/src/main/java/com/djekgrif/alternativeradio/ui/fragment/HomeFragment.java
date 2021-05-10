package com.djekgrif.alternativeradio.ui.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import androidx.core.view.GravityCompat;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
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
import java.util.Objects;

import javax.inject.Inject;

/**
 * Created by djek-grif on 5/25/16.
 */
public class HomeFragment extends BaseFragment implements HomeFragmentView {

    private TextView toolBarTitle;
    private FloatingActionButton playButton;
    private FloatingActionButton textButton;
    private ProgressBar textProgressBar;
    private ImageView headerImage;
    private ImageView artistImage;
    private TextView songInfo;
    private RecyclerView recentlyList;
    private DrawerLayout drawer;
    private RecyclerView stationList;
    private ProgressBar progressBar;
    private View shareButton;


    @Inject
    HomeFragmentPresenter homeFragmentPresenter;
    @Inject
    protected ImageLoader imageLoader;

    private HomeRecyclerViewAdapter recentlyListAdapter;
    private StationRecyclerViewAdapter stationListAdapter;

    public static HomeFragment getInstance() {
        return new HomeFragment();
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
        return inflater.inflate(R.layout.fragment_home, null);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = view.findViewById(R.id.home_toolbar);
        toolBarTitle = view.findViewById(R.id.toolbar_title);
        playButton = view.findViewById(R.id.home_play_button);
        textButton = view.findViewById(R.id.home_text_btn);
        textProgressBar = view.findViewById(R.id.home_text_progress);
        headerImage = view.findViewById(R.id.home_header_image);
        artistImage = view.findViewById(R.id.home_artist_image);
        songInfo = view.findViewById(R.id.home_header_image_title);
        recentlyList = view.findViewById(R.id.home_recently);
        drawer = view.findViewById(R.id.drawer_layout);
        NavigationView navigationView = view.findViewById(R.id.nav_view);
        stationList = view.findViewById(R.id.home_drawer_stations);
        progressBar = view.findViewById(R.id.home_progress);
        shareButton = view.findViewById(R.id.home_header_share);

        ((AppCompatActivity) Objects.requireNonNull(getActivity())).setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(), drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
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
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        homeFragmentPresenter.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return homeFragmentPresenter.onOptionsItemSelected(item);
    }

    @Override
    public void invalidateOptionsMenu() {
        setMenuVisibility(true);
    }

    private void injectComponent() {
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
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
        return false;
    }

    @Override
    public Intent getIntent() {
        return Objects.requireNonNull(getActivity()).getIntent();
    }

    @Override
    public void setSupportMediaController(MediaControllerCompat mediaController) {
        MediaControllerCompat.setMediaController(Objects.requireNonNull(getActivity()), mediaController);
    }

    @Override
    public MediaControllerCompat getSupportMediaController(){
        return MediaControllerCompat.getMediaController(Objects.requireNonNull(getActivity()));
    }

    @Override
    public void finish() {
        Objects.requireNonNull(getActivity()).finish();
    }

}
