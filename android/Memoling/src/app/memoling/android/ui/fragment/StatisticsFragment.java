package app.memoling.android.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import app.memoling.android.R;
import app.memoling.android.ui.ApplicationFragment;

import com.actionbarsherlock.view.MenuItem;

public class StatisticsFragment extends ApplicationFragment {

	private final static String TagsArray = "TagsArray";
	private final static int TabSize = 3;
	private String[] m_tags = new String[TabSize];

	private ViewPager m_pager;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View contentView = super.onCreateView(inflater, container, savedInstanceState,
				inflater.inflate(R.layout.fragment_statistics, container, false));
		setTitle(getActivity().getString(R.string.statistics_title));

		if (savedInstanceState != null) {
			m_tags = savedInstanceState.getStringArray(TagsArray);
		}

		final MyPagerAdapter adapter = new MyPagerAdapter(getActivity().getSupportFragmentManager());
		m_pager = (ViewPager) contentView.findViewById(R.id.statistics_pager);
		m_pager.setOffscreenPageLimit(TabSize - 1);
		m_pager.setAdapter(adapter);
		m_pager.setClickable(true);

		
		return contentView;
	}

	@Override
	protected boolean onCreateOptionsMenu() {
		MenuItem item;
		item = createMenuItem(0,"Yearly").setIcon(R.drawable.ic_statistics_365);
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);		
		item = createMenuItem(1,"Monthly").setIcon(R.drawable.ic_statistics_31);
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);		
		item = createMenuItem(2,"Details").setIcon(R.drawable.ic_details);
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);		
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == 0) {
			m_pager.setCurrentItem(0);				
			return false;
		} else if (item.getItemId() == 1) {
			m_pager.setCurrentItem(1);				
			return false;
		} else if (item.getItemId() == 2) {
			m_pager.setCurrentItem(2);				
			return false;
		}
		return true;
	}

	@Override
	public boolean onBackPressed() {

		int item = m_pager.getCurrentItem();
		if(item != 0) {
			m_pager.setCurrentItem(item-1);
		}

		return true;
	}
	
	private class MyPagerAdapter extends FragmentStatePagerAdapter {

		private Fragment[] m_tabs = new Fragment[TabSize];

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {

			if (m_tabs[position] != null) {
				return m_tabs[position];
			}

			Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag(m_tags[position]);

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

	@Override
	protected void onDataBind(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

	}
}
