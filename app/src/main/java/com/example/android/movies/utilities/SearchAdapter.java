package com.example.android.movies.utilities;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.android.movies.Items.Movie;

import java.util.ArrayList;

public class SearchAdapter extends ArrayAdapter {
    private ArrayList<Movie> dataList;
    private Context mContext;
    private int searchResultItemLayout;

    public SearchAdapter(Context context, int resource, ArrayList<Movie> storeSourceDataLst) {
        super(context, resource, storeSourceDataLst);
        dataList = storeSourceDataLst;
        mContext = context;
        searchResultItemLayout = resource;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Movie getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {

        if (view == null) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(searchResultItemLayout, parent, false);
        }

        TextView resultItem = (TextView) view.findViewById(android.R.id.text1);
    //    resultItem.setText(getItem(position));
        return view;
    }
}
