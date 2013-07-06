package app.memoling.android.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;
import app.memoling.android.R;
import app.memoling.android.helper.Helper;
import app.memoling.android.helper.Preferences;
import app.memoling.android.ui.GestureAdActivity;
import app.memoling.android.ui.ResourceManager;
import app.memoling.android.ui.control.BouncyLogo;

public class AboutActivity extends GestureAdActivity {

	private final static int LinesOfCode = 18823;
	private final static String AboutActivitySeen = "AboutActivitySeen";

	private ResourceManager m_resources;
	private Preferences m_preferences;

	private TextView m_lblUselessFacts;
	private TextView m_lblVersion;
	private Button m_btnDonate;
	private Button m_btnDonateDelux;
	
	private BouncyLogo m_bouncyLogo;
	
	private static Handler m_uiHandler = new Handler();	
	
	private String m_activityVisited;
	private static final double ToiletInjuriesPerQuaterSecond = 3.1709719837e-3;
	private double m_toiletInjuries = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		onCreate_Ads();

		m_resources = new ResourceManager(this);
		m_preferences = new Preferences(this);
		
		m_activityVisited = m_preferences.get(AboutActivitySeen);
		if (m_activityVisited == null) {
			m_activityVisited = "0";
		}
		m_activityVisited = Integer.toString((Integer.parseInt(m_activityVisited) + 1));
		m_preferences.set(AboutActivitySeen, m_activityVisited);
		
		m_resources.setFont(R.id.textView1, m_resources.getThinFont());
		m_resources.setFont(R.id.textView2, m_resources.getThinFont());
		m_resources.setFont(R.id.textView3, m_resources.getThinFont());
		m_resources.setFont(R.id.about_lblUselessFactsContent, m_resources.getCondensedFont());
		m_resources.setFont(R.id.about_lblVersion, m_resources.getCondensedFont());

		m_lblVersion = (TextView) findViewById(R.id.about_lblVersion);
		m_lblVersion.setText(String.format(getString(R.string.about_version, Helper.getPackage(this).versionName)));
	
		m_lblUselessFacts = (TextView) findViewById(R.id.about_lblUselessFactsContent);
		m_lblUselessFacts.setText(String.format(getString(R.string.about_uselessFactsContent), LinesOfCode, m_activityVisited, m_toiletInjuries));
	
		m_btnDonate = (Button)findViewById(R.id.about_btnDonate);
		m_btnDonateDelux = (Button)findViewById(R.id.about_btnDonateDelux);
		m_resources.setFont(m_btnDonateDelux, m_resources.getCondensedFont());
		
		
		m_bouncyLogo = (BouncyLogo)findViewById(R.id.about_surface);
		
		m_uiHandler.postDelayed(new ToiletUpdate(), 250);
		//m_uiHandler.postDelayed(new BounceLogo(), 10);
		
	}

	private class ToiletUpdate implements Runnable {

		@Override
		public void run() {
			m_toiletInjuries += ToiletInjuriesPerQuaterSecond;
			m_lblUselessFacts.setText(String.format(getString(R.string.about_uselessFactsContent), LinesOfCode, m_activityVisited, m_toiletInjuries));
			m_uiHandler.postDelayed(this, 250);
		}
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_about, menu);
		return true;
	}

	@Override
	public boolean onSwipeRightToLeft() {
		finish();
		return false;
	}
}
