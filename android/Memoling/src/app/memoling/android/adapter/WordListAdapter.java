package app.memoling.android.adapter;

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
		SQLiteDatabase db;
		Cursor cursor = null;
		
		try {
			db = getDatabase();

			String query = "SELECT COUNT(1) AS Size " +
					"FROM WordLists " +
					"WHERE LanguageIso639 = ? AND LENGTH(Word) > 5";

			cursor = db.rawQuery(query, new String[] { language.getCode().toUpperCase() });
			
			int size = 0;
			if (cursor.moveToNext()) {
				size = DatabaseHelper.getInt(cursor, "Size");
			}
			
			Random random = new Random();
			int rand = (int)(random.nextFloat() * size);
			
			query = "SELECT Word FROM WordLists " +
					"WHERE LanguageIso639 = ? " +
					"AND LENGTH(Word) > 5 " +
					"LIMIT " + Integer.toString(rand)+ ",1";

			cursor = db.rawQuery(query, new String[] { language.getCode().toUpperCase() });

			if (cursor.moveToNext()) {
				String word = DatabaseHelper.getString(cursor, "Word");
				return word;
			}

			return null;

		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
			
			closeDatabase();
		}
	}

}
