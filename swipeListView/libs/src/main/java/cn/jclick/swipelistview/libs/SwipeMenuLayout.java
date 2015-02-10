package cn.jclick.swipelistview.libs;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.widget.ScrollerCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author baoyz
 * @date 2014-8-23
 *
 */
public class SwipeMenuLayout extends FrameLayout {

    private static final int CONTENT_VIEW_ID = 1;
    private static final int MENU_VIEW_ID = 2;

    private View mContentView;
    private SwipeMenuView mMenuRightView;
    private SwipeMenuView mMenuLeftView;
    private int mDownX;
    private GestureDetectorCompat mGestureDetector;
    private OnGestureListener mGestureListener;
    private boolean isFling;
    private int MIN_FLING = dp2px(15);
    private int MAX_VELOCITYX = -dp2px(500);
    private int position;
    private Interpolator mCloseInterpolator;
    private Interpolator mOpenInterpolator;

    private Animator contentViewAnimator, menuViewAnimator;
    private AnimatorSet animatorAll;

    private SwipeDirection swipeDirection;
    private SwipeMenuoOpenedState swipeMenuoOpenedState = SwipeMenuoOpenedState.SWIPE_MENU_STATE_NONE_OPENED;

    private Drawable swipeBackground;
    private boolean isEnableStrech;

    public enum SwipeDirection{
        SWIPE_DIRECTION_LEFT, SWIPE_DIRECTION_RIGHT
    }

    enum SwipeMenuoOpenedState{
        SWIPE_MENU_STATE_LEFT_OPENED, SWIPE_MENU_STATE_RIGHT_OPENED, SWIPE_MENU_STATE_NONE_OPENED
    }

    public SwipeMenuLayout(View contentView, SwipeMenuView leftMenuView, SwipeMenuView rightMenuView) {
        this(contentView, leftMenuView, rightMenuView, null, null);
    }

    public SwipeMenuLayout(View contentView, SwipeMenuView leftMenuView, SwipeMenuView rightMenuView,
                           Interpolator closeInterpolator, Interpolator openInterpolator) {
        super(contentView.getContext());
        mCloseInterpolator = closeInterpolator;
        mOpenInterpolator = openInterpolator;
        mContentView = contentView;
        mMenuLeftView = leftMenuView;
        mMenuRightView = rightMenuView;
        if (mMenuRightView != null){
            mMenuRightView.setLayout(this);
        }
        if (mMenuLeftView != null){
            mMenuLeftView.setLayout(this);
        }
        init();
    }

    private SwipeMenuLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private SwipeMenuLayout(Context context) {
        super(context);
    }

    public void setEnableStrech(boolean isEnableStrech){
        this.isEnableStrech = isEnableStrech;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
        if(mMenuLeftView != null){
            mMenuLeftView.setPosition(position);
        }
        if(mMenuRightView != null){
            mMenuRightView.setPosition(position);
        }
    }

