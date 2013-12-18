package app.memoling.android.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import app.memoling.android.db.DatabaseHelper;
import app.memoling.android.db.SqliteAdapter;
import app.memoling.android.entity.Language;
import app.memoling.android.entity.WikiSynonym;
import app.memoling.android.wiktionary.WiktionaryDb;

public class WikiSynonymAdapter extends WiktionaryDb {

	public WikiSynonymAdapter(Context context) {
		super(context);
	}

	public ArrayList<WikiSynonym> get(String expression, Language language) {

		SQLiteDatabase db = null;

		try {
			db = getDatabase();
			return get(this, db, expression, language);

		} finally {
			closeDatabase();
		}
	}

	public static ArrayList<WikiSynonym> get(SqliteAdapter adapter, SQLiteDatabase db, String expression,
			Language language) {

		String query = "SELECT ExpressionA, ExpressionB, Language " + "FROM wiki_Synonyms "
				+ "WHERE ExpressionA = ? AND Language = ?";

		Cursor cursor = db.rawQuery(query,
				new String[] { expression, language.getCode() });

		try {

			ArrayList<WikiSynonym> synonyms = null;

			while (cursor.moveToNext()) {

				if (synonyms == null) {
					synonyms = new ArrayList<WikiSynonym>();
				}

				WikiSynonym synonym = new WikiSynonym();

				String exprA = DatabaseHelper.getString(cursor, "ExpressionA");
				String exprB = DatabaseHelper.getString(cursor, "ExpressionB");

				if (exprA.contains(expression)) {
					synonym.setExpressionA(exprA);
					synonym.setExpressionB(exprB);
				} else {
					synonym.setExpressionA(exprB);
					synonym.setExpressionB(exprA);
				}

				synonym.setLanguage(Language.parse(DatabaseHelper.getString(cursor, "Language")));

				synonyms.add(synonym);
			}

			return synonyms;
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
	}
	
	public void createIndexes() {

		SQLiteDatabase db = null;

		try {
			db = getDatabase();
			db.execSQL("CREATE INDEX IX_S_ExALg ON wiki_Synonyms(ExpressionA,Language)");

		} finally {
			closeDatabase();
		}
		
	}
}
