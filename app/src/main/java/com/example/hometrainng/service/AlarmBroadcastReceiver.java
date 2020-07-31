package com.example.hometrainng.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.hometrainng.events.MessageEvent;

import org.greenrobot.eventbus.EventBus;

public class AlarmBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        EventBus.getDefault().post(new MessageEvent("counseling", "alarm"));
    }
}
