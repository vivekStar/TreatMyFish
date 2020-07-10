package com.webfarms.treatmyfish;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.webfarms.treatmyfish.adapter.StringListAdapter;
import com.webfarms.treatmyfish.app.Config;
import com.webfarms.treatmyfish.utils.CommonUtil;
import com.webfarms.treatmyfish.utils.GlobalData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by WEBFARMSPC2 on 3/29/2018.
 */

public class SignUpActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String urlgetStateList = GlobalData.FILE_URL + GlobalData.SERVICE_GET_STATE_LIST;
    private static final String urlgetDistrictList = GlobalData.FILE_URL + GlobalData.SERVICE_GET_DISTRICT_LIST;
    private static final String urlgetTalukaList = GlobalData.FILE_URL + GlobalData.SERVICE_GET_CITY_LIST;

    private Context context;
    private Toolbar toolbar;
    private EditText edtMobileNo, edtPersonName, edtEmailID, edtPinCode;
    private Button btnSaveContact;

    private RequestQueue requestQueue;
    private ProgressDialog pDialog;

    private GoogleApiClient mGoogleApiClient;

    Spinner spinnerMr, spinnerStateList, spinnerDistrictList, spinnerTalukaList, spinnerCityList;
    private LinearLayout llDistrict, llTaluka, llCity;

    private static JSONObject responseStateJson = null, responseDistrictJson = null, responseTalukaJson = null;

    ArrayList<String> stateList, districtList, talukaList, cityList, villageList, acresList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.lay_signup);

        toolbar = GlobalData.initToolBar(SignUpActivity.this, getString(R.string.signup), true);

        initialize();

        initializeUserData();

        getStateList();

        btnSaveContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String loginType = CommonUtil.getSharePreferenceString(SignUpActivity.this, GlobalData.LOGIN_USER_TYPE, "0");

                if (loginType.equalsIgnoreCase(GlobalData.USING_MOBILE_NO)) {

                    saveMobileSignUpDetails();

                } else if (loginType.equalsIgnoreCase(GlobalData.USING_GOOGLE)) {

                    saveGoogleSignUpDetails();

                } else if (loginType.equalsIgnoreCase(GlobalData.USING_FACEBOOK)) {

                    saveGoogleSignUpDetails();
                }
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, stateList);

        spinnerStateList.setAdapter(adapter);

        spinnerStateList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getDistrictList(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerDistrictList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getTalukaList(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerTalukaList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selTal = spinnerTalukaList.getSelectedItem().toString().trim();

//                setCityVillageList(selTal);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void initialize() {

        context = SignUpActivity.this;

        edtMobileNo = findViewById(R.id.edtMobileNo);
        edtPersonName = findViewById(R.id.edtPersonName);
        edtEmailID = findViewById(R.id.edtEmailID);
        edtPinCode = findViewById(R.id.edtPinCode);

        btnSaveContact = findViewById(R.id.btnSaveContact);

        spinnerStateList = findViewById(R.id.spinnerStateList);
        spinnerDistrictList = findViewById(R.id.spinnerDistrictList);
        spinnerTalukaList = findViewById(R.id.spinnerTalukaList);
        spinnerCityList = findViewById(R.id.spinnerCityList);
//        spinnerAcres = findViewById(R.id.spinnerAcresList);

        llDistrict = findViewById(R.id.ll_add_district);
        llTaluka = findViewById(R.id.ll_add_taluka);
        llCity = findViewById(R.id.ll_add_city);

        requestQueue = Volley.newRequestQueue(context);

        pDialog = new ProgressDialog(context);
        pDialog.setIndeterminate(true);
        pDialog.setMessage("Please Wait...");
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setCancelable(false);

        stateList = new ArrayList<>();
        districtList = new ArrayList<>();
        talukaList = new ArrayList<>();
        cityList = new ArrayList<>();
        villageList = new ArrayList<>();
        acresList = new ArrayList<>();

        acresList.add(0, "Select Acres");
        acresList.add(1, "1-3 Acres");
        acresList.add(2, "3-7 Acres");
        acresList.add(3, "7-15 Acres");
        acresList.add(4, "15-25 Acres");

//        ArrayAdapter<String> adapter4 = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, acresList);
////        StringListAdapter adapter4 = new StringListAdapter(context, acresList);
//
//        spinnerAcres.setAdapter(adapter4);

    }

    //    *-*-*************************
    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = context.getAssets().open("city.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private void initializeUserData() {

        String loginType = CommonUtil.getSharePreferenceString(SignUpActivity.this, GlobalData.LOGIN_USER_TYPE, "0");


        if (loginType.equalsIgnoreCase(GlobalData.USING_GOOGLE)) {

            Intent intent = getIntent();
            String personName = intent.getStringExtra("personName");
            String personPhotoUrl = intent.getStringExtra("personPhotoUrl");
            String flag = intent.getStringExtra("flag");
            String emailID = intent.getStringExtra("emailID");


            if (emailID != null) {

                edtPersonName.setText(personName);
                edtPersonName.setEnabled(false);
                edtEmailID.setText(emailID);
                edtEmailID.setEnabled(false);

            }

            CommonUtil.setSharePreferenceString(SignUpActivity.this, GlobalData.LOGIN_USER_PHOTOURL, "" + personPhotoUrl);

        } else if (loginType.equalsIgnoreCase(GlobalData.USING_MOBILE_NO)) {

            Intent intent = getIntent();
            String contactNo = intent.getStringExtra("contactNo");
            String customerId = intent.getStringExtra("customerId");
            String flag = intent.getStringExtra("flag");


            if (contactNo != null) {
                edtMobileNo.setText(contactNo);
                edtMobileNo.setEnabled(false);
            }

            CommonUtil.setSharePreferenceString(SignUpActivity.this, GlobalData.LOGIN_USER_MOBILE_NUMBER, "" + contactNo);
            CommonUtil.setSharePreferenceString(SignUpActivity.this, GlobalData.LOGIN_USER_ID, "" + customerId);

        } else if (loginType.equalsIgnoreCase(GlobalData.USING_FACEBOOK)) {

            Intent intent = getIntent();
            String personName = intent.getStringExtra("personName");
            String personPhotoUrl = intent.getStringExtra("personPhotoUrl");
            String flag = intent.getStringExtra("flag");
            String emailID = intent.getStringExtra("emailID");


            if (emailID != null) {

                edtPersonName.setText(personName);
                edtPersonName.setEnabled(false);
                edtEmailID.setText(emailID);
                edtEmailID.setEnabled(false);

            }

            CommonUtil.setSharePreferenceString(SignUpActivity.this, GlobalData.LOGIN_USER_PHOTOURL, "" + personPhotoUrl);

        }

    }

    public void saveGoogleSignUpDetails() {


        if (edtPersonName.getText().toString().isEmpty()) {
            edtPersonName.setError("Required");
            return;
        }
        if (edtEmailID.getText().toString().isEmpty()) {
            edtEmailID.setError("Required");
            return;
        }
        if (edtMobileNo.getText().toString().isEmpty()) {
            edtMobileNo.setError("Required");
            return;
        }

        int valid = GlobalData.validateABC(edtPersonName.getText().toString());
//        if (valid != GlobalData.VALID) {
//            toastMessage("Please enter correct name");
//            return;
//        }
//        valid = GlobalData.validateEmail(edtEmailID.getText().toString());
//        if (valid != GlobalData.VALID) {
//            toastMessage("Please enter correct email");
//            return;
//        }
        valid = GlobalData.validateMobileNo(edtMobileNo.getText().toString());
        if (valid != GlobalData.VALID) {
            toastMessage("Enter correct mobile number");
            return;
        }

        String state = spinnerStateList.getSelectedItem().toString();
        if (state.equalsIgnoreCase("Select State")) {
            toastMessage("Select State");
            return;
        }
        String district = spinnerDistrictList.getSelectedItem().toString();
        if (district.equalsIgnoreCase("Select District")) {
            toastMessage("Select District");
            return;
        }
        String taluka = spinnerTalukaList.getSelectedItem().toString();
        if (taluka.equalsIgnoreCase("Select City")) {
            toastMessage("Select City");
            return;
        }
//        String city = spinnerCityList.getSelectedItem().toString();
//        if (city.equalsIgnoreCase("Select City")) {
//            toastMessage("Select City..");
//            return;
//        }

//        String block = spinnerAcres.getSelectedItem().toString();
//        if (block.equalsIgnoreCase("Select Acres")) {
//            toastMessage("Select Block");
//            return;
//        }

        final String personName = edtPersonName.getText().toString();
        final String mobileNo = edtMobileNo.getText().toString();
        final String email = edtEmailID.getText().toString();
        String pincode = edtPinCode.getText().toString();

        /*New Entrys*/

        String device_name = GlobalData.getDeviceName();
        String device_os = GlobalData.getDeviceOS();
        String imei = CommonUtil.getIMEI(context);

        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String token = pref.getString(GlobalData.USER_TOKEN, "0");


        if (token.equals("0")) {
            toastMessage("Invalid Token..");
        }

        /*End*/

        JSONObject json = new JSONObject();
        try {

            json.put("customerName", personName);
            json.put("contactNo", mobileNo);
            json.put("emailId", email);
            json.put("country", "India");
            json.put("state", state);
            json.put("city", taluka);
//            json.put("taluka", taluka);
            json.put("farmArea", "");
            json.put("pinCode", pincode);
            json.put("district", district);

            json.put("deviceName", device_name);
            json.put("deviceOS", device_os);
            json.put("deviceIMEI", imei);
            json.put("deviceToken", token);

        } catch (Exception e) {
            e.printStackTrace();
        }


//        public static final String SERVICE_UPDATE_VIA_SOCIAL = "customer/socialMedia";
        final String urlUpdateViaSocial = GlobalData.FILE_URL + GlobalData.SERVICE_UPDATE_VIA_SOCIAL;

        showProgressDialog();

        JsonObjectRequest jsonContactUploadReq = new JsonObjectRequest(Request.Method.POST, urlUpdateViaSocial, json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            String responseCode = response.getString("responseCode");

                            if (responseCode.equalsIgnoreCase("200")) {

                                String customerId = response.getString("customerId");

                                CommonUtil.setSharePreferenceString(SignUpActivity.this, GlobalData.LOGIN_USER_NAME, "" + personName);
                                CommonUtil.setSharePreferenceString(SignUpActivity.this, GlobalData.LOGIN_USER_EMAIL_ID, "" + email);
                                CommonUtil.setSharePreferenceString(SignUpActivity.this, GlobalData.LOGIN_USER_MOBILE_NUMBER, "" + mobileNo);
                                CommonUtil.setSharePreferenceString(SignUpActivity.this, GlobalData.LOGIN_USER_ID, customerId);
                                CommonUtil.setSharePreferenceString(SignUpActivity.this, GlobalData.SIGNED_UP_STATUS, GlobalData.STATUS_SIGNUP_COMPLETED);

                                //for miantaining the log in session
                                CommonUtil.setSharePreferenceString(SignUpActivity.this, GlobalData.APP_LOGIN_STATUS, GlobalData.LOGED_IN);

                                Intent intent = new Intent(SignUpActivity.this, InitialSetupActivity.class);
                                startActivity(intent);
                                finish();

                            }
                            if (responseCode.equalsIgnoreCase("402")) {

                                Toast.makeText(context, "Email/Mobile No already registered.", Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (pDialog.isShowing()) {
                            hideProgressDialog();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
                error.printStackTrace();
                Toast.makeText(context, "Error while processing data", Toast.LENGTH_LONG).show();
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

    public void saveMobileSignUpDetails() {

        if (edtPersonName.getText().toString().isEmpty()) {
            edtPersonName.setError("Required");
            return;
        }
        if (edtEmailID.getText().toString().isEmpty()) {
            edtEmailID.setError("Required");
            return;
        }
        if (edtMobileNo.getText().toString().isEmpty()) {
            edtMobileNo.setError("Required");
            return;
        }
        int valid = GlobalData.validateABC(edtPersonName.getText().toString());
        if (valid != GlobalData.VALID) {
            toastMessage("Please enter correct name");
            return;
        }
        valid = GlobalData.validateEmail(edtEmailID.getText().toString());
        if (valid != GlobalData.VALID) {
            toastMessage("Please enter correct email");
            return;
        }
        valid = GlobalData.validateMobileNo(edtMobileNo.getText().toString());
        if (valid != GlobalData.VALID) {
            toastMessage("Please enter correct mobile number");
            return;
        }

        String state = spinnerStateList.getSelectedItem().toString();
        if (state.equalsIgnoreCase("Select State")) {
            toastMessage("Select State..");
            return;
        }
        String district = spinnerDistrictList.getSelectedItem().toString();
        if (district.equalsIgnoreCase("Select District")) {
            toastMessage("Select District..");
            return;
        }
        String taluka = spinnerTalukaList.getSelectedItem().toString();
        if (taluka.equalsIgnoreCase("Select City")) {
            toastMessage("Select City..");
            return;
        }
//        String city = spinnerCityList.getSelectedItem().toString();
//        if (city.equalsIgnoreCase("Select City")) {
//            toastMessage("Select City..");
//            return;
//        }
//        String block = spinnerAcres.getSelectedItem().toString();
//        if (block.equalsIgnoreCase("Select Acres")) {
//            toastMessage("Select Block..");
//            return;
//        }


        final String personName = edtPersonName.getText().toString();
        final String mobileNo = edtMobileNo.getText().toString();
        final String email = edtEmailID.getText().toString();
        String pincode = edtPinCode.getText().toString();


        /*New Entrys*/

        String device_name = GlobalData.getDeviceName();
        String device_os = GlobalData.getDeviceOS();
        String imei = CommonUtil.getIMEI(context);

        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String token = pref.getString(GlobalData.USER_TOKEN, "0");


        if (token.equals("0")) {
            return;
        }

        /*End*/

        JSONObject json = new JSONObject();

        try {

            json.put("customerName", personName);
            json.put("contactNo", mobileNo);
            json.put("emailId", email);
            json.put("country", "India");
            json.put("state", state);
            json.put("city", taluka);
//            json.put("taluka", taluka);
            json.put("farmArea", "");
            json.put("pinCode", pincode);
            json.put("district", district);

            json.put("deviceName", device_name);
            json.put("deviceOS", device_os);
            json.put("deviceIMEI", imei);
            json.put("deviceToken", token);


        } catch (Exception e) {
            e.printStackTrace();
        }

//        public static final String SERVICE_UPDATE_VIA_OTP = "customer/updateViaOTP";
        final String urlFarmerInfo = GlobalData.FILE_URL + GlobalData.SERVICE_UPDATE_VIA_OTP;

        showProgressDialog();

        JsonObjectRequest jsonContactUploadReq = new JsonObjectRequest(Request.Method.POST, urlFarmerInfo, json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            String responseCode = response.getString("responseCode");

                            if (responseCode.equalsIgnoreCase("200")) {


                                CommonUtil.setSharePreferenceString(SignUpActivity.this, GlobalData.LOGIN_USER_NAME, "" + personName);
                                CommonUtil.setSharePreferenceString(SignUpActivity.this, GlobalData.LOGIN_USER_EMAIL_ID, "" + email);

                                String id = CommonUtil.getSharePreferenceString(SignUpActivity.this, GlobalData.LOGIN_USER_ID, "0");

                                //for miantaining the log in session
                                CommonUtil.setSharePreferenceString(SignUpActivity.this, GlobalData.APP_LOGIN_STATUS, GlobalData.LOGED_IN);

                                Intent intent = new Intent(SignUpActivity.this, InitialSetupActivity.class);
                                startActivity(intent);
                                finish();

                            }
                            if (responseCode.equalsIgnoreCase("402")) {

                                Toast.makeText(context, "Email/Mobile No already registered", Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (pDialog.isShowing()) {
                            hideProgressDialog();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
                error.printStackTrace();
                Toast.makeText(context, "Save Details S_Error...", Toast.LENGTH_LONG).show();

                //Error while processing data

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

    private void getStateList() {

        showProgressDialog();

        JsonObjectRequest stateJsonReq = new JsonObjectRequest(Request.Method.GET, urlgetStateList, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (pDialog.isShowing()) {
                            hideProgressDialog();
                        }
                        String status = null;
                        try {
                            status = "" + response.getInt("responseCode");
                            if (!status.equalsIgnoreCase("200")) {
                                return;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (response != null) {
                            setStateList(response);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
                error.printStackTrace();
//                Toast.makeText(context, "State List S_Error...", Toast.LENGTH_LONG).show();

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

        stateJsonReq.setRetryPolicy(new DefaultRetryPolicy(
                GlobalData.REQUEST_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stateJsonReq);
    }

    //setting state list to spinner state
    private void setStateList(JSONObject response) {

        llDistrict.setVisibility(View.GONE);
        llTaluka.setVisibility(View.GONE);
        llCity.setVisibility(View.GONE);

        stateList.clear();
        stateList.add(0, "Select State");

        responseStateJson = response;

        try {

//            JSONObject stateDistrictList = response.getJSONObject("stateList");
//            JSONArray stateArray = stateDistrictList.getJSONArray("state");

            JSONArray stateArray = response.getJSONArray("list");

            for (int i = 0; i < stateArray.length(); i++) {
                JSONObject stateOneJson = (JSONObject) stateArray.get(i);
                String state = stateOneJson.getString("stateName");
                stateList.add(state);
            }
        } catch (Exception e) {

            e.printStackTrace();

        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, stateList);
//        StringListAdapter adapter = new StringListAdapter(context, stateList);
        spinnerStateList.setAdapter(adapter);
    }

    private void getDistrictList(int position) {

        String selState = spinnerStateList.getSelectedItem().toString();

        String stateID = getStateId(selState);

        if (stateID == null) {
            return;
        }

        JSONObject js = new JSONObject();
        try {
            js.put("stateId", stateID);
        } catch (Exception e) {

        }


        showProgressDialog();

        JsonObjectRequest talukaJsonReq = new JsonObjectRequest(Request.Method.POST, urlgetDistrictList, js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {


                        if (response != null) {
                            setDistrictList(response);
                        }
                        if (pDialog.isShowing()) {
                            hideProgressDialog();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
                error.printStackTrace();
//                Toast.makeText(context, "Taluka List S_Error...", Toast.LENGTH_LONG).show();

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

        talukaJsonReq.setRetryPolicy(new DefaultRetryPolicy(
                GlobalData.REQUEST_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(talukaJsonReq);

    }

    private void setDistrictList(JSONObject response) {

        llDistrict.setVisibility(View.VISIBLE);
        llTaluka.setVisibility(View.GONE);
        llCity.setVisibility(View.GONE);

        districtList.clear();
        districtList.add(0, "Select District");

        responseDistrictJson = response;

        try {

//            JSONObject stateDistrictList = response.getJSONObject("stateList");
//            JSONArray stateArray = stateDistrictList.getJSONArray("state");

            JSONArray stateArray = response.getJSONArray("list");

            for (int i = 0; i < stateArray.length(); i++) {
                JSONObject stateOneJson = (JSONObject) stateArray.get(i);
                String districtName = stateOneJson.getString("districtName");
                districtList.add(districtName);
            }
        } catch (Exception e) {

            e.printStackTrace();

        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, districtList);
//        StringListAdapter adapter = new StringListAdapter(context, districtList);
        spinnerDistrictList.setAdapter(adapter);

    }

    private void getTalukaList(int position) {

        String selState = spinnerStateList.getSelectedItem().toString();
        String stateId = getStateId(selState);
        String selDistrict = spinnerDistrictList.getSelectedItem().toString();
        String districtId = getDistrictId(selDistrict);

        if (stateId == null) {
            return;
        }
        if (districtId == null) {
            return;
        }

        JSONObject js = new JSONObject();
        try {
            js.put("stateId", stateId);
            js.put("districtId", districtId);
        } catch (Exception e) {

        }


        showProgressDialog();

        JsonObjectRequest talukaJsonReq = new JsonObjectRequest(Request.Method.POST, urlgetTalukaList, js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (response != null) {
                            setTalukaList(response);
                        }
                        if (pDialog.isShowing()) {
                            hideProgressDialog();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
                error.printStackTrace();
//                Toast.makeText(context, "Taluka List S_Error...", Toast.LENGTH_LONG).show();

                if (pDialog.isShowing()) {
                    hideProgressDialog();
                }

                GlobalData.showNoResponseDialog(context);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

        talukaJsonReq.setRetryPolicy(new DefaultRetryPolicy(
                GlobalData.REQUEST_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(talukaJsonReq);

    }

    private void setTalukaList(JSONObject response) {

        llTaluka.setVisibility(View.VISIBLE);
        llCity.setVisibility(View.GONE);

        talukaList.clear();
        talukaList.add(0, "Select City");

        responseTalukaJson = response;

        try {

//            JSONObject stateDistrictList = response.getJSONObject("stateList");
//            JSONArray stateArray = stateDistrictList.getJSONArray("state");

            JSONArray stateArray = response.getJSONArray("list");

            for (int i = 0; i < stateArray.length(); i++) {
                JSONObject stateOneJson = (JSONObject) stateArray.get(i);
                String districtName = stateOneJson.getString("cityName");
                talukaList.add(districtName);
            }
        } catch (Exception e) {

            e.printStackTrace();

        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, talukaList);
//        StringListAdapter adapter = new StringListAdapter(context, talukaList);
        spinnerTalukaList.setAdapter(adapter);
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

    private Boolean exit = false;

    @Override
    public void onBackPressed() {
        if (exit) {

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            return;


        } else {

            GlobalData.showSnackBar(llDistrict, "Press back again to exit.", true);
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }
    }

    private void googleSignOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {

                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.

    }

    private String getStateId(String selState) {

        String stateID = null;

        try {

//            JSONObject stateDistrictList = response.getJSONObject("stateList");
//            JSONArray stateArray = stateDistrictList.getJSONArray("state");

            JSONArray stateArray = responseStateJson.getJSONArray("list");

            for (int i = 0; i < stateArray.length(); i++) {
                JSONObject stateOneJson = (JSONObject) stateArray.get(i);
                String state = stateOneJson.getString("stateName");
                if (state.equalsIgnoreCase(selState)) {
                    stateID = stateOneJson.getString("stateID");
                }
            }
        } catch (Exception e) {

            e.printStackTrace();

        }

        return stateID;

    }

    private String getDistrictId(String selDistrict) {

        String districtID = null;

        try {

//            JSONObject stateDistrictList = response.getJSONObject("stateList");
//            JSONArray stateArray = stateDistrictList.getJSONArray("state");

            JSONArray districtArray = responseDistrictJson.getJSONArray("list");

            for (int i = 0; i < districtArray.length(); i++) {
                JSONObject districtOneJson = (JSONObject) districtArray.get(i);
                String districtName = districtOneJson.getString("districtName");
                if (districtName.equalsIgnoreCase(selDistrict)) {
                    districtID = districtOneJson.getString("districtID");
                }
            }
        } catch (Exception e) {

            e.printStackTrace();

        }

        return districtID;

    }

    public void toastMessage(String msg) {
        Toast.makeText(context, "" + msg, Toast.LENGTH_SHORT).show();
    }

}
