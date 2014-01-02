package app.memoling.android.translator.service;

import java.util.ArrayList;

import android.content.Context;
import app.memoling.android.adapter.WikiTranslationAdapter;
import app.memoling.android.entity.Language;
import app.memoling.android.entity.WikiTranslation;
import app.memoling.android.entity.Word;
import app.memoling.android.thread.WorkerThread;
import app.memoling.android.translator.ITranslatorComplete;
import app.memoling.android.translator.TranslatorResult;
import app.memoling.android.wiktionary.WiktionaryDb;

public class WiktionaryTranslator {

	public final static String Source = "Wiktionary";

	public WiktionaryTranslator(final Context context, final Word word, final Language from, final Language to,
			final ITranslatorComplete onTranslatorResult) {
		
		if(!WiktionaryDb.isAvailable()) {
			return;
		}
		
		new WorkerThread<Void, Void, Void>() {

			private ArrayList<WikiTranslation> m_translations;

			@Override
			protected Void doInBackground(Void... params) {
				
				WikiTranslationAdapter adapter = new WikiTranslationAdapter(context);
				m_translations = adapter.get(word.getWord(), from, to);
				
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);

				if(m_translations == null) {
					onTranslatorResult.onTranslatorComplete(null);
					return;
				}
				
				ArrayList<Word> wordAs = new ArrayList<Word>();
				ArrayList<Word> wordBs = new ArrayList<Word>();
				
				for (WikiTranslation translation : m_translations) {
					Word wordA = new Word();
					wordA.setWord(translation.getExpressionA());
					wordA.setLanguage(translation.getLanguageA());
					wordA.setDescription(translation.getWikiTranslationMeaning().getMeaning());

					Word wordB = new Word();
					wordB.setWord(translation.getExpressionB());
					wordB.setLanguage(translation.getLanguageB());
					
					wordAs.add(wordA);
					wordBs.add(wordB);
				}
				
				TranslatorResult translatorResult = new TranslatorResult(from, to, wordAs, wordBs, Source);
				onTranslatorResult.onTranslatorComplete(translatorResult);

			}

		}.execute();

	}
}
