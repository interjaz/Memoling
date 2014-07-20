package app.memoling.android.wordlist.sqlprovider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import app.memoling.android.db.SqliteAdapter;
import app.memoling.android.entity.Language;
import app.memoling.android.entity.Word;

public class WordListAdapter extends SqliteAdapter {

	public WordListAdapter(Context context) throws IOException {
		super(context);
	}

	@SuppressLint("DefaultLocale")
	public List<Word> findWord(Word word, Language language, int limitFrom, int limitTo) {
		SQLiteDatabase db = null;
		List<Word> suggestions = new ArrayList<Word>();
		
		try {
			db = getDatabase();

			String sentence = word.getWord();
			String[] words = sentence.trim().split("\\ ");
			String sentenceNoLastWord = "";
			for(int i=0;i<words.length-1;i++) {
				sentenceNoLastWord += words[i] + " ";
			}
			
			String strWord = words[0].trim().toLowerCase();

			String dbLanguage = getLanguage(language);

			if (dbLanguage.compareTo(Language.Unsupported.toString()) == 0) {
				return suggestions;
			}

			String query = "SELECT `Word` FROM `WordLists`"
					+ " WHERE `LanguageIso639` = ? AND `Word` LIKE ? LIMIT ?,?";
			

			Cursor data = null;

			try {
				data = db.rawQuery(query,
						new String[] { dbLanguage, strWord + "%", Integer.toString(limitFrom), Integer.toString(limitTo) });

				if (!data.moveToFirst()) {
					return suggestions;
				}

				int wordIndex = data.getColumnIndex("Word");

				while (!data.isAfterLast()) {
					suggestions.add(new Word(sentenceNoLastWord + data.getString(wordIndex)));
					data.moveToNext();
				}
			} finally {
				if (data != null) {
					data.close();
				}
			}
			
			
		} finally {
			if (db != null) {
				closeDatabase();
			}
		}

		return suggestions;
	}
	
	public static boolean exists(SQLiteDatabase db, String word, Language language) {
		String dbLanguage = getLanguage(language);

		if (dbLanguage.compareTo(Language.Unsupported.toString()) == 0) {
			return false;
		}

		String query = "SELECT `Word` FROM `WordLists`"
				+ " WHERE `LanguageIso639` = ? AND `Word` = ?";
		

		Cursor data = null;

		try {
			data = db.rawQuery(query,
					new String[] { dbLanguage, word });

			if (!data.moveToFirst()) {
				return false;
			}

			return true;

		} finally {
			if (data != null) {
				data.close();
			}
		}
	}
	
	public boolean exists(String word, Language language) {

		SQLiteDatabase db = null;
		
		try {
			db = getDatabase();
			return exists(db, word, language);
			
		} finally {
			if (db != null) {
				closeDatabase();
			}
		}
	}
	
	public List<String> exists(Collection<String> words, Language language) { 
		
		SQLiteDatabase db = null;
		try {
			db = getDatabase();
			return WordListAdapter.exists(db, words, language);
			
		} finally {
			if(db != null) {
				db.close();
			}
		}
		
	}
	
	public static List<String> exists(SQLiteDatabase db, Collection<String> words, Language language) {
		String dbLanguage = getLanguage(language);

		List<String> foundWords = new ArrayList<String>();
		
		if (dbLanguage.compareTo(Language.Unsupported.toString()) == 0) {
			return foundWords;
		}
		
		Cursor cursor = null;

		String sql = "SELECT `Word` FROM WordLists WHERE `LanguageIso639` = ? AND `Word` IN (%s)";
		StringBuilder arg = new StringBuilder();
		for(String word : words) {
			arg.append(String.format("\"%s\",", word));
		}
		if(arg.length() > 0) {
			arg.setLength(arg.length()-1);
		}
		
		sql = String.format(sql, arg);
		
		try {			
			cursor = db.rawQuery(sql, new String[] { dbLanguage });
			
			while(cursor.moveToNext()) {
				// Performance
				foundWords.add(cursor.getString(0));
			}

		} finally {
			if(cursor != null) {
				cursor.close();
			}
		}
		
		return foundWords;
	}

	private static String getLanguage(Language language) {

		switch (language) {
		case EN:
			return "EN";
		case FR:
			return "FR";
		case ES:
			return "ES";
		case PL:
			return "PL";
		case RU:
			return "RU";
		case IT:
			return "IT";
		case DE:
			return "DE";
		default:
			return Language.Unsupported.toString();
		}

	}

}
