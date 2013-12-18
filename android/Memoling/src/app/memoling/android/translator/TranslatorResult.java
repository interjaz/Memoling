package app.memoling.android.translator;

import java.util.ArrayList;

import app.memoling.android.entity.Language;
import app.memoling.android.entity.Memo;
import app.memoling.android.entity.Word;

public class TranslatorResult {

	public Language From;
	public Language To;
	public ArrayList<Word> Originals;
	public ArrayList<Word> Translated;
	public String Source;
	
	public TranslatorResult() {
		Translated = new ArrayList<Word>();
	}

	public TranslatorResult(Language from,
			Language to, ArrayList<Word> originals, ArrayList<Word> translations, String source) {

		From = from;
		To = to;		
		Originals = originals;
		Translated = translations;
		Source = source;
	}
}
