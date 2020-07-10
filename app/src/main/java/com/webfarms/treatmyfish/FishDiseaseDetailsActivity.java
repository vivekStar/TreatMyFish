package com.webfarms.treatmyfish;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.webfarms.treatmyfish.adapter.ExpandableListAdapterTwo;
import com.webfarms.treatmyfish.bean.BeanDescription;
import com.webfarms.treatmyfish.bean.BeanTopics;
import com.webfarms.treatmyfish.utils.CommonUtil;
import com.webfarms.treatmyfish.utils.DatabaseHelper;
import com.webfarms.treatmyfish.utils.GlobalData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FishDiseaseDetailsActivity extends AppCompatActivity {

    private static final String TAG = "FishDiseaseDetailsActiv";

    private RequestQueue requestQueue;
    private ProgressDialog pDialog;

    private Context context;
    private Toolbar toolbar;

    private String subCatId;

    private ImageView imgDis1, imgDis2, imgDis3, imageDown;
    private LinearLayout llImages, llImagesDis;
    private ExpandableListAdapterTwo listAdapter;
    private ExpandableListView expListView;
    private List<String> listDataHeader, listDataHeaderOne;
    private HashMap<String, List<BeanDescription>> listDataChild, listDataChildOne;

    private ArrayList<BeanDescription> tempOneList;

    private ArrayList<BeanTopics> topicsArrayList, topicsArrayListOne;

    private int count = 0;
    private boolean isImagesVisible = false;

    private DatabaseHelper helper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.lay_fish_disease_details);

        initialize();

        String subName = getIntent().getStringExtra("subName");

        subCatId = getIntent().getStringExtra("subCatId");

        toolbar = GlobalData.initToolBar(this, subName, true);

        topicsArrayList = new ArrayList<>();
        topicsArrayListOne = new ArrayList<>();

