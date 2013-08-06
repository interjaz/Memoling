package app.memoling.android.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import app.memoling.android.Config;
import app.memoling.android.helper.AppLog;

public class SqliteUpdater {

	private static boolean updateSuccessfully = true;

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

	public static void onUpdate(int expectedVersion, SQLiteDatabase database) {

		//if (expectedVersion != database.getVersion()) {

			try {
				database.beginTransaction();

				if (expectedVersion == 1 && database.getVersion() == 0) {
					String createWordOfTheDay = "CREATE  TABLE \"WordOfTheDay\" "
							+ "(\"WordOfTheDayId\" TEXT PRIMARY KEY  NOT NULL , " + "\"MemoBaseId\" TEXT NOT NULL , "
							+ "\"Mode\" INTEGER NOT NULL , " + "\"ProviderId\" INTEGER NOT NULL , "
							+ "\"PreLanguageFrom\" TEXT, " + "\"LanguageTo\" TEXT NOT NULL );";

					String createSchedules = "CREATE  TABLE \"Schedules\" "
							+ "(\"ScheduleId\" TEXT PRIMARY KEY  NOT NULL , " + "\"MemoBaseId\" TEXT NOT NULL , "
							+ "\"Hours\" INTEGER NOT NULL , " + "\"Minutes\" INTEGER NOT NULL , "
							+ "\"Monday\" BOOL NOT NULL , " + "\"Tuesday\" BOOL NOT NULL , "
							+ "\"Wednesday\" BOOL NOT NULL , " + "\"Thursday\" BOOL NOT NULL , "
							+ "\"Friday\" BOOL NOT NULL , " + "\"Saturday\" BOOL NOT NULL , "
							+ "\"Sunday\" BOOL NOT NULL );";


					database.execSQL(createWordOfTheDay);
					database.execSQL(createSchedules);
					
					database.setVersion(1);
				}
				
				database.setTransactionSuccessful();
			} catch (Exception ex) {
				AppLog.e("SqliteUpdater", "Failed to update database", ex);
			} finally {
				database.endTransaction();
			}

		//}

	}
}
