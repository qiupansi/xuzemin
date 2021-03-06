package com.youkes.browser.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.youkes.browser.R;
import com.youkes.browser.adapter.GridViewAdapter;
import com.youkes.browser.object.GridItem;
import com.youkes.browser.object.IntentUrl;
import com.youkes.browser.utils.Constant;
import com.youkes.browser.utils.DBHelper;
import com.youkes.browser.utils.FileHandle;
import com.youkes.browser.utils.LogUtil;
import com.youkes.browser.utils.RootCmd;
import com.youkes.browser.utils.SaveBitmap;
import com.youkes.browser.utils.ScreenUtil;
import com.youkes.browser.utils.SharedPreferencesHelper;
import com.youkes.browser.utils.SoftKeyboardUtil;
import com.youkes.browser.utils.ToastUtil;
import com.youkes.browser.view.BlurBuilder;
import com.youkes.browser.view.LauncherVideoView;
import com.youkes.browser.view.MyDialog;
import com.youkes.browser.view.ZB_MyDialog;
import com.youkes.browser.widget.media.IRenderView;
import com.youkes.browser.widget.media.IjkVideoView;
import com.youkes.browser.widget.media.Settings;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tv.danmaku.ijk.media.player.IMediaPlayer;

import static com.youkes.browser.constant.Constants.TAG;
import static com.youkes.browser.utils.Constant.Current_Image;
import static com.youkes.browser.utils.Constant.Current_Video;
import static com.youkes.browser.utils.Constant.EVENT_GETEVENT;
import static com.youkes.browser.utils.Constant.EVENT_START_VIDEO;
import static com.youkes.browser.utils.Constant.EVENT_TO_THREAD;
import static com.youkes.browser.utils.Constant.SHOWTIME;
import static com.youkes.browser.utils.Constant.isImagePlay;
import static com.youkes.browser.utils.Constant.isVideoPlay;


public class MainActivity extends Activity implements View.OnClickListener {
    public static IntentUrl intentUrl;
    private Intent intent;
    private LinearLayout layout_cn,layout_content,layout_video,layout_new,layout_message,layout_contact,background,layout_background;
    private boolean isMain = true;
    private int currentIndex = 0;
    private RelativeLayout admin_relative;
    private static Bitmap backgroundback = null;
    private GridView gridView,video_girdview,news_girdview,message_girdview,contact_girdview,gridview_background;
    private ImageView imgview;
    private int videoRota;
    public static IjkVideoView videoView;
    private LinearLayout ll_video,ll_image;
    private RelativeLayout main_layout;
    public  static List<PackageInfo> packageInfoList = null;
    private static int REQUEST_EXTERNAL_STRONGE = 1;
    private RelativeLayout shipin,news_layout,message_layout,contact,settings,play,content;
    private GridViewAdapter gridViewAdapter,gridViewAdapter_video,gridViewAdapter_news,gridViewAdapter_message,gridViewAdapter_contact,gridViewAdapter_background;
    public static ArrayList<GridItem> mGridData,mBackgroudData;
    public static Boolean isDelete = false;
    private static ArrayList<String> urllist,video_urllist,news_urllist,message_urllist,contact_urllist;
    private PackageManager packageManager;
    private ProgressDialog progressDialog;

