package com.djekgrif.alternativeradio.ui.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.ParentViewHolder;
import com.djekgrif.alternativeradio.R;
import com.djekgrif.alternativeradio.manager.ImageLoader;
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
    private ImageLoader imageLoader;
    private int currentParentPosition = 0;
    private int currentChildPosition = 0;

    public void setItemSelectedListener(ItemSelectListener<Channel> itemSelectedListener) {
        this.itemSelectedListener = itemSelectedListener;
    }

    public StationRecyclerViewAdapter(Context context, ImageLoader imageLoader, @NonNull List<StationData> parentList) {
        super(parentList);
        inflater = LayoutInflater.from(context);
        this.imageLoader = imageLoader;
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
        if (!TextUtils.isEmpty(parent.getIconUrl())) {
            parentViewHolder.icon.setVisibility(View.VISIBLE);
            imageLoader.loadDefault(parent.getIconUrl(), parentViewHolder.icon);
        } else {
            parentViewHolder.icon.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBindChildViewHolder(@NonNull ChannelViewHolder childViewHolder, int parentPosition, int childPosition, @NonNull Channel child) {
        childViewHolder.title.setText(child.getName());
        childViewHolder.icon.setVisibility(parentPosition == currentParentPosition && childPosition == currentChildPosition ? View.VISIBLE : View.GONE);
    }

    class StationViewHolder extends ParentViewHolder {

        final TextView title;
        final ImageView icon;

        public StationViewHolder(@NonNull View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.list_item_station_title);
            icon = (ImageView) itemView.findViewById(R.id.list_item_station_icon);
        }
    }

    class ChannelViewHolder extends ChildViewHolder<Channel> {

        final TextView title;
        final ImageView icon;

        public ChannelViewHolder(@NonNull View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.list_item_channel_title);
            icon = (ImageView) itemView.findViewById(R.id.list_item_channel_icon);
            itemView.setOnClickListener(v -> {
                int oldParentPosition = currentParentPosition;
                int oldChildPosition = currentChildPosition;
                currentParentPosition = getParentAdapterPosition();
                currentChildPosition = getChildAdapterPosition();
                if (itemSelectedListener != null && currentChildPosition != RecyclerView.NO_POSITION && currentParentPosition != RecyclerView.NO_POSITION) {
                    itemSelectedListener.onItemClick(v, getParentList().get(currentParentPosition).getChannels().get(currentChildPosition));
                    notifyChildChanged(oldParentPosition, oldChildPosition);
                    notifyChildChanged(currentParentPosition, currentChildPosition);
                }
            });
        }
    }
}
