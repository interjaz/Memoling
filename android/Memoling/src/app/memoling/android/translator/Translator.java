package app.memoling.android.translator;

import android.annotation.SuppressLint;
import app.memoling.android.entity.Language;
import app.memoling.android.entity.Word;
import app.memoling.android.helper.CacheHelper;
import app.memoling.android.thread.WorkerThread;
import app.memoling.android.translator.service.BingTranslator;

public class Translator implements ITranslateComplete {

	private Language m_from;
	private Language m_to;
	private Word m_word;
	private ITranslateComplete m_onTranslateComplete;

	private final static int MaxCache = 300;
	private static CacheHelper<String, TranslatorResult> m_cache = new CacheHelper<String, TranslatorResult>(MaxCache);

	public Translator(Word word, Language from, Language to,
			ITranslateComplete onTranslateComplete) {

		m_word = new Word(word.getWord().trim());
		m_from = from;
		m_to = to;
		m_onTranslateComplete = onTranslateComplete;
		translate();
	}

	private void translate() {
		boolean cached = false;
		String cacheKey = m_word.getWord() + m_from.getCode() + m_to.getCode();

		synchronized (m_cache) {
			cached = m_cache.containsKey(cacheKey);
		}

		if (cached) {
			m_onTranslateComplete.onTranslateComplete(m_cache.get(cacheKey));
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
				String cacheKey = m_word.getWord() + m_from.getCode() + m_to.getCode();
				
				if (m_cache.containsKey(cacheKey)) {
					cached = m_cache.remove(cacheKey);

					for (Word translation : result.TranslatedSuggestions) {
						if (!cached.TranslatedSuggestions.contains(translation)) {
							cached.TranslatedSuggestions.add(translation);
						}
					}
				}

				m_cache.put(cacheKey, cached);
			}
		}

		m_onTranslateComplete.onTranslateComplete(cached);
	}

}
