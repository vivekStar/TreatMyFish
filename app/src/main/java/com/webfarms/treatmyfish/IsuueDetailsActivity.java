package com.webfarms.treatmyfish;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.webfarms.treatmyfish.utils.GlobalData;


public class IsuueDetailsActivity extends AppCompatActivity {

    private Context context;
    private Toolbar toolbar;
    private TextView tv_issue_comment;
    private String comment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lay_issue_details);
        initialize();
    }

    private void initialize() {

        context = IsuueDetailsActivity.this;

        tv_issue_comment = findViewById(R.id.tv_issue_comment);

        toolbar = GlobalData.initToolBar(this, getString(R.string.title_issue_details), true);

        Intent intent = getIntent();
        try {
            comment = intent.getStringExtra("comment");

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (comment != null) {
            tv_issue_comment.setText(comment);
        }
    }
}
