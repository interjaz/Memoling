package app.memoling.android.ui.view;

import app.memoling.android.entity.Language;
import app.memoling.android.ui.adapter.IGet;

public class LanguageView implements IGet<String> {

	private static LanguageView m_empty;
	
	private Language m_language;
	private String m_name;
	
	private LanguageView() {
		m_name = "";
	}
	
	public LanguageView(Language language) {
		m_language = language;
		m_name = language.getName();
	}
	
	@Override
	public String get(int index) {
		return m_name;
	}
	
	public Language getLanguage() {
		return m_language;
	}
	
	public static final LanguageView empty() {
		if(m_empty != null) {
			return m_empty;
		}
		
		m_empty = new LanguageView();
		return m_empty;
	}
}
