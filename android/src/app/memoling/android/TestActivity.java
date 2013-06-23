package app.memoling.android;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;

import app.memoling.android.R;
import app.memoling.android.adapter.StatisticsAdapter;
import app.memoling.android.ui.fragment.StatisticsTabCombinedFragment;
import app.memoling.android.ui.fragment.StatisticsTabMonthFragment;
import app.memoling.android.ui.fragment.StatisticsTabYearFragment;

public class TestActivity extends FragmentActivity {

	private StatisticsAdapter m_statisticsAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);

		try {
			m_statisticsAdapter = new StatisticsAdapter(this);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager(), this);

		ViewPager pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_test, menu);
		return true;
	}

	private class MyPagerAdapter extends FragmentPagerAdapter {
		private Fragment m_tab1;
		private Fragment m_tab2;
		private Fragment m_tab3;

		public MyPagerAdapter(FragmentManager fm, Context context) {
			super(fm);
			StatisticsTabYearFragment yearFragment = new StatisticsTabYearFragment();
			//yearFragment.setStatisticAdapter(m_statisticsAdapter);
			m_tab1 = yearFragment;

			StatisticsTabMonthFragment monthFragment = new StatisticsTabMonthFragment();
			//monthFragment.setStatisticAdapter(m_statisticsAdapter);
			m_tab2 = monthFragment;

			StatisticsTabCombinedFragment combinedFragment = new StatisticsTabCombinedFragment();
			//combinedFragment.setStatisticAdapter(m_statisticsAdapter);
			m_tab3 = combinedFragment;
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				return m_tab1;
			case 1:
				return m_tab2;
			case 2:
				return m_tab3;
			}

			return null;
		}

		@Override
		public int getCount() {
			return 3;
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
	}
}
