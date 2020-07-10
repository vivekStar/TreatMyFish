package com.webfarms.treatmyfish.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.webfarms.treatmyfish.R;

import java.util.ArrayList;
import java.util.List;

public class StringListAdapter extends BaseAdapter {

    Context context;
    ArrayList<String> stringArrayList;

    public StringListAdapter(Context context, ArrayList<String> stringArrayList) {
        this.context = context;
        this.stringArrayList = stringArrayList;
    }

    @Override
    public int getCount() {
        return stringArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return stringArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View viewOne = convertView;

        if (convertView == null) { // if it's not recycled, initialize some
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            viewOne = inflater.inflate(R.layout.string_single_row, parent, false);
        }

//        TextView _tv_title = viewOne.findViewById(R.id.tv_title);
        TextView _tv_number = viewOne.findViewById(R.id.tv_number);
        _tv_number.setText(stringArrayList.get(position));

        return viewOne;
    }
}
