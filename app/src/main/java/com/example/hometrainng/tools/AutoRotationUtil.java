package com.example.hometrainng.tools;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.Surface;

import androidx.annotation.Nullable;

public class AutoRotationUtil {
    private static final String TAG = "OrientationUtils";

    private @Nullable
    Activity activity;
    private @Nullable
    OrientationEventListener orientationEventListener;

    private int mOldScreenRotation = 0;

    //是否跟随系统
    private boolean mRotateWithSystem = true;
    //允许旋转到竖向
    private boolean mPortraitRotate = true;
    //横向时可以旋转
    private boolean mForceRotateLand = false;

    // 屏幕方向 -1未指定,0默认(竖向),1横向
    private int gravity = -1;

    // 被锁定，无法开启
    private boolean mLocked = false;


    public AutoRotationUtil(Activity activity) {
        this.activity = activity;

        initGravity(activity);
        initEventListener();
    }

    /**
     * 点击切换屏幕方向，比如竖屏的时候点击了就是切换到横屏，不会受屏幕的影响
     */
    public void toggleRotation() {
        if (activity != null) {
            mOldScreenRotation = activity.getWindowManager().getDefaultDisplay().getRotation();
            switch (mOldScreenRotation) {
                case Surface.ROTATION_0:
                    if (gravity < 1) {
                        setRequestedRotation(Surface.ROTATION_90);
                    } else {
                        setRequestedRotation(Surface.ROTATION_270);
                    }
                    break;
                case Surface.ROTATION_90:
                    setRequestedRotation(Surface.ROTATION_0);
                    break;
                case Surface.ROTATION_180:
                    if (gravity < 1) {
                        setRequestedRotation(Surface.ROTATION_90);
                    } else {
                        setRequestedRotation(Surface.ROTATION_270);
                    }
                    break;
                case Surface.ROTATION_270:
                    setRequestedRotation(Surface.ROTATION_0);
                    break;
            }
        }
    }

    /**
     * 开启自动旋转
     */
    public void enable() {
        if (!mLocked) {
            orientationEventListener.enable();
        }
    }

    /**
     * 关闭自动旋转
     */
    public void disable() {
        orientationEventListener.disable();
    }

    /**
     * 释放资源，不允许再开启
     */
    public void release() {
        disable();
        orientationEventListener = null;
        activity = null;
    }

    /**
     * 是否更新系统旋转，false的话，系统禁止旋转也会跟着旋转
     *
     * @param rotateWithSystem 默认true
     */
    public void setRotateWithSystem(boolean rotateWithSystem) {
        this.mRotateWithSystem = rotateWithSystem;
    }

    /**
     * 横向时可以旋转
     *
     * @param forceRotateLand true 允许
     */
    public void setForceRotateLand(boolean forceRotateLand) {
        mForceRotateLand = forceRotateLand;
    }

    /**
     * 允许自动旋转到竖向，可以在只允许横屏播放的场景设置为false
     *
     * @param portrait true 允许
     */
    public void enablePortrait(boolean portrait) {
        mPortraitRotate = portrait;
    }

    private void initGravity(Activity activity) {
        if (gravity == -1) {
            int defaultRotation = activity.getWindowManager().getDefaultDisplay().getRotation();
            Log.d(TAG, "initGravity: " + defaultRotation);
            if (defaultRotation == Surface.ROTATION_0) {
                // 竖向为正方向。 如：手机、小米平板
                gravity = 0;
            } else if (defaultRotation == Surface.ROTATION_270) {
                // 横向为正方向。 如：三星、sony平板
                gravity = 1;
            } else {
                // 未知方向
                gravity = 0;
            }
        }
    }

