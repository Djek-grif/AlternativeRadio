package com.djekgrif.alternativeradio.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by djek-grif on 1/8/17.
 */

public abstract class BaseRecyclerViewAdapter<D, T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {

    protected List<D> dataList;
    protected ItemSelectListener<D> itemOnLongClickListener;
    protected ItemSelectListener<D> itemSelectedListener;
    protected ItemSelectListener<D> itemSelectedViewListener;

    public void setDataList(List<D> dataList) {
        this.dataList = dataList;
    }

    public List<D> getDataList() {
        return dataList;
    }

    public void setItemSelectedListener(ItemSelectListener<D> itemSelectedListener) {
        this.itemSelectedListener = itemSelectedListener;
    }

    public void setItemOnLongClickListener(ItemSelectListener<D> itemOnLongClickListener) {
        this.itemOnLongClickListener = itemOnLongClickListener;
    }

    public void setItemSelectedViewListener(ItemSelectListener<D> itemSelectedViewListener) {
        this.itemSelectedViewListener = itemSelectedViewListener;
    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

    public void clear(){
        if(dataList != null && !dataList.isEmpty()){
            dataList.clear();
        }
    }

    @Override
    public abstract void onBindViewHolder(T holder, int position);

    @Override
    public abstract T onCreateViewHolder(ViewGroup parent, int viewType);
}
