package app.memoling.android.ui;

import java.util.Set;

import android.app.Activity;
import android.widget.LinearLayout;
import app.memoling.android.Config;
import app.memoling.android.R;
import app.memoling.android.adapter.WordAdapter;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.google.ads.mediation.admob.AdMobAdapterExtras;

public class AdCommon {

	public static void onCreate_Ads(Activity activity, AdView adView) {

		if (!Config.EnableAds) {
			return;
		}

		// Create the adView
		adView = new AdView(activity, AdSize.BANNER, Config.AdUnitId);

		LinearLayout layout = (LinearLayout) activity.findViewById(R.id.adLayout);

		// Add the adView to it
		layout.addView(adView);

		// Initiate a generic request to load it with an ad
		AdRequest adRequest = new AdRequest();

		if (Config.Debug) {
			adRequest.addTestDevice(AdRequest.TEST_EMULATOR); // Emulator
			adRequest.addTestDevice(Config.AdTestDeviceId); // Test Android Device
		}

		AdMobAdapterExtras extras = new AdMobAdapterExtras()
				.addExtra("color_bg", "888888")
				.addExtra("color_bg_top", "000000")
				.addExtra("color_border", "000000")
				.addExtra("color_link", "EEEEEE")
				.addExtra("color_text", "FFFFFF")
				.addExtra("color_url", "EEEEEE");
		adRequest.setNetworkExtras(extras);
		
		Set<String> keywords = new WordAdapter(activity).getAdKeywords();
		adRequest.addKeywords(keywords);
		
		adView.loadAd(adRequest);
	}

	public static void onDestroy_Ad(AdView adView) {
		if (adView != null) {
			adView.destroy();
		}
	}
}
