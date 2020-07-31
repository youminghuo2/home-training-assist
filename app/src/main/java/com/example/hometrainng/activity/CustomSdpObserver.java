package com.example.hometrainng.activity;

import com.example.hometrainng.tools.PLog;

import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;

class CustomSdpObserver implements SdpObserver {
    private String tag;

    CustomSdpObserver(String logTag) {
        this.tag = this.getClass().getCanonicalName();
        this.tag = this.tag + " " + logTag;
    }


    @Override
    public void onCreateSuccess(SessionDescription sessionDescription) {
    }

    @Override
    public void onSetSuccess() {
        PLog.d(this.tag, "onSetSuccess");
    }

    @Override
    public void onCreateFailure(String s) {
        PLog.d(this.tag, "onCreateFailure");
    }

    @Override
    public void onSetFailure(String s) {
        PLog.d(this.tag, "onSetFailure");
    }

}
