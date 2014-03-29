package app.memoling.android.wordlist;

import java.util.List;

import app.memoling.android.entity.Word;

public class WordsFindResult {
	
	public Word Searched;
	public List<Word> Result;
	
	public WordsFindResult(Word word, List<Word> words) {		
		Searched = word;
		Result = words;
	}
}
