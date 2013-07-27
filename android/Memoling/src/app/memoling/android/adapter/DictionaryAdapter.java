package app.memoling.android.adapter;

import java.util.Hashtable;

import app.memoling.android.entity.Language;

public class DictionaryAdapter {

	private static Hashtable<Language, String> m_library;

	static {

		m_library = new Hashtable<Language, String>();
		m_library.put(Language.EN, "http://i.word.com/idictionary/%s");
		m_library.put(Language.DE, "http://de.thefreedictionary.com/_/dict.aspx?word=%s");
		m_library.put(Language.SPA, "http://es.thefreedictionary.com/_/dict.aspx?word=%s");
		m_library.put(Language.FR, "http://fr.thefreedictionary.com/_/dict.aspx?word=%s");
		m_library.put(Language.IT, "http://it.thefreedictionary.com/_/dict.aspx?word=%s");
		m_library.put(Language.AR, "http://ar.thefreedictionary.com/_/dict.aspx?word=%s");
		m_library.put(Language.ZH, "http://zh.thefreedictionary.com/_/dict.aspx?word=%s");
		m_library.put(Language.PL, "http://pl.thefreedictionary.com/_/dict.aspx?word=%s");
		m_library.put(Language.PT, "http://pt.thefreedictionary.com/_/dict.aspx?word=%s");
		m_library.put(Language.NO, "http://no.thefreedictionary.com/_/dict.aspx?word=%s");
		m_library.put(Language.NL, "http://nl.thefreedictionary.com/_/dict.aspx?word=%s");
		m_library.put(Language.EL, "http://el.thefreedictionary.com/_/dict.aspx?word=%s");
		m_library.put(Language.RU, "http://ru.thefreedictionary.com/_/dict.aspx?word=%s");
		m_library.put(Language.TR, "http://tr.thefreedictionary.com/_/dict.aspx?word=%s");
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
