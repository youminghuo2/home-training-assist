package com.example.hometrainng.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.example.hometrainng.R;
import com.example.hometrainng.api.HomeTrainService;
import com.example.hometrainng.customview.IjkplayerVideoView;
import com.example.hometrainng.customview.VideoPlayerListener;
import com.example.hometrainng.db.Video;
import com.example.hometrainng.db.VideoHistory;
import com.example.hometrainng.entity.MsgModel;
import com.example.hometrainng.retrofit.Constants;
import com.example.hometrainng.retrofit.HttpHelper;
import com.example.hometrainng.tools.PLog;
import com.tamsiree.rxkit.RxImageTool;
import com.tamsiree.rxkit.RxSPTool;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

public class IjkplayerPlay extends BaseActivity {
    private static final String TAG = "IjkplayerPlay";

    @BindView(R.id.ijkPlayer_textureView)
    IjkplayerVideoView ijkPlayerTextureView;
    //    @BindView(R.id.time_data_tv)
//    TextView timeDataTv;
//    @BindView(R.id.ijkPlayer_img)
//    ImageView ijkPlayerImg;
    @BindView(R.id.ijkPlayer_tv)
    TextView ijkPlayerTv;
    @BindView(R.id.ijkplayer_seekbar)
    SeekBar ijkplayerSeekbar;
    @BindView(R.id.bottom_rel)
    RelativeLayout bottomRel;
    //    @BindView(R.id.ijkPlayer_frame)
//    FrameLayout ijkPlayerFrame;
    @BindView(R.id.view)
    View view;
    private boolean isPlaying = true;

    private Handler handler = new Handler();
    private Runnable runnable;

    private ImageView retreat_img, play_img, fast_forward_img, back_img;
    private long current, duration, seconds, minutes;

    private String VideoFileName;
    private int videoId;
    private String thumbnailPath;

//    //true为显示进度条，false为不显示
//    private boolean progressExist = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ijkplayer_play);
        ButterKnife.bind(this);
//        ijkPlayerTextureView.setVisibility(View.GONE);
//        ijkPlayerImg.setVisibility(View.VISIBLE);
        initNative();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onResume() {
        super.onResume();
        if (!isPlaying) {
            PopUpwindow();
        }
        ijkPlayerTextureView.setOnClickListener(view -> {
//            if (!progressExist) {
//                progressShow();
//            } else {
//                ijkPlayerTextureView.pause();
//                isPlaying = false;
//                PopUpwindow();
//            }
            PopUpwindow();
        });

//        //进度条不可拖动
//        NoDrag();

        /**
         * 增大触摸范围
         */
        bottomRel.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
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
            }
        });

    }



//    private void progressShow() {
//        IjkplayerPlay.this.runOnUiThread(() -> {
//            bottomRel.setVisibility(View.VISIBLE);
//            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
//            layoutParams.setMargins(0, 0, 0, RxImageTool.dip2px(80));
//            ijkPlayerFrame.setLayoutParams(layoutParams);
//            progressExist = true;
//            progressMiss();
//        });
//    }

//    private void progressMiss() {
//        CountDownTimer timer = new CountDownTimer(3000, 3000) {
//            @Override
//            public void onTick(long l) {
//
//            }
//
//            @Override
//            public void onFinish() {
//                IjkplayerPlay.this.runOnUiThread(() -> {
//                    bottomRel.setVisibility(View.GONE);
//                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
//                    layoutParams.setMargins(0, 0, 0, 0);
//                    ijkPlayerFrame.setLayoutParams(layoutParams);
//                    progressExist = false;
//                });
//            }
//        };
//        timer.start();
//    }

//    private void NoDrag() {
//        ijkplayerSeekbar.setOnTouchListener((view, motionEvent) -> true);
//    }




    @Override
    protected void onStop() {
        super.onStop();
        IjkMediaPlayer.native_profileEnd();
    }

    private void initNative() {
        IjkMediaPlayer.loadLibrariesOnce(null);
        try {
            IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        } catch (Exception e) {
            this.finish();
        }

        Intent intent = getIntent();
        VideoFileName = intent.getStringExtra("videoFileName");
        videoId = intent.getIntExtra("videoId", 0);
        thumbnailPath = intent.getStringExtra("thumbnailPath");
//        Glide.with(getApplicationContext()).load(Constants.PhotoPath + thumbnailPath).into(ijkPlayerImg);
        init();

//        String a = VideoFileName;
        ijkPlayerTextureView.setAssetVideoPath(VideoFileName);
        ijkPlayerTextureView.start();
        getpoll();
        ijkplayerSeekbar.setOnSeekBarChangeListener(seekListener);
    }

    private SeekBar.OnSeekBarChangeListener seekListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
