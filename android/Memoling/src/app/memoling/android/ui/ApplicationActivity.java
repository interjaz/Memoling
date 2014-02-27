package app.memoling.android.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ExpandableListView;
import app.memoling.android.Config;
import app.memoling.android.R;
import app.memoling.android.ui.adapter.DrawerAdapter;
import app.memoling.android.ui.fragment.MemoListFragment;
import app.memoling.android.ui.view.DrawerView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.GoogleAnalytics;

public class ApplicationActivity extends SherlockFragmentActivity {

	private String m_title;
	private DrawerLayout m_layDrawer;
	private ExpandableListView m_lstDrawer;
	private DrawerAdapter m_adapterDrawer;
	private ActionBarDrawerToggle m_toggleDrawer;
	private FragmentManager m_fragmentManager;

	public final static int FragmentContainerId = R.id.application_layContent;

	private Bundle m_onRequestFinishFragmentResult;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.activity_application);

		// Read all preferences at start
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

		m_fragmentManager = getSupportFragmentManager();

		m_layDrawer = (DrawerLayout) findViewById(R.id.application_layDrawer);
		m_layDrawer.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		m_layDrawer.setScrimColor(0x22000000);

		m_adapterDrawer = new DrawerAdapter(this, new ResourceManager(this));

		m_lstDrawer = (ExpandableListView) findViewById(R.id.application_lstDrawer);
		LayDrawerEventHandler lstDrawerHandler = new LayDrawerEventHandler();
		m_lstDrawer.setOnGroupClickListener(lstDrawerHandler);
		m_lstDrawer.setOnChildClickListener(lstDrawerHandler);
		m_lstDrawer.setAdapter(m_adapterDrawer);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		m_toggleDrawer = new SherlockActionBarDrawerToggle(this, m_layDrawer, R.drawable.ic_drawer,
				R.string.application_drawerOpen, R.string.application_drawerClose);
		m_layDrawer.setDrawerListener(m_toggleDrawer);

		// enable ActionBar app icon to behave as action to toggle nav drawer
		getSupportActionBar().setIcon(getResources().getDrawable(R.drawable.ic_transparent_bar));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		// Select first fragment
		if (savedInstanceState == null) {
			selectDrawerItem(0, DrawerAdapter.GroupPosition);
		}
	}

	private class LayDrawerEventHandler implements ExpandableListView.OnGroupClickListener,
			ExpandableListView.OnChildClickListener {

		@Override
		public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
			selectDrawerItem(groupPosition, childPosition);
			return false;
		}

		@Override
		public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
			selectDrawerItem(groupPosition, DrawerAdapter.GroupPosition);
			return false;
		}
	}

	public class SherlockActionBarDrawerToggle extends ActionBarDrawerToggle {

		public SherlockActionBarDrawerToggle(Activity activity, DrawerLayout drawerLayout, int drawerImageRes,
				int openDrawerContentDescRes, int closeDrawerContentDescRes) {
			super(activity, drawerLayout, drawerImageRes, openDrawerContentDescRes, closeDrawerContentDescRes);
		}

		public void onDrawerClosed(View view) {
			ApplicationActivity.this.getSupportActionBar().setTitle(ApplicationActivity.this.m_title);
			ApplicationActivity.this.invalidateOptionsMenu(); // creates call to
			// onPrepareOptionsMenu()
		}

		public void onDrawerOpened(View drawerView) {
			ApplicationActivity.this.getSupportActionBar().setTitle(ApplicationActivity.this.m_title);
			ApplicationActivity.this.invalidateOptionsMenu(); // creates call to
			// onPrepareOptionsMenu()
		}

	}

	@Override
	public void setTitle(CharSequence title) {
		if (title == null) {
			return;
		}

		m_title = title.toString();
		getSupportActionBar().setTitle(m_title);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		m_toggleDrawer.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggles
		m_toggleDrawer.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		boolean invokeSuper = getCurrentFragment().onOptionsItemSelected(item);

		if (!invokeSuper) {
			return super.onOptionsItemSelected(item);
		}

		if (!m_toggleDrawer.isDrawerIndicatorEnabled()) {
			// Close current fragment
			super.onBackPressed();
		} else {
			if (m_layDrawer.isDrawerOpen(m_lstDrawer)) {
				m_layDrawer.closeDrawer(m_lstDrawer);
			} else {
				m_layDrawer.openDrawer(m_lstDrawer);
			}
		}

		return super.onOptionsItemSelected(item);
	}

	public DrawerAdapter getDrawerAdapter() {
		return m_adapterDrawer;
	}

	public void setDrawerToggleEnabled(boolean enabled) {
		m_toggleDrawer.setDrawerIndicatorEnabled(enabled);
		m_layDrawer.setDrawerLockMode(enabled ? DrawerLayout.LOCK_MODE_UNLOCKED : DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
		m_layDrawer.invalidate();
	}

	public void requestFragmentReplace(ApplicationFragment fragment) {
		m_layDrawer.closeDrawers();
		m_fragmentManager.beginTransaction().replace(FragmentContainerId, fragment).addToBackStack(null).commit();
	}

	public void requestFinishFragment(Bundle result) {
		m_onRequestFinishFragmentResult = result;
		m_layDrawer.closeDrawers();
		m_fragmentManager.popBackStackImmediate();
	}

	public Bundle getRequestFinishFragmentResult() {
		return m_onRequestFinishFragmentResult;
	}

	public void requestInvalidateOptionsMenu() {
		invalidateOptionsMenu();
	}

	private ApplicationFragment getCurrentFragment() {
		ApplicationFragment fragment = (ApplicationFragment) m_fragmentManager.findFragmentById(FragmentContainerId);
		return fragment;
	}

	/** Swaps fragments in the main content view */
	private void selectDrawerItem(int groupPosition, int childPosition) {
		// Create a new fragment and specify the planet to show based on
		// position
		ApplicationFragment fragment = null;
		DrawerView.OnClickListener onClickListener = null;
		DrawerView view = null;

		if (m_adapterDrawer.getGroupCount() != 0) {
			view = m_adapterDrawer.getItem(groupPosition, childPosition);
			fragment = view.getFragment();
			onClickListener = view.getOnClickListener();
		}

		// First screen
		if (view == null) {
			fragment = new MemoListFragment();

			// Insert the fragment
			m_fragmentManager.beginTransaction().replace(FragmentContainerId, fragment).commit();

		} else if (fragment != null) {

			// Insert the fragment
			m_fragmentManager.beginTransaction().replace(FragmentContainerId, fragment).addToBackStack(null).commit();

		} else if (onClickListener != null) {
			onClickListener.onClick(view);
		} else {
			// Remove current fragment
			m_fragmentManager.popBackStack();
		}

		// Highlight the selected item, update the title, and close the drawer
		// m_lstDrawer.setItemChecked(position, true);

		// Do stuff here
		// setTitle(mPlanetTitles[position]);
		//

		if (childPosition != DrawerAdapter.GroupPosition || m_adapterDrawer.getChildrenCount(groupPosition) == 0) {
			m_layDrawer.closeDrawer(m_lstDrawer);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		getCurrentFragment().onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean invokeSuper = getCurrentFragment().onCreateOptionsMenu(menu);

		if (invokeSuper) {
			return super.onCreateOptionsMenu(menu);
		}

		return true;
	}

	@Override
	public void onBackPressed() {
		boolean invokeSuper = getCurrentFragment().onBackPressed();
		if (invokeSuper) {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {

			if (m_adapterDrawer != null && m_adapterDrawer.getGroupCount() > 0) {
				if (m_layDrawer.isDrawerOpen(m_lstDrawer)) {
					m_layDrawer.closeDrawer(m_lstDrawer);
				} else {
					m_layDrawer.openDrawer(m_lstDrawer);
				}
			}

			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	protected void onStop() {
		super.onStop();
		getAnalyticsTracker().activityStop(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		getAnalyticsTracker().activityStart(this);
	}

	public EasyTracker getAnalyticsTracker() {
		GoogleAnalytics.getInstance(this).setDryRun(Config.Debug);
		return EasyTracker.getInstance(this);
	}
}
