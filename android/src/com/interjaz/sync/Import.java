package com.interjaz.sync;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import org.json.JSONObject;

import android.content.Context;

import com.interjaz.db.Order;
import com.interjaz.entity.Memo;
import com.interjaz.entity.MemoAdapter;
import com.interjaz.entity.MemoAdapter.Sort;
import com.interjaz.entity.Word;
import com.interjaz.sync.ConflictResolve.OnConflictResolveHaltable;
import com.interjaz.sync.SupervisedSync.OnSyncComplete;

public class Import {

	public static void importMemosMemolingFile(final String destinationMemoBaseId, final Context context, final String path,
			final OnConflictResolveHaltable<Memo> onConflictMemo, final OnSyncComplete onComplete) {

		try {
			MemolingFile memolingFile = MemolingFile.parseFile(path);
			
			final ArrayList<Memo> externalMemos = new ArrayList<Memo>();
			for (Library lib : memolingFile.libraries) {
				ArrayList<Memo> libMemos = lib.memos;
				for (int i = 0; i < libMemos.size(); i++) {
					externalMemos.add(libMemos.get(i));
				}
			}

			final MemoAdapter memoAdapter = new MemoAdapter(context);
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
					if (internal.getLastReviewed().compareTo(external.getLastReviewed()) > 0) {
						return internal;
					} else {
						return external;
					}
				}

				@Override
				protected boolean submitTransaction(ArrayList<Memo> internalToDelete, ArrayList<Memo> externalToAdd) {
					
					for(int i=0; i< internalToDelete.size(); i++) {
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

			};

			syncBase.sync();

		} catch (Exception ex) {
			ex.printStackTrace();
			onComplete.onComplete(false);
		}
	}
	


	public static void importCsvFile(final String destinationMemoBaseId, final Context context, final String path,
			final OnConflictResolveHaltable<Memo> onConflictMemo, final OnSyncComplete onComplete) {

		try {
			
			final ArrayList<Memo> externalMemos = CsvParser.parseFile(path);
			File fInfo = new File(path);
			final Date externalDate = new Date(fInfo.lastModified());
			
			final MemoAdapter memoAdapter = new MemoAdapter(context);
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
					
					for(int i=0; i< internalToDelete.size(); i++) {
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

			};

			syncBase.sync();

		} catch (Exception ex) {
			ex.printStackTrace();
			onComplete.onComplete(false);
		}
	}
}
