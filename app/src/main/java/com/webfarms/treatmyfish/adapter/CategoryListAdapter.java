package com.webfarms.treatmyfish.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.webfarms.treatmyfish.R;
import com.webfarms.treatmyfish.bean.BeanCategory;

import java.util.ArrayList;

public class CategoryListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<BeanCategory> categoryArrayList;


    public CategoryListAdapter(Context context, ArrayList<BeanCategory> categoryArrayList) {
        this.context = context;
        this.categoryArrayList = categoryArrayList;
    }

    @Override
    public int getCount() {
        return categoryArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return categoryArrayList.get(position);
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
            viewOne = inflater.inflate(R.layout.cat_list_single_row, parent, false);
        }

        LinearLayout ll_cat_view = (LinearLayout) viewOne.findViewById(R.id.ll_cat_view);
        TextView tv_cat_title = (TextView) viewOne.findViewById(R.id.tv_cat_title);

        BeanCategory category = categoryArrayList.get(position);
        tv_cat_title.setText(category.getCategory());

        ll_cat_view.setBackgroundColor(context.getResources().getColor(R.color.colorListOdd));

       /* if(position % 2 == 0){
            ll_cat_view.setBackgroundColor(context.getResources().getColor(R.color.colorListEven));
//            ll_cat_view.setBackgroundColor(Color.MAGENTA);
//            viewOne.setBackgroundColor(Color.parseColor("#BEBEBE"));
        }else {
            ll_cat_view.setBackgroundColor(context.getResources().getColor(R.color.colorListOdd));
//            ll_cat_view.setBackgroundColor(Color.YELLOW);
//            viewOne.setBackgroundColor(Color.parseColor("#D0D0D0"));
        }
*/
        return viewOne;
    }
}
