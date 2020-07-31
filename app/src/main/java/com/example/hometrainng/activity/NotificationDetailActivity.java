package com.example.hometrainng.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.example.hometrainng.R;
import com.example.hometrainng.customview.IjkplayerVideoViewRecord;
import com.example.hometrainng.customview.VideoPlayerListener;
import com.example.hometrainng.db.RecordVideo;
import com.example.hometrainng.db.Video;
import com.example.hometrainng.tools.Utils;
import com.tamsiree.rxkit.RxImageTool;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class NotificationDetailActivity extends BaseActivity {
    private static final String TAG = "NotificationDetailActiv";

    @BindView(R.id.notification_date)
    TextView notificationDate;
    @BindView(R.id.notification_detail_title)
    TextView notificationDetailTitle;
//    @BindView(R.id.notification_time)
//    TextView notificationTime;
//    @BindView(R.id.notification_detail)
//    TextView notificationDetail;

    @BindView(R.id.duration_tv_all)
    TextView durationTvAll;
    @BindView(R.id.ijkPlayer_textureView)
    IjkplayerVideoViewRecord ijkPlayerTextureView;
    @BindView(R.id.back_img)
    ImageView backImg;
    @BindView(R.id.retreat_img)
    ImageView retreatImg;
    @BindView(R.id.play_img)
    ImageView playImg;
    @BindView(R.id.fast_forward_img)
    ImageView fastForwardImg;
    @BindView(R.id.ijkplayer_img)
    ImageView ijkplayerImg;
    @BindView(R.id.popup_linear)
    LinearLayout popupLinear;
    @BindView(R.id.ijkplayer_seekbar)
    SeekBar ijkplayerSeekbar;
    @BindView(R.id.ijkPlayerTv)
    TextView ijkPlayerTv;
    //    @BindView(R.id.ijkPlayerTv2)
//    TextView ijkPlayerTv2;
    @BindView(R.id.full_screen)
    ImageView fullScreen;

    /**
     * zengda
     */
//    @BindView(R.id.linear2)
//    NestedScrollView linear2;
    @BindView(R.id.linear_title)
    RelativeLayout linearTitle;
    @BindView(R.id.ijkPlayer_frame)
    FrameLayout ijkPlayerFrame;
    @BindView(R.id.topReal)
    RelativeLayout topReal;
    @BindView(R.id.seek_relativelayout)
    RelativeLayout seekRelativelayout;
    private boolean isPlaying = true;
    private long current, duration, seconds, minutes;
    private String videoPath, imgPath;

    private Handler handler = new Handler();
    private Runnable runnable;
    /**
     * 用来判断放大还是缩小
     */
    private boolean state = false;

    private boolean isFirstLogin = true;

//    //true为显示进度条，false为不显示
//    private boolean progressExist = true;
//    判断是否是全屏,full,noFull;
//    private boolean fullState = false;

    //总时长
    private String allDuration;
    //    private CountDownTimer countDownTimer;
    private int i = 0;

    private PopupWindow popupWindow;

    private boolean popShow=true;


    /**
     * 定义初始化时候接受的参数，用于oncreate显示该几项数据
     *
     * @param savedInstanceState
     */
    private String videoId, recordId;
    private List<Video> videoList = new ArrayList<>();
    private List<RecordVideo> recordVideoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_detail);
        ButterKnife.bind(this);
        ijkPlayerTextureView.setVisibility(View.GONE);
        ijkplayerImg.setVisibility(View.VISIBLE);
        isPlaying = false;
        initNative();
        initGetInternet();
        Glide.with(getApplicationContext()).load("file://" + imgPath).into(ijkplayerImg);
        ijkplayerSeekbar.setOnSeekBarChangeListener(seekListener);
    }

    private void initNative() {
        IjkMediaPlayer.loadLibrariesOnce(null);
        try {
            IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        } catch (Exception e) {
            this.finish();
        }
        init();
    }


    @Override
    protected void onStop() {
        super.onStop();
        isPlaying=false;
        popupLinear.setVisibility(View.GONE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isPlaying = false;
        Glide.with(this).load(R.mipmap.play).into(playImg);
        ijkPlayerTextureView.pause();
    }


    private SeekBar.OnSeekBarChangeListener seekListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                long a= (long) (((float)duration/100)*progress*1000);
                ijkPlayerTextureView.seekTo(a);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };


    @Override
    public void onBackPressed() {
        if (autoDismissPopupWindow !=null && autoDismissPopupWindow.isShowing()){
            autoDismissPopupWindow.dismiss();
        }else {
            ijkPlayerTextureView.release();
            handler.removeCallbacks(runnable);
            finish();
        }
    }

    public void init() {
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


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onResume() {
        super.onResume();
        if (duration <= 0) {
            Glide.with(this).load(R.mipmap.noreact).into(retreatImg);
        }
//        NoDrag();

        /**
         * 增大触摸范围
         */
        seekRelativelayout.setOnTouchListener((view, event) -> {
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


    private void addTextureView() {
        linearTitle.setVisibility(View.GONE);
//        int width=RxImageTool.dp2px(getApplicationContext(),1000);
//        int height=RxImageTool.dp2px(getApplicationContext(),1400);
//        int bottom=RxImageTool.dp2px(getApplicationContext(),150);
//        RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(width,height);
//        layoutParams.setMargins(0,0,0,bottom);
//        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
//        ijkPlayerFrame.setLayoutParams(layoutParams);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        topReal.setBackgroundColor(getResources().getColor(R.color.background_color_black));
        layoutParams.setMargins(200, 0, 200, 0);
        ijkPlayerFrame.setLayoutParams(layoutParams);
    }


    private void minTexteView() {
        linearTitle.setVisibility(View.VISIBLE);
        int width = RxImageTool.dp2px(getApplicationContext(), 800);
        int height = RxImageTool.dp2px(getApplicationContext(), 600);
        int top = RxImageTool.dp2px(getApplicationContext(), 150);
        topReal.setBackgroundColor(getResources().getColor(R.color.color_body));
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
        layoutParams.setMargins(0, top, 0, 0);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        ijkPlayerFrame.setLayoutParams(layoutParams);

//        int left = RxImageTool.dip2px(getApplicationContext(), 70);
//        int top = RxImageTool.dip2px(getApplicationContext(), 150);
//        layoutParams.setMargins(left, top, 0, 0);
//        ijkPlayerFrame.setLayoutParams(layoutParams);

    }


    //禁止拖动
//    private void NoDrag() {
//        ijkplayerSeekbar.setOnTouchListener((view, motionEvent) -> true);
//    }

    /**
     * 初始化，用于显示界面的数据
     */
    private void initGetInternet() {
        Intent intent = getIntent();
        int videoid = intent.getIntExtra("videoId", 0);
        int recordid = intent.getIntExtra("recordId", 0);
        if (videoid != 0 && recordid != 0) {
            videoId = String.valueOf(videoid);
            recordId = String.valueOf(recordid);
            //查询Video表
            videoList = LitePal.select("*").where("videoId = ?", videoId).find(Video.class);
            notificationDetailTitle.setText(videoList.get(0).getTitle());
//            notificationDetail.setText(videoList.get(0).getIndividualComment());
            //查询VideoRecord表
            recordVideoList = LitePal.select("*").where("id = ?", recordId).find(RecordVideo.class);
            durationTvAll.setText(recordVideoList.get(0).getDuration());
            notificationDate.setText(Utils.TimeToHHMMSS(recordVideoList.get(0).getRecordDate()));
//            notificationTime.setText(Utils.TimeToHHMMSS(recordVideoList.get(0).getRecordDate()));
            allDuration = recordVideoList.get(0).getDuration();
            videoPath = recordVideoList.get(0).getRecordPath();
            imgPath = recordVideoList.get(0).getRecordImg();
            ijkPlayerTextureView.setVideoPath(videoPath);
        }

    }

    @OnClick({R.id.back_img, R.id.retreat_img, R.id.play_img, R.id.fast_forward_img, R.id.ijkPlayer_textureView, R.id.full_screen})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_img:
                ijkPlayerTextureView.release();
                handler.removeCallbacks(runnable);
                finish();
                break;
            case R.id.retreat_img:
                Glide.with(this).load(R.mipmap.fast_forward).into(fastForwardImg);
                popShow=true;
                if (current * 1000 - 15000 <= 0) {
                    current = 0;
                    ijkPlayerTextureView.seekTo(0);
                    Glide.with(this).load(R.mipmap.noreact).into(retreatImg);
                } else if (current * 1000 - 15000 > 0) {
                    ijkPlayerTextureView.seekTo(current * 1000 - 15000);
                    Glide.with(this).load(R.mipmap.retreat).into(retreatImg);
                }
                break;
            case R.id.play_img:
                if (isFirstLogin) {
                    Glide.with(this).load(R.mipmap.retreat).into(retreatImg);
                    Glide.with(this).load(R.mipmap.pause).into(playImg);
                    popupLinear.setVisibility(View.GONE);
                    ijkplayerImg.setVisibility(View.GONE);
                    ijkPlayerTextureView.setVisibility(View.VISIBLE);
                    isPlaying = true;
                    ijkPlayerTextureView.start();
                    getpoll();
                    isFirstLogin = false;
                } else {
                    if (!isPlaying) {
                        isPlaying = true;
                        Glide.with(this).load(R.mipmap.pause).into(playImg);
                        ijkPlayerTextureView.start();
                    } else {
                        Glide.with(this).load(R.mipmap.play).into(playImg);
                        ijkPlayerTextureView.pause();
                        isPlaying = false;
                    }
                }
                break;
            case R.id.fast_forward_img:
                Glide.with(this).load(R.mipmap.retreat).into(retreatImg);
                if (current * 1000 + 15000 >= duration * 1000) {
                    current = duration;
                    ijkPlayerTextureView.seekTo(duration * 1000);
                    Glide.with(this).load(R.mipmap.no_fast_forward_img).into(fastForwardImg);
                    ijkPlayerTv.setText(allDuration);
//                    ijkPlayerTv2.setText(allDuration);
                } else if (current * 1000 + 15000 < duration * 1000) {
                    ijkPlayerTextureView.seekTo(current * 1000 + 15000);
                    Glide.with(this).load(R.mipmap.fast_forward).into(fastForwardImg);
                }
                break;
            case R.id.ijkPlayer_textureView:
//                if (progressExist && isPlaying) {
//                    ijkPlayerTextureView.pause();
//                    popupLinear.setVisibility(View.VISIBLE);
//                } else if (progressExist && !isPlaying) {
//                    popupLinear.setVisibility(View.GONE);
//                    ijkPlayerTextureView.start();
//                } else if (!progressExist) {
//                    progressShow();
//                }
//                popupLinear.setVisibility(View.VISIBLE);
                if (i % 2 == 0) {
                    popupLinear.setVisibility(View.VISIBLE);
                    i++;
                } else {
                    popupLinear.setVisibility(View.GONE);
                    i++;
                }
                break;
            case R.id.full_screen:
//                    if (!state) {
//                        //全屏显示进度条
//                        fullScreen.setImageResource(R.mipmap.no_full_screen);
//                        //放大屏幕并且state变为放大
//                        fullState = true;
//                        state = true;
//                        progressShowFull();
//                    } else {
//                            countDownTimer.cancel();
//                            minTexteViewFull();
//                            fullScreen.setImageResource(R.mipmap.full_screen);
//                            fullState = false;
//                            state = false;
//                    }
                if (!state) {
                    //全屏显示进度条
//                        addTextureView();
                    fullScreen.setImageResource(R.mipmap.no_full_screen);
                    //放大屏幕并且state变为放大
                    state = true;
                    addTextureView();
                } else {
                    minTexteView();
                    fullScreen.setImageResource(R.mipmap.full_screen);
                    state = false;
                }
                break;
        }
    }


//    private void minTexteViewFull() {
//        linear2.setVisibility(View.VISIBLE);
//        linearTitle.setVisibility(View.VISIBLE);
//        ijkPlayerTv2.setVisibility(View.VISIBLE);
//        seekRelativelayout.setVisibility(View.VISIBLE);
//
//        int widht = RxImageTool.dp2px(getApplicationContext(), 720);
//        int height = RxImageTool.dp2px(getApplicationContext(), 445);
//        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(widht, height);
//        int left = RxImageTool.dip2px(getApplicationContext(), 70);
//        int top = RxImageTool.dip2px(getApplicationContext(), 150);
//        layoutParams.setMargins(left, top, 0, 0);
//        ijkPlayerFrame.setLayoutParams(layoutParams);
//
//
//        FrameLayout.LayoutParams layoutParamsFrame = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
//        layoutParamsFrame.setMargins(0, 0, 0, RxImageTool.dip2px(56));
//        ijkPlayerTextureView.setLayoutParams(layoutParamsFrame);
//
//
//    }


//    //放大展示带progress的进度条
//    private void progressShowFull() {
//        linear2.setVisibility(View.GONE);
//        linearTitle.setVisibility(View.GONE);
//        ijkPlayerTv2.setVisibility(View.GONE);
//
//        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
//        layoutParams.setMargins(0, 0, 0, 56);
//        ijkPlayerFrame.setLayoutParams(layoutParams);
//        progressExist = true;
//
//        progressMiss();
//    }

//
//    //全屏下点击，展示屏幕下方的bar
//    private void progressShow() {
//        seekRelativelayout.setVisibility(View.VISIBLE);
//        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
//        layoutParams.setMargins(0, 0, 0, 56);
//        ijkPlayerFrame.setLayoutParams(layoutParams);
//
//        FrameLayout.LayoutParams layoutParamsFrame = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
//        layoutParamsFrame.setMargins(0, 0, 0, 106);
//        ijkPlayerTextureView.setLayoutParams(layoutParamsFrame);
//        progressExist = true;
//        progressMiss();
//
//    }


//    private void progressMiss() {
//        countDownTimer = new CountDownTimer(3000, 3000) {
//            @Override
//            public void onTick(long l) {
//
//            }
//
//            @Override
//            public void onFinish() {
//                seekRelativelayout.setVisibility(View.GONE);
//                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
//                layoutParams.setMargins(0, 0, 0, 0);
//                ijkPlayerTextureView.setLayoutParams(layoutParams);
//
//                RelativeLayout.LayoutParams layoutParamsRel = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
//                layoutParamsRel.setMargins(0, 0, 0, 0);
//                ijkPlayerFrame.setLayoutParams(layoutParamsRel);
//
//                progressExist = false;
//            }
//        };
//        countDownTimer.start();
//    }


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
//        handler.postDelayed(runnable, 1000);
        handler.post(runnable);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void refreshTime() {
        current = ijkPlayerTextureView.getCurrentPosition() / 1000;
        duration = ijkPlayerTextureView.getDuration() / 1000;

        seconds = current % 60;
        minutes = current / 60;


        String time = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        if (duration != 0) {
            if (current>0){
                Glide.with(this).load(R.mipmap.retreat).into(retreatImg);
            }
            ijkplayerSeekbar.setProgress((int) ((current * 100) / duration));
            ijkPlayerTv.setText(time);
//            ijkPlayerTv2.setText(time);
            if (((current == duration) || (duration - current) ==1) && isPlaying) {
                ijkPlayerTextureView.seekTo(duration * 1000);
                ijkPlayerTextureView.pause();
                if (popShow){
                    popupLinear.setVisibility(View.VISIBLE);
                    popShow=false;
                }
                ijkPlayerTv.setText(allDuration);
                Glide.with(this).load(R.mipmap.no_fast_forward_img).into(fastForwardImg);
                Glide.with(this).load(R.mipmap.play).into(playImg);
            }

        }
    }

}
