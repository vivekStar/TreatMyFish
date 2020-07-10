package com.webfarms.treatmyfish.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.webfarms.treatmyfish.FishDash;
import com.webfarms.treatmyfish.R;
import com.webfarms.treatmyfish.app.AppController;
import com.webfarms.treatmyfish.bean.BeanIssueList;
import com.webfarms.treatmyfish.database.TableIssueList;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLConnection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Ashish Zade on 2/14/2017 & 10:39 AM.
 */

public class GlobalData {

    //web service url.

    public static final String FILE_URL = "http://188.95.36.104:8080/CIFA/service/";
    public static final String ATTACHMENT_PATH = "http://188.95.36.104:8080/CIFA/resources/attachment/";

//    public static final String FILE_URL = "http://192.168.1.6:8080/ZoomCRM/service/";
//    public static final String ATTACHMENT_PATH = "http://192.168.1.6:8080/ZoomCRM/resources/attachment/";

    public static String tag_string_req5 = "string_req5";
    public static String TAG = FishDash.class.getSimpleName();

    public static String message = "";
    public static final String ERR_NETWORK_NO_CONNECTION = "Can't connect to the Internet. Please check your mobile data or Wifi Connection";
    public static final String TEMP_PHOTO_FILE_NAME = "temp_photo.jpg";

    public static final String SHARE_USER_ID = "SHARE_USER_ID";
    public static final String SHARE_MAIL_ADDRESS = "SHARE_MAIL_ADDRESS";
    public static final String SHARE_USER_NAME = "SHARE_USER_NAME";
    public static final String SHARE_MOBILE_NUMBER = "SHARE_MOBILE_NUMBER";

    public static final String USING_GOOGLE = "LOGIN_GOOGLE_TYPE";
    public static final String USING_FACEBOOK = "USING_FACEBOOK";
    public static final String USING_MOBILE_NO = "USING_MOBILE_NO";
    public static final String USING_USERID_PASSWORD = "USING_USERID_PASSWORD";

    public static final String FLAG_NEW = "NEW";
    public static final String FLAG_REGISTERED = "REGISTERED";

    public static final String LOGIN_USER_ID = "LOGIN_USER_ID";
    public static final String LOGIN_USER_TYPE = "LOGIN_USER_TYPE";
    public static final String LOGIN_USER_EMAIL_ID = "LOGIN_USER_EMAIL_ID";
    public static final String LOGIN_USER_NAME = "LOGIN_USER_NAME";
    public static final String LOGIN_USER_PHOTOURL = "LOGIN_USER_PHOTOURL";
    public static final String LOGIN_USER_MOBILE_NUMBER = "LOGIN_USER_MOBILE_NUMBER";

    public static final String APP_LOGIN_STATUS = "APP_LOGIN_STATUS";
    public static final String LOGED_IN = "LOGED_IN";
    public static final String LOGED_OUT = "LOGED_OUT";

    public static final String IMAGE_COUNT = "IMAGE_COUNT";
    public static final String TEXT_COUNT = "TEXT_COUNT";

    public static final String NOTIFICATION_STATUS = "NOTIFICATION_STATUS";
    public static final String NOTIFIACTION_SEEN = "NOTIFIACTION_SEEN";
    public static final String NOTIFIACTION_UNSEEN = "NOTIFIACTION_UNSEEN";

    public static final String USER_TOKEN = "USER_TOKEN";
    public static final String API_KEY = "abcd";

    public static final String LANGUAGE_SELECTED = "LANGUAGE_SELECTED";
    public static final String LANGUAGE = "language";
    public static final String HINDI_LANGUAGE = "hindi";
    public static final String ORIYA_LANGUAGE = "oriya";
    public static final String ENGLISH_LANGUAGE = "english";
    public static final String YES = "YES";
    public static final String NO = "NO";

