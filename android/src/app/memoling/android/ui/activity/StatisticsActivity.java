package app.memoling.android.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;

import app.memoling.android.R;
import app.memoling.android.ui.GestureFragmentActivity;
import app.memoling.android.ui.fragment.StatisticsTabCombinedFragment;
import app.memoling.android.ui.fragment.StatisticsTabMonthFragment;
import app.memoling.android.ui.fragment.StatisticsTabYearFragment;

public class StatisticsActivity extends GestureFragmentActivity {

	private final static String TagsArray = "TagsArray";
	private final static int TabSize = 3;
	private String[] m_tags = new String[TabSize];

	private ViewPager m_pager;
	private int m_previousItem = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_statistics);

		if (savedInstanceState != null) {
			m_tags = savedInstanceState.getStringArray(TagsArray);
		}

		MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());
		m_pager = (ViewPager) findViewById(R.id.statistics_pager);
		m_pager.setOffscreenPageLimit(TabSize - 1);
		m_pager.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_statistics, menu);
		return true;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putStringArray(TagsArray, m_tags);
	}

	@Override
	public boolean onSwipeLeftToRight() {
		final int firstItem = 0;

		if (m_pager.getCurrentItem() == firstItem && m_previousItem == firstItem) {
			finish();
			return true;
		}
		m_previousItem = m_pager.getCurrentItem();

		return false;
	}

	@Override
	public boolean onSwipeRightToLeft() {
		m_previousItem = m_pager.getCurrentItem();
		return false;
	}

	private class MyPagerAdapter extends FragmentPagerAdapter {

		private Fragment[] m_tabs = new Fragment[TabSize];

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {

			if (m_tabs[position] != null) {
				return m_tabs[position];
			}

			Fragment fragment = getSupportFragmentManager().findFragmentByTag(m_tags[position]);

			if (fragment == null) {
				fragment = createFragment(position);
				m_tags[position] = fragment.getTag();
			}

			m_tabs[position] = fragment;
			return fragment;
		}

		@Override
		public int getCount() {
			return TabSize;
		}

		@Override
		public String getPageTitle(int position) {
			switch (position) {
			case 0:
				return "Title 1";
			case 1:
				return "Title 2";
			case 2:
				return "Title 3";
			}

			return null;
		}

		private Fragment createFragment(int position) {

			switch (position) {
			case 0:
				return new StatisticsTabYearFragment();
			case 1:
				return new StatisticsTabMonthFragment();
			case 2:
				return new StatisticsTabCombinedFragment();
			default:
				return null;
			}

		}
	}
}
