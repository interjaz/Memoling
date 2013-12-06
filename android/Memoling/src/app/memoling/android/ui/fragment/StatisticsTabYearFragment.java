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

public class StatisticsTabYearFragment extends Fragment {

	private StatisticsAdapter m_statisticsAdapter;
	private Calendar m_calendar = Calendar.getInstance();
	private ResourceManager m_resources;

	private BarGraph m_barYear;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		m_resources = new ResourceManager(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View contentView = inflater.inflate(R.layout.statisitics_tabyear, container, false);
		m_barYear = (BarGraph) contentView.findViewById(R.id.statistics_barYear);

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

		m_barYear.setValues(m_statisticsAdapter.getMonthlyAdded());
		m_barYear.setTitle(Integer.toString(m_calendar.get(Calendar.YEAR)));
		m_barYear.setLabels(months);
		m_barYear.bind();
	}
}