//        new DownloadImageData().execute("");

        getSubCategoryDetails();

    }


    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
        return stream.toByteArray();
    }

    private void initialize() {

        context = FishDiseaseDetailsActivity.this;
        helper = new DatabaseHelper(context);

        llImages = findViewById(R.id.ll_images);
        llImagesDis = findViewById(R.id.ll_images_dis);
        imgDis1 = findViewById(R.id.img_dis1);
        imgDis2 = findViewById(R.id.img_dis2);
        imgDis3 = findViewById(R.id.img_dis3);
        imageDown = findViewById(R.id.image_down);

        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExp);

        requestQueue = Volley.newRequestQueue(context);

        pDialog = new ProgressDialog(context);
        pDialog.setIndeterminate(true);
        pDialog.setTitle(getString(R.string.fetching_data));
        pDialog.setMessage(getString(R.string.please_wait));
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setCancelable(false);

        //logic to close list's item on click of other item
        final int[] prevExpandPosition = {-1};

        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (prevExpandPosition[0] >= 0 && prevExpandPosition[0] != groupPosition) {
                    expListView.collapseGroup(prevExpandPosition[0]);
                }
                prevExpandPosition[0] = groupPosition;
            }
        });


        llImagesDis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isImagesVisible) {
                    isImagesVisible = true;
                    llImages.setVisibility(View.VISIBLE);
                    imageDown.animate().rotation(180).start();
                } else {
                    isImagesVisible = false;
                    llImages.setVisibility(View.GONE);
                    imageDown.animate().rotation(0).start();
                }
            }
        });

    }


    public void getSubCategoryDetails() {

        String language = CommonUtil.getSharePreferenceString(context, GlobalData.LANGUAGE, GlobalData.ENGLISH_LANGUAGE);

        showProgressDialog();

        JSONObject json = new JSONObject();

        try {

            json.put("subCategoryId", subCatId);
            json.put("language", language);

        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.e(TAG, "getAllTopics : " + json.toString());

//        public static final String SERVICE_GET_ALL_TOPICS_LIST = "content/getAllTopics";
        final String urlFarmerInfo = GlobalData.FILE_URL + GlobalData.SERVICE_GET_ALL_TOPICS_LIST;


        JsonObjectRequest jsonContactUploadReq = new JsonObjectRequest(Request.Method.POST, urlFarmerInfo, json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.e(TAG, "getAllTopics res : " + response.toString());

                        try {
                            String responseCode = response.getString("responseCode");

                            if (responseCode.equalsIgnoreCase("200")) {
                                // preparing list data
                                prepareListData(response);

                            }
                            if (responseCode.equalsIgnoreCase("402")) {

//                                Toast.makeText(context, "Server Error..!", Toast.LENGTH_SHORT).show();
                                GlobalData.showNoResponseDialog(context);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
                error.printStackTrace();
//                Toast.makeText(context, "Server Error...", Toast.LENGTH_LONG).show();
                GlobalData.showSeverErrorDialog(context);
                if (pDialog.isShowing()) {
                    hideProgressDialog();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

        jsonContactUploadReq.setRetryPolicy(new DefaultRetryPolicy(
                GlobalData.REQUEST_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(jsonContactUploadReq);

    }

    //Add new Language Strings to match with content


    private void prepareListData(JSONObject response) {

        listDataHeader = new ArrayList<String>();
        listDataHeaderOne = new ArrayList<String>();

        listDataChild = new HashMap<String, List<BeanDescription>>();
        listDataChildOne = new HashMap<String, List<BeanDescription>>();

        boolean isGrossAdded = false;
        boolean isMicroAdded = false;

        try {

            JSONObject jsonDescription = response.getJSONObject("description");

            JSONArray jsonDesList = jsonDescription.getJSONArray("dList");

            JSONArray topicList = jsonDescription.getJSONArray("topicList");

            for (int i = 0; i < topicList.length(); i++) {

                JSONObject json = topicList.getJSONObject(i);

                String topicName = json.getString("topicName");

                String topicId = json.getString("topicId");

                BeanTopics topics = new BeanTopics(topicId, topicName);

                topicsArrayList.add(topics);

                listDataHeader.add(topicName);

            }


            for (int i = 0; i < topicsArrayList.size(); i++) {

                String topicId = topicsArrayList.get(i).getTopicId();
                String topicName = topicsArrayList.get(i).getTopicName();

                ArrayList<BeanDescription> tempList = new ArrayList<>();

                for (int k = 0; k < jsonDesList.length(); k++) {

                    JSONObject dJsonObj = (JSONObject) jsonDesList.get(k);
                    count++;

                    boolean has = dJsonObj.has("dImage");

                    boolean isImgNull = dJsonObj.isNull("dImage");


                    if (!isImgNull) {

                        JSONObject jsondImage = dJsonObj.getJSONObject("dImage");

                        String topId = jsondImage.getString("topicId");

                        //                        ******************* Warning  Set String images according to language selected

                        String language = CommonUtil.getSharePreferenceString(context, GlobalData.LANGUAGE, "0");

                        if (language.equalsIgnoreCase(GlobalData.HINDI_LANGUAGE)) {
                            String gross = getResources().getString(R.string.gross_ph);
                            String micro = getResources().getString(R.string.micro_ph);
                            if (topicName.equalsIgnoreCase(gross)) {
                                topicName = "Gross Photographs";
                            } else if (topicName.equalsIgnoreCase(micro)) {
                                topicName = "Microscopic Photographs";
                            }
                        }


                        if (language.equalsIgnoreCase(GlobalData.ORIYA_LANGUAGE)) {
                            String gross = getResources().getString(R.string.gross_ph_or);
                            String micro = getResources().getString(R.string.micro_ph_or);
                            if (topicName.equalsIgnoreCase(gross)) {
                                topicName = "Gross Photographs";
                            } else if (topicName.equalsIgnoreCase(micro)) {
                                topicName = "Microscopic Photographs";
                            }
                        }

                        if (topId.equalsIgnoreCase(topicId)) {
//error
                            ArrayList<String> imagesStrings = helper.getOnlyImagesString(subCatId, topicName);

                            if (imagesStrings.size() > 0) {

                                if (topicName.equalsIgnoreCase(GlobalData.GROSS_PHOTOGRAPH)) {

                                    if (isGrossAdded == false) {
                                        for (int f = 0; f < imagesStrings.size(); f++) {
                                            String content = imagesStrings.get(f);
                                            BeanDescription temp1 = new BeanDescription("dImage", content);
                                            tempList.add(temp1);
                                        }

                                        if (topicName.equalsIgnoreCase(GlobalData.GROSS_PHOTOGRAPH)) {
                                            isGrossAdded = true;
                                        }
                                    }

                                } else if (topicName.equalsIgnoreCase(GlobalData.MICRO_PHOTOGRAPH)) {

                                    if (isMicroAdded == false) {
                                        for (int f = 0; f < imagesStrings.size(); f++) {
                                            String content = imagesStrings.get(f);
                                            BeanDescription temp1 = new BeanDescription("dImage", content);
                                            tempList.add(temp1);
                                        }

                                        if (topicName.equalsIgnoreCase(GlobalData.MICRO_PHOTOGRAPH)) {
                                            isMicroAdded = true;
                                        }
                                    }

                                }

                            }

                        }

                    }

                    boolean isTextNull = dJsonObj.isNull("dtext");


                    has = dJsonObj.has("dtext");


                    if (!isTextNull) {

                        JSONObject jsondText = dJsonObj.getJSONObject("dtext");
                        Log.e(TAG, "dtext is : " + jsondText.toString());
                        String topId = jsondText.getString("topicId");

                        if (topId.equalsIgnoreCase(topicId)) {

                            Log.e(TAG, "2121 topicId is : " + topicId + "   topId " + topId);
                            String temp1 = jsondText.getString("textValue");
                            Log.e(TAG, "2121 textValue is  : " + temp1);
                            BeanDescription temp = new BeanDescription("dText", temp1);

                            tempList.add(temp);

                            Log.e(TAG, "2121 tempList size : " + tempList.size());

                            Log.e(TAG, "2121 BeanDescription is : " + temp);

                        }
                    }

                }

                Log.e(TAG, "21214 listDataHeader templist Head : " + listDataHeader.get(i));
//                Log.e(TAG, "21213 listDataHeader title : " + listDataHeader.get(i) + " desc is : " + tempList.toString());
                Log.e(TAG, "21214 listDataHeader templist at : " + i + "  is : " + tempList.toString());

                listDataChild.put(listDataHeader.get(i), tempList); // Header, Child data

                Log.e(TAG, "2121 listDataChild size is : " + listDataChild.toString());

            }



            /*Extra logic for Images*/


            /*Gross Photographs logic*/
            /*BeanTopics topics = new BeanTopics("211", "Photographs");
            topicsArrayList.add(topics);

            listDataHeader.add("Gross Photographs");

            ArrayList<BeanDescription> tempList1 = new ArrayList<>();


            /*Gross Photographs logic*/

            /*Changes done as below for view images in fastly manner and resolve  Exception error */
            /*
            ArrayList<BeanImageStrings> imageArrList = helper.getImagesString(subCatId);

            if(imageArrList.size()>0){

                for (int i = 0;i<imageArrList.size();i++){
                    BeanImageStrings objImage = imageArrList.get(i);
                    String content = objImage.getImgString();
                    BeanDescription temp1 = new BeanDescription("dImage", content);
                    tempList1.add(temp1);
                }

            }
            */
            /*By using above app throws Exception error as size of Images is more than 2MB */
            /*android.database.CursorIndexOutOfBoundsException: Index 0 requested, with a size of 0*/
            /*DB Error may solved by using this solved*/

            /*Gross Photographs logic*/
            /*ArrayList<String> imagesStrings = helper.getOnlyImagesString(subCatId);
            if(imagesStrings.size()>0){
                for (int i = 0;i<imagesStrings.size();i++){
                    String content = imagesStrings.get(i);
                    BeanDescription temp1 = new BeanDescription("dImage", content);
                    tempList1.add(temp1);
                }
            }
            listDataChild.put(listDataHeader.get(listDataHeader.size()-1), tempList1); // Header, Child data*/
            /*Gross Photographs logic*/

            /*Extra logic for Images*/


            if (pDialog.isShowing()) {
                hideProgressDialog();
            }


            listAdapter = new ExpandableListAdapterTwo(this, subCatId, listDataHeader, listDataChild);
            // setting list adapter
            expListView.setAdapter(listAdapter);


            /*

            ArrayList<BeanImageStrings> imageArrList = helper.getImagesString(subCatId);

            for (int i = 0;i<imageArrList.size();i++){

                BeanImageStrings objImage = imageArrList.get(i);

                if (objImage != null) {
                    if (i == 0) {
                        String content = objImage.getImgString();

                        byte[] decodedString = Base64.decode(content, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        imgDis1.setImageBitmap(decodedByte);
                        imgDis1.setVisibility(View.VISIBLE);
                        imgDis1.setOnTouchListener(new ImageMatrixTouchHandler(context));
                    }if (i == 1) {
                        String content = objImage.getImgString();

                        byte[] decodedString = Base64.decode(content, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        imgDis2.setImageBitmap(decodedByte);
                        imgDis2.setVisibility(View.VISIBLE);
                        imgDis2.setOnTouchListener(new ImageMatrixTouchHandler(context));
                    }if (i == 2) {
                        String content = objImage.getImgString();

                        byte[] decodedString = Base64.decode(content, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        imgDis3.setImageBitmap(decodedByte);
                        imgDis3.setVisibility(View.VISIBLE);
                        imgDis3.setOnTouchListener(new ImageMatrixTouchHandler(context));
                    }
                }

            }

            llImages.setVisibility(View.VISIBLE);

            */

            /*original logic ends*/


            /*

            ArrayList<String> imageArrList = helper.getImageUrls(subCatId);

            for (int i = 0;i<imageArrList.size();i++){

                String image = imageArrList.get(i);
                int index = image.lastIndexOf("\\");
                String fileName = image.substring(index + 1);

                String path1 = GlobalData.ATTACHMENT_PATH + fileName;



                if (image != null) {
//                    Original Used code ---------------------- ***
                    if(i==0){
                        Picasso.with(context)
                                .load(path1)
                                .placeholder(R.drawable.img_loading) //this is optional the image to display while the url image is downloading
                                .error(R.drawable.img_notfound)         //this is also optional if some error has occurred in downloading the image this image would be displayed
                                .resize(600, 400)
                                .into(imgDis1);

                        imgDis1.setVisibility(View.VISIBLE);
                    }
                    if(i==1){
                        Picasso.with(context)
                                .load(path1)
                                .placeholder(R.drawable.img_loading) //this is optional the image to display while the url image is downloading
                                .error(R.drawable.img_notfound)         //this is also optional if some error has occurred in downloading the image this image would be displayed
                                .resize(600, 400)
                                .into(imgDis2);

                        imgDis2.setVisibility(View.VISIBLE);
                    }
                    if(i==2){
                        Picasso.with(context)
                                .load(path1)
                                .placeholder(R.drawable.img_loading) //this is optional the image to display while the url image is downloading
                                .error(R.drawable.img_notfound)         //this is also optional if some error has occurred in downloading the image this image would be displayed
                                .resize(600, 400)
                                .into(imgDis3);

                        imgDis3.setVisibility(View.VISIBLE);
                    }

                }

            }

            */


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void showProgressDialog() {

        try {
            if (!pDialog.isShowing())
                pDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void hideProgressDialog() {

        try {
            if (pDialog.isShowing())
                pDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void toastMessage(String msg) {
        Toast.makeText(context, "" + msg, Toast.LENGTH_SHORT).show();
    }


    class DownloadImageData extends AsyncTask<String, String, String> {

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... params) {

            try {


//                error
                ArrayList<String> imageArrList = helper.getImageUrls(subCatId);

                for (int i = 0; i < imageArrList.size(); i++) {

                    String image = imageArrList.get(i);
                    int index = image.lastIndexOf("\\");
                    String fileName = image.substring(index + 1);

                    String imageurl = GlobalData.ATTACHMENT_PATH + fileName;

                    try {

                        InputStream in = null;


                        URL url = new URL(imageurl);

                        URLConnection urlConn = url.openConnection();

                        HttpURLConnection httpConn = (HttpURLConnection) urlConn;

                        httpConn.connect();

                        in = httpConn.getInputStream();

                        BufferedInputStream bis = new BufferedInputStream(in);

                        Bitmap bm = null;

                        bm = BitmapFactory.decodeStream(bis);

                        byte[] imageArray = getBytes(bm);


                        String encodedString = null;

                       /* int offset = 0;
                        int numRead = 0;
                        while (offset < imageArray.length
                                && (numRead = bis.read(imageArray, offset, imageArray.length - offset)) >= 0) {
                            offset += numRead;
                        }*/

                        byte[] encoded1 = Base64.encode(imageArray, Base64.DEFAULT);
                        encodedString = new String(encoded1);


                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            } catch (Exception e) {
                GlobalData.printError(e);
            }
            return null;
        }
    }

}

