package app.memoling.android.wordlist;

import java.util.ArrayList;

import app.memoling.android.entity.Language;
import app.memoling.android.entity.Word;

public interface IWordsProvider {

	public ArrayList<Word> findWordStartingWith(Word word, Language language, int limitFrom, int limitTo);

	public boolean isSupported(Language language);
}
