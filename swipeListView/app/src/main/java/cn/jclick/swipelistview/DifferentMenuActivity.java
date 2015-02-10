package cn.jclick.swipelistview;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.jclick.swipelistview.libs.SwipeMenu;
import cn.jclick.swipelistview.libs.SwipeMenuCreator;
import cn.jclick.swipelistview.libs.SwipeMenuItem;
import cn.jclick.swipelistview.libs.SwipeMenuListView;
import cn.jclick.swipelistview.libs.SwipeMenuView;

public class DifferentMenuActivity extends Activity {

	private List<ApplicationInfo> mAppList;
	private AppAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);

		mAppList = getPackageManager().getInstalledApplications(0);

		SwipeMenuListView listView = (SwipeMenuListView) findViewById(R.id.listView);
		mAdapter = new AppAdapter();
		listView.setAdapter(mAdapter);
        listView.setOpenInterpolator(new BounceInterpolator());
        listView.setCloseInterpolator(new BounceInterpolator());
		// step 1. create a MenuCreator
		SwipeMenuCreator creator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {
				// Create different menus depending on the view type
				switch (menu.getViewType()) {
				case 0:
					createMenu1(menu);
					break;
				case 1:
					createMenu2(menu);
					break;
				case 2:
					createMenu3(menu);
					break;
				}
			}

			private void createMenu1(SwipeMenu menu) {
				SwipeMenuItem item1 = new SwipeMenuItem(
						getApplicationContext());
				item1.setBackground(new ColorDrawable(Color.rgb(0xE5, 0x18,
						0x5E)));
				item1.setWidth(dp2px(90));
				item1.setIcon(R.drawable.ic_action_favorite);
				menu.addRightMenuItem(item1);
				SwipeMenuItem item2 = new SwipeMenuItem(
						getApplicationContext());
				item2.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
						0xCE)));
				item2.setWidth(dp2px(90));
				item2.setIcon(R.drawable.ic_action_good);
                menu.setStrechMode(SwipeMenu.SwipeStrechMode.SWIPE_STRECH_MODE_BOTH);
                menu.addRightMenuItem(item2);
			}
			
			private void createMenu2(SwipeMenu menu) {
				SwipeMenuItem item1 = new SwipeMenuItem(
						getApplicationContext());
				item1.setBackground(new ColorDrawable(Color.rgb(0xE5, 0xE0,
						0x3F)));
				item1.setWidth(dp2px(90));
				item1.setIcon(R.drawable.ic_action_important);
				menu.addLeftMenuItem(item1);
				SwipeMenuItem item2 = new SwipeMenuItem(
						getApplicationContext());
				item2.setBackground(new ColorDrawable(Color.rgb(0xF9,
						0x3F, 0x25)));
				item2.setWidth(dp2px(90));
				item2.setIcon(R.drawable.ic_action_discard);
                menu.setStrechMode(SwipeMenu.SwipeStrechMode.SWIPE_STRECH_MODE_BOTH);
                menu.addLeftMenuItem(item2);
			}
			
			private void createMenu3(SwipeMenu menu) {
				SwipeMenuItem item1 = new SwipeMenuItem(
						getApplicationContext());
				item1.setBackground(new ColorDrawable(Color.rgb(0x30, 0xB1,
						0xF5)));
				item1.setWidth(dp2px(90));
				item1.setIcon(R.drawable.ic_action_about);
				menu.addLeftMenuItem(item1);
				SwipeMenuItem item2 = new SwipeMenuItem(
						getApplicationContext());
				item2.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
						0xCE)));
				item2.setWidth(dp2px(90));
				item2.setIcon(R.drawable.ic_action_share);
				menu.addRightMenuItem(item2);
                menu.setStrechMode(SwipeMenu.SwipeStrechMode.SWIPE_STRECH_MODE_BOTH);
			}
		};
		// set creator
		listView.setMenuCreator(creator);

		// step 2. listener item click event
		listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, boolean isRightMenu, SwipeMenu menu, SwipeMenuItem menuItem) {
                ApplicationInfo item = mAppList.get(position);
                Log.d(DifferentMenuActivity.this.getClass().getName(), "current click item position is = " + position + ", is right menu = " + isRightMenu + ", menu item id is " + menuItem.getId());
                return false;
            }
		});

        listView.setmOnStrechEndCalledListener(new SwipeMenuView.OnStrechEndCalledListener() {
            @Override
            public void onMenuItemStrechEndCalled(int position, boolean isRightMenu, SwipeMenu menu, SwipeMenuItem menuItem) {
                Log.d(DifferentMenuActivity.this.getClass().getName(), "current Strech item position is = " + position + ", is right menu = " + isRightMenu + ", menu item id is " + menuItem.getId());
            }
        });

	}

	class AppAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mAppList.size();
		}

		@Override
		public ApplicationInfo getItem(int position) {
			return mAppList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
		
		@Override
		public int getViewTypeCount() {
			// menu type count
			return 3;
		}
		
		@Override
		public int getItemViewType(int position) {
			// current menu type
			return position % 3;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(getApplicationContext(),
						R.layout.item_list_app, null);
				new ViewHolder(convertView);
			}
			ViewHolder holder = (ViewHolder) convertView.getTag();
			ApplicationInfo item = getItem(position);
			holder.iv_icon.setImageDrawable(item.loadIcon(getPackageManager()));
			holder.tv_name.setText(item.loadLabel(getPackageManager()));
			return convertView;
		}

		class ViewHolder {
			ImageView iv_icon;
			TextView tv_name;

			public ViewHolder(View view) {
				iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
				tv_name = (TextView) view.findViewById(R.id.tv_name);
				view.setTag(this);
			}
		}
	}

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}
}
