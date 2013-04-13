package com.interjaz.wordlist;

import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;

import com.interjaz.Language;
import com.interjaz.entity.Word;
import com.interjaz.wordlist.sqlprovider.SqlWordsProvider;

public class WordsFinder {

	private IWordsProvider m_wordProvider;

	public WordsFinder(Context context) {
		try {
			m_wordProvider = new SqlWordsProvider(context);
		} catch (WordsProviderException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
	}

	public void findWordsStartingWith(Word word, Language language,
			IWordsFindComplete onWordsFound) {

		if (m_wordProvider != null) {
			new FindWordsStartingWithAsync(language, onWordsFound)
					.execute(new Word[] { word });
		}
	}

	private class FindWordsStartingWithAsync extends
			AsyncTask<Word, Void, ArrayList<Word>> {
		IWordsFindComplete m_onWordsFound;
		Language m_language;
		Word m_word;

		public FindWordsStartingWithAsync(Language language,
				IWordsFindComplete onWordsFound) {
			m_onWordsFound = onWordsFound;
			m_language = language;
		}

		@Override
		protected ArrayList<Word> doInBackground(Word... word) {
			m_word = word[0];
			if (m_wordProvider.isSupported(m_language)) {
				return m_wordProvider.findWordStartingWith(word[0], m_language);
			} else {
				return new ArrayList<Word>();
			}
		}

		@Override
		protected void onPostExecute(ArrayList<Word> words) {
			super.onPostExecute(words);
			m_onWordsFound.onWordsFindComplete(new WordsFindResult(m_word,
					words));
		}

	}
}
