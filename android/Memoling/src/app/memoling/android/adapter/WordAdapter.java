package app.memoling.android.adapter;

import java.util.HashSet;
import java.util.Set;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import app.memoling.android.db.DatabaseHelper;
import app.memoling.android.db.SqliteAdapter;
import app.memoling.android.entity.Word;
import app.memoling.android.helper.AppLog;
import app.memoling.android.helper.CacheHelper;

public class WordAdapter extends SqliteAdapter {

	public final static String TableName = "Words";

	private final static int m_wordCacheSize = 20;
	private static CacheHelper<String, Word> m_wordCache = new CacheHelper<String, Word>(m_wordCacheSize);

	public WordAdapter(Context context) {
		super(context);
	}

	public WordAdapter(Context context, boolean persistant) {
		super(context, persistant);
	}

	public long add(Word word) {
		SQLiteDatabase db = null;
		invalidateGlobalCache();

		try {
			db = getDatabase();
			return add(this, db, word);
		} finally {
			closeDatabase();
		}
	}

	public static long add(SqliteAdapter adapter, SQLiteDatabase db, Word word) {
		ContentValues values = createValues(word);
		return db.insert(TableName, null, values);
	}

	public Set<String> getAdKeywords() {
		SQLiteDatabase db = null;
		Cursor cursor = null;
		Set<String> words = new HashSet<String>();

		try {
			db = getDatabase();

			String sql = "SELECT WA.Word AS WordA, WB.Word AS WordB          "
					+ "FROM Memos AS M                                    "
					+ "INNER JOIN Words AS WA ON M.WordAId = WA.WordId    "
					+ "INNER JOIN Words AS WB ON M.WordBId = WB.WordId    "
					+ "ORDER BY M.Created DESC LIMIT 10                   ";

			cursor = db.rawQuery(sql, null);

			while (cursor.moveToNext()) {
				String wordA = DatabaseHelper.getString(cursor, "WordA");
				String wordB = DatabaseHelper.getString(cursor, "WordB");

				words.add(wordA);
				words.add(wordB);
			}

		} catch (Exception ex) {
			AppLog.e("WordAdapter", "getAdKeywords", ex);
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}

			closeDatabase();
		}

		return words;
	}

	public int update(Word word) {
		SQLiteDatabase db = null;
		invalidateGlobalCache();

		try {
			db = getDatabase();
			ContentValues values = createValues(word);
			return db.update(TableName, values, "WordId = ?", new String[] { word.getWordId() });
		} finally {
			closeDatabase();
		}
	}

	public void delete(String wordId) {
		SQLiteDatabase db = null;
		invalidateGlobalCache();

		try {
			db = getDatabase();
			delete(this, db, wordId);
		} finally {
			closeDatabase();
		}
	}

	public static void delete(SqliteAdapter adapter, SQLiteDatabase db, String wordId) {
		db.delete(TableName, "WordId" + "=?", new String[] { wordId });
	}

	private static ContentValues createValues(Word word) {
		ContentValues values = new ContentValues();
		values.put("WordId", word.getWordId());
		values.put("Word", word.getWord());
		values.put("LanguageIso639", word.getLanguage().getCode());
		values.put("Description", word.getDescription());
		return values;
	}

	@Override
	protected void onInvalidateGlobalCache() {
		super.onInvalidateGlobalCache();

		m_wordCache.clear();
	}

	@Override
	protected void onInvalidateLocalCache() {
		super.onInvalidateLocalCache();

		m_wordCache.clear();
	}
}
