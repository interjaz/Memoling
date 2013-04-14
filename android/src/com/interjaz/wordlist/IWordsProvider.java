package com.interjaz.wordlist;

import java.util.ArrayList;

import com.interjaz.Language;
import com.interjaz.entity.Word;

public interface IWordsProvider {

	public ArrayList<Word> findWordStartingWith(Word word, Language language, int limitFrom, int limitTo);

	public boolean isSupported(Language language);
}
