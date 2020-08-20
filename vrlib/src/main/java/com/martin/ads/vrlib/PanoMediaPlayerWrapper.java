package com.martin.ads.vrlib;

import android.annotation.SuppressLint;
import android.content.res.AssetFileDescriptor;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
//import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;

import com.blankj.utilcode.util.ToastUtils;
import com.martin.ads.vrlib.constant.PanoStatus;
import com.martin.ads.vrlib.utils.StatusHelper;

import java.io.IOException;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

import static com.blankj.utilcode.util.SnackbarUtils.dismiss;

/**
 * Created by Ads on 2016/5/2.
 */
public class PanoMediaPlayerWrapper implements
        SurfaceTexture.OnFrameAvailableListener,
        IMediaPlayer.OnPreparedListener,
        IMediaPlayer.OnInfoListener,
        IMediaPlayer.OnSeekCompleteListener,
        IMediaPlayer.OnBufferingUpdateListener,
        IMediaPlayer.OnErrorListener,
        IMediaPlayer.OnCompletionListener,
        IMediaPlayer.OnVideoSizeChangedListener
//        MediaPlayer.OnCompletionListener,
//        MediaPlayer.OnErrorListener,
//        MediaPlayer.OnPreparedListener,
//        MediaPlayer.OnVideoSizeChangedListener,
//        MediaPlayer.OnInfoListener,
//        MediaPlayer.OnBufferingUpdateListener

{
    public static String TAG = "PanoMediaPlayerWrapper";

    private StatusHelper statusHelper;

    private PanoViewWrapper.RenderCallBack renderCallBack;

    private SurfaceTexture mSurfaceTexture;

    //    private MediaPlayer mMediaPlayer;//MediaPlayer播放器
    private IMediaPlayer mMediaPlayer = null;
    ;//第三方播放器
    IjkMediaPlayer ijkMediaPlayer = new IjkMediaPlayer();
//    private VideoPlayerListener listener;

    private PlayerCallback playerCallback;

    private PanoRender.OnTextureSizeChangedCallback onTextureSizeChangedCallback;

    //构造函数
    public PanoMediaPlayerWrapper() {
//        mMediaPlayer=new MediaPlayer();
//        mMediaPlayer.setOnPreparedListener(this);
//        mMediaPlayer.setOnCompletionListener(this);
//        mMediaPlayer.setOnErrorListener(this);
//        mMediaPlayer.setOnVideoSizeChangedListener(this);
//        mMediaPlayer.setOnInfoListener(this);
//        mMediaPlayer.setOnBufferingUpdateListener(this);
        //创建播放器
        createPlayer();
    }

    //创建一个播放器
    private void createPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.setDisplay(null);
            mMediaPlayer.release();

        }
        IjkMediaPlayer ijkMediaPlayer = new IjkMediaPlayer();
        ijkMediaPlayer.native_setLogLevel(IjkMediaPlayer.IJK_LOG_DEBUG);

//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);//开启硬解码

