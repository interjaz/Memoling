package app.memoling.android.wordlist;

import java.util.List;

import app.memoling.android.entity.Language;
import app.memoling.android.entity.Word;

public interface IWordsProvider {

	public List<Word> findWordStartingWith(Word word, Language language, int limitFrom, int limitTo);

	public boolean isSupported(Language language);
}
