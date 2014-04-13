package app.memoling.android.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.view.Window;
import android.widget.Toast;
import app.memoling.android.Config;
import app.memoling.android.R;
import app.memoling.android.adapter.SyncClientAdapter;
import app.memoling.android.entity.SyncClient;
import app.memoling.android.facebook.FacebookUser;
import app.memoling.android.facebook.FacebookWrapper;
import app.memoling.android.facebook.FacebookWrapper.IFacebookGetUserComplete;
import app.memoling.android.preference.IFindPreferenceApi;
import app.memoling.android.preference.Preferences;
import app.memoling.android.preference.custom.TimePreference;
import app.memoling.android.wordoftheday.AlarmReceiver;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.GoogleAnalytics;

@SuppressLint("NewApi")
@SuppressWarnings("deprecation")
public class PreferenceLegacyActivity extends PreferenceActivity implements IFindPreferenceApi {

	private Preferences m_preferences;
	private FacebookWrapper m_facebookWrapper;
	private IFindPreferenceApi m_findPreferenceApi;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);

		GoogleAnalytics.getInstance(this).setDryRun(Config.Debug);
		
		m_facebookWrapper = new FacebookWrapper(this);
		m_facebookWrapper.onCreateView(savedInstanceState);
		
		m_preferences = new Preferences(this);
		
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			initLegacy();
		} else {
			initNew();
		}
	}

	private void initLegacy() {
		addPreferencesFromResource(R.xml.preferences);
		initialize(this);
	}

	private void initNew() {

		setContentView(R.layout.activity_preference_legacy);
		// This is a hack do not use. Use ApplicationFragment when
		// possible
		PreferenceNewFragment fragment = new PreferenceNewFragment();
		android.app.FragmentManager fm = this.getFragmentManager();
		fm.beginTransaction().replace(R.id.preferenceLegacy_layContent, fragment).commit();
	}

	@Override
	public void onStart() {
		super.onStart();
		m_facebookWrapper.onStart();
		EasyTracker.getInstance(this).activityStart(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		m_facebookWrapper.onStop();
		EasyTracker.getInstance(this).activityStop(this);
	}
	
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		m_facebookWrapper.onSaveInstanceState(outState);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		m_facebookWrapper.onActivityResult(requestCode, resultCode, data);
	}




	@SuppressLint("NewApi")
	public static class PreferenceNewFragment extends PreferenceFragment implements IFindPreferenceApi {

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.preferences);
			((PreferenceLegacyActivity)getActivity()).initialize(this);
		}

		@Override
		public Preference findPreferenceApi(CharSequence key) {
			return findPreference(key);
		}
	}

	@Override
	public Preference findPreferenceApi(CharSequence key) {
		return findPreference(key);
	}
	
	private void initialize(IFindPreferenceApi findPreferenceApi) {
		m_findPreferenceApi = findPreferenceApi;
		syncPreference();
		languagePreference();
		facebookPreference();
		rateMemolingPreference();
		visitMemolingPreference();
		wordOfTheDayPreference();
		accentPreference();
	}
	
	private void syncPreference() {

		Preference button = m_findPreferenceApi.findPreferenceApi(Preferences.BTN_SYNC);
		updateSyncPreference();
		
		button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference arg0) {
				toggleSync();
				return true;
			}
		});
	}
	
	private void languagePreference() {
		Preference button = m_findPreferenceApi.findPreferenceApi(Preferences.BTN_CLEAR_LANGPREF);
		button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference arg0) {
				m_preferences.setLanguagePreferences(null);
				return true;
			}
		});
	}

	private void facebookPreference() {
		Preference button = m_findPreferenceApi.findPreferenceApi(Preferences.BTN_CLEAR_FACEBOOK);
		updateFacebookPreference();
		button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference arg0) {
				// Check if logged in
				if (m_preferences.getFacebookUser() == null) {
					m_facebookWrapper.getUser(new IFacebookGetUserComplete() {
						@Override
						public void onGetUserComplete(FacebookUser user) {
							if(user != null) {
								m_preferences.setFacebookUser(user);
								updateFacebookPreference();
							}
						}
					});					
				} else {
					FacebookWrapper.logout();
					m_preferences.setSyncEnabled(false);
					m_preferences.setFacebookUser(null);
					Toast.makeText(m_preferences.getContext(), R.string.preferences_facebookLogout, Toast.LENGTH_SHORT).show();
					updateFacebookPreference();
					updateSyncPreference();
				}
				
				return true;
			}
		});
	}

	private void rateMemolingPreference() {
		Preference button = m_findPreferenceApi.findPreferenceApi(Preferences.BTN_RATE_MEMOLING);
		button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference arg0) {
				String url = "https://play.google.com/store/apps/details?id=app.memoling.android";
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(url));
			    startActivity(intent);
			    
				return true;
			}
		});
	}
	
	private void visitMemolingPreference() {

		Preference button = m_findPreferenceApi.findPreferenceApi(Preferences.BTN_GOTO_MEMOLING);
		button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference arg0) {
				String url = "https://www.facebook.com/pages/Memoling/144784992381757";
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(url));
				startActivity(intent);
				
				return true;
			}
		});
	}
	
	private void wordOfTheDayPreference() {
		Preference wordOfTheDayPref = m_findPreferenceApi.findPreferenceApi(Preferences.WORDOFTHEDAY_TIME);
		wordOfTheDayPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				TimePreference pref = (TimePreference) preference;
				AlarmReceiver.updateAlarm(PreferenceLegacyActivity.this, pref.getLastHour(), pref.getLastMinute());
				return true;
			}
		});
	}
	
	private void accentPreference() {

		Preference languageAccentPref = m_findPreferenceApi.findPreferenceApi(Preferences.LST_LANGUAGE_ACCENT);
		languageAccentPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				m_preferences.setLanguageAccent((String)newValue);
				return true;
			}
		});
	}
	
	private void updateFacebookPreference() {
		Preference button = m_findPreferenceApi.findPreferenceApi(Preferences.BTN_CLEAR_FACEBOOK);
		FacebookUser facebookUser = m_preferences.getFacebookUser();
		
		String summary = null;
		if(facebookUser == null) {
			summary = getString(R.string.preferences_btnClearFacebook_summary);
		} else {
			summary = String.format(getString(R.string.preferences_btnClearFacebook_summary_logout), facebookUser.getName());
		}
		
		button.setSummary(summary);
	}
	
	private void updateSyncPreference() {
		Preference button = m_findPreferenceApi.findPreferenceApi(Preferences.BTN_SYNC);
		String syncSummary = getString(R.string.preferences_btnSync_summary);
		String syncYes = getString(R.string.preferences_btnSync_summary_yes);
		String syncNo = getString(R.string.preferences_btnSync_summary_no);
		syncSummary = String.format(syncSummary, m_preferences.getSyncEnabled() ? syncYes : syncNo);
		button.setSummary(syncSummary);
	}
	
	private void toggleSync() {
		// If enabled - disable
		if(m_preferences.getSyncEnabled()) {
			m_preferences.setSyncEnabled(false);
			updateSyncPreference();
			
			// For testing - delete user
			//SyncClientAdapter clientAdapter = new SyncClientAdapter(this);
			//clientAdapter.delete(clientAdapter.getCurrentSyncClientId());
			//clientAdapter.resetCurrentClient();
			// End testing
			
			return;
		}
		
		// Otherwise
		m_preferences.setSyncEnabled(true);
		
		// Check if SyncClient already exist
		final SyncClientAdapter clientAdapter = new SyncClientAdapter(this);
		String syncClientId = clientAdapter.getCurrentSyncClientId();
		
		if(syncClientId == null) {
			// New create one
			m_facebookWrapper.getUser(new IFacebookGetUserComplete() {

				@Override
				public void onGetUserComplete(FacebookUser user) {
					m_preferences.setFacebookUser(user);
					updateFacebookPreference();
					// Create client
					SyncClient syncClient = SyncClient.newSyncClient(user);
					clientAdapter.insert(syncClient);
					clientAdapter.resetCurrentClient();

					updateSyncPreference();
					SyncActivity.start(PreferenceLegacyActivity.this);
				}
				
			});
			
		} else {
			updateSyncPreference();
			SyncActivity.start(PreferenceLegacyActivity.this);
		}
	}
}
