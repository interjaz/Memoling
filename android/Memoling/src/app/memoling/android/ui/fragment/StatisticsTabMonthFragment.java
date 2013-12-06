package app.memoling.android.ui.fragment;

import java.text.DateFormatSymbols;
import java.util.Calendar;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import app.memoling.android.R;
import app.memoling.android.adapter.StatisticsAdapter;
import app.memoling.android.ui.ResourceManager;
import app.memoling.android.ui.control.BarGraph;

public class StatisticsTabMonthFragment extends Fragment {

	private BarGraph m_barMonth;
	private Calendar m_calendar = Calendar.getInstance();
	private ResourceManager m_resources;

	private StatisticsAdapter m_statisticsAdapter;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		m_resources = new ResourceManager(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View contentView = inflater.inflate(R.layout.statistics_tabmonth, container, false);
		m_barMonth = (BarGraph) contentView.findViewById(R.id.statistics_barMonth);

		m_resources.setFont(contentView, R.id.memo_lblLang, m_resources.getLightFont());
		return contentView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		m_statisticsAdapter = new StatisticsAdapter(this.getActivity());
		bindData();
	}

	private void bindData() {
		String[] months = new DateFormatSymbols().getMonths();
		m_barMonth.setTitle(months[m_calendar.get(Calendar.MONTH)]);
		String[] daysLabel = new String[m_calendar.getActualMaximum(Calendar.DAY_OF_MONTH)];
		for (int i = 0; i < daysLabel.length; i++) {
			daysLabel[i] = String.format("%02d", (i + 1));
		}

		m_barMonth.setValues(m_statisticsAdapter.getDailyReviewed());
		m_barMonth.setLabels(daysLabel);
		m_barMonth.bind();
	}
}
