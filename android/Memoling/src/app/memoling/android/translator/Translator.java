package app.memoling.android.translator;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.Context;
import app.memoling.android.entity.Language;
import app.memoling.android.entity.Word;
import app.memoling.android.helper.CacheHelper;
import app.memoling.android.translator.service.BingTranslator;
import app.memoling.android.translator.service.WiktionaryTranslator;

public class Translator implements ITranslatorComplete {

	private Language m_from;
	private Language m_to;
	private Word m_word;
	private WeakReference<Context> m_context;
	private ITranslatorComplete m_onTranslateComplete;
	private IAllTranslatorComplete m_onAllTranslateComplete;
	private AtomicInteger m_translationCompleted;

	private final static int TranslatorsCount = 2;

	private final static int MaxCache = 300;
	private static CacheHelper<String, ArrayList<TranslatorResult>> m_cache = new CacheHelper<String, ArrayList<TranslatorResult>>(
			MaxCache);

	public Translator(Context context, Word word, Language from, Language to, ITranslatorComplete onTranslateComplete) {
		this(context, word, from, to, onTranslateComplete, null);
	}

	public Translator(Context context, Word word, Language from, Language to, ITranslatorComplete onTranslateComplete,
			IAllTranslatorComplete onAllTranslateComplete) {
		this(new WeakReference<Context>(context), word, from, to, onTranslateComplete, onAllTranslateComplete);
	}

	public Translator(WeakReference<Context> context, Word word, Language from, Language to,
			ITranslatorComplete onTranslateComplete, IAllTranslatorComplete onAllTranslateComplete) {

		m_context = context;
		m_word = new Word(word.getWord().trim());
		m_from = from;
		m_to = to;
		m_onTranslateComplete = onTranslateComplete;
		m_onAllTranslateComplete = onAllTranslateComplete;
		m_translationCompleted = new AtomicInteger(0);
		translate();
	}

	private void translate() {
		boolean cached = false;
		String cacheKey = m_word.getWord() + m_from.getCode() + m_to.getCode();

		synchronized (m_cache) {
			cached = m_cache.containsKey(cacheKey);
		}

		if (cached) {
			ArrayList<TranslatorResult> results = m_cache.get(cacheKey);

			if (m_onTranslateComplete != null) {
				for (int i = 0; i < results.size(); i++) {
					m_onTranslateComplete.onTranslatorComplete(results.get(i));
				}
			}
			if (m_onAllTranslateComplete != null) {
				m_onAllTranslateComplete.onAllTranslatorComplete(m_cache.get(cacheKey));
			}
		} else {

			if (m_context.get() != null) {
				new BingTranslator(m_context.get(), m_word, m_from, m_to, Translator.this);
			}

			if (m_context.get() != null) {
				new WiktionaryTranslator(m_context.get(), m_word, m_from, m_to, Translator.this);
			}

		}
	}

	@Override
	public void onTranslatorComplete(TranslatorResult result) {

		ArrayList<TranslatorResult> cachedResult = null;
		String cacheKey = m_word.getWord() + m_from.getCode() + m_to.getCode();

		if (result != null && result.Translated.size() > 0) {
			synchronized (m_cache) {

				if (m_cache.containsKey(cacheKey)) {
					cachedResult = m_cache.get(cacheKey);
				} else {
					cachedResult = new ArrayList<TranslatorResult>();
					m_cache.put(cacheKey, cachedResult);
				}
				
				cachedResult.add(result);
			}
		}

		if (result != null && m_onTranslateComplete != null) {
			m_onTranslateComplete.onTranslatorComplete(result);
		}

		if (m_translationCompleted.incrementAndGet() == TranslatorsCount && m_onAllTranslateComplete != null) {
			// Check if there is other result in cache (could be set by other provider)
			synchronized (m_cache) {
				if (m_cache.containsKey(cacheKey)) {
					cachedResult = m_cache.get(cacheKey);
				}
			}
			
			m_onAllTranslateComplete.onAllTranslatorComplete(cachedResult);
		}
	}

	public static TranslatorResult getMostAccurate(ArrayList<TranslatorResult> translatorResults) {
		if (translatorResults == null) {
			return null;
		}

		for (int i = 0; i < translatorResults.size(); i++) {
			TranslatorResult result = translatorResults.get(i);
			if (result.Source.equals(WiktionaryTranslator.Source)) {
				return result;
			}
		}

		return translatorResults.get(0);
	}
}
