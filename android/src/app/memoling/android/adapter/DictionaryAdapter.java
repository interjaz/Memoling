package app.memoling.android.adapter;

import java.util.Hashtable;

import app.memoling.android.entity.Language;

public class DictionaryAdapter {

	private static Hashtable<Language, String> m_library;

	static {

		m_library = new Hashtable<Language, String>();
		m_library.put(Language.EN, "http://i.word.com/idictionary/%s");
		m_library.put(Language.DE, "http://www.thefreedictionary.com/_/dict.aspx?word=%s");
	}

	public static String get(Language language) {

		if(isSupported(language)) {
			return m_library.get(language);
		}
		
		return null;
	}
	
	public static boolean isSupported(Language language) {
		if(m_library.containsKey(language)) {
			return true;
		}
		
		return false;
	}

}
