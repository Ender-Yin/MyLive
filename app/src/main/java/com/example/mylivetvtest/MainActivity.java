package com.example.mylivetvtest;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import com.example.mylivetvtest.adapter.ChannelAdapter;
import com.example.mylivetvtest.adapter.FirstCategoryAdapter;
import com.example.mylivetvtest.keyUtil.ExitUtil;
import com.example.mylivetvtest.keyUtil.MACUtils;
import com.example.mylivetvtest.module.CategoryItem;
import com.example.mylivetvtest.module.CollectorDBOpenHelper;
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
import xyz.doikki.videoplayer.ijk.IjkPlayerFactory;
import xyz.doikki.videoplayer.player.AndroidMediaPlayerFactory;
import xyz.doikki.videoplayer.player.VideoView;
import xyz.doikki.videoplayer.util.L;

public class MainActivity extends Activity {
    public static String Cache_pwd_key = "Cache_pwd_key";
    public static String Cache_fmac_key = "Cache_fmac_key";

    //Handler Cons
    private static final int UPDATE_FOCUS = 0;
    private static final int FOCUS_ON_PLAYING = 1;
    private static final int HIDE_JIE_MU_LAN = 2;
    private static final int HIDE_CHANNEL_WINDOW = 3;
    private static final int Hide_Switch_Program_Window = 4;
    private static final int UPDATE_TCP_SPEED = 5;
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
    RelativeLayout window_number_switch_channel;
    TextView textview_switchTv;
    TextView textView_head_category;
    TextView textView_currentIndex;
    TextView textView_totalCount;
    RelativeLayout jieMuLan;
    xyz.doikki.videoplayer.player.VideoView videoView;
    String url2 = "http://cctvalih5ca.v.myalicdn.com/live/cctv1_2/index.m3u8";
    String url1 = "http://ivi.bupt.edu.cn/hls/cctv6hd.m3u8";
    String SAMPLE_URL = "https://vfx.mtime.cn/Video/2019/03/21/mp4/190321153853126488.mp4";

    RelativeLayout container_menu_option;
    LinearLayout window_menu_option;
    LinearLayout window_channel_info;
    TextView textView_window_index;
    TextView textView_window_dname;
    TextView btn_hard;
    TextView btn_soft;

    LinearLayout window_speed;
    TextView textView_speed;

    ImageView playingImage;
    RelativeLayout channelRelativeLayout = null;

    //状态变量 合集
    boolean firstFromCateToChannel = false;
    boolean isJieMuVisi = true;
    boolean isMenuOptVisi = false;
    boolean isCollectorListVisi = false;

    int mLastFocusPositionCategory = 0;
    int mLastFocusPositionChannel = 0;
    int countChangeFocusPos = 0;
    int mFinalFocusLastPosCate = 0;
    int mFinalFocusLastPosChan = 0;
    int currentFocusCategoryIndex = 0;
    int currentFocusChannelIndex = 0;


    int currentPlayingCategoryIndex = 0;
    int currentPlayingChannelIndex = 0;
    boolean hasChangeChannelForJumpFocus = false;
    int lastPlayingCategoryIndex = 0;
    int lastPlayingChannelIndex = 0;

    // tv 直播list
    private List<ModelTV> allProgramList=new ArrayList<ModelTV>();      //一级菜单数据列表
    List<ModelTV.ListItem> focusChannelList = new LinkedList<>() ;      //最终二级频道列表    即当前聚焦显示的列表
    List<ModelTV.ListItem> lastPlayingChannelList = new LinkedList<>() ;      //临时二级频道列表
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
    //数字切换频道相关
    String strSwitchTv = "";
    boolean isNumberSwitch = false;     //是否按下数字键切换频道
    boolean doAscendOrder = true;

    SharedPreferences sharedPreferences;       //本地偏好储存

