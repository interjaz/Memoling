package app.memoling.android.adapter;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import app.memoling.android.db.DatabaseHelper;
import app.memoling.android.db.SqliteAdapter;

public class QuizletDefinitionExampleAdapter extends SqliteAdapter {

	public QuizletDefinitionExampleAdapter(Context context) {
		super(context);
	}

	public QuizletDefinitionExampleAdapter(Context context, boolean persistant) {
		super(context, persistant);
	}

	public static ArrayList<String> get(SqliteAdapter adapter, SQLiteDatabase db, String quizletDefinitionId) {
		if (!adapter.inSync()) {
			adapter.invalidateLocalCache();
		}

		String query = "SELECT Example " + "FROM QuizletDefinitionExamples " + "WHERE QuizletDefinitionId = ?";

		Cursor cursor = db.rawQuery(query, new String[] { quizletDefinitionId });

		try {
			ArrayList<String> examples = new ArrayList<String>();

			while (cursor.moveToNext()) {

				String example = DatabaseHelper.getString(cursor, "Example");

				examples.add(example);
			}

			return examples;
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
	}

	public static boolean add(SqliteAdapter adapter, SQLiteDatabase db, String quizletDefinitionId,
			ArrayList<String> examples) {
		adapter.invalidateLocalCache();
		
		for (String example : examples) {
			ContentValues values = createValues(quizletDefinitionId, example);
			if(db.insert("QuizletDefinitionExamples", null, values) == -1) {
				return false;
			}
		}
		
		return true;
	}

	private static ContentValues createValues(String quizletDefinitionId, String example) {
		ContentValues values = new ContentValues();

		values.put("QuizletDefinitionId", quizletDefinitionId);
		values.put("Example", example);
		
		return values;
	}
}
