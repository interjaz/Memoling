package com.interjaz.entity;

import java.io.IOException;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.interjaz.db.SqliteAdapter;
import com.interjaz.helper.CacheHelper;

public class WordAdapter extends SqliteAdapter {

	public final static String DatabaseName = "TranslateMemo";
	public final static int m_databaseVersion = 1;
	public final static String TableName = "Words";

	private final static int m_wordCacheSize = 20;
	private static CacheHelper<String, Word> m_wordCache = new CacheHelper<String, Word>(
			m_wordCacheSize);

	public WordAdapter(Context context) throws IOException {
		super(context, DatabaseName, m_databaseVersion);

	}

	public long add(Word word) {
		SQLiteDatabase db = null;
		invalidateCache();

		try {
			db = getDatabase();
			ContentValues values = createValues(word);
			return db.insert(TableName, null, values);
		} finally {
			if (db != null) {
				close();
			}
		}
	}

	public Word get(String wordId) {
		// TODO not implemented
		return null;
	}

	public ArrayList<Word> getAll() {
		// TODO not implemented
		return null;
	}

	public int update(Word word) {
		SQLiteDatabase db = null;
		invalidateCache();

		try {
			db = getDatabase();
			ContentValues values = createValues(word);
			return db.update(TableName, values, "WordId = ?",
					new String[] { word.getWordId() });
		} finally {
			if (db != null) {
				close();
			}
		}
	}

	public void delete(String wordId) {
		SQLiteDatabase db = null;
		invalidateCache();

		try {
			db = getDatabase();
			db.delete(TableName, "WordId" + "=?", new String[] { wordId });
		} finally {
			if (db != null) {
				close();
			}
		}
	}

	private ContentValues createValues(Word word) {
		ContentValues values = new ContentValues();
		values.put("WordId", word.getWordId());
		values.put("Word", word.getWord());
		values.put("LanguageIso639", word.getLanguage().getCode());
		return values;
	}

	@Override
	protected void onInvalidateCache() {
		super.onInvalidateCache();

		m_wordCache.clear();
	}
}
