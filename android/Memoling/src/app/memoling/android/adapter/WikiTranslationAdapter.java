package app.memoling.android.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import app.memoling.android.db.DatabaseHelper;
import app.memoling.android.db.SqliteAdapter;
import app.memoling.android.entity.Language;
import app.memoling.android.entity.WikiTranslation;
import app.memoling.android.wiktionary.WiktionaryDb;

public class WikiTranslationAdapter extends WiktionaryDb {

	public WikiTranslationAdapter(Context context) {
		super(context);
	}

	public ArrayList<WikiTranslation> get(String expression, Language languageFrom, Language languageTo) {

		SQLiteDatabase db = null;

		try {
			db = getDatabase();
			return get(this, db, expression, languageFrom, languageTo);

		} finally {
			closeDatabase();
		}
	}

	public static ArrayList<WikiTranslation> get(SqliteAdapter adapter, SQLiteDatabase db, String expression,
			Language languageFrom, Language languageTo) {

		String query = "SELECT ExpressionA, ExpressionB, LanguageA, LanguageB, MeaningId "
				+ "FROM wiki_Translations "
				+ "WHERE (ExpressionA = ? AND LanguageA = ? AND LanguageB = ?) OR (ExpressionB = ? AND LanguageB = ? AND LanguageA = ?)";

		Cursor cursor = db.rawQuery(query, new String[] { expression, languageFrom.getCode(), languageTo.getCode(),
				expression, languageFrom.getCode(), languageTo.getCode() });

		try {

			ArrayList<WikiTranslation> translations = null;

			while (cursor.moveToNext()) {

				if (translations == null) {
					translations = new ArrayList<WikiTranslation>();
				}

				WikiTranslation translation = new WikiTranslation();

				String exprA = DatabaseHelper.getString(cursor, "ExpressionA");
				String exprB = DatabaseHelper.getString(cursor, "ExpressionB");
				Language langA = Language.parse(DatabaseHelper.getString(cursor, "LanguageA"));
				Language langB = Language.parse(DatabaseHelper.getString(cursor, "LanguageB"));

				if (exprA.contains(expression)) {
					translation.setExpressionA(exprA);
					translation.setExpressionB(exprB);
					translation.setLanguageA(langA);
					translation.setLanguageB(langB);
				} else {
					translation.setExpressionA(exprB);
					translation.setExpressionB(exprA);
					translation.setLanguageA(langB);
					translation.setLanguageB(langA);
				}

				translation.setMeaningId(DatabaseHelper.getInt(cursor, "MeaningId"));
				translation.setWikiTranslationMeaning(WikiTranslationMeaningAdapter.get(adapter, db,
						translation.getMeaningId()));

				translations.add(translation);
			}

			return translations;
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}

	}

	public ArrayList<WikiTranslation> get(String expression, Language languageFrom) {

		SQLiteDatabase db = null;

		try {
			db = getDatabase();
			return get(this, db, expression, languageFrom);

		} finally {
			closeDatabase();
		}
	}

	public static ArrayList<WikiTranslation> get(SqliteAdapter adapter, SQLiteDatabase db, String expression,
			Language languageFrom) {

		String query = "SELECT ExpressionA, ExpressionB, LanguageA, LanguageB, MeaningId " + "FROM wiki_Translations "
				+ "WHERE (ExpressionA = ? AND LanguageA = ?)";

		Cursor cursor = db.rawQuery(query,
				new String[] { expression, languageFrom.getCode() });

		try {

			ArrayList<WikiTranslation> translations = null;

			while (cursor.moveToNext()) {

				if (translations == null) {
					translations = new ArrayList<WikiTranslation>();
				}

				WikiTranslation translation = new WikiTranslation();

				String exprA = DatabaseHelper.getString(cursor, "ExpressionA");
				String exprB = DatabaseHelper.getString(cursor, "ExpressionB");
				Language langA = Language.parse(DatabaseHelper.getString(cursor, "LanguageA"));
				Language langB = Language.parse(DatabaseHelper.getString(cursor, "LanguageB"));

				translation.setExpressionA(exprA);
				translation.setExpressionB(exprB);
				translation.setLanguageA(langA);
				translation.setLanguageB(langB);
				
				translation.setMeaningId(DatabaseHelper.getInt(cursor, "MeaningId"));
				translation.setWikiTranslationMeaning(WikiTranslationMeaningAdapter.get(adapter, db,
						translation.getMeaningId()));

				translations.add(translation);
			}

			return translations;
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
			db.execSQL("CREATE INDEX IX_T_ExALgALgB ON wiki_Translations(ExpressionA,LanguageA,LanguageB)");
			db.execSQL("CREATE INDEX IX_T_ExBLgBLgA ON wiki_Translations(ExpressionB,LanguageB,LanguageA)");
			db.execSQL("CREATE INDEX IX_T_ExALgA ON wiki_Translations(ExpressionA,LanguageA)");

		} finally {
			closeDatabase();
		}

	}
}
