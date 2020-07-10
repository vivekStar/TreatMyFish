package com.webfarms.treatmyfish;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.webfarms.treatmyfish.app.Config;
import com.webfarms.treatmyfish.utils.CommonUtil;
import com.webfarms.treatmyfish.utils.GlobalData;
import com.webfarms.treatmyfish.utils.Validator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by WEBFARMSPC2 on 3/29/2018.
 */

/*
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 007;
    private static final int FB_SIGN_IN = 021;

    private Context context;
    private Toolbar toolbar;
    private String userID;

    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;
    private RequestQueue requestQueue;

    private LinearLayout ll_login, ll_otp;
    private EditText et_username, et_password, et_user_mobno;
    private TextView tv_forgot_pd, tv_signup;
    private Button btnLogin, btnObtainOtp, btnFBSignIn;
    private Button btnGoogleSignIn;

    private boolean isNetworkAvail, loginCancel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        isNetworkAvail = isNetworkAvailable();

        showSnack(isNetworkAvail);

        initialize();

        context = LoginActivity.this;
        String login = getResources().getString(R.string.login);
        toolbar = GlobalData.initToolBar(this, login, true);

        String loginStatus = CommonUtil.getSharePreferenceString(LoginActivity.this, GlobalData.APP_LOGIN_STATUS, "0");


        if (loginStatus.equalsIgnoreCase(GlobalData.LOGED_IN)) {

            Intent intent = new Intent(LoginActivity.this, InitialSetupActivity.class);
            startActivity(intent);
            finish();

        }
    }

    private void initialize() {

        context = LoginActivity.this;

        requestQueue = Volley.newRequestQueue(context);

        ll_login = findViewById(R.id.ll_login);
        ll_otp = findViewById(R.id.ll_otp);

        et_username = (EditText) findViewById(R.id.et_username);
        et_password = (EditText) findViewById(R.id.et_password);

        et_user_mobno = (EditText) findViewById(R.id.et_user_mobno);

        tv_forgot_pd = (TextView) findViewById(R.id.tv_forgot_pd);

        btnLogin = (Button) findViewById(R.id.btnLogin);

        btnObtainOtp = (Button) findViewById(R.id.btnObtainOtp);

        btnFBSignIn = (Button) findViewById(R.id.btnFacebookSignIn);

        /*Google Login Initialization*/

        btnGoogleSignIn = (Button) findViewById(R.id.btn_google_signin);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Customizing G+ button
