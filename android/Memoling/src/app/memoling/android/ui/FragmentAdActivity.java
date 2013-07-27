package app.memoling.android.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.ads.AdView;

public class FragmentAdActivity extends FragmentActivity {

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
}
