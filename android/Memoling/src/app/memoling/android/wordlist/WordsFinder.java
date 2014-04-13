package app.memoling.android.wordlist;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import app.memoling.android.entity.Language;
import app.memoling.android.entity.Word;
import app.memoling.android.helper.AppLog;
import app.memoling.android.thread.WorkerThread;
import app.memoling.android.wordlist.sqlprovider.SqlWordsProvider;

public class WordsFinder {

	private IWordsProvider m_wordProvider;

	public WordsFinder(Context context) {
		try {
			m_wordProvider = new SqlWordsProvider(context);
		} catch (WordsProviderException ex) {
			AppLog.e("WordsFinder", "WordsFinder", ex);
		}
	}

	public void findWordsStartingWith(Word word, Language language,
			IWordsFindComplete onWordsFound, int limitFrom, int limitTo) {
		
		if (m_wordProvider != null) {
			new FindWordsStartingWithAsync(language, onWordsFound, limitFrom, limitTo)
					.execute(word);
		}
	}

	private class FindWordsStartingWithAsync extends
			WorkerThread<Word, Void, List<Word>> {
		IWordsFindComplete m_onWordsFound;
		Language m_language;
		Word m_word;
		int m_limitFrom;
		int m_limitTo;

		public FindWordsStartingWithAsync(Language language,
				IWordsFindComplete onWordsFound, int limitFrom, int limitTo) {
			m_onWordsFound = onWordsFound;
			m_language = language;
			m_limitFrom = limitFrom;
			m_limitTo = limitTo;
		}

		@Override
		protected List<Word> doInBackground(Word... word) {
			Thread.currentThread().setName("WordFinderThread");
						
			m_word = word[0];
			if (m_wordProvider.isSupported(m_language)) {
				return m_wordProvider.findWordStartingWith(word[0], m_language, m_limitFrom, m_limitTo);
			} else {
				return new ArrayList<Word>();
			}
		}

		@Override
		protected void onPostExecute(List<Word> words) {
			super.onPostExecute(words);
			m_onWordsFound.onWordsFindComplete(new WordsFindResult(m_word,
					words));
		}

	}
}
