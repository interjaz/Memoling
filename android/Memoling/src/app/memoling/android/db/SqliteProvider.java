package app.memoling.android.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import app.memoling.android.Config;
import app.memoling.android.helper.AppLog;
import app.memoling.android.helper.Helper;

public class SqliteProvider {

	private SQLiteDatabase m_database;
	private Context m_context;
	private String m_databaseName;
	private String m_databasePath;
	private static final String Tag = SqliteProvider.class.toString();

	public SqliteProvider(Context context, String databaseName, int version) throws IOException {

		m_context = context;
		m_databaseName = databaseName;

		if (!databaseExists()) {
			copyDatabaseFromAssets();
		} else {
			SqliteUpdater.onUpdate(version, this);
		}
	}

	private String getDatabasePath() {
		if (m_databasePath != null) {
			return m_databasePath;
		}

		// Internal storage is not reliable use sdcard instead

		File dbDir = new File(Config.AppPath + "/db");
		if (!dbDir.exists()) {
			dbDir.mkdirs();
		}

		m_databasePath = dbDir.getAbsolutePath() + "/" + m_databaseName + ".sqlite";
		return m_databasePath;
	}

	private boolean databaseExists() {
		SQLiteDatabase database = null;

		try {
			database = SQLiteDatabase.openDatabase(getDatabasePath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
		} catch (SQLiteException ex) {
			AppLog.v(Tag, "DatabaseNotFound ", ex);
		} finally {
			if (database != null) {
				database.close();
			}
		}

		return database != null;
	}

	private void copyDatabaseFromAssets() throws IOException {
		InputStream input = m_context.getAssets().open(m_databaseName + ".sqlite");
		OutputStream output = new FileOutputStream(getDatabasePath());
		Helper.copyFile(input, output);
	}

	public synchronized void close() {
		if (m_database != null) {
			m_database.close();
			m_database = null;
			AppLog.w("DB", "Close");
		}
	}

	// Allows to use earlier API version
	public String getDbName() {
		return m_databaseName;
	}

	public Context getContext() {
		return m_context;
	}
	
	public String createBackup() throws IOException {
		String inputPath = getDatabasePath();
		String outputPath = inputPath + ".bak";
		File outputFile = new File(outputPath); 
		
		if(outputFile.exists()) {
			outputFile.delete();
		}
		
		InputStream input = new FileInputStream(getDatabasePath());
		OutputStream output = new FileOutputStream(outputPath);
		Helper.copyFile(input, output);
		
		return outputPath;
	}

	public synchronized SQLiteDatabase getDatabase() {
		if (m_database != null && !m_database.isOpen()) {
			m_database = null;
		}

		if (m_database == null) {
			m_database = SQLiteDatabase.openDatabase(getDatabasePath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS
					| SQLiteDatabase.OPEN_READWRITE);
			AppLog.w("DB", "Open");
		}

		return m_database;
	}

}
