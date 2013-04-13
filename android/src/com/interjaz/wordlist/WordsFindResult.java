package com.interjaz.wordlist;

import java.util.ArrayList;

import com.interjaz.entity.Word;

public class WordsFindResult {
	
	public Word Searched;
	public ArrayList<Word> Result;
	
	public WordsFindResult(Word word, ArrayList<Word> words) {		
		Searched = word;
		Result = words;
	}
}
