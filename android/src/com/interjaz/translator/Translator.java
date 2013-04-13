package com.interjaz.translator;

import com.interjaz.Language;
import com.interjaz.entity.Word;
import com.interjaz.helper.CacheHelper;
import com.interjaz.translator.service.BingTranslator;
import com.interjaz.translator.service.FrenglyTranslator;

public class Translator implements ITranslateComplete {

	private Language m_from;
	private Language m_to;
	private Word m_word;
	private ITranslateComplete m_onTranslateComplete;

	private final static int MaxCache = 100;
	private static CacheHelper<Word, TranslatorResult> m_cache = new CacheHelper<Word, TranslatorResult>(
			MaxCache);

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

		synchronized (m_cache) {
			cached = m_cache.containsKey(m_word);
		}

		if (cached) {
			m_onTranslateComplete.onTranslateComplete(m_cache.get(m_word));
		} else {
			new BingTranslator(m_word, m_from, m_to, this);
			//new FrenglyTranslator(m_word, m_from, m_to, this);
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
