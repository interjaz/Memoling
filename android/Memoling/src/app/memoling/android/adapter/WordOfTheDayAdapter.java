package app.memoling.android.adapter;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import app.memoling.android.db.DatabaseHelper;
import app.memoling.android.db.SqliteAdapter;
import app.memoling.android.entity.Language;
import app.memoling.android.entity.WordOfTheDay;
import app.memoling.android.wordoftheday.WordOfTheDayMode;

public class WordOfTheDayAdapter extends SqliteAdapter {

	public WordOfTheDayAdapter(Context context) {
		super(context);
	}

	public WordOfTheDay getByMemoBaseId(String memoBaseId) {

		SQLiteDatabase db = null;

		try {
			db = getDatabase();
			return getByMemoBaseId(this, db, memoBaseId);

		} finally {
			closeDatabase();
		}
	}

	public static WordOfTheDay getByMemoBaseId(SqliteAdapter adapter, SQLiteDatabase db, String memoBaseId) {

		String query = "SELECT " + "WordOfTheDayId, MemoBaseId, Mode, ProviderId, PreLanguageFrom, LanguageTo "
				+ "FROM WordOfTheDay " + "WHERE MemoBaseId = ?";

		Cursor cursor = db.rawQuery(query, new String[] { memoBaseId });

		if (cursor.moveToNext()) {

			WordOfTheDay word = new WordOfTheDay();
			word.setLanguageTo(Language.parse(DatabaseHelper.getString(cursor, "LanguageTo")));
			word.setMemoBaseId(DatabaseHelper.getString(cursor, "MemoBaseId"));
			word.setMode(WordOfTheDayMode.values()[DatabaseHelper.getInt(cursor, "Mode")]);
			String preLanguage = DatabaseHelper.getString(cursor, "PreLanguageFrom");
			if (preLanguage != null) {
				word.setPreLanguageFrom(Language.parse(preLanguage));
			}
			word.setProviderId(DatabaseHelper.getInt(cursor, "ProviderId"));
			word.setWordOfTheDayId(DatabaseHelper.getString(cursor, "WordOfTheDayId"));

			return word;
		}

		return null;
	}

	public ArrayList<WordOfTheDay> getAll() {
		SQLiteDatabase db = null;

		try {
			db = getDatabase();
			return getAll(this, db);

		} finally {
			closeDatabase();
		}
	}

	public static ArrayList<WordOfTheDay> getAll(SqliteAdapter adapter, SQLiteDatabase db) {

		ArrayList<WordOfTheDay> words = new ArrayList<WordOfTheDay>();

		String query = "SELECT " + "WordOfTheDayId, MemoBaseId, Mode, ProviderId, PreLanguageFrom, LanguageTo "
				+ "FROM WordOfTheDay ";

		Cursor cursor = db.rawQuery(query, null);
		while (cursor.moveToNext()) {

			WordOfTheDay word = new WordOfTheDay();
			word.setLanguageTo(Language.parse(DatabaseHelper.getString(cursor, "LanguageTo")));
			word.setMemoBaseId(DatabaseHelper.getString(cursor, "MemoBaseId"));
			word.setMode(WordOfTheDayMode.values()[DatabaseHelper.getInt(cursor, "Mode")]);
			String preLanguage = DatabaseHelper.getString(cursor, "PreLanguageFrom");
			if (preLanguage != null) {
				word.setPreLanguageFrom(Language.parse(preLanguage));
			}
			word.setProviderId(DatabaseHelper.getInt(cursor, "ProviderId"));
			word.setWordOfTheDayId(DatabaseHelper.getString(cursor, "WordOfTheDayId"));

			words.add(word);
		}

		return words;
	}

	public long add(WordOfTheDay wordOfTheDay) {
		SQLiteDatabase db = null;

		try {
			db = getDatabase();
			return add(this, db, wordOfTheDay);

		} finally {
			closeDatabase();
		}
	}

	public static long add(SqliteAdapter adapter, SQLiteDatabase db, WordOfTheDay wordOfTheDay) {
		ContentValues values = createValues(wordOfTheDay);
		return db.insert("WordOfTheDay", null, values);
	}

	public long update(WordOfTheDay wordOfTheDay) {
		SQLiteDatabase db = null;

		try {
			db = getDatabase();
			return update(this, db, wordOfTheDay);

		} finally {
			closeDatabase();
		}
	}

	public static long update(SqliteAdapter adapter, SQLiteDatabase db, WordOfTheDay wordOfTheDay) {
		ContentValues values = createValues(wordOfTheDay);
		return db.update("WordOfTheDay", values, "WordOfTheDayId = ?",
				new String[] { wordOfTheDay.getWordOfTheDayId() });
	}

	public void delete(String wordOfTheDayId) {
		SQLiteDatabase db = null;

		try {
			db = getDatabase();
			delete(this, db, wordOfTheDayId);

		} finally {
			closeDatabase();
		}
	}

	public static void delete(SqliteAdapter adapter, SQLiteDatabase db, String wordOfTheDayId) {
		db.delete("WordOfTheDay", "WordOfTheDayId" + "=?", new String[] { wordOfTheDayId });
	}

	private static ContentValues createValues(WordOfTheDay word) {
		ContentValues values = new ContentValues();

		values.put("MemoBaseId", word.getMemoBaseId());
		values.put("WordOfTheDayId", word.getWordOfTheDayId());
		values.put("ProviderId", word.getProviderId());
		values.put("LanguageTo", word.getLanguageTo().getCode());
		values.put("Mode", word.getMode().ordinal());
		if (word.getPreLanguageFrom() != null) {
			values.put("PreLanguageFrom", word.getPreLanguageFrom().getCode());
		} else {
			values.putNull("PreLanguageFrom");
		}

		return values;
	}
}
