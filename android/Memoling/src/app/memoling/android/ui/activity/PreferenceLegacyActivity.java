package app.memoling.android.ui.activity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import app.memoling.android.R;
import app.memoling.android.preference.Preferences;
import app.memoling.android.preference.PreferencesCommon;

@SuppressLint("NewApi")
@SuppressWarnings("deprecation")
public class PreferenceLegacyActivity extends PreferenceActivity {

	private PreferencesCommon m_common;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			initLegacy();
		} else {
			initNew();
		}

	}

	private void initLegacy() {
		addPreferencesFromResource(R.xml.preferences);

		m_common = new PreferencesCommon(this);

		Preference button;
		button = (Preference) findPreference(Preferences.BTN_CLEAR_LANGPREF);
		button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference arg0) {
				m_common.clearLanguagePreference();
				return true;
			}
		});

		button = (Preference) findPreference(Preferences.BTN_CLEAR_FACEBOOK);
		button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference arg0) {
				m_common.clearFacebookDetails();
				return true;
			}
		});

		button = (Preference) findPreference(Preferences.BTN_RATE_MEMOLING);
		button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference arg0) {
				m_common.rateMemoling(PreferenceLegacyActivity.this);
				return true;
			}
		});
		
		button = (Preference) findPreference(Preferences.BTN_GOTO_MEMOLING);
		button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference arg0) {
				m_common.goToMemoling(PreferenceLegacyActivity.this);
				return true;
			}
		});

	}

	private void initNew() {

		setContentView(R.layout.activity_preference_legacy);
		// This is a hack do not use. Use ApplicationFragment when
		// possible
		PreferenceNewFragment fragment = new PreferenceNewFragment();
		android.app.FragmentManager fm = this.getFragmentManager();
		fm.beginTransaction().replace(R.id.preferenceLegacy_layContent, fragment).commit();
	}

	@SuppressLint("NewApi")
	public static class PreferenceNewFragment extends PreferenceFragment {

		private PreferencesCommon m_common;

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.preferences);

			m_common = new PreferencesCommon(this.getActivity());

			Preference button;
			button = (Preference) findPreference(Preferences.BTN_CLEAR_LANGPREF);
			button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
				@Override
				public boolean onPreferenceClick(Preference arg0) {
					m_common.clearLanguagePreference();
					return true;
				}
			});

			button = (Preference) findPreference(Preferences.BTN_CLEAR_FACEBOOK);
			button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
				@Override
				public boolean onPreferenceClick(Preference arg0) {
					m_common.clearFacebookDetails();
					return true;
				}
			});

			button = (Preference) findPreference(Preferences.BTN_RATE_MEMOLING);
			button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
				@Override
				public boolean onPreferenceClick(Preference arg0) {
					m_common.rateMemoling(PreferenceNewFragment.this.getActivity());
					return true;
				}
			});
			
			button = (Preference) findPreference(Preferences.BTN_GOTO_MEMOLING);
			button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
				@Override
				public boolean onPreferenceClick(Preference arg0) {
					m_common.goToMemoling(PreferenceNewFragment.this.getActivity());
					return true;
				}
			});
		}

	}
}
