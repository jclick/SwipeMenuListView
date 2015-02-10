package cn.jclick.swipelistview.libs;

import android.R;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.WrapperListAdapter;
/**
 *
 * @author baoyz
 * @date 2014-8-24
 *
 */
public class SwipeMenuAdapter implements WrapperListAdapter,
        SwipeMenuView.OnSwipeItemClickListener, SwipeMenuView.OnStrechEndCalledListener {

    private ListAdapter mAdapter;
    private Context mContext;
    private boolean enableStrech;
    private Drawable strechBackground;

    public SwipeMenuAdapter(Context context, ListAdapter adapter) {
        mAdapter = adapter;
        mContext = context;
        strechBackground = getStrechBackground();
        enableStrech = isEnableStrech();
    }

    @Override
    public int getCount() {
        return mAdapter.getCount();
    }

    @Override
    public Object getItem(int position) {
        return mAdapter.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return mAdapter.getItemId(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SwipeMenuLayout layout = null;
        if (convertView == null) {
            View contentView = mAdapter.getView(position, convertView, parent);
            if(contentView.getBackground() == null){
                contentView.setBackgroundColor(contentView.getResources().getColor(android.R.color.white));
            }
            SwipeMenu menu = new SwipeMenu(mContext);
            menu.setViewType(mAdapter.getItemViewType(position));
            createMenu(menu);
            SwipeMenuView leftMenuView = null;
            SwipeMenuView rightMenuView = null;
            if (menu.getSwipeMenuType() == SwipeMenu.SwipeMenuType.SWIPE_MENU_TYPE_RIGHT || menu.getSwipeMenuType() == SwipeMenu.SwipeMenuType.SWIPE_MENU_TYPE_BOTH){
                rightMenuView = new SwipeMenuView(menu,
                        (SwipeMenuListView) parent, SwipeMenu.SwipeMenuType.SWIPE_MENU_TYPE_RIGHT);
                rightMenuView.setOnSwipeItemClickListener(this);
                rightMenuView.setOnStrechEndCalledListener(this);
            }
            if (menu.getSwipeMenuType() == SwipeMenu.SwipeMenuType.SWIPE_MENU_TYPE_LEFT || menu.getSwipeMenuType() == SwipeMenu.SwipeMenuType.SWIPE_MENU_TYPE_BOTH){
                leftMenuView = new SwipeMenuView(menu,
                        (SwipeMenuListView) parent, SwipeMenu.SwipeMenuType.SWIPE_MENU_TYPE_LEFT);
                leftMenuView.setOnSwipeItemClickListener(this);
                leftMenuView.setOnStrechEndCalledListener(this);
            }
            SwipeMenuListView listView = (SwipeMenuListView) parent;
            layout = new SwipeMenuLayout(contentView, leftMenuView, rightMenuView,
                    listView.getCloseInterpolator(),
                    listView.getOpenInterpolator());
            if(isEnableStrech()){
                layout.setEnableStrech(true);
            }else{
                layout.setEnableStrech(false);
            }
            layout.setSwipeBackground(strechBackground);
            layout.setPosition(position);
        } else {
            layout = (SwipeMenuLayout) convertView;
            if(isEnableStrech()){
                layout.setEnableStrech(true);
            }else{
                layout.setEnableStrech(false);
            }
            layout.setSwipeBackground(strechBackground);
            layout.closeMenu();
            layout.setPosition(position);
            View view = mAdapter.getView(position, layout.getContentView(),
                    parent);
        }
        return layout;
    }

    protected Drawable getStrechBackground(){
        return new ColorDrawable(mContext.getResources().getColor(R.color.white));
    }

    protected boolean isEnableStrech(){
        return true;
    }

    public void createMenu(SwipeMenu menu) {
        // Test Code
        SwipeMenuItem item = new SwipeMenuItem(mContext);
        item.setTitle("Item 1");
        item.setBackground(new ColorDrawable(Color.GRAY));
        item.setWidth(300);
        menu.addRightMenuItem(item);

        item = new SwipeMenuItem(mContext);
        item.setTitle("Item 2");
        item.setBackground(new ColorDrawable(Color.RED));
        item.setWidth(300);
        menu.addRightMenuItem(item);
    }

    @Override
    public void onItemClick(SwipeMenuView view, SwipeMenu menu, int index) {
    }

    @Override
    public void onMenuItemStrechEndCalled(int position, boolean isRightMenu, SwipeMenu menu, SwipeMenuItem item) {
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        mAdapter.registerDataSetObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        mAdapter.unregisterDataSetObserver(observer);
    }

    @Override
    public boolean areAllItemsEnabled() {
        return mAdapter.areAllItemsEnabled();
    }

    @Override
    public boolean isEnabled(int position) {
        return mAdapter.isEnabled(position);
    }

    @Override
    public boolean hasStableIds() {
        return mAdapter.hasStableIds();
    }

    @Override
    public int getItemViewType(int position) {
        return mAdapter.getItemViewType(position);
    }

    @Override
    public int getViewTypeCount() {
        return mAdapter.getViewTypeCount();
    }

    @Override
    public boolean isEmpty() {
        return mAdapter.isEmpty();
    }

    @Override
    public ListAdapter getWrappedAdapter() {
        return mAdapter;
    }

}
