package app.memoling.android.adapter;

import java.util.Hashtable;

import app.memoling.android.entity.Language;

public class ThesaurusAdapter {

	private static Hashtable<Language,String> m_library;
	
	static {
		
		m_library = new  Hashtable<Language,String>();
		m_library.put(Language.EN, "http://m.dictionary.com/t/?q=%s");
		m_library.put(Language.DE, "http://www.openthesaurus.de/synonyme/%s");
		m_library.put(Language.ES, "http://www.sinonimos.com/sinonimo.php?palabra=%s");
		m_library.put(Language.FR, "http://www.crisco.unicaen.fr/des/synonymes/%s");
		m_library.put(Language.IT, "http://www.sinonimi-contrari.it/%s/");
		//m_library.put(Language.AR, "");
		//m_library.put(Language.ZH, "");
		m_library.put(Language.PL, "http://synonimy.ux.pl/multimatch.php?word=%s&search=1");
		m_library.put(Language.PT, "http://www.sinonimos.com.br/%s/");
		//m_library.put(Language.NO, "");
		m_library.put(Language.NL, "http://synonym.oooforum.dk/suggestions.php?word=%s&search=1");
		//m_library.put(Language.EL, "");
		m_library.put(Language.RU, "http://synonymonline.ru/C/%s");
		//m_library.put(Language.TR, "");
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
