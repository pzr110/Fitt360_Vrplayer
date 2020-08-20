package com.linkflow.fitt360sdk.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.mediacodec.AVPacket;
import com.android.mediacodec.Packet;
import com.android.mediacodec.VideoDecoder;
import com.linkflow.fitt360sdk.R;
import com.martin.ads.vrlib.PanoMediaPlayerWrapper;
import com.martin.ads.vrlib.PanoViewWrapper;
import com.martin.ads.vrlib.constant.MimeType;
import com.martin.ads.vrlib.ui.Pano360ConfigBundle;

import app.library.linkflow.manager.NeckbandRestApiClient;
import app.library.linkflow.manager.item.RecordSetItem;
import app.library.linkflow.manager.model.StitchingModel;
import app.library.linkflow.manager.neckband.NotifyManage;
import app.library.linkflow.rtsp.AudioDecoderThread;
import app.library.linkflow.rtsp.RTSPStreamManager;
import tv.danmaku.ijk.media.player.IMediaPlayer;

public class PreviewActivity extends BaseActivity implements SurfaceHolder.Callback {
    private final String TAG = getClass().getSimpleName();
    private static final int MSG_NOT_START_RTSP = 10;
    private RTSPStreamManager mRTSPStreamManager;

    private Handler mRTSPChecker;
    private Button mMuteBtn, mStableBtn;
    private boolean mIsStable;
    private GLSurfaceView mGLSurfaceView;
    private Pano360ConfigBundle configBundle;
    private PanoViewWrapper mPanoViewWrapper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        hideHeader();
        super.onCreate(savedInstanceState);
        setBodyView(R.layout.activity_preview);


        mGLSurfaceView = findViewById(R.id.gLSurfaceView);


        SurfaceView surfaceView = findViewById(R.id.surface);
        surfaceView.getHolder().addCallback(this);

        mMuteBtn = findViewById(R.id.audio);
        mMuteBtn.setOnClickListener(this);

        mStableBtn = findViewById(R.id.stable);
        mStableBtn.setOnClickListener(this);
        mIsStable = mNeckbandManager.getSetManage().enabledStabilization();

        mStableBtn.setText(mIsStable ? R.string.stable_on : R.string.stable_off);

