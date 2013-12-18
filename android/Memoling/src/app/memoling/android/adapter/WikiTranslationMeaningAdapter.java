package app.memoling.android.adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import app.memoling.android.db.DatabaseHelper;
import app.memoling.android.db.SqliteAdapter;
import app.memoling.android.entity.WikiTranslationMeaning;
import app.memoling.android.helper.CacheHelper;
import app.memoling.android.wiktionary.WiktionaryDb;

public class WikiTranslationMeaningAdapter extends WiktionaryDb {

	private static int m_cacheSize = 100;
	private static CacheHelper<Integer, WikiTranslationMeaning> m_cache = new CacheHelper<Integer, WikiTranslationMeaning>(m_cacheSize);
	
	public WikiTranslationMeaningAdapter(Context context) {
		super(context);
	}

	public WikiTranslationMeaning get(int meaningId) {

		if (m_cache.containsKey(meaningId)) {
			return m_cache.get(meaningId);
		}
		
		SQLiteDatabase db = null;

		try {
			db = getDatabase();
			return get(this, db, meaningId);

		} finally {
			closeDatabase();
		}
	}

	public static WikiTranslationMeaning get(SqliteAdapter adapter, SQLiteDatabase db, int meaningId) {

		if (m_cache.containsKey(meaningId)) {
			return m_cache.get(meaningId);
		}
		
		String query = "SELECT MeaningId, Meaning " 
				+ "FROM wiki_TranslationMeanings " 
				+ "WHERE MeaningId = " + Integer.toString(meaningId);

		Cursor cursor = db.rawQuery(query, null);

		try {
			if (cursor.moveToNext()) {

				WikiTranslationMeaning translationMeaning = new WikiTranslationMeaning();

				translationMeaning.setMeaningId(DatabaseHelper.getInt(cursor, "MeaningId"));
				translationMeaning.setMeaning(DatabaseHelper.getString(cursor, "Meaning"));

				m_cache.put(meaningId, translationMeaning);
				
				return translationMeaning;
			}
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}

		return null;
	}
	
	public void createIndexes() {

		SQLiteDatabase db = null;

		try {
			db = getDatabase();
			db.execSQL("CREATE INDEX IX_M_MeaningId ON wiki_TranslationMeanings(MeaningId)");

		} finally {
			closeDatabase();
		}

	}
}
