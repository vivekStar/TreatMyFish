package com.webfarms.treatmyfish;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.webfarms.treatmyfish.utils.CommonUtil;
import com.webfarms.treatmyfish.utils.GlobalData;

/**
 * Created by Ashish Zade on 2/14/2017 & 5:18 PM.
 */

public class ProfileActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private Context context;
    private Toolbar toolbar;

    private LinearLayout ll_edit_profile, llChangeLanguage, llLogout;
    private TextView tv_user_name, tv_user_email;
    private ImageView profileImage;

    private String loginType;

    private GoogleApiClient mGoogleApiClient;
    private LinearLayout ll_btm_home, ll_btm_profile, ll_btm_history, ll_btm_logout, ll_btm_aboutus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        context = ProfileActivity.this;
        toolbar = GlobalData.initToolBar(this, getString(R.string.profile), true);

        initActivity();

    }

    private void initActivity() {

        loginType = CommonUtil.getSharePreferenceString(context, GlobalData.LOGIN_USER_TYPE, "1");

        profileImage = findViewById(R.id.circleView);
        tv_user_name = findViewById(R.id.tv_user_name);
        tv_user_email = findViewById(R.id.tv_user_email);

        ll_edit_profile = (LinearLayout) findViewById(R.id.ll_edit_profile);
        llLogout = (LinearLayout) findViewById(R.id.llLogout);
        llChangeLanguage = (LinearLayout) findViewById(R.id.ll_change_language);

        ll_edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, EditProfileActivity.class));
            }
        });

        llChangeLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent i = new Intent(ProfileActivity.this, SelectLanguageActivity.class);
//// set the new task and clear flags
//                CommonUtil.setSharePreferenceString(ProfileActivity.this, GlobalData.LANGUAGE, "0");
//                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(i);
            }
        });


        if (loginType.equalsIgnoreCase(GlobalData.USING_GOOGLE)) {

            String personPhotoUrl = CommonUtil.getSharePreferenceString(context, GlobalData.LOGIN_USER_PHOTOURL, "0");
            String userName = CommonUtil.getSharePreferenceString(context, GlobalData.LOGIN_USER_NAME, "0");
            String userEmail = CommonUtil.getSharePreferenceString(context, GlobalData.LOGIN_USER_EMAIL_ID, "0");

            if (!userName.equalsIgnoreCase("0")) {

                tv_user_name.setText(userName);

            }
            if (!userEmail.equalsIgnoreCase("0")) {

                tv_user_email.setText(userEmail);

            }
            if (!personPhotoUrl.equalsIgnoreCase("0")) {

                Glide.with(getApplicationContext()).load(personPhotoUrl)
                        .thumbnail(0.5f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(profileImage);

            }

        } else if (loginType.equalsIgnoreCase(GlobalData.USING_MOBILE_NO)) {

            String userName = CommonUtil.getSharePreferenceString(context, GlobalData.LOGIN_USER_NAME, "0");
            String userEmail = CommonUtil.getSharePreferenceString(context, GlobalData.LOGIN_USER_EMAIL_ID, "0");


            if (!userName.equalsIgnoreCase("0")) {

                tv_user_name.setText(userName);

            }
            if (!userEmail.equalsIgnoreCase("0")) {

                tv_user_email.setText(userEmail);

            }

        }

        String userEmailID = CommonUtil.getSharePreferenceString(ProfileActivity.this, GlobalData.LOGIN_USER_EMAIL_ID, "N/A");
        String userName = CommonUtil.getSharePreferenceString(ProfileActivity.this, GlobalData.LOGIN_USER_NAME, "N/A");
        String userMobileNo = CommonUtil.getSharePreferenceString(ProfileActivity.this, GlobalData.LOGIN_USER_MOBILE_NUMBER, "N/A");
        String photoUrl = CommonUtil.getSharePreferenceString(ProfileActivity.this, GlobalData.LOGIN_USER_PHOTOURL, "N/A");

        tv_user_email.setText(userEmailID);
        tv_user_name.setText(userName);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


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
//                startActivity(new Intent(context, ProfileActivity.class));
//                finish();
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

        llLogout.setOnClickListener(new View.OnClickListener() {
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

        ll_btm_aboutus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, AboutUsActivity.class));
            }
        });


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
        Toast.makeText(ProfileActivity.this, "" + msg, Toast.LENGTH_SHORT).show();
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
                            CommonUtil.setSharePreferenceString(ProfileActivity.this, GlobalData.APP_LOGIN_STATUS, GlobalData.LOGED_OUT);

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
                            CommonUtil.setSharePreferenceString(ProfileActivity.this, GlobalData.APP_LOGIN_STATUS, GlobalData.LOGED_OUT);


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
                            CommonUtil.setSharePreferenceString(ProfileActivity.this, GlobalData.APP_LOGIN_STATUS, GlobalData.LOGED_OUT);
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
                        dialog.dismiss();
                    }
                });

        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        String userEmailID = CommonUtil.getSharePreferenceString(ProfileActivity.this, GlobalData.LOGIN_USER_EMAIL_ID, "N/A");
        String userName = CommonUtil.getSharePreferenceString(ProfileActivity.this, GlobalData.LOGIN_USER_NAME, "N/A");
        tv_user_name.setText(userName);
        tv_user_email.setText(userEmailID);
    }
}