    public static final String COUNTER = "COUNTER";
    public static final String OLD_COUNTER = "OLD_COUNTER";
    public static final String NEW_COUNTER = "NEW_COUNTER";

    public static final String IMAGE_INITIALIZED = "IMAGE_INITIALIZED";
    public static final String YES_INITIALIZED = "YES_INITIALIZED";
    public static final String NOT_INITIALIZED = "NOT_INITIALIZED";

//    public static final String IMG_DOWNLD_STATUS = "IMG_DOWNLD_STATUS";
//    public static final String DOWNLD_COMPLETE = "DOWNLD_COMPLETE";
//    public static final String DOWNLD_IN_COMPLETE = "DOWNLD_IN_COMPLETE";

    /*89898989898*/

    public static final String SIGNED_UP_STATUS = "SIGNED_UP_STATUS";
    public static final String STATUS_SIGNUP_COMPLETED = "STATUS_SIGNUP_COMPLETED";
    public static final String STATUS_SIGNUP_IN_COMPLETE = "STATUS_SIGNUP_IN_COMPLETE";

    public static final int REQUEST_SOCKET_TIMEOUT_MS = 0;

    // service name
    public static final String SERVICE_USER_LOGIN = "login/loginCustomerViaApp";
    public static final String SERVICE_ISSUE_LIST = "issuemaster/getIssueList";
    public static final String SERVICE_SAVE_CUST_ISSUE = "customerissue/saveCustomerIssue";
    public static final String SERVICE_GET_CUST_ISSUE = "customerissue/getIssueHistory";
    public static final String SERVICE_GET_AMC_DETAILS = "amcmaster/getAMCDetails";
    public static final String SERVICE_GET_PROMOTIONS = "deals/getAll";
    public static final String SERVICE_GET_NEW_ISSUE_ENGG = "customerissue/getCustomerIssueByAssignTo";
    public static final String SERVICE_ENQUIRY_FORM = "enquiry/saveEnquiry";

    public static final String SERVICE_UPDATE_PROFILE = "customer/updateProfileInfo";
    public static final String SERVICE_OTP_REQUEST = "customer/otprequest";
    public static final String SERVICE_VERIFY_OTP = "customer/otpverify";
    public static final String SERVICE_UPDATE_VIA_OTP = "customer/updateViaOTP";
    public static final String SERVICE_UPDATE_VIA_SOCIAL = "customer/socialMedia";
    public static final String SERVICE_VERIFY_SOCIAL = "customer/verifySocial";

    public static final String SERVICE_GET_ALL_CATEGORY = "content/getAllCategory";
    public static final String SERVICE_GET_ALL_SUB_CATEGORY = "content/getAllSubCategory";
    public static final String SERVICE_GET_ALL_TOPICS_LIST = "content/getAllTopics";
    public static final String SERVICE_GET_ALL_IMAGES = "content/getImages";
    public static final String SERVICE_GET_ALL_IMAGES_STATUS = "content/getImageStatus";
    public static final String SERVICE_GET_ALL = "content/getAll";
    public static final String SERVICE_GET_IMG_COUNTER = "content/checkImageCounter";
    public static final String SERVICE_GET_IMAGES = "content/getImages";
    //    public static final String SERVICE_CHECK_COUNTER = "content/getUpdateCounter";
    public static final String SERVICE_GET_ABOUT_US = "content/getAboutUs";
    public static final String SERVICE_GET_STATE_LIST = "stateList";
    public static final String SERVICE_GET_DISTRICT_LIST = "districtList";
    public static final String SERVICE_GET_CITY_LIST = "cityList";

    /*Validation Constants*/
    public static final int VALID = 1;
    public static final int INVALID = 0;
    public static final int INVALID_ENTRY = -1;
    public static final int INVALID_LENGTH = -2;
    public static final int INVALID_START_WITH_ZERO = -3;
    public static final int INVALID_ENTRY_PASS = -4;
    public static final int INVALID_ENTRY_SIX = -5;
    public static final int INVALID_ENTRY_CONF_PASS = -6;
    public static final int INVALID_PASS_MISMATCH = -7;

