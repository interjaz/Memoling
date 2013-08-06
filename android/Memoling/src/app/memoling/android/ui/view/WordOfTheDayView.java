package app.memoling.android.ui.view;

import app.memoling.android.ui.adapter.IGet;
import app.memoling.android.wordoftheday.provider.Provider;

public class WordOfTheDayView implements IGet<String> {

	private Provider m_provider;
	
	@Override
	public String get(int index) {
		return "(" + m_provider.getBaseLanguage() + ") " + m_provider.getOwner(); 
	}
	
	public Provider getProvider() {
		return m_provider;
	}
	
	public WordOfTheDayView(Provider provider) {
		m_provider = provider;
	}

}
