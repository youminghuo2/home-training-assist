package com.example.hometrainng.customview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.hometrainng.tools.PLog;

import java.io.IOException;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

import static androidx.constraintlayout.widget.Constraints.TAG;

/**
 * @Package com.example.hometrainng.customView
 * @Description java类作用描述
 * @CreateDate: 2020/5/28 3:58 PM
 */
public class IjkplayerVideoViewRecord extends FrameLayout implements TextureView.SurfaceTextureListener{
    public IjkplayerVideoViewRecord(@NonNull Context context) {
        super(context);
        initVideoView(context);
    }

    public IjkplayerVideoViewRecord(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initVideoView(context);
    }

    public IjkplayerVideoViewRecord(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initVideoView(context);
    }

    @SuppressLint("NewApi")
    public IjkplayerVideoViewRecord(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initVideoView(context);
    }

    public IMediaPlayer mediaPlayer = null;
    private String mPath = "";
    private TextureView textureView;
    private VideoPlayerListener videoPlayerListener;
    private Context mContext;
    private Surface surface;

    private void initVideoView(Context context) {
        mContext = context;
    }

    public void setVideoPath(String path) {
        if (TextUtils.equals("", mPath)) {
            mPath = path;
            createTextureView();
        } else {
            mPath = path;
            load();
        }
    }



    private void createTextureView() {
        textureView = null;
        textureView = new TextureView(getContext());
        textureView.setSurfaceTextureListener(this);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.CENTER;
        textureView.setLayoutParams(layoutParams);
        addView(textureView);
    }


    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
        this.surface = new Surface(surfaceTexture);
        load();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {
        Log.d(TAG, "onSurfaceTextureSizeChanged: ");
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        surface.release();
        return false;
    }



    private void load() {
        createPlayer();
        try {
            mediaPlayer.setDataSource(mPath);
            mediaPlayer.setLooping(true);
        } catch (IOException e) {
            PLog.e(TAG+"load",e.toString());
        }
        mediaPlayer.setSurface(surface);
        mediaPlayer.prepareAsync();
    }

    private void createPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.setDisplay(null);
            mediaPlayer.release();
        }
        IjkMediaPlayer ijkMediaPlayer = new IjkMediaPlayer();
        ijkMediaPlayer.native_setLogLevel(IjkMediaPlayer.IJK_LOG_DEBUG);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);

        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 5);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1);

        //设置不自动播放
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0);

        //丢弃一些帧来达到同步
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER,"framdedrop",1);

        mediaPlayer = ijkMediaPlayer;


        if (videoPlayerListener != null) {
            mediaPlayer.setOnPreparedListener(videoPlayerListener);
            mediaPlayer.setOnInfoListener(videoPlayerListener);
            mediaPlayer.setOnSeekCompleteListener(videoPlayerListener);
            mediaPlayer.setOnBufferingUpdateListener(videoPlayerListener);
            mediaPlayer.setOnErrorListener(videoPlayerListener);

        }

    }

    public void setVideoPlayerListener(VideoPlayerListener listener) {
        this.videoPlayerListener = listener;
        if (mediaPlayer != null) {
            mediaPlayer.setOnPreparedListener(listener);
        }
    }

    public void setVolume(float v1, float v2) {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(v1, v2);
        }
    }

    public void start() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    public void reset() {
        if (mediaPlayer != null) {
            mediaPlayer.reset();
        }
    }

    public long getDuration() {
        if (mediaPlayer != null) {
            return mediaPlayer.getDuration();
        } else {
            return 0;
        }
    }

    public long getCurrentPosition() {
        if (mediaPlayer != null) {
            return mediaPlayer.getCurrentPosition();
        } else {
            return 0;
        }
    }


    public void seekTo(long l) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(l);
        }
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        Log.d(TAG, "onSurfaceTextureUpdated: ");
    }
}
