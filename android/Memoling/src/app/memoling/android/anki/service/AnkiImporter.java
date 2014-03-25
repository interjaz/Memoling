package app.memoling.android.anki.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import android.content.Context;
import app.memoling.android.adapter.MemoAdapter.Sort;
import app.memoling.android.adapter.MemoBaseAdapter;
import app.memoling.android.anki.AnkiIOEngine;
import app.memoling.android.anki.AnkiImportAdapter;
import app.memoling.android.anki.entity.AnkiCard;
import app.memoling.android.anki.entity.AnkiCollection;
import app.memoling.android.anki.entity.AnkiConfiguration;
import app.memoling.android.anki.entity.AnkiDeck;
import app.memoling.android.anki.entity.AnkiNote;
import app.memoling.android.db.DatabaseHelper.Order;
import app.memoling.android.entity.Language;
import app.memoling.android.entity.Memo;
import app.memoling.android.entity.MemoBase;
import app.memoling.android.entity.Word;
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
				// all notes from anki
				final ArrayList<AnkiNote> ankiNotes = ankiImportAdapter.getAllAnkiNotes(Sort.CreatedDate, Order.ASC);
				// collections described in the database
				final ArrayList<AnkiCollection> ankiCollections = ankiImportAdapter.getAllAnkiCollections(Sort.CreatedDate, Order.ASC);
				
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
					
					// MemoBase adapter is used to get all MemoBase'es from the database
					MemoBaseAdapter memoBaseAdapter = new MemoBaseAdapter(context);
					List<MemoBase> memoBases = memoBaseAdapter.getAll();

					// current 
					String destinationMemoBaseId;
					MemoBase destinationMemoBase;
					
					// for every anki deck
					for (AnkiDeck ankiDeck : ankiDecks) {
						destinationMemoBaseId = null;
						destinationMemoBase = null;
						// check if there is corresponding memoling deck
						for (MemoBase memoBase : memoBases){
							if(ankiDeck.getName().equals(memoBase.getName())) {
								// if it is then we will be updating the content
								destinationMemoBaseId = memoBase.getMemoBaseId();
								destinationMemoBase = memoBase;
								break;
							}
							// if it is not then we will create new deck	
							createMemoBase(context,ankiDeck);
							destinationMemoBaseId = memoBase.getMemoBaseId();
							destinationMemoBase = memoBase;
						}
						
						// prepare cards for that deck, it will be best if this would be multithreaded and the cards in the list should be removed 
						final ArrayList<AnkiCard> ankiCardsFromAnkiBase = ankiImportAdapter.getAnkiCards(Long.valueOf(destinationMemoBaseId), Sort.CreatedDate, Order.ASC);

						// convert AnkiCard to Memo
						List<Memo> convertedMemos = convertAnkiCardsIntoMemos(ankiCardsFromAnkiBase, ankiNotes, destinationMemoBaseId, destinationMemoBase);
					}
					
					// when all lists of cards for decks are ready then assign it one by one with usage of SupervisedSyncHaltable<Memo> from Import.java
					
					
				}
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
	
	private void createMemoBase(final Context context, AnkiDeck ankiDeck) {
		// there is a need of MemoBase adapter
		MemoBaseAdapter memoBaseAdapter = new MemoBaseAdapter(context);
		// create new MemoBase
		MemoBase newMemoBase = new MemoBase(); 
		// create random MemoBaseId
		String memoBaseId = UUID.randomUUID().toString();
		// by default it is active
		newMemoBase.setActive(true);
		// creation date is now
		newMemoBase.setCreated(new Date());
		// newly created MemoBaseId is used
		newMemoBase.setMemoBaseId(memoBaseId);
		// we take a name from AnkiDeck
		newMemoBase.setName(ankiDeck.getName());
		// add newly created MemoBase to the database
		memoBaseAdapter.add(newMemoBase);		
	}
	
	private List<Memo> convertAnkiCardsIntoMemos(List<AnkiCard> ankiCards, List<AnkiNote> ankiNotes, String memoBaseId, MemoBase memoBase) {
		// create empty list of Memos
		List<Memo> memosFromAnkiDeck = new ArrayList<Memo>();
		
		// for every AnkiCard try to find corresponding one AnkiNote
		for (AnkiCard ankiCard : ankiCards) {	
			// we process only cards that are the first from the pair
			if(ankiCard.getOrd() != 0){
				break;
			}
			
			AnkiNote tmpAnkiNote = null;
			// TODO change this maybe to map: m_NoteId => AnkiNote
			for (AnkiNote ankiNote : ankiNotes) {
				if(ankiCard.getNoteId() == ankiNote.getNoteId()) {
					tmpAnkiNote = ankiNote;
					break;
				}
			}
			// check if there was AnkiNote found
			if(tmpAnkiNote == null) {
				break;
			}
			
			// there is a need to cut out the initial wordB from wordA, strange
			tmpAnkiNote.getFlds();
			String ankiWordA = "";
			String ankiWordB = tmpAnkiNote.getSfld();
			
			// in Anki base there is no information about input language
			// we will ask user for going through short questions
			Word wordA = new Word(UUID.randomUUID().toString(), ankiWordA, Language.DE);
			Word wordB = new Word(UUID.randomUUID().toString(), ankiWordB, Language.PL);
			
			// create new memo, there are some problems with converting information
			Memo newMemo = new Memo(wordA, wordB, memoBaseId);
			
			// there is (all answers - wrong answers) of correct answers
			newMemo.setCorrectAnsweredWordA(ankiCard.getNumberAllAnswers() - ankiCard.getNumberWrongAnswers());

			// this is not straight to get the information about creation date
			newMemo.setCreated(ankiCard.getCardId());
			
			// last reviewed is in seconds so we have to multiply it by 1000
			newMemo.setLastReviewed(new Date(ankiCard.getLastModification().getTime()*1000));
			newMemo.setMemoBase(memoBase);
			
			AnkiCard secondAnkiCard = null;
			// get the index of current element
			int indexOfAnkiCard = ankiCards.indexOf(ankiCard);
			// check if the next one exists and if it has the same corresponding AnkiNote
			if(indexOfAnkiCard + 1 <= ankiCards.size() && ankiCards.get(indexOfAnkiCard + 1).getNoteId() == ankiCard.getNoteId()) {
				secondAnkiCard = ankiCards.get(indexOfAnkiCard + 1);
			}
			if(secondAnkiCard != null) {
				// there is (all answers - wrong answers) of correct answers
				newMemo.setCorrectAnsweredWordB(secondAnkiCard.getNumberAllAnswers() - secondAnkiCard.getNumberWrongAnswers());
				// second card exists so all answers is a sum of both cards
				newMemo.setDisplayed(ankiCard.getNumberAllAnswers() + secondAnkiCard.getNumberAllAnswers());
			} else {
				// second card do not exist so only answers from first card is taken
				newMemo.setDisplayed(ankiCard.getNumberAllAnswers());
			}
			
			// add the newly created memo to the memoList
			memosFromAnkiDeck.add(newMemo);
		}
		
		return memosFromAnkiDeck;
	}
}