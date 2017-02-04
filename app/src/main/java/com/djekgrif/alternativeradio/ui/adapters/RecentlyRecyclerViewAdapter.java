package com.djekgrif.alternativeradio.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.djekgrif.alternativeradio.R;
import com.djekgrif.alternativeradio.network.model.RecentlyItem;
import com.djekgrif.alternativeradio.ui.utils.StringUtils;

import java.util.List;

/**
 * Created by djek-grif on 1/8/17.
 */

public class RecentlyRecyclerViewAdapter extends BaseRecyclerViewAdapter<RecentlyItem, RecentlyRecyclerViewAdapter.RecentlyItemHolder> {

    @Override
    public RecentlyItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecentlyItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_recently, null));
    }

    @Override
    public void onBindViewHolder(RecentlyItemHolder holder, int position) {
        RecentlyItem recentlyItem = dataList.get(position);
        holder.time.setText(StringUtils.getNotNullString(recentlyItem.getTime()));
        holder.name.setText(StringUtils.getNotNullString(recentlyItem.getArtistName()));
        holder.track.setText(String.format(holder.track.getResources().getString(R.string.recently_track_format), recentlyItem.getTrackName()));
    }

    public void updateData(List<RecentlyItem> recentlyItems){
        dataList = recentlyItems;
        notifyDataSetChanged();
    }


    public class RecentlyItemHolder extends RecyclerView.ViewHolder {

        private final TextView name;
        private final TextView track;
        private final TextView time;

        public RecentlyItemHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.list_item_recently_name);
            track = (TextView) itemView.findViewById(R.id.list_item_recently_track);
            time = (TextView) itemView.findViewById(R.id.list_item_recently_time);
        }
    }
}
