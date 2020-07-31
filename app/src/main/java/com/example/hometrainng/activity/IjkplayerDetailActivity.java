package com.example.hometrainng.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.widget.NestedScrollView;

import com.bumptech.glide.Glide;
import com.example.hometrainng.R;
import com.example.hometrainng.api.HomeTrainService;
import com.example.hometrainng.customview.IjkplayerVideoView;
import com.example.hometrainng.customview.VideoPlayerListener;
import com.example.hometrainng.db.RecordVideo;
import com.example.hometrainng.db.Video;
import com.example.hometrainng.db.VideoHistory;
import com.example.hometrainng.entity.MsgModel;
import com.example.hometrainng.retrofit.Constants;
import com.example.hometrainng.retrofit.HttpHelper;
import com.example.hometrainng.tools.PLog;
import com.example.hometrainng.tools.Utils;
import com.tamsiree.rxkit.RxFileTool;
import com.tamsiree.rxkit.RxImageTool;
import com.tamsiree.rxkit.RxSPTool;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class IjkplayerDetailActivity extends BaseActivity {
    private static final String TAG = "IjkplayerDetailActivity";

    @BindView(R.id.back_img)
    ImageView backImg;
    @BindView(R.id.play_video_img)
    ImageView playVideoImg;
    @BindView(R.id.play_img)
    ImageView playImg;
    @BindView(R.id.ijkplayer_detail_title)
    TextView ijkplayerDetailTitle;
    @BindView(R.id.ijkplayer_detail_videoCommonComment)
    TextView ijkplayerDetailVideoCommonComment;
    @BindView(R.id.specifyStartTime)
    TextView specifyStartTime;
    @BindView(R.id.ijkplayer_detail_individualComment)
    TextView ijkplayerDetailIndividualComment;
    @BindView(R.id.image_detail)
    ImageView imageDetail;
    @BindView(R.id.time_all)
    TextView timeAll;
    @BindView(R.id.nest_ijkplayer_detail)
    NestedScrollView nestIjkplayerDetail;
    @BindView(R.id.title_linearLayout)
    LinearLayout titleLinearLayout;

    @BindView(R.id.linear_left_part)
    LinearLayout linearLeftPart;
    @BindView(R.id.ijkPlayer_frameLayout)
    FrameLayout ijkPlayerFrameLayout;

    private List<Video> videoList;
    private String videoTitle;
    private int videoId;

    //ijkplayer
    @BindView(R.id.ijkPlayer_textureView)
    IjkplayerVideoView ijkPlayerTextureView;
    @BindView(R.id.button_relativeLayout)
    RelativeLayout buttonRelativeLayout;
    @BindView(R.id.ijkplayer_seekbar)
    SeekBar ijkplayerSeekbar;
    @BindView(R.id.ijkPlayer_tv)
    TextView ijkPlayerTv;
    @BindView(R.id.full_screen)
    ImageView fullScreen;
    //    @BindView(R.id.duration_tv_all)
//    TextView durationTvAll;
    @BindView(R.id.seek_relativelayout)
    RelativeLayout seekRel;
    private String VideoFileName;
    private boolean isPlaying = false;
    private Handler handler = new Handler();
    private Runnable runnable;
    private ImageView retreat_img, play_img, fast_forward_img, back_img;
    private long current, duration, seconds, minutes;

    private boolean state = false;

    private PopupWindow popupWindow;
    //判断是否需要向后台发送请求
    private boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ijkplayer_detail);
        ButterKnife.bind(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Intent intent = getIntent();
        videoTitle = intent.getStringExtra("videoTitle");
        videoId = intent.getIntExtra("videoId", 0);
       flag=true;
        initDate();

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onResume() {
        super.onResume();
        ijkPlayerTextureView.setOnClickListener(view -> {
            if (!state){
                PopUpwindow2();
            }else {
                PopUpwindow();
            }
        });

        seekRel.setOnTouchListener((view, event) -> {
            Rect seekRect = new Rect();
            ijkplayerSeekbar.getHitRect(seekRect);
            if ((event.getY() >= (seekRect.top - 500)) && (event.getY() <= (seekRect.bottom + 500))) {
                float y = seekRect.top + (float)(seekRect.height()) / 2;
                //seekBar only accept relative x
                float x = event.getX() - seekRect.left;
                if (x < 0) {
                    x = 0;
                } else if (x > seekRect.width()) {
                    x = seekRect.width();
                }
                MotionEvent me = MotionEvent.obtain(event.getDownTime(), event.getEventTime(),
                        event.getAction(), x, y, event.getMetaState());
                return ijkplayerSeekbar.onTouchEvent(me);
            }
            return false;
        });
    }

    @SuppressLint("WrongConstant")
    private void initDate() {
        LocalDate localDate = LocalDate.now();
        String dateString = localDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
        ijkplayerDetailTitle.setText(videoTitle);
        videoList = LitePal.select("*").where("videoId= ? and specifyStartTime <= ?", String.valueOf(videoId),dateString).order("specifyStartTime desc").find(Video.class);

        if (videoList.isEmpty()) {
            Toast.makeText(getApplicationContext(), getString(R.string.tip_video_play_no_exist), Constants.Toast_Length).show();
            finish();
            return;
        }
        ijkplayerDetailVideoCommonComment.setText(videoList.get(0).getVideoCommonComment());
        specifyStartTime.setText(Utils.getDatePoint(videoList.get(0).getSpecifyStartTime()));
        ijkplayerDetailIndividualComment.setText(videoList.get(0).getIndividualComment());
        timeAll.setText(videoList.get(0).getDuration());
//        durationTvAll.setText(videoList.get(0).getDuration());
        Glide.with(getApplicationContext()).load(Constants.PhotoPath + videoList.get(0).getThumbnailPath()).into(imageDetail);


        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        //创建一个文件夹
        File dir2 = new File(externalStorageDirectory, "_MyVideo");
        if (!dir2.exists()) {
            dir2.mkdir();
        }
        long a = RxFileTool.getDirSize(Constants.RecordVideoPath);
        int b = Math.toIntExact(a / 1024 / 1024 / 1024);

        int count = LitePal.count(RecordVideo.class);
        if (count > 999 || b < 20) {
            playVideoImg.setImageResource(R.mipmap.play_video_no_img);
            playVideoImg.setClickable(false);
        } else {
            playVideoImg.setImageResource(R.mipmap.menu_video);
            playVideoImg.setClickable(true);
        }

        VideoFileName = videoList.get(0).getVideoFileName();
        initNative();
    }

    @OnClick({R.id.back_img, R.id.play_video_img, R.id.play_img, R.id.full_screen})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_img:
                finish();
                break;
            case R.id.play_video_img:
                if (!Utils.checkNet2(getApplicationContext())){
                    PopNoNetWork2();
                }else {
                    Intent intent = new Intent(this, VideoPreviewActivity.class);
                    intent.putExtra("videoFileName", videoList.get(0).getVideoFileName());
                    intent.putExtra("videoId", videoId);
                    intent.putExtra("thumbnailPath", videoList.get(0).getThumbnailPath());
                    startActivity(intent);
                }
                break;
            case R.id.play_img:
                List<RecordVideo> recordVideoList = LitePal.select("*").order("recordDate desc").find(RecordVideo.class);
                if (recordVideoList.size() != 0) {
                    String data = recordVideoList.get(0).getRecordDate();
                    LocalDate goaldate = LocalDate.parse(data, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    int number = Math.abs(Math.toIntExact(LocalDate.now().until(goaldate, ChronoUnit.DAYS)));
                    if (number > 14) {
                        PopWindow();
                    } else {
                        if (!Utils.checkNet2(getApplicationContext())){
                            PopNoNetWork();
                        }else {
                            startIntent();
                        }
                    }
                } else {
                    if (!Utils.checkNet2(getApplicationContext())){
                        PopNoNetWork();
                    }else {
                        startIntent();
                    }
                }
                break;
            case R.id.full_screen:
                if (!state) {
                    //全屏显示进度条
//                        addTextureView();
                    fullScreen.setImageResource(R.mipmap.no_full_screen);
                    //放大屏幕并且state变为放大
                    state = true;
                    addTextureView();
                } else {
                    minuTextureView();
                    fullScreen.setImageResource(R.mipmap.full_screen);
                    state = false;
                }
                break;
            default:
                PLog.e(TAG, "onViewClicked_err");
                break;
        }
    }

    private void minuTextureView() {

        titleLinearLayout.setVisibility(View.VISIBLE);
        seekRel.setVisibility(View.VISIBLE);
        nestIjkplayerDetail.setVisibility(View.VISIBLE);

        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(RxImageTool.dp2px(790),ViewGroup.LayoutParams.MATCH_PARENT);
        linearLeftPart.setLayoutParams(layoutParams);

        LinearLayout.LayoutParams layoutParams2=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams2.setMargins(0,0,0,0);
        titleLinearLayout.setLayoutParams(layoutParams2);

        LinearLayout.LayoutParams layoutParams1=new LinearLayout.LayoutParams(RxImageTool.dp2px(720),RxImageTool.dp2px(56));
        layoutParams1.setMargins(RxImageTool.dp2px(70),RxImageTool.dp2px(20),0,0);
        seekRel.setLayoutParams(layoutParams1);


        LinearLayout.LayoutParams layoutParamsFrame=new LinearLayout.LayoutParams(RxImageTool.dp2px(720),RxImageTool.dp2px(406));
        layoutParamsFrame.setMargins(RxImageTool.dp2px(70),0,0,0);
        ijkPlayerFrameLayout.setLayoutParams(layoutParamsFrame);




    }


    private void addTextureView() {
        titleLinearLayout.setVisibility(View.GONE);
        buttonRelativeLayout.setVisibility(View.GONE);
        nestIjkplayerDetail.setVisibility(View.GONE);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        linearLeftPart.setLayoutParams(layoutParams);

        LinearLayout.LayoutParams layoutParamsFrame = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, RxImageTool.dp2px(684));
        layoutParams.setMargins(RxImageTool.dp2px(32),0,RxImageTool.dp2px(32), 0);
        ijkPlayerFrameLayout.setLayoutParams(layoutParamsFrame);

        LinearLayout.LayoutParams layoutParams1=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,RxImageTool.dp2px(56));
        layoutParams1.setMargins(0,50,0,0);
        seekRel.setLayoutParams(layoutParams1);

    }

    private void startIntent() {
        fullScreen.setVisibility(View.VISIBLE);
        buttonRelativeLayout.setVisibility(View.GONE);
        seekRel.setVisibility(View.VISIBLE);
        imageDetail.setVisibility(View.GONE);
        timeAll.setVisibility(View.GONE);
        ijkPlayerTextureView.setVisibility(View.VISIBLE);
        isPlaying = true;
        ijkPlayerTextureView.start();
        getpoll();
        ijkplayerSeekbar.setOnSeekBarChangeListener(seekListener);
    }


    private void PopWindow() {
        View view = View.inflate(imageDetail.getContext(), R.layout.msgpopupwindow, null);
        PopupWindow popupWindow = new PopupWindow(view);
        Button cancel_button = view.findViewById(R.id.cancel_button);
        popupWindow.setFocusable(true);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels / 16 * 10;
        int height = metrics.heightPixels / 8 * 5;
        popupWindow.setWidth(width);
        popupWindow.setHeight(height);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        darkenBackground(0.2f);
        popupWindow.setOnDismissListener(() -> darkenBackground(1f));
        cancel_button.setOnClickListener(view1 -> {
            popupWindow.dismiss();
        });

    }

    private void darkenBackground(float bgcolor) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgcolor;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setAttributes(lp);
    }


    private void initNative() {
        IjkMediaPlayer.loadLibrariesOnce(null);
        try {
            IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        } catch (Exception e) {
            this.finish();
        }

        initIjkPlayer();
        ijkPlayerTextureView.setAssetVideoPath(VideoFileName);
    }

    private void initIjkPlayer() {
        ijkPlayerTextureView.setVideoPlayerListener(new VideoPlayerListener() {
            @Override
            public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int i) {

            }

            @Override
            public void onCompletion(IMediaPlayer iMediaPlayer) {

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

            }

            @Override
            public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int i, int i1, int i2, int i3) {

            }
        });
    }

    private SeekBar.OnSeekBarChangeListener seekListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