        mNeckbandManager.getPreviewModel().activateRTSP(mNeckbandManager.getAccessToken(), !mNeckbandManager.isPreviewing());
        mRTSPChecker = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what == MSG_NOT_START_RTSP) {
                    Log.d(TAG, "no started rtsp so do start after 6 sec");
                    mNeckbandManager.getPreviewModel().activateRTSP(mNeckbandManager.getAccessToken(), !mNeckbandManager.isPreviewing());
                    mRTSPChecker.sendEmptyMessageDelayed(MSG_NOT_START_RTSP, 6000);
                }
            }
        };



    }

    /**
     * Initialize the player
     * @param rtspUrl
     */
    private void initGL(String rtspUrl) {

        Pano360ConfigBundle configBundle = Pano360ConfigBundle
                .newInstance()
                .setFilePath(rtspUrl) // Play address
                .setTitle("直播")
                .setMimeType(MimeType.ONLINE | MimeType.VIDEO)
                .setPlaneModeEnabled(false)
                .setLive(true);

        Bitmap bitmap = null;

        assert configBundle != null;
        mPanoViewWrapper = PanoViewWrapper.with(this)
                .setConfig(configBundle)
                .setGlSurfaceView(mGLSurfaceView)
                .setBitmap(bitmap)
                .init();


        mPanoViewWrapper.getMediaPlayer().setPlayerCallback(new PanoMediaPlayerWrapper.PlayerCallback() {
            @Override
            public void updateProgress() {

            }

            @Override
            public void updateInfo() {

//                mAvi.hide();
//                mActivityImgBufferTop.setVisibility(View.GONE);
            }

            @Override
            public void requestFinish() {

            }

            @Override
            public boolean onError(IMediaPlayer mp, int what, int extra) {
                return false;
            }

//            @Override
//            public boolean onError() {
//                return false;
//            }
        });

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view.getId() == R.id.base_dialog_disagree) {
            if (mNeckbandManager.isRecording()) {
                mNeckbandManager.getRecordModel().actionRecord(mNeckbandManager.getAccessToken(), false);
                mNeckbandManager.setRecordState(false);
            }
            if (mRTSPStreamManager != null) {
                mRTSPStreamManager.stop();
            }
        } else if (view.getId() == R.id.audio) {
            boolean isAudioDisabled = !mRTSPStreamManager.isAudioDisabled();
            mRTSPStreamManager.setAudioDisable(isAudioDisabled);
            mMuteBtn.setText(isAudioDisabled ? R.string.audio_disable : R.string.audio_enable);
        } else if (view.getId() == R.id.stable) {
            setStable(mIsStable);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mPanoViewWrapper!=null){
            mPanoViewWrapper.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPanoViewWrapper!=null){
            mPanoViewWrapper.onResume();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPanoViewWrapper!=null){
            mPanoViewWrapper.releaseResources();
        }

        if (mRTSPStreamManager != null) {
            mRTSPStreamManager.stop();
        }
        mNeckbandManager.setPreviewState(false);
        mNeckbandManager.getPreviewModel().activateRTSP(mNeckbandManager.getAccessToken(), false);
    }

    private void setStable(boolean enable) {
        mNeckbandManager.getSetManage().getStitchingModel().setStabilizationState(mNeckbandManager.getAccessToken(), !enable, new StitchingModel.StabilizationListener() {
            @Override
            public void completedGetStabilizationState(boolean success, boolean enabled) {

            }

            @Override
            public void completedSetStabilizationState(boolean success) {
                if (success) {
                    mIsStable = !mIsStable;
                    mStableBtn.setText(mIsStable ? R.string.stable_on : R.string.stable_off);
                }
            }
        });
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.e(TAG, "surface created");
        mRTSPChecker.removeMessages(MSG_NOT_START_RTSP);
        mRTSPChecker.sendEmptyMessageDelayed(MSG_NOT_START_RTSP, 6000);
        RecordSetItem recordSetItem = mNeckbandManager.getSetManage().getRecordSetItem();

        mRTSPStreamManager = RTSPStreamManager.builder().setAsyncDataListener(new VideoDecoder.AsyncDataListener() {
            @Override
            public void asyncData(int type, Packet packet) {
                // packet has all information about stream. you can use it.
                if (type == AVPacket.PT_VIDEO) {

                } else if (type == AVPacket.PT_AUDIO) {

                }
            }

            @Override
            public boolean disablePreview() {
                return false;
            }
        }).setFrameCallback(new VideoDecoder.FrameCallback() {
            @Override
            public void hasFrame() {
                if (mRTSPChecker.hasMessages(MSG_NOT_START_RTSP)) {
                    mNeckbandManager.setPreviewState(true);
                    mRTSPChecker.removeMessages(MSG_NOT_START_RTSP);
                }
            }
        }).setAudioDecodedListener(new AudioDecoderThread.DecodedListener() {
            @Override
            public void decodedAudio(byte[] audioData, int offset, int length) {

            }
        }).setResolution(recordSetItem.getWidth(), recordSetItem.getHeight()).build();

        mRTSPStreamManager.setUrl(NeckbandRestApiClient.getRTSPUrl());
        mRTSPStreamManager.setSurface(holder.getSurface());
        mRTSPStreamManager.start();


    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.e(TAG, "surface changed");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surface destroyed");
    }

    @Override
    public void alertRTSP(String type) {
        if (type.equals(NotifyManage.RTSP_TYPE_EXIT)) {
            mRTSPStreamManager.stop();
            mNeckbandManager.getPreviewModel().activateRTSP(mNeckbandManager.getAccessToken(), false);
            finish();
        }
    }

}
