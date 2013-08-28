package app.memoling.android.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import app.memoling.android.helper.AppLog;
import app.memoling.android.helper.Lazy;
import app.memoling.android.preference.Preferences;
import app.memoling.android.ui.adapter.DrawerAdapter;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.google.ads.AdView;

public abstract class ApplicationFragment extends Fragment {

	public final static String Action = "ApplicationFragmentAction";

	private AdView m_adView;
	private ResourceManager m_resourceManager;
	private Preferences m_preferences;

	private Bundle m_savedInstanceState;

	private Menu m_menu;
	private ArrayList<Integer> m_menuItems;
	private float m_uiProgress;

	private Lazy<Handler> m_uiHandler = new Lazy<Handler>() {
		@Override
		protected Handler create() {
			return new Handler(Looper.getMainLooper());
		}
	};

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		onDetach_Ads();
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, View contentView) {
		ApplicationActivity activity = (ApplicationActivity) getActivity();

		m_resourceManager = new ResourceManager(activity);
		m_preferences = new Preferences(activity);

		// Create the Ads
		m_adView = AdCommon.onCreate_Ads(activity, contentView);
		activity.requestInvalidateOptionsMenu();

		// Enable drawer by default
		setDrawerEnabled(true);

		return contentView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		ApplicationActivity appActivity = (ApplicationActivity) this.getActivity();
		DrawerAdapter drawer = appActivity.getDrawerAdapter();
		drawer.clear();
		onPopulateDrawer(drawer);
		m_savedInstanceState = savedInstanceState;
	}

	@Override
	public void onResume() {
		super.onResume();
		ApplicationActivity appActivity = (ApplicationActivity) getActivity();
		onFragmentResult(appActivity.getRequestFinishFragmentResult());
		onDataBind(m_savedInstanceState);
		m_savedInstanceState = null;
	}

	protected void onPopulateDrawer(DrawerAdapter drawer) {
		setDrawerEnabled(false);
	}

	protected abstract void onDataBind(Bundle savedInstanceState);

	public void onFragmentResult(Bundle result) {
	}

	public boolean onBackPressed() {
		return true;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	}

	public final boolean onCreateOptionsMenu(Menu menu) {
		m_menu = menu;
		m_menuItems = new ArrayList<Integer>();
		return onCreateOptionsMenu();
	}

	protected boolean onCreateOptionsMenu() {
		return true;
	}

	protected MenuItem createMenuItem(int id, String title) {
		// By default group 1
		MenuItem item = m_menu.add(1, id, Menu.NONE, title);
		m_menuItems.add(id);
		return item;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		return true;
	}

	@Override
	public void onDestroyView() {
		if (m_menu != null) {
			for (Integer item : m_menuItems) {
				m_menu.removeItem(item.intValue());
			}
		}

		super.onDestroyView();
	}

	protected void finish(Bundle result) {
		ApplicationActivity activity = ((ApplicationActivity) getActivity());
		if (activity == null) {
			AppLog.e("ApplicationFragment", "finish - missing activity");
			return;
		}
		
		activity.requestFinishFragment(result);
	}

	protected void startFragment(ApplicationFragment fragment) {
		ApplicationActivity activity = ((ApplicationActivity) getActivity());
		if (activity == null) {
			AppLog.e("ApplicationFragment", "startFragment - missing activity");
			return;
		}
		
		activity.requestFragmentReplace(fragment);
	}

	public ResourceManager getResourceManager() {
		return m_resourceManager;
	}

	public void setTitle(CharSequence title) {
		Activity activity = getActivity();
		if (activity == null) {
			AppLog.e("ApplicationFragment", "setTitle - missing activity");
			return;
		}
		
		activity.setTitle(title);
	}

	public Preferences getPreferences() {
		return m_preferences;
	}

	private void onDetach_Ads() {
		AdCommon.onDestroy_Ad(m_adView);
	}

	private void setDrawerEnabled(boolean enabled) {
		ApplicationActivity activity = ((ApplicationActivity) getActivity());
		if (activity == null) {
			AppLog.e("ApplicationFragment", "setDrawerEnabled - missing activity");
			return;
		}
		
		activity.setDrawerToggleEnabled(enabled);
	}

	/**
	 * Sets value immediately
	 */
	public final void setSupportProgress(float newValue) {
		m_uiProgress = newValue;
		int progress = (int) ((Window.PROGRESS_END - Window.PROGRESS_START) * newValue);

		ApplicationActivity activity = ((ApplicationActivity) getActivity());
		if (activity == null) {
			AppLog.e("ApplicationFragment", "setSupportProgress - missing activity");
			return;
		}
		
		activity.setSupportProgressBarVisibility(true);
		activity.setSupportProgress(progress);
	}

	/**
	 * Sets value in fluid way. Can be run in background thread.
	 */
	public final void updateSupportProgress(final float newValue) {

		if (m_uiProgress == newValue) {
			return;
		}

		final float direction = m_uiProgress > newValue ? -1f : 1f;

		m_uiHandler.getValue().post(new Runnable() {
			@Override
			public void run() {
				ApplicationActivity activity = ((ApplicationActivity) getActivity());
				if (activity == null) {
					AppLog.e("ApplicationFragment", "updateSupportProgress - missing activity");
					return;
				}

				if (m_uiProgress == 1.0f) {
					activity.setSupportProgressBarVisibility(false);

				} else {

					activity.setSupportProgressBarVisibility(true);

					m_uiProgress += 0.05f * direction;

					// Normalize our progress along the progress bar's scale
					int progress = (int) ((Window.PROGRESS_END - Window.PROGRESS_START) * m_uiProgress);

					activity.setSupportProgress(progress);

					if (m_uiProgress < newValue) {
						m_uiHandler.getValue().postDelayed(this, 50);
					} else if (m_uiProgress >= 1.0f) {
						activity.setSupportProgressBarVisibility(false);
					}
				}
			}
		});
	}
}
