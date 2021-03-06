package com.example.mylivetvtest.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mylivetvtest.R;
import com.example.mylivetvtest.module.CategoryItem;

import java.util.List;

public class FirstCategoryAdapter extends RecyclerView.Adapter<FirstCategoryAdapter.ViewHolder> implements View.OnFocusChangeListener,View.OnClickListener{
    List<CategoryItem> mDataList;
    Context mContext;       //当前Activity的context

    View.OnFocusChangeListener mOnFocu;

    public FirstCategoryAdapter(Context context, List<CategoryItem> data){
        this.mDataList = data;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //生成item布局视图
        View view =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_first_category, parent, false);

        //传入视图 往holder
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        if (payloads.isEmpty()) {
            Log.d("=========", "onBindViewHolder payload isEmpty ");
        }else {
            if ((int)payloads.get(0) == 1){
                holder.itemView.setBackgroundResource(R.drawable.item_first_category_bg);       //1  为正常状态
                holder.itemView.getBackground().setAlpha(255);
            }else if((int)payloads.get(0) == 0){
                holder.itemView.setBackgroundResource(R.drawable.item_first_category_bg_focus);     //0  为聚焦状态 浅透明
                holder.itemView.getBackground().setAlpha(100);
            }
        }

        onBindViewHolder(holder, position);
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //重写一个自己的OnFocusChangeListener
        initFocusListener();

        //绑定数组数据 到对应匹配视图， 每个position处的
        holder.textView.setText(mDataList.get(position).getCategoryName());

        holder.itemView.setTag(position);
        holder.itemView.setOnFocusChangeListener(this);

        //在activity中改变数据后 调用notify 重新判断执行
        if(mDataList.get(position).isPlaying()){
            Log.i("绑定category数据","正在播放");
            holder.textView.setTextColor(mContext.getResources().getColor(android.R.color.holo_orange_light));
        }else if(!mDataList.get(position).isPlaying()){
            holder.textView.setTextColor(mContext.getResources().getColor(R.color.white));
        }
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            //itemView.setOnFocusChangeListener(mOnFocu);
            textView = itemView.findViewById(R.id.soap_category_title);
        }

        public TextView getmButton(){
            return textView;
        }
    }

    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        mOnItemFocusChangeListener.onMyFocusChange(v, (Integer) v.getTag(), mContext);
    }
    @Override
    public void onClick(View v) {
        //调用自定义接口函数
       // mOnItemClickListener.onMyClick(v, (Integer) v.getTag(), this.mContext);
    }

    //自定义的初始化方法
    void initFocusListener(){

    }

    //1------------------提供回调函数 focusChange 给activity用----------------------------
    private OnRecyclerViewItemFocusChangeListener mOnItemFocusChangeListener = null;

    public interface OnRecyclerViewItemFocusChangeListener {
        void onMyFocusChange(View view, int position, Context mContext);
    }

    public void setOnItemFocusChangeListener(OnRecyclerViewItemFocusChangeListener listener) {
        this.mOnItemFocusChangeListener = listener;
    }
    //2-------------------Click
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public interface OnRecyclerViewItemClickListener {
        void onMyClick(View view, int position, Context mContext);
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

}