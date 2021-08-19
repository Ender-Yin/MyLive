package com.example.mylivetvtest;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.mylivetvtest.adapter.ChannelAdapter;
import com.example.mylivetvtest.adapter.FirstCategoryAdapter;
import com.example.mylivetvtest.keyUtil.MACUtils;
import com.example.mylivetvtest.module.CategoryItem;
import com.example.mylivetvtest.module.ModelTV;
import com.example.mylivetvtest.newWidgt.FocusRecyclerView;
import com.example.mylivetvtest.widget.TvRecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends Activity {
    public static String Cache_pwd_key = "Cache_pwd_key";
    public static String Cache_fmac_key = "Cache_fmac_key";

    //Handler Cons
    private static final int UPDATE_FOCUS = 0;
    private static final int FOCUS_ON_PLAYING = 1;
    private static final int HIDE_JIE_MU_LAN = 2;
    private static final int HIDE_CHANNEL_WINDOW = 3;
    //一级 分类
    String[] categoryList =  new String[] {"中国大陆","韩国","美国","英国","香港", "偶像","中国大陆","韩国","美国","英国","香港", "偶像",
            "中国大陆","韩国","美国","英国","香港", "偶像","中国大陆","韩国","美国","英国","香港", "偶像"};
    List<CategoryItem> categoryItemList = new LinkedList<>();
    FocusRecyclerView firstRecyclerView;
    FirstCategoryAdapter firstCategoryAdapter;
    LinearLayoutManager firstLinerLayoutManager;

    //二级 频道
    //List<ChannelItem> currentChannelList = new LinkedList<>() ;
    FocusRecyclerView channelRecyclerView;
    ChannelAdapter channelAdapter;
    LinearLayoutManager channelLinearLayoutManager;

    String currentFocusCategory = "";        //当前选择哪个一级分类

    //UI
    TextView textView_head_category;
    TextView textView_currentIndex;
    TextView textView_totalCount;
    RelativeLayout jieMuLan;
    VideoView videoView;
    String url2 = "http://cctvalih5ca.v.myalicdn.com/live/cctv1_2/index.m3u8";
    String url1 = "http://ivi.bupt.edu.cn/hls/cctv6hd.m3u8";
    String SAMPLE_URL = "https://vfx.mtime.cn/Video/2019/03/21/mp4/190321153853126488.mp4";

    RelativeLayout menu_option;
    LinearLayout window_info;
    TextView textView_window_index;
    TextView textView_window_dname;
    Button btn_hard;
    Button btn_soft;

    ImageView playingImage;
    RelativeLayout channelRelativeLayout = null;

    //状态变量 合集
    boolean firstFromCateToChannel = false;
    boolean isJieMuVisi = true;
    boolean isMenuOptVisi = false;

    int mLastFocusPositionCategory = 0;
    int mLastFocusPositionChannel = 0;
    int countChangeFocusPos = 0;
    int mFinalFocusLastPosCate = 0;
    int mFinalFocusLastPosChan = 0;

    int currentPlayingCategory = 0;
    int currentPlayingChannel = 0;
    boolean hasChangeChannelForJumpFocus = false;
    int lastPlayingCategory = 0;
    int lastPlayingChannel = 0;

    // tv 直播list
    private List<ModelTV> allProgramList=new ArrayList<ModelTV>();      //一级菜单数据列表
    List<ModelTV.ListItem> focusChannelList = new LinkedList<>() ;      //最终二级频道列表
    List<ModelTV.ListItem> tempFocusChannelList = new LinkedList<>() ;      //临时二级频道列表
    // tv直播 URL 参数
    OkHttpClient okHttpClient;
    String strGet = "zlive://192.99.67.80:6678/6d203d3bfbf5038500276a9ea70fda4d";
    int port = 6677;
    String uuid = "";
    String server = "192.99.67.80:6678";
    String MAC = "";
    String urlForRegister = "http://127.0.0.1:" + port + "/stream/open?uuid=" + uuid;
    String urlForLive     = "http://127.0.0.1:" + port + "/stream/live?uuid=" + uuid + "&server=192.99.67.80:6678&group=1&mac=" + MAC;
    String urlForClose    = "http://127.0.0.1:" + port + "/stream/close?uuid=" + uuid;

    private final Handler mHandlerFocusFirst = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_FOCUS:
                    focusOnFirstCategoryItem();
                    //mHandlerUpdateTime.sendEmptyMessageDelayed(UPDATE_TIME, 500);     //不断往mHandler 发送更新时间的信息
                    return true;
                case FOCUS_ON_PLAYING:
                    focusOnPlaying();
                    return true;
            }
            return  false;
        }
    }) ;
    private final Handler mHandlerHideOrShow = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case HIDE_JIE_MU_LAN:
                    hideJiemulan();
                    return true;
                case HIDE_CHANNEL_WINDOW:
                    hideChannelWindow();
                    return true;
            }
            return  false;
        }
    }) ;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();
        loadFirstCategories();
        //showAndHideJiemulan();

        findViewById(R.id.jiemulan).getBackground().setAlpha(200);          //节目栏设置为透明 只一点
        findViewById(R.id.channel_window_info).getBackground().setAlpha(200);          //节目栏设置为透明 只一点
        //menu_option.getBackground().setAlpha(200);          //节目栏设置为透明 只一点
        menu_option.setVisibility(View.GONE);
        mHandlerFocusFirst.sendEmptyMessageDelayed(UPDATE_FOCUS, 1000);     //打开app一秒后聚焦 第一个
        firstRecyclerView.requestFocus();

        //showAndHideJiemulan();
    }
    void focusOnFirstCategoryItem(){
        Objects.requireNonNull(firstLinerLayoutManager.findViewByPosition(0)).requestFocus();
        showPlayingImage();         //默认第一个播放图标
        Thread pressRight = new Thread() {      //调用时该父方法时 都要创建。 因为在再start 主Thread只能一次，
            public void run() {
                try {
                    Instrumentation inst = new Instrumentation();
                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_RIGHT);        //press right button
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Handler().postDelayed(new Runnable() {
            @Override public void run() {
                pressRight.start();         //模拟点击右键 否则进入界面直接隐藏节目栏 按菜单键无法显示中心菜单。
            }
        },50);
    }
    void focusOnPlaying(){
        View view = channelLinearLayoutManager.findViewByPosition(currentPlayingChannel);
        view.requestFocus();
    }
    void showAndHideJiemulan(){
        mHandlerHideOrShow.removeMessages(HIDE_JIE_MU_LAN);
        jieMuLan.setVisibility(View.VISIBLE);
        if(isJieMuVisi){ mHandlerHideOrShow.sendEmptyMessageDelayed(HIDE_JIE_MU_LAN,6000); }
    }

    @SuppressLint("ResourceType")
    void initialize(){
        JnaCore.INSTANCE.OnLiveStart(port);
        okHttpClient = new OkHttpClient();

        allProgramList = MyApplication.TvListCache;

        menu_option = findViewById(R.id.menu_option_center);
        menu_option.setVisibility(View.INVISIBLE);
        window_info = findViewById(R.id.channel_window_info);
        textView_window_index = findViewById(R.id.channel_window_info_index);
        textView_window_dname = findViewById(R.id.channel_window_info_name);
        btn_hard = findViewById(R.id.btn_hard);
        btn_soft = findViewById(R.id.btn_soft);

        jieMuLan = findViewById(R.id.jiemulan);

        textView_head_category = findViewById(R.id.textView_head_category);
        textView_currentIndex = findViewById(R.id.textView_current_index);
        textView_totalCount = findViewById(R.id.textView_total_count);
        textView_totalCount.setText("20");

        firstRecyclerView = findViewById(R.id.first_recyclerView);
        firstLinerLayoutManager = new LinearLayoutManager(this);
        firstLinerLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        firstRecyclerView.setLayoutManager(firstLinerLayoutManager);

        channelRecyclerView = findViewById(R.id.channel_recyclerView);
        channelLinearLayoutManager = new LinearLayoutManager(this);
        channelLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        channelRecyclerView.setLayoutManager(channelLinearLayoutManager);

        channelRecyclerView.setItemViewCacheSize(50);
        ((DefaultItemAnimator)channelRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);        //设置 改变item数据的 不产生闪烁动画
        ((DefaultItemAnimator)firstRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);        //设置 改变item数据的 不产生闪烁动画

        //默认播放cctv1
        videoView = findViewById(R.id.video_view);
        videoView.setVideoURI(Uri.parse(url2));
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoView.start();
            }
        });

        //初始化categoryItem
        for(int i = 0; i < allProgramList.size(); i++) {
            ModelTV modelTV = allProgramList.get(i);
            categoryItemList.add(new CategoryItem(modelTV.getClassify()));
        }

        focusChannelList = allProgramList.get(0).getList();
        channelAdapter = new ChannelAdapter(this, focusChannelList);
        channelRecyclerView.setAdapter(channelAdapter);

        updateAndShowChannelWindow();
        Log.e("focusChannelList",focusChannelList.get(currentPlayingChannel).isPlaying()? "yes":"no");
        MACUtils.initMac(this);
        MAC = MACUtils.getMac();
    }
    @SuppressLint("ResourceType")
    public void setPlayingState(int position){
        ChannelAdapter.ViewHolder viewHolder = (ChannelAdapter.ViewHolder)
                channelRecyclerView.findViewHolderForAdapterPosition(position);
    }

    void loadFirstCategories(){
        //放入第一级分类
        firstCategoryAdapter = new FirstCategoryAdapter(this, categoryItemList);
        firstRecyclerView.setAdapter(firstCategoryAdapter);

        //-------------------CategoryRecyclerView父视图失去焦点 和 获得焦点 事件监听------------------
        firstRecyclerView.setGainFocusListener((child, focus) -> {
            if(isJieMuVisi) {        // 界面显示时， 聚焦Category才可以设为不透明/原初背景
                Log.i("category列表", "category获取焦点 变不透明");
                firstCategoryAdapter.notifyItemChanged(firstRecyclerView.getmLastFocusPosition(),1);        //设置上此聚焦category为正常
            }
        });
        firstRecyclerView.setFocusLostListener((lastFocusChild, direction) -> {         //这里是只有当按键往左右时 焦点移出时 才会调用。其他方式失去焦点不会触发。
            firstFromCateToChannel = true;     //移出category列表时 设为category获得焦点时不改变刷新adapter
            if(isJieMuVisi) {
                Log.i("category列表", "category失去焦点后，设置adapter不能刷新， 变透明");
                firstCategoryAdapter.notifyItemChanged(firstRecyclerView.getmLastFocusPosition(), 0);
            }
        });
        //--------------------重写FirstCategoryAdapter的 自定义焦点改变回调方法-------------------
        firstCategoryAdapter.setOnItemFocusChangeListener(new FirstCategoryAdapter.OnRecyclerViewItemFocusChangeListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onMyFocusChange(View view, int position,Context context) {
                if(jieMuLan.getVisibility() == View.VISIBLE){
                    showAndHideJiemulan();
                    Log.e("节目栏显示/按键","切换了焦点");
                }
                if (view.hasFocus() && !firstFromCateToChannel ) {       // 聚焦一级列表 且 返回一级列表之后， 执行方法.
                    textView_currentIndex.setText("0");                 //设置当前初始index

                    currentFocusCategory = categoryList[position];
                    Toast.makeText(getApplicationContext(), "你的焦点 改到了第" + currentFocusCategory, Toast.LENGTH_SHORT).show();

                    if(isJieMuVisi) {        // 显示界面后才可以加载/切换/显示 channels
                        mLastFocusPositionCategory = firstRecyclerView.getmLastFocusPosition();      //设置上次聚焦视图
                        channelRecyclerView.setmLastFocusPosition(0);       // 每次改变categories时初始化记忆焦点 即初始聚焦第一个
                        initData(position);
                        loadChannels();
                        Log.i("Category Item聚焦","刷新adapter");
                        hasChangeChannelForJumpFocus = true;
                    }
                }
                if (view.hasFocus()){
                    firstFromCateToChannel = false;     //获得焦点后， 可以在切换一级列表焦点时 刷新adapter
                    Log.i("Category Item聚焦","cate获焦，可以刷新channel列表");

                    //当前聚焦category为 播放中category时， 使得当前播放channel获得播放图标。
                    if(position == currentPlayingCategory){
                        focusChannelList.get(currentPlayingChannel).setPlaying(true);     //此刻播放的 设置播放图标
                        channelAdapter.notifyItemChanged(currentPlayingChannel);

                        channelRecyclerView.smoothScrollToPosition(currentPlayingChannel);      //滑动到播放中channel
                        channelRecyclerView.setmLastFocusPosition(currentPlayingChannel);       //设置焦点记忆为 正在播放channel
                    }
                }else if(!view.hasFocus()){
                    Log.i("Category Item聚焦","cate失去焦点");
                    if (firstRecyclerView.isComputingLayout()) {
                        firstRecyclerView.post(new Runnable() {
                            @Override
                            public void run() {
                                firstCategoryAdapter.notifyItemChanged(firstRecyclerView.getmLastFocusPosition(),1);
                            }
                        });
                    } else {
                        firstCategoryAdapter.notifyItemChanged(firstRecyclerView.getmLastFocusPosition(),1);
                    }
                    //firstCategoryAdapter.notifyItemChanged(firstRecyclerView.getmLastFocusPosition(),1);    //设置正常背景
                }
            }
        });

        //-------------------CategoryRecyclerView父视图失去焦点 和 获得焦点 事件监听------------------
        channelRecyclerView.setGainFocusListener(new TvRecyclerView.FocusGainListener() {
            @Override
            public void onFocusGain(View child, View focued) {
                //  isEnterChannelList = 2;      //进去过
                // not the right thing to report and take the good right
            }
        });
        channelRecyclerView.setFocusLostListener(new TvRecyclerView.FocusLostListener() {
            @Override
            public void onFocusLost(View lastFocusChild, int direction) {

            }
        });

        View mFocused = channelRecyclerView.findFocus();
    }

    void initData(int position){
        focusChannelList = allProgramList.get(position).getList();      //设置为当前
    }
    @SuppressLint({"SetTextI18n", "ResourceType"})
    void loadChannels(){
        channelAdapter = new ChannelAdapter(this, focusChannelList);
        channelRecyclerView.setAdapter(channelAdapter);

        textView_head_category.setText(currentFocusCategory);
        textView_totalCount.setText("" + focusChannelList.size());

        //--------------------Channel Adapter的Item 焦点变化监听--------------------------
        channelAdapter.setOnItemFocusChangeListener((view, position, mContext) -> {
            if(jieMuLan.getVisibility() == View.VISIBLE){
                showAndHideJiemulan();
                Log.e("节目栏显示/按键","切换了焦点");
            }
            if (view.hasFocus()) {
                mLastFocusPositionChannel = channelRecyclerView.getmLastFocusPosition();     //设为当前 channel聚焦位置

                int currentPos = position + 1;
                textView_currentIndex.setText("" + currentPos);         //设置当前 index

                countChangeFocusPos = 0;         //焦点改变后可以 重新赋值最新位置

                //设为正常背景
                ChannelAdapter.ViewHolder viewHolder = (ChannelAdapter.ViewHolder)
                        channelRecyclerView.findViewHolderForAdapterPosition(channelRecyclerView.getmLastFocusPosition());
                viewHolder.itemView.setBackgroundResource(R.drawable.channel_bg);


            } else if (!view.hasFocus()) {         //焦点失去时执行
            }
        });
        channelAdapter.setmOnVideoClickListener(new ChannelAdapter.OnVideosClickListener() {
            @Override
            public void onMyClick(View view, int position) {
                //记录当前播放的channel和category
                currentPlayingChannel = channelRecyclerView.getmLastFocusPosition();        // 这里 和 position 的值是一样的
                currentPlayingCategory = firstRecyclerView.getmLastFocusPosition();
                updateAndShowChannelWindow();
                showPlayingImage();

                registerAndPlay();
                //更新
                lastPlayingChannel = currentPlayingChannel;
                lastPlayingCategory = currentPlayingCategory;
            }
        });
    }

    /**
     *播放图标设置。并刷新对应item
     */
    public void showPlayingImage(){
        //设置channel的
        focusChannelList.get(lastPlayingChannel).setPlaying(false);       //上个点击的 取消播放图标
        focusChannelList.get(currentPlayingChannel).setPlaying(true);     //此刻点击的 设置播放图标
        //channelAdapter.setData(currentChannelList);                       //不用重新绑定数据
        channelAdapter.notifyItemChanged(currentPlayingChannel);
        channelAdapter.notifyItemChanged(lastPlayingChannel);

        //设置category list的
        categoryItemList.get(lastPlayingCategory).setPlaying(false);
        categoryItemList.get(currentPlayingCategory).setPlaying(true);
        firstCategoryAdapter.notifyItemChanged(currentPlayingCategory);
        firstCategoryAdapter.notifyItemChanged(lastPlayingCategory);

        //ViewGroup view = (ViewGroup) channelLinearLayoutManager.findViewByPosition(currentPlayingChannel);
        //requestAndClickRightButtonSimulate();
        Log.i("channel点击","当前点击/播放：" + currentPlayingChannel );
        Log.i("channel点击","上次点击/播放：" + lastPlayingChannel );
    }
    public void stopAndPlay(String url){
        videoView.pause();
        videoView.stopPlayback();
        videoView.setVideoURI(Uri.parse(url));
        Log.e("改变播放源","切换视频" );
        videoView.start();
    }
    public void registerAndPlay(){
        //----关闭上个频道----
        if(!uuid.equals("")){
            Request request2 = new Request.Builder()
                    .url(urlForClose)
                    .get()     //参数放在body体里
                    .build();
            Call call2 = okHttpClient.newCall(request2);
            call2.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d("关闭okhttp: ", "连接失败"+e.getMessage());
                }
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.e("关闭okhttp", "成功访问 / 关闭频道");
                }
            });
        }
        //显示列表 自己选择要播放的频道 点击切换频道
        ModelTV.ListItem channelItem = focusChannelList.get(currentPlayingChannel + 1);     //获得下一个频道 实例信息
        List<String> urlList = channelItem.getUrlList();
        String url = urlList.get(0);
        uuid = url.substring(26);
        urlForRegister = "http://127.0.0.1:" + port + "/stream/open?uuid=" + uuid;
        urlForLive     = "http://127.0.0.1:" + port + "/stream/live?uuid=" + uuid + "&server=192.99.67.80:6678&group=1&mac=" + MAC;
        Log.e("注册地址: ", urlForRegister);
        Log.e("播放地址: ", urlForLive);
        //------注册频道------
        Request request1 = new Request.Builder()
                .url(urlForRegister)
                .get()     //参数放在body体里
                .build();
        Call call1 = okHttpClient.newCall(request1);
        call1.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("okhttp: ", "连接失败"+e.getMessage());
            }
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e("注册okhttp: ", response.toString());
                Log.e("注册okhttp: ", "连接成功 / 注册成功");
                MainActivity.this.runOnUiThread(new Runnable() {        //在主线程中使用！！！
                    public void run() {
                        stopAndPlay(urlForLive);
                    }
                });
            }
        });

        urlForClose    = "http://127.0.0.1:" + port + "/stream/close?uuid=" + uuid;         //更新应该关闭的频道
    }

    public void simulateRequestAndClickRightButton(){
        Thread pressRight = new Thread() {
            public void run() {
                try {
                    Instrumentation inst = new Instrumentation();
                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_RIGHT);        //press right button
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        //滚动到播放中category  并选中其
        firstRecyclerView.smoothScrollToPosition(currentPlayingCategory);
        new Handler().postDelayed(new Runnable() {
                @Override public void run() {
                    //0.05秒后聚焦正在播放的category
                    if (hasChangeChannelForJumpFocus){
                        firstFromCateToChannel = false;         //使得节目栏显示 聚焦播放中category时 能够切换channel列表
                        firstLinerLayoutManager.findViewByPosition(currentPlayingCategory).requestFocus();
                        pressRight.start();
                    }else if(!hasChangeChannelForJumpFocus){
                        channelLinearLayoutManager.findViewByPosition(currentPlayingChannel).requestFocus();
                    }
                    //firstLinerLayoutManager.findViewByPosition(currentPlayingCategory).requestFocus();
                    //pressRight.start();
                    //firstLinerLayoutManager.findViewByPosition(currentPlayingCategory).requestFocus();
                    //channelLinearLayoutManager.findViewByPosition(currentPlayingChannel).requestFocus();        //直接聚焦channel
                    hasChangeChannelForJumpFocus = false;
                }
            },50);
        firstLinerLayoutManager.scrollToPosition(currentPlayingCategory);

        //滚动到播放中channel  模拟右击
        channelRecyclerView.smoothScrollToPosition(currentPlayingChannel);
        channelLinearLayoutManager.scrollToPosition(currentPlayingChannel);
        channelRecyclerView.setmLastFocusPosition(currentPlayingChannel);       //设置焦点记忆为 播放中channel
    }

    public void showJiemulan(){                         //仅使得 显示节目栏时使用
        jieMuLan.setVisibility(View.VISIBLE);           //开始寻找焦点

        //------------------------------界面显示了-----------------------------------------
        isJieMuVisi = true;                  //界面显示。 category聚焦后可以切换adapter
        simulateRequestAndClickRightButton();
        Log.i("节目栏： ","按中心键节目栏显示");

        showAndHideJiemulan();
    }
    public void hideJiemulan(){
        if(isJieMuVisi) {
            if (countChangeFocusPos == 0) {       //只执行一次
                mFinalFocusLastPosCate = mLastFocusPositionCategory;
                mFinalFocusLastPosChan = mLastFocusPositionChannel;
                countChangeFocusPos += 1;
            }
            isJieMuVisi = false;     //界面消失了 显示时再次聚焦时 不刷新adapter

            mHandlerHideOrShow.removeMessages(HIDE_JIE_MU_LAN);
            jieMuLan.setVisibility(View.INVISIBLE);     // 所有东西失去焦点
            Log.e("节目栏： ", "节目栏消失");
        }
    }

    /**
     * 频道窗口 实时改变index和name
     */
    @SuppressLint("SetTextI18n")
    public void updateAndShowChannelWindow(){
        window_info.setVisibility(View.VISIBLE);
        textView_window_index.setText("" + (currentPlayingChannel+1));         //小窗设置当前分类下的 index
        textView_window_dname.setText("" + focusChannelList.get(currentPlayingChannel).getDname());

        mHandlerHideOrShow.removeMessages(HIDE_CHANNEL_WINDOW);
        if(window_info.getVisibility() == View.VISIBLE){ mHandlerHideOrShow.sendEmptyMessageDelayed(HIDE_CHANNEL_WINDOW,6000); }    //6秒后隐藏
    }
    public void hideChannelWindow(){
        window_info.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        switch (event.getKeyCode()){
            case KeyEvent.KEYCODE_DPAD_DOWN:        //往小的频道切换
                if(isJieMuVisi){showAndHideJiemulan();
                    Log.e("节目栏显示/按键"," 点下了下按键");}
                if(!isJieMuVisi) {       //界面隐藏时执行
                    Log.e("节目栏隐藏/按键"," 点下了下按键");
                    if (lastPlayingChannel == 0){
                        Toast.makeText(this, "到底了" , Toast.LENGTH_SHORT).show();
                    }
                    if (0 < lastPlayingChannel && lastPlayingChannel <= focusChannelList.size() - 1) {    //到达第一个 ， 不执行
                        //
                        registerAndPlay();

                        //改变图片
                        currentPlayingChannel = currentPlayingChannel - 1;                    //向上移动
                        updateAndShowChannelWindow();
                        showPlayingImage();
                        lastPlayingChannel = currentPlayingChannel;
                    }
                    return  true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_UP:        //往大的频道切换
                if(isJieMuVisi){showAndHideJiemulan();
                    Log.e("节目栏显示/按键"," 点下了上按键");}
                if(!isJieMuVisi) {       //节目栏隐藏时执行
                    Log.e("节目栏隐藏/按键"," 点下了上按键");
                    if (lastPlayingChannel == focusChannelList.size() - 1){
                        Toast.makeText(this, "到底了" , Toast.LENGTH_SHORT).show();
                    }
                    if (0 <= lastPlayingChannel && lastPlayingChannel < focusChannelList.size() - 1) {    //最后一个时， 不能再上了
                        //
                        registerAndPlay();

                        //改变图片
                        currentPlayingChannel = currentPlayingChannel + 1;                    //向下移动
                        updateAndShowChannelWindow();
                        showPlayingImage();
                        lastPlayingChannel = currentPlayingChannel;
                    }
                    return  true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if(isJieMuVisi){showAndHideJiemulan();
                    Log.e("节目栏显示/按键"," 点下了右按键");}
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if(isJieMuVisi){showAndHideJiemulan();
                    Log.e("节目栏显示/按键"," 点下了左按键");}
                break;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getKeyCode()){
            case KeyEvent.KEYCODE_DPAD_CENTER:
                Log.e("节目栏显示/按键"," 点下中心按键");
                if(!isJieMuVisi && !isMenuOptVisi) {      //节目栏隐藏时 就显示
                    showJiemulan();
                    return true;        //消耗了
                }
                break;
            case KeyEvent.KEYCODE_BACK:
                if(isJieMuVisi) {       //节目栏显示时 就隐藏
                    hideJiemulan();
                    return true;
                }
                if(menu_option.getVisibility()== View.VISIBLE){
                    menu_option.setVisibility(View.GONE);
                    isMenuOptVisi = false;
                    Log.e("菜单显示/按键"," 隐藏菜单");
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_MENU:
                if(!isJieMuVisi){     //当节目栏不可见时 才显示menu
                    menu_option.setVisibility(View.VISIBLE);
                    isMenuOptVisi = true;
                    Log.e("节目栏显示/按键"," 点下了菜单按键");
                    btn_hard.requestFocus();

                    return true;
                }
                break;
        }

        return super.dispatchKeyEvent(event);
    }
}