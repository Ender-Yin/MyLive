package com.example.mylivetvtest.newWidgt;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;


import android.util.AttributeSet;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.View;

import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.mylivetvtest.Loger;
import com.example.mylivetvtest.widget.TvRecyclerView;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by yan on 2017/8/4.
 */
public class FocusRecyclerView extends RecyclerView {
    private boolean needGetDownView;
    private boolean needGetUpView;
    private boolean needGetLeftView;
    private boolean needGetRightView;

    //默认第一次选中第一个位置
    private int mLastFocusPosition = 0;
    View mLastFocusView = null;
    //焦点移出recyclerview的事件监听
    private TvRecyclerView.FocusLostListener mFocusLostListener;
    //焦点移入recyclerview的事件监听
    private TvRecyclerView.FocusGainListener mFocusGainListener;

    boolean mCanFocusOutVertical = false;
    boolean mCanFocusOutHorizontal =true;

    int isSwitchCategoryOrCanJIYI = 0;
    /**
     * isFocusOutAble = true
     * <p>
     * what is the effect ?
     * <p>
     * for example if the orientation in layoutManager is horizontal
     * when the recyclerView scroll to end that the focus could be out of recyclerView
     * just effect the direction that load more able to trigger
     */
    private boolean isFocusOutAble = true;

    public FocusRecyclerView(Context context) {
        this(context, null);
    }

