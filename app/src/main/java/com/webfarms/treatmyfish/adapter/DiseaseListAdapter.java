package com.webfarms.treatmyfish.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.webfarms.treatmyfish.R;
import com.webfarms.treatmyfish.bean.BeamSubCategory;

import java.util.ArrayList;
import java.util.List;

public class DiseaseListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<BeamSubCategory> items = new ArrayList<>();

    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, BeamSubCategory obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public DiseaseListAdapter(Context context, List<BeamSubCategory> items) {
        this.items = items;
        ctx = context;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView tv_disease_name;
        public LinearLayout ll_cat_view;

        public OriginalViewHolder(View v) {
            super(v);
            image = (ImageView) v.findViewById(R.id.image);
            tv_disease_name = (TextView) v.findViewById(R.id.tv_disease_name);
            ll_cat_view = (LinearLayout) v.findViewById(R.id.ll_cat_view);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.disease_list_single_row, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof OriginalViewHolder) {
            OriginalViewHolder view = (OriginalViewHolder) holder;

            BeamSubCategory p = items.get(position);
            view.tv_disease_name.setText(p.getSubName());

//            view.ll_cat_view.setBackgroundColor(ctx.getResources().getColor(R.color.colorListEven));

          /*  if(position % 2 == 0){
                view.ll_cat_view.setBackgroundColor(ctx.getResources().getColor(R.color.colorListEven));
//            ll_cat_view.setBackgroundColor(Color.MAGENTA);
//            viewOne.setBackgroundColor(Color.parseColor("#BEBEBE"));
            }else {

                view.ll_cat_view.setBackgroundColor(ctx.getResources().getColor(R.color.colorListOdd));
//            ll_cat_view.setBackgroundColor(Color.YELLOW);
//            viewOne.setBackgroundColor(Color.parseColor("#D0D0D0"));
            }*/

            view.ll_cat_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, items.get(position), position);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}
