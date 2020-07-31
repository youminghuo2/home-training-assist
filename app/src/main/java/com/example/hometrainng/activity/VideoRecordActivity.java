package com.example.hometrainng.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.ImageFormat;
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
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Range;
import android.util.Size;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.AppUtils;
import com.bumptech.glide.Glide;
import com.example.hometrainng.R;
import com.example.hometrainng.api.HomeTrainService;
import com.example.hometrainng.customview.AutoDismissPopupWindow;
import com.example.hometrainng.customview.IjkplayerVideoView;
import com.example.hometrainng.customview.VideoPlayerListener;
import com.example.hometrainng.customview.VideoTextureView;
import com.example.hometrainng.db.RecordVideo;
import com.example.hometrainng.db.Video;
import com.example.hometrainng.db.VideoHistory;
import com.example.hometrainng.entity.MsgModel;
import com.example.hometrainng.events.MessageEvent;
import com.example.hometrainng.retrofit.Constants;
import com.example.hometrainng.retrofit.HttpHelper;
import com.example.hometrainng.service.RingToneService;
import com.example.hometrainng.tools.PLog;
import com.example.hometrainng.tools.Utils;
import com.example.hometrainng.tools.ViewUtils;
import com.tamsiree.rxkit.RxDeviceTool;
import com.tamsiree.rxkit.RxImageTool;
import com.tamsiree.rxkit.RxSPTool;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;


public class VideoRecordActivity extends AppCompatActivity {
    private static final String TAG = "VideoRecordActivity";

    @BindView(R.id.ijkPlayer_img)
    ImageView ijkPlayerImg;
    @BindView(R.id.ijkPlayer_textureView)
    IjkplayerVideoView ijkPlayerTextureView;
    @BindView(R.id.time_data_tv)
    TextView timeDataTv;
    @BindView(R.id.ijkPlayer_tv)
    TextView ijkPlayerTv;
    @BindView(R.id.ijkplayer_seekbar)
    SeekBar ijkplayerSeekbar;
    @BindView(R.id.rel_visible)
    RelativeLayout relVisible;
    private Handler handler = new Handler();
    private Runnable runnable;
    //用于检测播放器状态
    private boolean isPlaying = true;
    //进度的时间及进度条
    private long current, duration, seconds, minutes;
    private String mCameraId;
    private File mVideoPath;
    private String VideoFileName, thumbnailPath;
    private int videoId;

    /**
     * 录制视频
     *
     * @param savedInstanceState
     */
    private FrameLayout rootLayout;
    private VideoTextureView textureView;
    private CameraDevice cameraDevice;
    private Size previewSize;
    private CaptureRequest.Builder previewRequestBuilder;
    private CaptureRequest previewRequest;
    private CameraCaptureSession captureSession;
    private MediaRecorder mMediaRecorder;
    private Size mVideoSize;
    private Size mPreviewSize;
    private String mNextVideoAbsolutePath;
    private Long systemTime;
    private View view;

    private String datatime;

    /**
     * 5s内不让他弹出菜单来快进快退
     */
    private int i = 0;

    /**
     * 判断是否继续执行循环操作
     *
     * @param savedInstanceState
     */
    private boolean cycle = true;

