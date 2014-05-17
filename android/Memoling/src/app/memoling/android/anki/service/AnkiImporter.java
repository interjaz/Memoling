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
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import app.memoling.android.R;
import app.memoling.android.adapter.MemoAdapter;
import app.memoling.android.adapter.MemoAdapter.Sort;
import app.memoling.android.adapter.MemoBaseAdapter;
import app.memoling.android.adapter.SyncClientAdapter;
import app.memoling.android.anki.AnkiIOEngine;
import app.memoling.android.anki.AnkiImportAdapter;
import app.memoling.android.anki.entity.AnkiCard;
import app.memoling.android.anki.entity.AnkiCollection;
import app.memoling.android.anki.entity.AnkiConfiguration;
import app.memoling.android.anki.entity.AnkiDeck;
import app.memoling.android.anki.entity.AnkiMessage;
import app.memoling.android.anki.entity.AnkiNote;
import app.memoling.android.db.DatabaseHelper.Order;
import app.memoling.android.entity.Language;
import app.memoling.android.entity.Memo;
import app.memoling.android.entity.MemoBase;
import app.memoling.android.entity.Word;
import app.memoling.android.helper.AppLog;
import app.memoling.android.sync.file.ConflictResolve.OnConflictResolveHaltable;
import app.memoling.android.sync.file.SupervisedSync.OnSyncComplete;
import app.memoling.android.sync.file.SupervisedSyncHaltable;
import app.memoling.android.thread.WorkerThread;
import app.memoling.android.ui.control.LanguageSpinner;
import app.memoling.android.ui.view.LanguageView;

public class AnkiImporter {

	private Context ctx;
	private OnConflictResolveHaltable<Memo> onConflictMemo;
	private AnkiImportAdapter ankiImportAdapter;
	private MemoBaseAdapter memoBaseAdapter;
	private MemoAdapter memoAdapter;
	private String path;
	
	private Language languageFrom = null;
	private Language languageTo = null;
	private boolean applySettingsToAllDecks = false;
	private boolean skipRestOfDecks = false;
	private boolean skipThisDeck = false;
	private boolean showHideButtonInProgressBarDialog = false;
	
	private AnkiConfiguration ankiConfiguration;
	private AnkiConfiguration ankiDefaultConfiguration;
	private List<AnkiDeck> ankiDecks;
	private List<MemoBase> memoBases;
	private List<AnkiNote> ankiNotes;
	private List<AnkiCollection> ankiCollections;
	
	private Integer numberOfDecks = Integer.valueOf(0);
	private Integer progressChunk = Integer.valueOf(0);
	
	private ProgressDialogManager progressDialogManager;
	private AnkiImporterManager ankiImporterManager;
	
	private class ProgressDialogManager {
		private TextView progressInfo;
		private View view;
		private LayoutInflater inflater;
		private ProgressBar ankiImportProgressBar;
		private AlertDialog progressAlertDialog;
		private AnkiMessage progressBarValueMessage;
		private Integer progressBarValue;
		private Integer ankiMessageType;
		
		private boolean progressBarDialogCreated = false;
		
