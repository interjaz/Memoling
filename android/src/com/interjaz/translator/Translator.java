package com.interjaz.translator;

import com.interjaz.Language;
import com.interjaz.WorkerThread;
import com.interjaz.entity.Word;
import com.interjaz.helper.CacheHelper;
import com.interjaz.translator.service.BingTranslator;

public class Translator implements ITranslateComplete {

	private Language m_from;
	private Language m_to;
	private Word m_word;
	private ITranslateComplete m_onTranslateComplete;

	private final static int MaxCache = 300;
	private static CacheHelper<Word, TranslatorResult> m_cache = new CacheHelper<Word, TranslatorResult>(MaxCache);

	public Translator(final Word word, final Language from, final Language to,
			final ITranslateComplete onTranslateComplete) {

		m_word = new Word(word.getWord().trim());
		m_from = from;
		m_to = to;
		m_onTranslateComplete = onTranslateComplete;
		translate();
	}

	private void translate() {
		boolean cached = false;

		synchronized (m_cache) {
			cached = m_cache.containsKey(m_word);
		}

		if (cached) {
			m_onTranslateComplete.onTranslateComplete(m_cache.get(m_word));
		} else {

			new WorkerThread<Void, Void, Void>() {

				@Override
				protected Void doInBackground(Void... params) {
					new BingTranslator(m_word, m_from, m_to, Translator.this);
					// new FrenglyTranslator(m_word, m_from, m_to, Translator.this);
					return null;
				}

			}.execute();
		}
	}

	@Override
	public void onTranslateComplete(TranslatorResult result) {
		TranslatorResult cached = result;

		if (result.TranslatedSuggestions.size() > 0) {

			synchronized (m_cache) {
				if (m_cache.containsKey(result.AutoCompleteWord)) {
					cached = m_cache.remove(result.AutoCompleteWord);

					for (Word translation : result.TranslatedSuggestions) {
						if (!cached.TranslatedSuggestions.contains(translation)) {
							cached.TranslatedSuggestions.add(translation);
						}
					}
				}

				m_cache.put(result.AutoCompleteWord, cached);
			}
		}

		m_onTranslateComplete.onTranslateComplete(cached);
	}

}
