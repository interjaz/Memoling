package app.memoling.android.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import app.memoling.android.db.DatabaseHelper;
import app.memoling.android.db.SqliteAdapter;
import app.memoling.android.entity.Language;
import app.memoling.android.entity.MemoSentence;
import app.memoling.android.helper.CacheHelper;

public class MemoSentenceAdapter extends SqliteAdapter {

	private static int m_sentenceCacheSize = 20;
	private static CacheHelper<String, List<MemoSentence>> m_sentenceListCache = new CacheHelper<String, List<MemoSentence>>(
			m_sentenceCacheSize);

	public MemoSentenceAdapter(Context context) {
		super(context);
	}

	public List<MemoSentence> getMemoSentences(String memoId, Language from, Language to) {
		SQLiteDatabase db = null;

		String cacheKey = memoId + from.getCode() + to.getCode();
		if (inSync() && m_sentenceListCache.containsKey(cacheKey)) {
			return m_sentenceListCache.get(cacheKey);
		}

		try {
			db = getDatabase();
			return getMemoSentences(this, db, memoId, from, to);
		} finally {
			closeDatabase();
		}
	}

	public static List<MemoSentence> getMemoSentences(SqliteAdapter adapter, SQLiteDatabase db, String memoId,
			Language from, Language to) {

		String cacheKey = memoId + from.getCode() + to.getCode();
		if (!adapter.inSync()) {
			adapter.invalidateLocalCache();
		}

		if (m_sentenceListCache.containsKey(cacheKey)) {
			return m_sentenceListCache.get(cacheKey);
		}

		List<MemoSentence> sentences = new ArrayList<MemoSentence>();
		
		// Look forward
		String sql = "SELECT MemoSentenceId, MemoId, OriginalSentence, OriginalLanguageIso639, TranslatedSentence, TranslatedLanguageIso639 "
				+ "FROM MemoSentences "
				+ "WHERE MemoId = ? "
				+ "AND OriginalLanguageIso639 = ? "
				+ "AND TranslatedLanguageIso639 = ?";

		Cursor cursor = db.rawQuery(sql, new String[] { memoId, from.getCode(), to.getCode() });

		try {
			while (cursor.moveToNext()) {
				MemoSentence sentence = new MemoSentence();

				sentence.setMemoSentenceId(DatabaseHelper.getString(cursor, "MemoSentenceId"));
				sentence.setMemoId(DatabaseHelper.getString(cursor, "MemoId"));

				sentence.setOriginalSentence(DatabaseHelper.getString(cursor, "OriginalSentence"));
				sentence.setTranslatedSentence(DatabaseHelper.getString(cursor, "TranslatedSentence"));

				sentence.setOriginalLanguage(Language.parse(DatabaseHelper.getString(cursor, "OriginalLanguageIso639")));
				sentence.setTranslatedLanguage(Language.parse(DatabaseHelper.getString(cursor,
						"TranslatedLanguageIso639")));

				sentences.add(sentence);
			}

		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}

		m_sentenceListCache.put(cacheKey, sentences);
		return sentences;
	}
	
	public List<MemoSentence> getSentences(String word, Language from, Language to) {
		SQLiteDatabase db = null;

		String cacheKey = word + from.getCode() + to.getCode();
		if (inSync() && m_sentenceListCache.containsKey(cacheKey)) {
			return m_sentenceListCache.get(cacheKey);
		}

		try {
			db = getDatabase();
			return getSentences(this, db, word, from, to);
		} finally {
			closeDatabase();
		}
	}

