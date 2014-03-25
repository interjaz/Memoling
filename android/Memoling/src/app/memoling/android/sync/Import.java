package app.memoling.android.sync;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import android.content.Context;
import app.memoling.android.adapter.MemoAdapter;
import app.memoling.android.adapter.MemoAdapter.Sort;
import app.memoling.android.anki.AnkiIOEngine;
import app.memoling.android.db.DatabaseHelper;
import app.memoling.android.db.DatabaseHelper.Order;
import app.memoling.android.entity.Memo;
import app.memoling.android.entity.MemoBase;
import app.memoling.android.entity.Word;
import app.memoling.android.helper.AppLog;
import app.memoling.android.sync.ConflictResolve.OnConflictResolveHaltable;
import app.memoling.android.sync.SupervisedSync.OnSyncComplete;

public class Import {

	public static void importMemosMemolingFile(final String destinationMemoBaseId, final Context context,
			final String path, final OnConflictResolveHaltable<Memo> onConflictMemo, final OnSyncComplete onComplete) {

		try {
			MemolingFile memolingFile = MemolingFile.parseFile(path);

			final ArrayList<Memo> externalMemos = new ArrayList<Memo>();
			for (MemoBase base : memolingFile.getMemoBases()) {
				ArrayList<Memo> memos = base.getMemos();
				for (int i = 0; i < memos.size(); i++) {
					externalMemos.add(memos.get(i));
				}
			}

			final MemoAdapter memoAdapter = new MemoAdapter(context, true);
			final ArrayList<Memo> internalMemos = memoAdapter
					.getAll(destinationMemoBaseId, Sort.CreatedDate, Order.ASC);

			SupervisedSyncHaltable<Memo> syncBase = new SupervisedSyncHaltable<Memo>(context, onConflictMemo,
					onComplete) {

				@Override
				protected ArrayList<Memo> getInternal() {
					return internalMemos;
				}

				@Override
				protected ArrayList<Memo> getExternal() {
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
				protected boolean submitTransaction(ArrayList<Memo> internalToDelete, ArrayList<Memo> externalToAdd) {

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

			syncBase.sync();

		} catch (Exception ex) {
			AppLog.w("Import", "importMemosMemosMemolingFile", ex);
			onComplete.onComplete(false);
		}
	}

	public static void importCsvFile(final String destinationMemoBaseId, final Context context, final String path,
			final OnConflictResolveHaltable<Memo> onConflictMemo, final OnSyncComplete onComplete) {

		try {

			final ArrayList<Memo> externalMemos = CsvParser.parseFile(path);
			File fInfo = new File(path);
			final Date externalDate = new Date(fInfo.lastModified());

			final MemoAdapter memoAdapter = new MemoAdapter(context, true);
			final ArrayList<Memo> internalMemos = memoAdapter
					.getAll(destinationMemoBaseId, Sort.CreatedDate, Order.ASC);

			SupervisedSyncHaltable<Memo> syncBase = new SupervisedSyncHaltable<Memo>(context, onConflictMemo,
					onComplete) {

				@Override
				protected ArrayList<Memo> getInternal() {
					return internalMemos;
				}

				@Override
				protected ArrayList<Memo> getExternal() {
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
				protected boolean submitTransaction(ArrayList<Memo> internalToDelete, ArrayList<Memo> externalToAdd) {

					for (int i = 0; i < internalToDelete.size(); i++) {
						Memo toDelete = internalToDelete.get(i);
						memoAdapter.delete(toDelete.getMemoId());
					}

					for (int i = 0; i < externalToAdd.size(); i++) {
						Memo toAdd = externalToAdd.get(i);
						toAdd.setMemoId(UUID.randomUUID().toString());
						toAdd.setCreated(new Date());
						toAdd.setLastReviewed(new Date());
						toAdd.setMemoBaseId(destinationMemoBaseId);
						memoAdapter.add(toAdd);
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
	
	public static void importAnkiFile(final String destinationMemoBaseId, final Context context, final String path,
			final OnConflictResolveHaltable<Memo> onConflictMemo, final OnSyncComplete onComplete) {
		// TODO Auto-generated method stub
		
		AnkiIOEngine.importFile(context, path);
	}
}
