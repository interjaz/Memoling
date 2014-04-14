package app.memoling.android.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import app.memoling.android.db.DatabaseHelper;
import app.memoling.android.db.SqliteAdapter;
import app.memoling.android.entity.QuizletDefinition;
import app.memoling.android.helper.CacheHelper;

public class QuizletDefinitionAdapter extends SqliteAdapter {

	private static int m_definitionListCacheSize = 20;
	private static CacheHelper<String, List<QuizletDefinition>> m_definitionListCache = new CacheHelper<String, List<QuizletDefinition>>(
			m_definitionListCacheSize);

	public QuizletDefinitionAdapter(Context context) {
		super(context);
	}

	public List<QuizletDefinition> get(String word) {

		if (inSync() && m_definitionListCache.containsKey(word)) {
			return m_definitionListCache.get(word);
		}

		SQLiteDatabase db = null;

		try {
			db = getDatabase();
			return get(this, db, word);

		} finally {
			closeDatabase();
		}
	}

	public static List<QuizletDefinition> get(SqliteAdapter adapter, SQLiteDatabase db, String word) {
		if (!adapter.inSync()) {
			adapter.invalidateLocalCache();
		}

		if (m_definitionListCache.containsKey(word)) {
			return m_definitionListCache.get(word);
		}

		String query = "SELECT QuizletDefinitionId, Word, SpeechPart, Definition, IsOfficial "
				+ "FROM QuizletDefinitions " + "WHERE Word = ?";

		Cursor cursor = db.rawQuery(query, new String[] { word });

		try {
			List<QuizletDefinition> definitions = new ArrayList<QuizletDefinition>();

			while (cursor.moveToNext()) {

				QuizletDefinition definition = new QuizletDefinition();
				definition.setQuizletDefinitionId(DatabaseHelper.getString(cursor, "QuizletDefinitionId"));
				definition.setWord(DatabaseHelper.getString(cursor, "Word"));
				definition.setSpeechPart(DatabaseHelper.getString(cursor, "SpeechPart"));
				definition.setDefinition(DatabaseHelper.getString(cursor, "Definition"));
				definition.setIsOfficial(DatabaseHelper.getBoolean(cursor, "IsOfficial"));

				definition.setExamples(QuizletDefinitionExampleAdapter.get(adapter, db,
						definition.getQuizletDefinitionId()));

				definitions.add(definition);
			}

			m_definitionListCache.put(word, definitions);

			return definitions;
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
	}

	public long add(QuizletDefinition definition) {

		SQLiteDatabase db = null;

		try {
			db = getDatabase();
			return add(this, db, definition);

		} finally {
			closeDatabase();
		}
	}

	public static long add(SqliteAdapter adapter, SQLiteDatabase db, QuizletDefinition definition) {

		long position;
		
		try {
			db.beginTransaction();

			ContentValues values = createValues(definition);
			position = db.insert("QuizletDefinitions", null, values);
			if(position == -1) {
				return -1;
			}
			
			boolean examplesAdded = QuizletDefinitionExampleAdapter.add(adapter, db, definition.getQuizletDefinitionId(), definition.getExamples());
			
			if(!examplesAdded) {
				return -1;
			}
			
			db.setTransactionSuccessful();
			return position;
		} finally {
			db.endTransaction();
		}
	}

	private static ContentValues createValues(QuizletDefinition definition) {
		ContentValues values = new ContentValues();

		values.put("QuizletDefinitionId", definition.getQuizletDefinitionId());
		values.put("Definition", definition.getDefinition());
		values.put("SpeechPart", definition.getSpeechPart());
		values.put("Word", definition.getWord());
		values.put("IsOfficial", definition.getIsOfficial());

		return values;
	}

	@Override
	protected void onInvalidateLocalCache() {
		super.onInvalidateLocalCache();
		m_definitionListCache.clear();
	}

	@Override
	protected void onInvalidateGlobalCache() {
		super.onInvalidateGlobalCache();
		m_definitionListCache.clear();
	}
}