    //收藏频道相关
    RelativeLayout container_collector;
    FocusRecyclerView collect_recyclerview;
    ChannelAdapter collectChannelAdapter;
    LinearLayoutManager collectLinearLayoutManager;
    List<ModelTV.ListItem> collectorChannelList = new LinkedList<>() ;      //收藏的频道列表 打开app时加载
    CollectorDBOpenHelper collectorDBOpenHelper;
    private SQLiteDatabase db;

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
                case Hide_Switch_Program_Window:
                    hideSwitchProgramWindow();
                    return true;
                case UPDATE_TCP_SPEED:
                    updateTcpSpeed();
                    mHandlerHideOrShow.sendEmptyMessageDelayed(UPDATE_TCP_SPEED,500);
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
        allProgramList = MyApplication.TvListCache;     //最开始 获得节目列表
        ExitUtil.getInstance().addActivity(this);
        sharedPreferences = this.getSharedPreferences("lastInfo",MODE_PRIVATE);   //创建本地偏好 文件名为 "lastInfo"

        doAscendOrder();
        initialize();
        setStateListener();
        loadCollectorList();
        loadFirstCategories();

        findViewById(R.id.jiemulan).getBackground().setAlpha(200);          //节目栏设置为透明 只一点
        findViewById(R.id.window_channel_info).getBackground().setAlpha(200);          //节目栏设置为透明 只一点
        window_number_switch_channel.getBackground().setAlpha(200);
        window_menu_option.getBackground().setAlpha(200);          //设置菜单为透明 只一点
        container_collector.getBackground().setAlpha(200);          //设置菜单为透明 只一点
        container_menu_option.setVisibility(View.INVISIBLE);
        window_number_switch_channel.setVisibility(View.INVISIBLE);
        mHandlerFocusFirst.sendEmptyMessageDelayed(UPDATE_FOCUS, 1000);     //打开app一秒后聚焦 第一个
        //firstRecyclerView.requestFocus();

