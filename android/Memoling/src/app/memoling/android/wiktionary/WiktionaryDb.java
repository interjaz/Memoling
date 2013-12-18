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

	public static boolean IsAvailable() {
		return new File(Config.AppPath + "/db/" + WiktionaryDbName + ".sqlite").exists();
	}
}
