package app.memoling.android.wordlist;

import java.util.ArrayList;

import app.memoling.android.entity.Word;

public class WordsFindResult {
	
	public Word Searched;
	public ArrayList<Word> Result;
	
	public WordsFindResult(Word word, ArrayList<Word> words) {		
		Searched = word;
		Result = words;
	}
}
