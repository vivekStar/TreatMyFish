package com.webfarms.treatmyfish;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
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
import com.webfarms.treatmyfish.animation.ViewAnimation;
import com.webfarms.treatmyfish.bean.BeanImageUrls;
import com.webfarms.treatmyfish.utils.CommonUtil;
import com.webfarms.treatmyfish.utils.DatabaseHelper;
import com.webfarms.treatmyfish.utils.GlobalData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class InitialSetupActivity extends AppCompatActivity {

    private View parent_view;
    private final static int LOADING_DURATION = 2000;

    private Toolbar toolbar;
    private Context context;
    private RequestQueue requestQueue;
    private ProgressDialog pDialog;


    private DatabaseHelper helper;
    private String initialCounter = null, newCounter = null;
    int imgCounter = 0;
    private LinearLayout lyt_progress;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_setup);

        initialize();

        ArrayList<BeanImageUrls> imageUrlsArrayList = helper.getAllImageUrls();

        if (imageUrlsArrayList != null) {


            String req = "";

            for (int i = 0; i < imageUrlsArrayList.size(); i++) {
                String imgId = imageUrlsArrayList.get(i).getImageId();
                String subCatId = imageUrlsArrayList.get(i).getSubCatId();

                req.concat(imgId);
            }

        }

        getImageCounter();

    }

    private void initialize() {

        context = InitialSetupActivity.this;
        helper = new DatabaseHelper(context);
        requestQueue = Volley.newRequestQueue(context);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.app_name));

        pDialog = new ProgressDialog(context);
        pDialog.setIndeterminate(true);
        pDialog.setTitle(getString(R.string.initial_setup));
        pDialog.setMessage(getString(R.string.please_wait));
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setCancelable(false);

        initialCounter = helper.getImageCounter();

        Log.e("initResp","counter : "+initialCounter);

        lyt_progress = (LinearLayout) findViewById(R.id.lyt_progress);
        lyt_progress.setVisibility(View.VISIBLE);

    }


    private void getImageCounter() {

        ViewAnimation.fadeOut(lyt_progress);

//        public static final String SERVICE_VERIFY_SOCIAL = "customer/verifySocial";
        final String urlGetImgCounter = GlobalData.FILE_URL + GlobalData.SERVICE_GET_IMG_COUNTER;

        JsonObjectRequest imgCounterReq = new JsonObjectRequest(Request.Method.GET, urlGetImgCounter, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.e("initResp",response.toString());

                        try {

                            String responseCode = response.getString("responseCode");

                            String status = "" + response.getInt("responseCode");
                            String responseMessage = response.getString("responseMessage");

                            if (status.equalsIgnoreCase("500")) {
                                Toast.makeText(context, responseMessage, Toast.LENGTH_LONG).show();
                            }

                            if (responseCode.equalsIgnoreCase("200")) {

                                JSONObject jsonImage = response.getJSONObject("image");

                                newCounter = jsonImage.getString("counter");
                                String date = jsonImage.getString("date");


                                if (!initialCounter.equalsIgnoreCase(newCounter)) {

                                    String imgInitialized = CommonUtil.getSharePreferenceString(context, GlobalData.IMAGE_INITIALIZED, "0");

                                    if (imgInitialized.equalsIgnoreCase(GlobalData.YES_INITIALIZED)) {

                                        getUpdatdImageUrls();

                                    } else {

                                        getAllImages();

                                    }

                                } else {


                                    ArrayList<BeanImageUrls> imageArrList = helper.getAllImageUrls();
                                    boolean isDownloadSts = false;

                                    Log.e("initResp","getAllImagesUrls size : "+imageArrList.size());

                                    for (int i = 0; i < imageArrList.size(); i++) {

                                        BeanImageUrls imageUrlObj = imageArrList.get(i);
                                        String imgDloadSts = imageUrlObj.getImgDloadSts();

                                        if (imgDloadSts.equalsIgnoreCase("NO")) {
                                            isDownloadSts = true;
                                            break;
                                        }
                                    }

                                    if (isDownloadSts) {
                                        Intent intent = new Intent(InitialSetupActivity.this, CheckDownloadImagesActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Intent intent = new Intent(InitialSetupActivity.this, FishDash.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            }
                            if (responseCode.equalsIgnoreCase("402")) {

//                                Toast.makeText(context, "Server Error.", Toast.LENGTH_SHORT).show();

                                if (pDialog.isShowing()) {
                                    hideProgressDialog();
                                }

                                GlobalData.showNoResponseDialog(context);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (pDialog.isShowing()) {
                    hideProgressDialog();
                }
//                Toast.makeText(context, "S_Error Img Counter...", Toast.LENGTH_LONG).show();
                GlobalData.showSeverErrorDialog(context);

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

        imgCounterReq.setRetryPolicy(new DefaultRetryPolicy(
                GlobalData.REQUEST_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(imgCounterReq);

    }

    public void getAllImages() {

        ViewAnimation.fadeOut(lyt_progress);

//        public static final String SERVICE_GET_ALL_IMAGES = "content/getImages";
        final String urlGetAllImages = GlobalData.FILE_URL + GlobalData.SERVICE_GET_ALL_IMAGES;


        JsonObjectRequest jsonContactUploadReq = new JsonObjectRequest(Request.Method.GET, urlGetAllImages, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            String responseCode = response.getString("responseCode");


                            if (responseCode.equalsIgnoreCase("200")) {

//                                Toast.makeText(context, "Image Counter Changed..", Toast.LENGTH_SHORT).show();

                                boolean isUpdated = helper.updateImageCounter(newCounter);

                                if (isUpdated) {
//                                    Toast.makeText(context, "Image Counter Updated..", Toast.LENGTH_SHORT).show();
                                }

                                JSONArray jsonArrImage = response.getJSONArray("dImage");


                                for (int i = 0; i < jsonArrImage.length(); i++) {

                                    JSONObject jsonImg = jsonArrImage.getJSONObject(i);
                                    String subCatTopicId = jsonImg.getString("subCatTopicId");
                                    String subName = jsonImg.getString("subName");
                                    String topicName = jsonImg.getString("topicName");
                                    String categoryName = jsonImg.getString("categoryName");
                                    String imageUrl = jsonImg.getString("image");
                                    String fileName = jsonImg.getString("fileName");
                                    String imageId = jsonImg.getString("imageId");
                                    String subCategoryId = jsonImg.getString("subCategoryId");
                                    String imagePicType = jsonImg.getString("imageType");


                                    BeanImageUrls img = new BeanImageUrls(imageId, subCategoryId, fileName, imageUrl, "0", imagePicType, "NO");

                                    boolean isAdded = helper.addImageUrlToDB(img);

                                    if (isAdded) {
                                        imgCounter++;
//                                        toastMessage("imgae count " + imgCounter);
                                    }
                                }

                                CommonUtil.setSharePreferenceString(context, GlobalData.IMAGE_INITIALIZED, GlobalData.YES_INITIALIZED);

                                /*String imgDldSts = CommonUtil.getSharePreferenceString(context, GlobalData.IMG_DOWNLD_STATUS, "0");
                                if (imgDldSts.equalsIgnoreCase(GlobalData.DOWNLD_IN_COMPLETE)) {
                                    helper.deleteImagesData();
                                }
*/
                                Intent intent = new Intent(InitialSetupActivity.this, DownloadImagesActivity.class);
                                startActivity(intent);
                                finish();

                            }
                            if (responseCode.equalsIgnoreCase("402")) {
                                if (pDialog.isShowing()) {
                                    hideProgressDialog();
                                }
//                                Toast.makeText(context, "Not Valid Response..!", Toast.LENGTH_SHORT).show();
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
//                Toast.makeText(context, "S_Error getAll Imgs...", Toast.LENGTH_LONG).show();
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

    public void getUpdatdImageUrls() {

        ViewAnimation.fadeOut(lyt_progress);

//        public static final String SERVICE_GET_ALL_IMAGES = "content/getImages";
        final String urlGetAllImagesStatus = GlobalData.FILE_URL + GlobalData.SERVICE_GET_ALL_IMAGES_STATUS;


        ArrayList<BeanImageUrls> imageUrlsArrayList = helper.getAllImageUrls();


        String imagesIds = "";

        JSONArray jsonArray = new JSONArray();

        for (int i = 0; i < imageUrlsArrayList.size(); i++) {

            String imgId = imageUrlsArrayList.get(i).getImageId();

            jsonArray.put(Integer.parseInt(imgId));

            if (i == imageUrlsArrayList.size() - 1) {
                imagesIds = imagesIds + imgId;
            } else {
                imagesIds = imagesIds + imgId + ",";
            }

        }

        JSONObject json = new JSONObject();

        try {
            json.put("imageID", jsonArray);

        } catch (Exception e) {
            e.printStackTrace();
        }


        JsonObjectRequest jsonContactUploadReq = new JsonObjectRequest(Request.Method.POST, urlGetAllImagesStatus, json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            String responseCode = response.getString("responseCode");


                            if (responseCode.equalsIgnoreCase("200")) {

//                                Toast.makeText(context, "Image Counter Changed..", Toast.LENGTH_SHORT).show();

                                boolean isUpdated = helper.updateImageCounter(newCounter);

                                if (isUpdated) {
//                                    Toast.makeText(context, "Image Counter Updated..", Toast.LENGTH_SHORT).show();
                                }


                                JSONObject jsonImage = response.getJSONObject("image");


                                boolean isNullNewInsert = jsonImage.isNull("insertImage");


                                if (!isNullNewInsert) {


                                    JSONObject jsonNewInsert = jsonImage.getJSONObject("insertImage");


                                    JSONArray jsonArrImage = jsonNewInsert.getJSONArray("imageList");


                                    for (int i = 0; i < jsonArrImage.length(); i++) {

                                        JSONObject jsonImg = jsonArrImage.getJSONObject(i);
                                        String subCatTopicId = jsonImg.getString("subCatTopicId");
                                        String subName = jsonImg.getString("subName");
                                        String topicName = jsonImg.getString("topicName");
                                        String categoryName = jsonImg.getString("categoryName");
                                        String imageUrl = jsonImg.getString("image");
                                        String fileName = jsonImg.getString("fileName");
                                        String imageId = jsonImg.getString("imageId");
                                        String subCategoryId = jsonImg.getString("subCategoryId");
                                        String imagePicType = jsonImg.getString("imageType");


                                        BeanImageUrls img = new BeanImageUrls(imageId, subCategoryId, fileName, imageUrl, "0", imagePicType, "NO");
                                        boolean isAdded = helper.addImageUrlToDB(img);


                                        if (isAdded) {
                                            imgCounter++;
//                                            toastMessage("imgae count " + imgCounter);
                                        }
                                    }


                                }


                                boolean isNullDelImgList = jsonImage.isNull("deleteImageList");


                                if (!isNullDelImgList) {


                                    JSONArray jsonArrDelImage = jsonImage.getJSONArray("deleteImageList");


                                    for (int j = 0; j < jsonArrDelImage.length(); j++) {

                                        JSONObject jsonDelImg = jsonArrDelImage.getJSONObject(j);
                                        String imageID = jsonDelImg.getString("imageID");

                                        boolean isDeleted = helper.deleteSelImages(imageID);
                                        if (isDeleted) {
//                                            toastMessage("imgae delete count " + j);

                                        } else {
//                                            toastMessage("imgae delete error " + j);

                                        }
                                    }


                                }


                                Intent intent = new Intent(InitialSetupActivity.this, DownloadImagesActivity.class);
                                startActivity(intent);
                                finish();

                            }
                            if (responseCode.equalsIgnoreCase("402")) {
                                if (pDialog.isShowing()) {
                                    hideProgressDialog();
                                }
//                                Toast.makeText(context, "Not Valid Response..!", Toast.LENGTH_SHORT).show();
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

//                Toast.makeText(context, "S_Error getUpdtImgs...", Toast.LENGTH_LONG).show();
                if (pDialog.isShowing()) {
                    hideProgressDialog();
                }

                GlobalData.showSeverErrorDialog(context);
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


    public void toastMessage(String msg) {
        Toast.makeText(context, "" + msg, Toast.LENGTH_SHORT).show();
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

}
