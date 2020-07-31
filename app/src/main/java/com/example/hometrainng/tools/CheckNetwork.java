package com.example.hometrainng.tools;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;

import androidx.annotation.NonNull;

import com.example.hometrainng.events.MessageEvent;

import org.greenrobot.eventbus.EventBus;

public class CheckNetwork {
    private Context context;

    public CheckNetwork(Context context) {
        this.context = context;
    }

    public void registerNetworkCallback() {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkRequest.Builder builder = new NetworkRequest.Builder();

            connectivityManager.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(@NonNull Network network) {

                    EventBus.getDefault().post(new MessageEvent("onAvailable", "network"));
                }

                @Override
                public void onLost(@NonNull Network network) {

                    EventBus.getDefault().post(new MessageEvent("onLost", "network"));
                }
            });

        } catch (Exception e) {

        }
    }
}
