package com.example.mylivetvtest.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.FocusFinder;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mylivetvtest.Loger;

import java.util.ArrayList;

/**
 * @Author：wuhaoteng
 * @Date:2018/12/23
 * @Desc：支持 1.选中项居中 2.焦点记忆 3.可以控制是否横竖向移除 此刻是不能纵向移出
 */
public class TvRecyclerView extends RecyclerView {
    //焦点是否居中
    private boolean mSelectedItemCentered = true;
    private int mSelectedItemOffsetStart = 0;
    private int mSelectedItemOffsetEnd = 0;

    //是否可以纵向移出
    private boolean mCanFocusOutVertical = false;
    //是否可以横向移出
    private boolean mCanFocusOutHorizontal = true;
    //焦点移出recyclerview的事件监听
    private FocusLostListener mFocusLostListener;
    //焦点移入recyclerview的事件监听
    private FocusGainListener mFocusGainListener;
    //最后一次聚焦的位置
    private int mLastFocusPosition = 0;
    private View mLastFocusView = null;


    public TvRecyclerView(Context context) {
        super(context);
        init();
    }

    public TvRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TvRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }


    public void init() {
        setItemAnimator(null);
        /**
         * 该属性是当一个为view获取焦点时，定义viewGroup和其子控件两者之间的关系。
         *
         * 属性的值有三种：
         * beforeDescendants：viewgroup会优先其子类控件而获取到焦点
         * afterDescendants：viewgroup只有当其子类控件不需要获取焦点时才获取焦点
         * blocksDescendants：viewgroup会覆盖子类控件而直接获得焦点
         * */
        setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        setChildrenDrawingOrderEnabled(true);
        this.setFocusable(true);
    }

    /**
     * 实现焦点记忆的关键代码
     * <p>
     * root.addFocusables会遍历root的所有子view和孙view,然后调用addFocusable把isFocusable的view添加到focusables
     */
    @Override
    public void addFocusables(ArrayList<View> views, int direction, int focusableMode) {    //3
        Loger.i("views = " + views);
        Loger.i("lastFocusView = " + mLastFocusView + " mLastFocusPosition = " + mLastFocusPosition);
        if (this.hasFocus() || mLastFocusView == null) {        //当recyclerView拥有焦点或子视图拥有 或者 上一个焦点子视图为空
            //在recyclerview内部焦点切换
            super.addFocusables(views, direction, focusableMode);
        } else {                                                //父视图没有焦点 且 上一个焦点子视图已经保存（即recyclerView的子视图被聚焦过）
            //将上次记忆的最后view放到Focusable views列表中，再次移入焦点时会取到该view,实现焦点记忆功能。  每一次焦点移动都要记录
            views.add(getLayoutManager().findViewByPosition(mLastFocusPosition));
            Loger.i("views.add(lastFocusView)");
        }
    }

    //ViewGroup才有的方法------recyclerView的子视图已经获得焦点
    //要换焦点时调用。 返回 下一个可获得焦点的 View。 还没获得呢
    @Override
    public View focusSearch(View focused, int direction) {      //1     按下方向键就启动了
        View realNextFocus = super.focusSearch(focused, direction);      //2                         //从始开始调用 递归调用到全局视图
        View nextFocus = FocusFinder.getInstance().findNextFocus(this, focused, direction);    //在 root/即本视图 内部中在找

        Loger.i("focused = " + focused);                //4      按下键后 改变焦点后 的原焦点视图
        Loger.i("realNextFocus = " + realNextFocus);
        Loger.i("nextFocus = " + nextFocus);
        //canScrollVertically(1) 关于底部 true表示能滚动，false表示已经滚动到底部
        //canScrollVertically(-1) 关于顶部 true表示能滚动，false表示已经滚动到顶部
        Loger.i("是否可以往上滚" + canScrollVertically(-1));
        Loger.i("是否可以往下滚" + canScrollVertically(1));
        //Loger.i("canScrollHorizontally(-1) = " + canScrollHorizontally(-1));
        //Loger.i("canScrollHorizontally(1) = " + canScrollHorizontally(1));

        switch (direction) {
            //焦点往右移动 调用移出父视图的监听
            case FOCUS_RIGHT:
                //调用移出父视图的监听
                if (nextFocus == null) {
                    if (mCanFocusOutHorizontal) {                                   //可以横向移除
                        if (mFocusLostListener != null) {                           //recyclerView的子视图已经获得 过 焦点
                            mFocusLostListener.onFocusLost(focused, direction);     //调用
                        }
                        return realNextFocus;
                    } else {
                        return null;
                    }
                }
                break;
            case FOCUS_LEFT:
                //调用移出父视图的监听
                if (nextFocus == null) {
                    if (mCanFocusOutHorizontal) {
                        if (mFocusLostListener != null) {
                            mFocusLostListener.onFocusLost(focused, direction);       //调用
                        }
                        return realNextFocus;
                    } else {
                        return null;
                    }
                }
                break;
            case FOCUS_UP:
                //滑倒顶部了
                if (nextFocus == null && !canScrollVertically(-1)) {
                    //滑动到顶部
                    if (mCanFocusOutVertical) {
                        return realNextFocus;
                    } else {
                        return null;
                    }
                }
                break;
            case FOCUS_DOWN:
                //滑倒底部了
                if (nextFocus == null && !canScrollVertically(1)) {
                    //滑动到底部
                    if (mCanFocusOutVertical) {
                        return realNextFocus;
                    } else {
                        return null;
                    }
                }
                break;
        }
        return realNextFocus;
    }

    /**
     * 通过ViewParent#requestChildFocus通知父控件即将获取焦点
     *
     * @param child   下一个要获得焦点的recyclerview item
     * @param focused 当前聚焦的view
     */
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

            //计算控制recyclerview 选中item的居中从参数
            if (mSelectedItemCentered) {
                mSelectedItemOffsetStart = !isVertical() ? (getFreeWidth() - child.getWidth()) : (getFreeHeight() - child.getHeight()); //643 - 80 = 563 返回所有子view的 总高度
                mSelectedItemOffsetStart /= 2;                                //所有子视图的中间点 px       563/2 = 281
                mSelectedItemOffsetEnd = mSelectedItemOffsetStart;
            }
        }
        Loger.i("mSelectedItemOffsetStart = " + mSelectedItemOffsetStart);
        Loger.i("mSelectedItemOffsetEnd = " + mSelectedItemOffsetEnd);
        //Log.i("当前父视图ScrollY", String.valueOf(getScrollY()));
    }

    /**
     * 通过该方法设置选中的item居中
     * <p>
     * 该方法能够确定在布局中滚动或者滑动时候，子item和parent之间的位置
     * dy，dx的实际意义就是在滚动中下滑和左右滑动的距离
     * 而这个值的确定会严重影响滑动的流畅程度
     */
    @Override
    public boolean requestChildRectangleOnScreen(View child, Rect rect, boolean immediate) {

        final int parentLeft = getPaddingLeft();                    //1 0       recyclerview的左内边距。  即左侧边 到 视图内子view左边 的距离
        final int parentRight = getWidth() - getPaddingRight();     //2 200                                     到      子view右边 的距离

        final int parentTop = getPaddingTop();                      //3 0
        final int parentBottom = getHeight() - getPaddingBottom();  //4 648

        final int childLeft = child.getLeft() + rect.left;          //1 0+0=0    此视图相对于其父视图的左侧位置
        final int childTop = child.getTop() + rect.top;             //2 80+0=80(实时改变)    child.getTop是当前聚焦子view的相对父视图上边距离， RecyclerView位置不变，该值是屏幕实际值
                                                                    //子视图 高度为 80 ,宽度为 200.   rect.top是该 视图坐标中 试图内容的实际坐标
        final int childRight = childLeft + rect.width();            //3 0+200=200
        final int childBottom = childTop + rect.height();           //4 80+80=160(实时改变)   该子view底部 到 父视图顶部 的距离
        Log.i("child","getLeft:" + child.getLeft() + " / rect.left:" + rect.left + " / getTop:" + child.getTop() + " / rect.Top:" + rect.top
        + " / rect.width()：" + rect.width() + " / rect.height()：" + rect.height() + " / child.getHeight()：" + child.getHeight() );

        final int offScreenLeft = Math.min(0, childLeft - parentLeft - mSelectedItemOffsetStart); // -281
        final int offScreenRight = Math.max(0, childRight - parentRight + mSelectedItemOffsetEnd);

        final int offScreenTop = Math.min(0, childTop - parentTop - mSelectedItemOffsetStart);     // 80-0-281 = -201  (为此时child到中部的垂直距离)
        final int offScreenBottom = Math.max(0, childBottom - parentBottom + mSelectedItemOffsetEnd); //还是中点 160-648+281 = -207 同上

        /*Log.i("","");
        Log.i("recycler view width", String.valueOf(getWidth()));
        Log.i("parentPaddingLeft:", String.valueOf(parentLeft));
        Log.i("parentRight:", String.valueOf(parentRight));
        Log.i("recycler view height", String.valueOf(getHeight()));
        Log.i("parentPaddingTop:", String.valueOf(parentTop));
        Log.i("parentPaddingBottom:", String.valueOf(getPaddingBottom()));*/

        final boolean canScrollHorizontal = getLayoutManager().canScrollHorizontally();
        final boolean canScrollVertical = getLayoutManager().canScrollVertically();

        // Favor the "start" layout direction over the end when bringing one side or the other
        // of a large rect into view. If we decide to bring in end because start is already
        // visible, limit the scroll such that start won't go out of bounds.
        final int dx;
        if (canScrollHorizontal) {
            if (ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL) {
                dx = offScreenRight != 0 ? offScreenRight
                        : Math.max(offScreenLeft, childRight - parentRight);
            } else {
                dx = offScreenLeft != 0 ? offScreenLeft
                        : Math.min(childLeft - parentLeft, offScreenRight);
            }
        } else {
            dx = 0;
        }

        // Favor bringing the top into view over the bottom. If top is already visible and
        // we should scroll to make bottom visible, make sure top does not go out of bounds.
        final int dy;
        if (canScrollVertical) {
            dy = offScreenTop != 0 ? offScreenTop : Math.min(childTop - parentTop, offScreenBottom);  // 为所有子视图中点  80，-207
        } else {
            dy = 0;
        }


        if (dx != 0 || dy != 0) {
            Loger.i("dx = " + dx);
            Loger.i("dy = " + dy);
            if (immediate) {
                scrollBy(dx, dy);
            } else {
                smoothScrollBy(dx, dy);
            }
            // 重绘是为了选中item置顶，具体请参考getChildDrawingOrder方法
            //Loger.i("当前父视图ScrollY: " + getScrollY());
            postInvalidate();
            return true;
        }

        return false;
    }

    //重写 使得当前聚焦view与最后一个view 绘画顺序互换
    @Override
    protected int getChildDrawingOrder(int childCount, int i) {     //childCount是可见item数量，  i是要绘制的item位置 可由我们返回自定义控制
        View view = getFocusedChild();
        if (null != view) {

            int mPosition = getChildAdapterPosition(view) - getFirstVisiblePosition();      //当前可见区域 焦点view的 相对position于顶部
            if (mPosition < 0) {
                return i;
            } else {
                if (i == childCount - 1) {      //当前绘制 为最后一个
                    if (mPosition > i) {
                        mPosition = i;
                    }
                    Loger.i("当前绘制第" + mPosition + "个");
                    return mPosition;           //使得 焦点view 绘制。即交换绘制顺序。
                }
                if (i == mPosition) {           //当前绘制为 焦点view
                    Loger.i("当前绘制" + (childCount - 1));
                    return childCount - 1;      //绘制 最后一个
                }
            }
        }
        return i;
    }

    public int getFirstVisiblePosition() {
        if (getChildCount() == 0)
            return 0;
        else
            return getChildAdapterPosition(getChildAt(0));
    }

    public int getLastVisiblePosition() {
        final int childCount = getChildCount();
        if (childCount == 0)
            return 0;
        else
            return getChildAdapterPosition(getChildAt(childCount - 1));
    }


    /**
     * 判断是垂直，还是横向.
     */
    private boolean isVertical() {
        LayoutManager manager = getLayoutManager();
        if (manager != null) {
            LinearLayoutManager layout = (LinearLayoutManager) getLayoutManager();
            return layout.getOrientation() == LinearLayoutManager.VERTICAL;

        }
        return false;
    }

    private int getFreeWidth() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }

    private int getFreeHeight() {
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }   // 658 - 0 - 15 = 643


    /**
     * 是否可以垂直滚动
     **/
    public boolean isCanFocusOutVertical() {
        return mCanFocusOutVertical;
    }

    /**
     * 设置可以垂直滚动
     **/
    public void setCanFocusOutVertical(boolean canFocusOutVertical) {
        mCanFocusOutVertical = canFocusOutVertical;
    }

    /**
     * 是否可以水平滚动
     **/
    public boolean isCanFocusOutHorizontal() {
        return mCanFocusOutHorizontal;
    }

    /**
     * 设置是否可以水平滚动
     **/
    public void setCanFocusOutHorizontal(boolean canFocusOutHorizontal) {
        mCanFocusOutHorizontal = canFocusOutHorizontal;
    }


    /**
     * 设置焦点丢失监听
     */
    public void setFocusLostListener(FocusLostListener focusLostListener) {
        this.mFocusLostListener = focusLostListener;
    }

    public interface FocusLostListener {
        void onFocusLost(View lastFocusChild, int direction);
    }

    /**
     * 设置焦点获取监听
     */
    public void setGainFocusListener(FocusGainListener focusListener) {
        this.mFocusGainListener = focusListener;
    }


    public interface FocusGainListener {
        void onFocusGain(View child, View focued);
    }


    public int getmLastFocusPosition() {
        return mLastFocusPosition;
    }
}