		private void createProgressDialog() {
			inflater = LayoutInflater.from(ctx);
			view = inflater.inflate(R.layout.dialog_language_progressbar_with_progressinfo, null);
			
			progressInfo = (TextView) view.findViewById(R.id.ankiImport_progressInfo);
			
			ankiImportProgressBar = (ProgressBar) view.findViewById(R.id.ankiImport_progressBar);
			ankiImportProgressBar.setMax(100);
			ankiImportProgressBar.setProgress(0);
			
			progressAlertDialog = new AlertDialog.Builder(ctx)
			.setTitle(ctx.getString(R.string.ankiImporter_ctxmenu_importProgressTitle))
			.setView(view)
			.setCancelable(false)
			.setPositiveButton(ctx.getString(R.string.ankiImporter_ctxmenu_importProgressHide), new android.content.DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// hide the progress
					Toast.makeText(ctx, R.string.ankiImporter_importInBackground, Toast.LENGTH_SHORT).show();
				}
			})
			.setIcon(R.drawable.ic_dialog_alert_holo_dark).create();
			progressAlertDialog.show();
		}
		
		private void showProgressDialog(AnkiMessage... ankiMessage) {
			if(!progressBarDialogCreated) {
				createProgressDialog();	
				progressBarDialogCreated = true;
			}
			
			int progressBarValue = ankiMessage[0].getProgressBarValue();
			ankiImportProgressBar.setProgress(progressBarValue);
			
			progressInfo.setText(ankiMessage[0].getProgressInfo());
		}

		private void cancelProgressDialog(AnkiMessage... ankiMessage) {
			int progressBarValue = ankiMessage[0].getProgressBarValue();
			ankiImportProgressBar.setProgress(progressBarValue);
			progressAlertDialog.cancel();
		}
		
		private AnkiMessage initializeProgressDialog() {
			ankiMessageType = 2;
			progressBarValue = 0;
			
			progressBarValueMessage = new AnkiMessage(ankiMessageType, progressBarValue, 
					ctx.getString(R.string.ankiImporter_progressInfo_loading));
			return progressBarValueMessage;
		}
		
		private AnkiMessage updateProgressDialog(Integer progressChunk, Integer ankiMessageType) {
			if(ankiMessageType == 2) {	
				progressBarValue = progressBarValue.intValue() + progressChunk;
				progressBarValueMessage.setProgressBarValue(progressBarValue);
				progressBarValueMessage.setProgressInfo(ctx.getString(R.string.ankiImporter_progressInfo_loadingDeck));
			} else if (ankiMessageType == 3) {
				progressBarValue = 100;
				progressBarValueMessage.setMessageType(ankiMessageType);
				progressBarValueMessage.setProgressBarValue(progressBarValue);
			}
			return progressBarValueMessage;
		}
	}
	
	private class AnkiImporterManager {
		private void initializeAnkiImporter() {
			// unpack the file *.apkg
			AnkiIOEngine.unpackFile(path);
			
			String databaseName = AnkiIOEngine.getImportDatabaseName();
			int databaseVersion = AnkiIOEngine.getImportDatabaseVersion();
			
			// open imported database
			ankiImportAdapter = new AnkiImportAdapter(ctx, databaseName, databaseVersion, true);
			// all notes from anki
			ankiNotes = ankiImportAdapter.getAllAnkiNotes(Sort.CreatedDate, Order.ASC);
			// collections described in the database
			ankiCollections = ankiImportAdapter.getAllAnkiCollections(Sort.CreatedDate, Order.ASC);
		}
		
		private void parseAnkiFile() {
			// parse 'conf' column
			ankiConfiguration = AnkiCollection.getConfigurationDescription(ankiCollections.get(0).getConfiguration());
			
			// parse 'models' column
//			List<AnkiModel> ankiModels = AnkiCollection.getModelsDescription(ankiCollections.get(0).getModels());
			
			// parse 'decks' column
			ankiDecks = AnkiCollection.getDecksDescription(ankiCollections.get(0).getDecks());
								
			// parse 'dconf' column
			ankiDefaultConfiguration = AnkiCollection.getConfigurationDescription(ankiCollections.get(0).getDefaultConfiguration());
			
			// parse 'tags' column
		}
	}
	
	public AnkiImporter(final Context ctx, final String path, final OnConflictResolveHaltable<Memo> onConflictMemo, final OnSyncComplete onComplete) {
		this.ctx = ctx;
		this.onConflictMemo = onConflictMemo;
		this.path = path;
		
		progressDialogManager = new ProgressDialogManager();
		memoBaseAdapter = new MemoBaseAdapter(ctx);
		memoAdapter = new MemoAdapter(ctx);
		ankiImporterManager = new AnkiImporterManager();
		
		// create new worker and execute it to work in background
		new WorkerThread<Void, AnkiMessage, Void>() {

			@Override
			protected void onProgressUpdate(AnkiMessage... ankiMessage) {
				
				if(ankiMessage[0].getMessageType().equals(0)){
					performSync(ankiMessage);
				} else if(ankiMessage[0].getMessageType().equals(1)) {
					performAlertDialog(ankiMessage);
				} else if(ankiMessage[0].getMessageType().equals(2)) {
					progressDialogManager.showProgressDialog(ankiMessage);
				} else if(ankiMessage[0].getMessageType().equals(3)) {
					progressDialogManager.cancelProgressDialog(ankiMessage);
				}
			}

			@Override
			protected Void doInBackground(Void... params) {		
				
				// initialize the dialog with a progress bar
				publishProgress(progressDialogManager.initializeProgressDialog());
		
				// initialize the importing
				ankiImporterManager.initializeAnkiImporter();
				
				if(!ankiCollections.isEmpty()) {
					
					// parsed information from anki file
					ankiImporterManager.parseAnkiFile();
					
					// get all memo bases from the memoling storage
					memoBases = memoBaseAdapter.getAll();

					// local settings 
					String destinationMemoBaseId;
					MemoBase destinationMemoBase;
					boolean theDeckAlreadyExists;
					
					// set progress chunk
					numberOfDecks = ankiDecks.size();
					progressChunk = 100 / numberOfDecks;
					
					if(!skipRestOfDecks) {
						// for every anki deck
						for (AnkiDeck ankiDeck : ankiDecks) {
							
							// importing of a deck
							publishProgress(progressDialogManager.updateProgressDialog(progressChunk,2));
							
							// clear local settings 
							destinationMemoBaseId = null;
							destinationMemoBase = null;
							theDeckAlreadyExists = false;
							
							// check if there is corresponding memoling deck
							for (MemoBase memoBase : memoBases){
								if(ankiDeck.getName().equals(memoBase.getName())) {
									// if it is then we will be updating the content
									destinationMemoBaseId = memoBase.getMemoBaseId();
									destinationMemoBase = memoBase;
									theDeckAlreadyExists = true;
									break;
								}
							}
														
							// prepare cards for that deck, it will be best if this would be multithreaded and the cards in the list should be removed 
							final List<AnkiCard> ankiCardsFromAnkiBase = ankiImportAdapter.getAnkiCards(ankiDeck.getDeckId().getTime(), Sort.CreatedDate, Order.ASC);
							
							// imported deck has at least one memo card ?
							if(ankiCardsFromAnkiBase.size() > 0) {

								if(!theDeckAlreadyExists) {
									// if it is not then we will create new deck	
									destinationMemoBase = createMemoBase(ankiDeck);
									destinationMemoBaseId = destinationMemoBase.getMemoBaseId();
								} 
								
								Memo exampleMemo = convertAnkiCardIntoMemo(ankiCardsFromAnkiBase, 
										ankiNotes, destinationMemoBaseId, destinationMemoBase, languageFrom, languageTo);

								if(!applySettingsToAllDecks && exampleMemo != null){
									// ask user about the languages in decks
									askUserAboutDeck(exampleMemo);
								} else if(exampleMemo == null) {
									skipThisDeck = true;
								}
								
								if(!skipThisDeck && !skipRestOfDecks) {
									// convert AnkiCard to Memo
									final List<Memo> externalMemos = convertAnkiCardsIntoMemos(ankiCardsFromAnkiBase, 
											ankiNotes, destinationMemoBaseId, destinationMemoBase, languageFrom, languageTo);
									
									// load internal memos
									final List<Memo> internalMemos = memoAdapter.getAllDeep(destinationMemoBaseId, Sort.CreatedDate, Order.ASC);

									// trigger for synchronization
									triggerSync(destinationMemoBaseId, internalMemos, externalMemos);	
									
									// reset skipThisDeck
									skipThisDeck = false;
								} else {
									// delete newly created MemoBase
									if(!theDeckAlreadyExists) {
										deleteMemoBase(destinationMemoBaseId);
									}
									// reset skipThisDeck
									skipThisDeck = false;
								}
							} 
						}	
					}
					// import completed
					publishProgress(progressDialogManager.updateProgressDialog(progressChunk,3));
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				// TODO on post execute action
				Toast.makeText(ctx, R.string.ankiImporter_importCompleted, Toast.LENGTH_SHORT).show();
				
				AnkiIOEngine.onAnkiImportComplete();
			}
			
			private void askUserAboutDeck(Memo exampleMemo) {
				// publishing lock created
				Lock alertDialogLock = new ReentrantLock();
				try {
					// lock taken
					alertDialogLock.lock();
					// ask user about the languages in decks
					publishProgress(new AnkiMessage(1,alertDialogLock, 
							exampleMemo.getWordA().getWord(), exampleMemo.getWordB().getWord()));	
					
					// waiting for response from user
					synchronized(alertDialogLock) {
						alertDialogLock.wait();	
					}
					// user responded
					alertDialogLock.unlock();
				} catch (InterruptedException e) {
					alertDialogLock.unlock();
					e.printStackTrace();
				}
			}
			
			private void triggerSync(String destinationMemoBaseId, List<Memo> internalMemos, List<Memo> externalMemos) {
				// publishing lock created
				Lock publishingLock = new ReentrantLock();
				try {
					// lock taken
					publishingLock.lock();
					// perform sync
					publishProgress(new AnkiMessage(0, destinationMemoBaseId, internalMemos, externalMemos, publishingLock));
					// waiting for completion
					synchronized(publishingLock) {
						publishingLock.wait();	
					}
					// complete
					publishingLock.unlock();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					publishingLock.unlock();
					e.printStackTrace();
				}
			}

		}.execute();
	}

	private void performSync(AnkiMessage... ankiMessage) {
		final MemoAdapter memoAdapter = new MemoAdapter(ctx);
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
				String syncClientId = new SyncClientAdapter(getContext()).getCurrentSyncClientId();
						
				for (int i = 0; i < internalToDelete.size(); i++) {
					Memo toDelete = internalToDelete.get(i);
					// [BB] Changed DAL
					memoAdapter.delete(toDelete.getMemoId(), syncClientId);
				}

				for (int i = 0; i < externalToAdd.size(); i++) {
					Memo toAdd = externalToAdd.get(i);
					toAdd.setMemoBaseId(destinationMemoBaseId);
					// [BB] Changed DAL
					try {
						memoAdapter.insert(toAdd, syncClientId);
					} catch(Exception ex) {
						return false;
					}
				}

				return true;
			}
		};

		// perform sync of the deck
		syncBase.sync();
	}
	
	private void performAlertDialog(AnkiMessage... ankiMessage) {
		
		final Lock publishingLock = ankiMessage[0].getPublishingLock();
		final String leftWord =  ankiMessage[0].getLeftWord();
		final String rightWord =  ankiMessage[0].getRightWord();
		
		LayoutInflater inflater = LayoutInflater.from(ctx);
		View view = inflater.inflate(R.layout.dialog_language_spinners_with_example_text, null);
		
		final LanguageSpinner spLanguageFrom = (LanguageSpinner) view.findViewById(R.id.ankiImport_spLanguageFrom);
		final LanguageSpinner spLanguageTo = (LanguageSpinner) view.findViewById(R.id.ankiImport_spLanguageTo);
		final CheckBox applySettingsCheckbox = (CheckBox) view.findViewById(R.id.ankiImport_checkBox);
		final TextView leftWordFromExamplePair = (TextView) view.findViewById(R.id.ankiImport_leftWordFromExamplePair);
		final TextView rightWordFromExamplePair = (TextView) view.findViewById(R.id.ankiImport_rightWordFromExamplePair);
		
		leftWordFromExamplePair.setText(leftWord);
		rightWordFromExamplePair.setText(rightWord);
		
		spLanguageFrom.bindData(ctx);
		spLanguageTo.bindData(ctx);
		
		if(languageFrom != null) {
			spLanguageFrom.setSelection(languageFrom);	
		} else {
			spLanguageFrom.setSelection(Language.Unsupported);
		}
		
		if(languageTo != null) {
			spLanguageTo.setSelection(languageTo);
		} else {
			spLanguageTo.setSelection(Language.Unsupported);
		}
		
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
						languageFrom = ((LanguageView) spLanguageFrom.getSelectedItem()).getLanguage();
						languageTo = ((LanguageView) spLanguageTo.getSelectedItem()).getLanguage();
						
						applySettingsToAllDecks = ((CheckBox) applySettingsCheckbox).isChecked();
						
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
						
						applySettingsToAllDecks = ((CheckBox) applySettingsCheckbox).isChecked();
						
						if(applySettingsToAllDecks) {
							skipRestOfDecks = true;
						} else {
							skipThisDeck = true;
						}
						
						// notify the waiting thread
						synchronized(publishingLock) {
							publishingLock.notify();	
						}
					}
				}).setIcon(R.drawable.ic_import).create().show();
	}
	
	private MemoBase createMemoBase(AnkiDeck ankiDeck) {
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
		// [BB] Changed DAL
		String syncClientId = new SyncClientAdapter(ctx).getCurrentSyncClientId();
		memoBaseAdapter.insert(newMemoBase, syncClientId);		
		// return it for further use
		return newMemoBase;
	}
	
	private void deleteMemoBase(String memoBaseId) {
		String syncClientId = new SyncClientAdapter(ctx).getCurrentSyncClientId();
		memoBaseAdapter.delete(memoBaseId, syncClientId);
	}
	
	protected Memo convertAnkiCardIntoMemo( List<AnkiCard> ankiCards, 
			List<AnkiNote> ankiNotes, String memoBaseId, MemoBase memoBase, Language languageFrom, Language languageTo) {
		
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
			String ankiWordB = stripHtml(tmpAnkiNote.getSfld().trim());
			String ankiWordA = stripHtml(tmpAnkiNote.getFlds().trim()).substring(ankiWordB.length());
			
			// in Anki base there is no information about input language
			// we will ask user for going through short questions
			Word wordA = new Word(UUID.randomUUID().toString(), ankiWordA, languageFrom);
			Word wordB = new Word(UUID.randomUUID().toString(), ankiWordB, languageTo);
			
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
			return newMemo;
		}
		return null;
	}
	
	private List<Memo> convertAnkiCardsIntoMemos(List<AnkiCard> ankiCards, 
			List<AnkiNote> ankiNotes, String memoBaseId, MemoBase memoBase, Language languageFrom, Language languageTo) {
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
			String ankiWordB = stripHtml(tmpAnkiNote.getSfld().trim());
			String ankiWordA = stripHtml(tmpAnkiNote.getFlds().trim()).substring(ankiWordB.length());
			
			// in Anki base there is no information about input language
			// we will ask user for going through short questions
			Word wordA = new Word(UUID.randomUUID().toString(), ankiWordA, languageFrom);
			Word wordB = new Word(UUID.randomUUID().toString(), ankiWordB, languageTo);
			
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
	
	public String stripHtml(String html) {
	    return Html.fromHtml(html).toString();
	}
}