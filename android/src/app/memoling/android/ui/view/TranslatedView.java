package app.memoling.android.ui.view;

import app.memoling.android.entity.Word;
import app.memoling.android.helper.Helper;
import app.memoling.android.ui.adapter.IGet;

public class TranslatedView implements IGet<String> {

	private Word m_from;
	private Word m_to;

	public final static String Separator = " : ";

	public TranslatedView(Word from) {
		this(from, new Word(""));
	}

	public TranslatedView(Word from, Word to) {
		m_from = from;
		m_to = to;
	}

	@Override
	public String get(int index) {
		if (index == 0) {
			return Helper.coalesce(m_from.getWord(), "");
		} else {
			return Helper.coalesce(m_to.getWord(), "");
		}
	}

	@Override
	public String toString() {
		if(m_to.getWord().equals("")) {
			return m_from.getWord();	
		} else {
			return m_from.getWord() + Separator + m_to.getWord();			
		}
	}

	@Override
	public boolean equals(Object object) {

		if (object.getClass() != TranslatedView.class) {
			return false;
		}

		TranslatedView tObj = (TranslatedView) object;

		if ((m_from == null && tObj.m_from != null)
				|| (m_to == null && tObj.m_to != null)) {
			return false;
		}

		if (!this.m_from.equals(tObj.m_from) || !this.m_to.equals(tObj.m_to)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		return m_from.hashCode() + m_to.hashCode() ^ 33;
	}
}
