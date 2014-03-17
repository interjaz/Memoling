package app.memoling.android.anki.service;

import java.util.ArrayList;

import android.content.Context;
import app.memoling.android.adapter.MemoAdapter.Sort;
import app.memoling.android.anki.AnkiIOEngine;
import app.memoling.android.anki.AnkiImportAdapter;
import app.memoling.android.anki.entity.AnkiCard;
import app.memoling.android.db.DatabaseHelper.Order;
import app.memoling.android.thread.WorkerThread;

public class AnkiImporter {

	public AnkiImporter(final Context context, final String path) {
		// create new worker and execute it to work in background
		new WorkerThread<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {				
				// unpack the file *.apkg
				AnkiIOEngine.unpackFile(path);
				
				String databaseName = AnkiIOEngine.getImportDatabaseName();
				AnkiIOEngine.setImportDatabaseVersion(1);
				int databaseVersion = AnkiIOEngine.getImportDatabaseVersion();
				
				// open imported database
				AnkiImportAdapter ankiImportAdapter = new AnkiImportAdapter(context, databaseName, databaseVersion, true);
				
				final ArrayList<AnkiCard> internalMemos = ankiImportAdapter.getAllAnkiCards(0, Sort.CreatedDate, Order.ASC);
				
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
