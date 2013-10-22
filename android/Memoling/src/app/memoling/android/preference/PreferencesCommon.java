package app.memoling.android.preference;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;
import app.memoling.android.R;
import app.memoling.android.facebook.FacebookWrapper;

public class PreferencesCommon {

	private Preferences m_preferences;

	public PreferencesCommon(Context ctx) {
		m_preferences = new Preferences(ctx);
	}

	public void clearLanguagePreference() {
		m_preferences.setLanguagePreferences(null);
	}

	public void clearFacebookDetails() {
		// Check if logged in
		if (m_preferences.getFacebookUser() == null) {
			Toast.makeText(m_preferences.getContext(), R.string.preferences_facebookLogout, Toast.LENGTH_SHORT).show();
			return;
		}

		FacebookWrapper.logout();
		m_preferences.setFacebookUser(null);

		Toast.makeText(m_preferences.getContext(), R.string.preferences_facebookLogout, Toast.LENGTH_SHORT).show();
	}

	public void rateMemoling(Context context) {
		String url = "https://play.google.com/store/apps/details?id=app.memoling.android";
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(url));
		context.startActivity(intent);
	}

	public void goToMemoling(Context context) {
		String url = "https://www.facebook.com/pages/Memoling/144784992381757";
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(url));
		context.startActivity(intent);
	}
	
	public void setLanguageAccent(String value) {
		m_preferences.setLanguageAccent(value);
	}

}
