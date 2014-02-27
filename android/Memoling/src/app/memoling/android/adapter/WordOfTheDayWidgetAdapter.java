package app.memoling.android.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import app.memoling.android.db.DatabaseHelper;
import app.memoling.android.db.SqliteAdapter;
import app.memoling.android.entity.WordOfTheDayWidget;

public class WordOfTheDayWidgetAdapter extends SqliteAdapter {

	public WordOfTheDayWidgetAdapter(Context context) {
		super(context);
	}

	public WordOfTheDayWidget get(int wordOfTheDayWidgetId) {
		SQLiteDatabase db = null;

		try {
			db = getDatabase();
			return get(this, db, wordOfTheDayWidgetId);

		} finally {
			closeDatabase();
		}
	}

	public static WordOfTheDayWidget get(SqliteAdapter adapter, SQLiteDatabase db, int wordOfTheDayWidgetId) {

		String query = "SELECT " + "WordOftheDayWidgetId, MemoBaseId " + "FROM WordOfTheDayWidget "
				+ "WHERE WordOfTheDayWidgetId = " + Integer.toString(wordOfTheDayWidgetId);

		Cursor cursor = db.rawQuery(query, null);

		try {
			if (cursor.moveToNext()) {

				WordOfTheDayWidget widget = new WordOfTheDayWidget();
				widget.setWidgetId(DatabaseHelper.getInt(cursor, "WordOfTheDayWidgetId"));
				widget.setMemoBaseId(DatabaseHelper.getString(cursor, "MemoBaseId"));

				return widget;
			}

			return null;
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
	}

	public long add(WordOfTheDayWidget wordOfTheDayWidget) {
		SQLiteDatabase db = null;

		try {
			db = getDatabase();
			return add(this, db, wordOfTheDayWidget);

		} finally {
			closeDatabase();
		}
	}

	public static long add(SqliteAdapter adapter, SQLiteDatabase db, WordOfTheDayWidget wordOfTheDayWidget) {
		ContentValues values = createValues(wordOfTheDayWidget);
		return db.insert("WordOfTheDayWidget", null, values);
	}

	public void delete(int wordOfTheDayWidgetId) {
		SQLiteDatabase db = null;

		try {
			db = getDatabase();
			delete(this, db, wordOfTheDayWidgetId);

		} finally {
			closeDatabase();
		}
	}

	public static void delete(SqliteAdapter adapter, SQLiteDatabase db, int wordOfTheDayWidgetId) {
		db.delete("WordOfTheDayWidget", "wordOfTheDayWidgetId" + "=" + Integer.toString(wordOfTheDayWidgetId), null);
	}

	private static ContentValues createValues(WordOfTheDayWidget wordOfTheDayWidget) {
		ContentValues values = new ContentValues();

		values.put("WordOfTheDayWidgetId", wordOfTheDayWidget.getWidgetId());
		values.put("MemoBaseId", wordOfTheDayWidget.getMemoBaseId());

		return values;
	}

}
