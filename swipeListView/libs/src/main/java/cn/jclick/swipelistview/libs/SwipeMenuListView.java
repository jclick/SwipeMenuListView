package cn.jclick.swipelistview.libs;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 *
 * @author baoyz
 * @date 2014-8-18
 *
 */
public class SwipeMenuListView extends ListView implements AbsListView.OnScrollListener{

    private static final int TOUCH_STATE_NONE = 0;
    private static final int TOUCH_STATE_X = 1;
    private static final int TOUCH_STATE_Y = 2;

    private int MAX_Y = 5;
    private int MAX_X = 3;
    private float mDownX;
    private float mDownY;
    private int mTouchState;
    private int mTouchPosition;
    private SwipeMenuLayout mTouchView;
    private OnSwipeListener mOnSwipeListener;

    private SwipeMenuCreator mMenuCreator;
    private OnMenuItemClickListener mOnMenuItemClickListener;
    private SwipeMenuView.OnStrechEndCalledListener mOnStrechEndCalledListener;
    private Interpolator mCloseInterpolator;
    private Interpolator mOpenInterpolator;
    protected List<OnScrollListener> onScrollListenerList = new ArrayList<OnScrollListener>();

    private Drawable mItemStrechBackground;
    private boolean isEnableItemStrech = true;

    public SwipeMenuListView(Context context) {
        super(context);
        init(null);
    }

