package com.example.hometrainng.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hometrainng.R;
import com.example.hometrainng.adapter.ChatAdapter;
import com.example.hometrainng.events.MessageEvent;
import com.example.hometrainng.retrofit.Constants;
import com.example.hometrainng.service.RoomService;
import com.example.hometrainng.tools.PLog;
import com.google.android.material.card.MaterialCardView;
import com.tamsiree.rxkit.RxImageTool;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.Camera1Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.DataChannel;
import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.DefaultVideoEncoderFactory;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CommunicateActivity extends BaseActivity {

    private static final String TAG = "CommunicateActivity";
    private static final int PERMISSIONS_CODE = 1;
    private ChatAdapter chatAdapter;
    private boolean showRemoteLandscape = true;
    private boolean showRemotePortrait = false;
    private boolean isPortrait = false;
    private boolean isFront = true;

    PeerConnectionFactory peerConnectionFactory;
    MediaConstraints audioConstraints;
    MediaConstraints videoConstraints;
    MediaConstraints sdpConstraints;
    VideoSource videoSource;
    VideoTrack localVideoTrack;
    AudioSource audioSource;
    AudioTrack localAudioTrack;
    SurfaceTextureHelper surfaceTextureHelper;
    PeerConnection localPeer;
    DataChannel localDataChannel;
    EglBase rootEglBase;

    FrameLayout.LayoutParams smallVideoParams;
    FrameLayout.LayoutParams bigVideoParams;
    List<PeerConnection.IceServer> peerIceServers = new ArrayList<>();
    List<String> chatMessages = new ArrayList<>();

    @BindView(R.id.local_gl_surface_view)
    SurfaceViewRenderer localVideoView;
    @BindView(R.id.local_card_view)
    MaterialCardView localCardView;
    @BindView(R.id.remote_card_view)
    MaterialCardView remoteCardView;
    @BindView(R.id.remote_gl_surface_view)
    SurfaceViewRenderer remoteVideoView;
    @BindView(R.id.chat_recycleView)
    RecyclerView chatRecyclerView;
    @BindView(R.id.microPhoneBtn)
    ImageButton microPhoneBtn;
    @BindView(R.id.switchBtn)
    ImageButton switchBtn;
    Button btnOk;

    VideoCapturer frontVideo;
    VideoCapturer backVideo;


    private AudioManager audioManager;

    static LinearLayout countdownTimerLinearLayout;
    static TextView connectTimerTv;
    static TextView connectTimerMessage;
    static LocalDateTime startDateTime;
    static Handler timeHandler = new Handler();
    static Runnable runnable = new Runnable() {
        @Override
        public void run() {
            long relayDate = Duration.between(startDateTime, LocalDateTime.now()).toMinutes();
            if (relayDate > 4 && relayDate % 5 == 0) {
                countdownTimerLinearLayout.setVisibility(View.VISIBLE);
                connectTimerTv.setText(String.valueOf(relayDate));
                if (relayDate >= 25) {
                    connectTimerTv.setTextColor(Color.parseColor("#FF9D00"));
                    connectTimerMessage.setTextColor(Color.parseColor("#FF9D00"));
                }
            }
            timeHandler.postDelayed(this, 1000);
        }
    };

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int mCurrentOrientation = getResources().getConfiguration().orientation;
        if (mCurrentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            chatRecyclerView.setVisibility(View.GONE);
//            isPortrait = true;
//            initVideoView();
//            if (showRemoteLandscape) {
//                remoteCardView.setTranslationZ(100);
//                localCardView.setTranslationZ(50);
//            } else {
//                localCardView.setTranslationZ(100);
//                remoteCardView.setTranslationZ(50);
//            }

        } else if (mCurrentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            chatRecyclerView.setVisibility(View.VISIBLE);
//            isPortrait = false;
//            initVideoView();
//            if (showRemotePortrait) {
//                remoteCardView.setTranslationZ(100);
//                localCardView.setTranslationZ(50);
//            } else {
//                localCardView.setTranslationZ(100);
//                remoteCardView.setTranslationZ(50);
//            }

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_communicate);
        ButterKnife.bind(this);

        countdownTimerLinearLayout = findViewById(R.id.countdown_timer_linear_layout);
        connectTimerTv = findViewById(R.id.connectTimerTv);
        connectTimerMessage = findViewById(R.id.connectTimerMessage);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            audioManager.setSpeakerphoneOn(!audioManager.isSpeakerphoneOn());
            if (audioManager.isMicrophoneMute()) {
                audioManager.setMicrophoneMute(false);
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO}, PERMISSIONS_CODE);
        } else {
            initVideoView();
            initChatView();
        }

        initVideoContext();
        initializePeerConnectionFactory();

    }

    @Override
    protected void onResume() {
        super.onResume();
        start();
        startDateTime = LocalDateTime.now();
        timeHandler.postDelayed(runnable, 1000);
    }

    @SuppressLint("RtlHardcoded")
    private void initChatView() {
        if (chatRecyclerView != null) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
            chatRecyclerView.setLayoutManager(linearLayoutManager);
            chatAdapter = new ChatAdapter(getApplicationContext(), chatMessages);
            chatRecyclerView.setAdapter(chatAdapter);
        }
    }

    @SuppressLint("RtlHardcoded")
    private void initVideoView() {
        smallVideoParams = new FrameLayout.LayoutParams(500, 500);
        smallVideoParams.gravity = Gravity.TOP | Gravity.RIGHT;
        smallVideoParams.topMargin = 20;
        smallVideoParams.rightMargin = 20;
        localCardView.setLayoutParams(smallVideoParams);
        bigVideoParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        bigVideoParams.gravity = Gravity.BOTTOM;
        remoteCardView.setLayoutParams(bigVideoParams);

//        Log.d("liaobude-test", "initVideoView: 竖屏=" + isPortrait + ",showRemoteLandscape=" + showRemoteLandscape + ",showRemotePortrait=" + showRemotePortrait);


//        if (!isPortrait) {
//            if (showRemoteLandscape) {
////                localCardView.setLayoutParams(bigVideoParams);
////                remoteCardView.setLayoutParams(smallVideoParams);
//                remoteVideoView.setZOrderOnTop(false);
//                localVideoView.setZOrderOnTop(true);
//                remoteCardView.setTranslationZ(1);
//                localCardView.setTranslationZ(0);
////                remoteVideoView.setZOrderMediaOverlay(true);
////                localVideoView.setZOrderMediaOverlay(false);
//                showRemoteLandscape = false;
//            } else {
////                remoteCardView.setLayoutParams(bigVideoParams);
////                localCardView.setLayoutParams(smallVideoParams);
//                localCardView.setTranslationZ(1);
//                remoteCardView.setTranslationZ(0);
////                remoteVideoView.setZOrderMediaOverlay(false);
////                localVideoView.setZOrderMediaOverlay(true);
//                showRemoteLandscape = true;
//            }
//        } else {
//            if (showRemotePortrait) {
//                remoteVideoView.setZOrderOnTop(false);
//                localVideoView.setZOrderOnTop(true);
//                remoteCardView.setTranslationZ(1);
//                localCardView.setTranslationZ(0);
//                showRemotePortrait = false;
//            } else {
//                remoteVideoView.setZOrderOnTop(false);
//                localVideoView.setZOrderOnTop(true);
//                localCardView.setTranslationZ(1);
//                remoteCardView.setTranslationZ(0);
//                showRemotePortrait = true;
//            }
//        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        audioManager.setMode(AudioManager.MODE_NORMAL);
        timeHandler.removeCallbacks(runnable);
        if (surfaceTextureHelper != null) {
            surfaceTextureHelper.dispose();
            surfaceTextureHelper = null;
        }
        closePeerConnection();
//        autoRotationUtil.enable();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (!(requestCode == PERMISSIONS_CODE
                && grantResults.length == 2
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
            finish();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @OnClick({R.id.local_gl_surface_view})
    public void onClick(View view) {
        if (view.getId() == R.id.local_gl_surface_view) {
            updateVideoViews();
        }
    }

    private void closePeerConnection() {
        if (localDataChannel != null) {
            localDataChannel.close();
        }
        if (localPeer != null) {
            localPeer.close();
        }
    }

    private void closeDataChannel() {
        if (localDataChannel != null) {
            localDataChannel.close();
        }
    }

    private void createPeerConnection() {

        PeerConnection.RTCConfiguration rtcConfig = new PeerConnection.RTCConfiguration(peerIceServers);
        rtcConfig.tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy.DISABLED;
        rtcConfig.bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE;
        rtcConfig.rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.REQUIRE;
        rtcConfig.continualGatheringPolicy = PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY;
        rtcConfig.keyType = PeerConnection.KeyType.ECDSA;
        localPeer = peerConnectionFactory.createPeerConnection(rtcConfig, new CustomPeerConnectionObserver("localPeerCreation") {
            @Override
            public void onIceCandidate(IceCandidate iceCandidate) {
                super.onIceCandidate(iceCandidate);
                onIceCandidateReceived(iceCandidate);
            }

            @Override
            public void onAddStream(MediaStream mediaStream) {
                super.onAddStream(mediaStream);
                gotRemoteStream(mediaStream);
            }
        });
        addStreamToLocalPeer();
    }

    private void createDataChanel() {

        localDataChannel = localPeer.createDataChannel("chat", new DataChannel.Init());
        localDataChannel.registerObserver(new DataChannel.Observer() {
            @Override
            public void onBufferedAmountChange(long l) {
            }

            @Override
            public void onStateChange() {
            }

            @Override
            public void onMessage(DataChannel.Buffer buffer) {
                runOnUiThread(() -> {
                    updateMessage(buffer);
                });
            }
        });
    }

    private void createPeerConnectionWithDataChanel() {

        PeerConnection.RTCConfiguration rtcConfig = new PeerConnection.RTCConfiguration(peerIceServers);
        rtcConfig.tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy.DISABLED;
        rtcConfig.bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE;
        rtcConfig.rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.REQUIRE;
        rtcConfig.continualGatheringPolicy = PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY;
        rtcConfig.keyType = PeerConnection.KeyType.ECDSA;
        localPeer = peerConnectionFactory.createPeerConnection(rtcConfig, new CustomPeerConnectionObserver("localPeerCreation") {
            @Override
            public void onIceCandidate(IceCandidate iceCandidate) {
                super.onIceCandidate(iceCandidate);
                onIceCandidateReceived(iceCandidate);
            }

            @Override
            public void onAddStream(MediaStream mediaStream) {
                super.onAddStream(mediaStream);
                gotRemoteStream(mediaStream);
            }

            @Override
            public void onDataChannel(DataChannel dataChannel) {
                super.onDataChannel(dataChannel);
                dataChannel.registerObserver(new DataChannel.Observer() {
                    @Override
                    public void onBufferedAmountChange(long l) {
                    }

                    @Override
                    public void onStateChange() {
                    }

                    @Override
                    public void onMessage(DataChannel.Buffer buffer) {
                        runOnUiThread(() -> {
                            updateMessage(buffer);
                        });
                    }
                });
            }
        });
        addStreamToLocalPeer();
    }

    private void updateMessage(DataChannel.Buffer buffer) {
        String message;
        ByteBuffer data = buffer.data;
        byte[] bytes;
        if (data.hasArray()) {
            bytes = data.array();
        } else {
            bytes = new byte[data.remaining()];
            data.get(bytes);
        }
        if (buffer.binary) {
            message = Base64.encodeToString(bytes, Base64.NO_WRAP);
        } else {
            message = new String(bytes, StandardCharsets.UTF_8);
        }
        chatAdapter.addMessage(message);
        chatRecyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
    }

    private void createOffer() {

        sdpConstraints = new MediaConstraints();
        sdpConstraints.mandatory.add(
                new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
        sdpConstraints.mandatory.add(
                new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));
        localPeer.createOffer(new CustomSdpObserver("localCreateOffer") {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                super.onCreateSuccess(sessionDescription);
                localPeer.setLocalDescription(new CustomSdpObserver("localSetLocalDesc"), sessionDescription);
                RoomService.getInstance().emitMessage(sessionDescription);
            }
        }, sdpConstraints);
    }

    private void createAnswer() {

        localPeer.createAnswer(new CustomSdpObserver("localCreateAns") {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                super.onCreateSuccess(sessionDescription);
                localPeer.setLocalDescription(new CustomSdpObserver("localSetLocal"), sessionDescription);
                RoomService.getInstance().emitMessage(sessionDescription);
            }
        }, new MediaConstraints());
    }

    public void onOfferReceived(final JSONObject data) {
        runOnUiThread(() -> {
            try {

                localPeer.setRemoteDescription(new CustomSdpObserver("localSetRemote"), new SessionDescription(SessionDescription.Type.OFFER, data.getString("sdp")));
                createAnswer();
            } catch (JSONException e) {
                PLog.e(TAG, e.toString());
            }
        });
    }

    public void onAnswerReceived(JSONObject data) {
        try {

            localPeer.setRemoteDescription(new CustomSdpObserver("localSetRemote"), new SessionDescription(SessionDescription.Type.fromCanonicalForm(data.getString("type").toLowerCase()), data.getString("sdp")));
        } catch (JSONException e) {
            PLog.e(TAG, e.toString());
        }
    }

    public void onIceCandidateReceived(JSONObject data) {
        try {
            localPeer.addIceCandidate(new IceCandidate(data.getString("id"), data.getInt("label"), data.getString("candidate")));
        } catch (JSONException e) {
            PLog.d(TAG, e.toString());
        }
    }

    private void gotRemoteStream(MediaStream stream) {
        final VideoTrack videoTrack = stream.videoTracks.get(0);
        runOnUiThread(() -> {
            try {

                remoteVideoView.setVisibility(View.VISIBLE);
                videoTrack.addSink(remoteVideoView);
            } catch (Exception e) {
                PLog.e(TAG, e.toString());
            }
        });
    }

    public void onIceCandidateReceived(IceCandidate iceCandidate) {

        RoomService.getInstance().emitIceCandidate(iceCandidate);
    }

    private void addStreamToLocalPeer() {

        MediaStream stream = peerConnectionFactory.createLocalMediaStream("102");
        stream.addTrack(localAudioTrack);
        stream.addTrack(localVideoTrack);

        localPeer.addStream(stream);
    }

    private void hangup() {
        try {
            if (localPeer != null) {
                localPeer.close();
            }
            localPeer = null;
            if (localVideoTrack != null) {
                localVideoTrack.removeSink(localVideoView);
            }

            Intent toRoomIntent = new Intent(this, CounselingRoomActivity.class);
            toRoomIntent.putExtra("fromFlag", "Communicate");
            startActivity(toRoomIntent);
            finish();
        } catch (Exception e) {
            PLog.e(TAG + "hangup", e.toString());
        }
    }

    @SuppressLint("RtlHardcoded")
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void updateVideoViews() {
        runOnUiThread(() -> {
            if (!isPortrait) {
                if (showRemoteLandscape) {
                    localCardView.setLayoutParams(bigVideoParams);
                    remoteCardView.setLayoutParams(smallVideoParams);
                    remoteCardView.setTranslationZ(1);
                    localCardView.setTranslationZ(0);
                    remoteVideoView.setZOrderMediaOverlay(true);
                    localVideoView.setZOrderMediaOverlay(false);
                    showRemoteLandscape = false;
                } else {
                    remoteCardView.setLayoutParams(bigVideoParams);
                    localCardView.setLayoutParams(smallVideoParams);
                    localCardView.setTranslationZ(1);
                    remoteCardView.setTranslationZ(0);
                    remoteVideoView.setZOrderMediaOverlay(false);
                    localVideoView.setZOrderMediaOverlay(true);
                    showRemoteLandscape = true;
                }
            } else {
                if (showRemotePortrait) {

                    remoteVideoView.setZOrderOnTop(true);
                    localVideoView.setZOrderOnTop(false);
                    localCardView.setLayoutParams(bigVideoParams);
                    remoteCardView.setLayoutParams(smallVideoParams);
                    remoteCardView.setTranslationZ(1);
                    localCardView.setTranslationZ(0);
                    showRemotePortrait = false;
                } else {

                    remoteVideoView.setZOrderOnTop(false);
                    localVideoView.setZOrderOnTop(true);
                    remoteCardView.setLayoutParams(bigVideoParams);
                    localCardView.setLayoutParams(smallVideoParams);
                    localCardView.setTranslationZ(1);
                    remoteCardView.setTranslationZ(0);
                    showRemotePortrait = true;
                }
            }
        });
    }

    public void initializePeerConnectionFactory() {
        //Initialize PeerConnectionFactory globals.
        PeerConnectionFactory.InitializationOptions initializationOptions = PeerConnectionFactory.InitializationOptions.builder(this).createInitializationOptions();
        PeerConnectionFactory.initialize(initializationOptions);
        //Create a new PeerConnectionFactory instance - using Hardware encoder and decoder.
        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
        DefaultVideoEncoderFactory defaultVideoEncoderFactory = new DefaultVideoEncoderFactory(rootEglBase.getEglBaseContext(), true, true);
        DefaultVideoDecoderFactory defaultVideoDecoderFactory = new DefaultVideoDecoderFactory(rootEglBase.getEglBaseContext());
        peerConnectionFactory = PeerConnectionFactory.builder().setOptions(options).setVideoEncoderFactory(defaultVideoEncoderFactory).setVideoDecoderFactory(defaultVideoDecoderFactory).createPeerConnectionFactory();
    }

    private void start() {
        getIceServers();
        initVideoDevice();
    }

    private void initVideoDevice() {
        frontVideo = createCamera("front", new Camera1Enumerator(false));
        backVideo = createCamera("back", new Camera1Enumerator(false));
        //Create MediaConstraints - Will be useful for specifying video and audio constraints.
        audioConstraints = new MediaConstraints();
        audioConstraints.mandatory.add(new MediaConstraints.KeyValuePair("googNoiseSuppression", "true"));
        audioConstraints.mandatory.add(new MediaConstraints.KeyValuePair("googEchoCancellation", "true"));
        audioConstraints.mandatory.add(new MediaConstraints.KeyValuePair("echoCancellation", "true"));
        audioConstraints.mandatory.add(new MediaConstraints.KeyValuePair("noiseSuppression", "true"));
        videoConstraints = new MediaConstraints();
        if (isFront) {
            if (frontVideo != null) {
                surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", rootEglBase.getEglBaseContext());
                videoSource = peerConnectionFactory.createVideoSource(frontVideo.isScreencast());
                frontVideo.initialize(surfaceTextureHelper, this, videoSource.getCapturerObserver());
            }
            localVideoTrack = peerConnectionFactory.createVideoTrack("100", videoSource);
            //create an AudioSource instance
            audioSource = peerConnectionFactory.createAudioSource(audioConstraints);
            localAudioTrack = peerConnectionFactory.createAudioTrack("101", audioSource);
            localAudioTrack.setVolume(500);
            if (frontVideo != null) {
                frontVideo.startCapture(1024, 720, 30);
            }
            localVideoTrack.addSink(localVideoView);
            localVideoView.setMirror(true);
            remoteVideoView.setMirror(true);
        }
        RoomService.getInstance().emitJoin();
    }

    private void initVideoContext() {
        rootEglBase = EglBase.create();

        localVideoView.init(rootEglBase.getEglBaseContext(), null);
        remoteVideoView.init(rootEglBase.getEglBaseContext(), null);
        localVideoView.setZOrderMediaOverlay(true);
        remoteVideoView.setZOrderMediaOverlay(true);
    }

    private void getIceServers() {
        PeerConnection.IceServer peerIceServer = PeerConnection.IceServer
                .builder(Constants.ICE_SERVER)
                .setUsername(Constants.ICE_USERNAME)
                .setPassword(Constants.ICE_KEY)
                .createIceServer();
        peerIceServers.add(peerIceServer);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onMessageEvent(MessageEvent event) {
        PLog.w("CommunicateActivity", event.getType() + "," + event.getMessage() + LocalDateTime.now().toString());

        if (event.getType().equals("room")) {
            switch (event.getMessage()) {
                case "CREATE_PEER_CONNECTION":
                    createPeerConnection();
                    break;
                case "CREATE_DATA_CHANEL":
                    createDataChanel();
                    break;
                case "CREATE_OFFER":
                    createOffer();
                    break;
                case "ON_OFFER_RECEIVED":
                    onOfferReceived(event.getData());
                    break;
                case "ON_ANSWER_RECEIVED":
                    onAnswerReceived(event.getData());
                    break;
                case "ON_ICECANDIDATE_RECEIVED":
                    onIceCandidateReceived(event.getData());
                    break;
                case "REPEAT_JOIN_MEETING":
                    closePeerConnection();
                    initVideoContext();
                    start();
                    createPeerConnection();
                    break;
                case "REPEAT_JOIN_MEETING2":
                    closePeerConnection();
                    createPeerConnection();
                    break;
                case "CLOSE_DATA_CHANEL":
                    closeDataChannel();
                    break;
                case "CREATE_PEER_CONNECTION_WITH_DATA_CHANEL":
                    createPeerConnectionWithDataChanel();
                    break;
                case "LEAVED_ROOM":
                    runOnUiThread(this::hangup);
                    break;
                case "NETWORK_CLOSE":
                    PLog.w("CommunicateActivity,NETWORK_CLOSE", LocalDateTime.now().toString());
                    showMessage(getString(R.string.network_close));
                    break;
                case "GO_AWAY":
                    PLog.w("CommunicateActivity,GO_AWAY", LocalDateTime.now().toString());
                    showMessage(getString(R.string.other_leave_room));
                    break;
                case "OUT_AGE":
                    PLog.w("CommunicateActivity,OUT_AGE", LocalDateTime.now().toString());
                    showMessage(getString(R.string.other_network_close));
                    break;
                case "otherJoin":
                    popupWindow.dismiss();
                    break;
                default:
                    break;
            }
        }
    }

    private void showMessage(String message) {
        View contentView = LayoutInflater.from(CommunicateActivity.this).inflate(R.layout.fragment_counselingmessage, null);
        int width = RxImageTool.dp2px(800);
        int height = RxImageTool.dp2px(500);
        popupWindow = new PopupWindow(contentView, width, height, false);
        popupWindow.setOutsideTouchable(false);

        TextView messageTextView = (TextView) contentView.findViewById(R.id.meetingMessageView);
        messageTextView.setText(message);
        btnOk = (Button) contentView.findViewById(R.id.btn_ok);
        View parentView = findViewById(android.R.id.content);
        popupWindow.showAtLocation(parentView, Gravity.CENTER, 0, 0);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                popupWindow.dismiss();
            }
        });
        btnOk.setOnClickListener(v -> {
            popupWindow.dismiss();
            RoomService.getInstance().emitLeaving();
            finish();
        });
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

    @OnClick(R.id.switchBtn)
    public void switchClick(View view) {
        closePeerConnection();
        videoSource.dispose();
        localVideoTrack.dispose();
        if (isFront) {
            if (backVideo != null) {
                videoSource = peerConnectionFactory.createVideoSource(backVideo.isScreencast());
                backVideo.initialize(surfaceTextureHelper, this, videoSource.getCapturerObserver());
                localVideoTrack = peerConnectionFactory.createVideoTrack("100", videoSource);
                localVideoTrack.addSink(localVideoView);
                backVideo.startCapture(1024, 720, 30);
            }
            if (frontVideo != null) {
                try {
                    frontVideo.stopCapture();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            isFront = false;
        } else {
            if (frontVideo != null) {
                videoSource = peerConnectionFactory.createVideoSource(frontVideo.isScreencast());
                frontVideo.initialize(surfaceTextureHelper, this, videoSource.getCapturerObserver());
                localVideoTrack = peerConnectionFactory.createVideoTrack("100", videoSource);
                localVideoTrack.addSink(localVideoView);
                frontVideo.startCapture(1024, 720, 30);
            }
            if (backVideo != null) {
                try {
                    backVideo.stopCapture();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            isFront = true;
        }
//        initVideoView();
        createPeerConnection();
        RoomService.getInstance().emitJoin();
    }

    @OnClick(R.id.microPhoneBtn)
    public void microPhoneBtnClick(View view) {

        if (audioManager.isMicrophoneMute()) {
            audioManager.setMicrophoneMute(false);
            microPhoneBtn.setImageResource(R.mipmap.unmute);
        } else {
            audioManager.setMicrophoneMute(true);
            microPhoneBtn.setImageResource(R.drawable.mute);
        }
    }

}