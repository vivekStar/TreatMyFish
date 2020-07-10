package com.webfarms.treatmyfish.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.webfarms.treatmyfish.R;
import com.webfarms.treatmyfish.bean.BeanDescription;

import java.util.HashMap;
import java.util.List;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;

public class ExpandableListAdapterTwo extends BaseExpandableListAdapter {


    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<BeanDescription>> _listDataChild;
    private String subCatId;

    public ExpandableListAdapterTwo(Context context, String subCatId, List<String> listDataHeader,
                                    HashMap<String, List<BeanDescription>> listChildData) {
        this._context = context;
        this.subCatId = subCatId;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }


    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final BeanDescription desc = (BeanDescription) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.lay_exlist_item, null);
        }

        Log.e("ADP", "21213 BeanDescription : at " + childPosition + " desc " + desc.toString());

        String type = desc.getType();
        String content = desc.getContent();

        final String contentImg = content;

        LinearLayout lyt_parent = convertView.findViewById(R.id.lyt_parent_one);
        LinearLayout llText = convertView.findViewById(R.id.llText);
        LinearLayout llImage = convertView.findViewById(R.id.llImage);

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.lblListItem);

        ImageView img1 = convertView.findViewById(R.id.img1);


        if (type == "dText") {

            txtListChild.setText(content);

            llImage.setVisibility(View.GONE);
            llText.setVisibility(View.VISIBLE);
        }
        if (type == "dImage") {

            byte[] decodedString = Base64.decode(content, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            img1.setImageBitmap(decodedByte);
            llImage.setVisibility(View.VISIBLE);
            llText.setVisibility(View.GONE);

            final int grpPos = groupPosition;
            final int childPos = childPosition;

            img1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BeanDescription desc1 = (BeanDescription) getChild(grpPos, childPos);
                    String content1 = desc1.getContent();

                    byte[] decodedString1 = Base64.decode(content1, Base64.DEFAULT);
                    GalleryDialog(decodedString1);
                }
            });


//            String path1 = GlobalData.ATTACHMENT_PATH + fileName;


//            Original Used code ---------------------- ***
//            Picasso.with(_context)
//                    .load(path1)
//                    .placeholder(R.drawable.img_loading) //this is optional the image to display while the url image is downloading
//                    .error(R.drawable.img_notfound)         //this is also optional if some error has occurred in downloading the image this image would be displayed
//                    .resize(600, 400)
//                    .into(img1);

//            Glide.with(_context).load(path1)
//                    .thumbnail(0.5f)
//                    .crossFade()
//                    .error(R.drawable.img_notfound)
//                    .placeholder(R.drawable.img_loading)
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .into(img1);


            llImage.setVisibility(View.VISIBLE);
            llText.setVisibility(View.GONE);

        }

//        img1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                GalleryDialogImage(contentImg);
//            }
//        });

//        img1.setOnTouchListener(new ImageMatrixTouchHandler(_context));

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        String headerTitle = (String) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.lay_fish_disease_list_two, null);
        }

        Log.e("ADP", "21213 BeanDescription Heading : at " + groupPosition + " header " + headerTitle);

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;

    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    Dialog dialog1;

    private void GalleryDialog(byte[] decodedString) {

        dialog1 = new Dialog(_context);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setContentView(R.layout.row_gallery_view);
        dialog1.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog1.setCancelable(true);

        ImageViewTouch galleryImg = (ImageViewTouch) dialog1.findViewById(R.id.img_bk);

        final Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        galleryImg.setImageBitmap(decodedByte);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // only for gingerbread and newer versions
            galleryImg.setZ(5.0f);
        }

        dialog1.show();
    }

    private void GalleryDialogImage(String url) {

        dialog1 = new Dialog(_context);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setContentView(R.layout.row_gallery_view);
        dialog1.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog1.setCancelable(true);

        ImageViewTouch galleryImg = (ImageViewTouch) dialog1.findViewById(R.id.img_bk);

        Picasso.with(_context)
                .load(url)
                .placeholder(R.drawable.img_loading) //this is optional the image to display while the url image is downloading
                .error(R.drawable.img_notfound)         //this is also optional if some error has occurred in downloading the image this image would be displayed
                .into(galleryImg);

        dialog1.show();
    }


}
