package com.example.hometrainng.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.hometrainng.service.RabbitMqService;
import com.example.hometrainng.tools.PLog;

import java.util.Objects;

public class ShutdownBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "ShutdownBroadcastReceiver";
    private static final String ACTION_SHUTDOWN = "android.intent.action.ACTION_SHUTDOWN";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Objects.equals(intent.getAction(), ACTION_SHUTDOWN)) {
            context.stopService(new Intent(context, RabbitMqService.class));
            PLog.i(TAG, "ShutdownBroadcastReceiver onReceive(), Do thing!");
        }
    }
}
