package com.interjaz.translator;

import java.util.ArrayList;

import com.interjaz.Language;
import com.interjaz.entity.Word;

public class TranslatorResult {

	public Language From;
	public Language To;
	public Word AutoCompleteWord;
	public ArrayList<Word> TranslatedSuggestions;

	public TranslatorResult() {
		TranslatedSuggestions = new ArrayList<Word>();
	}

	public TranslatorResult(Word word, Language from,
			Language to, ArrayList<Word> translations) {
		AutoCompleteWord = word;
		From = from;
		To = to;
		TranslatedSuggestions = translations;
	}
	
}
