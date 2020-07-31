package com.example.hometrainng.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hometrainng.R;
import com.example.hometrainng.customview.IjkplayerVideoViewRecord;
import com.example.hometrainng.customview.VideoPlayerListener;
import com.example.hometrainng.db.RecordVideo;
import com.example.hometrainng.db.Video;
import com.example.hometrainng.tools.Utils;
import com.example.hometrainng.tools.ViewUtils;

import org.litepal.LitePal;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import tv.danmaku.ijk.media.player.IMediaPlayer;

public class VideoEditAdapter extends RecyclerView.Adapter<VideoEditAdapter.MyViewHolder> implements View.OnClickListener {
    private static final String TAG = "VideoEditAdapter";
    private LayoutInflater inflater;
    private Context mContext;
    private List<RecordVideo> mList;
    private List<RecordVideo> selectVideos = new ArrayList<>();
    private boolean isSelectAll = false;

    private SeekBar ijkplayer_seekbar;
    private TextView ijkPlayerTv, duration_tv_all, title_tv, date_tv, ijkPlayerTv2;
    public IjkplayerVideoViewRecord ijkplayer_video;
    private ImageView ivClose;
    private long current, duration, seconds, minutes;
    private boolean isPlaying = true;
    private Handler handler = new Handler();
    private Runnable runnable;
   public  AlertDialog dialog;

    public VideoEditAdapter(Context context, List<RecordVideo> list) {
        this.mContext = context;
        this.mList = list;
        inflater = LayoutInflater.from(context);
        isSelectAll = false;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.video_edit_item, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        selectVideos.clear();
        if (isSelectAll) {
            holder.itemView.setTag(1);
            holder.bg.setVisibility(View.VISIBLE);
            selectVideos.addAll(mList);
        } else {
            holder.itemView.setTag(0);
            holder.bg.setVisibility(View.GONE);
        }

        List<Video> videoList = LitePal.select("*").where("videoId = ?", String.valueOf(mList.get(position).getVideoId())).find(Video.class);
        holder.textView.setText(videoList.get(0).getTitle());


        Glide.with(mContext).load("file://" + mList.get(position).getRecordImg()).into(holder.imageView);

        LocalDate localDate = LocalDate.now();
        LocalDate sqliteDate = LocalDate.parse(mList.get(position).getRecordDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));


        holder.rl_above.setOnClickListener(view -> {
            if (holder.itemView.getTag().equals(0)) {
                holder.itemView.setTag(1);
                holder.bg.setVisibility(View.VISIBLE);
                selectVideos.add(mList.get(position));
            } else if (holder.itemView.getTag().equals(1)) {
                holder.itemView.setTag(0);
                holder.bg.setVisibility(View.GONE);
                selectVideos.remove(mList.get(position));
            }
        });

        holder.rl_below.setOnClickListener(view -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            dialog = builder.create();
            View videoEditView = View.inflate(mContext, R.layout.ijkplayer_popup, null);
            dialog.show();
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.round_dialog_bg);
            dialog.setCanceledOnTouchOutside(false);
            Window window = dialog.getWindow();
            window.setContentView(videoEditView);
            dialog.getWindow().setLayout(ViewUtils.dp2px(mContext, 888), ViewUtils.dp2px(mContext, 716));
            ivClose = videoEditView.findViewById(R.id.iv_close);
            ijkplayer_seekbar = videoEditView.findViewById(R.id.ijkplayer_seekbar);
            ijkPlayerTv = videoEditView.findViewById(R.id.ijkPlayerTv);
            duration_tv_all = videoEditView.findViewById(R.id.duration_tv_all);
            title_tv = videoEditView.findViewById(R.id.title_tv);
            date_tv = videoEditView.findViewById(R.id.date_tv);
            ijkPlayerTv2 = videoEditView.findViewById(R.id.ijkPlayerTv2);
            ijkplayer_video = videoEditView.findViewById(R.id.ijkplayer_video);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.dimAmount = 0.5f;
            window.setAttributes(lp);
            duration_tv_all.setText(mList.get(position).getDuration());
            date_tv.setText(Utils.TimeToHHMMSS(mList.get(position).getRecordDate()));
            title_tv.setText(videoList.get(0).getTitle());
            ivClose.setOnClickListener(v -> {
                ijkplayer_video.pause();
                ijkplayer_video.release();
                dialog.dismiss();
            });

            dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
                    if (keyCode == KeyEvent.KEYCODE_SEARCH) {
                        return true;
                    } else if (keyCode==KeyEvent.KEYCODE_BACK){
                        ijkplayer_video.release();
                        handler.removeCallbacks(runnable);
                        return false;
                    }else {
                        return false;
                    }
                }
            });
            ijkplayer_video.setVideoPlayerListener(new VideoPlayerListener() {
                @Override
                public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int i) {
                    Log.d(TAG, "onBufferingUpdate: ");
                }

                @Override
                public void onCompletion(IMediaPlayer iMediaPlayer) {
                    Log.d(TAG, "onCompletion: ");

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
                    ijkplayer_seekbar.setOnTouchListener((view, motionEvent) -> true);
                    iMediaPlayer.start();
                }

                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onSeekComplete(IMediaPlayer iMediaPlayer) {
                    Log.d(TAG, "onSeekComplete: ");
                }

                @Override
                public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int i, int i1, int i2, int i3) {
                    Log.d(TAG, "onVideoSizeChanged: ");
                }
            });
            ijkplayer_video.setVideoPath(mList.get(position).getRecordPath());
            isPlaying = true;
            ijkplayer_video.start();
            getpoll();
        });


//        holder.WatchVideoImg.setVisibility(View.GONE);
        holder.tv_time.setText(Utils.TimeToHHMMSS(mList.get(position).getRecordDate()));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void refreshTime() {
        current = ijkplayer_video.getCurrentPosition() / 1000;
        duration = ijkplayer_video.getDuration() / 1000;
        seconds = current % 60;
        minutes = current / 60;


        String time = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        ijkPlayerTv.setText(time);
        ijkPlayerTv2.setText(time);
        if (duration != 0) {
            ijkplayer_seekbar.setProgress((int) ((current * 100) / duration));
            if (((current == duration) || (duration - current) == 1) && isPlaying) {
                ijkplayer_video.pause();
                ijkplayer_video.release();
                dialog.dismiss();
            }
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
        handler.postDelayed(runnable, 1000);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onClick(View view) {
    }

    public List<RecordVideo> getSelectVideos() {
        return selectVideos;
    }

    public void refresh(boolean isSelectAll) {
        this.isSelectAll = isSelectAll;
        selectVideos.clear();
        notifyDataSetChanged();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cardview_item_img)
        ImageView imageView;
        @BindView(R.id.cardview_item_tv)
        TextView textView;
        //        @BindView(R.id.watch_video_img)
//        ImageView WatchVideoImg;
        @BindView(R.id.tv_time)
        TextView tv_time;
        //        @BindView(R.id.tv_date)
//        TextView tv_date;
        @BindView(R.id.bg)
        View bg;
        @BindView(R.id.rl_time)
        RelativeLayout rl_time;
        @BindView(R.id.notification_player_img)
        ImageView notification_player_img;
        @BindView(R.id.rl_below)
        RelativeLayout rl_below;
        @BindView(R.id.rl_above)
        RelativeLayout rl_above;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
