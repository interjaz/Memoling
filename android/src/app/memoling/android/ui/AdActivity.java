package app.memoling.android.ui;

import android.app.Activity;
import android.os.Bundle;

import com.google.ads.AdView;

public class AdActivity extends Activity {

	private AdView m_adView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	protected void onCreate_Ads() {
		AdCommon.onCreate_Ads(this, m_adView);
	}

	@Override
	protected void onDestroy() {
		AdCommon.onDestroy_Ad(m_adView);
		super.onDestroy();
	}
}