    public FocusRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public FocusRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            resetValue();
        } else {
            return super.dispatchKeyEvent(event);
        }

        if (this.getChildAt(0) == null) {
            return super.dispatchKeyEvent(event);
        }

        LayoutParams layoutParams = (LayoutParams) this.getChildAt(0).getLayoutParams();
        int offsetY = this.getChildAt(0).getHeight() + layoutParams.topMargin + layoutParams.bottomMargin;
        int offsetX = this.getChildAt(0).getWidth() + layoutParams.leftMargin + layoutParams.rightMargin;

        View focusView = this.getFocusedChild();    //当前父视图内 焦点视图

        int layoutDirection = getCurrentLayoutDirection();

        if (focusView != null) {        //父视图内有子图有焦点
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    View downView = FocusFinder.getInstance().findNextFocus(this, focusView, View.FOCUS_DOWN);
                    if (layoutDirection == OrientationHelper.HORIZONTAL || (isFocusOutAble && downView == null && isRecyclerViewToBottom())) {
                        break;
                    }
                    if (downView != null) {     //有视图 可以聚焦
                        downView.requestFocusFromTouch();
                        downView.requestFocus();
                        return true;
                    } else {
                        if (!isRecyclerViewToBottom()) {
                            needGetDownView = true;
                        }
                        this.smoothScrollBy(0, offsetY);
                        return true;
                    }
                case KeyEvent.KEYCODE_DPAD_UP:
                    View upView = FocusFinder.getInstance().findNextFocus(this, focusView, View.FOCUS_UP);
                    if (layoutDirection == OrientationHelper.HORIZONTAL || (upView == null && isRecyclerViewToTop())) {
                        break;
                    }
                    if (upView != null) {
                        upView.requestFocusFromTouch();
                        upView.requestFocus();
                        return true;
                    } else {
                        if (!isRecyclerViewToTop()) {
                            needGetUpView = true;
                        }
                        this.smoothScrollBy(0, -offsetY);
                        return true;
                    }
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    View rightView = FocusFinder.getInstance().findNextFocus(this, focusView, View.FOCUS_RIGHT);
                    if (layoutDirection == OrientationHelper.VERTICAL || (isFocusOutAble && rightView == null && isRecyclerViewToRight())) {
                        break;
                    }
                    if (rightView != null) {
                        rightView.requestFocusFromTouch();
                        rightView.requestFocus();
                        return true;
                    } else {
                        if (!isRecyclerViewToRight()) {
                            needGetRightView = true;
                        }
                        this.smoothScrollBy(offsetX, 0);
                        return true;
                    }
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    View leftView = FocusFinder.getInstance().findNextFocus(this, focusView, View.FOCUS_LEFT);
                    if (layoutDirection == OrientationHelper.VERTICAL || (leftView == null && isRecyclerViewToLeft())) {
                        break;
                    }
                    if (leftView != null) {
                        leftView.requestFocusFromTouch();
                        leftView.requestFocus();
                        return true;
                    } else {
                        if (!isRecyclerViewToLeft()) {
                            needGetLeftView = true;
                        }
                        this.smoothScrollBy(-offsetX, 0);
                        return true;
                    }
            }
        }
        return super.dispatchKeyEvent(event);
    }



    private void resetValue() {
        needGetDownView = false;
        needGetUpView = false;
        needGetLeftView = false;
        needGetRightView = false;
    }

    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);

        final View focusView = getFocusedChild();
        if (focusView != null) {
            if (needGetRightView) {
                View rightView = FocusFinder.getInstance().findNextFocus(this, focusView, View.FOCUS_RIGHT);
                if (rightView != null) {
                    needGetRightView = false;
                    rightView.requestFocusFromTouch();
                    rightView.requestFocus();
                }
            } else if (needGetLeftView) {
                View leftView = FocusFinder.getInstance().findNextFocus(this, focusView, View.FOCUS_LEFT);
                if (leftView != null) {
                    needGetLeftView = false;
                    leftView.requestFocusFromTouch();
                    leftView.requestFocus();
                }
            } else if (needGetDownView) {
                View downView = FocusFinder.getInstance().findNextFocus(this, focusView, View.FOCUS_DOWN);
                if (downView != null) {
                    needGetDownView = false;
                    downView.requestFocusFromTouch();
                    downView.requestFocus();
                }
            } else if (needGetUpView) {
                View upView = FocusFinder.getInstance().findNextFocus(this, focusView, View.FOCUS_UP);
                if (upView != null) {
                    needGetUpView = false;
                    upView.requestFocusFromTouch();
                    upView.requestFocus();
                }
            }
        }
    }

    public void setIsSwitchCategoryOrCanJIYI(int a){
        this.isSwitchCategoryOrCanJIYI = a;
    }

    //实现焦点记忆的关键代码
    @Override
    public void addFocusables(ArrayList<View> views, int direction, int focusableMode) {
        View view = null;
        if (this.hasFocus() || mLastFocusPosition < 0 || (view = getLayoutManager().findViewByPosition(mLastFocusPosition)) == null) {
            super.addFocusables(views,direction,focusableMode);
        }else if(view.isFocusable()){       // 父视图没有焦点即移出列表时 且 上个焦点位置有效 且 上个焦点不为空， 将上个焦点放入
            //将当前的view放到Focusable views列表中，再次移入焦点时会取到该view,实现焦点记忆功能
                views.add(view);        //第一次进入时默认放入第一个，即首次进入时聚焦第一个item
        }else{
            super.addFocusables(views,direction,focusableMode);
        }
    }

    //覆写focusSearch寻焦策略
    @Override
    public View focusSearch(View focused, int direction) {
        //Log.i(TAG, "focusSearch " + focused + ",direction= " + direction);
        View view = super.focusSearch(focused, direction);          //全局搜索
        if (focused == null) {
            return view;
        }
        if (view != null) {
            //该方法返回焦点view所在的父view,如果是在recyclerview之外，就会是null.所以根据是否是null,来判断是否是移出了recyclerview
            View nextFocusItemView = findContainingItemView(view);
            if (nextFocusItemView == null) {
                if (!mCanFocusOutVertical && (direction == View.FOCUS_DOWN || direction == View.FOCUS_UP)) {
                    //屏蔽焦点纵向移出recyclerview
                    return focused;
                }
                if (!mCanFocusOutHorizontal && (direction == View.FOCUS_LEFT || direction == View.FOCUS_RIGHT) ) {
                    //屏蔽焦点横向移出recyclerview
                    return focused;
                }
                //调用移出的监听
                if (mFocusLostListener != null && (direction == View.FOCUS_LEFT || direction == View.FOCUS_RIGHT) ) {
                    mFocusLostListener.onFocusLost(focused, direction);
                }
                return view;
            }
        }
        return view;
    }

    @Override
    //父视图内子视图 改变/获取焦点时调用（在子视图内移动才调用 移动到其他父视图就不调用）。 其实焦点已经改变了。
    public void requestChildFocus(View child, View focused) {   //5       已经找到下一个可获取焦点的子视图
        if (null != child) {
            Loger.i("nextchild = " + child + ",focused = " + focused);
            if (!hasFocus()) {      //父视图不包含焦点
                //recyclerview的 子view 重新获取焦点，调用移入焦点的事件监听
                if (mFocusGainListener != null) {
                    mFocusGainListener.onFocusGain(child, focused);     //调用
                }
            }

            //执行过super.requestChildFocus之后hasFocus会变成true
            super.requestChildFocus(child, focused);
            //设定当前聚焦的item的 View 和 position
            mLastFocusView = focused;                                   //focused已经改变了  focused和child现在是同一个
            mLastFocusPosition = getChildViewHolder(child).getAdapterPosition();
            Loger.i("focusPos = " + mLastFocusPosition);

        }
    }

    /**
     * 设置焦点丢失监听
     */
    public void setFocusLostListener(TvRecyclerView.FocusLostListener focusLostListener) {
        this.mFocusLostListener = focusLostListener;
    }
    public interface FocusLostListener {
        void onFocusLost(View lastFocusChild, int direction);
    }

    /**
     * 设置焦点获取监听
     */
    public void setGainFocusListener(TvRecyclerView.FocusGainListener focusListener) {
        this.mFocusGainListener = focusListener;
    }
    public interface FocusGainListener {
        void onFocusGain(View child, View focued);
    }
    public int getmLastFocusPosition() {
        return mLastFocusPosition;
    }
    public void setmLastFocusPosition(int n){
        this.mLastFocusPosition = n;
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        final int tempFocusIndex = indexOfChild(getFocusedChild());
        if (tempFocusIndex == -1) {
            return i;
        }
        if (tempFocusIndex == i) {
            return childCount - 1;
        } else if (i == childCount - 1) {
            return tempFocusIndex;
        } else {
            return i;
        }
    }

    public void setFocusOutAble(boolean focusOutAble) {
        isFocusOutAble = focusOutAble;
    }

    public void setFocusFrontAble(boolean focusFront) {
        setChildrenDrawingOrderEnabled(focusFront);
    }

    @SuppressLint("WrongConstant")
    private int getCurrentLayoutDirection() {
        int layoutDirection = 1;
        if (getLayoutManager() != null) {
            if (getLayoutManager() instanceof LinearLayoutManager) {
                layoutDirection = ((LinearLayoutManager) getLayoutManager()).getOrientation();
            } else if (getLayoutManager() instanceof StaggeredGridLayoutManager) {
                layoutDirection = ((StaggeredGridLayoutManager) getLayoutManager()).getOrientation();
            }
        }
        return layoutDirection;
    }

    /**
     * ---------------------- recyclerView can scroll -----------------------
     *
     * @return
     */

    public boolean isRecyclerViewToTop() {
        LayoutManager manager = this.getLayoutManager();
        if (manager == null) {
            return true;
        }
        if (manager.getItemCount() == 0) {
            return true;
        }

        int firstChildTop = 0;
        if (this.getChildCount() > 0) {
            View firstVisibleChild = this.getChildAt(0);
            if (firstVisibleChild != null && firstVisibleChild.getMeasuredHeight() >= this.getMeasuredHeight()) {
                if (Build.VERSION.SDK_INT < 14) {
                    return !(ViewCompat.canScrollVertically(this, -1) || this.getScrollY() > 0);
                } else {
                    return !ViewCompat.canScrollVertically(this, -1);
                }
            }

            View firstChild = this.getChildAt(0);
            LayoutParams layoutParams = (LayoutParams) firstChild.getLayoutParams();
            firstChildTop = firstChild.getTop() - layoutParams.topMargin - getRecyclerViewItemInset(layoutParams, 1) - this.getPaddingTop();
        }
        if (manager instanceof LinearLayoutManager) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) manager;
            if (layoutManager.findFirstCompletelyVisibleItemPosition() < 1 && firstChildTop == 0) {
                return true;
            }
        } else if (manager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) manager;
            int[] out = layoutManager.findFirstCompletelyVisibleItemPositions(null);
            if (out[0] < 1 && firstChildTop == 0) {
                return true;
            }
        }
        return false;
    }

    public boolean isRecyclerViewToBottom() {
        LayoutManager manager = this.getLayoutManager();
        if (manager == null || manager.getItemCount() == 0) {
            return false;
        }

        if (manager instanceof LinearLayoutManager) {
            View lastVisibleChild = this.getChildAt(this.getChildCount() - 1);
            if (lastVisibleChild != null && lastVisibleChild.getMeasuredHeight() >= this.getMeasuredHeight()) {
                if (Build.VERSION.SDK_INT < 14) {
                    return !(ViewCompat.canScrollVertically(this, 1) || this.getScrollY() < 0);
                } else {
                    return !ViewCompat.canScrollVertically(this, 1);
                }
            }

            LinearLayoutManager layoutManager = (LinearLayoutManager) manager;
            if (layoutManager.findLastCompletelyVisibleItemPosition() == layoutManager.getItemCount() - 1) {
                return true;
            }
        } else if (manager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) manager;

            int[] out = layoutManager.findLastCompletelyVisibleItemPositions(null);
            int lastPosition = layoutManager.getItemCount() - 1;
            for (int position : out) {
                if (position == lastPosition) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isRecyclerViewToLeft() {
        LayoutManager manager = this.getLayoutManager();
        if (manager == null) {
            return true;
        }
        if (manager.getItemCount() == 0) {
            return true;
        }

        int firstChildLeft = 0;
        if (this.getChildCount() > 0) {
            View firstVisibleChild = this.getChildAt(0);
            if (firstVisibleChild != null && firstVisibleChild.getMeasuredWidth() >= this.getMeasuredWidth()) {
                if (Build.VERSION.SDK_INT < 14) {
                    return !(ViewCompat.canScrollHorizontally(this, -1) || this.getScrollX() > 0);
                } else {
                    return !ViewCompat.canScrollHorizontally(this, -1);
                }
            }

            View firstChild = this.getChildAt(0);
            LayoutParams layoutParams = (LayoutParams) firstChild.getLayoutParams();
            firstChildLeft = firstChild.getLeft() - layoutParams.leftMargin - getRecyclerViewItemInset(layoutParams, 2) - this.getPaddingLeft();
        }
        if (manager instanceof LinearLayoutManager) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) manager;
            if (layoutManager.findFirstCompletelyVisibleItemPosition() < 1 && firstChildLeft == 0) {
                return true;
            }
        } else if (manager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) manager;
            int[] out = layoutManager.findFirstCompletelyVisibleItemPositions(null);
            if (out[0] < 1 && firstChildLeft == 0) {
                return true;
            }
        }
        return false;
    }

    public boolean isRecyclerViewToRight() {
        LayoutManager manager = this.getLayoutManager();
        if (manager == null || manager.getItemCount() == 0) {
            return false;
        }

        if (manager instanceof LinearLayoutManager) {
            View lastVisibleChild = this.getChildAt(this.getChildCount() - 1);
            if (lastVisibleChild != null && lastVisibleChild.getMeasuredWidth() >= this.getMeasuredWidth()) {
                if (Build.VERSION.SDK_INT < 14) {
                    return !(ViewCompat.canScrollHorizontally(this, 1) || this.getScrollX() < 0);
                } else {
                    return !ViewCompat.canScrollHorizontally(this, 1);
                }
            }

            LinearLayoutManager layoutManager = (LinearLayoutManager) manager;
            if (layoutManager.findLastCompletelyVisibleItemPosition() == layoutManager.getItemCount() - 1) {
                return true;
            }
        } else if (manager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) manager;

            int[] out = layoutManager.findLastCompletelyVisibleItemPositions(null);
            int lastPosition = layoutManager.getItemCount() - 1;
            for (int position : out) {
                if (position == lastPosition) {
                    return true;
                }
            }
        }
        return false;
    }

    private int getRecyclerViewItemInset(LayoutParams layoutParams, int type) {
        try {
            Field field = LayoutParams.class.getDeclaredField("mDecorInsets");
            field.setAccessible(true);
            Rect decorInsets = (Rect) field.get(layoutParams);
            if (type == 1) {
                return decorInsets.top;
            } else if (type == 2) {
                return decorInsets.left;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

}
