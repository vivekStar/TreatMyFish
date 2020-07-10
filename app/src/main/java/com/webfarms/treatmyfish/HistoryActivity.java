package com.webfarms.treatmyfish;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.webfarms.treatmyfish.app.AppController;
import com.webfarms.treatmyfish.bean.BeanCustomerIssue;
import com.webfarms.treatmyfish.utils.CommonUtil;
import com.webfarms.treatmyfish.utils.GlobalData;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Ashish Zade on 2/15/2017 & 11:54 AM.
 */
public class HistoryActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private Context context;
    private Toolbar toolbar;
    private ProgressDialog pDialog;
    private ListView lv_history;

    private String tag_string_req = "string_req";
    private String TAG = HistoryActivity.class.getSimpleName();

    private List<BeanCustomerIssue> mCustIssueList;
    private HistoryAdapter addAdapter;

    private GoogleApiClient mGoogleApiClient;
    private LinearLayout ll_btm_home, ll_btm_profile, ll_btm_history, ll_btm_logout,ll_btm_aboutus;
    private LinearLayout lyt_progress,ll_request_issue_form;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        context = HistoryActivity.this;
        toolbar = GlobalData.initToolBar(this, getString(R.string.history), true);

        pDialog = new ProgressDialog(this);
        pDialog.setIndeterminate(true);
        pDialog.setMessage(getString(R.string.loading_history));
        pDialog.setCancelable(false);

        CommonUtil.setSharePreferenceString(getApplicationContext(), GlobalData.NOTIFICATION_STATUS, GlobalData.NOTIFIACTION_SEEN);

        initActivity();

    }

    private void initActivity() {

        lv_history = findViewById(R.id.lv_history);
        lyt_progress = findViewById(R.id.lyt_progress);
        ll_request_issue_form = findViewById(R.id.ll_request_issue_form);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        // Bottom filter menu
        ll_btm_home = findViewById(R.id.ll_btm_home);
        ll_btm_profile = findViewById(R.id.ll_btm_profile);
        ll_btm_history = findViewById(R.id.ll_btm_history);
        ll_btm_logout = findViewById(R.id.ll_btm_logout);
        ll_btm_aboutus = findViewById(R.id.ll_btm_aboutus);

        // Bottom filter On Click

        ll_btm_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, ProfileActivity.class));
            }
        });

        ll_btm_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(context, HistoryActivity.class));
