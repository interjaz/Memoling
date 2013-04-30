package com.interjaz.db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.interjaz.Config;

public class SqliteHelper {

	private SQLiteDatabase m_database;
	private Context m_context;
	private String m_databaseName;
	private String m_databasePath;
	private static final String Tag = SqliteHelper.class.toString();

	public SqliteHelper(Context context, String databaseName, int version) throws IOException {

		m_context = context;
		m_databaseName = databaseName;

		// TODO development copy always database
		// copyDatabase();

		if (!databaseExists()) {
			copyDatabase();
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
			Log.e(Tag, "DatabaseNotFound " + ex.toString());
		} finally {
			if (database != null) {
				database.close();
			}
		}

		return database != null;
	}

	private void copyDatabase() throws IOException {
		InputStream input = m_context.getAssets().open(m_databaseName + ".sqlite");
		OutputStream output = new FileOutputStream(getDatabasePath());
		byte[] buffer = new byte[1024];
		int length;
		while ((length = input.read(buffer)) > 0) {
			output.write(buffer, 0, length);
		}
		output.flush();
		output.close();
		input.close();
	}

	public synchronized void close() {
		if (m_database != null) {
			m_database.close();
			m_database = null;
		}
	}

	// Allows to use earlier API version
	public String getDbName() {
		return m_databaseName;
	}

	public Context getContext() {
		return m_context;
	}

	public synchronized SQLiteDatabase getDatabase() {
		if (m_database == null) {
			m_database = SQLiteDatabase.openDatabase(getDatabasePath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS
					| SQLiteDatabase.OPEN_READWRITE);
		}
		return m_database;
	}

}