    public static final String GROSS_PHOTOGRAPH = "Gross Photographs";
    public static final String MICRO_PHOTOGRAPH = "Microscopic Photographs";


    public static void printError(String message, Exception e) {
        e.printStackTrace();
        System.out.println(message);
    }

    public static void printError(Exception e) {
        e.printStackTrace();
        System.out.println(message);
    }

    public static Toolbar initToolBar(final AppCompatActivity c, String title, boolean showBack) {

        Toolbar toolbar = (Toolbar) c.findViewById(R.id.app_bar);
        if (title != null) {
            toolbar.setTitle(title);
        }
        c.setSupportActionBar(toolbar);

        if (showBack) {
            c.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //           c.getSupportActionBar().setLogo(R.drawable.ic_launcher);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    c.onBackPressed();
                }
            });
        } else {
            c.getSupportActionBar().setDisplayShowHomeEnabled(true);
            //  c.getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        }

        c.getSupportActionBar().setHomeButtonEnabled(true);

        return toolbar;
    }

    public static void showSnackBar(View view, String message, boolean flag) {

        if (message.length() > 0) {
            Snackbar.make(view, message, Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
    }


    public static void showNoResponseDialog(final Context context) {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_no_response);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


        ((AppCompatButton) dialog.findViewById(R.id.bt_no_dt_bk)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), ((AppCompatButton) v).getText().toString() + " Clicked", Toast.LENGTH_SHORT).show();
//                dialog.dismiss();
                ((Activity) context).finish();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    public static void showSeverErrorDialog(final Context context) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_server_error);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


        ((AppCompatButton) dialog.findViewById(R.id.bt_no_sr_bk)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), ((AppCompatButton) v).getText().toString() + " Clicked", Toast.LENGTH_SHORT).show();