//        btnGoogleSignIn.setSize(SignInButton.SIZE_STANDARD);
//        btnGoogleSignIn.setScopes(gso.getScopeArray());

        /*Google Login Intialization */


        btnLogin.setOnClickListener(this);
        btnObtainOtp.setOnClickListener(this);
        btnFBSignIn.setOnClickListener(this);
        btnGoogleSignIn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.btn_google_signin:
                isNetworkAvail = isNetworkAvailable();
                Log.e(TAG, "onClick: "+isNetworkAvail );
                if (isNetworkAvail) {
                    Log.e(TAG, "isNetworkAvail : "+isNetworkAvail );
                    googleSignIn();
                } else {
                    showSnack(isNetworkAvail);
                    Toast.makeText(context, "Connect to Internet", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.btnObtainOtp:

                isNetworkAvail = isNetworkAvailable();
                if (isNetworkAvail) {
                    String mob = et_user_mobno.getText().toString();
                    int valid = Validator.validatePhoneNumber(context, mob);
                    if (valid != 1) {
                        et_user_mobno.setError("");
                        return;
                    }
                    CustomDialogClass cdd = new CustomDialogClass(LoginActivity.this);
                    cdd.show();
                } else {
                    showSnack(isNetworkAvail);
                    Toast.makeText(context, "Connect to Internet", Toast.LENGTH_SHORT).show();
                    return;
                }

                break;

            case R.id.btnLogin:
                isNetworkAvail = isNetworkAvailable();
                String username = et_username.getText().toString();
                String password = et_password.getText().toString();
                if (isNetworkAvail) {
                    signInUsingUsernamePassword();
                } else {
                    showSnack(isNetworkAvail);
                    Toast.makeText(context, "Connect to Internet", Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }

    private void googleSignIn() {
        Log.e(TAG, "googleSignIn: " );
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Log.e(TAG, "onActivityResult: requestCode == RC_SIGN_IN" );
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleGoogleSignInResult(result);
        } else {

//            callbackManager.onActivityResult(requestCode, resultCode, data);

        }
    }

    private void handleGoogleSignInResult(GoogleSignInResult result) {

        if (result.isSuccess()) {

            Log.e(TAG, "handleGoogleSignInResult: success" );

            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            String personName = acct.getDisplayName();
            String emailID = acct.getEmail();

            Log.e(TAG, "handleGoogleSignInResult: personName "+personName );
            Log.e(TAG, "handleGoogleSignInResult: emailID  "+emailID );

            signInUsingGoogle(personName, "", emailID);

        } else {
            Toast.makeText(context, "Error while Signing in", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "handleGoogleSignInResult: " + result.getStatus());
            Log.e(TAG, "handleGoogleSignInResult: " + result.getSignInAccount());
            Log.e(TAG, "handleGoogleSignInResult: " + result.isSuccess());

        }

    }

    private void signInUsingGoogle(String personName, String personPhotoUrl, String emailID) {

        Log.e(TAG, "signInUsingGoogle: "+personName+"email"+emailID );

        showProgressDialog();

//        public static final String SERVICE_VERIFY_SOCIAL = "customer/verifySocial";
        final String urlSocialLogin = GlobalData.FILE_URL + GlobalData.SERVICE_VERIFY_SOCIAL;

        Log.e(TAG, "signInUsingGoogle: "+urlSocialLogin );

        final String mPersonName = personName;
        final String mEmailID = emailID;
        final String mPersonPhotoUrl = personPhotoUrl;

        final JSONObject json = new JSONObject();

        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String token = pref.getString(GlobalData.USER_TOKEN, "0");


        try {

            json.put("emailId", "" + emailID);
            json.put("deviceToken", "" + token);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e(TAG, "signInUsingGoogle: "+json.toString() );

        JsonObjectRequest googleReq = new JsonObjectRequest(Request.Method.POST, urlSocialLogin, json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {


                        Log.e(TAG, "onResponse: "+response.toString() );

                        if (mProgressDialog.isShowing()) {
                            hideProgressDialog();
                        }

                        try {

                            String responseCode = response.getString("responseCode");

                            String status = "" + response.getInt("responseCode");
                            String responseMessage = response.getString("responseMessage");

                            if (status.equalsIgnoreCase("500")) {
                                Toast.makeText(context, responseMessage, Toast.LENGTH_LONG).show();
                            }

                            if (responseCode.equalsIgnoreCase("200")) {

                                if (mProgressDialog.isShowing()) {
                                    hideProgressDialog();
                                }

                                String flag = response.getString("flag");
                                String customerId = response.getString("customerId");

                                if (flag.equalsIgnoreCase(GlobalData.FLAG_NEW)) {

                                    CommonUtil.setSharePreferenceString(LoginActivity.this, GlobalData.LOGIN_USER_TYPE, GlobalData.USING_GOOGLE);

                                    Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                                    intent.putExtra("personName", mPersonName);
                                    intent.putExtra("emailID", mEmailID);
                                    intent.putExtra("personPhotoUrl", mPersonPhotoUrl);
                                    intent.putExtra("flag", flag);
                                    startActivity(intent);
                                    finish();

                                } else {

                                    CommonUtil.setSharePreferenceString(LoginActivity.this, GlobalData.LOGIN_USER_TYPE, GlobalData.USING_GOOGLE);
                                    CommonUtil.setSharePreferenceString(LoginActivity.this, GlobalData.LOGIN_USER_ID, customerId);

                                    CommonUtil.setSharePreferenceString(LoginActivity.this, GlobalData.LOGIN_USER_NAME, "" + mPersonName);
                                    CommonUtil.setSharePreferenceString(LoginActivity.this, GlobalData.LOGIN_USER_EMAIL_ID, "" + mEmailID);

                                    //for maintaining the log in session
                                    CommonUtil.setSharePreferenceString(LoginActivity.this, GlobalData.APP_LOGIN_STATUS, GlobalData.LOGED_IN);

                                    Intent intent = new Intent(LoginActivity.this, InitialSetupActivity.class);
                                    startActivity(intent);
                                    finish();

                                }

                            }
                            if (responseCode.equalsIgnoreCase("402")) {

                                Log.e(TAG, "onResponse: "+response.toString() );

//                                Toast.makeText(context, "Server Error.", Toast.LENGTH_SHORT).show();
                                if (mProgressDialog.isShowing()) {
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

                Log.e(TAG, "onErrorResponse: "+error.toString() );

                if (mProgressDialog.isShowing()) {
                    hideProgressDialog();
                }
//                Toast.makeText(context, "Google Login S_Error...", Toast.LENGTH_LONG).show();

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

        googleReq.setRetryPolicy(new DefaultRetryPolicy(
                GlobalData.REQUEST_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        googleReq.setRetryPolicy(new DefaultRetryPolicy());

        requestQueue.add(googleReq);

    }

    private void signInUsingMobile() {

        showProgressDialog();

//        public static final String SERVICE_OTP_REQUEST = "customer/otprequest";
        final String urlOtpRequest = GlobalData.FILE_URL + GlobalData.SERVICE_OTP_REQUEST;

        final String contactNo = et_user_mobno.getText().toString();

        final JSONObject json = new JSONObject();

        try {

            json.put("contactNo", "" + contactNo);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest mobileReq = new JsonObjectRequest(Request.Method.POST, urlOtpRequest, json, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                if (mProgressDialog.isShowing()) {
                    hideProgressDialog();
                }

                try {

                    String responseCode = response.getString("responseCode");

                    String status = "" + response.getInt("responseCode");
                    String responseMessage = response.getString("responseMessage");

                    if (status.equalsIgnoreCase("500")) {
                        Toast.makeText(context, responseMessage, Toast.LENGTH_LONG).show();
                    }

                    if (responseCode.equalsIgnoreCase("200")) {

                        String customerId = response.getString("customerId");
                        String flag = response.getString("flag");


                        if (mProgressDialog.isShowing()) {
                            hideProgressDialog();
                        }

                        Intent intent = new Intent(LoginActivity.this, VerifyOtpActivity.class);
                        intent.putExtra("contactNo", contactNo);
                        intent.putExtra("customerId", customerId);
                        intent.putExtra("flag", flag);
                        startActivity(intent);
                        finish();

                    }

                    if (responseCode.equalsIgnoreCase("402")) {

                        Toast.makeText(context, responseMessage, Toast.LENGTH_LONG).show();
                        if (mProgressDialog.isShowing()) {
                            hideProgressDialog();
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

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };


        mobileReq.setRetryPolicy(new DefaultRetryPolicy(
                GlobalData.REQUEST_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(mobileReq);

    }

    private void signInUsingFacebook(String personName, String personPhotoUrl, String emailID) {

        showProgressDialog();

//        public static final String SERVICE_VERIFY_SOCIAL = "customer/verifySocial";
        final String urlSocialLogin = GlobalData.FILE_URL + GlobalData.SERVICE_VERIFY_SOCIAL;

        final String mPersonName = personName;
        final String mEmailID = emailID;
        final String mPersonPhotoUrl = personPhotoUrl;

        final JSONObject json = new JSONObject();

        try {
            json.put("emailId", "" + emailID);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest facebookReq = new JsonObjectRequest(Request.Method.POST, urlSocialLogin, json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (mProgressDialog.isShowing()) {
                            hideProgressDialog();
                        }

                        try {


                            String responseCode = response.getString("responseCode");

                            String status = "" + response.getInt("responseCode");
                            String responseMessage = response.getString("responseMessage");

                            if (status.equalsIgnoreCase("500")) {
                                Toast.makeText(context, responseMessage, Toast.LENGTH_LONG).show();
                            }

                            if (responseCode.equalsIgnoreCase("200")) {

                                String flag = response.getString("flag");
                                String customerId = response.getString("customerId");

                                if (flag.equalsIgnoreCase(GlobalData.FLAG_NEW)) {

                                    CommonUtil.setSharePreferenceString(LoginActivity.this, GlobalData.LOGIN_USER_TYPE, GlobalData.USING_FACEBOOK);

                                    Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                                    intent.putExtra("personName", mPersonName);
                                    intent.putExtra("emailID", mEmailID);
                                    intent.putExtra("personPhotoUrl", mPersonPhotoUrl);
                                    intent.putExtra("flag", flag);
                                    startActivity(intent);
                                    finish();

                                } else {

                                    CommonUtil.setSharePreferenceString(LoginActivity.this, GlobalData.LOGIN_USER_TYPE, GlobalData.USING_FACEBOOK);
                                    CommonUtil.setSharePreferenceString(LoginActivity.this, GlobalData.LOGIN_USER_ID, customerId);
                                    CommonUtil.setSharePreferenceString(LoginActivity.this, GlobalData.LOGIN_USER_NAME, "" + mPersonName);
                                    CommonUtil.setSharePreferenceString(LoginActivity.this, GlobalData.LOGIN_USER_EMAIL_ID, "" + mEmailID);
                                    //for miantaining the log in session
                                    CommonUtil.setSharePreferenceString(LoginActivity.this, GlobalData.APP_LOGIN_STATUS, GlobalData.LOGED_IN);

                                    Intent intent = new Intent(LoginActivity.this, InitialSetupActivity.class);
                                    startActivity(intent);
                                    finish();

                                }

                                if (mProgressDialog.isShowing()) {
                                    hideProgressDialog();
                                }

                            }
                            if (responseCode.equalsIgnoreCase("402")) {

//                                Toast.makeText(context, "Server Error..", Toast.LENGTH_SHORT).show();
                                if (mProgressDialog.isShowing()) {
                                    hideProgressDialog();
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
                Toast.makeText(context, "Facebook Login S_Error...", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

        facebookReq.setRetryPolicy(new DefaultRetryPolicy(
                GlobalData.REQUEST_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(facebookReq);

    }

    private void signInUsingUsernamePassword() {

        showProgressDialog();

        String url = GlobalData.FILE_URL + GlobalData.SERVICE_USER_LOGIN;

        final String emailID = et_username.getText().toString();
        String password = et_password.getText().toString();

        String device_name = GlobalData.getDeviceName();
        String device_os = GlobalData.getDeviceOS();
        String imei = CommonUtil.getIMEI(context);
        String userID = CommonUtil.getSharePreferenceString(context, GlobalData.SHARE_USER_ID, "0");

        Map<String, String> postParam = new HashMap<String, String>();

        /*JSONObject js = new JSONObject();*/
        try {
            postParam.put("api_key", GlobalData.API_KEY);
            postParam.put("userName", "" + emailID);
            postParam.put("password", "" + password);
            postParam.put("deviceName", device_name);
            postParam.put("deviceOS", device_os);
            postParam.put("deviceIMEI", imei);
            postParam.put("deviceToken", "");

        } catch (Exception e) {
            e.printStackTrace();
        }

        /*{
                "userName":"Motiram",
                "password":"1111111",
                "deviceName":"Laptop",
                "deviceOS":"Linux",
                "deviceIMEI":"123456",
                "deviceToken":"NOT"
        }*/

        /*{
        	"api_key": "abcd",
            "userName":"imran",
                "password":"123123",
                "deviceName":"Mobile",
                "deviceOS":"Android",
                "deviceIMEI":1234,
                "deviceToken":""
        }*/


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT,
                url, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        if (mProgressDialog.isShowing()) {
                            hideProgressDialog();
                        }

                        String str = response.toString();


                        String jsonFormattedString = str.replaceAll("\\\\r", "").replaceAll("\\\\n", "").replaceAll("\\\\", "");

                        try {
                            String status = "" + response.getInt("responseCode");
                            String responseMessage = response.getString("responseMessage");

                            if (status.equalsIgnoreCase("500")) {
                                Toast.makeText(context, responseMessage, Toast.LENGTH_LONG).show();
                            }

                            if (status.equalsIgnoreCase("200")) {


                                JSONObject loginJObj = response.getJSONObject("login");

                                String customerId = "" + loginJObj.getInt("customerId");
                                String SHARE_USER_NAME = "" + loginJObj.getString("customerName");
                                String SHARE_MOBILE_NUMBER = "" + loginJObj.getString("contactNo");

                                CommonUtil.setSharePreferenceString(LoginActivity.this, GlobalData.SHARE_USER_ID, "" + customerId);
                                CommonUtil.setSharePreferenceString(LoginActivity.this, GlobalData.SHARE_MAIL_ADDRESS, "" + emailID);
                                CommonUtil.setSharePreferenceString(LoginActivity.this, GlobalData.SHARE_USER_NAME, "" + SHARE_USER_NAME);
                                CommonUtil.setSharePreferenceString(LoginActivity.this, GlobalData.SHARE_MOBILE_NUMBER, "" + SHARE_MOBILE_NUMBER);

                                /*Temp Setting values */

                                CommonUtil.setSharePreferenceString(LoginActivity.this, GlobalData.LOGIN_USER_TYPE, GlobalData.USING_USERID_PASSWORD);
                                CommonUtil.setSharePreferenceString(LoginActivity.this, GlobalData.LOGIN_USER_ID, customerId);

                                /*Temp Setting values */

                                Intent intent = new Intent(context, InitialSetupActivity.class);
                                startActivity(intent);
                                finish();

                            }

                            if (mProgressDialog.isShowing()) {
                                hideProgressDialog();
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
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(jsonObjReq);

    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    // Showing the status in Snackbar
    private void showSnack(boolean isConnected) {
        String message;
        int color;
        if (isConnected) {
            message = "Good! Connected to Internet";
            color = Color.WHITE;
        } else {
            message = "No internet connection";
            color = Color.WHITE;
        }

        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.view), message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(color);
        if (!isConnected)
            snackbar.show();
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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public class CustomDialogClass extends Dialog implements
            View.OnClickListener {

        public Activity c;
        public Dialog d;
        public Button yes, no;

        public CustomDialogClass(Activity a) {
            super(a);
            // TODO Auto-generated constructor stub
            this.c = a;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.lay_otp_custom_dialog);
            TextView tvDiaMobNo = (TextView) findViewById(R.id.tvDiaMobNo);
            tvDiaMobNo.setText(et_user_mobno.getText().toString());
            yes = (Button) findViewById(R.id.btnSubmitMob);
            no = (Button) findViewById(R.id.btnEditMob);
            yes.setOnClickListener(this);
            no.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnSubmitMob:
                    signInUsingMobile();
                    dismiss();
                    break;
                case R.id.btnEditMob:
                    dismiss();
                    break;
                default:
                    break;
            }
            dismiss();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
