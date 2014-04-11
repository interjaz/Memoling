package app.memoling.android.anki.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import app.memoling.android.R;
import app.memoling.android.adapter.MemoAdapter;
import app.memoling.android.adapter.MemoAdapter.Sort;
import app.memoling.android.adapter.MemoBaseAdapter;
import app.memoling.android.anki.AnkiIOEngine;
import app.memoling.android.anki.AnkiImportAdapter;
import app.memoling.android.anki.entity.AnkiCard;
import app.memoling.android.anki.entity.AnkiCollection;
import app.memoling.android.anki.entity.AnkiConfiguration;
import app.memoling.android.anki.entity.AnkiDeck;
import app.memoling.android.anki.entity.AnkiMessage;
import app.memoling.android.anki.entity.AnkiNote;
import app.memoling.android.db.DatabaseHelper;
import app.memoling.android.db.DatabaseHelper.Order;
import app.memoling.android.entity.Language;
import app.memoling.android.entity.Memo;
import app.memoling.android.entity.MemoBase;
import app.memoling.android.entity.Word;
import app.memoling.android.helper.AppLog;
import app.memoling.android.sync.ConflictResolve.OnConflictResolveHaltable;
import app.memoling.android.sync.SupervisedSync.OnSyncComplete;
import app.memoling.android.sync.SupervisedSyncHaltable;
import app.memoling.android.thread.WorkerThread;
import app.memoling.android.ui.control.LanguageSpinner;
import app.memoling.android.ui.view.LanguageView;

public class AnkiImporter {

	public AnkiImporter(final Context ctx, final String path, final OnConflictResolveHaltable<Memo> onConflictMemo, final OnSyncComplete onComplete) {
		// create new worker and execute it to work in background
		new WorkerThread<Void, AnkiMessage, Void>() {

			protected void onProgressUpdate(AnkiMessage... ankiMessage) {
				
				if(ankiMessage[0].getMessageType().equals(0)){
					performSync(ankiMessage);
				} else if(ankiMessage[0].getMessageType().equals(1)) {
					performAlertDialog(ankiMessage);
				}
			}
						
			private void performAlertDialog(AnkiMessage... ankiMessage) {
				
				final Lock publishingLock = ankiMessage[0].getPublishingLock();
				
				LayoutInflater inflater = LayoutInflater.from(ctx);
				View view = inflater.inflate(R.layout.dialog_language_spinners, null);
				
				final LanguageSpinner m_spLanguageFrom = (LanguageSpinner) view.findViewById(R.id.ankiImport_spLanguageFrom);
				final LanguageSpinner m_spLanguageTo = (LanguageSpinner) view.findViewById(R.id.ankiImport_spLanguageTo);
				final Button m_btnLanguageSwap = (Button) view.findViewById(R.id.ankiImport_btnLanguageSwap);
				final CheckBox m_applySettingsCheckbox = (CheckBox) view.findViewById(R.id.ankiImport_checkBox);
				
				m_btnLanguageSwap.setOnClickListener(new OnClickListener() {				
					@Override
					public void onClick(View arg0) {
						int from = m_spLanguageFrom.getSelectedItemPosition();
						int to = m_spLanguageTo.getSelectedItemPosition();
						m_spLanguageTo.setSelection(from);
						m_spLanguageFrom.setSelection(to);
					}
				});
				
				m_applySettingsCheckbox.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						boolean checked = ((CheckBox) v).isChecked();
					}
				});
				
				m_spLanguageFrom.bindData(ctx);
				m_spLanguageTo.bindData(ctx);
				
