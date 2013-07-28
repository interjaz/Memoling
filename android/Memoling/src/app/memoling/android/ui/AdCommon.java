package app.memoling.android.ui;

import java.util.Set;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;
import app.memoling.android.Config;
import app.memoling.android.R;
import app.memoling.android.adapter.WordAdapter;
import app.memoling.android.helper.Helper;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.google.ads.mediation.admob.AdMobAdapterExtras;

public class AdCommon {

	public static AdView onCreate_Ads(Activity activity) {
		LinearLayout layout = (LinearLayout) activity.findViewById(R.id.adLayout);
		return onCreate_Ads(activity, layout);
	}

	public static AdView onCreate_Ads(Activity activity, View viewToAttach) {
		LinearLayout layout = (LinearLayout) viewToAttach.findViewById(R.id.adLayout);
		return onCreate_Ads(activity, layout);
	}

	private static AdView onCreate_Ads(Activity activity, LinearLayout layout) {

		// Create the Ads
		AdView adView = new AdView(activity, AdSize.BANNER, Config.AdUnitId);

		// If no ads enabled
		if (!Config.EnableAds) {
			return null;
		}

		float density = activity.getResources().getDisplayMetrics().density;
		// If low res remove ads
		if (density < 1) {
			return null;
		}

		// Create the adView
		adView = new AdView(activity, AdSize.BANNER, Config.AdUnitId);

		if (Helper.hasInternetAccess(activity)) {
			adView.setMinimumHeight((int) (density * 50f));
		}

		// Add the adView to it
		layout.addView(adView);

		// Initiate a generic request to load it with an ad
		AdRequest adRequest = new AdRequest();

		if (Config.Debug) {
			adRequest.addTestDevice(AdRequest.TEST_EMULATOR); // Emulator
			adRequest.addTestDevice(Config.AdTestDeviceId); // Test Android
															// Device
		}

		AdMobAdapterExtras extras = new AdMobAdapterExtras().addExtra("color_bg", "888888")
				.addExtra("color_bg_top", "000000").addExtra("color_border", "000000").addExtra("color_link", "EEEEEE")
				.addExtra("color_text", "FFFFFF").addExtra("color_url", "EEEEEE");
		adRequest.setNetworkExtras(extras);

		Set<String> keywords = new WordAdapter(activity).getAdKeywords();
		adRequest.addKeywords(keywords);

		adView.loadAd(adRequest);

		return adView;
	}

	public static void onDestroy_Ad(AdView adView) {
		if (adView != null) {
			adView.destroy();
		}
	}
}