//                ijkPlayerTextureView.seekTo(duration / 100 * progress * 1000);
                long a = (long) (((float) duration / 100) * progress * 1000);
                ijkPlayerTextureView.seekTo(a);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
//            ijkPlayerTextureView.seekTo(duration/100*progress*1000);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };


    private void PopUpwindow2() {
        View view = View.inflate(ijkPlayerTextureView.getContext(), R.layout.menu_dialog, null);
         popupWindow = new PopupWindow(view);

        play_img = view.findViewById(R.id.play_img);
        fast_forward_img = view.findViewById(R.id.fast_forward_img);
        retreat_img = view.findViewById(R.id.retreat_img);
        back_img = view.findViewById(R.id.back_img);
        if (isPlaying && current <duration){
            Glide.with(this).load(R.mipmap.pause).into(play_img);
        }else {
            Glide.with(this).load(R.mipmap.play).into(play_img);
        }
        if (current<duration){
            Glide.with(this).load(R.mipmap.fast_forward).into(fast_forward_img);
        } else {
            Glide.with(this).load(R.mipmap.no_fast_forward_img).into(fast_forward_img);
        }

        popupWindow.setFocusable(true);

        popupWindow.setWidth(1000);
        popupWindow.setHeight(240);

        popupWindow.showAtLocation(view, Gravity.LEFT, 300, 0);

        back_img.setOnClickListener(view14 -> {
            popupWindow.dismiss();
            ijkPlayerTextureView.release();
            finish();
        });

        play_img.setOnClickListener(view1 -> {
            if (!isPlaying) {
                isPlaying = true;
                Glide.with(this).load(R.mipmap.pause).into(play_img);
                ijkPlayerTextureView.start();
            } else {
                ijkPlayerTextureView.pause();
                Glide.with(this).load(R.mipmap.play).into(play_img);
                isPlaying = false;
            }

        });

        /**
         * 快进按钮
         */
        fast_forward_img.setOnClickListener(view12 -> {
            //快进的时候，后退的按钮置白
            if (current * 1000 + 15000 >= duration * 1000) {
                Log.d("------", "1231231");
                current = duration;
                Log.d(TAG, String.valueOf(current));
                ijkPlayerTextureView.seekTo(duration * 1000);
                isPlaying = false;
                ijkPlayerTextureView.pause();
                Glide.with(this).load(R.mipmap.no_fast_forward_img).into(fast_forward_img);
                Glide.with(this).load(R.mipmap.play).into(play_img);
                if (flag){
                    flag=false;
                    finishThePlayer();
                }
            } else if (current * 1000 + 15000 < duration * 1000) {
                Glide.with(this).load(R.mipmap.retreat).into(retreat_img);
                Glide.with(this).load(R.mipmap.pause).into(play_img);
                Glide.with(this).load(R.mipmap.fast_forward).into(fast_forward_img);
                ijkPlayerTextureView.seekTo(current * 1000 + 15000);
                Log.d(TAG, String.valueOf(current));
                if (!isPlaying) {
                    isPlaying = true;
                    ijkPlayerTextureView.start();
                }
            }
        });

        /**
         * 后退按钮
         */
        retreat_img.setOnClickListener(view13 -> {
            flag=true;
            //后退后，灰白白
            if (current * 1000 - 15000 <= 0) {
                current = 0;
                ijkPlayerTextureView.seekTo(0);
                if (!isPlaying) {
                    isPlaying = true;
                    ijkPlayerTextureView.start();
                }
                Glide.with(this).load(R.mipmap.fast_forward).into(fast_forward_img);
                Glide.with(this).load(R.mipmap.pause).into(play_img);
                Glide.with(this).load(R.mipmap.noreact).into(retreat_img);
            } else if (current * 1000 - 15000 > 0) {
                //白白白
                ijkPlayerTextureView.seekTo(current * 1000 - 15000);
                if (!isPlaying) {
                    isPlaying = true;
                    ijkPlayerTextureView.start();
                }
                Log.d(TAG, String.valueOf(current));
                Glide.with(this).load(R.mipmap.fast_forward).into(fast_forward_img);
                Glide.with(this).load(R.mipmap.pause).into(play_img);
                Glide.with(this).load(R.mipmap.retreat).into(retreat_img);
            }
        });
    }

    private void PopUpwindow() {
        View view = View.inflate(ijkPlayerTextureView.getContext(), R.layout.menu_dialog, null);
         popupWindow = new PopupWindow(view);

        play_img = view.findViewById(R.id.play_img);
        fast_forward_img = view.findViewById(R.id.fast_forward_img);
        retreat_img = view.findViewById(R.id.retreat_img);
        back_img = view.findViewById(R.id.back_img);

        popupWindow.setFocusable(true);

        popupWindow.setWidth(1000);
        popupWindow.setHeight(240);

        if (isPlaying && current<duration){
            Glide.with(this).load(R.mipmap.pause).into(play_img);
        }else {
            Glide.with(this).load(R.mipmap.play).into(play_img);
        }

        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        back_img.setOnClickListener(view14 -> {
            popupWindow.dismiss();
            ijkPlayerTextureView.release();
            finish();
        });


        play_img.setOnClickListener(view1 -> {
            if (!isPlaying) {
                isPlaying = true;
                Glide.with(this).load(R.mipmap.pause).into(play_img);
                ijkPlayerTextureView.start();
            } else {
                ijkPlayerTextureView.pause();
                Glide.with(this).load(R.mipmap.play).into(play_img);
                isPlaying = false;
            }

        });

        /**
         * 快进按钮
         */
        fast_forward_img.setOnClickListener(view12 -> {
            //快进的时候，后退的按钮置白
            if (current * 1000 + 15000 >= duration * 1000) {
                current = duration;
                Log.d(TAG, String.valueOf(current));
                ijkPlayerTextureView.seekTo(duration * 1000);
                isPlaying = false;
                ijkPlayerTextureView.pause();
                Glide.with(this).load(R.mipmap.no_fast_forward_img).into(fast_forward_img);
                Glide.with(this).load(R.mipmap.play).into(play_img);
                finishThePlayer();
            } else if (current * 1000 + 15000 < duration * 1000) {
                Glide.with(this).load(R.mipmap.retreat).into(retreat_img);
                Glide.with(this).load(R.mipmap.pause).into(play_img);
                Glide.with(this).load(R.mipmap.fast_forward).into(fast_forward_img);
                ijkPlayerTextureView.seekTo(current * 1000 + 15000);
                Log.d(TAG, String.valueOf(current));
                if (!isPlaying) {
                    isPlaying = true;
                    ijkPlayerTextureView.start();
                }
            }
        });

        /**
         * 后退按钮
         */
        retreat_img.setOnClickListener(view13 -> {
            //后退后，灰白白
            if (current * 1000 - 15000 <= 0) {
                current = 0;
                ijkPlayerTextureView.seekTo(0);
                if (!isPlaying) {
                    isPlaying = true;
                    ijkPlayerTextureView.start();
                }
//                Log.d(TAG, String.valueOf(current));
                Glide.with(this).load(R.mipmap.fast_forward).into(fast_forward_img);
                Glide.with(this).load(R.mipmap.pause).into(play_img);
                Glide.with(this).load(R.mipmap.noreact).into(retreat_img);
            } else if (current * 1000 - 15000 > 0) {
                //白白白
                ijkPlayerTextureView.seekTo(current * 1000 - 15000);
                if (!isPlaying) {
                    isPlaying = true;
                    ijkPlayerTextureView.start();
                }
                Log.d(TAG, String.valueOf(current));
                Glide.with(this).load(R.mipmap.fast_forward).into(fast_forward_img);
                Glide.with(this).load(R.mipmap.pause).into(play_img);
                Glide.with(this).load(R.mipmap.retreat).into(retreat_img);
            }
        });
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (popupWindow!=null && popupWindow.isShowing()){
            isPlaying=false;
            popupWindow.dismiss();
        }
        IjkMediaPlayer.native_profileEnd();
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
        pushToSocket();
        updateRate(localTime);
    }


    private void getpoll() {
        runnable = new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    refreshTime();
                    handler.postDelayed(this, 1000);
                }
            }
        };
        handler.post(runnable);
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
            if (current>0){
                if (popupWindow!=null && popupWindow.isShowing()){
                    Glide.with(this).load(R.mipmap.retreat).into(retreat_img);
                }
            }
            ijkplayerSeekbar.setProgress((int) ((current * 100) / duration));
            if (((current == duration) || (duration - current) == 1) && isPlaying) {
                ijkPlayerTextureView.seekTo(duration * 1000);
                isPlaying = false;
                ijkPlayerTextureView.pause();

                if (!state){
                    if (popupWindow==null){
                        PopUpwindow2();
                    }
                }else {
                    if (popupWindow==null){
                        PopUpwindow();
                    }
                }
                if (flag){
                    flag=false;
                    finishThePlayer();
                }
                Glide.with(this).load(R.mipmap.play).into(play_img);
                Glide.with(this).load(R.mipmap.no_fast_forward_img).into(fast_forward_img);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isPlaying = false;
        ijkPlayerTextureView.pause();
    }

    @Override
    public void onBackPressed() {
        if (autoDismissPopupWindow!=null && autoDismissPopupWindow.isShowing()){
            autoDismissPopupWindow.dismiss();
        }
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
        ijkPlayerTextureView.release();
        finish();
    }

    //更新百分比进度
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateRate(String trainingDay) {
        int watchNum = 0;
        JSONObject requestDate = new JSONObject();
        List<Video> videoList = LitePal.select("*").where("specifyStartTime <=? and specifyEndTime >? and specifyEndTime is not null", trainingDay, trainingDay).find(Video.class);
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
                        Toast.makeText(IjkplayerDetailActivity.this, response.body().getMsg(), Constants.Toast_Length).show();
                    }
                }
                @SuppressLint("WrongConstant")
                @Override
                public void onFailure(Call<MsgModel> call, Throwable t) {
                    Toast.makeText(getApplicationContext(),  getString(R.string.no_Wifi_training), Constants.Toast_Length).show();
                }
            });
        } catch (JSONException e) {
            PLog.e(TAG + "/updateRate", e.toString());
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
                        Toast.makeText(IjkplayerDetailActivity.this, getString(R.string.tip_video_play_end), Constants.Toast_Length).show();
                    } else {
                        Toast.makeText(IjkplayerDetailActivity.this, response.body().getMsg(), Constants.Toast_Length).show();
                    }
                }

                @SuppressLint("WrongConstant")
                @Override
                public void onFailure(Call<MsgModel> call, Throwable t) {
                    Toast.makeText(getApplicationContext(),getString(R.string.no_Wifi_training), Constants.Toast_Length).show();
                }
            });
        } catch (JSONException e) {
            PLog.e(TAG + "/pushToSocket", e.toString());
        }
    }


    private void PopNoNetWork() {
        View view =getLayoutInflater().inflate(R.layout.activity_main_dialog, null);
        PopupWindow popupWindow = new PopupWindow(view);
        Button btnOk = (Button) view.findViewById(R.id.btn_ok);
        TextView messageTextView = (TextView) view.findViewById(R.id.text_view_message);
        messageTextView.setTextSize(25);
        messageTextView.setText(getString(R.string.no_Wifi_training));
        popupWindow.setFocusable(true);
        int width = RxImageTool.dp2px(800);
        int height = RxImageTool.dp2px(500);
        popupWindow.setWidth(width);
        popupWindow.setHeight(height);
        popupWindow.showAtLocation(titleLinearLayout, Gravity.CENTER, 0, 0);
        darkenBackground(0.2f);
        popupWindow.setOnDismissListener(() -> darkenBackground(1f));
        btnOk.setOnClickListener(view1 -> {
            popupWindow.dismiss();
            startIntent();
        });
    }

    private void PopNoNetWork2() {
        View view =getLayoutInflater().inflate(R.layout.activity_main_dialog, null);
        PopupWindow popupWindow = new PopupWindow(view);
        Button btnOk = (Button) view.findViewById(R.id.btn_ok);
        TextView messageTextView = (TextView) view.findViewById(R.id.text_view_message);
        messageTextView.setTextSize(25);
        messageTextView.setText(getString(R.string.no_Wifi_training));
        popupWindow.setFocusable(true);
        int width = RxImageTool.dp2px(800);
        int height = RxImageTool.dp2px(500);
        popupWindow.setWidth(width);
        popupWindow.setHeight(height);
        popupWindow.showAtLocation(titleLinearLayout, Gravity.CENTER, 0, 0);
        darkenBackground(0.2f);
        popupWindow.setOnDismissListener(() -> darkenBackground(1f));
        btnOk.setOnClickListener(view1 -> {
            popupWindow.dismiss();
            Intent intent = new Intent(this, VideoPreviewActivity.class);
            intent.putExtra("videoFileName", videoList.get(0).getVideoFileName());
            intent.putExtra("videoId", videoId);
            intent.putExtra("thumbnailPath", videoList.get(0).getThumbnailPath());
            startActivity(intent);
        });
    }

}
