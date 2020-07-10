package com.webfarms.treatmyfish;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.webfarms.treatmyfish.adapter.CategoryGridAdapter;
import com.webfarms.treatmyfish.bean.BeanCategory;
import com.webfarms.treatmyfish.utils.CommonUtil;
import com.webfarms.treatmyfish.utils.GlobalData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class FishDash extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private ActionBar actionBar;
    private Toolbar toolbar;

    private static Context context;
    private GoogleApiClient mGoogleApiClient;
    private LinearLayout llRaiseDiseases;

    public ImageView getToolbar_logo() {
        return toolbar_logo;
    }

    private ImageView toolbar_logo;
    private RecyclerView recyclerView;
    private RequestQueue requestQueue;
    private ProgressDialog pDialog;
    private ArrayList<BeanCategory> categoryArrayList;
    private static final int PERMISSIONS_REQUEST_CAMERA = 10;

    private boolean isInternetAvailable = false;
    private boolean isCategoryListAvail = false;

    //    private DatabaseHelper helper;
    private int imgCounter = 0;

    private static FishDash INSTANCE = null;

    public static FishDash getInstance() {
        return INSTANCE;
    }


    @Override
    protected void onResume() {
        super.onResume();
        INSTANCE = this;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        INSTANCE = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fish_dash);

        initialize();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getResources().getString(R.string.app_name));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        getDiseasesList();

        toolbar_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, HistoryActivity.class));
            }
        });

    }


    //    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_profile) {
            startActivity(new Intent(context, ProfileActivity.class));
        } else if (id == R.id.nav_history) {
            startActivity(new Intent(context, HistoryActivity.class));
        } else if (id == R.id.nav_abt_us) {
            startActivity(new Intent(context, AboutUsActivity.class));
        } else if (id == R.id.nav_logout) {
            String type = CommonUtil.getSharePreferenceString(context, GlobalData.LOGIN_USER_TYPE, "N/A");
            if (type.equalsIgnoreCase(GlobalData.USING_GOOGLE)) {
                googleSignOut();
            } else {
                logout(context);
            }
        } else if (id == R.id.nav_chglang) {
            CommonUtil.setSharePreferenceString(context, GlobalData.LANGUAGE_SELECTED, GlobalData.NO);
            CommonUtil.setSharePreferenceString(context, GlobalData.LANGUAGE, GlobalData.ENGLISH_LANGUAGE);

            Intent intentS = new Intent(context, SelectLanguageActivity.class);
            intentS.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intentS);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void initialize() {

        context = FishDash.this;

//        helper = new DatabaseHelper(context);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
//        recyclerView.addItemDecoration(new SpacingItemDecoration(2, Tools.dpToPx(this, 4), true));
        recyclerView.setHasFixedSize(true);

        llRaiseDiseases = findViewById(R.id.llRaiseDiseases);

        llRaiseDiseases.setOnClickListener(this);

        toolbar_logo = findViewById(R.id.toolbar_logo);

        requestQueue = Volley.newRequestQueue(context);

        pDialog = new ProgressDialog(context);
        pDialog.setIndeterminate(true);
        pDialog.setTitle(getString(R.string.fetching_data));
        pDialog.setMessage(getString(R.string.please_wait));
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setCancelable(false);

        categoryArrayList = new ArrayList<>();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        String notificationStatus = CommonUtil.getSharePreferenceString(getApplicationContext(), GlobalData.NOTIFICATION_STATUS, "0");

        if (notificationStatus.equalsIgnoreCase(GlobalData.NOTIFIACTION_UNSEEN)) {
            toolbar_logo.setImageResource(R.drawable.bell_red);
        } else {
            toolbar_logo.setImageResource(R.drawable.bell);
        }

    }

    private void initToolbar() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Drawer News");

    }

    private void initNavigationMenu() {

        NavigationView nav_view = (NavigationView) findViewById(R.id.nav_view);
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        drawer.setDrawerListener(toggle);
        toggle.syncState();

        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(final MenuItem item) {
                Toast.makeText(getApplicationContext(), item.getTitle() + " Selected", Toast.LENGTH_SHORT).show();
                actionBar.setTitle(item.getTitle());
                drawer.closeDrawers();
                return true;
            }
        });

        // open drawer at start
        drawer.openDrawer(GravityCompat.START);
    }


    private boolean checkPermission() {

        int result = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED;

    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION, CAMERA, WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case PERMISSIONS_REQUEST_CAMERA:
                if (grantResults.length > 0) {

                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[2] == PackageManager.PERMISSION_GRANTED;

                    if (locationAccepted && cameraAccepted && storageAccepted) {
//                        Snackbar.make(llRaiseDiseases, "Permission Granted, Now you can access location data and camera and storage.", Snackbar.LENGTH_LONG).show();
                        Intent raise = new Intent(context, RaiseIssueActivity.class);
                        startActivity(raise);
                    } else {
                        Snackbar.make(llRaiseDiseases, "To use this functionality, You need to grant all the permissions.", Snackbar.LENGTH_LONG).show();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{ACCESS_FINE_LOCATION, CAMERA},
                                                            PERMISSIONS_REQUEST_CAMERA);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }

                break;
        }
    }


    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


    private void googleSignOut() {

        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        logout(context);
                    }
                });
    }


    //this is dashboard

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
    }


    @Override
    protected void onRestart() {
        super.onRestart();

        String notificationStatus = CommonUtil.getSharePreferenceString(getApplicationContext(), GlobalData.NOTIFICATION_STATUS, "0");

        if (notificationStatus.equalsIgnoreCase(GlobalData.NOTIFIACTION_UNSEEN)) {
            toolbar_logo.setImageResource(R.drawable.bell_red);
        } else {
            toolbar_logo.setImageResource(R.drawable.bell);
        }
    }

    private Boolean exit = false;

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

        if (exit) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
        } else {

//            GlobalData.showSnackBar(btnBacterial, "Press back again to exit.", true);
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);
        }
    }

    private static final String TAG = "FishDash";

    public void getDiseasesList() {

        String language = CommonUtil.getSharePreferenceString(context, GlobalData.LANGUAGE, GlobalData.ENGLISH_LANGUAGE);

//        public static final String SERVICE_UPDATE_VIA_OTP = "content/getAllCategory";
        final String urlFarmerInfo = GlobalData.FILE_URL + GlobalData.SERVICE_GET_ALL_CATEGORY;

        showProgressDialog();

        JSONObject json = new JSONObject();

        try {

            json.put("language", language);

        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.e(TAG, "getDiseasesList : " + json.toString());

        JsonObjectRequest jsonContactUploadReq = new JsonObjectRequest(Request.Method.POST, urlFarmerInfo, json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (pDialog.isShowing()) {
                            hideProgressDialog();
                        }

                        Log.e(TAG, "onResponse: " + response);

                        try {
                            String responseCode = response.getString("responseCode");

                            if (responseCode.equalsIgnoreCase("200")) {

                                JSONArray categoryList = response.getJSONArray("categoryList");

                                for (int i = 0; i < categoryList.length(); i++) {

                                    JSONObject json = categoryList.getJSONObject(i);
                                    String categoryName = json.getString("categoryName");
                                    String categoryId = json.getString("categoryId");

                                    BeanCategory category = new BeanCategory(categoryId, categoryName);

                                    categoryArrayList.add(category);

                                    setListView();
                                }

                                isCategoryListAvail = true;
                            }
                            if (responseCode.equalsIgnoreCase("402")) {
//                                Toast.makeText(context, "List not available..!", Toast.LENGTH_SHORT).show();
                                GlobalData.showNoResponseDialog(context);

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

    private void setListView() {

        CategoryGridAdapter adapter = new CategoryGridAdapter(context, categoryArrayList);
        recyclerView.setAdapter(adapter);

        // on item list clicked
        adapter.setOnItemClickListener(new CategoryGridAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, BeanCategory obj, int position) {
//                Snackbar.make(llRaiseDiseases, "Item " + position + " clicked", Snackbar.LENGTH_SHORT).show();
                if (categoryArrayList.size() > 0) {
                    Intent intentBacteria = new Intent(context, FishDiseaseListActivity.class);
                    intentBacteria.putExtra("disease", categoryArrayList.get(position).getCategory());
                    intentBacteria.putExtra("categoryId", categoryArrayList.get(position).getId());
                    startActivity(intentBacteria);
                } else {
                    Toast.makeText(context, R.string.server_not_responding, Toast.LENGTH_LONG).show();
                    Toast.makeText(context, R.string.try_after_time, Toast.LENGTH_LONG).show();
                    getDiseasesList();
                }
            }
        });

    }

    // Method to manually check connection status
    private void checkConnection() {

        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        showSnack(haveConnectedWifi || haveConnectedMobile);
    }

    // Showing the status in Snackbar
    private void showSnack(boolean isConnected) {
        String message;
        int color;
        if (isConnected) {
            message = "Good! Connected to Internet";
            color = Color.WHITE;
            isInternetAvailable = true;
        } else {
            message = "No internet connction";
            color = Color.RED;
            isInternetAvailable = false;
        }

        /*
        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.llRaiseDiseases), message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
        */
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


    public void logout(final Context context) {

        String yes = getResources().getString(R.string.yes);
        String no = getResources().getString(R.string.no);

        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(
                context);
        alertDialogBuilder.setMessage(R.string.logout_msg);
        alertDialogBuilder.setPositiveButton(yes,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        String type = CommonUtil.getSharePreferenceString(context, GlobalData.LOGIN_USER_TYPE, "N/A");

                        if (type.equalsIgnoreCase(GlobalData.USING_GOOGLE)) {

                            //for miantaining the log in session
                            CommonUtil.setSharePreferenceString(context, GlobalData.APP_LOGIN_STATUS, GlobalData.LOGED_OUT);
                            context.startActivity(new Intent(context, LoginActivity.class));
                            finish();

                        } else if (type.equalsIgnoreCase(GlobalData.USING_MOBILE_NO)) {

                            //for miantaining the log in session
                            CommonUtil.setSharePreferenceString(context, GlobalData.APP_LOGIN_STATUS, GlobalData.LOGED_OUT);

                            context.startActivity(new Intent(context, LoginActivity.class));
                            finish();

                            //     makeLogoutRquest(userId, imei);

                        } else {

                            //for miantaining the log in session
                            CommonUtil.setSharePreferenceString(context, GlobalData.APP_LOGIN_STATUS, GlobalData.LOGED_OUT);
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

        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    @Override
    public void onClick(View v) {
        checkConnection();
        if (!isInternetAvailable) {
//            toastMessage("Turn on internet first");
            return;
        }

        switch (v.getId()) {
            case R.id.llRaiseDiseases:

                if (checkPermission()) {
                    Intent raise = new Intent(context, RaiseIssueActivity.class);
                    startActivity(raise);
                } else {
                    Snackbar.make(llRaiseDiseases, "Please grant permissions.", Snackbar.LENGTH_LONG).show();
                    requestPermission();
                }
                break;
        }
    }


    public static boolean isActivityRunning() {

        /*boolean isActivityFound;

        ActivityManager activityManager = (ActivityManager)FishDash.this.getSystemService (Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> activitys = activityManager.getRunningTasks(Integer.MAX_VALUE);
        isActivityFound = false;
        for (int i = 0; i < activitys.size(); i++) {
            if (activitys.get(i).topActivity.toString().equalsIgnoreCase("ComponentInfo{com.example.testapp/com.example.testapp.Your_Activity_Name}")) {
                isActivityFound = true;
            }
        }
        return isActivityFound;*/

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (ActivityManager.RunningTaskInfo task : tasks) {
            if (context.getPackageName().equalsIgnoreCase(task.baseActivity.getPackageName()))
                return true;
        }

        return false;

    }

}