    AutoDismissPopupWindow autoDismissPopupWindow;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_record);
        ButterKnife.bind(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //控制ijkplayer先隐藏，来达到禁止效果
        ijkPlayerTextureView.setVisibility(View.GONE);
        ijkPlayerImg.setVisibility(View.VISIBLE);
        //获取detail页面传入的参数
        Intent intent = getIntent();
        VideoFileName = intent.getStringExtra("videoFileName");
        videoId = intent.getIntExtra("videoId", 0);
        mCameraId = intent.getStringExtra("mCameraId");
        thumbnailPath = intent.getStringExtra("thumbnailPath");
        /**
         * 配置录像的存储位置，指定相应的文件夹
         */
        File externalStorageFile = getApplicationContext().getExternalFilesDir("").getAbsoluteFile();
        mVideoPath = new File(externalStorageFile, "MY_Video");
        if (!mVideoPath.exists()) {
            mVideoPath.mkdir();
        }
        isPlaying = false;
        inintNative();
        /**
         * 动态申请权限，确保已经开启相应的摄像机功能
         */
        rootLayout = findViewById(R.id.root);
        requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0x123);
    }

    /**
     * 初始化播放器,引入so包
     */

    private void inintNative() {
        IjkMediaPlayer.loadLibrariesOnce(null);
        try {
            IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        } catch (Exception e) {
            this.finish();
        }
        Glide.with(getApplicationContext()).load(Constants.PhotoPath + thumbnailPath).into(ijkPlayerImg);
        init();

        String a = VideoFileName;
        ijkPlayerTextureView.setAssetVideoPath(a);


        /**
         * 倒计时5s,结束后播放视频
         */
        CountDownTimer countDownTimer = new CountDownTimer(5000L + 300L, 1000L) {
            @Override
            public void onTick(long l) {
                timeDataTv.setText("" + l / 1000);
            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onFinish() {
                i = 1;
                relVisible.setVisibility(View.VISIBLE);
                ijkPlayerImg.setVisibility(View.GONE);
                ijkPlayerTextureView.setVisibility(View.VISIBLE);
                timeDataTv.setVisibility(View.GONE);
                isPlaying = true;
                ijkPlayerTextureView.start();
                getpoll();
                startRecordingVideo();
            }
        };
        countDownTimer.start();
    }

    /**
     * 设置进度条
     */
    private void getpoll() {
        runnable = new Runnable() {
            @Override
            public void run() {
                if (cycle) {
                    refreshTime();
                    handler.postDelayed(this, 1000);
                } else if (!cycle) {
                    handler.removeCallbacksAndMessages(null);
                }

            }
        };
        handler.postDelayed(runnable, 1000);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void refreshTime() {
        current = ijkPlayerTextureView.getCurrentPosition() / 1000;
        duration = ijkPlayerTextureView.getDuration() / 1000;

        seconds = current % 60;
        minutes = current / 60;

        String time = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        ijkPlayerTv.setText(time);
        if (duration != 0) {
            ijkplayerSeekbar.setProgress((int) ((current * 100) / duration));
            if ((current == duration) || (duration - current) == 1) {
                cycle = false;
                ijkPlayerTextureView.stop();
                ijkplayerSeekbar.setProgress(100);
                mMediaRecorder.setOnErrorListener(null);
                mMediaRecorder.stop();
                PopUpwindow("pop2");
            }
        }
    }

    private void finishThePlayer() {
        LocalDate localDate = LocalDate.now();
        String localTime = localDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
        /**
         * 看完一条记录，加入相应数据库数据
         */
        int count = LitePal.where("videoId=? and playEndTime = ?", String.valueOf(videoId), localTime).count(VideoHistory.class);
        if (count == 0) {
            VideoHistory videoHistory = new VideoHistory();
            videoHistory.setVideoId(videoId);
            videoHistory.setPlayEndTime(localTime);
            videoHistory.setUserId(RxSPTool.getInt(getApplicationContext(), Constants.USER_ID));
            videoHistory.save();
        }
        updateRate(localTime);
        pushToSocket();
        ijkPlayerTextureView.release();
//        finish();
    }

    //更新百分比进度
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateRate(String trainingDay) {
        int watchNum = 0;
        JSONObject requestDate = new JSONObject();
        List<Video> videoList = LitePal.select("*").where("specifyStartTime <=? and specifyEndTime >=?", trainingDay, trainingDay).find(Video.class);
        List<Video> videoList1 = LitePal.select("*").where("specifyEndTime is null and specifyStartTime <= ?", trainingDay).find(Video.class);
        List<Video> videoListAll = new ArrayList<>();
        videoListAll.addAll(videoList);
        videoListAll.addAll(videoList1);

        for (int i=0;i<videoListAll.size();i++){
            int count = LitePal.where("videoId = ? and playEndTime = ?", String.valueOf(videoListAll.get(i).getVideoId()), trainingDay).count(VideoHistory.class);
            if (count == 0) {
                String watched = "no";
            } else {
                watchNum++;
            }
        }
        int watched = watchNum;
        int total = videoListAll.size();
        int percent = watched * 100 / total;

        try {
            requestDate.put("rate", String.valueOf(percent));
            requestDate.put("trainingDay", trainingDay);
            requestDate.put("userId", RxSPTool.getInt(getApplicationContext(), Constants.USER_ID));
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), requestDate.toString());
            Call<MsgModel> updateRate = HttpHelper.getInstance().create(HomeTrainService.class).updateRate(RxSPTool.getString(getApplicationContext(), Constants.TOKEN), requestBody);
            updateRate.enqueue(new Callback<MsgModel>() {
                @SuppressLint("WrongConstant")
                @Override
                public void onResponse(Call<MsgModel> call, Response<MsgModel> response) {
                    if (response.body().getCode() == 200) {
                    } else {
                        Toast.makeText(VideoRecordActivity.this, response.body().getMsg(), Constants.Toast_Length).show();
                    }
                }

                @SuppressLint("WrongConstant")
                @Override
                public void onFailure(Call<MsgModel> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), getString(R.string.no_Wifi_training), Constants.Toast_Length).show();
                }
            });

        } catch (JSONException e) {
            PLog.e(TAG + "updateRate", e.toString());
        }


    }

    //更新视频记录
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void pushToSocket() {
        JSONObject requestDate = new JSONObject();
        LocalDateTime localDate = LocalDateTime.now();
        String playEndTime = localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        try {
            requestDate.put("playEndTime", playEndTime);
            requestDate.put("userId", String.valueOf(RxSPTool.getInt(getApplicationContext(), Constants.USER_ID)));
            requestDate.put("videoId", String.valueOf(videoId));

            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), requestDate.toString());
            Call<MsgModel> videoInsert = HttpHelper.getInstance().create(HomeTrainService.class).videoInsert(RxSPTool.getString(getApplicationContext(), Constants.TOKEN), requestBody);
            videoInsert.enqueue(new Callback<MsgModel>() {
                @SuppressLint("WrongConstant")
                @Override
                public void onResponse(Call<MsgModel> call, Response<MsgModel> response) {
                    if (response.body().getCode() == 200) {
                        Toast.makeText(VideoRecordActivity.this, getString(R.string.tip_save_video_success), Constants.Toast_Length).show();
                    } else {
                        Toast.makeText(VideoRecordActivity.this, response.body().getMsg(), Constants.Toast_Length).show();
                    }
                }

                @SuppressLint("WrongConstant")
                @Override
                public void onFailure(Call<MsgModel> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), getString(R.string.no_Wifi_training), Constants.Toast_Length).show();
                }
            });
        } catch (JSONException e) {
            PLog.e(TAG + "/pushToSocket", e.toString());
        }
    }

    /**
     * 初始化ijkplayer播放器
     */
    public void init() {
        ijkPlayerTextureView.setVideoPlayerListener(new VideoPlayerListener() {
            @Override
            public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int i) {
                PLog.w(TAG, "init_updateVideo");
            }

            @Override
            public void onCompletion(IMediaPlayer iMediaPlayer) {
                PLog.w(TAG, "init_complete");
            }

            @Override
            public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
                return false;
            }

            @Override
            public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
                return false;
            }

            @Override
            public void onPrepared(IMediaPlayer iMediaPlayer) {
                iMediaPlayer.start();
            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onSeekComplete(IMediaPlayer iMediaPlayer) {
                PLog.w(TAG, "init_SeekCompletion");
            }

            @Override
            public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int i, int i1, int i2, int i3) {
                PLog.w(TAG, "init_VideoSize");
            }
        });
    }


    /**
     * 录制视频模块
     */
    private final TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
            openCamera(width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
            configureTransform(width, height);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

        }
    };

    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            VideoRecordActivity.this.cameraDevice = cameraDevice;
            createCameraPreviewSession();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            cameraDevice.close();
            VideoRecordActivity.this.cameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int error) {
            cameraDevice.close();
            VideoRecordActivity.this.cameraDevice = null;
            VideoRecordActivity.this.finish();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        rootLayout.setOnClickListener(view -> {
            if (i == 1) {
                PopUpwindow("pop1");
            }
        });
        if (isPlaying == false) {
            isPlaying = true;
            ijkPlayerTextureView.start();
        }


        NoDrag();

    }

    /**
     * 禁止拖动
     */
    private void NoDrag() {
        ijkplayerSeekbar.setOnTouchListener((view, motionEvent) -> true);
    }