//                dialog.dismiss();
                ((Activity) context).finish();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    public static String getDeviceName() {

        try {
            String manufacturer = Build.MANUFACTURER;
            String model = Build.MODEL;
            int version = Build.VERSION.SDK_INT;

            if (model.startsWith(manufacturer)) {
                return capitalize(model);
            } else {
                return capitalize(manufacturer) + " " + model;
            }
        } catch (Exception e) {
            // TODO: handle exception
        }

        return "";

    }

    public static String getDeviceOS() {
        String os = "";
        try {

            int version = Build.VERSION.SDK_INT;
            os = "" + version;

        } catch (Exception e) {
            // TODO: handle exception
        }

        return os;

    }

    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    /*
    public static void logout(final Context context) {

        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(
                context);
        alertDialogBuilder.setMessage(R.string.logout_msg);
        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        String type = CommonUtil.getSharePreferenceString(context, GlobalData.LOGIN_USER_TYPE, "N/A");

                        logMessage(type);

                        if(type.equalsIgnoreCase(GlobalData.USING_GOOGLE)){

                            SharedPreferences setting = PreferenceManager.getDefaultSharedPreferences(context);
                            SharedPreferences.Editor edit = setting.edit();
                            edit.putString(GlobalData.LOGIN_USER_TYPE, "0");
                            edit.putString(GlobalData.LOGIN_USER_NAME, "0");
                            edit.putString(GlobalData.LOGIN_USER_EMAIL_ID, "0");
                            edit.putString(GlobalData.LOGIN_USER_PHOTOURL, "0");
                            edit.commit();

                        }else{

                            SharedPreferences setting = PreferenceManager.getDefaultSharedPreferences(context);
                            SharedPreferences.Editor edit = setting.edit();
                            edit.putString(GlobalData.SHARE_USER_ID, "0");
                            edit.putString(GlobalData.SHARE_MAIL_ADDRESS, "0");

                            edit.commit();

                            //     makeLogoutRquest(userId, imei);

                        }
                    }
                });
        alertDialogBuilder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {

                    }
                });

        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    */

    public static void getSplashDataSync(Context context) {

        try {
            makeGetIssueListRequestSync(context);
        } catch (Exception e) {
            GlobalData.printError(e);
        }
    }

    public static String status = "";

    private static void makeGetIssueListRequestSync(final Context context) {

        //   http://zoomcrmrestcontroller.whelastic.net/service/issuemaster/getIssueList

        String url = GlobalData.FILE_URL + GlobalData.SERVICE_ISSUE_LIST;


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                String str = response.toString();

                LinkedList<BeanIssueList> issueList = new LinkedList<BeanIssueList>();

                try {
                    status = "" + response.getInt("responseCode");
                } catch (Exception e) {


                }
                if (status.equalsIgnoreCase("200")) {

                    try {

                        JSONArray issueArray = response.getJSONArray("issueList");

                        for (int i = 0; i < issueArray.length(); i++) {
                            try {
                                BeanIssueList bean = new BeanIssueList();
                                JSONObject obj = issueArray.getJSONObject(i);
                                bean.setIssueId(obj.getInt("issueId"));
                                bean.setIssueName(obj.getString("issueName"));
                                bean.setActive(obj.getString("active"));
                                bean.setCreatedOn(obj.getString("createdOn"));

                                issueList.add(bean);

                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }

                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    try {
                        TableIssueList issTable = new TableIssueList(context);
                        if (issueList.size() > 0) {
                            issTable.deleteAllCategory();
                            issTable.insertAll(issueList);
                        }

                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                } else {

                    try {
                        String message = response.getString("status");
                        //    GlobalData.showSnackBar(view, message, true);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
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
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_string_req5);

    }

    public static String getFileExt(String file) {
        String extension = file.substring(file.lastIndexOf("."));
        return extension.toUpperCase();
    }

    public static int checkFileSizeFromURL(String url) {
        CommonUtil.printMessage(url);
        URLConnection urlConnection = null;
        try {
            java.net.URL myUrl = new java.net.URL(url);
            urlConnection = myUrl.openConnection();

            urlConnection.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return urlConnection.getContentLength();
    }


    public static int validateEmail(String hex) {
        Pattern pattern;
        Matcher matcher;

        String EMAIL_PATTERN =
                "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";


        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(hex);
        if (matcher.matches()) {
            return VALID;
        } else {
            return INVALID;
        }
    }


    public static int validatePwdLength(String password) {

        if (password == null || password.length() == 0) {
            return INVALID_ENTRY_PASS;
        }

        if (password.length() < 6) {
            return INVALID_ENTRY_SIX;
        }

        return VALID;
    }


    public static int validateMobileNo(String mobileNo) {

        if (mobileNo == null || mobileNo.length() == 0) {
            return INVALID_ENTRY_PASS;
        } else if (mobileNo.length() != 10) {
            return INVALID_ENTRY_SIX;
        } else if (mobileNo.substring(0, 1).equals("0")) {
            return INVALID_START_WITH_ZERO;
        }

        Pattern p = Pattern.compile("[0987654321]");

        boolean b = p.matcher(mobileNo).find();

        if (b)
            return VALID;
        else
            return INVALID;
    }


    public static int validateABC(String text) {

        Pattern pattern;
        Matcher matcher;

        String TEXT_PATTERN = "^[a-zA-Z]+$";
        pattern = Pattern.compile(TEXT_PATTERN);
        matcher = pattern.matcher(text);
        if (matcher.matches()) {
            return VALID;
        } else {
            return INVALID;
        }
    }

    // Method to manually check connection status
    public static boolean checkConnection(Context context, View view) {

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
//        showSnack(haveConnectedWifi || haveConnectedMobile, view);

        return haveConnectedWifi || haveConnectedMobile;
    }


}
