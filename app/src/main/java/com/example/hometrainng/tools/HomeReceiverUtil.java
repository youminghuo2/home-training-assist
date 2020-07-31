package com.example.hometrainng.tools;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.example.hometrainng.model.HomeKeyListener;

/**
 * @Package com.example.hometrainng.tools
 * @Description 广播，监听home键位
 * @CreateDate: 2020/6/23 11:58 AM
 */
public class HomeReceiverUtil {
    private static final String SYSTEM_DIALOG_REASON_KEY = "reason";
    private static final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
    private static final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
    public static BroadcastReceiver mHomeReceiver = null;

    public static  void registerHomeKeyReceiver(Context context, final HomeKeyListener listener){
        mHomeReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                homeFinish(intent,context,listener);
            }
        };
        final IntentFilter homeFilter=new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.registerReceiver(mHomeReceiver,homeFilter);
    }

    public static void unregistenerHomeKeyReceiver(Context context){
        if (null!=mHomeReceiver){
            context.unregisterReceiver(mHomeReceiver);
            mHomeReceiver=null;
        }
    }

    private static void homeFinish(Intent intent,Context context,HomeKeyListener listener){
        String action=intent.getAction();
        if (action!=null && action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
            String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
            if (reason != null && (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)|| reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS))) {
                if (listener != null){
                    listener.homeKey();
                }
            }
        }
    }



}
