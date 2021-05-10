package com.djekgrif.alternativeradio.ui.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by djek-grif on 7/11/17.
 */

public abstract class BaseRecyclerViewSpaceAdapter<D, T extends BaseRecyclerViewSpaceAdapter.FooterViewHolder> extends BaseRecyclerViewAdapter<D, BaseRecyclerViewSpaceAdapter.FooterViewHolder> {

    public static final int ITEM = 0;
    public static final int FOOTER = 1;

    private int footerHeight = 240;

    public int getFooterHeight() {
        return footerHeight;
    }

    public void setFooterHeight(int footerHeight) {
        this.footerHeight = footerHeight;
    }

    @Override
    public BaseRecyclerViewSpaceAdapter.FooterViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        if(viewType != FOOTER){
            return onCreateItemViewHolder(parent, viewType);
        }else{
            View view = new View(parent.getContext());
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getFooterHeight()));
            return new FooterViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(BaseRecyclerViewSpaceAdapter.FooterViewHolder holder, int position){
        if(getItemViewType(position) != FOOTER){
            onBindItemViewHolder((T)holder, position);
        }
    }


    @Override
    public int getItemViewType(int position) {
        return position == dataList.size() ? FOOTER : ITEM;
    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size() + 1; //added footer item
    }

    protected abstract void onBindItemViewHolder(T holder, int position);
    public abstract T onCreateItemViewHolder(ViewGroup parent, int viewType);

    protected class FooterViewHolder extends RecyclerView.ViewHolder{

        public FooterViewHolder(View view) {
            super(view);
        }
    }
}
