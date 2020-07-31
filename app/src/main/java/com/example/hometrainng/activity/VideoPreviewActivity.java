package com.example.hometrainng.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;

import android.os.PowerManager;
import android.util.Size;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.blankj.utilcode.util.AppUtils;
import com.example.hometrainng.R;
import com.example.hometrainng.customview.AutoDismissPopupWindow;
import com.example.hometrainng.events.MessageEvent;
import com.example.hometrainng.retrofit.Constants;
import com.example.hometrainng.service.RingToneService;
import com.example.hometrainng.tools.PLog;
import com.example.hometrainng.tools.Utils;
import com.tamsiree.rxkit.RxDeviceTool;
import com.tamsiree.rxkit.RxSPTool;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VideoPreviewActivity extends AppCompatActivity {

    private static final String TAG = "VideoPreviewActivity";
    @BindView(R.id.back_img)
    ImageView backImg;
    @BindView(R.id.convert_img)
    ImageView convertImg;
    @BindView(R.id.preview_texture)
    TextureView previewTexture;
    @BindView(R.id.video_button)
    Button videoButton;
    private Context mContext;

    private String mCameraId;
    private Size mPreviewSize;
    private HandlerThread mCameraThread;
    private Handler mCameraHandler;
    private CameraDevice mCameraDevice;
    private CaptureRequest.Builder mCaptureRequestBuilder;
    private CaptureRequest mCaptureRequest;
    private CameraCaptureSession mCameraCaptureSession;

    public static final String CAMERA_FRONT = "1";
    public static final String CAMERA_BACK = "0";

    private String VideoFileName, thumbnailPath;
    private int videoId;

    AutoDismissPopupWindow autoDismissPopupWindow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_preview);
        ButterKnife.bind(this);
        mContext = getApplicationContext();
        Intent intent = getIntent();
        VideoFileName = intent.getStringExtra("videoFileName");
        videoId = intent.getIntExtra("videoId", 0);
        thumbnailPath = intent.getStringExtra("thumbnailPath");

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCameraThread();
        if (!previewTexture.isAvailable()) {
            previewTexture.setSurfaceTextureListener(textureListener);
        } else {
            startPreview();
        }
    }

    private void startCameraThread() {
        //开启一个子线程Looper
        mCameraThread = new HandlerThread("CameraTextureViewThread");
        mCameraThread.start();
        mCameraHandler = new Handler(mCameraThread.getLooper());
    }


    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
            setupCamera(width, height);
            transformImage(width, height);
            openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

        }
    };


    @OnClick({R.id.back_img, R.id.convert_img, R.id.video_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_img:
                finish();
                break;
            case R.id.convert_img:
                if (Utils.isFastClick()){
                    switchCamera();
                }
                break;
            case R.id.video_button:
                Intent intent = new Intent(this, VideoRecordActivity.class);
                intent.putExtra("mCameraId", mCameraId);
                intent.putExtra("videoFileName", VideoFileName);
                intent.putExtra("videoId", videoId);
                intent.putExtra("thumbnailPath", thumbnailPath);
                startActivity(intent);
                mCameraDevice.close();
                finish();

        }
    }

    @Override
    public void onBackPressed() {
        if (autoDismissPopupWindow!=null && autoDismissPopupWindow.isShowing()){
            autoDismissPopupWindow.dismiss();
        }else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraDevice!=null){
            mCameraDevice.close();
        }
        EventBus.getDefault().unregister(this);
    }

    private void switchCamera() {
        if (mCameraId.equals(CAMERA_FRONT)) {
            mCameraId = CAMERA_BACK;
            mCameraDevice.close();
            reopenCamera();
        } else if (mCameraId.equals(CAMERA_BACK)) {
            mCameraId = CAMERA_FRONT;
            mCameraDevice.close();
            reopenCamera();
        }

    }


    public void reopenCamera() {
        if (previewTexture.isAvailable()) {
            openCamera();
        } else {
            previewTexture.setSurfaceTextureListener(textureListener);
        }
    }


    private void setupCamera(int width, int height) {
        //获得所有摄像头的管理者CameraManager
        CameraManager cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
                Integer facing = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING);

                if (facing != null && facing == CameraCharacteristics.LENS_FACING_BACK)
                    continue;

                StreamConfigurationMap map = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                assert map != null;
                //摄像头支持的预览Size数组
                mPreviewSize = getOptimalSize(map.getOutputSizes(SurfaceTexture.class), width, height);
                mCameraId = cameraId;
            }
        } catch (CameraAccessException e) {
            PLog.e(TAG+"setupCamera",e.toString());
        }
    }


    private Size getOptimalSize(Size[] sizeMap, int width, int height) {
        List<Size> sizeList = new ArrayList<>();
        for (Size option : sizeMap) {
            if (width > height) {
                if (option.getWidth() > width && option.getHeight() > height) {
                    sizeList.add(option);
                }
            } else {
                if (option.getWidth() > height && option.getHeight() > width) {
                    sizeList.add(option);
                }
            }
        }

        if (sizeList.size() > 0) {
            return Collections.min(sizeList, (lhs, rhs) -> Long.signum(lhs.getWidth() * (long) lhs.getHeight() - rhs.getHeight() * rhs.getWidth()));
        }
        return sizeMap[0];
    }

    private void openCamera() {
        CameraManager manager = (CameraManager) getSystemService(CAMERA_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        try {

            manager.openCamera(mCameraId, mStateCallback, mCameraHandler);
        } catch (Exception e) {
           PLog.e(TAG+"/openCamera",e.toString());
        }
    }


    private CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            mCameraDevice = cameraDevice;
            startPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            if (mCameraDevice != null) {
                mCameraDevice.close();
                cameraDevice.close();
                mCameraDevice = null;
            }
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int i) {
            if (mCameraDevice != null) {
                mCameraDevice.close();
                cameraDevice.close();
                mCameraDevice = null;
            }
        }
    };


    /**
     * 开启预览
     */
    private void startPreview() {
        SurfaceTexture mSurfaceTexture = previewTexture.getSurfaceTexture();
        mSurfaceTexture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
        Surface previewSurface = new Surface(mSurfaceTexture);

        try {
            mCaptureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mCaptureRequestBuilder.addTarget(previewSurface);
            mCameraDevice.createCaptureSession(Arrays.asList(previewSurface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    mCaptureRequest = mCaptureRequestBuilder.build();
                    mCameraCaptureSession = session;
                    try {
                        mCameraCaptureSession.setRepeatingRequest(mCaptureRequest, null, mCameraHandler);
                    } catch (CameraAccessException e) {
                       PLog.e(TAG+"/startPreview",e.toString());
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {

                }
            }, mCameraHandler);
        } catch (CameraAccessException e) {
            PLog.e(TAG+"/startPreview",e.toString());
        }

    }


    /**
     * 旋转角度
     *
     * @param width
     * @param height
     */
    private void transformImage(int width, int height) {
        if (mPreviewSize == null || previewTexture == null) {
            return;
        }
        Matrix matrix = new Matrix();
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        RectF textureRectF = new RectF(0, 0, width, height);
        RectF previewRectF = new RectF(0, 0, mPreviewSize.getHeight(), mPreviewSize.getWidth());
        float centerX = textureRectF.centerX();
        float centery = textureRectF.centerY();

        if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_270) {
        } else if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) {
            previewRectF.offset(centerX - previewRectF.centerX(), centery - previewRectF.centerY());
            matrix.setRectToRect(textureRectF, previewRectF, Matrix.ScaleToFit.FILL);
            float scale = Math.max((float) width / mPreviewSize.getWidth(), (float) height / mPreviewSize.getHeight());

            matrix.postScale(scale, scale, centerX, centery);
            matrix.postRotate(90F * (rotation - 2), centerX, centery);
            previewTexture.setTransform(matrix);

        }
    }



    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onMessageEvent(MessageEvent event) {
        if (event.getType().equals("room")) {
            switch (event.getMessage()) {
                case Constants.ROOM_EVENT_DISABLE_START_BUTTON:
                    RxSPTool.putBoolean(getApplicationContext(), Constants.ENABLE_MEETING_BUTTON, false);
                    autoDismissPopupWindow.dismiss();
                    stopService(new Intent(this, RingToneService.class));
                    PLog.i("end_calling", LocalDateTime.now().toString());
                    break;
                case Constants.ROOM_EVENT_ENABLE_START_BUTTON:
                    if (RxSPTool.getBoolean(getApplicationContext(), Constants.ENABLE_MEETING_BUTTON)) {
                        return;
                    } else {
                        RxSPTool.putBoolean(getApplicationContext(), Constants.ENABLE_MEETING_BUTTON, true);
                        if (event.getContent() != null) {
                            showStartMeetingWindow();
                        }
                        PLog.i("start_calling", LocalDateTime.now().toString());
                    }
                    break;
                default:
                    break;
            }
        }
    }


    public void showStartMeetingWindow() {
        KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
        kl.disableKeyguard();

        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        boolean screenOn = pm.isScreenOn();
        if (!screenOn) {
            @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
            wl.acquire();
            wl.release();

        }
        if (!AppUtils.isAppForeground()) {
            AppUtils.launchApp("com.example.hometrainng");

        }
        int screenWidth = RxDeviceTool.getScreenWidth(this);
        int screenHeight = RxDeviceTool.getScreenHeight(this);
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_start_meeting, null);
        autoDismissPopupWindow = new AutoDismissPopupWindow(contentView, screenWidth / 15 * 8, screenHeight / 15 * 8, false);
        autoDismissPopupWindow.setDelayMillis(600000);
        autoDismissPopupWindow.setOutsideTouchable(false);

        View parentView = findViewById(android.R.id.content);
        autoDismissPopupWindow.showAtLocation(parentView, Gravity.CENTER, 0, 0);
        bgAlpha(0.618f);
        Button buttonStartMeeting = (Button) contentView.findViewById(R.id.btn_go_to_meeting);
        TextView messageTextView = (TextView) contentView.findViewById(R.id.caller_message_textView);
        messageTextView.setText(getString(R.string.new_calling));
        startService(new Intent(this, RingToneService.class));
        autoDismissPopupWindow.setOnDismissListener(() -> {
            bgAlpha(1.0f);
            stopService(new Intent(getApplicationContext(), RingToneService.class));
        });
        buttonStartMeeting.setOnClickListener(v -> {
            finish();
            autoDismissPopupWindow.dismiss();
            goToMeeting2();
        });
    }

    private void bgAlpha(float f) {
        WindowManager.LayoutParams layoutParams = this.getWindow().getAttributes();
        layoutParams.alpha = f;
        this.getWindow().setAttributes(layoutParams);
    }

    private void goToMeeting2() {
        stopService(new Intent(this, RingToneService.class));
        Intent communicateIntent = new Intent(this, MeetingActivity.class);
        communicateIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(communicateIntent);
    }


}
