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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.mylivetvtest.adapter.ChannelAdapter;
import com.example.mylivetvtest.adapter.FirstCategoryAdapter;
import com.example.mylivetvtest.module.ChannelItem;
import com.example.mylivetvtest.newWidgt.FocusRecyclerView;
import com.example.mylivetvtest.widget.TvRecyclerView;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends Activity {

    private static final int UPDATE_FOCUS = 0;
    private static final int FOCUS_ON_PLAYING = 1;

    //
    String[] categoryList =  new String[] {"中国大陆","韩国","美国","英国","香港", "偶像","中国大陆","韩国","美国","英国","香港", "偶像",
            "中国大陆","韩国","美国","英国","香港", "偶像","中国大陆","韩国","美国","英国","香港", "偶像"};
    FocusRecyclerView firstRecyclerView;
    FirstCategoryAdapter firstCategoryAdapter;
    LinearLayoutManager firstLinerLayoutManager;

    List<ChannelItem> currentChannelList = new LinkedList<ChannelItem>() ;
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

    ImageView playingImage;
    RelativeLayout channelRelativeLayout = null;

    //状态变量 合集
    boolean firstFromCateToChannel = false;
    boolean isMemuVisi = true;

    int mLastFocusPositionCategory = 0;
    int mLastFocusPositionChannel = 0;
    int countChangeFocusPos = 0;
    int mFinalFocusLastPosCate = 0;
    int mFinalFocusLastPosChan = 0;

    int currentPlayingCategory = 0;
    int currentPlayingChannel = 0;
    boolean hasChangeChannel = false;
    int lastPlayingChannel = 0;

    private final Handler mHandlerFocusFirst = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_FOCUS:
                    focusOnFirstItem();
                    //mHandlerUpdateTime.sendEmptyMessageDelayed(UPDATE_TIME, 500);     //不断往mHandler 发送更新时间的信息
                    return true;
                case FOCUS_ON_PLAYING:
                    focusOnPlaying();
                    return true;
            }
            return  false;
        }
    }) ;
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();
        loadFirstCategories();

        findViewById(R.id.jiemulan).getBackground().setAlpha(200);          //节目栏设置为透明 只一点
        mHandlerFocusFirst.sendEmptyMessageDelayed(UPDATE_FOCUS, 1000);     //打卡app一秒后聚焦
    }
    void focusOnFirstItem(){
        Objects.requireNonNull(firstLinerLayoutManager.findViewByPosition(0)).requestFocus();
        showPlayingImage();         //默认第一个播放图标
    }
    void focusOnPlaying(){
        View view = channelLinearLayoutManager.findViewByPosition(currentPlayingChannel);
        view.requestFocus();
    }
    @SuppressLint("ResourceType")
    void initialize(){
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

        //默认播放cctv1
        videoView = findViewById(R.id.video_view);
        videoView.setVideoURI(Uri.parse(url2));
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoView.start();
            }
        });


        /*
        for (int i = 0; i < 20; i++) {
            currentChannelList.add(new ChannelItem("CCTV1", R.drawable.cctv1));
        }
        channelAdapter = new ChannelAdapter(this, currentChannelList);
        channelRecyclerView.setAdapter(channelAdapter);*/

    }
    @SuppressLint("ResourceType")
    public void setPlayingState(int position){
        ChannelAdapter.ViewHolder viewHolder = (ChannelAdapter.ViewHolder)
                channelRecyclerView.findViewHolderForAdapterPosition(position);
    }

    void loadFirstCategories(){
        //放入第一级分类
        firstCategoryAdapter = new FirstCategoryAdapter(this, categoryList);
        firstRecyclerView.setAdapter(firstCategoryAdapter);

        //-------------------CategoryRecyclerView父视图失去焦点 和 获得焦点 事件监听------------------
        firstRecyclerView.setGainFocusListener((child, focus) -> {
            if(isMemuVisi) {        // 界面显示时， 聚焦Category才可以设为不透明/原初背景
                Log.i("category列表", "category获取焦点 变不透明");
                firstCategoryAdapter.notifyItemChanged(firstRecyclerView.getmLastFocusPosition(),1);        //设置上此聚焦category为正常
            }
        });
        firstRecyclerView.setFocusLostListener((lastFocusChild, direction) -> {         //这里是只有当按键往左右时 焦点移出时 才会调用。其他方式失去焦点不会触发。
            firstFromCateToChannel = true;     //移出category列表时 设为category获得焦点时不改变刷新adapter
            if(isMemuVisi) {
                Log.i("category列表", "category失去焦点后，设置adapter不能刷新， 变透明");
                firstCategoryAdapter.notifyItemChanged(firstRecyclerView.getmLastFocusPosition(), 0);
            }
        });
        //--------------------重写FirstCategoryAdapter的 自定义焦点改变回调方法-------------------
        firstCategoryAdapter.setOnItemFocusChangeListener(new FirstCategoryAdapter.OnRecyclerViewItemFocusChangeListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onMyFocusChange(View view, int position,Context context) {
                if (view.hasFocus() && !firstFromCateToChannel ) {       // 聚焦一级列表 且 返回一级列表之后， 聚焦再执行方法.
                    textView_currentIndex.setText("0");                 //设置当前初始index

                    currentFocusCategory = categoryList[position];
                    Toast.makeText(getApplicationContext(), "你的焦点 改到了第" + currentFocusCategory, Toast.LENGTH_SHORT).show();

                    if(isMemuVisi) {        // 显示界面后才可以加载/切换/显示 channels
                        mLastFocusPositionCategory = firstRecyclerView.getmLastFocusPosition();      //设置上次聚焦视图
                        channelRecyclerView.setmLastFocusPosition(0);       // 每次改变categories时初始化记忆焦点 即初始聚焦第一个
                        initData();
                        loadChannels();
                        Log.i("Category Item聚焦","刷新adapter");
                    }
                }
                if (view.hasFocus()){
                    firstFromCateToChannel = false;     //获得焦点后 可以切换一级列表焦点时 刷新adapter
                    Log.i("Category Item聚焦","cate获焦，可以刷新channel列表");

                    //当前聚焦category为 播放中category时， 使得当前播放channel获得播放图标。
                    if(position == currentPlayingCategory){
                        currentChannelList.get(currentPlayingChannel).setPlaying(true);     //此刻播放的 设置播放图标
                        channelAdapter.setData(currentChannelList);
                        channelAdapter.notifyItemChanged(currentPlayingChannel);
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
                //isEnterChannelList = 2;      //进去过

            }
        });
        channelRecyclerView.setFocusLostListener(new TvRecyclerView.FocusLostListener() {
            @Override
            public void onFocusLost(View lastFocusChild, int direction) {

            }
        });

        View mFocused = channelRecyclerView.findFocus();
    }

    void initData(){
        switch (currentFocusCategory) {
            case "中国大陆":
                currentChannelList.clear();
                currentChannelList.add(new ChannelItem("CCTV1", R.drawable.cctv1,"http://cctvalih5ca.v.myalicdn.com/live/cctv1_2/index.m3u8"));
                currentChannelList.add(new ChannelItem("CCTV6", R.drawable.cctv6,"http://ivi.bupt.edu.cn/hls/cctv6hd.m3u8"));
                for (int i = 0; i < 20; i++) {
                    currentChannelList.add(new ChannelItem("CCTV1", R.drawable.cctv1));
                }
                break;
            case "韩国":
                currentChannelList.clear();
                for (int i = 0; i < 45; i++) {
                    currentChannelList.add(new ChannelItem("宇宙起源", R.drawable.steal));
                }
                break;
        }
    }
    @SuppressLint({"SetTextI18n", "ResourceType"})
    void loadChannels(){
        channelAdapter = new ChannelAdapter(this, currentChannelList);
        channelRecyclerView.setAdapter(channelAdapter);


        textView_head_category.setText(currentFocusCategory);
        textView_totalCount.setText("" + currentChannelList.size());

        //--------------------Channel Adapter的Item 焦点变化监听--------------------------
        channelAdapter.setOnItemFocusChangeListener((view, position, mContext) -> {
                if(view.hasFocus()) {
                    mLastFocusPositionChannel = channelRecyclerView.getmLastFocusPosition();     //设为当前 channel聚焦位置

                    int currentPos = position + 1;
                    textView_currentIndex.setText("" + currentPos);         //设置当前 index

                    countChangeFocusPos = 0;         //焦点改变后可以 重新赋值最新位置

                    //设为正常背景
                    ChannelAdapter.ViewHolder viewHolder = (ChannelAdapter.ViewHolder)
                            channelRecyclerView.findViewHolderForAdapterPosition(channelRecyclerView.getmLastFocusPosition());
                    viewHolder.itemView.setBackgroundResource(R.drawable.channel_bg);


                }
                else if(!view.hasFocus()){         //焦点失去时执行
                }
        });
        channelAdapter.setmOnVideoClickListener(new ChannelAdapter.OnVideosClickListener() {
            @Override
            public void onMyClick(View view, int position) {
                //记录当前播放的channel和category
                currentPlayingChannel = channelRecyclerView.getmLastFocusPosition();        // 这里 和 position 的值是一样的
                currentPlayingCategory = firstRecyclerView.getmLastFocusPosition();
                showPlayingImage();
                lastPlayingChannel = currentPlayingChannel;

                //显示列表 自己选择要播放的频道 点击切换频道
                ChannelItem channelItem =currentChannelList.get(position);
                String url = channelItem.getUrl();
                if (url != null){
                    stopAndPlay(url);
                }
            }
        });
    }

    /**
     *播放图标设置。并刷新对应item
     */
    public void showPlayingImage(){
        currentChannelList.get(lastPlayingChannel).setPlaying(false);       //上个点击的 取消播放图标
        currentChannelList.get(currentPlayingChannel).setPlaying(true);     //此刻点击的 设置播放图标
        channelAdapter.setData(currentChannelList);
        channelAdapter.notifyItemChanged(currentPlayingChannel);
        channelAdapter.notifyItemChanged(lastPlayingChannel);
        //requestAndClickRightButtonSimulate();
        Log.i("channel点击","当前点击/播放：" + currentPlayingChannel );
        Log.i("channel点击","上次点击/播放：" + lastPlayingChannel );
    }
    public void stopAndPlay(String url){
        videoView.pause();
        videoView.stopPlayback();
        videoView.setVideoURI(Uri.parse(url));
        videoView.start();
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
                    firstLinerLayoutManager.findViewByPosition(currentPlayingCategory).requestFocus();      //0.05秒后聚焦正在播放的category
                    pressRight.start();
                }
            },5);
        firstLinerLayoutManager.scrollToPosition(currentPlayingCategory);

        //滚动到播放中channel  模拟右击
        channelRecyclerView.smoothScrollToPosition(currentPlayingChannel);
        channelLinearLayoutManager.scrollToPosition(currentPlayingChannel);
        channelRecyclerView.setmLastFocusPosition(currentPlayingChannel);       //设置焦点记忆为 播放中channel
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (event.getKeyCode()){
            case KeyEvent.KEYCODE_DPAD_CENTER:
                if(!isMemuVisi) {
                    jieMuLan.setVisibility(View.VISIBLE);           //开始寻找焦点

                    //------------------------------界面显示了-----------------------------------------

                    isMemuVisi = true;                  //界面显示。 category聚焦后可以切换adapter
                    simulateRequestAndClickRightButton();

                    //mHandlerFocusFirst.sendEmptyMessageDelayed(FOCUS_ON_PLAYING, 100);     //显示一秒后聚焦播放中的channel
                    return true;        //消耗了
                }
            case KeyEvent.KEYCODE_BACK:
                if(isMemuVisi) {
                    if (countChangeFocusPos == 0) {       //只执行一次
                        mFinalFocusLastPosCate = mLastFocusPositionCategory;
                        mFinalFocusLastPosChan = mLastFocusPositionChannel;
                        countChangeFocusPos += 1;
                    }
                    isMemuVisi = false;     //界面消失了 显示时再次聚焦时 不刷新adapter

                    jieMuLan.setVisibility(View.INVISIBLE);     // 所有东西失去焦点
                    Log.i("节目栏： ","节目栏消失");
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:        //往小的频道切换
                if(!isMemuVisi) {       //界面隐藏时执行
                    if (lastPlayingChannel == 0){
                        Toast.makeText(this, "到底了" , Toast.LENGTH_SHORT).show();
                    }
                    if (0 < lastPlayingChannel && lastPlayingChannel <= currentChannelList.size() - 1) {    //到达第一个 ， 不执行
                        //
                        ChannelItem channelItem = currentChannelList.get(currentPlayingChannel - 1);
                        String url = channelItem.getUrl();
                        if (url != null) {
                            stopAndPlay(url);
                        }

                        //改变图片
                        currentPlayingChannel = currentPlayingChannel - 1;                    //向下位移
                        showPlayingImage();
                        lastPlayingChannel = currentPlayingChannel;
                    }
                    return  true;
                }
            case KeyEvent.KEYCODE_DPAD_UP:        //往大的频道切换
                if(!isMemuVisi) {       //界面隐藏时执行
                    if (lastPlayingChannel == currentChannelList.size() - 1){
                        Toast.makeText(this, "到底了" , Toast.LENGTH_SHORT).show();
                    }
                    if (0 <= lastPlayingChannel && lastPlayingChannel < currentChannelList.size() - 1) {    //最后一个时， 不能再上了
                        //
                        ChannelItem channelItem = currentChannelList.get(currentPlayingChannel + 1);
                        String url = channelItem.getUrl();
                        if (url != null) {
                            stopAndPlay(url);
                        }

                        //改变图片
                        currentPlayingChannel = currentPlayingChannel + 1;                    //向下位移
                        showPlayingImage();
                        lastPlayingChannel = currentPlayingChannel;
                    }
                    return  true;
                }
        }

        return super.onKeyDown(keyCode, event);
    }
}