package app.memoling.android.ui.view;

import app.memoling.android.ui.adapter.IGet;
import app.memoling.android.wordoftheday.WordOfTheDayMode;

public class WordOfTheDayModeView implements IGet<String> {

	private String m_name;
	private int m_id;
	
	@Override
	public String get(int index) {
		return m_name;
	}
	
	public int getId() {
		return m_id;
	}
	
	public WordOfTheDayMode getMode() {
		return WordOfTheDayMode.values()[m_id];
	}
	
	public WordOfTheDayModeView(int id, String name) {
		m_id = id;
		m_name = name;
	}

}
