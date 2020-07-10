package com.webfarms.treatmyfish;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.webfarms.treatmyfish.utils.CommonUtil;
import com.webfarms.treatmyfish.utils.GlobalData;


public class SelectLanguageActivity extends AppCompatActivity implements View.OnClickListener {

    private Context context;
    private Button btnEnglish, btnHindi, btnOriya;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_language);

        initialize();

    }

    private void initialize() {

        context = SelectLanguageActivity.this;
        btnEnglish = findViewById(R.id.btnEnglish);
        btnHindi = findViewById(R.id.btnHindi);
        btnOriya = findViewById(R.id.btnOriya);
        Button btnContinue = findViewById(R.id.btnContinue);

        btnEnglish.setOnClickListener(this);
        btnHindi.setOnClickListener(this);
        btnOriya.setOnClickListener(this);
        btnContinue.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnEnglish:
                btnEnglish.setBackgroundResource(R.drawable.select_box);
                btnHindi.setBackgroundResource(R.drawable.comment_box);
                btnOriya.setBackgroundResource(R.drawable.comment_box);
                CommonUtil.setSharePreferenceString(context, GlobalData.LANGUAGE_SELECTED, "yes");
                CommonUtil.setSharePreferenceString(context, GlobalData.LANGUAGE, GlobalData.ENGLISH_LANGUAGE);
                CommonUtil.setLocaleEnglish(context);
                Toast.makeText(context, "Language set to English", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btnHindi:
                btnHindi.setBackgroundResource(R.drawable.select_box);
                btnEnglish.setBackgroundResource(R.drawable.comment_box);
                btnOriya.setBackgroundResource(R.drawable.comment_box);
                CommonUtil.setSharePreferenceString(context, GlobalData.LANGUAGE_SELECTED, "yes");
                CommonUtil.setSharePreferenceString(context, GlobalData.LANGUAGE, GlobalData.HINDI_LANGUAGE);
                CommonUtil.setLocaleHindi(context);
                Toast.makeText(context, "Language set to Hindi", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btnOriya:
                btnOriya.setBackgroundResource(R.drawable.select_box);
                btnEnglish.setBackgroundResource(R.drawable.comment_box);
                btnHindi.setBackgroundResource(R.drawable.comment_box);
                CommonUtil.setSharePreferenceString(context, GlobalData.LANGUAGE_SELECTED, "yes");
                CommonUtil.setSharePreferenceString(context, GlobalData.LANGUAGE, GlobalData.ORIYA_LANGUAGE);
                CommonUtil.setLocaleOriya(context);
                Toast.makeText(context, "Language set to Oriya", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btnContinue:
                startAppOne();
                break;

        }
    }


    private void startAppOne() {

        try {

            String userId = CommonUtil.getSharePreferenceString(context, GlobalData.SHARE_USER_ID, "0");
            String token = CommonUtil.getSharePreferenceString(context, GlobalData.USER_TOKEN, "0");

            if (!userId.equals("0")) {

                Intent intent = new Intent(SelectLanguageActivity.this, InitialSetupActivity.class);
                startActivity(intent);
                finish();
            } else {

                Intent intent = new Intent(SelectLanguageActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        } catch (Exception e) {

        }

    }

}
