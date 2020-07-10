package com.webfarms.treatmyfish;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.webfarms.treatmyfish.utils.CommonUtil;
import com.webfarms.treatmyfish.utils.GlobalData;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AboutUsActivity extends AppCompatActivity {

    private Context context;
//    private Toolbar toolbar;
    private ProgressDialog pDialog;
    private RequestQueue requestQueue;
//    private CollapsingToolbarLayout collapsingToolbar;
    private LinearLayout ll_btm_home, ll_btm_profile, ll_btm_history, ll_btm_logout, ll_btm_aboutus;

    private TextView tvAboutUs;

    private String TAG = AboutUsActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        initialize();

        makeAboutUsRequest();

    }

    private void initialize() {

        context = AboutUsActivity.this;
        tvAboutUs = findViewById(R.id.tvAboutUs);
//        collapsingToolbar = findViewById(R.id.collapsing_toolbar);

        pDialog = new ProgressDialog(context);
        pDialog.setIndeterminate(true);
        pDialog.setMessage(getString(R.string.raise_issue));
        pDialog.setCancelable(false);

        requestQueue = Volley.newRequestQueue(context);

        // Bottom filter menu
        ll_btm_home = findViewById(R.id.ll_btm_home);
        ll_btm_profile = findViewById(R.id.ll_btm_profile);
        ll_btm_history = findViewById(R.id.ll_btm_history);
        ll_btm_logout = findViewById(R.id.ll_btm_logout);
        ll_btm_aboutus = findViewById(R.id.ll_btm_aboutus);

        // Bottom filter On Click


        ll_btm_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, FishDash.class));
            }
        });

        ll_btm_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, ProfileActivity.class));
            }
        });

        ll_btm_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, HistoryActivity.class));
            }
        });

        ll_btm_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, FishDash.class));
            }
        });

        ll_btm_aboutus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, AboutUsActivity.class));
            }
        });


    }


    private void showProgressDialog() {

        try {
            if (!pDialog.isShowing())
                pDialog.show();
        } catch (Exception e) {
            GlobalData.printError(e);
        }

    }

    private void hideProgressDialog() {

        try {
            if (pDialog.isShowing())
                pDialog.dismiss();
        } catch (Exception e) {
            GlobalData.printError(e);
        }

    }

    private void makeAboutUsRequest() {

        showProgressDialog();

        final String url = GlobalData.FILE_URL + GlobalData.SERVICE_GET_ABOUT_US;

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        if (pDialog.isShowing()){
                            hideProgressDialog();
                        }

                        try {
                            String status = "" + response.getInt("responseCode");

                            if (status.equalsIgnoreCase("500")) {
                                //    Toast.makeText(context,R.string.err_login, Toast.LENGTH_LONG).show();
                            }

                            String responseMessage = response.getString("responseMessage");


                            if (status.equalsIgnoreCase("200")) {

                                JSONObject aboutUsJson = response.getJSONObject("aboutUs");

                                String language = CommonUtil.getSharePreferenceString(context, GlobalData.LANGUAGE, "0");

                                if (language.equalsIgnoreCase(GlobalData.HINDI_LANGUAGE)) {
                                    String descHindi = aboutUsJson.getString("hindi");
                                    tvAboutUs.setText(descHindi);
                                }

                                if (language.equalsIgnoreCase(GlobalData.ENGLISH_LANGUAGE)) {
                                    String descEnglish = aboutUsJson.getString("english");
                                    tvAboutUs.setText(descEnglish);
                                }

                            }else {
                                GlobalData.showNoResponseDialog(context);
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        hideProgressDialog();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //  Toast.makeText(context, "No internet connection", Toast.LENGTH_LONG).show();
                GlobalData.showSeverErrorDialog(context);
//                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hideProgressDialog();
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }

        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                GlobalData.REQUEST_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        requestQueue.add(jsonObjReq);
    }


}
