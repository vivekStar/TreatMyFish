package com.webfarms.treatmyfish.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.webfarms.treatmyfish.FishDash;
import com.webfarms.treatmyfish.R;

public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (FishDash.getInstance() != null) {
            FishDash.getInstance().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    FishDash.getInstance().getToolbar_logo().setImageResource(R.drawable.bell_red);
                }
            });
        }
    }
}
