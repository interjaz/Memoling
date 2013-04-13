package com.interjaz.wordlist.sqlprovider;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;

import com.interjaz.Language;
import com.interjaz.entity.Word;
import com.interjaz.helper.CacheHelper;
import com.interjaz.wordlist.IWordsProvider;
import com.interjaz.wordlist.WordsProviderException;

public final class SqlWordsProvider implements IWordsProvider {

	private final static int MaxCache = 20;
	private static CacheHelper<Word, ArrayList<Word>> m_cache = new CacheHelper<Word, ArrayList<Word>>(
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
	public ArrayList<Word> findWordStartingWith(Word word, Language language) {

		ArrayList<Word> words = null;

		if (m_cache.containsKey(word)) {
			words = m_cache.get(word);
		} else {
			words = m_wordListAdapter.findWord(word, language);
			m_cache.put(word, words);
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
		case SPA:
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