//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 60);
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max-fps", 0);
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "fps", 30);
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_RV16);
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "packet-buffering", 0);
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "fflags", "nobuffer");
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "max-buffer-size", 1024);
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "min-frames", 3);
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 1);
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "probsize", "4096");
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzeduration", "2000000");


        // 支持硬解 1：开启 O:关闭
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-hevc", 1);
        // 设置播放前的探测时间 1,达到首屏秒开效果
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzeduration", 1);

        /**
         * 播放延时的解决方案
         */
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_RV32);
        // 如果是rtsp协议，可以优先用tcp(默认是用udp)
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "rtsp_transport", "tcp");
        // 设置播放前的最大探测时间 （100未测试是否是最佳值）
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzemaxduration", 100);
        // 每处理一个packet之后刷新io上下文
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "flush_packets", 1L);
        // 需要准备好后自动播放
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 1);
//        // 不额外优化（使能非规范兼容优化，默认值0 ）
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "fast", 1);
//        // 是否开启预缓冲，一般直播项目会开启，达到秒开的效果，不过带来了播放丢帧卡顿的体验
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "packet-buffering",  0);
//        // 自动旋屏
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 0);
//        // 处理分辨率变化
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", 0);
        // 最大缓冲大小,单位kb
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "max-buffer-size", 0);
        // 默认最小帧数2
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "min-frames", 2);
//        // 最大缓存时长
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER,  "max_cached_duration", 3); //300
//        // 是否限制输入缓存数
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER,  "infbuf", 1);
        // 缩短播放的rtmp视频延迟在1s内
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "fflags", "nobuffer");
//        // 播放前的探测Size，默认是1M, 改小一点会出画面更快
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "probesize", 200); //1024L)
        // 播放重连次数
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER,"reconnect",5);
        // TODO:
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "http-detect-range-support", 0);
//        // 设置是否开启环路过滤: 0开启，画面质量高，解码开销大，48关闭，画面质量差点，解码开销小
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48L);
//        // 跳过帧 ？？
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_frame", 0);
//        // 视频帧处理不过来的时候丢弃一些帧达到同步的效果
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 5);

        //----------------尝试方案2------------------
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_RV32);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "rtsp_transport", "tcp");
        ijkMediaPlayer.setOption(1, "analyzemaxduration", 100L);
        ijkMediaPlayer.setOption(1, "probesize", 10240L);
        ijkMediaPlayer.setOption(1, "flush_packets", 1L);
        ijkMediaPlayer.setOption(4, "packet-buffering", 0L);
        ijkMediaPlayer.setOption(4, "framedrop", 1L); //
//        ijkMediaPlayer.setOption(4, "framedrop", 5);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 0);//环路过滤，画面质量高
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "max-buffer-size", 0);//缓冲区大小


        //----------------尝试方案2------------------

        // 如果是rtsp协议，可以优先用tcp(默认是用udp)
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "rtsp_transport", "tcp");
//// 设置播放前的探测时间 1,达到首屏秒开效果
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzeduration", 1);
//// 设置播放前的最大探测时间 （100未测试是否是最佳值）
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzemaxduration", 100L);
//// 每处理一个packet之后刷新io上下文
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "flush_packets", 1L);
//// 需要准备好后自动播放
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0);
//// 是否开启预缓冲，一般直播项目会开启，达到秒开的效果，不过带来了播放丢帧卡顿的体验
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "packet-buffering",  0);
//
////        String url = mUri.toString();
////        if (!TextUtils.isEmpty(url) && url.contains("rtsp://")) {  // 如果 rtmp 的协议， 修改 size 后会没有声音
////// 播放前的探测Size，默认是1M, 改小一点会出画面更快
////            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "probesize", 1024); //1024L)
////        }
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "http-detect-range-support", 0);
//// 设置是否开启环路过滤: 0开启，画面质量高，解码开销大，48关闭，画面质量差点，解码开销小
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48L);
//// 跳过帧 ？？
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_frame", 8);
//// 视频帧处理不过来的时候丢弃一些帧达到同步的效果
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1);


        //--------------------------------------------


        mMediaPlayer = ijkMediaPlayer;
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnInfoListener(this);
        mMediaPlayer.setOnSeekCompleteListener(this);
        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnVideoSizeChangedListener(this);
