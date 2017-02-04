package com.djekgrif.alternativeradio.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.ParentViewHolder;
import com.djekgrif.alternativeradio.R;
import com.djekgrif.alternativeradio.network.model.Channel;
import com.djekgrif.alternativeradio.network.model.StationData;

import java.util.List;

/**
 * Created by djek-grif on 2/4/17.
 */

public class StationRecyclerViewAdapter extends ExpandableRecyclerAdapter<StationData, Channel,
        StationRecyclerViewAdapter.StationViewHolder, StationRecyclerViewAdapter.ChannelViewHolder> {

    private LayoutInflater inflater;
    protected ItemSelectListener<Channel> itemSelectedListener;

    public void setItemSelectedListener(ItemSelectListener<Channel> itemSelectedListener) {
        this.itemSelectedListener = itemSelectedListener;
    }

    public StationRecyclerViewAdapter(Context context, @NonNull List<StationData> parentList) {
        super(parentList);
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public StationViewHolder onCreateParentViewHolder(@NonNull ViewGroup parentViewGroup, int viewType) {
        return new StationViewHolder(inflater.inflate(R.layout.list_item_station, parentViewGroup, false));
    }

    @NonNull
    @Override
    public ChannelViewHolder onCreateChildViewHolder(@NonNull ViewGroup childViewGroup, int viewType) {
        return new ChannelViewHolder(inflater.inflate(R.layout.list_item_channel, childViewGroup, false));
    }

    @Override
    public void onBindParentViewHolder(@NonNull StationViewHolder parentViewHolder, int parentPosition, @NonNull StationData parent) {
        parentViewHolder.title.setText(parent.getName());
    }

    @Override
    public void onBindChildViewHolder(@NonNull ChannelViewHolder childViewHolder, int parentPosition, int childPosition, @NonNull Channel child) {
        childViewHolder.title.setText(child.getName());
    }

    class StationViewHolder extends ParentViewHolder {

        final TextView title;

        public StationViewHolder(@NonNull View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.list_item_station_title);
        }
    }

    class ChannelViewHolder extends ChildViewHolder<Channel> {

        final TextView title;

        public ChannelViewHolder(@NonNull View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.list_item_channel_title);
            itemView.setOnClickListener(v -> {
                int parentPosition = getParentAdapterPosition();
                int childPosition = getChildAdapterPosition();
                if (itemSelectedListener != null && childPosition != RecyclerView.NO_POSITION && parentPosition != RecyclerView.NO_POSITION) {
                    itemSelectedListener.onItemClick(v, getParentList().get(parentPosition).getChannels().get(childPosition));
                }
            });
        }
    }
}
