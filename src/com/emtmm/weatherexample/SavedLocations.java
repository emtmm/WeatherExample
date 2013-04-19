package com.emtmm.weatherexample;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.emtmm.weatherexample.fragments.NewLocationFragment;
import com.emtmm.weatherexample.fragments.SavedLocationsFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

public class SavedLocations extends SherlockFragmentActivity {
	public static final String TAG = SavedLocations.class.getSimpleName();
	ActionBar actionBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_saved_locations);
		actionBar = getSupportActionBar();
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.addTab(actionBar
				.newTab()
				.setText(
						getResources().getString(
								R.string.saved_location_tab_label))
				.setTabListener(
						new TabListener<SavedLocationsFragment>(this,
								SavedLocationsFragment.TAG,
								SavedLocationsFragment.class)));
		actionBar.addTab(actionBar
				.newTab()
				.setText(
						getResources().getString(
								R.string.add_location_tab_label))
				.setTabListener(
						new TabListener<NewLocationFragment>(this,
								NewLocationFragment.TAG,
								NewLocationFragment.class)));
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("tab", getSupportActionBar()
				.getSelectedNavigationIndex());
	}

	public static class TabListener<T extends Fragment> implements
			ActionBar.TabListener {
		private final SherlockFragmentActivity mActivity;
		private final String mTag;
		private final Class<T> mClass;
		private final Bundle mArgs;
		private Fragment mFragment;

		public TabListener(SherlockFragmentActivity activity, String tag,
				Class<T> clz) {
			this(activity, tag, clz, null);
		}

		public TabListener(SherlockFragmentActivity activity, String tag,
				Class<T> clz, Bundle args) {
			mActivity = activity;
			mTag = tag;
			mClass = clz;
			mArgs = args;

			// Check to see if we already have a fragment for this tab, probably
			// from a previously saved state. If so, deactivate it, because our
			// initial state is that a tab isn't shown.
			mFragment = mActivity.getSupportFragmentManager()
					.findFragmentByTag(mTag);
			if (mFragment != null && !mFragment.isDetached()) {
				FragmentTransaction ft = mActivity.getSupportFragmentManager()
						.beginTransaction();
				ft.detach(mFragment);
				ft.commit();
			}
		}

		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			if (mFragment == null) {
				mFragment = Fragment.instantiate(mActivity, mClass.getName(),
						mArgs);
				ft.add(android.R.id.content, mFragment, mTag);
			} else {
				ft.attach(mFragment);
			}
		}

		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			if (mFragment != null) {
				ft.detach(mFragment);
			}
		}

		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			Toast.makeText(mActivity, "Reselected!", Toast.LENGTH_SHORT).show();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.actionbarsherlock.app.SherlockFragmentActivity#onCreateOptionsMenu
	 * (com.actionbarsherlock.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

}
