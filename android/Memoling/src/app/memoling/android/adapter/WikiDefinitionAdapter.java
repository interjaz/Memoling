package app.memoling.android.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import app.memoling.android.db.DatabaseHelper;
import app.memoling.android.db.SqliteAdapter;
import app.memoling.android.entity.Language;
import app.memoling.android.entity.WikiDefinition;
import app.memoling.android.wiktionary.WiktionaryDb;

public class WikiDefinitionAdapter extends WiktionaryDb {

	public WikiDefinitionAdapter(Context context) {
		super(context);
	}

	public List<WikiDefinition> get(String expression, Language language) {

		SQLiteDatabase db = null;

		try {
			db = getDatabase();
			return get(this, db, expression, language);

		} finally {
			closeDatabase();
		}
	}

	public static List<WikiDefinition> get(SqliteAdapter adapter, SQLiteDatabase db, String expression, Language language) {

		String query = "SELECT Expression, Language, PartOfSpeech, Definition " + "FROM wiki_Definitions "
				+ "WHERE Expression = ? AND Language = ?";

		Cursor cursor = db.rawQuery(query, new String[] { expression, language.getCode() });

		ArrayList<WikiDefinition> definitions = new ArrayList<WikiDefinition>();
		
		try {
			
			while (cursor.moveToNext()) {

				WikiDefinition definition = new WikiDefinition();

				definition.setExpression(DatabaseHelper.getString(cursor, "Expression"));
				definition.setLanguage(Language.parse(DatabaseHelper.getString(cursor, "Language")));
				definition.setPartOfSpeech(DatabaseHelper.getString(cursor, "PartOfSpeech"));
				definition.setDefinition(DatabaseHelper.getString(cursor, "Definition"));

				definitions.add(definition);
			}
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}

		return definitions;
	}
	
	public void createIndexes() {

		SQLiteDatabase db = null;

		try {
			db = getDatabase();
			db.execSQL("CREATE INDEX IX_D_ExLg ON wiki_Definitions(Expression,Language)");

		} finally {
			closeDatabase();
		}

	}

	public boolean isOk() {

		SQLiteDatabase db = null;

		try {
			db = getDatabase();

			String query = "SELECT  Expression, Language, PartOfSpeech, Definition FROM wiki_Definitions LIMIT 1";

			Cursor cursor = db.rawQuery(query, null);

			try {

				cursor.moveToFirst();

			} finally {
				if (cursor != null && !cursor.isClosed()) {
					cursor.close();
				}
			}

			return true;
		} catch (Exception ex) {
			return false;
		} finally {
			closeDatabase();
		}
	}
}
