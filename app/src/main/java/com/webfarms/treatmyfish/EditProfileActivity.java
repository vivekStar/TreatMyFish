package com.webfarms.treatmyfish;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.webfarms.treatmyfish.app.AppController;
import com.webfarms.treatmyfish.utils.CommonUtil;
import com.webfarms.treatmyfish.utils.GlobalData;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by Ashish Zade on 2/20/2017 & 2:22 PM.
 */

public class EditProfileActivity extends AppCompatActivity {

    private Context context;
    private Toolbar toolbar;
    private EditText input_customer_name, input_email, input_contact_no;
    private Button btUpdate;
    private String userId;

    private ProgressDialog pDialog;
    private String tag_string_req = "string_req";
    private String TAG = EditProfileActivity.class.getSimpleName();
    private ImageView profile_image;
    private LinearLayout ll_btm_home, ll_btm_profile, ll_btm_history, ll_btm_logout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activty_edit_profilr);

        context = EditProfileActivity.this;
        toolbar = GlobalData.initToolBar(this, getString(R.string.profile), true);

        pDialog = new ProgressDialog(context);
        pDialog.setIndeterminate(true);
        pDialog.setMessage(getString(R.string.raise_issue));
        pDialog.setCancelable(false);

        initActivity();

    }

    private void initActivity() {

        profile_image = findViewById(R.id.profile_image);

        input_customer_name = (EditText) findViewById(R.id.input_customer_name);
        input_contact_no = (EditText) findViewById(R.id.input_contact_no);
        input_email = (EditText) findViewById(R.id.input_email);
        btUpdate = (Button) findViewById(R.id.btUpdate);

        setData();

        btUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                String loginType = CommonUtil.getSharePreferenceString(context, GlobalData.LOGIN_USER_TYPE, "0");
//                if (loginType.equalsIgnoreCase(GlobalData.USING_GOOGLE)) {
//                    return;
//                }

                updateUserProfile();
            }
        });


        // Bottom filter menu
        ll_btm_home = (LinearLayout) findViewById(R.id.ll_btm_home);
        ll_btm_profile = (LinearLayout) findViewById(R.id.ll_btm_profile);
        ll_btm_history = (LinearLayout) findViewById(R.id.ll_btm_history);
        ll_btm_logout = (LinearLayout) findViewById(R.id.ll_btm_logout);

        // Bottom filter On Click

        ll_btm_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, ProfileActivity.class));
                finish();
            }
        });

        ll_btm_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, HistoryActivity.class));
                finish();
            }
        });

        ll_btm_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

        ll_btm_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, FishDash.class));
                finish();
            }
        });


    }

    private void updateUserProfile() {

        updateProfileRequest();
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

    private void updateProfileRequest() {

        showProgressDialog();

        userId = CommonUtil.getSharePreferenceString(context, GlobalData.LOGIN_USER_ID, "0");

        String url = GlobalData.FILE_URL + GlobalData.SERVICE_UPDATE_PROFILE;

        final String customerName = input_customer_name.getText().toString().trim();
        final String emailId = input_email.getText().toString().trim();
        final String contactNo = input_contact_no.getText().toString().trim();
        JSONObject js = new JSONObject();
        try {

            js.put("customerId", Integer.parseInt(userId));
            js.put("customerName", customerName);
            js.put("emailId", emailId);
            js.put("contactNo", "" + contactNo);

        } catch (Exception e) {
            e.printStackTrace();
        }


     /*   {
            "profile": {
            "customerName": "abc",
                    "contactNo": "123456",
                    "address1": "china",
                    "address2": "beging",
                    "password": "1111",
                    "customerId": 6
        }
        }*/

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, js,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {


                        try {
                            String status = "" + response.getInt("responseCode");

                            if (status.equalsIgnoreCase("500")) {
                                //    Toast.makeText(context,R.string.err_login, Toast.LENGTH_LONG).show();
                            }

                            String responseMessage = response.getString("responseMessage");

                            //     Toast.makeText(context, "" + responseMessage, Toast.LENGTH_LONG).show();

                            if (status.equalsIgnoreCase("200")) {

                                CommonUtil.setSharePreferenceString(context, GlobalData.LOGIN_USER_NAME, "" + customerName);
                                CommonUtil.setSharePreferenceString(context, GlobalData.LOGIN_USER_MOBILE_NUMBER, "" + contactNo);
                                // ll_feedback_form.setVisibility(View.GONE);
                                //  ll_thank_submit.setVisibility(View.VISIBLE);
                                Toast.makeText(context, "" + responseMessage, Toast.LENGTH_LONG).show();
                                finish();
                            } else {
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
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_string_req);
    }

    private void setData() {

        String loginType = CommonUtil.getSharePreferenceString(context, GlobalData.LOGIN_USER_TYPE, "0");


        if (loginType.equalsIgnoreCase(GlobalData.USING_GOOGLE)) {

            String userName = CommonUtil.getSharePreferenceString(context, GlobalData.LOGIN_USER_NAME, "0");
            String userEmail = CommonUtil.getSharePreferenceString(context, GlobalData.LOGIN_USER_EMAIL_ID, "0");
            String userContact = CommonUtil.getSharePreferenceString(context, GlobalData.LOGIN_USER_MOBILE_NUMBER, "0");
            String personPhotoUrl = CommonUtil.getSharePreferenceString(context, GlobalData.LOGIN_USER_PHOTOURL, "N/A");

            input_customer_name.setText(userName);
            input_email.setText(userEmail);
            input_customer_name.setEnabled(false);
            input_email.setEnabled(false);
            input_contact_no.setText(userContact);

            Glide.with(getApplicationContext()).load(personPhotoUrl)
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(profile_image);

        } else {

            String userName = CommonUtil.getSharePreferenceString(context, GlobalData.LOGIN_USER_NAME, "0");
            String userEmail = CommonUtil.getSharePreferenceString(context, GlobalData.LOGIN_USER_EMAIL_ID, "0");
            String userContact = CommonUtil.getSharePreferenceString(context, GlobalData.LOGIN_USER_MOBILE_NUMBER, "0");
            String personPhotoUrl = CommonUtil.getSharePreferenceString(context, GlobalData.LOGIN_USER_PHOTOURL, "N/A");

            input_customer_name.setText(userName);
            input_email.setText(userEmail);
            input_email.setEnabled(false);
            input_contact_no.setText(userContact);


        }
    }
}