//        mMediaPlayer.setVolume(0,0); // 静音
    }

    public void setRenderCallBack(PanoViewWrapper.RenderCallBack renderCallBack) {
        this.renderCallBack = renderCallBack;
    }

    public void setSurface(int mTextureID) {
        mSurfaceTexture = new SurfaceTexture(mTextureID);
        mSurfaceTexture.setOnFrameAvailableListener(this);
        Surface surface = new Surface(mSurfaceTexture);
        mMediaPlayer.setSurface(surface);
        surface.release();
    }

    public void doTextureUpdate(float[] mSTMatrix) {
        mSurfaceTexture.updateTexImage();
        mSurfaceTexture.getTransformMatrix(mSTMatrix);
    }

    public void openRemoteFile(String path) {
        try {
            mMediaPlayer.setDataSource(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setMediaPlayerFromUri(Uri uri) {
        Log.e("PZRTAG", "setMediaPlayerFromUri");

        try {
            mMediaPlayer.setDataSource(statusHelper.getContext(), uri);
        } catch (IOException e) {
            Log.e("PZRTAG", "setMediaPlayerFromUri111" + e.getMessage().toString());
            e.printStackTrace();
        }
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setLooping(true);
    }

    public void setMediaPlayerFromAssets(AssetFileDescriptor assetFileDescriptor) {
//        try{
//            mMediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(), assetFileDescriptor.getStartOffset(), assetFileDescriptor.getDeclaredLength());
//        }catch (IOException e){
//            e.printStackTrace();
//        }
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setLooping(true);
    }

    public void setStatusHelper(StatusHelper statusHelper) {
        this.statusHelper = statusHelper;
    }

    public void prepare() {
        Log.e("PZRTAG", "prepare:准备了");

        if (statusHelper.getPanoStatus() == PanoStatus.IDLE || statusHelper.getPanoStatus() == PanoStatus.STOPPED) {
            mMediaPlayer.prepareAsync();

        }
    }

    public void start() {
        Log.e("PZRTAG", "start:开始了");

        PanoStatus panoStatus = statusHelper.getPanoStatus();
        if (panoStatus == PanoStatus.PREPARED || panoStatus == PanoStatus.PAUSED || panoStatus == PanoStatus.PAUSED_BY_USER) {
            mMediaPlayer.start();
            statusHelper.setPanoStatus(PanoStatus.PLAYING);

        }
    }

    public void pause() {
        Log.e("PZRTAG", "pause:暂停");
        PanoStatus panoStatus = statusHelper.getPanoStatus();
        if (panoStatus == PanoStatus.PLAYING) {
            mMediaPlayer.pause();
            statusHelper.setPanoStatus(PanoStatus.PAUSED);

        }
    }

    public void pauseByUser() {
        PanoStatus panoStatus = statusHelper.getPanoStatus();
        if (panoStatus == PanoStatus.PLAYING) {
            mMediaPlayer.pause();
            statusHelper.setPanoStatus(PanoStatus.PAUSED_BY_USER);
        }
        Log.e("PZRTAG", "pauseByUser:用户暂停");

    }

    public void stop() {
        PanoStatus panoStatus = statusHelper.getPanoStatus();
        if (panoStatus == PanoStatus.PLAYING
                || panoStatus == PanoStatus.PREPARED
                || panoStatus == PanoStatus.PAUSED
                || panoStatus == PanoStatus.PAUSED_BY_USER) {
            mMediaPlayer.stop();
            statusHelper.setPanoStatus(PanoStatus.STOPPED);
        }

        Log.e("PZRTAG", "stop:停止");

    }

    public void releaseResource() {
        if (mMediaPlayer != null) {
            mMediaPlayer.setSurface(null);
            if (mSurfaceTexture != null) mSurfaceTexture = null;
            stop();
            mMediaPlayer.release();
        }
        Log.e("PZRTAG", "releaseResource:释放资源");

    }

//    @Override
//    public void onCompletion(MediaPlayer mp) {
//        statusHelper.setPanoStatus(PanoStatus.COMPLETE);
//        if (playerCallback!=null){
//            playerCallback.requestFinish();
//        }
//    }

//    @Override
//    public boolean onError(MediaPlayer mp, int what, int extra) {
//        return false;
//    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        renderCallBack.renderImmediately();
        if (playerCallback != null) {
            playerCallback.updateProgress();
        }
    }

//    @Override
//    public void onPrepared(MediaPlayer mp) {
//        statusHelper.setPanoStatus(PanoStatus.PREPARED);
//        if (playerCallback!=null){
//            playerCallback.updateInfo();
//        }
//        start();
//    }

//    @Override
//    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
//        if(onTextureSizeChangedCallback!=null)
//            onTextureSizeChangedCallback.notifyTextureSizeChanged(width,height);
//    }

    public void seekTo(int pos) { // 拖动回跳到关键帧播放
        if (mMediaPlayer != null) {
            PanoStatus panoStatus = statusHelper.getPanoStatus();
            if (panoStatus == PanoStatus.PLAYING
                    || panoStatus == PanoStatus.PAUSED
                    || panoStatus == PanoStatus.PAUSED_BY_USER)
                mMediaPlayer.seekTo(pos);
        }
    }

//    public int getDuration(){
//        if (mMediaPlayer!=null){
//            return mMediaPlayer.getDuration();
//        }
//        return 0;
//    }

    public long getDuration() {  // 获取时长
        if (mMediaPlayer != null) {
            return mMediaPlayer.getDuration();
        }
        return 0;
    }

//    public int getCurrentPosition(){
//        if (mMediaPlayer!=null){
//            return mMediaPlayer.getCurrentPosition();
//        }
//        return 0;
//    }

    public void setMute(float volume) {
        if (mMediaPlayer != null) {
            mMediaPlayer.setVolume(volume, volume);
        }
    }

    public long getCurrentPosition() { // 获取当前位置
        if (mMediaPlayer != null) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

//    @Override
//    public void onBufferingUpdate(MediaPlayer mp, int percent) {
//    }

//    @Override
//    public boolean onInfo(MediaPlayer mp, int what, int extra) {
//        return false;
//    }

    @Override
    public void onPrepared(IMediaPlayer mp) {  //  准备就绪
        statusHelper.setPanoStatus(PanoStatus.PREPARED);  // 准备就绪
        if (playerCallback != null) {
            playerCallback.updateInfo();
        }

//        Log.e("PZRTAG", "onPrepared");
//        mp.setOnBufferingUpdateListener(new IMediaPlayer.OnBufferingUpdateListener() {
//            long currentPosition, duration;
//
//            @Override
//            public void onBufferingUpdate(IMediaPlayer mp, int percent) {
//                // 获得当前播放时间和当前视频的长度
//                currentPosition = mMediaPlayer.getCurrentPosition(); //当前播放时间
//                duration = mMediaPlayer.getDuration(); // 总时长
//
//                Log.e("Buffering", "percent:" + percent + " currentPosition:" + currentPosition + " duration:" + duration);
//
////                long time = ((currentPosition * 100) / duration);
//            }
//        });
        start();
    }

//    @SuppressLint("HandlerLeak")
//    private Handler mHandler = new Handler() {
//        public void handleMessage(android.os.Message msg) {
//            if (msg.arg1 >= msg.arg2) {
//                if (bufferingProgress == 0) {
//                    if (!creatingProgress.isShowing()) {
//                        show();
//                        if (mVideoView.isPlaying()) {
//                            dismiss();
//                        }
//                    }
//                } else {
//                    show();
//                }
//            } else {
//                dismiss();
//            }
//        }
//
//        ;
//    };

    @Override
    public boolean onInfo(IMediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onSeekComplete(IMediaPlayer mp) {

    }

    @Override
    public void onBufferingUpdate(IMediaPlayer mp, int percent) {

    }

    @Override
    public boolean onError(IMediaPlayer mp, int what, int extra) {
        statusHelper.setPanoStatus(PanoStatus.ERROR);
        if (playerCallback != null) {
            playerCallback.onError(mp, what, extra);
        }
        return false;
    }

    @Override
    public void onCompletion(IMediaPlayer mp) {
        statusHelper.setPanoStatus(PanoStatus.COMPLETE);
        if (playerCallback != null) {
            playerCallback.requestFinish();
        }
    }

    @Override
    public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sar_num, int sar_den) {
        if (onTextureSizeChangedCallback != null)
            onTextureSizeChangedCallback.notifyTextureSizeChanged(width, height);
    }

    public interface PlayerCallback {
        void updateProgress();

        void updateInfo();

        void requestFinish();

        boolean onError(IMediaPlayer mp, int what, int extra);
    }

    public void setPlayerCallback(PlayerCallback playerCallback) {
        this.playerCallback = playerCallback;
    }

    public void setOnTextureSizeChangedCallback(PanoRender.OnTextureSizeChangedCallback onTextureSizeChangedCallback) {
        this.onTextureSizeChangedCallback = onTextureSizeChangedCallback;
    }
}