//                ijkPlayerTextureView.seekTo(duration / 100 * progress * 1000);
                long a= (long) (((float)duration/100)*progress*1000);
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
//        CountDownTimer timer = new CountDownTimer(5000L + 300L, 1000L) {
//            @Override
//            public void onTick(long l) {
//                timeDataTv.setText("" + l / 1000);
//            }
//
//            @Override
//            public void onFinish() {
////                ijkPlayerImg.setVisibility(View.GONE);
////                ijkPlayerTextureView.setVisibility(View.VISIBLE);
//                timeDataTv.setVisibility(View.GONE);
//                ijkPlayerTextureView.start();
//                getpoll();
//                progressMiss();
//
//            }
//
//        };
//        timer.start();
//
//    }

    private void PopUpwindow() {
        View view = View.inflate(ijkPlayerTextureView.getContext(), R.layout.menu_dialog, null);
        PopupWindow popupWindow = new PopupWindow(view);

        play_img = view.findViewById(R.id.play_img);
        fast_forward_img = view.findViewById(R.id.fast_forward_img);
        retreat_img = view.findViewById(R.id.retreat_img);
        back_img = view.findViewById(R.id.back_img);

        popupWindow.setFocusable(true);

        popupWindow.setWidth(1000);
        popupWindow.setHeight(240);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        back_img.setOnClickListener(view14 -> {
            popupWindow.dismiss();
            ijkPlayerTextureView.release();
            finish();
        });

        play_img.setOnClickListener(view1 -> {
//            Glide.with(this).load(R.mipmap.retreat).into(retreat_img);
//            isPlaying = true;
//            popupWindow.dismiss();
//            if (current < duration) {
//                ijkPlayerTextureView.start();
//            } else {
//                finishThePlayer();
//            }
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
                isPlaying = false;
                ijkPlayerTextureView.pause();
                Glide.with(this).load(R.mipmap.no_fast_forward_img).into(fast_forward_img);
                Glide.with(this).load(R.mipmap.pause).into(play_img);
                current = duration;
                Log.d(TAG, String.valueOf(current));
                ijkPlayerTextureView.seekTo(duration * 1000);
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
            pushToSocket();
            updateRate(localTime);
        }
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
        ijkPlayerTv.setText(time);
        if (duration != 0 ) {
            ijkplayerSeekbar.setProgress((int) ((current * 100) / duration));
            if (((current == duration) || (duration - current) == 1) && isPlaying) {
                isPlaying = false;
                ijkPlayerTextureView.pause();
                ijkPlayerTextureView.seekTo(duration * 1000);
                PopUpwindow();
                finishThePlayer();
                Glide.with(this).load(R.mipmap.pause).into(play_img);
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
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
        ijkPlayerTextureView.release();
        finish();
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


    //更新百分比进度
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateRate(String trainingDay) {
        JSONObject requestDate = new JSONObject();
        List<Video> videoList = LitePal.select("*").where("specifyStartTime <=? and specifyEndTime >=?", trainingDay, trainingDay).find(Video.class);
        List<Video> videoList1 = LitePal.select("*").where("specifyEndTime is null and specifyStartTime <= ?", trainingDay).find(Video.class);
        List<Video> videoListAll = new ArrayList<>();
        videoListAll.addAll(videoList);
        videoListAll.addAll(videoList1);
        int count = LitePal.where("videoId=? and playEndTime = ?", String.valueOf(videoId), trainingDay).count(VideoHistory.class);
        int watched = count;
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
                        Toast.makeText(IjkplayerPlay.this, response.body().getMsg(), Constants.Toast_Length).show();
                    }
                }

                @SuppressLint("WrongConstant")
                @Override
                public void onFailure(Call<MsgModel> call, Throwable t) {
                    Toast.makeText(IjkplayerPlay.this, t.getMessage(), Constants.Toast_Length).show();
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
                        Toast.makeText(IjkplayerPlay.this, getString(R.string.tip_video_play_end), Constants.Toast_Length).show();
                    } else {
                        Toast.makeText(IjkplayerPlay.this, response.body().getMsg(), Constants.Toast_Length).show();
                    }
                }

                @SuppressLint("WrongConstant")
                @Override
                public void onFailure(Call<MsgModel> call, Throwable t) {
                    Toast.makeText(IjkplayerPlay.this, t.getMessage(), Constants.Toast_Length).show();
                }
            });
        } catch (JSONException e) {
            PLog.e(TAG + "/pushToSocket", e.toString());
        }
    }


}
