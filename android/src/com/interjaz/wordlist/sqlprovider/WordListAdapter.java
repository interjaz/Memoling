package com.interjaz.wordlist.sqlprovider;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.interjaz.Language;
import com.interjaz.db.SqliteAdapter;
import com.interjaz.entity.Word;

public class WordListAdapter extends SqliteAdapter {

	private static final String DatabaseName = "TranslateMemo";
	private static int m_version = 1;

	public WordListAdapter(Context context) throws IOException {
		super(context, DatabaseName, getDatabaseVersion());
	}

	private static int getDatabaseVersion() {
		return m_version;
	}

	public ArrayList<Word> findWord(Word word, Language language) {
		SQLiteDatabase db = null;
		ArrayList<Word> suggestions = new ArrayList<Word>();
		
		try {
			db = getDatabase();

			String sentence = word.getWord();
			String[] words = sentence.trim().split("\\ ");
			String sentenceNoLastWord = "";
			for(int i=0;i<words.length-1;i++) {
				sentenceNoLastWord += words[i] + " ";
			}
			
			String strWord = words[words.length-1];

			String dbLanguage = getLanguage(language);

			if (dbLanguage.compareTo(Language.Unsupported.toString()) == 0) {
				return suggestions;
			}

			String query = "SELECT `Word` FROM `WordLists`"
					+ " WHERE `LanguageIso639` = ? AND `Word` LIKE ? LIMIT 10";
			

			Cursor data = null;
			try {
				data = db.rawQuery(query,
						new String[] { dbLanguage, strWord + "%" });

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
				close();
			}
		}

		return suggestions;
	}

	private static String getLanguage(Language language) {

		switch (language) {
		case EN:
			return "EN";
		case FR:
			return "FR";
		case SPA:
			return "SPA";
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
