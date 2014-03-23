package app.memoling.android.anki.service;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import app.memoling.android.adapter.MemoAdapter.Sort;
import app.memoling.android.anki.AnkiIOEngine;
import app.memoling.android.anki.AnkiImportAdapter;
import app.memoling.android.anki.entity.AnkiCard;
import app.memoling.android.anki.entity.AnkiCollection;
import app.memoling.android.anki.entity.AnkiConfiguration;
import app.memoling.android.anki.entity.AnkiDeck;
import app.memoling.android.anki.entity.AnkiGrave;
import app.memoling.android.anki.entity.AnkiIndexStat;
import app.memoling.android.anki.entity.AnkiModel;
import app.memoling.android.anki.entity.AnkiNote;
import app.memoling.android.anki.entity.AnkiReviewLog;
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
				
				final ArrayList<AnkiCard> ankiCards = ankiImportAdapter.getAllAnkiCards(0, Sort.CreatedDate, Order.ASC);
				
				final ArrayList<AnkiNote> ankiNotes = ankiImportAdapter.getAllAnkiNotes(0, Sort.CreatedDate, Order.ASC);
				
				final ArrayList<AnkiReviewLog> ankiReviewLogs = ankiImportAdapter.getAllAnkiReviewLogs(0, Sort.CreatedDate, Order.ASC);
				
				final ArrayList<AnkiGrave> ankiGraves = ankiImportAdapter.getAllAnkiGraves(0, Sort.CreatedDate, Order.ASC);
				
				final ArrayList<AnkiIndexStat> ankiIndexStats = ankiImportAdapter.getAllAnkiIndexStats(0, Sort.CreatedDate, Order.ASC);
				
				final ArrayList<AnkiCollection> ankiCollections = ankiImportAdapter.getAllAnkiCollections(0, Sort.CreatedDate, Order.ASC);
				
				if(!ankiCollections.isEmpty()) {
					// parse 'conf' column
					AnkiConfiguration ankiConfiguration = AnkiCollection.getConfigurationDescription(ankiCollections.get(0).getConfiguration());
					
					// parse 'models' column
//					List<AnkiModel> ankiModels = AnkiCollection.getModelsDescription(ankiCollections.get(0).getModels());
					
					// parse 'decks' column
					List<AnkiDeck> ankiDecks = AnkiCollection.getDecksDescription(ankiCollections.get(0).getDecks());
										
					// parse 'dconf' column
					AnkiConfiguration ankiDefaultConfiguration = AnkiCollection.getConfigurationDescription(ankiCollections.get(0).getDefaultConfiguration());
					
					// parse 'tags' column
				}
				
				// open Memoling database
				
				// create or update Memobases

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