    public static PackageManager pm;
    public static ArrayList<String> VideoNameList;
    private static ArrayList<String> ImageList;
    public static ArrayList<String> ImageNameList;
    private static ArrayList<Bitmap> ImageBitmapList;
    private static String FileName = null;
    private RelativeLayout admin_layout;
    private boolean isRunning = false;
    private SharedPreferencesHelper sharedPreferencesHelper;
    private static DBHelper dbHelper;
    private static BroadcastReceiver installedReceiver;
    public static boolean isAdmin = false;
    private static String uninstall_Name = null;
    public static int applicationNumber = 0;
    public static List<Map> urlList = new ArrayList<>();// 机器人
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case EVENT_START_VIDEO:
                    LogUtil.e("EVENT_START_VIDEO");
                    if ((VideoNameList != null &&  VideoNameList.size() > 0)
                            || (ImageList != null && ImageList.size() > 0)) {
                        startPlay();
                    }
                    break;
                case EVENT_GETEVENT:
                    LogUtil.e("EVENT_GETEVENT");
                    FileHandle.readFile(Constant.EventPath);
                    break;
                case EVENT_TO_THREAD:
                    if ((VideoNameList == null || VideoNameList.size() == 0)
                            && (ImageNameList == null || ImageNameList.size() == 0)) {
                        return;
                    }
                    if (videoView.isPlaying()) {
                        videoView.pause();
                        videoView.stopBackgroundPlay();
                    }
                    isVideoPlay = false;
                    isImagePlay = false;
                    handler.removeMessages(EVENT_START_VIDEO);
                    ll_image.setVisibility(View.GONE);
                    ll_video.setVisibility(View.GONE);
                    main_layout.setVisibility(View.VISIBLE);
                    ll_video.setVisibility(View.GONE);
                    ll_image.setVisibility(View.VISIBLE);
                    if (isRunning && !isVideoPlay && !isImagePlay) {
                        Constant.debugLog("MyConstant.Constant 22222" + isVideoPlay + ";" + isImagePlay + ";" + isRunning);
                        if (Constant.isResetPlay) {
                            Constant.CurrentNumber = 0;
                            Constant.isResetPlay = false;
                        }
                        if (Constant.CurrentNumber >= Constant.OUTTIME) {
                            if (!Constant.isPlay(MainActivity.this)) {
                                Constant.CurrentNumber = 0;
                                isVideoPlay = true;
                                isImagePlay = true;
                                handler.sendEmptyMessage(EVENT_START_VIDEO);
                                return;
                            } else {
                                Constant.CurrentNumber = 0;
                            }
                        } else {
                            if (Constant.isPlay(MainActivity.this)) {
                                Constant.CurrentNumber = 0;
                            }
                            Constant.CurrentNumber++;
                        }
                        if(!FileHandle.isIsRun()){
                            handler.sendEmptyMessage(EVENT_GETEVENT);
                        }
                        Constant.debugLog("MyConstant.Constant 22222" + Constant.CurrentNumber);
                        handler.sendEmptyMessageDelayed(EVENT_TO_THREAD, 2000);
                    }
                    break;
            }

        }
    } ;

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        isRunning = false;
        setContentView(R.layout.activity_first);
        layout_cn = findViewById(R.id.layout_cn);
        findViewById(R.id.admin).setOnClickListener(this);
        gridView = findViewById(R.id.all_content);
        video_girdview = findViewById(R.id.video_girdview);
        layout_content = findViewById(R.id.layout_content);
        layout_video = findViewById(R.id.layout_video);
        news_girdview = findViewById(R.id.new_girdview);
        layout_new = findViewById(R.id.layout_new);
        message_girdview = findViewById(R.id.message_girdview);
        layout_message = findViewById(R.id.layout_message);
        contact_girdview = findViewById(R.id.contact_girdview);
        layout_contact = findViewById(R.id.layout_contact);
        background = findViewById(R.id.background);
        layout_background =findViewById(R.id.layout_background);
        gridview_background = findViewById(R.id.gridview_background);
        dbHelper = DBHelper.getInstance(getApplicationContext());
        admin_layout = findViewById(R.id.admin_layout);
        admin_relative = findViewById(R.id.admin_relative);
        main_layout = findViewById(R.id.main_layout);
        videoView = findViewById(R.id.videoView);
        ll_video = findViewById(R.id.ll_video);
        imgview = findViewById(R.id.imgview);
        ll_image = findViewById(R.id.ll_image);
        initView();
    }
    private void initView(){
//        layout_cn.setVisibility(View.VISIBLE);
        shipin = findViewById(R.id.shipin);
        shipin.setOnClickListener(this);
        news_layout = findViewById(R.id.news_layout);
        news_layout.setOnClickListener(this);
        message_layout = findViewById(R.id.message_layout);
        message_layout.setOnClickListener(this);
        contact = findViewById(R.id.contact);
        contact.setOnClickListener(this);
        play = findViewById(R.id.play);
        play.setOnClickListener(this);
        settings = findViewById(R.id.settings);
        settings.setOnClickListener(this);
        content = findViewById(R.id.content);
        content.setOnClickListener(this);
        admin_layout.setOnClickListener(this);
        ll_video.setOnClickListener(this);
        ll_image.setOnClickListener(this);
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sharedPreferencesHelper = new SharedPreferencesHelper(
                MainActivity.this, Constant.TAG);
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            backgroundback = ((BitmapDrawable)getResources().getDrawable(R.mipmap.beijing4)).getBitmap();
            background.setBackgroundDrawable(new BitmapDrawable(SaveBitmap.getInstance().resizeBitmap(
                    backgroundback,ScreenUtil.getScreenWidth(getApplicationContext()),ScreenUtil.getScreenHeight(getApplicationContext()))));
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STRONGE);
        }else {
            if(sharedPreferencesHelper.contain(Constant.fileName)){
//            SaveBitmap.saveBitmap(getApplicationContext(), backgroundback, Constant.backround);
                RefreshArray();
                if(ImageNameList !=null && ImageNameList.size() >= 4){
                    reBackground();
                }else{
                    if(backgroundback !=null) {
                        background.setBackgroundDrawable(new BitmapDrawable(SaveBitmap.getInstance().resizeBitmap(
                                backgroundback,ScreenUtil.getScreenWidth(getApplicationContext()),ScreenUtil.getScreenHeight(getApplicationContext()))));
                    }else{
                        ImageInit();
                    }
                }
            }else{
                ImageInit();
            }

            getPhotoVideoPath();
            getVideoPath();
        }

        installedReceiver = new BootReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.PACKAGE_ADDED");
        filter.addAction("android.intent.action.PACKAGE_REMOVED");
        filter.addDataScheme("package");
        this.registerReceiver(installedReceiver, filter);
        if (!isImagePlay && !isVideoPlay ) {
            handler.sendEmptyMessage(EVENT_TO_THREAD);
            handler.sendEmptyMessage(EVENT_GETEVENT);
        } else {
            handler.sendEmptyMessage(EVENT_START_VIDEO);
        }
        isRunning = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(installedReceiver);
        LogUtil.d("is onPause");
        isRunning = false;
        handler.removeMessages(EVENT_TO_THREAD);
        FileHandle.stopFileHandle();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Constant.CurrentNumber = 0;
        LogUtil.d("00000"+event.toString());
        return super.onTouchEvent(event);
    }

    public void startPlay() {
        if(dialog !=null &&dialog.isShowing()){
            dialog.dismiss();
        }
        getVideoPath();
        getPhotoVideoPath();
        Constant.debugLog(" VideoNameList"+ VideoNameList.size());
        Constant.debugLog(" isVideoPlay"+ isVideoPlay);
        Constant.debugLog(" Current_Video"+ Current_Video);
        Constant.CurrentNumber = 0;
        handler.removeMessages(EVENT_START_VIDEO);
        main_layout.setVisibility(View.GONE);
        if(isVideoPlay) {
            if(VideoNameList!=null && VideoNameList.size() > 0) {
                ll_video.setVisibility(View.VISIBLE);
                ll_image.setVisibility(View.GONE);
                if (Current_Video < VideoNameList.size()) {
                    isImagePlay = false;
                    videoView.setVideoPath(VideoNameList.get(Current_Video));
                    videoView.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(IMediaPlayer mp) {
                            handler.sendEmptyMessage(EVENT_START_VIDEO);
                        }
                    });
                    videoView.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
                        @Override
                        public boolean onError(IMediaPlayer mp, int what, int extra) {
                            LogUtil.e("what"+what);
                            LogUtil.e("extra"+extra);
                            handler.sendEmptyMessage(EVENT_START_VIDEO);
                            return true;
                        }
                    });
                    videoView.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(IMediaPlayer mp) {
                            videoView.start();
                            videoView.stopBackgroundPlay();
                        }
                    });
                    Current_Video++;
                } else {
                    ChangeToVideo(false);
                }
            }else{
                ChangeToVideo(false);
            }
        }

        Constant.debugLog(" ImageList"+ ImageList.size());
        Constant.debugLog(" isImagePlay"+ isImagePlay);
        Constant.debugLog(" Current_Image"+ Current_Image);
        if(isImagePlay) {
            if(videoView.isPlaying()){
                videoView.pause();
                videoView.stopBackgroundPlay();
            }
            if(ImageList!=null && ImageList.size() > 0){
                ll_video.setVisibility(View.GONE);
                ll_image.setVisibility(View.VISIBLE);
                if (Current_Image < ImageList.size()) {
                    Bitmap bmp = BitmapFactory.decodeFile(ImageList.get(Current_Image));
                    imgview.setImageBitmap(bmp);
                    Current_Image++;
                    handler.sendEmptyMessageDelayed(EVENT_START_VIDEO, SHOWTIME);
                } else {
                    ChangeToVideo(true);
                    handler.sendEmptyMessage(EVENT_START_VIDEO);
                }
            }else{
                ChangeToVideo(true);
                handler.sendEmptyMessage(EVENT_START_VIDEO);
            }
        }
    }

    public void ChangeToVideo(boolean isVideo){
        if(isVideo){
            Current_Image = 0;
            Current_Video = 0;
            isImagePlay = false;
            isVideoPlay = true;
        }else{
            Current_Image = 0;
            Current_Video = 0;
            isImagePlay = true;
            isVideoPlay = false;
        }
    }

    private void reBackground(){
        if(!sharedPreferencesHelper.contain(Constant.fileName)){
            ImageInit();
        }else {
            FileName = sharedPreferencesHelper.getSharedPreference(Constant.fileName, "").toString().trim();
            Constant.debugLog(FileName);
            if (!FileName.equals("")) {
                backgroundback = SaveBitmap.getInstance().getBitmapFileName(FileName);
                if(backgroundback == null){
                    ImageInit();
                }else{
                    background.setBackgroundDrawable(new BitmapDrawable(SaveBitmap.getInstance().resizeBitmap(
                            backgroundback,ScreenUtil.getScreenWidth(getApplicationContext()),ScreenUtil.getScreenHeight(getApplicationContext()))));
                }
            }
        }
    }

    public void RefreshArray(){
        SaveBitmap.getInstance().getPhotoVideoPath();
        ImageNameList = SaveBitmap.getInstance().getImageNameList();
        ImageBitmapList = SaveBitmap.getInstance().getImageBitmapList();
        mBackgroudData = new ArrayList<>();
        if(ImageNameList !=null && ImageNameList.size()>0 &&
                ImageBitmapList !=null && ImageBitmapList.size()>0
                && ImageNameList.size() == ImageBitmapList.size()) {
            GridItem item = new GridItem();
            item.setTitle(getString(R.string.add));
            item.setImage(ContextCompat.getDrawable(getApplicationContext(),R.mipmap.add_1));
            mBackgroudData.add(item);
            for (int i = 0; i < ImageNameList.size(); i++) {
                item = new GridItem();
                item.setTitle(ImageNameList.get(i));
                item.setImage(new BitmapDrawable(ImageBitmapList.get(i)));
                mBackgroudData.add(item);
            }
            gridViewAdapter_background = new GridViewAdapter(this, R.layout.gridview_item_background, mBackgroudData,true);
            gridview_background.setAdapter(gridViewAdapter_background);
        }else{
            ImageInit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            String finalName = null;
            Bitmap photoBmp = null;
            Uri mImageCaptureUri = data.getData();
            Constant.debugLog("mImageCaptureUri:"+mImageCaptureUri);
            String fileName = SaveBitmap.getInstance().getPath(this,mImageCaptureUri);
            try {

                if(fileName != null) {
                    String[] array = fileName.split("/");
                    Constant.debugLog("fileName:" + fileName);
                    if (array.length > 0) {
                        fileName = array[array.length - 1];
                        Constant.debugLog("path:" + fileName);
                        array = fileName.split("\\.");
                        if (array.length > 0) {
                            Constant.debugLog("path:" + array[array.length - 1] + " " + array[array.length - 1].length());
                            finalName = fileName.substring(0, fileName.length() - (array[array.length - 1].length() + 1));
                        }
                    }
                }else{
                    String[] array = mImageCaptureUri.toString().split("/");
                    Constant.debugLog("fileName:" + fileName);
                    if (array.length > 0) {
                        fileName = array[array.length - 1];
                        Constant.debugLog("path:" + fileName);
                        array = fileName.split("\\.");
                        if (array.length > 0) {
                            Constant.debugLog("path:" + array[array.length - 1] + " " + array[array.length - 1].length());
                            finalName = fileName.substring(0, fileName.length() - (array[array.length - 1].length() + 1));
                        }
                    }
                }
                Constant.debugLog("path:"+finalName);
                if (mImageCaptureUri != null) {
                    try {
                        photoBmp = SaveBitmap.getInstance().getBitmapFormUri(this, mImageCaptureUri);
//                    photoBmp = SaveBitmap.getInstance().getBitmapFormUri(this, Uri.fromFile(file));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if(photoBmp !=null) {
                    if(finalName== null) {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");// HH:mm:ss
                        Date date = new Date(System.currentTimeMillis());
                        SaveBitmap.getInstance().saveBitmap(getApplicationContext(), photoBmp, simpleDateFormat.format(date));
                    }else{
                        SaveBitmap.getInstance().saveBitmap(getApplicationContext(), photoBmp, finalName);
                    }
                    RefreshArray();
                }
            }catch (Exception e){
                ToastUtil.showMessage("Pictures are not available,Please confirm the path and file name. ");
            }
        }
    }

    private void ImageInit(){
        backgroundback = ((BitmapDrawable)getResources().getDrawable(R.mipmap.beijing4)).getBitmap();
        SaveBitmap.getInstance().saveBitmap(getApplicationContext(), backgroundback, Constant.Default);
        Bitmap cache = ((BitmapDrawable)getResources().getDrawable(R.mipmap.beijing3)).getBitmap();
        SaveBitmap.getInstance().saveBitmap(getApplicationContext(), cache, "dark");
        cache = ((BitmapDrawable)getResources().getDrawable(R.mipmap.beijing2)).getBitmap();
        SaveBitmap.getInstance().saveBitmap(getApplicationContext(), cache, "dawn");
        cache = ((BitmapDrawable)getResources().getDrawable(R.mipmap.beijing1)).getBitmap();
        SaveBitmap.getInstance().saveBitmap(getApplicationContext(), cache, "bridge");
        cache = ((BitmapDrawable)getResources().getDrawable(R.mipmap.beijing)).getBitmap();
        SaveBitmap.getInstance().saveBitmap(getApplicationContext(), cache, "sea");
        sharedPreferencesHelper.put(Constant.fileName,Constant.Default);
        FileName = "default";
        RefreshArray();
        reBackground();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//根据请求是否通过的返回码进行判断，然后进一步运行程序
        if (grantResults.length > 0 && requestCode == REQUEST_EXTERNAL_STRONGE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            ImageInit();
            RefreshArray();
        }
//        else{
////            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STRONGE);
//        }

    }

    private void initDb(){
        urlList = dbHelper.queryListMap("select * from url ", null);
        if(urlList ==null || urlList.size() ==0) {
            insert("Settings", "com.android.settings", 0);
            insert("Google Play", "com.android.vending", 0);
            insert("Wallpaper", "Wallpaper", 1);
            insert("Chrome", "com.android.chrome", 2);
            insert(getString(R.string.amazon), "https://www.amazon.com/", 2);
            insert(getString(R.string.bbc), "https://www.bbc.co.uk/news", 2);
            insert(getString(R.string.ccn), "https://www.cnn.com/", 2);
            insert(getString(R.string.discovery), "https://www.discovery.com/", 2);
            insert(getString(R.string.economist), "https://www.economist.com/", 2);
            insert(getString(R.string.fb), "https://www.facebook.com/", 2);
            insert(getString(R.string.google_news), "https://news.google.com/", 2);
            insert(getString(R.string.logo_ig), "https://www.instagram.com/", 2);
            insert(getString(R.string.logo_mlb), "https://www.mlb.com/", 2);
            insert(getString(R.string.lnasa), "https://www.nasa.gov/", 2);
            insert(getString(R.string.logo_nba), "https://www.nba.com/", 2);
            insert(getString(R.string.logo_qiy), "https://www.iqiyi.com/", 2);
            insert(getString(R.string.logo_spotify), "https://www.spotify.com/", 2);
            insert(getString(R.string.logo_ted), "https://www.ted.com/", 2);
            insert(getString(R.string.logo_twitter), "https://www.twitter.com/", 2);
            insert(getString(R.string.logo_weibo), "https://www.weibo.com/", 2);
            insert(getString(R.string.logo_youku), "https://www.youku.com", 2);
            insert(getString(R.string.logo_youtube), "https://www.youtube.com/", 2);
        }
    }

    public void getUrl(boolean isAdmin){
        this.isAdmin = isAdmin;
        if(isAdmin){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                admin_relative.setBackground(getDrawable(R.drawable.btn_show_on));
            }
            urlList = dbHelper.queryListMap("select * from url ", null);
            if (urlList != null && urlList.size() > 0) {
                LogUtil.e("urlList first" + urlList.size());
            } else {
                if(!isRunning) {
                    initDb();
                }
                urlList = dbHelper.queryListMap("select * from url ", null);
                LogUtil.e("urlList first" + urlList.size());
            }
        }else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                admin_relative.setBackground(getDrawable(R.drawable.btn_show));
            }
            urlList = dbHelper.queryListMap("select * from url where type = 2 and show = 0", null);
            if (urlList != null && urlList.size() > 0) {
                LogUtil.e("urlList first" + urlList.size());
            } else {
                initDb();
                urlList = dbHelper.queryListMap("select * from url where type = 2 and show = 0 ", null);
                LogUtil.e("urlList first" + urlList.size());
            }
        }
        applicationNumber = urlList.size() - 1 ;
        setUrlContent();
    }


    public void getApplicantInfo(){
        pm = getPackageManager();
        packageInfoList = new ArrayList<>();
        List<PackageInfo> packageInfos = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
        LogUtil.e("packageInfos size "+packageInfos.size());
        for(PackageInfo packageInfo : packageInfos){
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                // 系统程序
            }else{
                if(!packageInfo.applicationInfo.packageName.equals("com.youkes.browser")
                        && !packageInfo.applicationInfo.packageName.equals("com.google.android.gsf")
                        && !packageInfo.applicationInfo.packageName.equals("com.google.android.gms")
                        && !packageInfo.applicationInfo.packageName.equals("com.android.vending")
                        && !packageInfo.applicationInfo.packageName.equals("com.android.chrome")
                ){
                    packageInfoList.add(packageInfo);
                }
            }
        }
    }


    private void setUrlContent(){
        getApplicantInfo();
        if(packageInfoList != null && packageInfoList.size() > 0){
            for(PackageInfo packageInfo : packageInfoList){
                HashMap map = new HashMap();
                map.put("name",packageInfo.applicationInfo.loadLabel(pm).toString());
                map.put("packagename",packageInfo.packageName);
                urlList.add(map);
                LogUtil.e("map"+map.toString());
            }
        }
        if(urlList != null && urlList.size() > 0) {
            mGridData = new ArrayList<>();
            GridItem items = new GridItem();
            mGridData.add(items);
            for (int i = 0, size = urlList.size() - 1; i < size; i++) {
                items = new GridItem();
                items.setTitle("");
                items.setImage(getResources().getDrawable(R.mipmap.photo));
                mGridData.add(items);
            }

            if(!isRunning) {
                if(urlList != null || urlList.size() != 0) {
                    initAdapter();
                    gridView.setVisibility(View.VISIBLE);
                }else{
                    Toast.makeText(getApplicationContext(),"There is none of icon",Toast.LENGTH_SHORT).show();
                    gridView.setVisibility(View.GONE);
                }
            }else{
                if(urlList != null || urlList.size() != 0){
                    if(gridViewAdapter == null){
                        initAdapter();
                    }
                    gridViewAdapter.notifyDataSetChanged();
                    gridView.setVisibility(View.VISIBLE);
                }else{
                    Toast.makeText(getApplicationContext(),"There is none of icon",Toast.LENGTH_SHORT).show();
                    gridView.setVisibility(View.GONE);
                }
            }
        }else{
            Toast.makeText(getApplicationContext(),"There is none of icon",Toast.LENGTH_SHORT).show();
            gridView.setVisibility(View.GONE);
        }
    }

    public void initAdapter(){
        gridViewAdapter = new GridViewAdapter(this, R.layout.gridview_item_main, mGridData, urlList);
        gridView.setAdapter(gridViewAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /**获得Intent*/
                Constant.isResetPlay = true;
                if (isAdmin) {
                    if (position <= 2) {
                        switch (position) {
                            case 0:
                                packageManager = getPackageManager();
                                Intent intentSetting = packageManager.getLaunchIntentForPackage("com.android.settings"); //com.xx.xx是我们获取到的包名 
                                if (intentSetting != null) {
                                    startActivity(intentSetting);
                                    overridePendingTransition(R.anim.dialog_enter_anim, R.anim.dialog_exit_anim);
                                } else {
                                    Toast.makeText(getApplicationContext(), R.string.settings_not_exit, Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case 2:
                                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    backgroundback = ((BitmapDrawable) getResources().getDrawable(R.mipmap.beijing4)).getBitmap();
                                    background.setBackgroundDrawable(new BitmapDrawable(SaveBitmap.getInstance().resizeBitmap(
                                            backgroundback, ScreenUtil.getScreenWidth(getApplicationContext()), ScreenUtil.getScreenHeight(getApplicationContext()))));
                                    Toast.makeText(getApplicationContext(), R.string.get_file_no_permission, Toast.LENGTH_SHORT).show();
                                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STRONGE);

                                } else {
                                    RefreshArray();
                                    currentIndex = Constant.BACKGROUND;
                                    isMain = false;
                                    setAnimation(Constant.BACKGROUND, layout_content, layout_background);
                                }
                                break;
                            case 1:
                                packageManager = getPackageManager();
                                Intent intent1 = packageManager.getLaunchIntentForPackage("com.android.vending"); //com.xx.xx是我们获取到的包名 
                                if (intent1 != null) {
                                    startActivity(intent1);
                                    overridePendingTransition(R.anim.dialog_enter_anim, R.anim.dialog_exit_anim);
                                } else {
                                    Toast.makeText(getApplicationContext(), R.string.google_play_not_exit, Toast.LENGTH_SHORT).show();
                                }
                                break;
                        }
                    } else if (position <= applicationNumber) {
                        if ((int) (urlList.get(position).get("show")) == 0) {
                            dbHelper.execSQL("update url set show = 1 where id = " + urlList.get(position).get("id"));
                        } else {
                            dbHelper.execSQL("update url set show = 0 where id = " + urlList.get(position).get("id"));
                        }
                        getUrl(true);
                    } else {
                        unInstall(packageInfoList.get(position - applicationNumber - 1));
                    }
                } else {
                    if (position <= applicationNumber) {
                        if (urlList.get(position).get("url").equals("com.android.chrome")) {
                            packageManager = getPackageManager();
                            Intent intent = packageManager.getLaunchIntentForPackage("com.android.chrome"); //com.xx.xx是我们获取到的包名 
                            if (intent != null) {
                                startActivity(intent);
                                overridePendingTransition(R.anim.dialog_enter_anim, R.anim.dialog_exit_anim);
                            } else {
                                Toast.makeText(getApplicationContext(), R.string.google_play_not_exit, Toast.LENGTH_SHORT).show();
                            }
                            return;
                        } else {
                            starIntent(urlList.get(position).get("url").toString());
                        }
                    } else {
                        startActivity(packageInfoList.get(position - applicationNumber - 1).packageName);
                    }
                }
            }
        });
    }

    private void startActivity(String packageName){
        packageManager = getPackageManager();
        Intent intent1 = packageManager.getLaunchIntentForPackage(packageName); //com.xx.xx是我们获取到的包名 
        if (intent1 != null) {
            startActivity(intent1);
            overridePendingTransition(R.anim.dialog_enter_anim, R.anim.dialog_exit_anim);
        } else {
            Toast.makeText(getApplicationContext(), R.string.google_play_not_exit, Toast.LENGTH_SHORT).show();
        }
    }

    private void initData(){
        getVideoPath();
        getPhotoVideoPath();
        getUrl(false);
        gridview_background.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                isDelete = true;
                gridViewAdapter_background.notifyDataSetChanged();
                return true;
            }
        });
        gridview_background.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /**获得Intent*/
                if(!isDelete) {
                    if (position == 0) {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");//无类型限制
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        startActivityForResult(intent, 1);
                    } else {
                        Constant.debugLog(ImageNameList.get(position - 1));
                        sharedPreferencesHelper.put(Constant.fileName, ImageNameList.get(position - 1));
                        reBackground();
                    }
                }else{
                    if(ImageNameList != null && ImageNameList.size() > 0 && position != 0) {
                        if(SaveBitmap.delete(SaveBitmap.SD_PATH + ImageNameList.get(position - 1) + ".jpg")){
                            RefreshArray();
                        }
                    }
                }
            }
        });


        video_urllist = new ArrayList<>();
        video_urllist.add("https://www.iqiyi.com/");
        video_urllist.add("https://www.spotify.com/");
        video_urllist.add("https://www.youku.com/");
        video_urllist.add("https://www.youtube.com/");
        int icno_video[] = { R.mipmap.logo_qiy,
                R.mipmap.logo_spotify,R.mipmap.logo_youku, R.mipmap.logo_youtube
        };
        String name_video[]={ getString(R.string.logo_qiy),
                getString(R.string.logo_spotify),getString(R.string.logo_youku)
                ,getString(R.string.logo_youtube)
        };
        mGridData = new ArrayList<>();
        for (int i=0; i<icno_video.length; i++) {
            GridItem item = new GridItem();
            item.setTitle(name_video[i]);
            item.setImage(getResources().getDrawable(icno_video[i]));
            mGridData.add(item);
        }
        gridViewAdapter_video = new GridViewAdapter(this, R.layout.gridview_item, mGridData);
        video_girdview.setAdapter(gridViewAdapter_video);
        video_girdview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /**获得Intent*/
                starIntent(video_urllist.get(position));
            }
        });


        news_urllist = new ArrayList<>();
        news_urllist.add("https://www.bbc.co.uk/news");
        news_urllist.add("https://www.cnn.com/");
        news_urllist.add("https://news.google.com/");
        news_urllist.add("https://www.economist.com/");
        int news_icno[] = { R.mipmap.logo_bbc,
                R.mipmap.logo_cnn,R.mipmap.logo_googlenews, R.mipmap.logo_economist
        };
        String news_name[]={ getString(R.string.bbc),
                getString(R.string.ccn),getString(R.string.google_news)
                ,getString(R.string.economist)
        };
        mGridData = new ArrayList<>();
        for (int i=0; i<news_icno.length; i++) {
            GridItem item = new GridItem();
            item.setTitle(news_name[i]);
            item.setImage(getResources().getDrawable(news_icno[i]));
            mGridData.add(item);
        }
        gridViewAdapter_news = new GridViewAdapter(this, R.layout.gridview_item, mGridData);
        news_girdview.setAdapter(gridViewAdapter_news);
        news_girdview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /**获得Intent*/
                starIntent(news_urllist.get(position));
            }
        });

        contact_urllist = new ArrayList<>();
        contact_urllist.add("https://www.weibo.com/");
        contact_urllist.add("https://www.facebook.com/");
        contact_urllist.add("https://www.instagram.com/");
        contact_urllist.add("https://twitter.com/");
        int constact_icno[] = { R.mipmap.logo_weibo,
                R.mipmap.logo_fb,R.mipmap.logo_ig, R.mipmap.logo_twitter
        };
        String constact_name[]={ getString(R.string.logo_weibo),
                getString(R.string.fb),getString(R.string.logo_ig)
                ,getString(R.string.logo_twitter)
        };
        mGridData = new ArrayList<>();
        for (int i=0; i<constact_icno.length; i++) {
            GridItem item = new GridItem();
            item.setTitle(constact_name[i]);
            item.setImage(getResources().getDrawable(constact_icno[i]));
            mGridData.add(item);
        }
        gridViewAdapter_contact = new GridViewAdapter(this, R.layout.gridview_item, mGridData);
        contact_girdview.setAdapter(gridViewAdapter_contact);
        contact_girdview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /**获得Intent*/
                starIntent(contact_urllist.get(position));
            }
        });

        message_urllist = new ArrayList<>();
        message_urllist.add("https://www.amazon.com/");
        message_urllist.add("https://www.mlb.com/");
        message_urllist.add("https://www.discovery.com/");
        message_urllist.add("https://www.nasa.gov/");
        message_urllist.add("https://www.nba.com/");
        message_urllist.add("https://www.ted.com/");
        int message[] = { R.mipmap.logo_amazon,
                R.mipmap.logo_mlb,R.mipmap.logo_discovery, R.mipmap.logo_nasa,
                R.mipmap.logo_nba,R.mipmap.logo_ted
        };
        String messagename[]={ getString(R.string.amazon),
                getString(R.string.logo_mlb),getString(R.string.discovery)
                ,getString(R.string.lnasa),getString(R.string.logo_nba)
                ,getString(R.string.logo_ted)
        };
        mGridData = new ArrayList<>();
        for (int i=0; i<message.length; i++) {
            GridItem item = new GridItem();
            item.setTitle(messagename[i]);
            item.setImage(getResources().getDrawable(message[i]));
            mGridData.add(item);
        }
        gridViewAdapter_message = new GridViewAdapter(this, R.layout.gridview_item, mGridData);
        message_girdview.setAdapter(gridViewAdapter_message);
        message_girdview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /**获得Intent*/
                starIntent(message_urllist.get(position));
            }
        });

    }

    public String getVideoPath(){
        File path = new File(Constant.VideoDir);
        if (Environment.getExternalStorageState().
                equals(Environment.MEDIA_MOUNTED)) {
            File[] files = path.listFiles();// 读取文件夹下文件
            getVideoName(files);
        }
        return null;
    }

    protected void getVideoName(File[] files) {
        if(files != null){
            VideoNameList = new ArrayList<>();
            for (File file : files) {
                if (!file.isDirectory()) { // wmv gif mkv（无法播放） 3pg(无声音)  avi
                    if (file.getName().endsWith(".mp4") || file.getName().endsWith(".mov")
//                            || file.getName().endsWith(".rmvb")
//                  || file.getName().endsWith(".flv")
//                    ||file.getName().endsWith(".mpg")
//                            || file.getName().endsWith(".rmvb")
//                            ||file.getName().endsWith(".swf") || file.getName().endsWith(".vob")
                    ){
                        VideoNameList.add(Constant.VideoDir+file.getName());
                        Constant.debugLog(Constant.VideoDir+file.getName()+"");
                    }
                }
            }
        }
    }
    public String getPhotoVideoPath(){
        File path = new File(Constant.ImgDir);
        if (Environment.getExternalStorageState().
                equals(Environment.MEDIA_MOUNTED)) {
            File[] files = path.listFiles();// 读取文件夹下文件
            getPhotoName(files);
        }
        return null;
    }

    protected void getPhotoName(File[] files) {
        if(files != null){
            ImageList = new ArrayList<>();
            for (File file : files) {
                if (!file.isDirectory()) {
                    if (file.getName().endsWith(".jpg") || file.getName().endsWith(".png")){
                        ImageList.add(Constant.ImgDir+file.getName());
                        Constant.debugLog(Constant.ImgDir+file.getName()+"");
                    }
                }
            }
        }
    }

    public void insert(String Name,String url,int type){
        dbHelper.execSQL("insert into url (name,url,show,type) values " +
                "('"+Name+"','"+url+"',0,"+type+")");
    }

    @Override
    public void onClick(View v) {
//        BlurBuilder.snapShotWithoutStatusBar(this);
        Constant.isResetPlay = true;
        Constant.CurrentNumber = 0;
        switch (v.getId()){
            case R.id.message_layout:
//                myDialog=new MyDialog(MainActivity.this,R.style.MyDialog,Constant.MESSAGE,backgroundback,onClickListener);
//                myDialog.show();
                currentIndex = Constant.MESSAGE;
                isMain = false;
                setAnimation(Constant.MESSAGE,layout_cn,layout_message);
                break;
            case R.id.shipin:
                currentIndex = Constant.VIDEO;
                isMain = false;
                setAnimation(Constant.VIDEO,layout_cn,layout_video);
//                myDialog=new MyDialog(MainActivity.this,R.style.MyDialog,Constant.VIDEO, backgroundback,onClickListener);
//                myDialog.show();
                break;
            case R.id.contact:
                currentIndex = Constant.CONSTACT;
                isMain = false;
                setAnimation(Constant.CONSTACT,layout_cn,layout_contact);
//                myDialog=new MyDialog(MainActivity.this,R.style.MyDialog,Constant.CONSTACT,backgroundback,onClickListener);
//                myDialog.show();
                break;
            case R.id.settings:
                packageManager = getPackageManager();
                Intent intentSetting = packageManager.getLaunchIntentForPackage("com.android.settings"); //com.xx.xx是我们获取到的包名 
                if(intentSetting!=null){
                    startActivity(intentSetting);
                    overridePendingTransition(R.anim.dialog_enter_anim, R.anim.dialog_exit_anim);
                }else{
                    Toast.makeText(getApplicationContext(),R.string.settings_not_exit,Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.play:
                packageManager = getPackageManager();
                Intent intent = packageManager.getLaunchIntentForPackage("com.android.vending"); //com.xx.xx是我们获取到的包名 
                if(intent!=null){
                    startActivity(intent);
                    overridePendingTransition(R.anim.dialog_enter_anim, R.anim.dialog_exit_anim);
                }else{
                    Toast.makeText(getApplicationContext(),R.string.google_play_not_exit,Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.news_layout:
                currentIndex = Constant.NEW;
                isMain = false;
                setAnimation(Constant.NEW,layout_cn,layout_new);
//                myDialog=new MyDialog(MainActivity.this,R.style.MyDialog,Constant.NEW,backgroundback,onClickListener);
//                myDialog.show();
                break;
            case R.id.content:
                currentIndex = Constant.CONTENT;
                isMain = false;
                setAnimation(Constant.CONTENT,layout_cn,layout_content);
                break;
            case R.id.admin_layout:
            case R.id.admin:
//                getAPKPath(this);
                if(isAdmin){
                    getUrl(false);
                }else{
                    adminDialog();
                }
                break;
            case R.id.ll_image:
            case R.id.ll_video:
                getUrl(isAdmin);
                handler.sendEmptyMessage(EVENT_TO_THREAD);
                handler.sendEmptyMessage(EVENT_GETEVENT);
                break;
        }
    }



    private ZB_MyDialog dialog;
    private EditText editText;
    public void adminDialog(){
        // 区域Dialog
        dialog = new ZB_MyDialog(this);
        editText = (EditText) dialog.getEditText();
        // 确定Dialog
        dialog.setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().toString().trim().equals("")) {
                    Toast.makeText(getApplicationContext(), "password is empty", Toast.LENGTH_SHORT).show();
                } else {
                    if(editText.getText().toString().trim().equals(Constant.AdminPassword)){
                        Toast.makeText(getApplicationContext(), "Login Succes", Toast.LENGTH_SHORT).show();
                        getUrl(true);
                        dialog.dismiss();
                        SoftKeyboardUtil.hideSoftKeyboard(MainActivity.this);
                    }else{
                        Toast.makeText(getApplicationContext(), "input password error", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        // 取消Dialog
        dialog.setOnNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 销毁当前Dialog
                dialog.dismiss();
                SoftKeyboardUtil.hideSoftKeyboard(MainActivity.this);
            }
        });
        // 显示Dialog
        dialog.show();
    }

    public void starIntent(String url){
        intentUrl=new IntentUrl();
        intentUrl.setUrl(url);
        intent=new Intent(this,BrowserMainActivity.class);
        intent.putExtra("intentUrl",new Gson().toJson(intentUrl));
        startActivity(intent);
        overridePendingTransition(R.anim.dialog_enter_anim, R.anim.dialog_exit_anim);
    }

    @Override
    public void finish() {
        BlurBuilder.recycle();
        overridePendingTransition(R.anim.dialog_enter_anim, R.anim.dialog_exit_anim);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Constant.isResetPlay = true;
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if(!isMain) {
                    if(isDelete){
                        isDelete = false;
                        gridViewAdapter_background.notifyDataSetChanged();
                        return true;
                    }
                    isMain = true;
                    switch (currentIndex) {
                        case Constant.BACKGROUND:
                            setAnimation(Constant.VIDEO,layout_background, layout_content);
                            break;
                        case Constant.VIDEO:
                            setAnimation(Constant.VIDEO,layout_video, layout_cn);
                            break;
                        case Constant.CONTENT:
                            setAnimation(Constant.CONTENT,layout_content, layout_cn);
                            break;
                        case Constant.NEW:
                            setAnimation(Constant.NEW,layout_new, layout_cn);
                            break;
                        case Constant.MESSAGE:
                            setAnimation(Constant.MESSAGE,layout_message, layout_cn);
                            break;
                        case Constant.CONSTACT:
                            setAnimation(Constant.CONSTACT,layout_contact, layout_cn);
                            break;
                    }
                    currentIndex = -1;
                    return true;
                }
                if(isAdmin){
                    getUrl(false);
                    return true;
                }
                return false;//拦截事件
            case KeyEvent.KEYCODE_MENU:
                break;
            case KeyEvent.KEYCODE_HOME:
                // 收不到
                break;
            case KeyEvent.KEYCODE_APP_SWITCH:
                // 收不到
                break;
            default:
                break;
        }

        return super.onKeyDown(keyCode, event);
    }

    private TranslateAnimation old_TA ;
    private TranslateAnimation new_TA;
    private AnimationSet old_AS;
    private AnimationSet new_AS;
    private AlphaAnimation old_AA;
    private AlphaAnimation new_AA;
    private ScaleAnimation old_SA;
    private ScaleAnimation new_SA;
    public void setAnimation(int Number,final LinearLayout layout_old,final LinearLayout layout_new){
        if(isMain){
            background.setLongClickable(true);
        }else{
            background.setLongClickable(false);
        }
        old_AS = new AnimationSet(true);
        new_AS = new AnimationSet(true);
        old_AS.setDuration(Constant.ANIMATIONTIME);
        new_AS.setDuration(Constant.ANIMATIONTIME);
        old_AA = new AlphaAnimation(1,0);
        old_AA.setDuration(Constant.ANIMATIONTIME);
        old_AS.addAnimation(old_AA);
        new_AA = new AlphaAnimation(0,1);
        new_AA.setDuration(Constant.ANIMATIONTIME);
        new_AS.addAnimation(new_AA);

//        old_SA = new ScaleAnimation(1, 0.1f, 1, 0.1f,Animation.ABSOLUTE,1f,Animation.ABSOLUTE,1f);
//        old_SA.setDuration(Constant.ANIMATIONTIME);
//        old_AS.addAnimation(old_SA);
//
//        new_SA = new ScaleAnimation(0.1f, 1, 0.1f, 1,Animation.ABSOLUTE,1f,Animation.ABSOLUTE,1f);
//        new_SA.setDuration(Constant.ANIMATIONTIME);
//        new_AS.addAnimation(new_SA);

        if(isMain) {
            switch (Number){
                case Constant.BACKGROUND:
                case Constant.VIDEO:
                    new_TA = new TranslateAnimation(ScreenUtil.getScreenWidth(this), 0, 0, 0);
                    old_TA = new TranslateAnimation(0, ScreenUtil.getScreenWidth(this) * -1, 0, 0);
                    break;
                case Constant.NEW:
                    new_TA = new TranslateAnimation(0, 0, ScreenUtil.getScreenHeight(this) , 0);
                    old_TA = new TranslateAnimation(0, 0, 0, ScreenUtil.getScreenHeight(this)* -1);
                    break;
                case Constant.MESSAGE:
                case Constant.CONSTACT:
                    new_TA = new TranslateAnimation(0, 0, ScreenUtil.getScreenHeight(this) * -1, 0);
                    old_TA = new TranslateAnimation(0, 0, 0, ScreenUtil.getScreenHeight(this) );
                    break;
                case Constant.CONTENT:
                    new_TA = new TranslateAnimation(ScreenUtil.getScreenWidth(this) * -1, 0, 0, 0);
                    old_TA = new TranslateAnimation(0, ScreenUtil.getScreenWidth(this), 0, 0);
                    break;
            }
        }else{
            switch (Number){
                case Constant.BACKGROUND:
                case Constant.VIDEO:
                    new_TA = new TranslateAnimation(ScreenUtil.getScreenWidth(this) * -1, 0, 0, 0);
                    old_TA = new TranslateAnimation(0, ScreenUtil.getScreenWidth(this), 0, 0);
                    break;
                case Constant.NEW:
                    new_TA = new TranslateAnimation(0, 0, ScreenUtil.getScreenHeight(this) * -1, 0);
                    old_TA = new TranslateAnimation(0, 0, 0, ScreenUtil.getScreenHeight(this));
                    break;
                case Constant.MESSAGE:
                case Constant.CONSTACT:
                    new_TA = new TranslateAnimation(0, 0, ScreenUtil.getScreenHeight(this), 0);
                    old_TA = new TranslateAnimation(0, 0, 0, ScreenUtil.getScreenHeight(this) * -1);
                    break;
                case Constant.CONTENT:
                    new_TA = new TranslateAnimation(ScreenUtil.getScreenWidth(this), 0, 0, 0);
                    old_TA = new TranslateAnimation(0, ScreenUtil.getScreenWidth(this) * -1, 0, 0);
                    break;
            }
        }
        old_TA.setDuration(Constant.ANIMATIONTIME);
        old_AS.addAnimation(old_TA);
        new_TA.setDuration(Constant.ANIMATIONTIME);
        new_AS.addAnimation(new_TA);

        layout_old.startAnimation(old_AS);

        old_AS.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                layout_new.setVisibility(View.VISIBLE);
                layout_new.startAnimation(new_AS);
            }
            @SuppressLint("NewApi")
            @Override
            public void onAnimationEnd(Animation animation) {
                layout_new.setVisibility(View.VISIBLE);
                layout_old.setVisibility(View.GONE);
//                if(!isMain){
//                    if(backgroundback != null) {
//                        background.setBackgroundDrawable(new BitmapDrawable(backgroundback));
//                    }else if(SaveBitmap.getBitmapFileName("backgroundbak")!=null){
//                        background.setBackgroundDrawable(new BitmapDrawable(SaveBitmap.getBitmapFileName("backgroundbak")));
//                    }else{
//                        background.setBackgroundResource(R.mipmap.beijing4);
//                    }
//                }else{
//                    background.setBackgroundResource(R.mipmap.beijing4);
//                }
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void showProgressDialog(String titile) {
        progressDialog = new ProgressDialog(MainActivity.this) ;
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL) ; //设置为水平进度条，当然你也可以设置为圆形
        progressDialog.setCancelable(false); //通过点击back是否可以退出dialog
        progressDialog.setCanceledOnTouchOutside(false); //通过点击外边是否可以diss
        progressDialog.setIcon(R.drawable.ic_launcher_background);
        progressDialog.setTitle(titile);//设置标题
        progressDialog.setMax(100);//设置到头的数值，这里就用100结束了
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() { //你懂的的点击方法
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        progressDialog.show();  //不能忘的最后一句show ，没有他上边再怎么写也是显示不出来滴！
    }

    public void updateProgressDialog(int value)
    {
        if(progressDialog!=null) {
            progressDialog.setProgress(value);
        }
    }

    public void hideProgressDialog() {
        if(progressDialog!=null)
            progressDialog.dismiss();
    }

    public String getAPKPath(Context context){
        File path = new File(Constant.ApkDir);
        if (Environment.getExternalStorageState().
                equals(Environment.MEDIA_MOUNTED)) {
            File[] files = path.listFiles();// 读取文件夹下文件
            getApkName(files,context);
        }
        return null;
    }

    protected void getApkName(File[] files, Context context) {
        if(files != null){
            PackageManager pm = context.getPackageManager();
            for (File file : files) {
                if (!file.isDirectory()) {
                    if (file.getName().equals(Constant.ApkName) ){
                        PackageInfo pi = pm.getPackageArchiveInfo(Constant.ApkDir+Constant.ApkName, 0);
                        ApplicationInfo applicationInfo = pi.applicationInfo;
                        try {
                            getVersionName(context);
                            if(applicationInfo.packageName.equals(Constant.Package)
                                    && versionName !=null && versionCode != 0
                                    && !pi.versionName.equals(versionName)
                                    && pi.versionCode > versionCode
                            ){
                                install(Constant.ApkDir+Constant.ApkName,context);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
    private static String versionName = "";
    private static int versionCode = 0;
    public static void getVersionName(Context context) throws Exception {
        // 获取packagemanager的实例
        PackageManager packageManager = context.getPackageManager();
        // getPackageName()是你当前类的包名
        PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        versionName = packInfo.versionName;
        versionCode = packInfo.versionCode;

    }

    private void install(String filePath,Context context) {
        RootCmd.execRootCmdSilent("pm install -r "+ filePath);
    }

    private void unInstall(PackageInfo packageInfo) {
        uninstall_Name = packageInfo.applicationInfo.loadLabel(pm).toString();
        RootCmd.execRootCmdSilent("pm uninstall "+ packageInfo.applicationInfo.packageName);
    }

    public class BootReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            getUrl(isAdmin);
            if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
                String packageName = Constant.getAppName(getApplicationContext(), intent.getData().getSchemeSpecificPart());
                if (packageName != null) {
                    Toast.makeText(context, "Application " + packageName + " is Installed", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "Package " + intent.getData().getSchemeSpecificPart() + " is Installed", Toast.LENGTH_LONG).show();
                }
            }
            if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
                String packageName = intent.getData().getSchemeSpecificPart();
                if (uninstall_Name != null) {
                    Toast.makeText(context, "Application " + uninstall_Name + " Is Uninstalled", Toast.LENGTH_LONG).show();
                    uninstall_Name = null;
                } else {
                    Toast.makeText(context, "package " + packageName + " Is Uninstalled", Toast.LENGTH_LONG).show();
                }
            }
            if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {
                String packageName = Constant.getAppName(getApplicationContext(), intent.getData().getSchemeSpecificPart());
                if (packageName != null) {
                    Toast.makeText(context, "Application " + packageName + " Is Replaced", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "Package " + intent.getData().getSchemeSpecificPart() + " Is Replaced", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

}
