package com.google.douban.cardlayout;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by logan676 on 2019-08-02.
 */
public abstract class CollapseCardAdapter extends BaseAdapter {

    private List<CardBean> mCardBeans = new ArrayList<>();

    private Context mContext;

    public CollapseCardAdapter(Context context) {
    }

    public int getCount() {
        return mCardBeans.size();
    }

    @Override
    public abstract Object getItem(int position);

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public abstract CardView getView(int position, View convertView, ViewGroup parent);
}
