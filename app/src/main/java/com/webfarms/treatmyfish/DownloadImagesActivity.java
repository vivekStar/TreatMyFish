package com.webfarms.treatmyfish;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.webfarms.treatmyfish.bean.BeanImageStrings;
import com.webfarms.treatmyfish.bean.BeanImageUrls;
import com.webfarms.treatmyfish.utils.CommonUtil;
import com.webfarms.treatmyfish.utils.DatabaseHelper;
import com.webfarms.treatmyfish.utils.GlobalData;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class DownloadImagesActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Context context;
    private RequestQueue requestQueue;
    private ProgressDialog pDialog;
    private DatabaseHelper helper;
    private int imgCounter = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_images);

        initialize();

        toolbar.setTitle(getResources().getString(R.string.app_name));

        new DownloadImageData().execute();

        CommonUtil.setSharePreferenceString(context, GlobalData.IMAGE_INITIALIZED, GlobalData.YES_INITIALIZED);

    }


    private void initialize() {

        context = DownloadImagesActivity.this;
        helper = new DatabaseHelper(context);
        requestQueue = Volley.newRequestQueue(context);
        toolbar = findViewById(R.id.toolbar);

        pDialog = new ProgressDialog(context);
        pDialog.setIndeterminate(true);
        pDialog.setTitle(getString(R.string.fetching_data));
        pDialog.setMessage(getString(R.string.please_wait));
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setCancelable(false);

    }

    class DownloadImageData extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!pDialog.isShowing()) {
                showProgressDialog();
            }

//            CommonUtil.setSharePreferenceString(context, GlobalData.IMG_DOWNLD_STATUS, GlobalData.DOWNLD_IN_COMPLETE);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... params) {

            try {


                ArrayList<BeanImageUrls> imageArrList = helper.getAllImageUrls();

                for (int i = 0; i < imageArrList.size(); i++) {

                    Log.e("TAG", "imageArrList size : " + imageArrList.size());


                    BeanImageUrls imageUrlObj = imageArrList.get(i);

                    String imgType = imageUrlObj.getImageType();
                    String subCatId = imageUrlObj.getSubCatId();
                    String imgPicType = imageUrlObj.getImgPicType();

                    Log.e("dldTAG", imgType);
                    Log.e("dldTAG", subCatId);
                    Log.e("dldTAG", imgPicType);


                    if (imgType.equalsIgnoreCase("new")) {

                        String image = imageUrlObj.getImageUrl();
                        int index = image.lastIndexOf("\\");
                        String fileName = image.substring(index + 1);

                        String imageurl = GlobalData.ATTACHMENT_PATH + fileName;

                        Log.e("TAG", "imageArrList imageurl : " + imageurl);

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

                            Log.e("TAG", "imageArrList size : " + imageArray.length);

                            Log.e("TAG", "imageArrList size : " + encodedString.length());

                            BeanImageStrings objImageStrings = new BeanImageStrings(imageUrlObj.getImageId(), imageUrlObj.getSubCatId(), imageUrlObj.getImgPicType(), encodedString);

                            boolean isAdded = helper.addImageStringToDB(objImageStrings);


                            if (isAdded) {
                                imgCounter++;
//                                toastMessage(" imgae added to DB " + imgCounter);
                            }


                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else {

                    }
                }

            } catch (Exception e) {
                GlobalData.printError(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (pDialog.isShowing()) {
                hideProgressDialog();
            }

//            CommonUtil.setSharePreferenceString(context, GlobalData.IMG_DOWNLD_STATUS, GlobalData.DOWNLD_COMPLETE);

//            helper.printImageUrls();

            Intent intent = new Intent(DownloadImagesActivity.this, FishDash.class);
            startActivity(intent);
            finish();
        }
    }

    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
        return stream.toByteArray();
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