//    private void clickChangeView() {
//
//
//    }


    @Override
    protected void onPause() {
        super.onPause();
        isPlaying = false;
        ijkPlayerTextureView.pause();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0x123 && grantResults.length == 3 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            textureView = new VideoTextureView(VideoRecordActivity.this, null);
//            RelativeLayout relativeLayout = new RelativeLayout(getApplicationContext());
//            relativeLayout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(400, 333);
//            textureView.setLayoutParams(layoutParams);
            FrameLayout.LayoutParams frameLayout = new FrameLayout.LayoutParams(ViewUtils.dp2px(VideoRecordActivity.this, 224), ViewUtils.dp2px(VideoRecordActivity.this, 157));
            frameLayout.gravity = Gravity.RIGHT | Gravity.BOTTOM;
//            frameLayout.bottomMargin = ViewUtils.dp2px(VideoRecordActivity.this, 80);
//            frameLayout.rightMargin = ViewUtils.dp2px(VideoRecordActivity.this, 20);
            textureView.setLayoutParams(frameLayout);

            // TEST CODE START
            textureView.setTranslationZ(1);

            FrameLayout.LayoutParams playerLayoutParams = (FrameLayout.LayoutParams) ijkPlayerTextureView.getLayoutParams();
            playerLayoutParams.width = getResources().getDisplayMetrics().widthPixels;
            playerLayoutParams.height = getResources().getDisplayMetrics().heightPixels - ViewUtils.dp2px(this, 80);
            ijkPlayerTextureView.setLayoutParams(playerLayoutParams);

            // @see onRequireViewChanged
            ijkPlayerTextureView.setOnClickListener(v -> {
//                onRequireViewChanged();
                PopUpwindow("pop1");
            });
            textureView.setOnClickListener(v -> {
                onRequireViewChanged();
            });

            view = new View(VideoRecordActivity.this, null);
            FrameLayout.LayoutParams viewParams = new FrameLayout.LayoutParams(ViewUtils.dp2px(VideoRecordActivity.this, 30), ViewUtils.dp2px(VideoRecordActivity.this, 30));
            viewParams.gravity = Gravity.RIGHT | Gravity.TOP;
            viewParams.topMargin = ViewUtils.dp2px(VideoRecordActivity.this, 40);
            viewParams.rightMargin = ViewUtils.dp2px(VideoRecordActivity.this, 40);
            view.setLayoutParams(viewParams);
            view.setBackgroundResource(R.drawable.red_point);
            view.setTranslationZ(2);
            textureView.setSurfaceTextureListener(mSurfaceTextureListener);
            rootLayout.addView(textureView);
            rootLayout.addView(view);
            pointHandler.sendEmptyMessageDelayed(1, 1000);
        }
    }

    /**
     * change record/player view size
     */
    private void onRequireViewChanged() {
        FrameLayout.LayoutParams recordLayoutParams = (FrameLayout.LayoutParams) textureView.getLayoutParams();
        FrameLayout.LayoutParams playerLayoutParams = (FrameLayout.LayoutParams) ijkPlayerTextureView.getLayoutParams();
        if (textureView.getTranslationZ() == 1) {
            //record view is mini
            //change record view layout params and set translation Z to 0
            recordLayoutParams.topMargin = 0;
            recordLayoutParams.rightMargin = 0;
            recordLayoutParams.width = getResources().getDisplayMetrics().widthPixels;
            recordLayoutParams.height = getResources().getDisplayMetrics().heightPixels;
            textureView.setLayoutParams(recordLayoutParams);
            textureView.setTranslationZ(0);
            textureView.setAspectRatio(recordLayoutParams.width, recordLayoutParams.height);
            //change player view layout params and set translation Z to 1
            playerLayoutParams.gravity = Gravity.END | Gravity.BOTTOM;
            playerLayoutParams.bottomMargin = ViewUtils.dp2px(this, 0);
//            playerLayoutParams.rightMargin = ViewUtils.dp2px(this, 20);
            playerLayoutParams.width = ViewUtils.dp2px(this, 224);
            playerLayoutParams.height = ViewUtils.dp2px(this, 157);
            ijkPlayerTextureView.setLayoutParams(playerLayoutParams);
            ijkPlayerTextureView.setTranslationZ(1);
            ((View) ijkplayerSeekbar.getParent()).setVisibility(View.GONE);
            textureView.setOnClickListener(view -> {
                PopUpwindow("pop1");
            });
            ijkPlayerTextureView.setOnClickListener(view -> {
                onRequireViewChanged();
            });
        } else {
            //change record view layout params and set translation Z to 1
            playerLayoutParams.width = ViewUtils.dp2px(this, 224);
            playerLayoutParams.height = ViewUtils.dp2px(this, 157);
            textureView.setLayoutParams(playerLayoutParams);
            textureView.setTranslationZ(1);
            textureView.setAspectRatio(playerLayoutParams.width, playerLayoutParams.height);
            //change player view layout params and set translation Z to 0
            recordLayoutParams.height = getResources().getDisplayMetrics().heightPixels - ViewUtils.dp2px(this, 80);
            ijkPlayerTextureView.setLayoutParams(recordLayoutParams);
            ijkPlayerTextureView.setTranslationZ(0);
            ((View) ijkplayerSeekbar.getParent()).setVisibility(View.VISIBLE);
            ijkPlayerTextureView.setOnClickListener(view -> {
                PopUpwindow("pop1");
            });
            textureView.setOnClickListener(view -> {
                onRequireViewChanged();
            });
        }
    }


    Handler pointHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            view.setVisibility(view.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            pointHandler.sendEmptyMessageDelayed(1, 1000);
        }
    };


    private void setUpMediaRecorder(int width, int height) throws IOException {
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(mCameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.
                    SCALER_STREAM_CONFIGURATION_MAP);

            Size largest = Collections.max(
                    Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)),
                    new CompareSizesByArea());
            mVideoSize = chooseVideoSize(map.getOutputSizes(MediaRecorder.class));
            mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class),
                    width, height, mVideoSize);
            previewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class),
                    width, height, largest);
            int orientation = getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                textureView.setAspectRatio(previewSize.getWidth(), previewSize.getHeight());
            } else {
                textureView.setAspectRatio(previewSize.getHeight(), previewSize.getWidth());
            }
            SetMediaRecorder();


        } catch (CameraAccessException e) {
            PLog.e(TAG + "/setUpMediaRecorder", e.toString());
        } catch (NullPointerException e) {
//            System.out.println("出现错误。");

        }
    }

    private void SetMediaRecorder() {
        try {
            mMediaRecorder = new MediaRecorder();
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mNextVideoAbsolutePath = "";
            File externalStorageDirectory = Environment.getExternalStorageDirectory();
            File dir = new File(externalStorageDirectory, "_MyVideo");
            if (!dir.exists()) {
                dir.mkdir();
            }
            systemTime = System.currentTimeMillis();
            mNextVideoAbsolutePath = dir.getAbsolutePath() + "/video_" + systemTime + ".mp4";
            mMediaRecorder.setOutputFile(mNextVideoAbsolutePath);
            mMediaRecorder.setVideoEncodingBitRate(10000000);
            mMediaRecorder.setVideoFrameRate(30);
            mMediaRecorder.setVideoSize(mVideoSize.getWidth(), mVideoSize.getHeight());
            mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mMediaRecorder.prepare();
        } catch (IOException e) {
            PLog.e(TAG + "/SetMediaRecorder", e.toString());
        }
    }

    private Size chooseVideoSize(Size[] choices) {
        for (Size size : choices) {
            if (size.getWidth() == size.getHeight() * 4 / 3 && size.getWidth() <= 1080) {
                Log.i(TAG, "chooseVideoSize: " + size.toString());
                return size;
            }
        }
        PLog.e(TAG, "Couldn't find any suitable video size");
        return choices[choices.length - 1];
    }

    private void stopRecordingVideo() {
        try {
            mMediaRecorder.setOnErrorListener(null);
            mMediaRecorder.stop();

        } catch (RuntimeException stopException) {
        }
        mMediaRecorder.release();
        mMediaRecorder = null;
        Toast.makeText(this, getString(R.string.tip_save_video_success), Toast.LENGTH_LONG).show();

        RecordVideo recordVideo = new RecordVideo();
        recordVideo.setRecordPath(mNextVideoAbsolutePath);

        recordVideo.setDuration(Utils.getVideoDuration(mNextVideoAbsolutePath));
        recordVideo.setRecordImg(Utils.getVideoPhotoPath(mNextVideoAbsolutePath, systemTime));

        LocalDate localDate = LocalDate.now();
        datatime = localDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
        recordVideo.setRecordDate(datatime);
        recordVideo.setVideoId(videoId);

        LocalDateTime localDateTime=LocalDateTime.now();
        String recordDateTime=localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        recordVideo.setRecordDateTime(recordDateTime);
        recordVideo.save();

//        createCameraPreviewSession();
    }


    private Range<Integer> getRange() {
        CameraManager mCameraManager = (CameraManager) getApplicationContext().getSystemService(Context.CAMERA_SERVICE);
        CameraCharacteristics chars = null;
        try {
            if (mCameraManager != null) {
                chars = mCameraManager.getCameraCharacteristics(mCameraId);
            }
        } catch (CameraAccessException e) {
            PLog.e(TAG + "/run", e.toString());
        }
        Range<Integer> result = null;
        if (chars != null) {
            Range<Integer>[] ranges = chars.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES);
            if (ranges != null) {
                for (Range<Integer> range : ranges) {
                    if (range.getLower() < 10)
                        continue;
                    if (result == null)
                        result = range;
                    else if (range.getLower() <= 15 && (range.getUpper() - range.getLower()) > (result.getUpper() - result.getLower()))
                        result = range;
                }
            }
        }
        return result;
    }


    private void updatePreview() {
        if (null == cameraDevice) {
            return;
        }
        try {
//            previewRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

            previewRequestBuilder.set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, getRange());
            previewRequestBuilder.set(CaptureRequest.CONTROL_AE_LOCK, false);
            previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START);


            captureSession.setRepeatingRequest(previewRequestBuilder.build(), null, null);
        } catch (CameraAccessException e) {
            PLog.e(TAG + "/updatePreview", e.toString());
        }
    }

    private void startRecordingVideo() {
        EventBus.getDefault().register(this);
        if (null == cameraDevice || !textureView.isAvailable() || null == mPreviewSize) {
            return;
        }
        try {
            SurfaceTexture texture = textureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            CaptureRequest.Builder captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            List<Surface> surfaces = new ArrayList<>();

            Surface previewSurface = new Surface(texture);
            surfaces.add(previewSurface);
            if (mMediaRecorder == null) {
                SetMediaRecorder();
            }
            Surface recorderSurface = mMediaRecorder.getSurface();
            surfaces.add(recorderSurface);
            captureRequestBuilder.addTarget(recorderSurface);
            previewRequestBuilder.addTarget(recorderSurface);
            cameraDevice.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession CaptureSession) {
                    captureSession = CaptureSession;
                    updatePreview();
                    mMediaRecorder.start();
//                    record.setEnabled(false);
//                    stop.setEnabled(true);
                }

                @SuppressLint("WrongConstant")
                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(VideoRecordActivity.this, getString(R.string.setting_failure),
                            Constants.Toast_Length).show();
                }
            }, null);
        } catch (CameraAccessException e) {
            PLog.e(TAG + "/startRecordingVideo", e.toString());
        }
    }

    private void configureTransform(int viewWidth, int viewHeight) {
        if (null == previewSize) {
            return;
        }
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, previewSize.getHeight(), previewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) viewHeight / previewSize.getHeight(),
                    (float) viewWidth / previewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90F * (rotation - 2), centerX, centerY);
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180F, centerX, centerY);
        }
        textureView.setTransform(matrix);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void openCamera(int width, int height) {
        //表示输出设置（拍照后的保存设置）
        try {
            setUpMediaRecorder(width, height);
        } catch (IOException e) {
            PLog.e(TAG + "/openCamera", e.toString());
        }
        configureTransform(width, height);
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            // 如果用户没有授权使用摄像头，直接返回
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            // 打开摄像头
            manager.openCamera(mCameraId, stateCallback, null); // ①
        } catch (CameraAccessException e) {
            PLog.e(TAG + "/openCamera", e.toString());
        }
    }

    private void createCameraPreviewSession() {
        try {
            SurfaceTexture texture = textureView.getSurfaceTexture();
            texture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
            Surface surface = new Surface(texture);
            // 创建作为预览的CaptureRequest.Builder
            previewRequestBuilder = cameraDevice
                    .createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            // 将textureView的surface作为CaptureRequest.Builder的目标

            //给此次请求添加一个Surface对象作为图像的输出目标，
            // CameraDevice返回的数据送到这个target surface中
            previewRequestBuilder.addTarget(surface);
            // 创建CameraCaptureSession，该对象负责管理处理预览请求和拍照请求
            //第一个参数是一个数组，表示对相机捕获到的数据进行处理的相关容器组
            //第二个参数是状态回调
            //第三个参数设置线程
            cameraDevice.createCaptureSession(Arrays.asList(surface),
                    new CameraCaptureSession.StateCallback() // ③
                    {
                        //完成配置时回调，可以开始拍照或预览、录像
                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                            // 如果摄像头为null，直接结束方法
                            if (null == cameraDevice) {
                                return;
                            }
                            // 当摄像头已经准备好时，开始显示预览
                            captureSession = cameraCaptureSession;
                            // 设置自动对焦模式
                            previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                            // 设置自动曝光模式
                            previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                                    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
                            // 开始显示相机预览
                            previewRequest = previewRequestBuilder.build();
                            try {
                                // 设置预览时连续捕获图像数据
                                captureSession.setRepeatingRequest(previewRequest, null, null);  // ④
                            } catch (CameraAccessException e) {
                                PLog.e(TAG + "/createCameraPreviewSession", e.toString());
                            }
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                            Toast.makeText(VideoRecordActivity.this, getString(R.string.setting_failure),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }, null);
        } catch (CameraAccessException e) {
            PLog.e(TAG + "/createCameraPreviewSession", e.toString());
        }
    }


    private static Size chooseOptimalSize(Size[] choices
            , int width, int height, Size aspectRatio) {
        List<Size> bigEnough = new ArrayList<>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (Size option : choices) {
            if (option.getHeight() == option.getWidth() * h / w &&
                    option.getWidth() >= width && option.getHeight() >= height) {
                bigEnough.add(option);
            }
        }
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else {
//            System.out.println("找不到合适的预览尺寸！！！");
            return choices[0];
        }
    }


    static class CompareSizesByArea implements Comparator<Size> {
        @Override
        public int compare(Size lhs, Size rhs) {
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }
    }


    @Override
    public void onBackPressed() {
//        cameraDevice.close();
//        ijkPlayerTextureView.release();
//        mMediaRecorder.setOnErrorListener(null);
//        mMediaRecorder.stop();
//        mMediaRecorder.release();
//        mMediaRecorder = null;
//        finish();
        if (autoDismissPopupWindow !=null && autoDismissPopupWindow.isShowing()){
            autoDismissPopupWindow.dismiss();
        }
        if (i==0){
            finish();
        }else {
            PopUpwindow("pop1");
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void PopUpwindow(String form) {
        View view = View.inflate(textureView.getContext(), R.layout.video_record_popup, null);
        PopupWindow popupWindow = new PopupWindow(view);
        TextView video_record_tv_fir = view.findViewById(R.id.video_record_tv_fir);
        TextView video_record_tv_sec = view.findViewById(R.id.video_record_tv_sec);
        Button cancel_button = view.findViewById(R.id.cancel_button);
        Button determine_button = view.findViewById(R.id.determine_button);
        popupWindow.setFocusable(true);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
//        int width = metrics.widthPixels / 16 * 10;
//        int height = metrics.heightPixels / 8 * 6;
             int width= RxImageTool.dp2px(800);
             int height=RxImageTool.dp2px(500);
        popupWindow.setWidth(width);
        popupWindow.setHeight(height);

        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        darkenBackground(0.2f);

        popupWindow.setOnDismissListener(() -> darkenBackground(1f));

        if (form.equals("pop1")) {
            SpannableString fir_text = new SpannableString("動画を停止しますか?");
            ForegroundColorSpan span = new ForegroundColorSpan(Color.RED);
            fir_text.setSpan(span, 3, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            video_record_tv_fir.setText(fir_text);
            video_record_tv_sec.setText("（録画は中断されます）");
            cancel_button.setOnClickListener(view1 -> {
                //是否停止，取消停止
                if (video_record_tv_fir.getText().toString().equals("動画を停止しますか?")) {
                    popupWindow.dismiss();
                } else {
                    //是否保存，取消保存
                    File file = new File(mNextVideoAbsolutePath);
                    boolean boo = false;
                    if (file.exists()) {
                        boo = file.delete();
                    }
                    if (!boo) {
                        PLog.e(TAG, "delete_fail_pop1");
                    }

                    cameraDevice.close();
                    ijkPlayerTextureView.release();
                    //fix sometime has crash when finish this page
                    if (mMediaRecorder != null) {
                        mMediaRecorder.setOnErrorListener(null);
                        mMediaRecorder.stop();
                        mMediaRecorder.release();
                        mMediaRecorder = null;
                    }

                    finish();
                }
            });

            determine_button.setOnClickListener(view12 -> {
                if (video_record_tv_fir.getText().toString().equals("動画を停止しますか?")) {
                    //是否停止，确定停止
                    video_record_tv_fir.setText("録画を終了しました");
                    SpannableString sec_text = new SpannableString("録画動画を保存しますか？");
                    ForegroundColorSpan span2 = new ForegroundColorSpan(Color.GREEN);
                    sec_text.setSpan(span2, 5, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    video_record_tv_sec.setText(sec_text);
                } else {
                    popupWindow.dismiss();
                    stopRecordingVideo();
                    if (cameraDevice != null) {
                        cameraDevice.close();
                    }
                    ijkPlayerTextureView.release();
//                    JSONObject requestDate = new JSONObject();
//                    try {
//                        List<RecordVideo>recordVideoList= LitePal.select("id").where("recordPath=?", mNextVideoAbsolutePath).find(RecordVideo.class);
//                        String id=String.valueOf(recordVideoList.get(0).getId());
//                        requestDate.put("recordingId", id);
//                        requestDate.put("completionDate", datatime);
//                        requestDate.put("defaultName", mNextVideoAbsolutePath);
//                        requestDate.put("duration", Utils.getVideoDuration(mNextVideoAbsolutePath));
//                        requestDate.put("recordImg", Utils.getVideoPhotoPath(mNextVideoAbsolutePath, systemTime));
//                        requestDate.put("videoId", String.valueOf(videoId));
//                        requestDate.put("userId", String.valueOf(RxSPTool.getInt(getApplicationContext(), Constants.USER_ID)));
//                        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), requestDate.toString());
//                        Call<MsgModel> recordingAdd = HttpHelper.getInstance().create(HomeTrainService.class).recordingAdd(RxSPTool.getString(getApplicationContext(), Constants.TOKEN), requestBody);
//                        recordingAdd.enqueue(new Callback<MsgModel>() {
//                            @SuppressLint("WrongConstant")
//                            @Override
//                            public void onResponse(Call<MsgModel> call, Response<MsgModel> response) {
//                                if (response.body().getCode() == 200) {
//                                } else {
//                                    Toast.makeText(VideoRecordActivity.this, response.body().getMsg(), Constants.Toast_Length).show();
//                                }
//                            }
//
//                            @Override
//                            public void onFailure(Call<MsgModel> call, Throwable t) {
//                                PLog.e("VideoRecordActivity", t.getMessage());
//                            }
//                        });
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
                    Intent intent = new Intent(this, MainHomeActivity.class);
                    intent.putExtra("id", 4);
                    startActivity(intent);
                    finish();
                }
            });
        } else if (form.equals("pop2")) {
            video_record_tv_fir.setText("録画を終了しました");
            SpannableString sec_text = new SpannableString("録画動画を保存しますか？");
            ForegroundColorSpan span2 = new ForegroundColorSpan(Color.GREEN);
            sec_text.setSpan(span2, 5, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            video_record_tv_sec.setText(sec_text);
            cancel_button.setOnClickListener(view13 -> {
                File file = new File(mNextVideoAbsolutePath);
                boolean boo = false;
                if (file.exists()) {
                    boo = file.delete();
                }
                if (!boo) {
                    PLog.e(TAG, "delete_fail_pop2");
                }

                cameraDevice.close();
                finishThePlayer();
//                mMediaRecorder.setOnErrorListener(null);
//                mMediaRecorder.stop();
                mMediaRecorder.release();
                mMediaRecorder = null;
                finish();
            });
            determine_button.setOnClickListener(view14 -> {
                popupWindow.dismiss();
                stopRecordingVideo();
                if (cameraDevice != null) {
                    cameraDevice.close();
                }
                finishThePlayer();
                Intent intent = new Intent(this, MainHomeActivity.class);
                intent.putExtra("id", 4);
                startActivity(intent);
                finish();

            });

        }


    }

    private void darkenBackground(float bgcolor) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgcolor;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setAttributes(lp);
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
            stopRecordingVideo();
            if (cameraDevice != null) {
                cameraDevice.close();
            }
            ijkPlayerTextureView.release();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
