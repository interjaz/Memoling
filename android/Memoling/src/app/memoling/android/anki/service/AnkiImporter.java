package app.memoling.android.anki.service;

import android.content.Context;
import app.memoling.android.anki.AnkiIOEngine;
import app.memoling.android.anki.AnkiImportAdapter;
import app.memoling.android.thread.WorkerThread;

public class AnkiImporter {

	public AnkiImporter(final Context context, final String path) {
		// create new worker and execute it to work in background
		new WorkerThread<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				String databaseName = AnkiIOEngine.getImportDatabaseName();
				int databaseVersion = AnkiIOEngine.getImportDatabaseVersion();
				
				// unpack the file *.apkg
				AnkiIOEngine.unpackFile(path);
				
				// open imported database
				AnkiImportAdapter adapter = new AnkiImportAdapter(context, databaseName, databaseVersion, true);
				
				
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				// TODO on post execute action
				AnkiIOEngine.onAnkiImportComplete();
			}

		}.execute();
	}
}
