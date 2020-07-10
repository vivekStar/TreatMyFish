package com.webfarms.treatmyfish;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.webfarms.treatmyfish.adapter.DiseaseListAdapter;
import com.webfarms.treatmyfish.bean.BeamSubCategory;
import com.webfarms.treatmyfish.utils.CommonUtil;
import com.webfarms.treatmyfish.utils.GlobalData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FishDiseaseListActivity extends AppCompatActivity {


    private static final String TAG = "FishDiseaseListActivity";

    private RequestQueue requestQueue;
    private ProgressDialog pDialog;

    private Context context;
    private Toolbar toolbar;

    private LinearLayout lyt_progress,lyt_response;
    private String categoryId;

    private RecyclerView lv_diseases;

    private ArrayList<String> disasesList;

    private ArrayList<BeamSubCategory> subCategoriesList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.lay_fish_disease);

        initialize();

        String category = getIntent().getStringExtra("disease");

        categoryId = getIntent().getStringExtra("categoryId");

        toolbar = GlobalData.initToolBar(this, category, true);

        subCategoriesList = new ArrayList<>();

        getSubCategoryDetails();

    }

    private void initialize() {

        context = FishDiseaseListActivity.this;

        lyt_response = findViewById(R.id.lyt_response);
        lyt_progress = findViewById(R.id.lyt_progress);

        lv_diseases = findViewById(R.id.lv_diseases);
        lv_diseases.setLayoutManager(new LinearLayoutManager(this));
        lv_diseases.setHasFixedSize(true);

        requestQueue = Volley.newRequestQueue(context);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage(getString(R.string.please_wait));
        pDialog.setIndeterminate(true);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setCancelable(false);

    }


    public void getSubCategoryDetails() {

        String language = CommonUtil.getSharePreferenceString(context, GlobalData.LANGUAGE, GlobalData.ENGLISH_LANGUAGE);

        JSONObject json = new JSONObject();

        try {

            json.put("categoryId", categoryId);
            json.put("language", language);

        } catch (Exception e) {
            e.printStackTrace();
        }


//        public static final String SERVICE_GET_ALL_SUB_CATEGORY = "content/getAllSubCategory";
        final String urlGetAllSubCategory = GlobalData.FILE_URL + GlobalData.SERVICE_GET_ALL_SUB_CATEGORY;

        Log.e(TAG, "getAllSubCategory : "+json.toString());


//        showProgressDialog();

        JsonObjectRequest jsonContactUploadReq = new JsonObjectRequest(Request.Method.POST, urlGetAllSubCategory, json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        lyt_progress.setVisibility(View.GONE);
                        lyt_response.setVisibility(View.VISIBLE);

//                        if (pDialog.isShowing()) {
//                            hideProgressDialog();
//                        }

                        Log.e(TAG, "getAllSubCategory res : "+response.toString());

                        try {
                            String responseCode = response.getString("responseCode");

                            if (responseCode.equalsIgnoreCase("200")) {

                                disasesList = new ArrayList<>();

                                try {

                                    JSONArray subCategory = response.getJSONArray("subCategory");

                                    for (int i = 0; i < subCategory.length(); i++) {
                                        JSONObject json = subCategory.getJSONObject(i);
                                        String subName = json.getString("subName");
                                        String subCategoryId = json.getString("subCategoryId");

                                        BeamSubCategory subCat = new BeamSubCategory(subCategoryId, subName);
                                        subCategoriesList.add(subCat);

                                        disasesList.add(subName);
                                    }

                                    setListView();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                            if (responseCode.equalsIgnoreCase("402")) {

//                                Toast.makeText(context, "Server Error..!", Toast.LENGTH_SHORT).show();
                                GlobalData.showNoResponseDialog(context);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//                        if (pDialog.isShowing()) {
//                            hideProgressDialog();
//                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
                error.printStackTrace();
                GlobalData.showSeverErrorDialog(context);
//                Toast.makeText(context, "Server Error...", Toast.LENGTH_LONG).show();
//                if (pDialog.isShowing()) {
//                    hideProgressDialog();
//                }
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

    private void setListView() {

        DiseaseListAdapter adapter1 = new DiseaseListAdapter(context,subCategoriesList);
        lv_diseases.setAdapter(adapter1);

        // on item list clicked
        adapter1.setOnItemClickListener(new DiseaseListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, BeamSubCategory obj, int position) {

                String subCatId = subCategoriesList.get(position).getSubCategoryId();

//                Intent intent = new Intent(FishDiseaseListActivity.this, FishDiseaseDetailsActivityFour.class);
                Intent intent = new Intent(FishDiseaseListActivity.this, FishDiseaseDetailsActivity.class);
                intent.putExtra("subName", subCategoriesList.get(position).getSubName());
                intent.putExtra("categoryId", categoryId);
                intent.putExtra("subCatId", subCatId);
                startActivity(intent);
            }
        });


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
}
