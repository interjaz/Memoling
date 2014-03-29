package app.memoling.android.translator;

import java.util.ArrayList;
import java.util.List;

import app.memoling.android.entity.Language;
import app.memoling.android.entity.Word;

public class TranslatorResult {

	public Language From;
	public Language To;
	public List<Word> Originals;
	public List<Word> Translated;
	public String Source;
	
	public TranslatorResult() {
		Translated = new ArrayList<Word>();
	}

	public TranslatorResult(Language from,
			Language to, List<Word> originals, List<Word> translations, String source) {

		From = from;
		To = to;		
		Originals = originals;
		Translated = translations;
		Source = source;
	}
}
