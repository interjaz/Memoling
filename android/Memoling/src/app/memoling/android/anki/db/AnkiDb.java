package app.memoling.android.anki.db;

import java.io.File;

import android.content.Context;
import app.memoling.android.Config;
import app.memoling.android.db.SqliteAdapter;

public class AnkiDb extends SqliteAdapter {

	private static String m_databaseName;
	private static int m_version;
	
	public AnkiDb(Context context, String databaseName, int version, boolean persistant) {
		super(context, databaseName, version, persistant);
		AnkiDb.m_databaseName = databaseName;
		AnkiDb.m_version = version;
	}

	public static boolean isAvailable() {
		return getDbFile().exists();
	}
	
	public static long getSize() {
		return getDbFile().length();
	}
	
	public static boolean delete() {
		return getDbFile().delete();
	}
	
	private static File getDbFile() {
		// TODO name have to be passed
		if(m_version == 0) {
			return new File(Config.AppPath + "/db/" + m_databaseName + ".anki");	
		} else if(m_version == 1) {
			return new File(Config.AppPath + "/db/" + m_databaseName + ".anki2");
		} else {
			// TODO log improper version usage
			return null;	
		}
	}
}