	public static List<MemoSentence> getSentences(SqliteAdapter adapter, SQLiteDatabase db, String word,
			Language from, Language to) {

		String cacheKey = word + from.getCode() + to.getCode();
		if (!adapter.inSync()) {
			adapter.invalidateLocalCache();
		}

		if (m_sentenceListCache.containsKey(cacheKey)) {
			return m_sentenceListCache.get(cacheKey);
		}

		List<MemoSentence> sentences = new ArrayList<MemoSentence>();
		
		// Look forward
		String sql = "SELECT MemoSentenceId, MemoId, OriginalSentence, OriginalLanguageIso639, TranslatedSentence, TranslatedLanguageIso639 "
				+ "FROM MemoSentences "
				+ "WHERE OriginalSentence LIKE ? "
				+ "AND OriginalLanguageIso639 = ? "
				+ "AND TranslatedLanguageIso639 = ?";

		Cursor cursor = db.rawQuery(sql, new String[] { "%" + word + "%", from.getCode(), to.getCode() });

		try {
			while (cursor.moveToNext()) {
				MemoSentence sentence = new MemoSentence();

				sentence.setMemoSentenceId(DatabaseHelper.getString(cursor, "MemoSentenceId"));
				sentence.setMemoId(DatabaseHelper.getString(cursor, "MemoId"));

				sentence.setOriginalSentence(DatabaseHelper.getString(cursor, "OriginalSentence"));
				sentence.setTranslatedSentence(DatabaseHelper.getString(cursor, "TranslatedSentence"));

				sentence.setOriginalLanguage(Language.parse(DatabaseHelper.getString(cursor, "OriginalLanguageIso639")));
				sentence.setTranslatedLanguage(Language.parse(DatabaseHelper.getString(cursor,
						"TranslatedLanguageIso639")));

				sentences.add(sentence);
			}

		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		
		// Look backward
		sql = "SELECT MemoSentenceId, MemoId, OriginalSentence, OriginalLanguageIso639, TranslatedSentence, TranslatedLanguageIso639 "
				+ "FROM MemoSentences "
				+ "WHERE TranslatedSentence LIKE ? "
				+ "AND OriginalLanguageIso639 = ? "
				+ "AND TranslatedLanguageIso639 = ?";

		cursor = db.rawQuery(sql, new String[] { "%" + word + "%", to.getCode(), from.getCode() });

		try {
			while (cursor.moveToNext()) {
				MemoSentence sentence = new MemoSentence();

				sentence.setMemoSentenceId(DatabaseHelper.getString(cursor, "MemoSentenceId"));
				sentence.setMemoId(DatabaseHelper.getString(cursor, "MemoId"));

				sentence.setTranslatedSentence(DatabaseHelper.getString(cursor, "OriginalSentence"));
				sentence.setOriginalSentence(DatabaseHelper.getString(cursor, "TranslatedSentence"));

				sentence.setTranslatedLanguage(Language.parse(DatabaseHelper.getString(cursor, "OriginalLanguageIso639")));
				sentence.setOriginalLanguage(Language.parse(DatabaseHelper.getString(cursor,
						"TranslatedLanguageIso639")));

				sentences.add(sentence);
			}

		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}

		m_sentenceListCache.put(cacheKey, sentences);

		return sentences;
	}

	public long add(MemoSentence sentence) {
		SQLiteDatabase db = null;
		invalidateGlobalCache();

		try {
			db = getDatabase();

			ContentValues values = createValues(sentence);
			return db.insert("MemoSentences", null, values);
		} finally {
			closeDatabase();
		}
	}

	public void addAll(List<MemoSentence> sentences) {
		SQLiteDatabase db = null;
		invalidateGlobalCache();

		try {
			db = getDatabase();

			for (MemoSentence sentence : sentences) {
				ContentValues values = createValues(sentence);
				db.insert("MemoSentences", null, values);
			}
		} finally {
			closeDatabase();
		}
	}

	private static ContentValues createValues(MemoSentence sentence) {
		ContentValues values = new ContentValues();
		values.put("MemoSentenceId", sentence.getMemoSentenceId());
		values.put("MemoId", sentence.getMemoId());
		values.put("OriginalSentence", sentence.getOriginalSentence());
		values.put("TranslatedSentence", sentence.getTranslatedSentence());
		values.put("OriginalLanguageIso639", sentence.getOriginalLanguage().getCode());
		values.put("TranslatedLanguageIso639", sentence.getTranslatedLanguage().getCode());
		return values;
	}

	@Override
	protected void onInvalidateLocalCache() {
		super.onInvalidateLocalCache();
		m_sentenceListCache.clear();
	}
	
	@Override
	protected void onInvalidateGlobalCache() {
		super.onInvalidateGlobalCache();
		m_sentenceListCache.clear();
	}
}