    @TargetApi(16)
    private void init() {
        setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));
        mGestureListener = new SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                isFling = false;
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2,
                                   float velocityX, float velocityY) {
                if ((e1.getX() - e2.getX()) > MIN_FLING
                        && velocityX < MAX_VELOCITYX) {
                    isFling = true;
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        };
        mGestureDetector = new GestureDetectorCompat(getContext(),
                mGestureListener);

        if(swipeBackground != null){
            if(Build.VERSION.SDK_INT >= 16){
                this.setBackground(swipeBackground);
            }else{
                this.setBackgroundDrawable(swipeBackground);
            }
        }

        LayoutParams contentParams = new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        mContentView.setLayoutParams(contentParams);
        if (mContentView.getId() < 1) {
            mContentView.setId(CONTENT_VIEW_ID);
        }



        if (mMenuLeftView != null){
            mMenuLeftView.setId(MENU_VIEW_ID);
            mMenuLeftView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT));
            addView(mMenuLeftView);
        }
        addView(mContentView);
        if (mMenuRightView != null){
            mMenuRightView.setId(MENU_VIEW_ID);
            mMenuRightView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT));
            addView(mMenuRightView);
        }

        // in android 2.x, MenuView height is MATCH_PARENT is not work.
        // getViewTreeObserver().addOnGlobalLayoutListener(
        // new OnGlobalLayoutListener() {
        // @Override
        // public void onGlobalLayout() {
        // setMenuHeight(mContentView.getHeight());
        // // getViewTreeObserver()
        // // .removeGlobalOnLayoutListener(this);
        // }
        // });

    }

    /**
     * @param swipeBackground the swipeBackground to set
     */
    public void setSwipeBackground(Drawable swipeBackground) {
        this.swipeBackground = swipeBackground;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public boolean onSwipe(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = (int) event.getX();
                isFling = false;
                break;
            case MotionEvent.ACTION_MOVE:
                int dis = (int) (mDownX - event.getX());
                SwipeMenuView currentSwipeMenu = null;
                if (swipeMenuoOpenedState == SwipeMenuoOpenedState.SWIPE_MENU_STATE_LEFT_OPENED){
                    currentSwipeMenu = mMenuLeftView;
                    dis -= mMenuLeftView.getWidth();
                }else if(swipeMenuoOpenedState == SwipeMenuoOpenedState.SWIPE_MENU_STATE_RIGHT_OPENED){
                    currentSwipeMenu = mMenuRightView;
                    dis += mMenuRightView.getWidth();
                }else{
                    if (dis > 0){
                        swipeDirection = SwipeDirection.SWIPE_DIRECTION_RIGHT;
                        if(mMenuLeftView != null){
                            mMenuLeftView.setVisibility(View.GONE);
                        }
                        if(mMenuRightView != null){
                            mMenuRightView.setVisibility(View.VISIBLE);
                            currentSwipeMenu = mMenuRightView;
                        }
                    }else{
                        swipeDirection = SwipeDirection.SWIPE_DIRECTION_LEFT;
                        if(mMenuRightView != null){
                            mMenuRightView.setVisibility(View.GONE);
                        }
                        if(mMenuLeftView != null){
                            mMenuLeftView.setVisibility(View.VISIBLE);
                            currentSwipeMenu = mMenuLeftView;
                        }
                    }
                }
                if(currentSwipeMenu != null){
                    swipe(currentSwipeMenu, dis);
                }
                break;
            case MotionEvent.ACTION_UP:
                boolean openMenu = false;
                if(swipeMenuoOpenedState == SwipeMenuoOpenedState.SWIPE_MENU_STATE_NONE_OPENED){
                    if (mDownX - event.getX() < 0){
                        swipeDirection = SwipeDirection.SWIPE_DIRECTION_LEFT;
                        if(mMenuLeftView != null){
                            if (isFling || Math.abs((mDownX - event.getX())) > (mMenuLeftView.getWidth() / 2)) {
                                if(mMenuLeftView.getmMenu().getStrechMode() == SwipeMenu.SwipeStrechMode.SWIPE_STRECH_MODE_BOTH || mMenuLeftView.getmMenu().getStrechMode() == SwipeMenu.SwipeStrechMode.SWIPE_STRECH_MODE_LEFT){
                                    if(getWidth() * 0.8 >  mMenuLeftView.getWidth()){
                                        if( Math.abs((mDownX - event.getX())) > getWidth() * 0.8){
                                            if(mMenuLeftView.getOnStrechEndCalledListener() != null){
                                                mMenuLeftView.getOnStrechEndCalledListener().onMenuItemStrechEndCalled(mMenuLeftView.getPosition(), true, mMenuLeftView.getmMenu(), mMenuLeftView.getmMenu().getLeftMenuItems().get(mMenuLeftView.getmMenu().getLeftMenuItems().size() - 1));
                                            }
                                        }
                                    }else{
                                        if(mDownX - event.getX() > mMenuRightView.getWidth() * 0.8){
                                            if(mMenuLeftView.getOnStrechEndCalledListener() != null){
                                                mMenuLeftView.getOnStrechEndCalledListener().onMenuItemStrechEndCalled(mMenuLeftView.getPosition(), true, mMenuLeftView.getmMenu(), mMenuLeftView.getmMenu().getLeftMenuItems().get(mMenuLeftView.getmMenu().getLeftMenuItems().size() - 1));
                                            }
                                        }
                                    }
                                }
                                openMenu = true;
                            } else {
                                openMenu = false;
                            }
                        }
                    }else{
                        swipeDirection = SwipeDirection.SWIPE_DIRECTION_RIGHT;
                        if(mMenuRightView != null){
                            if (isFling || (mDownX - event.getX()) > (mMenuRightView.getWidth() / 2)) {
                                if(mMenuRightView.getmMenu().getStrechMode() == SwipeMenu.SwipeStrechMode.SWIPE_STRECH_MODE_BOTH || mMenuRightView.getmMenu().getStrechMode() == SwipeMenu.SwipeStrechMode.SWIPE_STRECH_MODE_RIGHT){
                                    if(getWidth() * 0.8 >  mMenuRightView.getWidth()){
                                        if(mDownX - event.getX() > getWidth() * 0.8){
                                            if(mMenuRightView.getOnStrechEndCalledListener() != null){
                                                mMenuRightView.getOnStrechEndCalledListener().onMenuItemStrechEndCalled(mMenuRightView.getPosition(), true, mMenuRightView.getmMenu(), mMenuRightView.getmMenu().getRightMenuItems().get(mMenuRightView.getmMenu().getRightMenuItems().size() - 1));
                                            }
                                        }
                                    }else{
                                        if(mDownX - event.getX() > mMenuRightView.getWidth() * 0.8){
                                            if(mMenuRightView.getOnStrechEndCalledListener() != null){
                                                mMenuRightView.getOnStrechEndCalledListener().onMenuItemStrechEndCalled(mMenuRightView.getPosition(), true, mMenuRightView.getmMenu(), mMenuRightView.getmMenu().getRightMenuItems().get(mMenuRightView.getmMenu().getRightMenuItems().size() - 1));
                                            }
                                        }
                                    }
                                }
                                openMenu = true;
                            } else {
                                openMenu = false;
                            }
                        }
                    }
                }

                if (openMenu){
                    smoothOpenMenu();
                }else{
                    smoothCloseMenu();
                }
                break;
        }
        return true;
    }

    public boolean isOpen() {
        return swipeMenuoOpenedState != SwipeMenuoOpenedState.SWIPE_MENU_STATE_NONE_OPENED;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    private void swipe(SwipeMenuView menuView,int dis) {
        if (mMenuRightView != null && mMenuLeftView == null){
            if (dis < 0) {
                dis = 0;
            }
        }else if(mMenuLeftView != null && mMenuRightView == null){
            if(dis > 0){
                dis = 0;
            }
        }else if(mMenuLeftView == null && mMenuRightView == null){
            return;
        }
        cancelAnimation();
        List<Animator> animatorList = getSwipeMenuAnimators(menuView, -dis, 0);
        if (Math.abs(dis) > menuView.getWidth()) {
            if(isEnableStrech){
                contentViewAnimator = ObjectAnimator.ofFloat(mContentView,  "translationX", 0f, -dis).setDuration(0);
            }
            boolean canStrech = false;
            if(menuView == mMenuLeftView){
                if(-dis > mMenuLeftView.getWidth()){
                    if(menuView.getmMenu().getStrechMode() == SwipeMenu.SwipeStrechMode.SWIPE_STRECH_MODE_LEFT || menuView.getmMenu().getStrechMode() == SwipeMenu.SwipeStrechMode.SWIPE_STRECH_MODE_BOTH){
                        canStrech = true;
                    }else{
                        dis = -mMenuLeftView.getWidth();
                    }
                }
            }else if(menuView == mMenuRightView){
                if(dis > mMenuRightView.getWidth()){
                    if(menuView.getmMenu().getStrechMode() == SwipeMenu.SwipeStrechMode.SWIPE_STRECH_MODE_RIGHT || menuView.getmMenu().getStrechMode() == SwipeMenu.SwipeStrechMode.SWIPE_STRECH_MODE_BOTH){
                        canStrech = true;
                    }else{
                        dis = mMenuRightView.getWidth();
                    }
                }
            }

            if(canStrech){
                for(int i = 0; i < menuView.getChildCount(); i++){
                    if(menuView != mMenuRightView){
                        if(i != 0){
                            menuView.getChildAt(i).setVisibility(View.GONE);
                        }else{
                            this.setBackgroundDrawable(menuView.getChildAt(i).getBackground());
                        }
                    }else{
                        if(i != menuView.getChildCount() - 1){
                            menuView.getChildAt(i).setVisibility(View.GONE);
                        }else{
                            this.setBackgroundDrawable(menuView.getChildAt(i).getBackground());
                        }
                    }
                }
            }
        }else{
            contentViewAnimator = ObjectAnimator.ofFloat(mContentView,  "translationX", 0f, -dis).setDuration(0);
        }
        animatorAll = new AnimatorSet();
        menuViewAnimator = ObjectAnimator.ofFloat(menuView,  "translationX", 0f, -dis).setDuration(0);
        animatorList.add(contentViewAnimator);
        animatorList.add(menuViewAnimator);
        animatorAll.playTogether(animatorList);
        animatorAll.start();
    }

    private List<Animator> getSwipeMenuAnimators(ViewGroup menuView, float swipeDistance, int duration){
        List<Animator> animatorList = new ArrayList<Animator>();
        float width = 0;
        if(menuView == mMenuRightView){
            for(int i = 1; i < menuView.getChildCount(); i ++){

                width = -(swipeDistance / menuView.getChildCount() + menuView.getChildAt(i - 1).getWidth());
                if(width > 0){
                    width = 0;
                }
                Animator animator = ObjectAnimator.ofFloat(menuView.getChildAt(i),"translationX", width).setDuration(duration);
                animatorList.add(animator);
            }
        }else if(menuView == mMenuLeftView) {
            for(int i = 0; i < menuView.getChildCount() - 1; i ++){
                if(i != menuView.getChildCount() - 1){
                    width = menuView.getChildAt(i + 1).getWidth() - swipeDistance / menuView.getChildCount();
                    if(width < 0){
                        width = 0;
                    }
                    Animator animator = ObjectAnimator.ofFloat(menuView.getChildAt(i),"translationX", width).setDuration(duration);
                    animatorList.add(animator);
                }
            }
        }
        return animatorList;
    }

    private void cancelAnimation(){
        if (animatorAll != null){
            animatorAll.cancel();
            animatorAll = null;
        }
    }

    public void smoothCloseMenu() {
        swipeMenuoOpenedState = SwipeMenuoOpenedState.SWIPE_MENU_STATE_NONE_OPENED;
        if (swipeDirection == SwipeDirection.SWIPE_DIRECTION_RIGHT) {
            if(mMenuRightView != null){
                setMenuItemsVisible(mMenuRightView);
                animationMenu(mMenuRightView, 0, 200, mOpenInterpolator);
            }
        }else{
            if(mMenuLeftView != null){
                setMenuItemsVisible(mMenuLeftView);
                animationMenu(mMenuLeftView, 0, 200, mOpenInterpolator);
            }
        }
    }

    private void setMenuItemsVisible(ViewGroup menuView){
        for(int i = 0; i < menuView.getChildCount(); i++){
            menuView.getChildAt(i).setVisibility(View.VISIBLE);
        }
    }

    public void smoothOpenMenu() {
        if (swipeDirection == SwipeDirection.SWIPE_DIRECTION_LEFT){
            swipeMenuoOpenedState = SwipeMenuoOpenedState.SWIPE_MENU_STATE_LEFT_OPENED;
            if(mMenuLeftView != null){
                setMenuItemsVisible(mMenuLeftView);
                animationMenu(mMenuLeftView, mMenuLeftView.getRealWidth(), 200, mOpenInterpolator);
            }
        }else{
            swipeMenuoOpenedState = SwipeMenuoOpenedState.SWIPE_MENU_STATE_RIGHT_OPENED;
            if(mMenuRightView != null){
                setMenuItemsVisible(mMenuRightView);
                animationMenu(mMenuRightView, -mMenuRightView.getRealWidth(), 200, mOpenInterpolator);
            }
        }
    }

    public void closeMenu() {
        swipeMenuoOpenedState = SwipeMenuoOpenedState.SWIPE_MENU_STATE_NONE_OPENED;
        if (swipeDirection == SwipeDirection.SWIPE_DIRECTION_RIGHT) {
            if(mMenuRightView != null){
                setMenuItemsVisible(mMenuRightView);
                animationMenu(mMenuRightView, 0, 0, mOpenInterpolator);
            }
        }else{
            if(mMenuLeftView != null){
                setMenuItemsVisible(mMenuLeftView);
                animationMenu(mMenuLeftView, 0, 0, mOpenInterpolator);
            }
        }
    }

    public void openMenu() {
        if (swipeDirection == SwipeDirection.SWIPE_DIRECTION_LEFT){
            swipeMenuoOpenedState = SwipeMenuoOpenedState.SWIPE_MENU_STATE_LEFT_OPENED;
            if(mMenuLeftView != null){
                setMenuItemsVisible(mMenuLeftView);
                animationMenu(mMenuLeftView, mMenuLeftView.getRealWidth(), 0, mOpenInterpolator);
            }
        }else{
            swipeMenuoOpenedState = SwipeMenuoOpenedState.SWIPE_MENU_STATE_RIGHT_OPENED;
            if(mMenuRightView != null){
                setMenuItemsVisible(mMenuRightView);
                animationMenu(mMenuRightView, -mMenuRightView.getRealWidth(), 0, mOpenInterpolator);
            }
        }
    }

    private void animationMenu(ViewGroup menuView, int dis, int duration, Interpolator interpolator){
        cancelAnimation();
        animatorAll = new AnimatorSet();
        contentViewAnimator = ObjectAnimator.ofFloat(mContentView, "translationX",  dis).setDuration(duration);
        menuViewAnimator = ObjectAnimator.ofFloat(menuView, "translationX", dis).setDuration(duration);
        List<Animator> animatorList = getSwipeMenuAnimators(menuView, dis, duration);
        animatorList.add(contentViewAnimator);
        animatorList.add(menuViewAnimator);
        animatorAll.playTogether(animatorList);
        if (interpolator != null){
            animatorAll.setInterpolator(interpolator);
        }
        animatorAll.start();
    }

    public View getContentView() {
        return mContentView;
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getContext().getResources().getDisplayMetrics());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(mMenuRightView != null){
            mMenuRightView.measure(MeasureSpec.makeMeasureSpec(0,
                    MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(
                    getMeasuredHeight(), MeasureSpec.EXACTLY));
        }
        if(mMenuLeftView != null){
            mMenuLeftView.measure(MeasureSpec.makeMeasureSpec(0,
                    MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(
                    getMeasuredHeight(), MeasureSpec.EXACTLY));
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(mMenuLeftView != null){
            mMenuLeftView.layout(-mMenuLeftView.getMeasuredWidth(), 0, 0,
                    mContentView.getMeasuredHeight());
        }
        if(mMenuRightView != null){
            mMenuRightView.layout(getMeasuredWidth(), 0,
                    getMeasuredWidth() + mMenuRightView.getMeasuredWidth(),
                    mContentView.getMeasuredHeight());
        }
        mContentView.layout(0, 0, getMeasuredWidth(),
                mContentView.getMeasuredHeight());

    }

    public void setMenuHeight(int measuredHeight) {
        Log.i("byz", "pos = " + position + ", height = " + measuredHeight);
        LayoutParams params = (LayoutParams) mMenuRightView.getLayoutParams();
        if (params.height != measuredHeight) {
            params.height = measuredHeight;
            mMenuRightView.setLayoutParams(mMenuRightView.getLayoutParams());
        }
    }
}
