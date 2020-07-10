package com.webfarms.treatmyfish;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.webfarms.treatmyfish.database.DatabaseHandler;
import com.webfarms.treatmyfish.utils.CommonUtil;
import com.webfarms.treatmyfish.utils.GlobalData;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * Created by Ashish Zade on 2/11/2017 & 10:41 PM.
 * CIFA key Hash      h/7skJYf2AuTh/g8zikQmCdbIDE=
 */

public class SplashActivity extends AppCompatActivity {

    private Context context;
    private ProgressBar mProgress;
    private LinearLayout llNoInternet;
//    private ProgressDialog mProgressDialog;
//    private RequestQueue requestQueue;
//    private DatabaseHelper helper;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        context = SplashActivity.this;
        llNoInternet = findViewById(R.id.llNoInternet);
        mProgress = (ProgressBar) findViewById(R.id.splash_screen_progress_bar);
//        requestQueue = Volley.newRequestQueue(context);
//        helper = new DatabaseHelper(context);

        checkInternetStatus();

//        printHashKey(context);


    }

  /*  public void printHashKey(Context pContext) {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));

            }
        } catch (NoSuchAlgorithmException e) {

        } catch (Exception e) {

        }
    }*/

    private void doWork() {

        for (int progress = 0; progress <= 100; progress += 70) {
            try {
                Thread.sleep(1000);
                mProgress.setProgress(progress);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void startApp() {


        String langSel = CommonUtil.getSharePreferenceString(context, GlobalData.LANGUAGE_SELECTED, "0");
        String language = CommonUtil.getSharePreferenceString(context, GlobalData.LANGUAGE, "0");


        if (language.equalsIgnoreCase(GlobalData.HINDI_LANGUAGE)) {
            CommonUtil.setLocaleHindi(context);
        } else if (language.equalsIgnoreCase(GlobalData.ENGLISH_LANGUAGE)) {
            CommonUtil.setLocaleEnglish(context);
        } else if (language.equalsIgnoreCase(GlobalData.ORIYA_LANGUAGE)) {
            CommonUtil.setLocaleOriya(context);
        } else {
            CommonUtil.setLocaleEnglish(context);
        }

        if (langSel.equalsIgnoreCase("yes")) {

            try {

                String userId = CommonUtil.getSharePreferenceString(context, GlobalData.SHARE_USER_ID, "0");
                String token = CommonUtil.getSharePreferenceString(context, GlobalData.USER_TOKEN, "0");

                if (!userId.equals("0")) {

                    Intent intent = new Intent(SplashActivity.this, InitialSetupActivity.class);
                    startActivity(intent);
                    finish();
                } else {

                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Intent intent = new Intent(SplashActivity.this, SelectLanguageActivity.class);
            startActivity(intent);
            finish();
        }

    }


    class DownloadData extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            try {
                GlobalData.getSplashDataSync(context);
            } catch (Exception e) {
                GlobalData.printError(e);
            }
            return null;
        }
    }

    private void checkInternetStatus() {

        boolean internetAvailable = GlobalData.checkConnection(context, llNoInternet);

        if (internetAvailable) {
            // Start lengthy operation in a background thread
            new Thread(new Runnable() {
                public void run() {
                    doWork();
                    new DownloadData().execute("");
                    startApp();
                }
            }).start();

            try {
                DatabaseHandler db = new DatabaseHandler(this);
                db.initDatabase();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
            }

        } else {
//            Toast.makeText(context, "No Internet Connection.", Toast.LENGTH_SHORT).show();
            showNotInternetAlert(context);
        }

    }

    public void showNotInternetAlert(final Context context) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("No Internet Connection");
        alertDialog.setMessage("Check your Internet connection and try again");
        alertDialog.setCancelable(true);
        alertDialog.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                checkInternetStatus();
            }
        });

    }

}
