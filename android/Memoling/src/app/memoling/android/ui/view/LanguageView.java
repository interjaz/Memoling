package app.memoling.android.ui.view;

import android.content.Context;
import app.memoling.android.entity.Language;
import app.memoling.android.ui.adapter.IGet;

public class LanguageView implements IGet<String> {

	private static LanguageView m_empty;
	
	private Language m_language;
	private String m_name;
	
	private LanguageView() {
		m_name = "";
	}
	
	public LanguageView(Language language, Context context) {
		m_language = language;
		m_name = language.getName(context);
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
