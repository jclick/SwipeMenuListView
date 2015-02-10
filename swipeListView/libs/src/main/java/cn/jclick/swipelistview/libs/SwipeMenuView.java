package cn.jclick.swipelistview.libs;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author baoyz
 * @date 2014-8-23
 *
 */
public class SwipeMenuView extends LinearLayout implements OnClickListener {

    private SwipeMenuListView mListView;
    private SwipeMenuLayout mLayout;
    private SwipeMenu mMenu;
    private List<SwipeMenuItem> itemList;
    private OnSwipeItemClickListener onItemClickListener;
    private int position;
    public int getPosition() {
        return position;
    }

    public SwipeMenu getmMenu() {
        return mMenu;
    }

    private boolean isRightMenu;

    public void setPosition(int position) {
        this.position = position;
    }

    public SwipeMenuView(SwipeMenu menu, SwipeMenuListView listView, SwipeMenu.SwipeMenuType type) {
        super(menu.getContext());
        mListView = listView;
        mMenu = menu;
        if(type == SwipeMenu.SwipeMenuType.SWIPE_MENU_TYPE_RIGHT){
            itemList = menu.getRightMenuItems();
            isRightMenu = true;
        }else{
            itemList = menu.getLeftMenuItems();
        }
        int id = 0;
        for (SwipeMenuItem item : itemList) {
            addItem(item, id++, type);
        }
    }

    private void addItem(SwipeMenuItem item, int id, SwipeMenu.SwipeMenuType type) {
        LayoutParams params = new LayoutParams(item.getWidth(),
                LayoutParams.MATCH_PARENT);
        LinearLayout parent = new LinearLayout(getContext());
        parent.setId(id);
        parent.setGravity(Gravity.CENTER);
        parent.setOrientation(LinearLayout.VERTICAL);
        parent.setLayoutParams(params);
        parent.setBackgroundDrawable(item.getBackground());
        parent.setOnClickListener(this);
        if(type == SwipeMenu.SwipeMenuType.SWIPE_MENU_TYPE_LEFT){
            addView(parent, 0);
        }else{
            addView(parent);
        }

        if (item.getIcon() != null) {
            parent.addView(createIcon(item));
        }
        if (!TextUtils.isEmpty(item.getTitle())) {
            parent.addView(createTitle(item));
        }

    }

    public boolean isRightMenu() {
        return isRightMenu;
    }

    public int getRealWidth(){
        int width = 0;
        for(SwipeMenuItem item : itemList){
            width += item.getWidth();
        }
        return width;
    }

    private ImageView createIcon(SwipeMenuItem item) {
        ImageView iv = new ImageView(getContext());
        iv.setImageDrawable(item.getIcon());
        return iv;
    }

    private TextView createTitle(SwipeMenuItem item) {
        TextView tv = new TextView(getContext());
        tv.setText(item.getTitle());
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(item.getTitleSize());
        tv.setTextColor(item.getTitleColor());
        return tv;
    }

    @Override
    public void onClick(View v) {
        if (onItemClickListener != null && mLayout.isOpen()) {
            onItemClickListener.onItemClick(this, mMenu, v.getId());
        }
    }

    public OnSwipeItemClickListener getOnSwipeItemClickListener() {
        return onItemClickListener;
    }
    private OnStrechEndCalledListener onStrechEndCalledListener;

    public OnStrechEndCalledListener getOnStrechEndCalledListener() {
        return onStrechEndCalledListener;
    }

    public void setOnStrechEndCalledListener(OnStrechEndCalledListener onStrechEndCalledListener) {
        this.onStrechEndCalledListener = onStrechEndCalledListener;
    }

    public void setOnSwipeItemClickListener(OnSwipeItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setLayout(SwipeMenuLayout mLayout) {
        this.mLayout = mLayout;
    }

    public static interface OnSwipeItemClickListener {
        void onItemClick(SwipeMenuView view, SwipeMenu menu, int index);
    }

    public static interface  OnStrechEndCalledListener{
        /**
         * 拉伸侧滑到边界
         * @param position
         * @param isRightMenu
         * @param menu
         * @param item
         */
        void onMenuItemStrechEndCalled(int position, boolean isRightMenu, SwipeMenu menu, SwipeMenuItem item);
    }
}
