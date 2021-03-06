package com.example.mylivetvtest.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mylivetvtest.R;
import com.example.mylivetvtest.module.ModelTV;

import java.util.List;

/**
 * 展示所有视频的列表 4*4， 统一布局
 */
public class ChannelAdapter extends RecyclerView.Adapter<ChannelAdapter.ViewHolder> implements View.OnFocusChangeListener,View.OnClickListener,View.OnLongClickListener {
    Intent intent;

    //data
    List<ModelTV.ListItem> mData;
    Context context;


    public ChannelAdapter(){

    }

    public ChannelAdapter( Context context, List<ModelTV.ListItem> mData){
        this.mData = mData;
        this.context = context;
    }

    public void setData(List<ModelTV.ListItem> mData) {
        this.mData = mData;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_channel, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    /*@Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads){
        super.onBindViewHolder(holder, position, payloads);
        if (payloads.isEmpty()) {
            Log.d("=========", "onBindViewHolder payload isEmpty ");
        }else {
            if ((boolean)payloads.get(0)){
                holder.mPlayImage.setVisibility(View.VISIBLE);
            }else {
                holder.mPlayImage.setVisibility(View.INVISIBLE);
            }
        }

        onBindViewHolder(holder, position);
    }*/

    @Override
    public void onBindViewHolder( ViewHolder holder, int position) {
        View view= holder.itemView;
        //绑定数组数据 到对应匹配视图
        ImageView targetImageView = holder.mImgPoster;
        String internetUrl = mData.get(position).getIco();
        Glide.with(context).load(internetUrl).into(targetImageView);

        holder.mTextViewOrder.setText(mData.get(position).getOrder());
        holder.mTextViewTitle.setText(mData.get(position).getDname());
        holder.mTextViewTitle.setSelected(true);

        //设置监听器
        view.setTag(position);
        view.setOnFocusChangeListener(this);
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);

        //在activity中改变数据后 调用notify 重新判断执行
        if(mData.get(position).isPlaying()){
            holder.getmPlayImage().setVisibility(View.VISIBLE);
            Log.i("绑定数据channel","正在播放");
            holder.mTextViewTitle.setTextColor(context.getResources().getColor(android.R.color.holo_orange_light));
        }else if(!mData.get(position).isPlaying()){
            holder.getmPlayImage().setVisibility(View.INVISIBLE);
            holder.mTextViewTitle.setTextColor(context.getResources().getColor(R.color.white));
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    //ViewHolder类
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mImgPoster;
        TextView mTextViewOrder;
        TextView mTextViewTitle;
        ImageView mPlayImage;
        //RecyclerView recyclerView;

        public ViewHolder(View itemView) {
            super(itemView);
            mImgPoster = itemView.findViewById(R.id.imageView_channel);
            mTextViewOrder = itemView.findViewById(R.id.textView_channel_order);
            mTextViewTitle = itemView.findViewById(R.id.textView_channel_name);
            mPlayImage = itemView.findViewById(R.id.imageView_playing);

            //itemView.setOnFocusChangeListener(ChannelAdapter.this);
        }
        public TextView getTextView(){
            return mTextViewTitle;
        }
        public ImageView getmPlayImage(){
            return mPlayImage;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

        mOnItemFocusChangeListener.onMyFocusChange(v, (int)v.getTag(), context);
    }
    @Override
    public void onClick(View v) {
        mOnVideoClickListener.onMyClick(v, (Integer) v.getTag());
    }
    @Override
    public boolean onLongClick(View v) {
        mOnItemOnLongClickListener.onLongClick(v, (int)v.getTag(), context);
        return false;
    }

    void initListener(){

    }

    //---------------提供点击回调函数-------------
    private OnVideosClickListener mOnVideoClickListener = new OnVideosClickListener() {
        @Override
        public void onMyClick(View view, int position) {
        }
    };

    public  interface OnVideosClickListener{
        void onMyClick(View view,int position);
    }

    public void setmOnVideoClickListener(OnVideosClickListener m){
        this.mOnVideoClickListener =  m;
    }

    //---------------提供聚焦回调函数-------------
    private ChannelAdapter.OnRecyclerViewItemFocusChangeListener mOnItemFocusChangeListener = new OnRecyclerViewItemFocusChangeListener() {
        @Override
        public void onMyFocusChange(View view, int position, Context mContext) {
        }
    };

    public interface OnRecyclerViewItemFocusChangeListener {
        void onMyFocusChange(View view, int position,Context mContext);
    }

    public void setOnItemFocusChangeListener(ChannelAdapter.OnRecyclerViewItemFocusChangeListener listener) {
        this.mOnItemFocusChangeListener = listener;
    }

    //---------------提供聚焦回调函数-------------
    private ChannelAdapter.OnRecyclerViewItemOnLongClickListener mOnItemOnLongClickListener = new OnRecyclerViewItemOnLongClickListener() {
        @Override
        public void onLongClick(View view, int position, Context mContext) {
        }
    };

    public interface OnRecyclerViewItemOnLongClickListener {
        void onLongClick(View view, int position,Context mContext);
    }

    public void setOnItemOnLongClickListener(ChannelAdapter.OnRecyclerViewItemOnLongClickListener listener) {
        this.mOnItemOnLongClickListener = listener;
    }

}
