package app.memoling.android.ui;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockActivity;
import com.google.ads.AdView;
import com.google.analytics.tracking.android.EasyTracker;

public class AdActivity extends SherlockActivity {

	private AdView m_adView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	protected void onCreate_Ads() {
		m_adView = AdCommon.onCreate_Ads(this);
	}

	@Override
	protected void onDestroy() {
		AdCommon.onDestroy_Ad(m_adView);
		super.onDestroy();
	}

	@Override
	protected void onStart() {
		super.onStart();
		EasyTracker.getInstance(this).activityStop(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		EasyTracker.getInstance(this).activityStart(this);
	}
	
	
}
