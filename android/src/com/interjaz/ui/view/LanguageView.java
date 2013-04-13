package com.interjaz.ui.view;

import java.util.ArrayList;

import com.interjaz.IGet;
import com.interjaz.Language;

public class LanguageView implements IGet<String> {

	private Language m_language;
	
	public LanguageView(Language language) {
		m_language = language;
	}
	
	@Override
	public String get(int index) {
		return m_language.getName();
	}
	
	public Language getLanguage() {
		return m_language;
	}
	
	public static ArrayList<LanguageView> getAll() {
		
		ArrayList<LanguageView> languageView = new ArrayList<LanguageView>();
		
		for(Language language : Language.values()) {
			languageView.add(new LanguageView(language));
		}
		
		return languageView;
	}

}
