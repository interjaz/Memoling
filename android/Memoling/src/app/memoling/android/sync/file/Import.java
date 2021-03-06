package app.memoling.android.sync.file;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import android.content.Context;
import app.memoling.android.adapter.MemoAdapter;
import app.memoling.android.adapter.SyncClientAdapter;
import app.memoling.android.adapter.MemoAdapter.Sort;
import app.memoling.android.anki.AnkiIOEngine;
import app.memoling.android.db.DatabaseHelper.Order;
import app.memoling.android.entity.Memo;
import app.memoling.android.entity.MemoBase;
import app.memoling.android.entity.Word;
import app.memoling.android.helper.AppLog;
import app.memoling.android.sync.file.ConflictResolve.OnConflictResolveHaltable;
import app.memoling.android.sync.file.SupervisedSync.OnSyncComplete;

public class Import {

	public static void importMemosMemolingFile(final String destinationMemoBaseId, final Context context,
			final String path, final OnConflictResolveHaltable<Memo> onConflictMemo, final OnSyncComplete onComplete) {

		try {
			MemolingFile memolingFile = MemolingFile.parseFile(path);

			final List<Memo> externalMemos = new ArrayList<Memo>();
			for (MemoBase base : memolingFile.getMemoBases()) {
				List<Memo> memos = base.getMemos();
				for (int i = 0; i < memos.size(); i++) {
					externalMemos.add(memos.get(i));
				}
			}

			final MemoAdapter memoAdapter = new MemoAdapter(context);
			final List<Memo> internalMemos = memoAdapter
					.getAllDeep(destinationMemoBaseId, Sort.CreatedDate, Order.ASC);

			SupervisedSyncHaltable<Memo> syncBase = new SupervisedSyncHaltable<Memo>(context, onConflictMemo,
					onComplete) {

				@Override
				protected List<Memo> getInternal() {
					return internalMemos;
				}

				@Override
				protected List<Memo> getExternal() {
					return externalMemos;
				}

				@Override
				protected Memo contains(Memo object) throws Exception {

					for (Memo memo : internalMemos) {

						if (memo.getMemoId().equals(object.getMemoId())) {
							return memo;
						}
					}

					return null;
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

					String syncClientId = new SyncClientAdapter(context).getCurrentSyncClientId();
					
					for (int i = 0; i < internalToDelete.size(); i++) {
						Memo toDelete = internalToDelete.get(i);
						memoAdapter.delete(toDelete.getMemoId(), syncClientId);
					}

					for (int i = 0; i < externalToAdd.size(); i++) {
						Memo toAdd = externalToAdd.get(i);
						toAdd.setMemoBaseId(destinationMemoBaseId);
						try {
							memoAdapter.insert(toAdd, syncClientId);
						} catch(RuntimeException ex) {
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

			syncBase.sync();

		} catch (Exception ex) {
			AppLog.w("Import", "importMemosMemosMemolingFile", ex);
			onComplete.onComplete(false);
		}
	}

	public static void importCsvFile(final String destinationMemoBaseId, final Context context, final String path,
			final OnConflictResolveHaltable<Memo> onConflictMemo, final OnSyncComplete onComplete) {

		try {

			final List<Memo> externalMemos = CsvParser.parseFile(path);
			File fInfo = new File(path);
			final Date externalDate = new Date(fInfo.lastModified());

			final MemoAdapter memoAdapter = new MemoAdapter(context);
			final List<Memo> internalMemos = memoAdapter
					.getAllDeep(destinationMemoBaseId, Sort.CreatedDate, Order.ASC);
			
			SupervisedSyncHaltable<Memo> syncBase = new SupervisedSyncHaltable<Memo>(context, onConflictMemo,
					onComplete) {

				@Override
				protected List<Memo> getInternal() {
					return internalMemos;
				}

				@Override
				protected List<Memo> getExternal() {
					return externalMemos;
				}

				@Override
				protected Memo contains(Memo object) throws Exception {

					for (Memo memo : internalMemos) {
						Word wmA = memo.getWordA();
						Word wmB = memo.getWordB();
						Word woA = object.getWordA();
						Word woB = object.getWordB();

						if (wmA.getWord().equals(woA.getWord())
								&& wmA.getLanguage().getCode().equals(woA.getLanguage().getCode())
								&& wmB.getWord().equals(woB.getWord())
								&& wmB.getLanguage().getCode().equals(woB.getLanguage().getCode())) {
							return memo;
						}
					}

					return null;
				}

				@Override
				protected Memo getNewer(Memo internal, Memo external) {
					if (internal.getLastReviewed().compareTo(externalDate) > 0) {
						return internal;
					} else {
						return external;
					}
				}

				@Override
				protected boolean submitTransaction(List<Memo> internalToDelete, List<Memo> externalToAdd) {

					String syncClientId = new SyncClientAdapter(context).getCurrentSyncClientId();
					
					for (int i = 0; i < internalToDelete.size(); i++) {
						Memo toDelete = internalToDelete.get(i);
						memoAdapter.delete(toDelete.getMemoId(), syncClientId);
					}

					for (int i = 0; i < externalToAdd.size(); i++) {
						Memo toAdd = externalToAdd.get(i);
						toAdd.setMemoId(UUID.randomUUID().toString());
						toAdd.setCreated(new Date());
						toAdd.setLastReviewed(new Date());
						toAdd.setMemoBaseId(destinationMemoBaseId);
						try {
							memoAdapter.insert(toAdd, syncClientId);
						} catch(RuntimeException ex) {
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

			syncBase.sync();

		} catch (Exception ex) {
			AppLog.w("Import", "importCsvFile", ex);
			onComplete.onComplete(false);
		}
	}
	
	public static void importAnkiFile(final Context context, final String path,
			final OnConflictResolveHaltable<Memo> onConflictMemo, final OnSyncComplete onComplete) {
		// TODO Auto-generated method stub
		
		AnkiIOEngine.importFile(context, path, onConflictMemo, onComplete);
	}
}
