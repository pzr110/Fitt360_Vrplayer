package com.martin.ads.vrlib.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.martin.ads.vrlib.PanoMediaPlayerWrapper;
import com.martin.ads.vrlib.PanoViewWrapper;
import com.martin.ads.vrlib.R;
import com.martin.ads.vrlib.constant.MimeType;
import com.martin.ads.vrlib.constant.PanoMode;
import com.martin.ads.vrlib.constant.PanoStatus;
import com.martin.ads.vrlib.filters.advanced.FilterType;
import com.martin.ads.vrlib.utils.UIUtils;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by Ads on 2016/11/10.
 * UI is modified from UtoVR demo
 */
//FIXME:looks so lame.
public class PanoPlayerActivity extends Activity {

    String TAG = "LIB";
    public static final String CONFIG_BUNDLE = "configBundle";

    private PanoUIController mPanoUIController;
    private PanoViewWrapper mPanoViewWrapper;
    private ImageView mImgBufferAnim;
    private ImageView mImgBufferAnimTop;
    private Pano360ConfigBundle configBundle;
    private AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.player_activity_layout);
        init();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init() {
        configBundle = (Pano360ConfigBundle) getIntent().getSerializableExtra(CONFIG_BUNDLE);
        if (configBundle == null) {
            throw new RuntimeException("config can't be null");
        }


        findViewById(R.id.progress_seek_bar).setVisibility(configBundle.isLive() ? View.GONE : View.VISIBLE);
        findViewById(R.id.txt_time_total).setVisibility(configBundle.isLive() ? View.GONE : View.VISIBLE);

        findViewById(R.id.img_full_screen).setVisibility(configBundle.isWindowModeEnabled() ? View.VISIBLE : View.GONE);

        mImgBufferAnim = (ImageView) findViewById(R.id.activity_imgBuffer);
        mImgBufferAnimTop = (ImageView) findViewById(R.id.activity_imgBuffer_top);
        mImgBufferAnimTop.setBackgroundResource(R.drawable.gs_bg);

        UIUtils.setBufferVisibility(mImgBufferAnim, mImgBufferAnimTop, !configBundle.isImageModeEnabled());
        mPanoUIController = new PanoUIController(
                (RelativeLayout) findViewById(R.id.player_toolbar_control),
                (RelativeLayout) findViewById(R.id.player_toolbar_progress),
                this, configBundle.isImageModeEnabled());
        TextView title = (TextView) findViewById(R.id.video_title);
//        title.setText(Uri.parse(configBundle.getFilePath()).getLastPathSegment());
        title.setText(configBundle.getTitle());

        GLSurfaceView glSurfaceView = (GLSurfaceView) findViewById(R.id.surface_view);
        Bitmap bitmap = null;
        if ((configBundle.getMimeType() & MimeType.BITMAP) != 0) {
            bitmap = getIntent().getParcelableExtra("bitmap");
        }
        mPanoViewWrapper = PanoViewWrapper.with(this)
                .setConfig(configBundle)
                .setGlSurfaceView(glSurfaceView)
                .setBitmap(bitmap)
                .init();
        if (configBundle.isRemoveHotspot())
            mPanoViewWrapper.removeDefaultHotSpot();
        glSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mPanoUIController.startHideControllerTimer();
                return mPanoViewWrapper.handleTouchEvent(event);
            }
        });
        mPanoUIController.setAutoHideController(true);
        mPanoUIController.setUiCallback(new PanoUIController.UICallback() {
            @Override
            public void requestScreenshot() {
                Log.e("PZRTAG", "111");
                mPanoViewWrapper.getTouchHelper().shotScreen();
            }

            @Override
            public void requestFinish() {
                Log.e("PZRTAG", "222");
//                finish();
//                ToastUtils.showLong("直播中断");
                finish();
//                ShowDialog();
            }


            @Override
            public void changeDisPlayMode() {

                if (mPanoViewWrapper.getStatusHelper().getPanoDisPlayMode() == PanoMode.DUAL_SCREEN) {
                    mPanoViewWrapper.getStatusHelper().setPanoDisPlayMode(PanoMode.SINGLE_SCREEN);
                } else {
                    mPanoViewWrapper.getStatusHelper().setPanoDisPlayMode(PanoMode.DUAL_SCREEN);
                    Log.e("PZRTAG", "555");
                }
            }

            @Override
            public void changeInteractiveMode() {
                if (mPanoViewWrapper.getStatusHelper().getPanoInteractiveMode() == PanoMode.MOTION) {
                    mPanoViewWrapper.getStatusHelper().setPanoInteractiveMode(PanoMode.TOUCH);
                    Log.e("PZRTAG", "666");
                } else {
                    mPanoViewWrapper.getStatusHelper().setPanoInteractiveMode(PanoMode.MOTION);
                    Log.e("PZRTAG", "777");
                }
            }

            @Override
            public void changePlayingStatus() {
                if (mPanoViewWrapper.getStatusHelper().getPanoStatus() == PanoStatus.PLAYING) {
                    mPanoViewWrapper.getMediaPlayer().pauseByUser();
                } else if (mPanoViewWrapper.getStatusHelper().getPanoStatus() == PanoStatus.PAUSED_BY_USER) {
                    mPanoViewWrapper.getMediaPlayer().start();
                    Log.e("PZRTAG", "999");
                }
            }

            @Override
            public void playerSeekTo(int pos) {
                mPanoViewWrapper.getMediaPlayer().seekTo(pos);
                Log.e("PZRTAG", "10");
            }

            @Override
            public int getPlayerDuration() {
                Log.e("PZRTAG", "11");
                return (int) mPanoViewWrapper.getMediaPlayer().getDuration();
            }

            @Override
            public int getPlayerCurrentPosition() {
                Log.e("PZRTAG", "12");
                return (int) mPanoViewWrapper.getMediaPlayer().getCurrentPosition();
            }

            @Override
            public void addFilter(FilterType filterType) {
                Log.e("PZRTAG", "13");
                mPanoViewWrapper.getRenderer().switchFilter();
            }
        });
        mPanoViewWrapper.getTouchHelper().setPanoUIController(mPanoUIController);
        if (!configBundle.isImageModeEnabled()) {
            Log.e("PZRTAG", "14");
            mPanoViewWrapper.getMediaPlayer().setPlayerCallback(new PanoMediaPlayerWrapper.PlayerCallback() {
                @Override
                public void updateProgress() {
                    mPanoUIController.updateProgress();
                }

                @Override
                public void updateInfo() {
                    UIUtils.setBufferVisibility(mImgBufferAnim, mImgBufferAnimTop, false);
                    mPanoUIController.startHideControllerTimer();
                    mPanoUIController.setInfo();
                }

                @Override
                public void requestFinish() {
//                    finish();
                    Log.e("PZRTAG", "17");

                    ShowDialog();
                }

                @Override
                public boolean onError(IMediaPlayer mp, int what, int extra) {
                    return false;
                }
//
//                @Override
//                public boolean onError() {
//                    return false;
//                }
            });
        } else mPanoUIController.startHideControllerTimer();
    }

    private void ShowDialog() {
        final View view = LayoutInflater.from(this).inflate(R.layout.dialog_tips, null, false);
        mDialog = new AlertDialog.Builder(this).create();
        mDialog.setView(view, 0, 0, 0, 0);

        Window window = mDialog.getWindow();
        //这一句消除白块
//        window.setBackgroundDrawable(new BitmapDrawable());
        assert window != null;
        window.setBackgroundDrawableResource(android.R.color.transparent);// 背景透明
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);// 全屏（注：内容区域全屏，有状态显示）


        mDialog.setCancelable(false);

        TextView tv_cancel = view.findViewById(R.id.tv_close);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                finish();
            }
        });

        mDialog.show();
        //此处设置位置窗体大小，我这里设置为了手机屏幕宽度的3/4  注意一定要在show方法调用后再写设置窗口大小的代码，否则不起效果会
        //此处设置位置窗体大小，
//        dialog.getWindow().setLayout(width,height);
        mDialog.getWindow().setLayout((ScreenUtils.getScreenWidth() / 2), LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPanoViewWrapper.onPause();
        Log.e("PZRTAG", "18");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPanoViewWrapper.onResume();
        Log.e("PZRTAG", "19");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPanoViewWrapper.releaseResources();

        Log.e("PZRTAG", "20");
    }


}
