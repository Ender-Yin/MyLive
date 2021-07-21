package com.example.mylivetvtest.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mylivetvtest.R;
import com.example.mylivetvtest.module.ChannelItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 展示所有视频的列表 4*4， 统一布局
 */
public class ChannelAdapter extends RecyclerView.Adapter<ChannelAdapter.ViewHolder> implements View.OnFocusChangeListener,View.OnClickListener{
    Intent intent;

    //data
    List<ChannelItem> mData;
    Context context;


    public ChannelAdapter(){

    }

    public ChannelAdapter( Context context, List<ChannelItem> mData){
        this.mData = mData;
        this.context = context;
    }

    public void setData(List<ChannelItem> mData) {
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
        initListener();

        View view= holder.itemView;
        //绑定数组数据 到对应匹配视图
        holder.mImgPoster.setImageResource(mData.get(position).getPoster());
        holder.mTextViewTitle.setText(mData.get(position).getTitle());
        //holder.mPlayImage.setVisibility(View.INVISIBLE);
        if(mData.get(position).isPlaying()){
            holder.getmPlayImage().setVisibility(View.VISIBLE);
        }else{
            holder.getmPlayImage().setVisibility(View.INVISIBLE);
        }

        //设置监听器
        view.setTag(position);
        view.setOnFocusChangeListener(this);
        view.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    //ViewHolder类
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mImgPoster;
        TextView mTextViewTitle;
        ImageView mPlayImage;
        //RecyclerView recyclerView;

        public ViewHolder(View itemView) {
            super(itemView);
            mImgPoster = itemView.findViewById(R.id.imageView_channel);
            mTextViewTitle = itemView.findViewById(R.id.textView_channel);
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

    void initListener(){

    }

    //---------------提供点击回调函数-------------
    private OnVideosClickListener mOnVideoClickListener = null;

    public  interface OnVideosClickListener{
        void onMyClick(View view,int position);
    }

    public void setmOnVideoClickListener(OnVideosClickListener m){
        this.mOnVideoClickListener =  m;
    }

    //---------------提供聚焦回调函数-------------
    private ChannelAdapter.OnRecyclerViewItemFocusChangeListener mOnItemFocusChangeListener = null;

    public interface OnRecyclerViewItemFocusChangeListener {
        void onMyFocusChange(View view, int position,Context mContext);
    }

    public void setOnItemFocusChangeListener(ChannelAdapter.OnRecyclerViewItemFocusChangeListener listener) {
        this.mOnItemFocusChangeListener = listener;
    }

}
