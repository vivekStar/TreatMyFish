package com.webfarms.treatmyfish;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by WEBFARMSPC2 on 3/24/2018.
 */

public class VerifyOtpActivity extends AppCompatActivity {

    private Button btnVerifyOtp;
    private EditText etOTPNo;
    private RequestQueue requestQueue;
    private TextView tv_timer;
    private ProgressDialog mProgressDialog;

    private Context context;
    private Toolbar toolbar;

    private String contactNo, customerId, flag;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lay_verify_otp);

        context = VerifyOtpActivity.this;
        toolbar = GlobalData.initToolBar(this, getString(R.string.verifyotp), true);

        initialize();

        Intent intent = getIntent();

        contactNo = intent.getStringExtra("contactNo");
        customerId = intent.getStringExtra("customerId");
        flag = intent.getStringExtra("flag");

    }

    private void initialize() {

        context = VerifyOtpActivity.this;

        requestQueue = Volley.newRequestQueue(context);

        btnVerifyOtp = (Button) findViewById(R.id.btnVerifyOtp);

        etOTPNo = (EditText) findViewById(R.id.etOTPNo);

        tv_timer = findViewById(R.id.tv_timer);

        /*Start To show count down from 30 to 00*/

        new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {

                long millis = millisUntilFinished / 1000;
                if (millis < 10) {
                    tv_timer.setText("00 : 0" + millisUntilFinished / 1000);
                } else {
                    tv_timer.setText("00 : " + millisUntilFinished / 1000);
                }
            }

            public void onFinish() {
                tv_timer.setText("Time End");
            }
        }.start();

        /*End To show count down from 30 to 00*/


        btnVerifyOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (contactNo != null) {

//                    ViewDialog alert = new ViewDialog();
//                    alert.showDialog(context);

                    verifyOTPRequest();

                }

            }
        });

    }


    private void verifyOTPRequest() {

        showProgressDialog();

        final String urlVerifyOtp = GlobalData.FILE_URL + GlobalData.SERVICE_VERIFY_OTP;

        String otp = etOTPNo.getText().toString();

        JSONObject json = new JSONObject();

        try {

            json.put("otp", "" + otp);
            json.put("contactNo", "" + contactNo);

        } catch (JSONException e) {
            e.printStackTrace();
        }



        JsonObjectRequest verifyOTPReq = new JsonObjectRequest(Request.Method.POST, urlVerifyOtp, json, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {


                if (mProgressDialog.isShowing()) {
                    hideProgressDialog();
                }

                try {

                    String responseCode = response.getString("responseCode");

                    if (responseCode.equalsIgnoreCase("200")) {

                        if (flag.equalsIgnoreCase(GlobalData.FLAG_NEW)) {

                            CommonUtil.setSharePreferenceString(VerifyOtpActivity.this, GlobalData.LOGIN_USER_TYPE, GlobalData.USING_MOBILE_NO);

                            Intent intent = new Intent(VerifyOtpActivity.this, SignUpActivity.class);
                            intent.putExtra("contactNo", contactNo);
                            intent.putExtra("customerId", customerId);
                            intent.putExtra("flag", flag);
                            startActivity(intent);
                            finish();

                        } else {

                            CommonUtil.setSharePreferenceString(VerifyOtpActivity.this, GlobalData.LOGIN_USER_TYPE, GlobalData.USING_MOBILE_NO);
                            CommonUtil.setSharePreferenceString(VerifyOtpActivity.this, GlobalData.LOGIN_USER_ID, customerId);
                            CommonUtil.setSharePreferenceString(VerifyOtpActivity.this, GlobalData.LOGIN_USER_MOBILE_NUMBER, contactNo);

                            //for miantaining the log in session
                            CommonUtil.setSharePreferenceString(VerifyOtpActivity.this, GlobalData.APP_LOGIN_STATUS, GlobalData.LOGED_IN);

                            Intent intent = new Intent(VerifyOtpActivity.this, InitialSetupActivity.class);
                            startActivity(intent);
                            finish();
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (mProgressDialog.isShowing()) {
                    hideProgressDialog();
                }
//                Toast.makeText(context, "Server Error...", Toast.LENGTH_LONG).show();

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


        verifyOTPReq.setRetryPolicy(new DefaultRetryPolicy(
                GlobalData.REQUEST_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(verifyOTPReq);

    }


    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.please_wait));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }


    public class ViewDialog {

        public void showDialog(Context context) {

            final Dialog dialog = new Dialog(context);

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            dialog.setCancelable(false);

            dialog.setContentView(R.layout.lay_otp_custom_dialog);

            TextView tvDiaMobNo = (TextView) dialog.findViewById(R.id.tvDiaMobNo);
            tvDiaMobNo.setText(contactNo);

            Button btnEditMob = (Button) dialog.findViewById(R.id.btnEditMob);
            Button btnSubmitMob = (Button) dialog.findViewById(R.id.btnSubmitMob);


            btnSubmitMob.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    verifyOTPRequest();
                    dialog.dismiss();
                }
            });

            btnEditMob.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();

        }
    }
}