//                finish();
            }
        });

        ll_btm_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String type = CommonUtil.getSharePreferenceString(context, GlobalData.LOGIN_USER_TYPE, "0");

                if (type.equalsIgnoreCase(GlobalData.USING_GOOGLE)) {
                    googleSignOut();
                } else if (type.equalsIgnoreCase(GlobalData.USING_MOBILE_NO)) {
                    logout(context);
                } else if (type.equalsIgnoreCase(GlobalData.USING_FACEBOOK)) {
                    logout(context);
                }
            }
        });

        ll_btm_home.setOnClickListener(new View.OnClickListener() {
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

        callWebService();

    }

    private void callWebService() {
        makeHistoryRequest();
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

    private void makeHistoryRequest() {

//        showProgressDialog();

        String url = GlobalData.FILE_URL + GlobalData.SERVICE_GET_CUST_ISSUE;

        String userId = CommonUtil.getSharePreferenceString(HistoryActivity.this, GlobalData.LOGIN_USER_ID, "0");


        Map<String, Object> postParam = new HashMap<String, Object>();

        JSONObject js = new JSONObject();
        try {

            js.put("api_key", GlobalData.API_KEY);
            js.put("customerId", userId);

        } catch (Exception e) {
            e.printStackTrace();
        }


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, js,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        lyt_progress.setVisibility(View.GONE);
                        ll_request_issue_form.setVisibility(View.VISIBLE);


                        try {
                            String status = "" + response.getInt("responseCode");

                            if (status.equalsIgnoreCase("500")) {
                                //    Toast.makeText(context,R.string.err_login, Toast.LENGTH_LONG).show();
                            }

                            // customerIssueList

                            if (status.equalsIgnoreCase("200")) {

                                String customerIssueList = "" + response.getJSONArray("customerIssueList");

                                //    Toast.makeText(context, "" + responseMessage, Toast.LENGTH_LONG).show();

                                Gson gson = new Gson();

                                try {
                                    Type collectionType = new TypeToken<Collection<BeanCustomerIssue>>() {
                                    }.getType();
                                    // String jsonstring = response.getString("addresslist");
                                    mCustIssueList = gson.fromJson(customerIssueList, collectionType);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                if (mCustIssueList.size() > 0) {

                                    addAdapter = new HistoryAdapter(context);
                                    lv_history.setAdapter(addAdapter);

                                } else {

                                    toastMessage("No data available");
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


    private class HistoryAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        public HistoryAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        public int getCount() {
            return mCustIssueList.size();
            // return  2;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {

                convertView = mInflater.inflate(R.layout.item_history_layout, null);
                holder = new ViewHolder();
                holder.tvDate = (TextView) convertView.findViewById(R.id.tvDate);
                holder.tvIssueID = (TextView) convertView.findViewById(R.id.tvIssueID);
                holder.tvDiseaseStatus = (TextView) convertView.findViewById(R.id.tvDiseaseStatus);
                holder.bt_select_issue = (Button) convertView.findViewById(R.id.bt_select_issue);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
//            holder.tvDate.setText("Issue Date : " + mCustIssueList.get(position).getIssueDate());
            holder.tvIssueID.setText("Issue ID : " + mCustIssueList.get(position).getCustomerIssueId());
//            holder.tvDiseaseStatus.setText("Issue : " + mCustIssueList.get(position).getIssueStatus());
            holder.tvDiseaseStatus.setText("Status : " + mCustIssueList.get(position).getSolvedStatus());

            ArrayList<String> dmy = getDMY(mCustIssueList.get(position).getIssueDate());

            String year = dmy.get(0);
            String month = dmy.get(1);
            String date = dmy.get(2);

            holder.tvDate.setText("Issue Date : " + date + "-" + month + "-" + year);

            holder.bt_select_issue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Bundle b = new Bundle();
                    b.putString("issueName", "" + mCustIssueList.get(position).getIssueName());
                    b.putString("comment", "" + mCustIssueList.get(position).getComment());
                    b.putString("issueDate", "" + mCustIssueList.get(position).getIssueDate());
                    b.putString("attachment1", "" + mCustIssueList.get(position).getAttachment1());
                    b.putString("attachment2", "" + mCustIssueList.get(position).getAttachment2());
                    b.putString("attachment3", "" + mCustIssueList.get(position).getAttachment3());
                    b.putString("description", "" + mCustIssueList.get(position).getDescription());
                    b.putInt("customerIssueId", mCustIssueList.get(position).getCustomerIssueId());
                    context.startActivity(new Intent(context, IsuueDetailsActivity.class).putExtras(b));
                    //  startActivity(new Intent(context, AssignIssueActivity.class));
                }
            });

            return convertView;
        }

        class ViewHolder {
            TextView tvDate, tvIssueID, tvDiseaseStatus;
            Button bt_select_issue;
        }

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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
    }

    public void toastMessage(String msg) {
        Toast.makeText(HistoryActivity.this, "" + msg, Toast.LENGTH_SHORT).show();
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


//                            SharedPreferences setting = PreferenceManager.getDefaultSharedPreferences(context);
//                            SharedPreferences.Editor edit = setting.edit();
//                            edit.putString(GlobalData.LOGIN_USER_ID, "0");
//                            edit.putString(GlobalData.LOGIN_USER_TYPE, "0");
//                            edit.putString(GlobalData.LOGIN_USER_NAME, "0");
//                            edit.putString(GlobalData.LOGIN_USER_EMAIL_ID, "0");
//                            edit.putString(GlobalData.LOGIN_USER_PHOTOURL, "0");
//                            edit.commit();

                            //for miantaining the log in session
                            CommonUtil.setSharePreferenceString(HistoryActivity.this, GlobalData.APP_LOGIN_STATUS, GlobalData.LOGED_OUT);

                            context.startActivity(new Intent(context, LoginActivity.class));
                            finish();

                        } else if (type.equalsIgnoreCase(GlobalData.USING_MOBILE_NO)) {

//                            SharedPreferences setting = PreferenceManager.getDefaultSharedPreferences(context);
//                            SharedPreferences.Editor edit = setting.edit();
//                            edit.putString(GlobalData.SHARE_USER_ID, "0");
//                            edit.putString(GlobalData.SHARE_MAIL_ADDRESS, "0");
//                            edit.putString(GlobalData.LOGIN_USER_ID, "0");
//                            edit.putString(GlobalData.LOGIN_USER_TYPE, "0");
////                            edit.putString(GlobalData.LOGIN_USER_NAME, "0");
////                            edit.putString(GlobalData.LOGIN_USER_EMAIL_ID, "0");
////                            edit.putString(GlobalData.LOGIN_USER_PHOTOURL, "0");
//                            edit.commit();

                            //for miantaining the log in session
                            CommonUtil.setSharePreferenceString(HistoryActivity.this, GlobalData.APP_LOGIN_STATUS, GlobalData.LOGED_OUT);

                            context.startActivity(new Intent(context, LoginActivity.class));
                            finish();

                            //     makeLogoutRquest(userId, imei);

                        } else {

//                            SharedPreferences setting = PreferenceManager.getDefaultSharedPreferences(context);
//                            SharedPreferences.Editor edit = setting.edit();
//                            edit.putString(GlobalData.SHARE_USER_ID, "0");
//                            edit.putString(GlobalData.SHARE_MAIL_ADDRESS, "0");
//                            edit.putString(GlobalData.LOGIN_USER_ID, "0");
//                            edit.putString(GlobalData.LOGIN_USER_TYPE, "0");
////                            edit.putString(GlobalData.LOGIN_USER_NAME, "0");
////                            edit.putString(GlobalData.LOGIN_USER_EMAIL_ID, "0");
////                            edit.putString(GlobalData.LOGIN_USER_PHOTOURL, "0");
//                            edit.commit();


                            //for miantaining the log in session
                            CommonUtil.setSharePreferenceString(HistoryActivity.this, GlobalData.APP_LOGIN_STATUS, GlobalData.LOGED_OUT);
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

    public static ArrayList<String> getDMY(String selectedGridDate) {

//        String[] dateItemArray = selectedGridDate.split("/");
        String[] dateItemArray = selectedGridDate.split("-");
        String day = null, monthName = null, year;

//        String dd = dateItemArray[1];
//        String mm = dateItemArray[0];
//        String yy = dateItemArray[2];

        String dd = dateItemArray[2];
        String mm = dateItemArray[1];
        String yy = dateItemArray[0];

        int month = Integer.parseInt(mm);

        String[] dayStr = dd.split(" ");
        String day1 = dayStr[0];
        int d = Integer.parseInt(day1);

        if (d < 10) {
            day = dd;
        }

        day = dd;

        if (month == 1) {
            monthName = "01";
        }
        if (month == 2) {
            monthName = "02";
        }
        if (month == 3) {
            monthName = "03";
        }
        if (month == 4) {
            monthName = "04";
        }
        if (month == 5) {
            monthName = "05";
        }
        if (month == 6) {
            monthName = "06";
        }
        if (month == 7) {
            monthName = "07";
        }
        if (month == 8) {
            monthName = "08";
        }
        if (month == 9) {
            monthName = "09";
        }
        if (month == 10) {
            monthName = "10";
        }
        if (month == 11) {
            monthName = "11";
        }
        if (month == 12) {
            monthName = "12";
        }

        year = yy;

        ArrayList<String> dmy1 = new ArrayList<>(3);
        dmy1.add(0, "" + year);
        dmy1.add(1, "" + monthName);
        dmy1.add(2, "" + d);

        return dmy1;

    }
}