        //showAndHideJiemulan();
    }
    @Override
    protected void onStop() {
        saveLastChannel();
        super.onStop();
    }
    void focusOnFirstCategoryItem(){
        firstRecyclerView.scrollToPosition(currentPlayingCategoryIndex);
        //showAndUpdatePlayingImage();         //默认第一个播放图标
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
                Objects.requireNonNull(firstLinerLayoutManager.findViewByPosition(currentPlayingCategoryIndex)).requestFocus();
                pressRight.start();         //模拟点击右键 否则进入界面直接隐藏节目栏 按菜单键无法显示中心菜单。
            }
        },50);
    }
    void focusOnPlaying(){
        View view = channelLinearLayoutManager.findViewByPosition(currentPlayingChannelIndex);
        view.requestFocus();
    }
    void showAndHideJiemulan(){
        mHandlerHideOrShow.removeMessages(HIDE_JIE_MU_LAN);
        jieMuLan.setVisibility(View.VISIBLE);
        if(isJieMuVisi){ mHandlerHideOrShow.sendEmptyMessageDelayed(HIDE_JIE_MU_LAN,6000); }
    }

    void doAscendOrder(){
        //遍历节目表 使得各频道order升序
        if(doAscendOrder) {     //是否做升序处理
            int count = 1;
            ModelTV modelTvTemp;
            List<ModelTV.ListItem> channelListTemp = new LinkedList<>();      //检索到的二级频道列表
            ModelTV.ListItem channelItemTemp;
            for (int i = 0; i < allProgramList.size(); i++) {
                modelTvTemp = allProgramList.get(i);
                channelListTemp = modelTvTemp.getList();
                for (int j = 0; j < channelListTemp.size(); j++) {
                    channelItemTemp = channelListTemp.get(j);
                    channelItemTemp.setOrder(String.valueOf(count));
                    count++;    //计数
                }
            }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("ResourceType")
    void initialize(){

        JnaCore.INSTANCE.OnLiveStart(port);
        okHttpClient = new OkHttpClient();

        window_number_switch_channel = findViewById(R.id.window_number_switch_channel);
        textview_switchTv = findViewById(R.id.textview_window_switch);
        //switchTv.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);

        //menu_options / window_channel相关
        container_menu_option = findViewById(R.id.container_menu_option);
        window_menu_option = findViewById(R.id.window_menu_option);
        btn_hard = findViewById(R.id.btn_hard);
        btn_soft = findViewById(R.id.btn_soft);
        btn_soft.setTextColor(this.getResources().getColor(android.R.color.holo_orange_light));
        window_channel_info = findViewById(R.id.window_channel_info);
        textView_window_index = findViewById(R.id.channel_window_info_index);
        textView_window_dname = findViewById(R.id.channel_window_info_name);

        //中间tcp speed/loading窗口
        window_speed = findViewById(R.id.window_speed);
        textView_speed = findViewById(R.id.textview_tcpSpeed);

        jieMuLan = findViewById(R.id.jiemulan);

        //节目栏 频道栏 头标题
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

        //默认播放cctv1 播放器相关
        videoView = findViewById(R.id.video_view);
        videoView.setOnStateChangeListener(mOnStateChangeListener);     //设置状态监听
        videoView.setPlayerFactory(IjkPlayerFactory.create());

        //初始化categoryItems
        for(int i = 0; i < allProgramList.size(); i++) {
            ModelTV modelTV = allProgramList.get(i);
            categoryItemList.add(new CategoryItem(modelTV.getClassify()));
        }
        focusChannelList = allProgramList.get(currentPlayingCategoryIndex).getList();       //初始化频道列表
        lastPlayingChannelList = allProgramList.get(lastPlayingCategoryIndex).getList();
        channelAdapter = new ChannelAdapter(this, focusChannelList);
        channelRecyclerView.setAdapter(channelAdapter);

        //Log.e("focusChannelList",focusChannelList.get(currentPlayingChannelIndex).isPlaying()? "yes":"no");
        MACUtils.initMac(this);
        MAC = MACUtils.getMac();

        //收藏频道相关
        collect_recyclerview = findViewById(R.id.collect_recyclerview);
        collectLinearLayoutManager = new LinearLayoutManager(this);
        collectLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        collect_recyclerview.setLayoutManager(collectLinearLayoutManager);

        container_collector = findViewById(R.id.container_collector);
        collectorDBOpenHelper = new CollectorDBOpenHelper(this);
        db =  collectorDBOpenHelper.getWritableDatabase();

        //db.execSQL("DROP TABLE IF EXISTS "+ "collectlist");
    }
    void setStateListener(){
        btn_hard.setOnClickListener(v -> {
            videoView.pause();
            videoView.release();
            videoView.setPlayerFactory(AndroidMediaPlayerFactory.create());     //切换为 自带MediaPlayer
            videoView.start();
            btn_hard.setTextColor(this.getResources().getColor(android.R.color.holo_orange_light));
            btn_soft.setTextColor(this.getResources().getColor(R.color.black));
        });
        btn_soft.setOnClickListener(v -> {
            videoView.pause();
            videoView.release();
            videoView.setPlayerFactory(IjkPlayerFactory.create());      //切换为 ijkPlayer
            videoView.start();
            //btn_soft.setTextColor(R.color.light);
            btn_soft.setTextColor(this.getResources().getColor(android.R.color.holo_orange_light));
            btn_hard.setTextColor(this.getResources().getColor(R.color.black));
        });
        findViewById(R.id.btn_collector).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                container_collector.setVisibility(View.VISIBLE);
                isCollectorListVisi = true;

                if(container_menu_option.getVisibility()== View.VISIBLE){
                    container_menu_option.setVisibility(View.GONE);     //一定要GONE 不然会显示不出来
                    isMenuOptVisi = false;
                    Log.e("菜单显示/按键"," 隐藏菜单");
                }
            }
        });
    }
    @SuppressLint("ResourceType")
    public void setPlayingState(int position){
        ChannelAdapter.ViewHolder viewHolder = (ChannelAdapter.ViewHolder)
                channelRecyclerView.findViewHolderForAdapterPosition(position);
    }
    void loadCollectorList(){
        int tempCategoryIndex;
        int tempChannelIndex;
        ModelTV ModelTvTemp;
        List<ModelTV.ListItem> ChannelListTemp = new LinkedList<>() ;      //检索到的二级频道列表
        ModelTV.ListItem ChannelItemTemp;
        Cursor cursor = db.query("collectlist", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                tempCategoryIndex = cursor.getInt( cursor.getColumnIndex("categoryindex") );
                tempChannelIndex = cursor.getInt( cursor.getColumnIndex("channelindex") );
                ModelTvTemp = allProgramList.get(tempCategoryIndex);
                ChannelListTemp = ModelTvTemp.getList();
                ChannelItemTemp = ChannelListTemp.get(tempChannelIndex);


                collectorChannelList.add(ChannelItemTemp);      //放入收藏
            } while (cursor.moveToNext());
        }
        cursor.close();

        collectChannelAdapter = new ChannelAdapter(this,collectorChannelList);
        collect_recyclerview.setAdapter(collectChannelAdapter);
    }
    void loadFirstCategories(){
        //放入第一级分类
        firstCategoryAdapter = new FirstCategoryAdapter(this, categoryItemList);
        firstRecyclerView.setAdapter(firstCategoryAdapter);

        loadLastChannel();
        //-------------------CategoryRecyclerView父视图失去焦点 和 获得焦点 事件监听------------------
        firstRecyclerView.setGainFocusListener((child, focus) -> {      // 列表获取焦点时执行
            //if(isJieMuVisi) {        // 界面显示时， 聚焦Category才可以设为不透明/原初背景
                Log.e("category列表", "category获取焦点 变正常背景");
                firstCategoryAdapter.notifyItemChanged(firstRecyclerView.getmLastFocusPosition(),1);        //设置上此聚焦category为正常
            //}
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

                    currentFocusCategory = categoryItemList.get(position).getCategoryName();
                    //Toast.makeText(getApplicationContext(), "你的焦点 改到了第" + currentFocusCategory, Toast.LENGTH_SHORT).show();

                    if(isJieMuVisi) {        // 显示界面后才可以加载/切换/显示 channels
                        mLastFocusPositionCategory = firstRecyclerView.getmLastFocusPosition();      //设置上次聚焦视图
                        channelRecyclerView.setmLastFocusPosition(0);       // 每次改变categories时初始化记忆焦点 即初始聚焦第一个
                        initData(position);
                        loadChannels();
                        Log.i("Category Item聚焦","刷新adapter");
                        hasChangeChannelForJumpFocus = true;
                    }
                    //若当前聚焦category为 播放中category时， 使得当前播放channel获得播放图标。 且焦点记忆也设置为其
                    if(position == currentPlayingCategoryIndex){
                        channelRecyclerView.scrollToPosition(currentPlayingChannelIndex);      //滑动到播放中channel
                        channelRecyclerView.setmLastFocusPosition(currentPlayingChannelIndex);       //设置焦点记忆为 正在播放channel
                    }
                }
                if (view.hasFocus()){
                    currentFocusCategoryIndex = position;
                    hasChangeChannelForJumpFocus = true;        //切换categories焦点
                    firstFromCateToChannel = false;     //一级列表获得焦点后， 可以在切换一级列表焦点时 刷新adapter
                    Log.i("Category Item聚焦","cate获焦，可以刷新channel列表");

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

    /**
     * 加载上次观看的频道
     */
    void loadLastChannel(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //if(!sharedPreferences.getString("lastPlayingChannelIndex", "-1").equals("-1")){     //不为空
            currentPlayingChannelIndex = Integer.parseInt(sharedPreferences.getString("lastPlayingChannelIndex", "0"));        // 这里 和 position 的值是一样的， 当前点击和当前聚焦为同一个
            currentPlayingCategoryIndex = Integer.parseInt(sharedPreferences.getString("lastPlayingCategoryIndex", "0"));
       // }
        focusChannelList = allProgramList.get(currentPlayingCategoryIndex).getList();       //初始化频道列表
        lastPlayingChannelList = allProgramList.get(lastPlayingCategoryIndex).getList();       //打开app默认播放 第一个
        channelAdapter = new ChannelAdapter(this, focusChannelList);
        channelRecyclerView.setAdapter(channelAdapter);

        Log.e("加载上次储存",currentPlayingChannelIndex + "");
        updateAndShowChannelWindow();
        showAndUpdatePlayingImage();
        registerAndPlay();
        //更新
        lastPlayingChannelIndex = currentPlayingChannelIndex;           //其实就是现在播放中 分类与频道
        lastPlayingCategoryIndex = currentPlayingCategoryIndex;

    }
    void saveLastChannel(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("lastPlayingCategoryIndex", String.valueOf(currentPlayingCategoryIndex));
        editor.putString("lastPlayingChannelIndex", String.valueOf(currentPlayingChannelIndex));
        editor.apply();
        Log.e("储存当前播放",currentPlayingChannelIndex + "");
    }

    void initData(int position){
        focusChannelList= allProgramList.get(position).getList();      //设置当前聚焦 可见的 channel-list
        //for(int i = 0; i < tempFocusChannelList.size(); i++)
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
        //--------------------Channel Adapter的Item 点击监听--------------------------
        channelAdapter.setmOnVideoClickListener(new ChannelAdapter.OnVideosClickListener() {
            @Override
            public void onMyClick(View view, int position) {        //点击频道时触发

                //记录当前要播放的channel和category
                currentPlayingChannelIndex = channelRecyclerView.getmLastFocusPosition();        // 这里 和 position 的值是一样的， 当前点击和当前聚焦为同一个
                currentPlayingCategoryIndex = firstRecyclerView.getmLastFocusPosition();
                if(currentPlayingCategoryIndex != lastPlayingCategoryIndex || currentPlayingChannelIndex != lastPlayingChannelIndex) {
                    updateAndShowChannelWindow();
                    showAndUpdatePlayingImage();

                    registerAndPlay();
                    //更新
                    lastPlayingChannelIndex = currentPlayingChannelIndex;           //其实就是现在播放中 分类与频道
                    lastPlayingCategoryIndex = currentPlayingCategoryIndex;
                }
            }
        });
        //--------------------Channel Adapter的Item 长按点击监听--------------------------
        channelAdapter.setOnItemOnLongClickListener(new ChannelAdapter.OnRecyclerViewItemOnLongClickListener() {
            @Override
            public void onLongClick(View view, int position, Context mContext) {
                Toast.makeText(getApplicationContext(), "你长按了", Toast.LENGTH_SHORT).show();
                ContentValues values = new ContentValues();
                values.put("categoryindex", currentFocusCategoryIndex);
                values.put("channelindex",position);
                db.insert("collectlist", null, values);
            }
        });

    }

    /**
     *播放图标设置。并刷新对应item
     */
    public void showAndUpdatePlayingImage(){
        lastPlayingChannelList = allProgramList.get(lastPlayingCategoryIndex).getList();       //点击切换频道时，更新 上一个播放中频道列表
        //设置channel的
        lastPlayingChannelList.get(lastPlayingChannelIndex).setPlaying(false);       //上个播放列表中的频道 取消播放图标，文字颜色设为白色
        focusChannelList.get(currentPlayingChannelIndex).setPlaying(true);     //此刻点击的 设置播放图标，文字颜色设为黄色
        //channelAdapter.setData(currentChannelList);                       //不用重新绑定数据
        channelAdapter.notifyItemChanged(currentPlayingChannelIndex);
        channelAdapter.notifyItemChanged(lastPlayingChannelIndex);

        //设置category list的
        categoryItemList.get(lastPlayingCategoryIndex).setPlaying(false);
        categoryItemList.get(currentPlayingCategoryIndex).setPlaying(true);
        firstCategoryAdapter.notifyItemChanged(currentPlayingCategoryIndex);
        firstCategoryAdapter.notifyItemChanged(lastPlayingCategoryIndex);

        //ViewGroup view = (ViewGroup) channelLinearLayoutManager.findViewByPosition(currentPlayingChannel);
        //requestAndClickRightButtonSimulate();
        Log.i("channel点击","当前点击/播放：" + currentPlayingChannelIndex);
        Log.i("channel点击","上次点击/播放：" + lastPlayingChannelIndex);
    }
    public void stopAndPlay(String url){
        videoView.pause();
        videoView.release();
        videoView.setUrl(url);
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
        ModelTV.ListItem channelItem = focusChannelList.get(currentPlayingChannelIndex);     //-----关键--------获得新的频道 实例信息-------------
        List<String> urlList = channelItem.getUrlList();
        String url = urlList.get(0);
        uuid = url.substring(26);       //获取频道id
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

                MainActivity.this.runOnUiThread(new Runnable() {        //在主线程中使用运行
                    public void run() {
                        stopAndPlay(urlForLive);
                    }
                });
            }
        });
        saveLastChannel();

        urlForClose    = "http://127.0.0.1:" + port + "/stream/close?uuid=" + uuid;         //更新应该关闭的频道
    }

    public void simulateRequestAndClickRightButton(){
        Log.e("显示节目栏 模拟","");
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
        firstRecyclerView.scrollToPosition(currentPlayingCategoryIndex);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //0.05秒后聚焦正在播放的category
                if (hasChangeChannelForJumpFocus || isNumberSwitch) {
                    firstFromCateToChannel = false;         //使得节目栏显示 聚焦播放中category时 能够切换channel列表
                    firstLinerLayoutManager.findViewByPosition(currentPlayingCategoryIndex).requestFocus();
                    pressRight.start();
                    //channelLinearLayoutManager.findViewByPosition(currentPlayingChannelIndex).requestFocus();

                    isNumberSwitch = false;
                } else if (!hasChangeChannelForJumpFocus) {
                    channelLinearLayoutManager.findViewByPosition(currentPlayingChannelIndex).requestFocus();
                }
                hasChangeChannelForJumpFocus = false;
            }
        }, 10);
        firstLinerLayoutManager.scrollToPosition(currentPlayingCategoryIndex);
        firstCategoryAdapter.notifyItemChanged(currentPlayingCategoryIndex, 0);     //直接设置为 透明状态背景

        //滚动到播放中channel  模拟右击
        channelRecyclerView.scrollToPosition(currentPlayingChannelIndex);
        channelLinearLayoutManager.scrollToPosition(currentPlayingChannelIndex);
        channelRecyclerView.setmLastFocusPosition(currentPlayingChannelIndex);       //设置焦点记忆为 播放中channel
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

            //---节目栏消失后， 当前聚焦列表 设为 当前实际播放中频道列表
            focusChannelList = allProgramList.get(currentPlayingCategoryIndex).getList();
        }
    }

    /**
     * 显示频道信息小窗口 实时改变index和name
     */
    @SuppressLint("SetTextI18n")
    public void updateAndShowChannelWindow(){
        window_channel_info.setVisibility(View.VISIBLE);
        textView_window_index.setText("" + (focusChannelList.get(currentPlayingChannelIndex).getOrder()));         //小窗设置当前分类下的 index
        textView_window_dname.setText("" + focusChannelList.get(currentPlayingChannelIndex).getDname());

        mHandlerHideOrShow.removeMessages(HIDE_CHANNEL_WINDOW);
        if(window_channel_info.getVisibility() == View.VISIBLE){ mHandlerHideOrShow.sendEmptyMessageDelayed(HIDE_CHANNEL_WINDOW,6000); }    //6秒后隐藏
    }
    public void hideChannelWindow(){
        window_channel_info.setVisibility(View.INVISIBLE);
    }

    /**
     * 显示切换频道窗口 实时改变已经按出的数字
     */
    public void showSwitchProgramWindow(){
        window_number_switch_channel.setVisibility(View.VISIBLE);
        textview_switchTv.setText(strSwitchTv);

        mHandlerHideOrShow.removeMessages(Hide_Switch_Program_Window);
        if(window_number_switch_channel.getVisibility() == View.VISIBLE){ mHandlerHideOrShow.sendEmptyMessageDelayed(Hide_Switch_Program_Window,3000); }    //5秒后隐藏 并切换频道
    }
    ModelTV ModelTvTempForSearch;
    List<ModelTV.ListItem> ChannelListTempForSearch = new LinkedList<>() ;      //检索到的二级频道列表
    ModelTV.ListItem ChannelItemTempForSearch;
    List<ModelTV.ListItem> findedChannelList = new LinkedList<>() ;      //检索到的二级频道列表
    public void hideSwitchProgramWindow(){      //最终停止输入时 检索窗消失时执行。 执行切换频道。
        int cateIndex = 0;
        int channelIndex = 0;
        for(int i = 0; i < allProgramList.size(); i++) {
            ModelTvTempForSearch = allProgramList.get(i);
            ChannelListTempForSearch = ModelTvTempForSearch.getList();
            for (int j = 0; j < ChannelListTempForSearch.size(); j++) {
                ChannelItemTempForSearch = ChannelListTempForSearch.get(j);
                if (ChannelItemTempForSearch.getOrder().equals(strSwitchTv)) {       //找到该频道了
                    //切换频道
                    findedChannelList = ChannelListTempForSearch;
                    cateIndex = i;
                    channelIndex = j;
                }
            }
        }

        if(findedChannelList.size() != 0){      //找到了
            focusChannelList = findedChannelList;
            currentPlayingCategoryIndex = cateIndex;
            currentPlayingChannelIndex = channelIndex;
            updateAndShowChannelWindow();
            showAndUpdatePlayingImage();
            registerAndPlay();
            //更新
            lastPlayingChannelIndex = currentPlayingChannelIndex;           //其实就是现在播放中 分类与频道
            lastPlayingCategoryIndex = currentPlayingCategoryIndex;

            strSwitchTv = "";
            isNumberSwitch = true;
        }

        window_number_switch_channel.setVisibility(View.INVISIBLE);
    }

    private long mExitTime;
    private boolean shortPressCenter = false;
    //按键控制
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        switch (event.getKeyCode()){
            case KeyEvent.KEYCODE_DPAD_DOWN:        //往小的频道切换
                if(isJieMuVisi){showAndHideJiemulan();
                    Log.e("节目栏显示/按键"," 点下了下按键");}
                if(!isJieMuVisi && !isMenuOptVisi) {       //界面隐藏时执行
                    Log.e("节目栏隐藏/按键"," 点下了下按键");
                    if (lastPlayingChannelIndex == 0){
                        Toast.makeText(this, "到底了" , Toast.LENGTH_SHORT).show();
                    }
                    if (0 < lastPlayingChannelIndex && lastPlayingChannelIndex <= focusChannelList.size() - 1) {    //到达第一个 ， 不执行
                        //
                        currentPlayingChannelIndex = currentPlayingChannelIndex - 1;                    //向上移动
                        registerAndPlay();
                        //改变图片
                        updateAndShowChannelWindow();
                        showAndUpdatePlayingImage();
                        lastPlayingChannelIndex = currentPlayingChannelIndex;
                    }
                    return  true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_UP:        //往大的频道切换
                if(isJieMuVisi){showAndHideJiemulan();
                    Log.e("节目栏显示/按键"," 点下了上按键");}
                if(!isJieMuVisi && !isMenuOptVisi) {       //节目栏隐藏时执行
                    Log.e("节目栏隐藏/按键"," 点下了上按键");
                    if (lastPlayingChannelIndex == focusChannelList.size() - 1){
                        Toast.makeText(this, "到底了" , Toast.LENGTH_SHORT).show();
                    }
                    if (0 <= lastPlayingChannelIndex && lastPlayingChannelIndex < focusChannelList.size() - 1) {    //最后一个时， 不能再上了
                        //
                        currentPlayingChannelIndex = currentPlayingChannelIndex + 1;                    //向下移动
                        registerAndPlay();

                        //改变图片
                        updateAndShowChannelWindow();
                        showAndUpdatePlayingImage();
                        lastPlayingChannelIndex = currentPlayingChannelIndex;
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

            case KeyEvent.KEYCODE_0:
                if(!strSwitchTv.equals("") && strSwitchTv.length()<4 && !isJieMuVisi){    strSwitchTv += "0";       //当检索频道值不为空 且 位数小于4时才执行,且节目栏不显示时
                Log.e("按键"," 点下了 0 按键");
                Log.e("按键",strSwitchTv);
                showSwitchProgramWindow();  }
                break;
            case KeyEvent.KEYCODE_1:
                if(strSwitchTv.length()<4 && !isJieMuVisi){     strSwitchTv += "1";
                Log.e("按键"," 点下了 1 按键");
                showSwitchProgramWindow();}
                break;
            case KeyEvent.KEYCODE_2:
                if(strSwitchTv.length()<4 && !isJieMuVisi){     strSwitchTv += "2";
                Log.e("按键"," 点下了 2 按键");
                showSwitchProgramWindow();}
                break;
            case KeyEvent.KEYCODE_3:
                if(strSwitchTv.length()<4 && !isJieMuVisi){     strSwitchTv += "3";
                Log.e("按键"," 点下了 3 按键");
                showSwitchProgramWindow();}
                break;
            case KeyEvent.KEYCODE_4:
                if(strSwitchTv.length()<4 && !isJieMuVisi){     strSwitchTv += "4";
                Log.e("按键"," 点下了 4 按键");
                    showSwitchProgramWindow();}
                break;
            case KeyEvent.KEYCODE_5:
                if(strSwitchTv.length()<4 && !isJieMuVisi){     strSwitchTv += "5";
                Log.e("按键"," 点下了 5 按键");
                    showSwitchProgramWindow();}
                break;
            case KeyEvent.KEYCODE_6:
                if(strSwitchTv.length()<4 && !isJieMuVisi){     strSwitchTv += "6";
                Log.e("按键"," 点下了 6 按键");
                    showSwitchProgramWindow();}
                break;
            case KeyEvent.KEYCODE_7:
                if(strSwitchTv.length()<4 && !isJieMuVisi){     strSwitchTv += "7";
                Log.e("按键"," 点下了 7 按键");
                    showSwitchProgramWindow();}
                break;
            case KeyEvent.KEYCODE_8:
                if(strSwitchTv.length()<4 && !isJieMuVisi){     strSwitchTv += "8";
                Log.e("按键"," 点下了 8 按键");
                    showSwitchProgramWindow();}
                break;
            case KeyEvent.KEYCODE_9:
                if(strSwitchTv.length()<4 && !isJieMuVisi){     strSwitchTv += "9";
                Log.e("按键"," 点下了 9 按键");
                    showSwitchProgramWindow();}
                break;

            case KeyEvent.KEYCODE_BACK:
                if(isJieMuVisi) {       //节目栏显示时 就隐藏
                    firstCategoryAdapter.notifyItemChanged(firstRecyclerView.getmLastFocusPosition(),1);        //设置上此聚焦category为正常
                    hideJiemulan();
                    return true;
                }
                if(container_menu_option.getVisibility()== View.VISIBLE){
                    container_menu_option.setVisibility(View.GONE);     //一定要GONE 不然会显示不出来
                    isMenuOptVisi = false;
                    Log.e("菜单显示/按键"," 隐藏菜单");
                    return true;
                }
                if(isCollectorListVisi){
                    container_collector.setVisibility(View.INVISIBLE);
                    isCollectorListVisi = false;
                    return true;
                }
                if(!isJieMuVisi && !isMenuOptVisi){     //直接退出app
                    if ((System.currentTimeMillis() - mExitTime) > 2000) {      //2s内 连按返回退出
                        Object mHelperUtils;
                        Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
                        mExitTime = System.currentTimeMillis();

                    } else {
                        ExitUtil.getInstance().exit();
                    }
                    return true;
                }
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

            case KeyEvent.KEYCODE_MENU:
                if(!isJieMuVisi){     //当节目栏不可见时 才显示menu
                    container_menu_option.setVisibility(View.VISIBLE);
                    isMenuOptVisi = true;
                    Log.e("节目栏显示/按键"," 点下了菜单按键");
                    btn_hard.requestFocus();

                    return true;
                }
                break;
        }

        return super.dispatchKeyEvent(event);
    }

    @SuppressLint("SetTextI18n")
    public void updateTcpSpeed(){
        int speed = (int) (videoView.getTcpSpeed() / 1024);
        textView_speed.setText(speed + "kb/s");
    }
    //状态改变监听 实例
    private final xyz.doikki.videoplayer.player.VideoView.OnStateChangeListener mOnStateChangeListener = new xyz.doikki.videoplayer.player.VideoView.SimpleOnStateChangeListener() {
        @Override
        public void onPlayerStateChanged(int playerState) {
            switch (playerState) {
                case xyz.doikki.videoplayer.player.VideoView.PLAYER_NORMAL://小屏
                    break;
                case xyz.doikki.videoplayer.player.VideoView.PLAYER_FULL_SCREEN://全屏
                    break;
            }
        }

        @Override
        public void onPlayStateChanged(int playState) {
            switch (playState) {
                case xyz.doikki.videoplayer.player.VideoView.STATE_IDLE:
                    break;
                case xyz.doikki.videoplayer.player.VideoView.STATE_PREPARING:
                    window_speed.setVisibility(View.VISIBLE);
                    mHandlerHideOrShow.sendEmptyMessage(UPDATE_TCP_SPEED);
                    break;
                case xyz.doikki.videoplayer.player.VideoView.STATE_PREPARED:
                    break;
                case xyz.doikki.videoplayer.player.VideoView.STATE_PLAYING:
                    window_speed.setVisibility(View.GONE);
                    mHandlerHideOrShow.removeMessages(UPDATE_TCP_SPEED);
                    break;
                case xyz.doikki.videoplayer.player.VideoView.STATE_PAUSED:
                    break;
                case xyz.doikki.videoplayer.player.VideoView.STATE_BUFFERING:
                    break;
                case xyz.doikki.videoplayer.player.VideoView.STATE_BUFFERED:
                    break;
                case xyz.doikki.videoplayer.player.VideoView.STATE_PLAYBACK_COMPLETED:
                    break;
                case VideoView.STATE_ERROR:
                    break;
            }
        }
    };
}