package com.example.weatherapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListViewAdapter extends BaseAdapter {

    // Declare Variables

    Context mContext;
    LayoutInflater inflater;
    private List<String> cityName = null;
    private ArrayList<String> arraylist;
    OnClickListener onClickListener;

    public ListViewAdapter(Context context, List<String> cityName, MainActivity activity) {
        mContext = context;
        this.cityName = cityName;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<String>();
        this.arraylist.addAll(cityName);
        onClickListener=activity;
    }

    public class ViewHolder {
        TextView name;
    }

    @Override
    public int getCount() {
        return cityName.size();
    }

    @Override
    public String getItem(int position) {
        return cityName.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.listview_item, null);
            // Locate the TextViews in listview_item.xml
            holder.name = (TextView) view.findViewById(R.id.name);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        holder.name.setText(cityName.get(position));

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onItemClick(cityName.get(position));
            }
        });

        return view;
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        cityName.clear();

        if (charText.length() == 0) {
            cityName.addAll(new ArrayList<String>());
        }
        if (charText.length() == 0) {
            cityName.addAll(new ArrayList<String>());
        } else {
            for (String wp : arraylist) {
                if (wp.toLowerCase(Locale.getDefault()).contains(charText)) {
                    cityName.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

}