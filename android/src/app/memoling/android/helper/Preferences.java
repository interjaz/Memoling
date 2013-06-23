package app.memoling.android.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Preferences {
	
	private SharedPreferences m_sharedPreferences;
	
	public Preferences(Context context) {
		m_sharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
	}
	
	public String get(String key) {
		return m_sharedPreferences.getString(key, null);
	}
	
	public void set(String key, String value) {
		Editor editor = m_sharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
}
