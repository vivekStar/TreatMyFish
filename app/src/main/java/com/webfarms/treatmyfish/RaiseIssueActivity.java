package com.webfarms.treatmyfish;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.webfarms.treatmyfish.app.AppController;
import com.webfarms.treatmyfish.database.TableIssueList;
import com.webfarms.treatmyfish.utils.Base64;
import com.webfarms.treatmyfish.utils.CommonUtil;
import com.webfarms.treatmyfish.utils.CropImage;
import com.webfarms.treatmyfish.utils.GlobalData;
import com.webfarms.treatmyfish.utils.InternalStorageContentProvider;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class RaiseIssueActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private Context context;
    private Toolbar toolbar;

    private Location mylocation;
    private Location location;
    private String myLocation;
    private GoogleApiClient googleApiClient;
    private final static int REQUEST_CHECK_SETTINGS_GPS = 0x12;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS = 0x14;

    private LinkedList<ContentValues> issueList1 = new LinkedList<>();

    private long selectedIssueId = 0;

    private static final int FRAGMENT_GROUPID = 30;

    private Bitmap imgBitmap[] = new Bitmap[4];
    private static final int BUTTON1_CAMERA = 1;
    private static final int BUTTON2_CAMERA = 2;
    private static final int BUTTON3_CAMERA = 3;
    private static final int BUTTON4_CAMERA = 4;

    private static final int BUTTON1_GALARY = 5;
    private static final int BUTTON2_GALARY = 6;
    private static final int BUTTON3_GALARY = 7;
    private static final int BUTTON4_GALARY = 8;

    private static final int CROP_FROM_CAMERA1 = 9;
    private static final int CROP_FROM_CAMERA2 = 10;
    private static final int CROP_FROM_CAMERA3 = 11;

    private int mYear;
    private int mMonth;
    private int mDay;

    private String tag_string_req = "string_req";
    private String TAG = RaiseIssueActivity.class.getSimpleName();

    private GoogleApiClient mGoogleApiClient;

    private ProgressDialog pDialog;

    private Spinner spn_issue_list;
    private Button bt_submit_issue;
    private LinearLayout ll_request_submit, ll_request_issue_form, lin_btm;
    private TextView tv_customerIssueId;

    ImageButton ib1, ib2, ib3;
    private FrameLayout fib2, fib3;

    private LinearLayout ll_btm_home, ll_btm_profile, ll_btm_history, ll_btm_logout, ll_btm_aboutus;

    private EditText edt_wtr_area, edt_wtr_depth, edt_sps_composition, edt_stocking_density, edt_biomass;
    private EditText edt_fish_weight, edt_mor_ep_no, edt_wtr_ph, edt_wtr_alkalinity, edt_detail_desc, edt_med_given;

    private TextView edt_stocking_date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.lay_raise_issue_one);

        context = RaiseIssueActivity.this;
        toolbar = GlobalData.initToolBar(this, getString(R.string.title), true);

        initialize();


        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

    }

    private synchronized void initialize() {

        pDialog = new ProgressDialog(context);
        pDialog.setIndeterminate(true);
        pDialog.setMessage(getString(R.string.raise_issue));
        pDialog.setCancelable(false);

        // get the current date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);


        spn_issue_list = (Spinner) findViewById(R.id.spn_issue_list);

        edt_wtr_area = (EditText) findViewById(R.id.edt_wtr_area);
        edt_wtr_depth = (EditText) findViewById(R.id.edt_wtr_depth);
        edt_sps_composition = (EditText) findViewById(R.id.edt_sps_composition);
        edt_stocking_density = (EditText) findViewById(R.id.edt_stocking_density);
        edt_stocking_date = (TextView) findViewById(R.id.edt_stocking_date);
        edt_biomass = (EditText) findViewById(R.id.edt_biomass);
        edt_fish_weight = (EditText) findViewById(R.id.edt_fish_weight);
        edt_mor_ep_no = (EditText) findViewById(R.id.edt_mor_ep_no);
        edt_wtr_ph = (EditText) findViewById(R.id.edt_wtr_ph);
        edt_wtr_alkalinity = (EditText) findViewById(R.id.edt_wtr_alkalinity);
        edt_detail_desc = (EditText) findViewById(R.id.edt_detail_desc);
        edt_med_given = (EditText) findViewById(R.id.edt_med_given);

        bt_submit_issue = (Button) findViewById(R.id.bt_submit_issue);

        ll_request_submit = (LinearLayout) findViewById(R.id.ll_request_submit);
        ll_request_issue_form = (LinearLayout) findViewById(R.id.ll_request_issue_form);
        lin_btm = (LinearLayout) findViewById(R.id.lin_btm);
        tv_customerIssueId = (TextView) findViewById(R.id.tv_customerIssueId);

        ib1 = (ImageButton) findViewById(R.id.ib1);
        ib2 = (ImageButton) findViewById(R.id.ib2);
        ib3 = (ImageButton) findViewById(R.id.ib3);

        fib2 = (FrameLayout) findViewById(R.id.fib2);
        fib3 = (FrameLayout) findViewById(R.id.fib3);

        registerForContextMenu(ib1);
        registerForContextMenu(ib2);
        registerForContextMenu(ib3);

        ib1.setOnClickListener(imgClick);
        ib2.setOnClickListener(imgClick);
        ib3.setOnClickListener(imgClick);

        ib1.setTag(1);
        ib2.setTag(2);
        ib3.setTag(3);

        TableIssueList issueDb = new TableIssueList(context);
        issueList1 = new LinkedList<>();

        issueList1 = issueDb.selectIssue();

        String[] issue_name = new String[issueList1.size() + 1];
        issue_name[0] = "" + getString(R.string.str_select_issue);
        for (int i = 0; i < issueList1.size(); i++) {
            issue_name[i + 1] = issueList1.get(i).getAsString(TableIssueList.DataIssueList.ISSUE_KEY_issueName).trim();
        }


        bt_submit_issue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                validateRaiseIssueData();

            }
        });


        //to start GPS show dialog to turn On GPS -----START----

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        googleApiClient.connect();

        //to start GPS show dialog to turn On GPS -----ENDS----


        // Bottom filter menu
        ll_btm_home = (LinearLayout) findViewById(R.id.ll_btm_home);
        ll_btm_profile = (LinearLayout) findViewById(R.id.ll_btm_profile);
        ll_btm_history = (LinearLayout) findViewById(R.id.ll_btm_history);
        ll_btm_logout = (LinearLayout) findViewById(R.id.ll_btm_logout);
        ll_btm_aboutus = findViewById(R.id.ll_btm_aboutus);

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

                String type = CommonUtil.getSharePreferenceString(context, GlobalData.LOGIN_USER_TYPE, "N/A");


                if (type.equalsIgnoreCase(GlobalData.USING_GOOGLE)) {

                    googleSignOut();

                } else {
                    logout(context);
                }
            }
        });

        ll_btm_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, FishDash.class));
                finish();
            }
        });

        ll_btm_aboutus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, AboutUsActivity.class));
            }
        });

        edt_stocking_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(1001);
            }
        });

    }

    @Override
    public void onLocationChanged(Location location1) {
        location = location1;
        myLocation = "Latitude:" + location1.getLatitude() + ", Longitude:" + location1.getLongitude();

        /*
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocation(location1.getLatitude(), location1.getLongitude(), 1);
            String address = addressList.get(0).getAddressLine(0);
            String city = addressList.get(0).getLocality();
            String state = addressList.get(0).getAdminArea();
            String country = addressList.get(0).getCountryName();
            String postalCode = addressList.get(0).getPostalCode();
            String knownName = addressList.get(0).getFeatureName();
        } catch (IOException e) {
            e.printStackTrace();
        }
        */

    }

    @Override
    public void onConnected(Bundle bundle) {
        checkPermissions();
    }

    @Override
    public void onConnectionSuspended(int i) {
        //Do whatever you need
        //You can display a message here
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //You can display a message here
    }

    private void getMyLocation() {

        if (googleApiClient != null) {

            if (googleApiClient.isConnected()) {

                int permissionLocation = ContextCompat.checkSelfPermission(RaiseIssueActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionLocation == PackageManager.PERMISSION_GRANTED) {

                    mylocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
//                    LocationRequest locationRequest = new LocationRequest();
                    LocationRequest locationRequest = LocationRequest.create();
                    locationRequest.setInterval(3000);
                    locationRequest.setFastestInterval(3000);
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                            .addLocationRequest(locationRequest);
                    builder.setAlwaysShow(true);
                    LocationServices.FusedLocationApi
                            .requestLocationUpdates(googleApiClient, locationRequest, this);
                    PendingResult<LocationSettingsResult> result =
                            LocationServices.SettingsApi
                                    .checkLocationSettings(googleApiClient, builder.build());

                    result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

                        @Override
                        public void onResult(LocationSettingsResult result) {
                            final Status status = result.getStatus();
                            switch (status.getStatusCode()) {
                                case LocationSettingsStatusCodes.SUCCESS:
                                    // All location settings are satisfied.
                                    // You can initialize location requests here.
                                    int permissionLocation = ContextCompat
                                            .checkSelfPermission(RaiseIssueActivity.this,
                                                    Manifest.permission.ACCESS_FINE_LOCATION);
                                    if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                                        mylocation = LocationServices.FusedLocationApi
                                                .getLastLocation(googleApiClient);
                                    }
                                    break;
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    // Location settings are not satisfied.
                                    // But could be fixed by showing the user a dialog.
                                    try {
                                        // Show the dialog by calling startResolutionForResult(),
                                        // and check the result in onActivityResult().
                                        // Ask to turn on GPS automatically
                                        status.startResolutionForResult(RaiseIssueActivity.this,
                                                REQUEST_CHECK_SETTINGS_GPS);
                                    } catch (IntentSender.SendIntentException e) {
                                        // Ignore the error.
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    // Location settings are not satisfied.
                                    // However, we have no way
                                    // to fix the
                                    // settings so we won't show the dialog.
                                    // finish();
                                    break;
                            }
                        }
                    });
                }
            }
        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch (requestCode) {
//            case REQUEST_CHECK_SETTINGS_GPS:
//                switch (resultCode) {
//                    case Activity.RESULT_OK:
//                        getMyLocation();
//                        break;
//                    case Activity.RESULT_CANCELED:
//                        finish();
//                        break;
//                }
//                break;
//        }
//    }

    private void checkPermissions() {
        int permissionLocation = ContextCompat.checkSelfPermission(RaiseIssueActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this,
                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            }
        } else {
            getMyLocation();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        int permissionLocation = ContextCompat.checkSelfPermission(RaiseIssueActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
            getMyLocation();
        }
    }


    // the callback received when the user "sets" the date in the dialog
    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {

                public void onDateSet(DatePicker view, int year,
                                      int monthOfYear, int dayOfMonth) {
                    mYear = year;
                    mMonth = monthOfYear;
                    mDay = dayOfMonth;

                    int month = mMonth + 1;
                    edt_stocking_date.setText("" + mDay + "-" + month + "-" + mYear);
                }
            };

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case 1001:
                return new DatePickerDialog(this, mDateSetListener, mYear, mMonth,
                        mDay);
        }
        return null;
    }

    String strComment, userId;
    String ba1_base64 = "", ba2_base64 = "", ba3_base64 = "";
    String currentDateTimeString;

    private void validateRaiseIssueData() {

        if (imgBitmap[2] != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imgBitmap[2].compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] ba = baos.toByteArray();
            ba3_base64 = Base64.encodeBytes(ba);
        }

        if (imgBitmap[1] != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imgBitmap[1].compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] ba = baos.toByteArray();
            ba2_base64 = Base64.encodeBytes(ba);
        }

        if (imgBitmap[0] != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imgBitmap[0].compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] ba = baos.toByteArray();
            ba1_base64 = Base64.encodeBytes(ba);
        }

        if (edt_wtr_area.getText().toString().isEmpty()) {
            edt_wtr_area.setError("Required");
            return;
        }
        if (edt_wtr_depth.getText().toString().isEmpty()) {
            edt_wtr_depth.setError("Required");
            return;
        }
        if (edt_sps_composition.getText().toString().isEmpty()) {
            edt_sps_composition.setError("Required");
            return;
        }
        if (edt_stocking_density.getText().toString().isEmpty()) {
            edt_stocking_density.setError("Required");
            return;
        }
        /*if (edt_stocking_date.getText().toString().equalsIgnoreCase("Date")) {
            edt_stocking_date.setError("Require");
            return;
        }
        if (edt_biomass.getText().toString().isEmpty()) {
            edt_biomass.setError("Require");
            return;
        }*/
        if (edt_fish_weight.getText().toString().isEmpty()) {
            edt_fish_weight.setError("Required");
            return;
        }
        /*if (edt_mor_ep_no.getText().toString().isEmpty()) {
            edt_mor_ep_no.setError("Require");
            return;
        }
        if (edt_wtr_ph.getText().toString().isEmpty()) {
            edt_wtr_ph.setError("Require");
            return;
        }
        if (edt_wtr_alkalinity.getText().toString().isEmpty()) {
            edt_wtr_alkalinity.setError("Require");
            return;
        }*/
        if (edt_detail_desc.getText().toString().isEmpty()) {
            edt_detail_desc.setError("Required");
            return;
        }
        if (edt_med_given.getText().toString().isEmpty()) {
            edt_med_given.setError("Required");
            return;
        }

       /* strComment = edt_detail_desc.getText().toString().trim();
        if (strComment == null || strComment.length() == 0) {
            GlobalData.showSnackBar(bt_submit_issue, "describe your problem", true);
            return;
        }*/


        makeRaiseIssueRequest();

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

    private void makeRaiseIssueRequest() {

        showProgressDialog();

        //currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

        Date date = Calendar.getInstance().getTime();
        //
        // Display a date in day, month, year format
        //
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String today = formatter.format(date);

        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        currentDateTimeString = df.format(c.getTime());

        String issueDate = currentDateTimeString;
        String description = edt_detail_desc.getText().toString().trim();
        String active = "Y";
        String customerId = CommonUtil.getSharePreferenceString(RaiseIssueActivity.this, GlobalData.LOGIN_USER_ID, "0");
        String issueId = "12";
        String attachment1 = ba1_base64;
        String attachment2 = ba2_base64;
        String attachment3 = ba3_base64;
        String waterSpreadArea = edt_wtr_area.getText().toString().trim();
        String waterDepth = edt_wtr_depth.getText().toString().trim();
        String speciesComposition = edt_sps_composition.getText().toString().trim();
        String stockingDensity = edt_stocking_density.getText().toString().trim();
        String stockingDate = edt_stocking_date.getText().toString().trim();
        String biomass = edt_biomass.getText().toString().trim();
        String size = edt_fish_weight.getText().toString().trim();
        String mortality = edt_mor_ep_no.getText().toString().trim();
        String waterPH = edt_wtr_ph.getText().toString().trim();
        String waterAlkalinity = edt_wtr_alkalinity.getText().toString().trim();
        String medication = edt_med_given.getText().toString().trim();

        String url = GlobalData.FILE_URL + GlobalData.SERVICE_SAVE_CUST_ISSUE;

        JSONObject jsObj1 = new JSONObject();

        JSONObject js = new JSONObject();

        try {

            js.put("issueId", issueId);
            js.put("customerId", customerId);
            js.put("issueDate", issueDate);
            js.put("description", description);
//            js.put("assignTo", 0);
//            js.put("assignBy", 0);
//            js.put("solvedStatus", "");
//            js.put("reAssignStatus", "");
            js.put("location", myLocation);
//            js.put("time", "");
            js.put("active", active);
//            js.put("createdBy", "");
//            js.put("createdOn", "");
//            js.put("customerName", "");
//            js.put("companyName", "");
//            js.put("issueName", "");
            js.put("attachment1", attachment1);
            js.put("attachment2", attachment2);
            js.put("attachment3", attachment3);
//            js.put("contactNo", "");
//            js.put("comment", "");
//            js.put("pincode", "");
//            js.put("customerSource", "");
//            js.put("emailId", "");
//            js.put("city", "");
            js.put("waterSpreadArea", waterSpreadArea);
            js.put("waterDepth", waterDepth);
            js.put("speciesComposition", speciesComposition);
            js.put("stockingDensity", stockingDensity);
            js.put("stockingDate", stockingDate);
            js.put("biomass", biomass);
            js.put("size", size);
            js.put("mortality", mortality);
            js.put("waterPH", waterPH);
            js.put("waterAlkalinity", waterAlkalinity);
            js.put("medication", medication);

            jsObj1.put("customerIssue", js);

        } catch (Exception e) {
            e.printStackTrace();
        }


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, jsObj1,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        if (pDialog.isShowing()) {
                            hideProgressDialog();
                        }


                        try {

                            String status = "" + response.getInt("responseCode");
                            String responseMessage = response.getString("responseMessage");

                            if (status.equalsIgnoreCase("500")) {
                                Toast.makeText(context, responseMessage, Toast.LENGTH_LONG).show();
                            }

                            Toast.makeText(context, "" + responseMessage, Toast.LENGTH_LONG).show();

                            if (status.equalsIgnoreCase("200")) {

                                String customerIssueId = "" + response.getInt("customerIssueId");
                                tv_customerIssueId.setText("Token no. : " + customerIssueId);

                                ll_request_issue_form.setVisibility(View.GONE);
                                ll_request_submit.setVisibility(View.VISIBLE);
                                lin_btm.setVisibility(View.GONE);

                                new Thread(new Runnable() {
                                    public void run() {
                                        doWork();
                                        finish();
                                    }
                                }).start();
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

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                GlobalData.REQUEST_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_string_req);
    }



    private void doWork() {
        for (int i = 0; i < 100; i += 50) {
            try {
                Thread.sleep(700);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    //----------------------Raise Issue Image Logic Start----------------------------------

    private File mFileTemp;
    private int buttonClick;

    View.OnClickListener imgClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            buttonClick = Integer.parseInt(v.getTag().toString());
            v.showContextMenu();
        }
    };

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Please Select");
        menu.add(FRAGMENT_GROUPID, v.getId(), 0, "Camera");
        if (buttonClick != 4) {
            menu.add(FRAGMENT_GROUPID, v.getId(), 0, "Gallery");
        }
        menu.add(FRAGMENT_GROUPID, v.getId(), 0, "Delete");
    }

    Uri imageUri;
    private static int PICK_IMAGE;
    private static int PICK_Camera_IMAGE;

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mFileTemp = new File(Environment.getExternalStorageDirectory(), GlobalData.TEMP_PHOTO_FILE_NAME);
        } else {
            mFileTemp = new File(context.getFilesDir(), GlobalData.TEMP_PHOTO_FILE_NAME);
        }

        if (item.getGroupId() == FRAGMENT_GROUPID) {
            if (item.getTitle() == "Camera") {
                if (buttonClick == 4) {
                    String fileName = "video.mp4";
                    // create parameters for Intent with filename
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, fileName);
                    values.put(MediaStore.Images.Media.DESCRIPTION,
                            "Video captured by camera");
                    // imageUri is the current activity attribute, define and
                    // save
                    // it
                    // for later usage (also in onSaveInstanceState)
                    imageUri =
                            getContentResolver()
                                    .insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                                            values);
                    PICK_Camera_IMAGE = BUTTON4_CAMERA;

                    /*
                     * Intent intent = new
                     * Intent(MediaStore.ACTION_VIDEO_CAPTURE); // Add
                     * (optional) extra to save video to our file
                     * intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); //
                     * Optional extra to set video quality
                     * intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 5);
                     * intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 2);
                     * intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
                     * startActivityForResult(intent, PICK_Camera_IMAGE);
                     */

                    Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

                    int size = 3;
                    intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);//0 for low, 1 higth
                    intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30); //10seq
                    intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, size * 1048576L);//X mb *1024*1024
                    intent.putExtra(MediaStore.EXTRA_SHOW_ACTION_ICONS, true);
                    intent.putExtra(
                            MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA, true);
                    startActivityForResult(intent, PICK_Camera_IMAGE);

                } else {

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    try {
                        if (Environment.MEDIA_MOUNTED.equals(state)) {
                            imageUri = Uri.fromFile(mFileTemp);
                            Log.e("tag", "CameraPermission 1 imgUri : " + imageUri.toString());
                        } else {
                            /*
                             * The solution is taken from here:
                             * http://stackoverflow
                             * .com/questions/10042695/how-to
                             * -get-camera-result-as-a-uri-in-data-folder
                             */
                            imageUri = InternalStorageContentProvider.CONTENT_URI;
                            Log.e("tag", "CameraPermission 1 imgUri : " + imageUri.toString());
                        }
                        intent.putExtra(
                                MediaStore.EXTRA_OUTPUT,
                                imageUri);
                        intent.putExtra("return-data", true);
                        switch (buttonClick) {
                            case 1:
                                PICK_Camera_IMAGE = BUTTON1_CAMERA;
                                break;
                            case 2:
                                PICK_Camera_IMAGE = BUTTON2_CAMERA;
                                break;
                            case 3:
                                PICK_Camera_IMAGE = BUTTON3_CAMERA;
                                break;
                        }

                        startActivityForResult(intent, PICK_Camera_IMAGE);

                    } catch (ActivityNotFoundException e) {

                    }

                }
                return true;
            } else if (item.getTitle() == "Gallery") {
                if (buttonClick == 4) {
                    //  openGalleryVideo();
                } else {
                    try {
                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                        photoPickerIntent.setType("image/*");
                        // photoPickerIntent.setAction(Intent.ACTION_GET_CONTENT);
                        switch (buttonClick) {
                            case 1:
                                PICK_IMAGE = BUTTON1_GALARY;
                                break;
                            case 2:
                                PICK_IMAGE = BUTTON2_GALARY;
                                break;
                            case 3:
                                PICK_IMAGE = BUTTON3_GALARY;
                                break;
                            case 4:
                                PICK_IMAGE = BUTTON4_GALARY;
                                break;
                        }
                        startActivityForResult(Intent.createChooser(
                                photoPickerIntent, "Select Image"),
                                PICK_IMAGE);

                        //                        startActivityForResult(intent,requestcode);

                    } catch (Exception e) {

                    }
                }
                return true;
            } else if (item.getTitle() == "Delete") {
                try {
                    switch (buttonClick) {
                        case 1:
                            ib1.setImageResource(R.drawable.button_camera);
                            imgBitmap[0] = null;
                            break;
                        case 2:
                            ib2.setImageResource(R.drawable.button_camera);
                            imgBitmap[1] = null;
                            break;
                        case 3:
                            ib3.setImageResource(R.drawable.button_camera);
                            imgBitmap[2] = null;
                            break;
                        case 4:
                            //   ib4.setImageResource(R.drawable.button_video);
                            //    videoFile = null;
                            break;
                    }
                } catch (Exception e) {

                }
                return true;
            }
        }
        return super.onContextItemSelected(item);
    }

    public static final int REQUEST_CODE_CROP_IMAGE = 0x3;

    private void startCropImage(int REQUEST_ID) {

        Intent intent = new Intent(RaiseIssueActivity.this, CropImage.class);
        intent.putExtra(CropImage.IMAGE_PATH, mFileTemp.getPath());
        intent.putExtra(CropImage.SCALE, false);

        intent.putExtra(CropImage.ASPECT_X, 3);
        intent.putExtra(CropImage.ASPECT_Y, 3);

        startActivityForResult(intent, REQUEST_ID);
    }

    // rounded corner by Ashish

    public Bitmap roundCornerImage(Bitmap src, float round) {
        // Source image size
        int width = src.getWidth();
        int height = src.getHeight();
        // create result bitmap output
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        // set canvas for painting
        Canvas canvas = new Canvas(result);
        canvas.drawARGB(0, 0, 0, 0);

        // configure paint
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);

        // configure rectangle for embedding
        final Rect rect = new Rect(0, 0, width, height);
        final RectF rectF = new RectF(rect);

        // draw Round rectangle to canvas
        canvas.drawRoundRect(rectF, round, round, paint);

        // create Xfer mode
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        // draw source image to canvas
        canvas.drawBitmap(src, rect, rect, paint);

        // return final image
        return result;
    }

    public static void copyStream(InputStream input, OutputStream output)
            throws IOException {

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Uri selectedImageUri = null;

        switch (requestCode) {
            case BUTTON1_GALARY:
            case BUTTON2_GALARY:
            case BUTTON3_GALARY:
                if (resultCode == Activity.RESULT_OK) {
                    selectedImageUri = data.getData();
                    if (selectedImageUri != null) {
                        try {
                            // OI FILE Manager

                            InputStream inputStream = context.getContentResolver()
                                    .openInputStream(data.getData());
                            FileOutputStream fileOutputStream = new FileOutputStream(
                                    mFileTemp);
                            copyStream(inputStream, fileOutputStream);
                            fileOutputStream.close();
                            inputStream.close();

                            if (mFileTemp != null) {
                                switch (requestCode) {

                                    case BUTTON1_GALARY:
                                        startCropImage(CROP_FROM_CAMERA1);
                                        break;

                                    case BUTTON2_GALARY:
                                        startCropImage(CROP_FROM_CAMERA2);
                                        break;

                                    case BUTTON3_GALARY:
                                        startCropImage(CROP_FROM_CAMERA3);
                                        break;
                                }
                            }

                        } catch (Exception e) {
                            Toast.makeText(context, "Internal error",
                                    Toast.LENGTH_LONG).show();

                        }
                    }
                }
                break;
            case BUTTON4_GALARY:
                if (resultCode == Activity.RESULT_OK) {
                    //  getVideoPath(data.getData());
                    return;
                }
                break;
            case BUTTON1_CAMERA:
                Log.e("tag", "BUTTON1_CAMERA result : " + resultCode);
                if (data != null) {
                    Log.e("tag", "BUTTON1_CAMERA result not null ");
                }
                if (resultCode == RESULT_OK) {
                    // use imageUri here to access the image
                    selectedImageUri = imageUri;
                    startCropImage(CROP_FROM_CAMERA1);

                } else if (resultCode == RESULT_CANCELED) {
                    Log.e("tag", " RESULT_CANCELED ");
                } else {
                    Log.e("tag", " ELSE ");
                }
                break;
            case BUTTON2_CAMERA:
                if (resultCode == RESULT_OK) {
                    // use imageUri here to access the image
                    selectedImageUri = imageUri;
                    startCropImage(CROP_FROM_CAMERA2);

                }
                break;
            case BUTTON3_CAMERA:
                if (resultCode == RESULT_OK) {
                    // use imageUri here to access the image
                    selectedImageUri = imageUri;
                    startCropImage(CROP_FROM_CAMERA3);
                }
                break;
            case BUTTON4_CAMERA:
                if (data != null) {
                    if (resultCode == Activity.RESULT_OK) {
                        //   getVideoPath(data.getData());
                        return;
                    }
                }
                break;

            case CROP_FROM_CAMERA1:
                if (data != null) {

                    String path = data.getStringExtra(CropImage.IMAGE_PATH);
                    if (path == null) {
                        return;
                    }

                    imgBitmap[0] = BitmapFactory.decodeFile(mFileTemp.getPath());
                    //  ib1.setImageBitmap(imgBitmap[0]);
                    ib1.setImageBitmap(roundCornerImage(imgBitmap[0], 35));
                    // ib2.setEnabled(true);
                    // fib2.setVisibility(View.VISIBLE);

                }
                break;

            case CROP_FROM_CAMERA2:
                if (data != null) {
                    String path = data.getStringExtra(CropImage.IMAGE_PATH);
                    if (path == null) {
                        return;
                    }

                    imgBitmap[1] = BitmapFactory.decodeFile(mFileTemp.getPath());
                    // ib2.setImageBitmap(imgBitmap[1]);
                    ib2.setImageBitmap(roundCornerImage(imgBitmap[1], 35));
                    ib3.setEnabled(true);
                    fib3.setVisibility(View.VISIBLE);
                }
                break;

            case CROP_FROM_CAMERA3:
                if (data != null) {
                    String path = data.getStringExtra(CropImage.IMAGE_PATH);
                    if (path == null) {
                        return;
                    }

                    imgBitmap[2] = BitmapFactory.decodeFile(mFileTemp.getPath());
                    //  ib3.setImageBitmap(imgBitmap[2]);
                    ib3.setImageBitmap(roundCornerImage(imgBitmap[2], 35));
                }
                break;

            case REQUEST_CHECK_SETTINGS_GPS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        getMyLocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        finish();
                        break;
                }
                break;
        }
    }

    /*-----------Raise Issue Image Logic End-----------------------*/

    public void toastMessage(String msg) {
        Toast.makeText(RaiseIssueActivity.this, "" + msg, Toast.LENGTH_SHORT).show();
    }


    public void logout(final Context context) {

        String yes = getResources().getString(R.string.yes);
        String no = getResources().getString(R.string.no);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);
        alertDialogBuilder.setMessage(R.string.logout_msg);
        alertDialogBuilder.setPositiveButton(yes,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        String type = CommonUtil.getSharePreferenceString(context, GlobalData.LOGIN_USER_TYPE, "N/A");

                        if (type.equalsIgnoreCase(GlobalData.USING_GOOGLE)) {

                            googleSignOut();

                            //for miantaining the log in session
                            CommonUtil.setSharePreferenceString(RaiseIssueActivity.this, GlobalData.APP_LOGIN_STATUS, GlobalData.LOGED_OUT);

                            context.startActivity(new Intent(context, LoginActivity.class));

                        } else if (type.equalsIgnoreCase(GlobalData.USING_MOBILE_NO)) {

                            //for miantaining the log in session
                            CommonUtil.setSharePreferenceString(RaiseIssueActivity.this, GlobalData.APP_LOGIN_STATUS, GlobalData.LOGED_OUT);

                            context.startActivity(new Intent(context, LoginActivity.class));
                            finish();

                            //     makeLogoutRquest(userId, imei);

                        } else {
                            //for miantaining the log in session
                            CommonUtil.setSharePreferenceString(RaiseIssueActivity.this, GlobalData.APP_LOGIN_STATUS, GlobalData.LOGED_OUT);
                            context.startActivity(new Intent(context, LoginActivity.class));
                            finish();

                            //     makeLogoutRquest(userId, imei);

                        }
                    }
                });
        alertDialogBuilder.setNegativeButton(no,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    /*sign out*/

    private void googleSignOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {

                    }
                });
    }

}