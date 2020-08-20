package com.linkflow.fitt360sdk.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.linkflow.fitt360sdk.R;
import com.martin.ads.vrlib.PanoMediaPlayerWrapper;
import com.martin.ads.vrlib.PanoViewWrapper;
import com.martin.ads.vrlib.ui.Pano360ConfigBundle;

import tv.danmaku.ijk.media.player.IMediaPlayer;

public class VideoTestActivity extends AppCompatActivity {

    private GLSurfaceView mGLSurfaceView;

    private Pano360ConfigBundle configBundle;
    private PanoViewWrapper mPanoViewWrapper;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        mGLSurfaceView = findViewById(R.id.gLSurfaceView);
        initGL();
    }

    private void initGL() {
        Intent intent = getIntent();
//        LiveBean liveBean = (LiveBean) intent.getSerializableExtra("LiveBean");
        configBundle = (Pano360ConfigBundle) intent.getSerializableExtra("configBundle");

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
    protected void onPause() {
        super.onPause();
        mPanoViewWrapper.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPanoViewWrapper.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPanoViewWrapper.releaseResources();

//        exitRoom();
    }
}
