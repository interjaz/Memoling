package app.memoling.android.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
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
		} catch(Exception ex) {
			AppLog.e("SqliteUpdater", "update", ex);
			updateSuccessfully = false;
		}
	}
	
	public static void onUpdate(int expectedVersion, SQLiteDatabase database) {
	
		if(expectedVersion != database.getVersion()) {
			// Update scripts 
		}
		
	}
	
}