    public SwipeMenuListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    public SwipeMenuListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void     init(AttributeSet attrs) {
        this.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    if(mTouchView != null){
                        mTouchView.closeMenu();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        MAX_X = dp2px(MAX_X);
        MAX_Y = dp2px(MAX_Y);
        mTouchState = TOUCH_STATE_NONE;
    }

    public void setItemStrechEnable(boolean isEnable){
        this.isEnableItemStrech = isEnable;
    }

    public void setItemStrechBackground(Drawable drawable){
        this.mItemStrechBackground = drawable;
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(new SwipeMenuAdapter(getContext(), adapter) {
            @Override
            public void createMenu(SwipeMenu menu) {
                if (mMenuCreator != null) {
                    mMenuCreator.create(menu);
                }
            }

            @Override
            protected Drawable getStrechBackground() {
                return mItemStrechBackground;
            }

            @Override
            protected boolean isEnableStrech() {
                return isEnableItemStrech;
            }

            @Override
            public void onItemClick(SwipeMenuView view, SwipeMenu menu, int index) {
                boolean flag = false;
                if (mOnMenuItemClickListener != null) {
                    flag = mOnMenuItemClickListener.onMenuItemClick(view.getPosition(), view.isRightMenu(), menu,
                            view.isRightMenu() ? menu.getRightMenuItems().get(index) : menu.getLeftMenuItems().get(index));
                }
                if (mTouchView != null && !flag) {
                    mTouchView.smoothCloseMenu();
                }
            }

            @Override
            public void onMenuItemStrechEndCalled(int position, boolean isRightMenu, SwipeMenu menu, SwipeMenuItem item) {
                if(mOnStrechEndCalledListener != null){
                    mOnStrechEndCalledListener.onMenuItemStrechEndCalled(position, isRightMenu, menu, item);
                }
            }
        });
    }

    @Override
    public void setOnScrollListener(OnScrollListener l) {
        if(l != null){
            this.onScrollListenerList.add(l);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        for(OnScrollListener l : onScrollListenerList) {
            l.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        for(OnScrollListener l : onScrollListenerList) {
            l.onScrollStateChanged(view, scrollState);
        }
    }

    public void setCloseInterpolator(Interpolator interpolator) {
        mCloseInterpolator = interpolator;
    }

    public void setOpenInterpolator(Interpolator interpolator) {
        mOpenInterpolator = interpolator;
    }

    public Interpolator getOpenInterpolator() {
        return mOpenInterpolator;
    }

    public Interpolator getCloseInterpolator() {
        return mCloseInterpolator;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public void setOnItemClickListener(
            android.widget.AdapterView.OnItemClickListener listener) {
        // TODO Auto-generated method stub
        super.setOnItemClickListener(listener);
    }

    private boolean canPerformClick = true;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(ev.getPointerCount() > 1) {
            return true;
        }
        if (ev.getAction() != MotionEvent.ACTION_DOWN && mTouchView == null)
            return super.onTouchEvent(ev);
        int action = MotionEventCompat.getActionMasked(ev);
        action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                int oldPos = mTouchPosition;
                mDownX = ev.getX();
                mDownY = ev.getY();
                mTouchState = TOUCH_STATE_NONE;

                mTouchPosition = pointToPosition((int) ev.getX(), (int) ev.getY());

                if (mTouchPosition == oldPos && mTouchView != null
                        && mTouchView.isOpen()) {
                    mTouchState = TOUCH_STATE_X;
                    mTouchView.onSwipe(ev);
                    return true;
                }

                View view = getChildAt(mTouchPosition - getFirstVisiblePosition());

                if (mTouchView != null && mTouchView.isOpen()) {
                    mTouchView.smoothCloseMenu();
                    mTouchView = null;
//				return super.onTouchEvent(ev);
                    canPerformClick = false;
                }else{
                    canPerformClick = true;
                }
                if (view instanceof SwipeMenuLayout) {
                    mTouchView = (SwipeMenuLayout) view;
                }
                if (mTouchView != null) {
                    mTouchView.onSwipe(ev);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float dy = Math.abs((ev.getY() - mDownY));
                float dx = Math.abs((ev.getX() - mDownX));
                if (mTouchState == TOUCH_STATE_X) {
                    if (mTouchView != null) {
                        mTouchView.onSwipe(ev);
                    }
                    getSelector().setState(new int[] { 0 });
                    ev.setAction(MotionEvent.ACTION_CANCEL);
                    super.onTouchEvent(ev);
                    return true;
                } else if (mTouchState == TOUCH_STATE_NONE) {
                    if (Math.abs(dy) > MAX_Y) {
                        mTouchState = TOUCH_STATE_Y;
                    } else if (dx > MAX_X) {
                        mTouchState = TOUCH_STATE_X;
                        if (mOnSwipeListener != null) {
                            mOnSwipeListener.onSwipeStart(mTouchPosition);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mTouchState == TOUCH_STATE_X) {
                    if (mTouchView != null) {
                        mTouchView.onSwipe(ev);
                        if (!mTouchView.isOpen()) {
                            mTouchPosition = -1;
                            mTouchView = null;
                        }
                    }
                    if (mOnSwipeListener != null) {
                        mOnSwipeListener.onSwipeEnd(mTouchPosition);
                    }
                    ev.setAction(MotionEvent.ACTION_CANCEL);
                    super.onTouchEvent(ev);
                    return true;
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean performItemClick(View view, int position, long id) {
        if(!canPerformClick){
            return false;
        }
        return super.performItemClick(view, position, id);
    }

    @Override
    public boolean performLongClick() {
        if(!canPerformClick){
            return false;
        }
        return super.performLongClick();
    }

    @Override
    public boolean performClick() {
        if(!canPerformClick){
            return false;
        }
        return super.performClick();
    }

    public void smoothOpenMenu(int position) {
        if (position >= getFirstVisiblePosition()
                && position <= getLastVisiblePosition()) {
            View view = getChildAt(position - getFirstVisiblePosition());
            if (view instanceof SwipeMenuLayout) {
                mTouchPosition = position;
                if (mTouchView != null && mTouchView.isOpen()) {
                    mTouchView.smoothCloseMenu();
                }
                mTouchView = (SwipeMenuLayout) view;
                mTouchView.smoothOpenMenu();
            }
        }
    }

    /**
     * 平滑关闭
     * @param position
     */
    public void smoothCloseMenu(int position){
        if (position >= getFirstVisiblePosition()
                && position <= getLastVisiblePosition()) {
            View view = getChildAt(position - getFirstVisiblePosition());
            if (view instanceof SwipeMenuLayout) {
                mTouchPosition = position;
                mTouchView = (SwipeMenuLayout) view;
                mTouchView.smoothCloseMenu();
            }
        }
    }


    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getContext().getResources().getDisplayMetrics());
    }

    public void setMenuCreator(SwipeMenuCreator menuCreator) {
        this.mMenuCreator = menuCreator;
    }

    public void setOnMenuItemClickListener(
            OnMenuItemClickListener onMenuItemClickListener) {
        this.mOnMenuItemClickListener = onMenuItemClickListener;
    }

    public void setOnSwipeListener(OnSwipeListener onSwipeListener) {
        this.mOnSwipeListener = onSwipeListener;
    }

    public void  setmOnStrechEndCalledListener(SwipeMenuView.OnStrechEndCalledListener listener){
        this.mOnStrechEndCalledListener = listener;
    }

    public static interface OnMenuItemClickListener {
        /**
         *
         * @param position
         * @param isRightMenu 是否为右侧滑动菜单
         * @param menu
         * @param item 当前点击的item
         * @return
         */
        boolean onMenuItemClick(int position, boolean isRightMenu, SwipeMenu menu, SwipeMenuItem item);
    }

    public static interface OnSwipeListener {
        void onSwipeStart(int position);

        void onSwipeEnd(int position);
    }
}
