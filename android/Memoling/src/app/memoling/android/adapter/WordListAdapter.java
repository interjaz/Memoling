package app.memoling.android.adapter;

import java.util.ArrayList;
import java.util.Random;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import app.memoling.android.db.DatabaseHelper;
import app.memoling.android.db.SqliteAdapter;
import app.memoling.android.entity.Language;

@SuppressLint("DefaultLocale")
public class WordListAdapter extends SqliteAdapter {

	public WordListAdapter(Context context) {
		super(context);
	}

	public String getRandom(Language language) {
		ArrayList<String> words = getRandom(language, 1, 6, Integer.MAX_VALUE);
		if (words.size() == 0) {
			return null;
		}
		return words.get(0);
	}

	public ArrayList<String> getRandom(Language language, int count, int minLength, int maxLength) {
		SQLiteDatabase db;
		Cursor cursor = null;

		try {
			db = getDatabase();
			
			ArrayList<String> words = new ArrayList<String>();

			String query = "SELECT Word FROM WordLists " + "WHERE LanguageIso639 = ? " + " AND LENGTH(Word) >= "
					+ Integer.toString(minLength) + " AND LENGTH(Word) <= " + Integer.toString(maxLength)
					+ " ORDER BY RANDOM() LIMIT " + Integer.toString(count);

			cursor = db.rawQuery(query, new String[] { language.getCode().toUpperCase() });

			while (cursor.moveToNext()) {
				String word = DatabaseHelper.getString(cursor, "Word");
				words.add(word);
			}

			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}

			return words;

		} finally {
			closeDatabase();
		}
	}

}