				// start activity for result, there is a need to determine the languages
				new AlertDialog.Builder(ctx)
				.setTitle(ctx.getString(R.string.ankiImporter_ctxmenu_languagesSelectionTitle))
				.setMessage(ctx.getString(R.string.ankiImporter_ctxmenu_languagesSelectionQuestion))
				.setView(view)
				.setCancelable(false)
				.setPositiveButton(ctx.getString(R.string.ankiImporter_ctxmenu_languagesSelectionConfirmation),
						new android.content.DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// take the information about selected languages from the user
								Language fromLang = ((LanguageView) m_spLanguageFrom.getSelectedItem()).getLanguage();
								Language toLang = ((LanguageView) m_spLanguageTo.getSelectedItem()).getLanguage();
								
								// notify the waiting thread
								synchronized(publishingLock) {
									publishingLock.notify();	
								}
							}
						})
				.setNegativeButton(ctx.getString(R.string.ankiImporter_ctxmenu_languagesSelectionCancelation),
						new android.content.DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// notify the waiting thread
								synchronized(publishingLock) {
									publishingLock.notify();	
								}
							}
						}).setIcon(R.drawable.ic_dialog_alert_holo_dark).create().show();
			}
			
			private void performSync(AnkiMessage... ankiMessage) {
				final MemoAdapter memoAdapter = new MemoAdapter(ctx, true);
				final List<Memo> internalMemos = ankiMessage[0].getInternalMemos();
				final List<Memo> externalMemos = ankiMessage[0].getExternalMemos();
				final String destinationMemoBaseId = ankiMessage[0].getDestinationMemoBaseId();
				final Lock publishingLock = ankiMessage[0].getPublishingLock();
				
				SupervisedSyncHaltable<Memo> syncBase = new SupervisedSyncHaltable<Memo>(ctx, onConflictMemo,
						new OnSyncComplete() {
							@Override
							public void onComplete(boolean result) {
								AppLog.e("AnkiImporter#onProgressUpdate", "onComplete fired", null);
								synchronized(publishingLock) {
									publishingLock.notify();	
								}
								AppLog.e("AnkiImporter#onProgressUpdate", "publishing lock was notified", null);
							}
				}) 
					{

					@Override
					protected Memo contains(Memo object)
							throws Exception {

						for (Memo memo : internalMemos) {

							if (memo.getMemoId().equals(object.getMemoId())) {
								return memo;
							}
						}

						return null;
					}

					@Override
					protected List<Memo> getInternal() {
						return internalMemos;
					}

					@Override
					protected List<Memo> getExternal() {
						return externalMemos;
					}

					@Override
					protected Memo getNewer(Memo internal, Memo external) {
						if (internal.getLastReviewed().compareTo(external.getLastReviewed()) > 0) {
							return internal;
						} else {
							return external;
						}
					}

					@Override
					protected boolean submitTransaction(List<Memo> internalToDelete, List<Memo> externalToAdd) {

						for (int i = 0; i < internalToDelete.size(); i++) {
							Memo toDelete = internalToDelete.get(i);
							memoAdapter.delete(toDelete.getMemoId());
						}

						for (int i = 0; i < externalToAdd.size(); i++) {
							Memo toAdd = externalToAdd.get(i);
							toAdd.setMemoBaseId(destinationMemoBaseId);
							if(memoAdapter.add(toAdd) == DatabaseHelper.Error) {
								return false;
							}
						}

						return true;
					}

					@Override
					protected void clean() throws Exception {
						memoAdapter.closePersistant();
					}
				};

				// perform sync of the deck
				syncBase.sync();
			}
			
			@Override
			protected Void doInBackground(Void... params) {				
				// unpack the file *.apkg
				AnkiIOEngine.unpackFile(path);
				
				String databaseName = AnkiIOEngine.getImportDatabaseName();
				AnkiIOEngine.setImportDatabaseVersion(1);
				int databaseVersion = AnkiIOEngine.getImportDatabaseVersion();
				
				// open imported database
				AnkiImportAdapter ankiImportAdapter = new AnkiImportAdapter(ctx, databaseName, databaseVersion, true);
				// all notes from anki
				final List<AnkiNote> ankiNotes = ankiImportAdapter.getAllAnkiNotes(Sort.CreatedDate, Order.ASC);
				// collections described in the database
				final List<AnkiCollection> ankiCollections = ankiImportAdapter.getAllAnkiCollections(Sort.CreatedDate, Order.ASC);
				
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
					MemoBaseAdapter memoBaseAdapter = new MemoBaseAdapter(ctx);
					List<MemoBase> memoBases = memoBaseAdapter.getAll();

					// current 
					String findDestinationMemoBaseId;
					MemoBase destinationMemoBase;
					boolean theDeckAlreadyExists;
					
					// for every anki deck
					for (AnkiDeck ankiDeck : ankiDecks) {
						findDestinationMemoBaseId = null;
						destinationMemoBase = null;
						// check if there is corresponding memoling deck
						
						theDeckAlreadyExists = false;
						for (MemoBase memoBase : memoBases){
							if(ankiDeck.getName().equals(memoBase.getName())) {
								// if it is then we will be updating the content
								findDestinationMemoBaseId = memoBase.getMemoBaseId();
								destinationMemoBase = memoBase;
								theDeckAlreadyExists = true;
								break;
							}
						}
						
						if(!theDeckAlreadyExists) {
							// if it is not then we will create new deck	
							destinationMemoBase = createMemoBase(ctx,ankiDeck);
							findDestinationMemoBaseId = destinationMemoBase.getMemoBaseId();

							Lock alertDialogLock = new ReentrantLock();
							AppLog.e("AnkiImporter#doInBackground", "publishing lock created", null);
							try {
								alertDialogLock.lock();
								AppLog.e("AnkiImporter#doInBackground", "publishing lock taken", null);
								publishProgress(new AnkiMessage(1,alertDialogLock));
								AppLog.e("AnkiImporter#doInBackground", "publish progress was sent", null);
								synchronized(alertDialogLock) {
									alertDialogLock.wait();	
								}
								AppLog.e("AnkiImporter#doInBackground", "publishing lock was notified", null);
								alertDialogLock.unlock();
								AppLog.e("AnkiImporter#doInBackground", "unlock the thread", null);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								alertDialogLock.unlock();
								e.printStackTrace();
							}
						}
												
						// prepare cards for that deck, it will be best if this would be multithreaded and the cards in the list should be removed 
						final List<AnkiCard> ankiCardsFromAnkiBase = ankiImportAdapter.getAnkiCards(ankiDeck.getDeckId().getTime(), Sort.CreatedDate, Order.ASC);

						// convert AnkiCard to Memo
						final List<Memo> externalMemos = convertAnkiCardsIntoMemos(ankiCardsFromAnkiBase, ankiNotes, findDestinationMemoBaseId, destinationMemoBase);
						
						final MemoAdapter memoAdapter = new MemoAdapter(ctx, true);
						final List<Memo> internalMemos = memoAdapter.getAll(findDestinationMemoBaseId, Sort.CreatedDate, Order.ASC);
						final String destinationMemoBaseId = findDestinationMemoBaseId;

						Lock publishingLock = new ReentrantLock();
						AppLog.e("AnkiImporter#doInBackground", "publishing lock created", null);
						try {
							publishingLock.lock();
							AppLog.e("AnkiImporter#doInBackground", "publishing lock taken", null);
							publishProgress(new AnkiMessage(0, destinationMemoBaseId, internalMemos, externalMemos, publishingLock));
							AppLog.e("AnkiImporter#doInBackground", "publish progress was sent", null);
							synchronized(publishingLock) {
								publishingLock.wait();	
							}
							AppLog.e("AnkiImporter#doInBackground", "publishing lock was notified", null);
							publishingLock.unlock();
							AppLog.e("AnkiImporter#doInBackground", "unlock the thread", null);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							publishingLock.unlock();
							e.printStackTrace();
						}

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
	
	private MemoBase createMemoBase(final Context context, AnkiDeck ankiDeck) {
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
		// return it for further use
		return newMemoBase;
	}
	
	private List<Memo> convertAnkiCardsIntoMemos(List<AnkiCard> ankiCards, List<AnkiNote> ankiNotes, String memoBaseId, MemoBase memoBase) {
		// create empty list of Memos
		List<Memo> memosFromAnkiDeck = new ArrayList<Memo>();
		
		// for every AnkiCard try to find corresponding one AnkiNote
		for (AnkiCard ankiCard : ankiCards) {	
			// we process only cards that are the first from the pair
			if(ankiCard.getOrd() != 0){
				continue;
			}
			
			AnkiNote tmpAnkiNote = null;
			// TODO change this maybe to map: m_NoteId => AnkiNote
			for (AnkiNote ankiNote : ankiNotes) {
				if(ankiCard.getNoteId().equals(ankiNote.getNoteId())) {
					tmpAnkiNote = ankiNote;
					break;
				}
			}
			// check if there was AnkiNote found
			if(tmpAnkiNote == null) {
				continue;
			}
			
			// TODO there is a need to cut out the initial wordB from wordA, strange
			tmpAnkiNote.getFlds();
			String ankiWordA = "";
			String ankiWordB = tmpAnkiNote.getSfld();
			
			// in Anki base there is no information about input language
			// we will ask user for going through short questions
			Word wordA = new Word(UUID.randomUUID().toString(), ankiWordA, Language.DE);
			Word wordB = new Word(UUID.randomUUID().toString(), ankiWordB, Language.PL);
			
			Memo newMemo = new Memo(wordA, wordB, memoBaseId);
			// there is (all answers - wrong answers) of correct answers
			newMemo.setCorrectAnsweredWordA(ankiCard.getNumberAllAnswers() - ankiCard.getNumberWrongAnswers());
			newMemo.setCreated(ankiCard.getCardId());
			newMemo.setLastReviewed(ankiCard.getLastModification());
			newMemo.setMemoBase(memoBase);
			
			AnkiCard secondAnkiCard = null;
			// get the index of current element
			int indexOfAnkiCard = ankiCards.indexOf(ankiCard);
			// check if the next one exists and if it has the same corresponding AnkiNote
			if(indexOfAnkiCard + 1 < ankiCards.size() && ankiCards.get(indexOfAnkiCard + 1).getNoteId().equals(ankiCard.getNoteId())) {
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