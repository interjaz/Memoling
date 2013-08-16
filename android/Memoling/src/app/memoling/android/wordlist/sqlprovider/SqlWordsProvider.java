package app.memoling.android.wordlist.sqlprovider;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;

import app.memoling.android.entity.Language;
import app.memoling.android.entity.Word;
import app.memoling.android.helper.CacheHelper;
import app.memoling.android.wordlist.IWordsProvider;
import app.memoling.android.wordlist.WordsProviderException;

public final class SqlWordsProvider implements IWordsProvider {

	private final static int MaxCache = 200;
	private static CacheHelper<String, ArrayList<Word>> m_cache = new CacheHelper<String, ArrayList<Word>>(
			MaxCache);

	private WordListAdapter m_wordListAdapter;

	public SqlWordsProvider(Context context) throws WordsProviderException {

		try {
			m_wordListAdapter = new WordListAdapter(context);
		} catch (IOException ex) {
			throw new WordsProviderException(ex);
		}
	}

	@Override
	public ArrayList<Word> findWordStartingWith(Word word, Language language, int limitFrom, int limitTo) {

		ArrayList<Word> words = null;
		String key = word.getWord() + Integer.toString(limitFrom) + Integer.toString(limitTo);

		if (m_cache.containsKey(key)) {
			return m_cache.get(key);
		} else {
			words = m_wordListAdapter.findWord(word, language, limitFrom, limitTo);
			m_cache.put(key, words);
		}

		return words;
	}

	@Override
	public boolean isSupported(Language language) {	

		switch (language) {
		case EN:
			return true;
		case FR:
			return true;
		case ES:
			return true;
		case PL:
			return true;
		case RU:
			return true;
		case IT:
			return true;
		case DE:
			return true;
		default:
			return false;
		}
	}
}
