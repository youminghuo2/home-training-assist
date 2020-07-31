package com.example.hometrainng.customview;

import android.os.Handler;
import android.view.View;
import android.widget.PopupWindow;


public class AutoDismissPopupWindow extends PopupWindow {
    private long delayMillis = 10000;

    public AutoDismissPopupWindow setDelayMillis(long delayMillis) {
        this.delayMillis = delayMillis;
        return this;
    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            dismiss();
        }
    };

    public AutoDismissPopupWindow(View contentView, int width, int height, boolean focusable) {
        super(contentView, width, height, focusable);
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
        handler.postDelayed(runnable, delayMillis);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        handler.removeCallbacks(runnable);
    }
}
