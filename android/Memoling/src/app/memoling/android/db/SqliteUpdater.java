package app.memoling.android.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import app.memoling.android.Config;
import app.memoling.android.helper.AppLog;

public class SqliteUpdater {

	private static boolean updateSuccessfully = true;
	private static Boolean firstCall = Boolean.TRUE;

	public static boolean updateSuccessfully() {
		return updateSuccessfully;
	}

	public static void update(Context context) {
		try {
			new SqliteProvider(context, Config.DatabaseName, Config.DatabaseVersion).close();
			updateSuccessfully = true;
		} catch (Exception ex) {
			AppLog.e("SqliteUpdater", "update", ex);
			updateSuccessfully = false;
		}
	}

	public static void onUpdate(int expectedVersion, SqliteProvider provider) {

		synchronized (firstCall) {
			if (!firstCall) {
				return;
			}
		}

		SQLiteDatabase database = null;
		try {
			database = provider.getDatabase();
			int version = database.getVersion();
			if (expectedVersion != version) {

				try {
					database.beginTransaction();

					if (version == 0) {
						update01(database);
						database.setVersion(1);
						version = 1;
					}

					if (version == 1) {
						update12(database);
						database.setVersion(2);
						version = 2;
					}

					database.setTransactionSuccessful();
				} catch (Exception ex) {
					AppLog.e("SqliteUpdater", "Failed to update database", ex);
				} finally {
					database.endTransaction();
				}

			}
		} finally {
			firstCall = Boolean.FALSE;
			if(database != null && database.isOpen()) {
				database.close();
			}
		}
	}

	private static void update01(SQLiteDatabase database) {

		String createWordOfTheDay = "CREATE  TABLE \"WordOfTheDay\" "
				+ "(\"WordOfTheDayId\" TEXT PRIMARY KEY  NOT NULL , " + "\"MemoBaseId\" TEXT NOT NULL , "
				+ "\"Mode\" INTEGER NOT NULL , " + "\"ProviderId\" INTEGER NOT NULL , " + "\"PreLanguageFrom\" TEXT, "
				+ "\"LanguageTo\" TEXT NOT NULL );";

		String createSchedules = "CREATE  TABLE \"Schedules\" " + "(\"ScheduleId\" TEXT PRIMARY KEY  NOT NULL , "
				+ "\"MemoBaseId\" TEXT NOT NULL , " + "\"Hours\" INTEGER NOT NULL , "
				+ "\"Minutes\" INTEGER NOT NULL , " + "\"Monday\" BOOL NOT NULL , " + "\"Tuesday\" BOOL NOT NULL , "
				+ "\"Wednesday\" BOOL NOT NULL , " + "\"Thursday\" BOOL NOT NULL , " + "\"Friday\" BOOL NOT NULL , "
				+ "\"Saturday\" BOOL NOT NULL , " + "\"Sunday\" BOOL NOT NULL );";

		database.execSQL(createWordOfTheDay);
		database.execSQL(createSchedules);
	}

	private static void update12(SQLiteDatabase database) {

		String createMemoSenteces = "CREATE TABLE \"MemoSentences\" " +
				"(\"MemoSentenceId\" TEXT PRIMARY KEY  NOT NULL, " +
				"\"MemoId\" TEXT NOT NULL, " +
				"\"OriginalSentence\" TEXT NOT NULL, " +
				"\"OriginalLanguageIso639\" TEXT NOT NULL, " +
				"\"TranslatedSentence\" TEXT NOT NULL, " +
				"\"TranslatedLanguageIso639\" TEXT NOT NULL )";

		String updateWords = "ALTER TABLE Words " + "ADD Description TEXT NOT NULL DEFAULT \"\"";

		database.execSQL(createMemoSenteces);
		database.execSQL(updateWords);
	}
}