    private void initEventListener() {
        orientationEventListener = new OrientationEventListener(activity) {
            @Override
            public void onOrientationChanged(int rotation) {
                if (activity == null) {
                    return;
                }
                int screenRotation = activity.getWindowManager().getDefaultDisplay().getRotation();
//                PLog.d(TAG, "mScreenRotation=: " + screenRotation + ", rotation=" + rotation);

                if (disallowAutoRotate(rotation, screenRotation)) return;

                // 自动竖屏
                if (((rotation >= 0) && (rotation <= 15)) || (rotation >= 345)) {
                    if (!mPortraitRotate) {
                        return;
                    }
                    if (mOldScreenRotation != Surface.ROTATION_0) {
                        mOldScreenRotation = -1;
                    }
                    if (screenRotation == Surface.ROTATION_0 || (mOldScreenRotation != -1)) {
                        return;
                    }
                    setRequestedRotation(Surface.ROTATION_0);
                }
                // 自动反向横屏
                else if (rotation > 80 && rotation < 100) {
                    if (mOldScreenRotation != Surface.ROTATION_270) {
                        mOldScreenRotation = -1;
                    }
                    if (screenRotation == Surface.ROTATION_270 || (mOldScreenRotation != -1)) {
                        return;
                    }
                    setRequestedRotation(Surface.ROTATION_270);
                }
                // 自动反向竖屏
                else if (rotation > 165 && rotation < 195) {
                    if (!mPortraitRotate) {
                        return;
                    }
                    if (mOldScreenRotation != Surface.ROTATION_180) {
                        mOldScreenRotation = -1;
                    }
                    if (screenRotation == Surface.ROTATION_180 || mOldScreenRotation != -1) {
                        return;
                    }
                    setRequestedRotation(Surface.ROTATION_180);
                }
                // 自动横屏
                else if (((rotation >= 260) && (rotation <= 280))) {
                    if (mOldScreenRotation != Surface.ROTATION_90) {
                        mOldScreenRotation = -1;
                    }
                    if (screenRotation == Surface.ROTATION_90 || (mOldScreenRotation != -1)) {
                        return;
                    }
                    setRequestedRotation(Surface.ROTATION_90);
                }
            }
        };
    }

    /**
     * 是否拦截自动旋转
     *
     * @param rotation       当前旋转的实时角度
     * @param screenRotation 当前屏幕的角度
     * @return true: 不允许，拦截。false: 允许
     */
    private boolean disallowAutoRotate(int rotation, int screenRotation) {
        boolean autoRotateOn = (Settings.System.getInt(activity.getContentResolver(),
                Settings.System.ACCELEROMETER_ROTATION, 0) == 1);
        if (!autoRotateOn && mRotateWithSystem) {
            if (mForceRotateLand) {
                // 开启横屏强制旋转
                if (gravity < 1) {
                    // 默认情况
                    if ((screenRotation == Surface.ROTATION_90 || screenRotation == Surface.ROTATION_270)
                            && ((rotation > 80 && rotation < 100) || ((rotation >= 260) && (rotation <= 280)))) {
                        // rotate
                        return false;
                    } else {
                        // disallow
                        return true;
                    }
                } else {
                    // 正方向相反
                    if ((screenRotation == Surface.ROTATION_0 || screenRotation == Surface.ROTATION_180)
                            && (((rotation >= 0) && (rotation <= 15)) || (rotation > 165 && rotation < 195))) {
                        // rotate
                        return false;
                    } else {
                        // disallow
                        return true;
                    }
                }
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    /**
     * 根据请求的旋转角度完成旋转（自动处理默认方向不一致问题）
     *
     * @param rotation 需要旋转到的角度
     */
    private void setRequestedRotation(/*@Surface.Rotation*/ int rotation) {
        switch (rotation) {
            case Surface.ROTATION_0:
                if (gravity < 1) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
                break;
            case Surface.ROTATION_90:
                if (gravity < 1) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                }
                break;
            case Surface.ROTATION_180:
                if (gravity < 1) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                }
                break;
            case Surface.ROTATION_270:
                if (gravity < 1) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
                break;
        }
    }

    private void setRequestedOrientation(int requestedOrientation) {
        try {
            activity.setRequestedOrientation(requestedOrientation);
        } catch (IllegalStateException exception) {
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O || Build.VERSION.SDK_INT == Build.VERSION_CODES.O_MR1) {
                if (requestedOrientationOErrorListener != null) {
                    requestedOrientationOErrorListener.onVersionOIllegalStateException(requestedOrientation);
                }
            }
        }
    }

    private RequestedOrientationOErrorListener requestedOrientationOErrorListener;

    /**
     * Android O 透明activity不允许旋转bug
     * android O bug https://zhuanlan.zhihu.com/p/32190223
     */
    public interface RequestedOrientationOErrorListener {
        void onVersionOIllegalStateException(int requestedOrientation);
    }

    public void setRequestedOrientationListener(RequestedOrientationOErrorListener requestedOrientationOErrorListener) {
        this.requestedOrientationOErrorListener = requestedOrientationOErrorListener;
    }

    public boolean isLocked() {
        return mLocked;
    }

    public void setLocked(boolean locked) {
        this.mLocked = locked;
    }
}
