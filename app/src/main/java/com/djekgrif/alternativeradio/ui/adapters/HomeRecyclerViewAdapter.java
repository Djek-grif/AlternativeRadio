package com.djekgrif.alternativeradio.ui.adapters;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.djekgrif.alternativeradio.R;
import com.djekgrif.alternativeradio.manager.ImageLoader;
import com.djekgrif.alternativeradio.network.model.RecentlyItem;
import com.djekgrif.alternativeradio.network.model.SongTextItem;
import com.djekgrif.alternativeradio.ui.model.HomeListItem;
import com.djekgrif.alternativeradio.ui.utils.StringUtils;

import java.util.List;

/**
 * Created by djek-grif on 1/8/17.
 */

public class HomeRecyclerViewAdapter extends BaseRecyclerViewSpaceAdapter<HomeListItem, HomeRecyclerViewAdapter.HomeItemHolder> {

    private ImageLoader imageLoader;

    public HomeRecyclerViewAdapter(ImageLoader imageLoader) {
        this.imageLoader = imageLoader;
    }

    @Override
    public HomeItemHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        return viewType == HomeListItem.RECENTLY_ITEM ?
                new RecentlyItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_recently, parent, false)) :
                new SongTextItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_song_text_item, parent, false));
    }

    @Override
    protected void onBindItemViewHolder(HomeItemHolder holder, int position) {
        if (getItemViewType(position) == HomeListItem.RECENTLY_ITEM) {
            bindRecentlyItem(holder, position);
        } else {
            bindTextItem(holder, position);
        }
    }

    private void bindRecentlyItem(HomeItemHolder holder, int position) {
        RecentlyItem recentlyItem = (RecentlyItem) dataList.get(position);
        RecentlyItemHolder recentlyItemHolder = (RecentlyItemHolder) holder;
        if(!TextUtils.isEmpty(recentlyItem.getImage())) {
            imageLoader.loadDefault(recentlyItem.getImage(), recentlyItemHolder.image);
        } else {
            recentlyItemHolder.image.setImageResource(R.drawable.ic_guitar);
        }
        recentlyItemHolder.name.setText(String.format(recentlyItemHolder.name.getResources().getString(R.string.recently_track_format),
                StringUtils.getNotNull(recentlyItem.getArtistName()), StringUtils.getNotNull(recentlyItem.getTrackName())));
    }

    private void bindTextItem(HomeItemHolder holder, int position) {
        SongTextItem songTextItem = (SongTextItem) dataList.get(position);
        SongTextItemHolder itemHolder = (SongTextItemHolder) holder;
        itemHolder.title.setText(songTextItem.getTitle());
        itemHolder.text.setText(songTextItem.getText());
    }

    @Override
    public int getItemViewType(int position) {
        int type = super.getItemViewType(position);
        if (type == ITEM) {
            return dataList.get(position).getType();
        } else {
            return type;
        }
    }

    public void addSongTextItem(SongTextItem songTextItem) {
        if (dataList != null) {
            dataList.add(0, songTextItem);
            notifyItemInserted(0);
        }
    }

    public void removeSongTextItem() {
        if (isSongTextItem()) {
            dataList.remove(0);
            notifyItemRemoved(0);
        }
    }

    public boolean isSongTextItem() {
        return dataList != null && !dataList.isEmpty() && dataList.get(0) instanceof SongTextItem;
    }

    public void updateData(List<HomeListItem> recentlyItems) {
        if (isSongTextItem()) {
            recentlyItems.add(0, dataList.get(0));
        }
        dataList = recentlyItems;
        notifyDataSetChanged();
    }

    class HomeItemHolder extends BaseRecyclerViewSpaceAdapter.FooterViewHolder {
        HomeItemHolder(View itemView) {
            super(itemView);
        }
    }

    private class SongTextItemHolder extends HomeItemHolder {

        private final TextView title;
        private final TextView text;

        public SongTextItemHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.list_item_song_title);
            text = (TextView) itemView.findViewById(R.id.list_item_song_text);
        }
    }

    private class RecentlyItemHolder extends HomeItemHolder {

        private final TextView name;
        //        private final TextView track;
        private final ImageView image;

        public RecentlyItemHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.list_item_recently_track);
//            track = (TextView) itemView.findViewById(R.id.list_item_recently_track);
            image = (ImageView) itemView.findViewById(R.id.list_item_recently_img);
        }
    }
}
