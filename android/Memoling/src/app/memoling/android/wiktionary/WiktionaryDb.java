package app.memoling.android.wiktionary;

import java.io.File;

import android.content.Context;
import app.memoling.android.Config;
import app.memoling.android.db.SqliteAdapter;

public class WiktionaryDb extends SqliteAdapter {

	private final static String WiktionaryDbName = "Wiktionary";
	private final static int WiktionaryDbVersion = 0;
	
	public WiktionaryDb(Context context) {
		super(context, WiktionaryDbName, WiktionaryDbVersion, true);
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
		return new File(Config.AppPath + "/db/" + WiktionaryDbName + ".sqlite");
	}
}
