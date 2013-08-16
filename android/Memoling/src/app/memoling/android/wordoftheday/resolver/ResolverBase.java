package app.memoling.android.wordoftheday.resolver;

import java.util.UUID;

import android.annotation.SuppressLint;
import app.memoling.android.entity.Language;
import app.memoling.android.entity.Word;
import app.memoling.android.translator.ITranslateComplete;
import app.memoling.android.translator.Translator;
import app.memoling.android.translator.TranslatorResult;
import app.memoling.android.wordoftheday.IFetchComplete;
import app.memoling.android.wordoftheday.provider.Provider;
import app.memoling.android.wordoftheday.provider.Provider.WordWithDescription;

@SuppressLint("DefaultLocale")
public abstract class ResolverBase {

	protected IFetchComplete m_onFetchComplete;
	protected Provider m_provider;

	private String m_wordFrom;
	private String m_descriptionFrom;
	private String m_wordTo;
	private String m_descriptionTo;

	public ResolverBase(Provider provider) {
		m_provider = provider;
	}

	public final void fetch(IFetchComplete onFetchComplete) {
		m_onFetchComplete = onFetchComplete;
		fetchRaw();
	}

	protected final void onFetchRawComplete(String raw) {
		WordWithDescription wd;

		Object rootRaw = getRootedData(raw);
		if (m_provider.getPostProcessor() != null) {
			wd = m_provider.getPostProcessor().process(rootRaw);
		} else {
			wd = new WordWithDescription(rootRaw.toString(), null);
		}

		if (wd == null) {
			m_onFetchComplete.onFetchComplete(null);
			return;
		}

		if (m_provider.getPreTranslateToLanguage() != null) {
			new Translator(new Word(wd.getWord()), m_provider.getBaseLanguage(),
					m_provider.getPreTranslateToLanguage(), new PreTranslateWordFrom());

			if (wd.getDescription() != null && !wd.getDescription().equals("")) {
				new Translator(new Word(wd.getDescription()), m_provider.getBaseLanguage(),
						m_provider.getPreTranslateToLanguage(), new PreTranslateDescriptionFrom());
			} else {
				m_descriptionFrom = "";				
			}

		} else {
			m_wordFrom = wd.getWord();
			if( wd.getDescription() != null) {
				m_descriptionFrom = wd.getDescription(); 
			} else{
				m_descriptionFrom = "";
			}
		}

		new Translator(new Word(wd.getWord()), m_provider.getBaseLanguage(), m_provider.getTranslateToLanguage(),
				new TranslateWordTo());

		if (wd.getDescription() != null && !wd.getDescription().equals("")) {
			new Translator(new Word(wd.getDescription()), m_provider.getBaseLanguage(),
					m_provider.getTranslateToLanguage(), new TranslateDescriptionTo());
		} else {
			m_descriptionTo = "";
		}
	}

	public class PreTranslateWordFrom implements ITranslateComplete {
		@Override
		public void onTranslateComplete(TranslatorResult result) {
			if (result.TranslatedSuggestions.size() > 0) {
				m_wordFrom = result.TranslatedSuggestions.get(0).getWord();
				m_wordFrom = m_wordFrom.toLowerCase();
			} else {
				m_wordFrom = "";
			}
			onTranslateCompleted();
		}
	}

	public class PreTranslateDescriptionFrom implements ITranslateComplete {
		@Override
		public void onTranslateComplete(TranslatorResult result) {
			if (result.TranslatedSuggestions.size() > 0) {
				m_descriptionFrom = result.TranslatedSuggestions.get(0).getWord();
			} else {
				m_descriptionFrom = "";
			}
			onTranslateCompleted();
		}
	}

	public class TranslateWordTo implements ITranslateComplete {
		@Override
		public void onTranslateComplete(TranslatorResult result) {
			if (result.TranslatedSuggestions.size() > 0) {
				m_wordTo = result.TranslatedSuggestions.get(0).getWord();
				m_wordTo = m_wordTo.toLowerCase();
			} else {
				m_wordTo = "";
			}
			onTranslateCompleted();
		}
	}

	public class TranslateDescriptionTo implements ITranslateComplete {
		@Override
		public void onTranslateComplete(TranslatorResult result) {
			if (result.TranslatedSuggestions.size() > 0) {
				m_descriptionTo = result.TranslatedSuggestions.get(0).getWord();
			} else {
				m_descriptionTo = "";
			}
			onTranslateCompleted();
		}
	}

	private synchronized void onTranslateCompleted() {
		if (m_wordFrom == null) {
			return;
		}

		if (m_descriptionFrom == null) {
			return;
		}

		if (m_wordTo == null) {
			return;
		}

		if (m_descriptionTo == null) {
			return;
		}

		Language langFrom = (m_provider.getPreTranslateToLanguage() != null) ? m_provider.getPreTranslateToLanguage()
				: m_provider.getBaseLanguage();

		Word wordA = new Word(UUID.randomUUID().toString(), m_wordFrom, langFrom);
		wordA.setDescription(m_descriptionFrom);
		Word wordB = new Word(UUID.randomUUID().toString(), m_wordTo, m_provider.getTranslateToLanguage());
		wordB.setDescription(m_descriptionTo);

		MemoOfTheDay memo = new MemoOfTheDay(wordA, wordB, UUID.randomUUID().toString());
		memo.setProviderId(m_provider.getId());

		m_onFetchComplete.onFetchComplete(memo);
	}

	protected abstract void fetchRaw();

	protected abstract Object getRootedData(String raw);
}
