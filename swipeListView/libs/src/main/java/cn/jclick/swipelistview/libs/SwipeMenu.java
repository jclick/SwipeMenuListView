package cn.jclick.swipelistview.libs;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author baoyz
 * @date 2014-8-23
 *
 */
public class SwipeMenu {

    private Context mContext;
    private List<SwipeMenuItem> mLeftItems;
    private List<SwipeMenuItem> mRightItems;
    private int mViewType;

    private SwipeStrechMode strechMode;

    public enum  SwipeStrechMode{
        SWIPE_STRECH_MODE_NONE, SWIPE_STRECH_MODE_LEFT, SWIPE_STRECH_MODE_RIGHT,SWIPE_STRECH_MODE_BOTH
    }
    public enum SwipeMenuType{
        SWIPE_MENU_TYPE_LEFT, SWIPE_MENU_TYPE_RIGHT, SWIPE_MENU_TYPE_BOTH, SWIPE_MENU_TYPE_NONE
    }

    public SwipeMenuType getSwipeMenuType() {
        if (mLeftItems.size() > 0 && mRightItems.size() > 0){
            return SwipeMenuType.SWIPE_MENU_TYPE_BOTH;
        }else if(mLeftItems.size() > 0){
            return SwipeMenuType.SWIPE_MENU_TYPE_LEFT;
        }else if( mRightItems.size() > 0){
            return  SwipeMenuType.SWIPE_MENU_TYPE_RIGHT;
        }else{
            return SwipeMenuType.SWIPE_MENU_TYPE_NONE;
        }
    }
    public SwipeMenu(Context context) {
        mContext = context;
        mLeftItems = new ArrayList<SwipeMenuItem>();
        mRightItems = new ArrayList<SwipeMenuItem>();
        this.strechMode = SwipeStrechMode.SWIPE_STRECH_MODE_NONE;
    }

    public Context getContext() {
        return mContext;
    }

    public void addLeftMenuItem(SwipeMenuItem item) {
        mLeftItems.add(item);
    }
    public void addRightMenuItem(SwipeMenuItem item) {
        mRightItems.add(item);
    }

    public void removeRightMenuItem(SwipeMenuItem item) {
        mRightItems.remove(item);
    }
    public void removeLeftMenuItem(SwipeMenuItem item) {
        mLeftItems.remove(item);
    }

    public List<SwipeMenuItem> getRightMenuItems() {
        return mRightItems;
    }
    public List<SwipeMenuItem> getLeftMenuItems() {
        return mLeftItems;
    }

    public int getViewType() {
        return mViewType;
    }

    public void setViewType(int viewType) {
        this.mViewType = viewType;
    }

    public void setStrechMode(SwipeStrechMode strechMode) {
        this.strechMode = strechMode;
    }

    public SwipeStrechMode getStrechMode() {
        return strechMode;
    }
}
