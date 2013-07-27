package app.memoling.android.ui.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import app.memoling.android.R;
import app.memoling.android.helper.Helper;
import app.memoling.android.ui.ApplicationFragment;
import app.memoling.android.ui.ResourceManager;

public class AboutFragment extends ApplicationFragment {

	private final static int LinesOfCode = 26706;

	private TextView m_lblUselessFacts;
	private TextView m_lblVersion;
//	private Button m_btnDonate;
//	private Button m_btnDonateDelux;

	private static Handler m_uiHandler = new Handler();

	private String m_activityVisited;
	private static final double ToiletInjuriesPerQuaterSecond = 3.1709719837e-3;
	private double m_toiletInjuries = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View contentView = super.onCreateView(inflater, container, savedInstanceState,
				inflater.inflate(R.layout.fragment_about, container, false));
		setTitle(getActivity().getString(R.string.about_title));

		ResourceManager resources = getResourceManager();
		Typeface thinFont = resources.getThinFont();
		Typeface condensedFont = resources.getCondensedFont();
		
		int seen = getPreferences().getAboutSeen()+1;
		m_activityVisited = Integer.toString(seen);
		getPreferences().setAboutSeen(seen);

		resources.setFont(contentView, R.id.textView1, thinFont);
		resources.setFont(contentView, R.id.textView2, thinFont);
		resources.setFont(contentView, R.id.textView3, thinFont);
		resources.setFont(contentView, R.id.about_lblUselessFactsContent,
				getResourceManager().getCondensedFont());
		resources.setFont(contentView, R.id.about_lblVersion, condensedFont);

		m_lblVersion = (TextView) contentView.findViewById(R.id.about_lblVersion);
		m_lblVersion.setText(String.format(getString(R.string.about_version),
				Helper.getPackage(getActivity()).versionName));

		m_lblUselessFacts = (TextView) contentView.findViewById(R.id.about_lblUselessFactsContent);
		m_lblUselessFacts.setText(String.format(getString(R.string.about_uselessFactsContent), LinesOfCode,
				m_activityVisited, m_toiletInjuries));

//		m_btnDonate = (Button) contentView.findViewById(R.id.about_btnDonate);
//		m_btnDonateDelux = (Button) contentView.findViewById(R.id.about_btnDonateDelux);
//		resources.setFont(m_btnDonateDelux, condensedFont);

		m_uiHandler.postDelayed(new ToiletUpdate(), 250);
		// m_uiHandler.postDelayed(new BounceLogo(), 10);

		return contentView;
	}

	private class ToiletUpdate implements Runnable {

		@Override
		public void run() {
			if (!AboutFragment.this.isAdded()) {
				return;
			}
			m_toiletInjuries += ToiletInjuriesPerQuaterSecond;
			m_lblUselessFacts.setText(String.format(getString(R.string.about_uselessFactsContent), LinesOfCode,
					m_activityVisited, m_toiletInjuries));
			m_uiHandler.postDelayed(this, 250);
		}

	}

	@Override
	protected void onDataBind(Bundle savedInstanceState) {
	}
}
