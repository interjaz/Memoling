package com.interjaz.ui.fragment;

import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.interjaz.R;
import com.interjaz.entity.Statistics;
import com.interjaz.entity.StatisticsAdapter;
import com.interjaz.ui.ResourceManager;

public class StatisticsTabCombinedFragment extends Fragment {

	private ResourceManager m_resources;

	private Calendar m_calendar = Calendar.getInstance();

	private StatisticsAdapter m_statisticsAdapter;
	private Statistics m_statistics;

	private TextView m_lblMemos;
	private TextView m_lblLibraries;
	private TextView m_lblPopularLanguage;
	private TextView m_lblTotalRepetitions;
	private TextView m_lblAvgPerformance;
	private TextView m_lblMostRepeated;
	private TextView m_lblMostRepeatedNumber;
	private TextView m_lblLeastRepeated;
	private TextView m_lblLeastRepeatedNumber;
	private TextView m_lblLongest;
	private TextView m_lblShortest;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		m_resources = new ResourceManager(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View contentView = inflater.inflate(R.layout.statistics_tabcombined, container, false);

		m_lblMemos = (TextView) contentView.findViewById(R.id.statistics_lblMemos);
		m_resources.setFont(m_lblMemos, m_resources.getThinFont());
		m_lblLibraries = (TextView) contentView.findViewById(R.id.statistics_lblLibraries);
		m_resources.setFont(m_lblLibraries, m_resources.getThinFont());
		m_lblPopularLanguage = (TextView) contentView.findViewById(R.id.statistics_lblPopularLanguage);
		m_resources.setFont(m_lblPopularLanguage, m_resources.getThinFont());
		m_lblTotalRepetitions = (TextView) contentView.findViewById(R.id.statistics_lblTotalRepetitions);
		m_resources.setFont(m_lblTotalRepetitions, m_resources.getThinFont());
		m_lblAvgPerformance = (TextView) contentView.findViewById(R.id.statistics_lblAvgPerformance);
		m_resources.setFont(m_lblAvgPerformance, m_resources.getThinFont());
		m_lblMostRepeated = (TextView) contentView.findViewById(R.id.statistics_lblMostRepeated);
		m_resources.setFont(m_lblMostRepeated, m_resources.getThinFont());
		m_lblMostRepeatedNumber = (TextView) contentView.findViewById(R.id.statistics_lblMostRepeatedNumber);
		m_resources.setFont(m_lblMostRepeatedNumber, m_resources.getThinFont());
		m_lblLeastRepeated = (TextView) contentView.findViewById(R.id.statistics_lblLeastRepeated);
		m_resources.setFont(m_lblLeastRepeated, m_resources.getThinFont());
		m_lblLeastRepeatedNumber = (TextView) contentView.findViewById(R.id.statistics_lblLeastRepeatedNumber);
		m_resources.setFont(m_lblLeastRepeatedNumber, m_resources.getThinFont());
		m_lblLongest = (TextView) contentView.findViewById(R.id.statistics_lblLongest);
		m_resources.setFont(m_lblLongest, m_resources.getThinFont());
		m_lblShortest = (TextView) contentView.findViewById(R.id.statistics_lblShortest);
		m_resources.setFont(m_lblShortest, m_resources.getThinFont());

		m_resources.setFont(contentView, R.id.textView1, m_resources.getThinFont());
		m_resources.setFont(contentView, R.id.textView2, m_resources.getThinFont());
		m_resources.setFont(contentView, R.id.textView3, m_resources.getThinFont());
		m_resources.setFont(contentView, R.id.textView4, m_resources.getThinFont());
		m_resources.setFont(contentView, R.id.textView5, m_resources.getThinFont());
		m_resources.setFont(contentView, R.id.textView6, m_resources.getThinFont());
		m_resources.setFont(contentView, R.id.textView7, m_resources.getThinFont());
		m_resources.setFont(contentView, R.id.textView8, m_resources.getThinFont());
		m_resources.setFont(contentView, R.id.textView9, m_resources.getThinFont());
		m_resources.setFont(contentView, R.id.textView10, m_resources.getThinFont());
		m_resources.setFont(contentView, R.id.textView11, m_resources.getThinFont());
		m_resources.setFont(contentView, R.id.textView12, m_resources.getThinFont());

		return contentView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (savedInstanceState != null) {
			String strStatistics = savedInstanceState.getString("m_statistics");
			if (strStatistics != null) {
				try {
					m_statistics = new Statistics().deserialize(new JSONObject(strStatistics));
				} catch (JSONException ex) {
					ex.printStackTrace();
				}
			}
		}

		if (m_statistics == null) {
			try {
				m_statisticsAdapter = new StatisticsAdapter(this.getActivity());
				m_statistics = m_statisticsAdapter.getStatitstics();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		bindData();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (m_statistics != null) {
			try {
				outState.putString("m_statistics", m_statistics.serialize().toString());
			} catch (JSONException ex) {
				ex.printStackTrace();
			}
		}
	}

	private void bindData() {

		if (m_statistics == null) {
			return;
		}

		m_lblMemos.setText(Integer.toString(m_statistics.getTotalMemos()));
		m_lblLibraries.setText(Integer.toString(m_statistics.getLibrariesCount()));
		// TODO: make it better II8N
		// m_lblPopularLanguage.setText()
		m_lblTotalRepetitions.setText(Integer.toString(m_statistics.getTotalRepetitions()));
		m_lblAvgPerformance.setText(String.format("%2.2f", m_statistics.getAveragePerformance()));
		m_lblMostRepeated.setText(String.format("%s / %s", m_statistics.getMostRepeatedMemo().getWordA().getWord(),
				m_statistics.getMostRepeatedMemo().getWordB().getWord()));
		m_lblMostRepeatedNumber.setText(Integer.toString(m_statistics.getMostRepeatedMemo().getDisplayed()));
		m_lblLeastRepeated.setText(String.format("%s / %s", m_statistics.getLeastRepeatedMemo().getWordA().getWord(),
				m_statistics.getLeastRepeatedMemo().getWordB().getWord()));
		m_lblLeastRepeatedNumber.setText(Integer.toString(m_statistics.getLeastRepeatedMemo().getDisplayed()));
		m_lblLongest.setText(m_statistics.getLongestWord().getWord());
		m_lblShortest.setText(m_statistics.getShortestWord().getWord());

	}

}
