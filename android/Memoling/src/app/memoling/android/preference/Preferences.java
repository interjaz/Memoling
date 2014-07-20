package app.memoling.android.preference;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import app.memoling.android.facebook.FacebookUser;
import app.memoling.android.helper.AppLog;
import app.memoling.android.preference.custom.MemoListPreference;
import app.memoling.android.preference.custom.TimePreference;
import app.memoling.android.preference.custom.WordOfTheDayTime;

public class Preferences {

	public final static String BTN_SYNC = "BTN_SYNC";
	public final static String BTN_RATE_MEMOLING = "BTN_RATE_MEMOLING";
	public final static String BTN_GOTO_MEMOLING = "BTN_GOTO_MEMOLING";
	public final static String BTN_CLEAR_LANGPREF = "BTN_CLEAR_LANGPREF";
	public final static String BTN_CLEAR_FACEBOOK = "BTN_CLEAR_FACEBOOK";
	public final static String INSTALLED_VERSION = "INSTALLED_VERSION";
	public final static String LANGUAGE_PREFERENCES = "LANGUAGE_PREFERENCES";
	public final static String MEMOLIST_PREFERENCES = "MEMOLIST_PREFERENCES";
	public final static String FACEBOOK_USER = "FACEBOOK_USER";
	public final static String LEARNING_SET_SIZE = "LEARNING_SET_SIZE";
	public final static String ABOUT_FRAGMENT_SEEN = "ABOUT_FRAGMENT_SEEN";
	public final static String LAST_MEMOBASE_ID = "LAST_MEMOBASE_ID";
	public final static String WORDOFTHEDAY_TIME = "WORDOFTHEDAY_TIME";
	public final static String LST_LANGUAGE_ACCENT = "LST_LANGUAGE_ACCENT";
	public final static String BTN_SYNC_FIX  = "BTN_SYNC_FIX";
	public final static String SORT_PREFERENCES = "SORT_PREFERENCES";

	private SharedPreferences m_sharedPreferences;
	private Context m_context;
	
	public Preferences(Context context) {
		m_context = context;
		m_sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
	}

	public Context getContext() {
		return m_context;
	}
	
	private String get(String key) {
		return m_sharedPreferences.getString(key, null);
	}
	
	private boolean get(String key, boolean defValue) {
		return m_sharedPreferences.getBoolean(key, defValue);
	}

	private int get(String key, int defValue) {
		return m_sharedPreferences.getInt(key, defValue);
	}
	
	private void set(String key, String value) {
		Editor editor = m_sharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	private void set(String key, boolean value) {
		Editor editor = m_sharedPreferences.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}
	
	private void set(String key, int value) {
		Editor editor = m_sharedPreferences.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public String getInstalledVersion() {
		return get(INSTALLED_VERSION);
	}

	public void setInstalledVersion(String version) {
		set(INSTALLED_VERSION, version);
	}

	// TODO: Move logic here
	public String getLanguagePreferences() {
		return get(LANGUAGE_PREFERENCES);
	}

	// TODO: Move logic here
	public void setLanguagePreferences(String preferences) {
		set(LANGUAGE_PREFERENCES, preferences);
	}

	public FacebookUser getFacebookUser() {
		String json = get(FACEBOOK_USER);
		if (json == null) {
			return null;
		}

		FacebookUser user;
		try {
			user = new FacebookUser();
			user.deserialize(new JSONObject(json));
		} catch (JSONException ex) {
			AppLog.e("Preferences", "getFacebookUser", ex);
			user = null;
		}
		return user;
	}

	public void setFacebookUser(FacebookUser facebookUser) {
		if (facebookUser == null) {
			set(FACEBOOK_USER, null);
		} else {
			try {
				set(FACEBOOK_USER, facebookUser.serialize());
			} catch (JSONException ex) {
				set(FACEBOOK_USER, null);
				AppLog.e("Preferences", "setFacebookUser", ex);
			}
		}
	}

	public int getAboutSeen() {
		String seen = get(ABOUT_FRAGMENT_SEEN);
		if (seen == null) {
			return 0;
		} else {
			return Integer.valueOf(seen);
		}
	}

	public void setAboutSeen(int seen) {
		set(ABOUT_FRAGMENT_SEEN, Integer.toString(seen));
	}

	public String getLastMemoBaseId() {
		return get(LAST_MEMOBASE_ID);
	}

	public void setLastMemoBaseId(String id) {
		set(LAST_MEMOBASE_ID, id);
	}

	public int getLearningSetSize() {
		String value = get(LEARNING_SET_SIZE);
		if (value == null) {
			return 10;
		}

		return Integer.parseInt(value);
	}

	public MemoListPreference getMemoListPreference(String memoBaseId) {
		try {
			List<MemoListPreference> list = MemoListPreference.deserializeList(get(MEMOLIST_PREFERENCES));

			for (MemoListPreference pref : list) {
				if (pref.getMemoBaseId().equals(memoBaseId)) {
					return pref;
				}
			}

			return null;
		} catch (JSONException ex) {
			AppLog.e("Preferences", "Failed to deserialize MemoListPreferences", ex);
			try {
				set(MEMOLIST_PREFERENCES, MemoListPreference.serializeList(new ArrayList<MemoListPreference>()));
			} catch (JSONException ex2) {
				AppLog.e("Preferences", "Failed to deserialize MemoListPreferences - on clearing", ex2);
			}
			return null;
		}
	}

	public void setMemoListPreference(MemoListPreference preference) {
		try {
			List<MemoListPreference> list = MemoListPreference.deserializeList(get(MEMOLIST_PREFERENCES));

			MemoListPreference found = null;
			for (MemoListPreference pref : list) {
				if (pref.getMemoBaseId().equals(preference.getMemoBaseId())) {
					found = pref;
					break;
				}
			}

			if (found == null) {
				list.add(preference);
			} else {
				found.setLanguageFromCode(preference.getLanguageFromCode());
				found.setLanguageToCode(preference.getLanguageToCode());
				found.setMemoBaseId(preference.getMemoBaseId());
			}

			set(MEMOLIST_PREFERENCES, MemoListPreference.serializeList(list));
		} catch (JSONException ex) {
			AppLog.e("Preferences", "Failed to serialize memolist", ex);
		}
	}

	public WordOfTheDayTime getWordOfTheDayTime() {
		WordOfTheDayTime time = new WordOfTheDayTime();
		String pref = get(WORDOFTHEDAY_TIME);
		if(pref == null) { 
			// TODO: Fix it
			// This should not happen, however sometimes does
			pref = "12:00";
		}
		time.setHours(TimePreference.getHour(pref));
		time.setMinutes(TimePreference.getMinute(pref));

		return time;
	}
	
	public void setLanguageAccent(String value) {
		set(LST_LANGUAGE_ACCENT, value);
	}
	
	public Locale getEnglishAccent() {
		String accent = get(LST_LANGUAGE_ACCENT);
		if(accent != null && accent.equals("1")) {
			return Locale.UK;
		} else {
			return Locale.US;
		}
	}
	
	public void setSyncEnabled(boolean enabled) {
		set(BTN_SYNC, enabled);
	}
	
	public boolean getSyncEnabled() {
		return get(BTN_SYNC, false);
	}
	
	public Integer getSortPreferences() {
		int sort = get(SORT_PREFERENCES, -1);
		if(sort == -1) {
			return null;
		}
		
		return Integer.valueOf(sort);
	}
	
	public void setSortPreferences(Integer sort) {
		set(SORT_PREFERENCES, sort.intValue());
	}
}
