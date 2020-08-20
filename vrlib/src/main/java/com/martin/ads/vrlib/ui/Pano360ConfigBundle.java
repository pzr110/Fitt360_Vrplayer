package com.martin.ads.vrlib.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.SparseArray;

import com.martin.ads.vrlib.constant.MimeType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Ads on 2017/4/12.
 */

public class Pano360ConfigBundle implements Serializable{
    private String filePath;
    private String videoHotspotPath;
    private boolean imageModeEnabled;
    private boolean planeModeEnabled;
    private boolean windowModeEnabled;
    private boolean removeHotspot;
    private boolean isLive;
    private int mimeType;
    private String title;



    private Pano360ConfigBundle() {
        filePath=null;
        title="";
        videoHotspotPath=null;
        imageModeEnabled=false;
        planeModeEnabled=false;
        windowModeEnabled=false;
        removeHotspot=false;
        isLive=true;
    }

    public static Pano360ConfigBundle newInstance(){
        return new Pano360ConfigBundle();
    }

    public void startEmbeddedActivity(Context context){
        Intent intent=new Intent(context,PanoPlayerActivity.class);
        intent.putExtra(PanoPlayerActivity.CONFIG_BUNDLE,this);
        context.startActivity(intent);
    }

//    public void startEmbeddedActivity(Context context){
//        Intent intent=new Intent(context,PanoPlayerNewActivity.class);
//        intent.putExtra(PanoPlayerActivity.CONFIG_BUNDLE,this);
//        context.startActivity(intent);
//    }

    public void startEmbeddedActivityWithSpecifiedBitmap(Context context,Bitmap bitmap){
        Intent intent=new Intent(context,PanoPlayerActivity.class);
        intent.putExtra(PanoPlayerActivity.CONFIG_BUNDLE,this);
        intent.putExtra("bitmap",bitmap);
        context.startActivity(intent);
    }

    public String getFilePath() {
        return filePath;
    }

    public Pano360ConfigBundle setFilePath(String filePath) {
        this.filePath = filePath;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Pano360ConfigBundle setTitle(String titles) {
        this.title = titles;
        return this;
    }

    public String getVideoHotspotPath() {
        return videoHotspotPath;
    }

    public Pano360ConfigBundle setVideoHotspotPath(String videoHotspotPath) {
        this.videoHotspotPath = videoHotspotPath;
        return this;
    }

    public boolean isImageModeEnabled() {
        return imageModeEnabled;
    }

    public boolean isPlaneModeEnabled() {
        return planeModeEnabled;
    }

    public Pano360ConfigBundle setPlaneModeEnabled(boolean planeModeEnabled) { // 平面
        this.planeModeEnabled = planeModeEnabled;
        return this;
    }

    public boolean isWindowModeEnabled() {
        return windowModeEnabled;
    }

    public boolean isRemoveHotspot() {
        return removeHotspot;
    }

    public Pano360ConfigBundle setRemoveHotspot(boolean removeHotspot) {
        this.removeHotspot = removeHotspot;
        return this;
    }

    public boolean isLive() {
        return isLive;
    }

    public Pano360ConfigBundle setLive(boolean live) {
        this.isLive = live;
        return this;
    }

    public Pano360ConfigBundle setMimeType(int mimeType) {
        this.mimeType = mimeType;
        imageModeEnabled=(mimeType & MimeType.PICTURE)!=0;
        return this;
    }

    public int getMimeType() {
        return mimeType;
    }
}
