package com.example.hometrainng.activity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.example.hometrainng.R;
import com.example.hometrainng.events.MessageEvent;
import com.example.hometrainng.retrofit.Constants;
import com.example.hometrainng.service.RoomService;
import com.example.hometrainng.tools.PLog;
import com.tamsiree.rxkit.RxSPTool;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.Camera1Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.DefaultVideoEncoderFactory;
import org.webrtc.EglBase;
import org.webrtc.MediaConstraints;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.time.Duration;
import java.time.LocalDateTime;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MeetingActivity extends BaseActivity {

    public static final String TAG = "MeetingActivity";

    PeerConnectionFactory peerConnectionFactory;
    EglBase rootEglBase;
    MediaConstraints audioConstraints;
    MediaConstraints videoConstraints;
    VideoSource videoSource;
    VideoTrack videoTrack;
    SurfaceTextureHelper surfaceTextureHelper;
    AudioSource audioSource;
    AudioTrack audioTrack;
    VideoCapturer frontVideoCapturer;
    @BindView(R.id.selfSurfaceView)
    SurfaceViewRenderer selfSurfaceView;
    @BindView(R.id.startMeetingBtn)
    Button startMeetingBtn;
    boolean isConnectionOk = false;
    LocalDateTime startDateTime;
    Handler timeHandler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            long relayDate = Duration.between(startDateTime, LocalDateTime.now()).toMillis();
            // 大于2s 未收到 服务端告知 web端在线的消息
            if (relayDate >= 2) {
                if (isConnectionOk) {
                    RoomService.getInstance().emitCalled();
                    RxSPTool.putBoolean(getApplicationContext(), Constants.ENABLE_MEETING_BUTTON, false);
                    startActivity(new Intent(MeetingActivity.this, CommunicateActivity.class));
                }
                finish();
            }
            timeHandler.postDelayed(this, 1000);
        }
    };


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting);
        ButterKnife.bind(this);
        initCamera();
        if (RxSPTool.getBoolean(getApplicationContext(), Constants.ENABLE_MEETING_BUTTON)) {
            startMeetingBtn.setEnabled(true);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            return true;
        } else {
            return super.dispatchKeyEvent(event);
        }
    }

    private void initCamera() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        PeerConnectionFactory.InitializationOptions initializationOptions =
                PeerConnectionFactory.InitializationOptions.builder(this)
                        .createInitializationOptions();
        PeerConnectionFactory.initialize(initializationOptions);

        //Create a new PeerConnectionFactory instance - using Hardware encoder and decoder.
        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();

        rootEglBase = EglBase.create();
        selfSurfaceView.init(rootEglBase.getEglBaseContext(), null);
        selfSurfaceView.setZOrderMediaOverlay(true);

        DefaultVideoEncoderFactory defaultVideoEncoderFactory = new DefaultVideoEncoderFactory(rootEglBase.getEglBaseContext(), true, true);
        DefaultVideoDecoderFactory defaultVideoDecoderFactory = new DefaultVideoDecoderFactory(rootEglBase.getEglBaseContext());
        peerConnectionFactory = PeerConnectionFactory.builder()
                .setOptions(options)
                .setVideoEncoderFactory(defaultVideoEncoderFactory)
                .setVideoDecoderFactory(defaultVideoDecoderFactory)
                .createPeerConnectionFactory();

        frontVideoCapturer = createCamera("front", new Camera1Enumerator(false));
        surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", rootEglBase.getEglBaseContext());

        audioConstraints = new MediaConstraints();
        videoConstraints = new MediaConstraints();

        if (frontVideoCapturer != null) {
            videoSource = peerConnectionFactory.createVideoSource(frontVideoCapturer.isScreencast());
            frontVideoCapturer.initialize(surfaceTextureHelper, this, videoSource.getCapturerObserver());
        }
        videoTrack = peerConnectionFactory.createVideoTrack("100", videoSource);
        //create an AudioSource instance
        audioSource = peerConnectionFactory.createAudioSource(audioConstraints);
        audioTrack = peerConnectionFactory.createAudioTrack("101", audioSource);
        if (frontVideoCapturer != null) {
            frontVideoCapturer.startCapture(1024, 720, 30);
        }
        selfSurfaceView.setVisibility(View.VISIBLE);
        // And finally, with our VideoRenderer ready, we
        // can add our renderer to the VideoTrack.
        videoTrack.addSink(selfSurfaceView);
        selfSurfaceView.setMirror(true);
    }

    @OnClick({R.id.startMeetingBtn})
    public void onViewClicked(View view) {
//        switch (view.getId()) {
//            case R.id.startMeetingBtn:
//        RoomService.getInstance().emitCalled();
//        RxSPTool.putBoolean(getApplicationContext(), Constants.ENABLE_MEETING_BUTTON, false);
//        startActivity(new Intent(MeetingActivity.this, CommunicateActivity.class));
//        finish();

        RoomService.getInstance().emitConnectionCheck();
        startDateTime = LocalDateTime.now();
        timeHandler.postDelayed(runnable, 1000);
//                break;

//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timeHandler.removeCallbacks(runnable);
        if (rootEglBase != null) {
            rootEglBase.release();
        }
        PLog.w("liaobude-test", "meetingactivity on destroy");
        RxSPTool.putBoolean(getApplicationContext(), Constants.ENABLE_MEETING_BUTTON, false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onMessageEvent(MessageEvent event) {
//        Log.d("liaobude-test", "onMessageEvent: " + event.getType() + "," + event.getMessage());
        if (event == null) {
            return;
        }

        if (event.getType().equals("room")) {
            switch (event.getMessage()) {
                case Constants.ROOM_EVENT_DISABLE_START_BUTTON:
                    startMeetingBtn.setEnabled(false);
                    finish();
                    break;
                case Constants.ROOM_EVENT_ENABLE_START_BUTTON:
                    startMeetingBtn.setEnabled(true);
                    break;
                case Constants.ROOM_EVENT_CONNECTION_OK:
                    Log.d("liaobude-test", "onMessageEvent: " + isConnectionOk);
                    isConnectionOk = true;
                    Log.d("liaobude-test", "onMessageEvent: " + isConnectionOk);
                    break;
            }
        }
    }

    private VideoCapturer createCamera(String position, CameraEnumerator enumerator) {
        final String[] deviceNames = enumerator.getDeviceNames();
        VideoCapturer videoCapturer = null;
        for (String deviceName : deviceNames) {
            if (position.equals("front") && enumerator.isFrontFacing(deviceName)) {
                videoCapturer = enumerator.createCapturer(deviceName, null);
                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
            if (position.equals("back") && enumerator.isBackFacing(deviceName)) {
                videoCapturer = enumerator.createCapturer(deviceName, null);
                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        return null;
    }
}
