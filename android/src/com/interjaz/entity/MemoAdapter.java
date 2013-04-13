package com.interjaz.entity;

import java.io.IOException;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.interjaz.Language;
import com.interjaz.db.Order;
import com.interjaz.db.SqliteAdapter;
import com.interjaz.helper.CacheHelper;
import com.interjaz.helper.DatabaseHelper;
import com.interjaz.helper.DateHelper;

public class MemoAdapter extends SqliteAdapter {

	public final static String DatabaseName = "TranslateMemo";
	public final static int m_databaseVersion = 1;
	public final static String TableName = "Memos";

	private static int m_memoCacheSize = 10;
	private static CacheHelper<String, Memo> m_memoCache = new CacheHelper<String, Memo>(m_memoCacheSize);

	private static int m_memoListCacheSize = 5;
	private static CacheHelper<String, ArrayList<Memo>> m_memoListCache = new CacheHelper<String, ArrayList<Memo>>(
			m_memoListCacheSize);
	
	public enum Sort {
		WordA, WordB, CreatedDate, ReviewedDate, Displayed, CorrectAnsweredWordA, CorrectAnsweredWordB 
	}

	public MemoAdapter(Context context) throws IOException {
		super(context, DatabaseName, m_databaseVersion);
	}

	public long add(Memo memo) {
		SQLiteDatabase db = null;
		invalidateCache();

		try {
			db = getDatabase();

			try {
				WordAdapter wordAdapter = new WordAdapter(super.getContext());
				if (wordAdapter.add(memo.getWordA()) == -1) {
					return -1;
				}
				if (wordAdapter.add(memo.getWordB()) == -1) {
					return -1;
				}

			} catch (IOException e) {
				e.printStackTrace();
				return -1;
			}

			ContentValues values = createValues(memo);
			return db.insert(TableName, null, values);
		} finally {
			if (db != null) {
				close();
			}
		}
	}

	public Memo get(String memoId) {
		SQLiteDatabase db = null;

		if (inSync() && m_memoCache.containsKey(memoId)) {
			return m_memoCache.get(memoId);
		}

		try {
			db = getDatabase();

			String query = "SELECT "
					+ "	M.MemoId M_MemoId, M.Created M_Created, M.LastReviewed M_LastReviewed, M.Displayed M_Displayed, M.CorrectAnsweredWordA M_CorrectAnsweredWordA, M.CorrectAnsweredWordB M_CorrectAnsweredWordB, M.Active M_Active, "
					+ "	B.MemoBaseId B_MemoBaseId, B.Name B_Name, B.Created B_Created, B.Active B_Active, "
					+ "	WA.WordId WA_WordId, WA.LanguageIso639 WA_LanguageIso639, WA.Word WA_Word, "
					+ "	WB.WordId WB_WordId, WB.LanguageIso639 WB_LanguageIso639, WB.Word WB_Word "
					+ "FROM Memos  AS M " + "JOIN MemoBases AS B ON M.MemoBaseId = B.MemoBaseId "
					+ "JOIN Words AS WA ON M.WordAId = WA.WordId " + "JOIN Words AS WB ON M.WordBId = WB.WordId "
					+ "WHERE M.MemoId = ?";

			Cursor cursor = db.rawQuery(query, new String[] { memoId });

			if (cursor.moveToNext()) {

				Word wordA = new Word();
				wordA.setWord(DatabaseHelper.getString(cursor, "WA_Word"));
				wordA.setLanguage(Language.parse(DatabaseHelper.getString(cursor, "WA_LanguageIso639")));
				wordA.setWordId(DatabaseHelper.getString(cursor, "WA_WordId"));

				Word wordB = new Word();
				wordB.setWord(DatabaseHelper.getString(cursor, "WB_Word"));
				wordB.setLanguage(Language.parse(DatabaseHelper.getString(cursor, "WB_LanguageIso639")));
				wordB.setWordId(DatabaseHelper.getString(cursor, "WB_WordId"));

				MemoBase memoBase = new MemoBase();
				memoBase.setActive(DatabaseHelper.getBoolean(cursor, "B_Active"));
				memoBase.setCreated(DatabaseHelper.getDate(cursor, "B_Created"));
				memoBase.setMemoBaseId(DatabaseHelper.getString(cursor, "B_MemoBaseId"));
				memoBase.setName(DatabaseHelper.getString(cursor, "B_Name"));

				Memo memo = new Memo();
				memo.setMemoId(DatabaseHelper.getString(cursor, "M_MemoId"));
				memo.setActive(DatabaseHelper.getBoolean(cursor, "M_Active"));
				memo.setCorrectAnsweredWordA(DatabaseHelper.getInt(cursor, "M_CorrectAnsweredWordA")); memo.setCorrectAnsweredWordB(DatabaseHelper.getInt(cursor, "M_CorrectAnsweredWordB"));
				memo.setCreated(DatabaseHelper.getDate(cursor, "M_Created"));
				memo.setDisplayed(DatabaseHelper.getInt(cursor, "M_Displayed"));
				memo.setLastReviewed(DatabaseHelper.getDate(cursor, "M_LastReviewed"));
				memo.setMemoBase(memoBase);
				memo.setMemoBaseId(memoBase.getMemoBaseId());
				memo.setWordA(wordA);
				memo.setWordB(wordB);

				m_memoCache.put(memoId, memo);

				return memo;
			}

			return null;
		} finally {
			if (db != null) {
				close();
			}
		}

	}

	public ArrayList<Memo> getAll(String memoBaseId, Sort sort, Order order) {
		SQLiteDatabase db = null;

		String cacheKey = memoBaseId + sort.toString() + order.toString();

		if (inSync() && m_memoListCache.containsKey(cacheKey)) {
			return m_memoListCache.get(cacheKey);
		}

		try {
			MemoBase memoBase = null;
			ArrayList<Memo> memos = new ArrayList<Memo>();

			db = getDatabase();

			try {
				memoBase = new MemoBaseAdapter(super.getContext()).get(memoBaseId);
			} catch (IOException ex) {
				return null;
			}

			if (memoBase == null) {
				return null;
			}

			// For performance reasons get all records at once, instead by
			// calling many times MemoAdapter;
			String query = "SELECT "
					+ "	M.MemoId M_MemoId, M.Created M_Created, M.LastReviewed M_LastReviewed, M.Displayed M_Displayed, M.CorrectAnsweredWordA M_CorrectAnsweredWordA, M.CorrectAnsweredWordB M_CorrectAnsweredWordB, M.Active M_Active, "
					+ "	B.MemoBaseId B_MemoBaseId, B.Name B_Name, B.Created B_Created, B.Active B_Active, "
					+ "	WA.WordId WA_WordId, WA.LanguageIso639 WA_LanguageIso639, WA.Word WA_Word, "
					+ "	WB.WordId WB_WordId, WB.LanguageIso639 WB_LanguageIso639, WB.Word WB_Word "
					+ "FROM Memos  AS M " + "JOIN MemoBases AS B ON M.MemoBaseId = B.MemoBaseId "
					+ "JOIN Words AS WA ON M.WordAId = WA.WordId " + "JOIN Words AS WB ON M.WordBId = WB.WordId "
					+ "WHERE M.MemoBaseId = ?";
			query += buildSort(sort, order);

			Cursor cursor = db.rawQuery(query, new String[] { memoBaseId });

			while (cursor.moveToNext()) {

				Word wordA = new Word();
				wordA.setWord(DatabaseHelper.getString(cursor, "WA_Word"));
				wordA.setLanguage(Language.parse(DatabaseHelper.getString(cursor, "WA_LanguageIso639")));
				wordA.setWordId(DatabaseHelper.getString(cursor, "WA_WordId"));

				Word wordB = new Word();
				wordB.setWord(DatabaseHelper.getString(cursor, "WB_Word"));
				wordB.setLanguage(Language.parse(DatabaseHelper.getString(cursor, "WB_LanguageIso639")));
				wordB.setWordId(DatabaseHelper.getString(cursor, "WB_WordId"));

				Memo memo = new Memo();
				memo.setMemoId(DatabaseHelper.getString(cursor, "M_MemoId"));
				memo.setActive(DatabaseHelper.getBoolean(cursor, "M_Active"));
				memo.setCorrectAnsweredWordA(DatabaseHelper.getInt(cursor, "M_CorrectAnsweredWordA")); memo.setCorrectAnsweredWordB(DatabaseHelper.getInt(cursor, "M_CorrectAnsweredWordB"));
				memo.setCreated(DatabaseHelper.getDate(cursor, "M_Created"));
				memo.setDisplayed(DatabaseHelper.getInt(cursor, "M_Displayed"));
				memo.setLastReviewed(DatabaseHelper.getDate(cursor, "M_LastReviewed"));
				memo.setMemoBase(memoBase);
				memo.setMemoBaseId(memoBase.getMemoBaseId());
				memo.setWordA(wordA);
				memo.setWordB(wordB);

				memos.add(memo);
			}

			if (memos.size() > 0) {
				m_memoListCache.put(cacheKey, memos);
			}

			return memos;
		} finally {
			if (db != null) {
				close();
			}
		}
	}

	public ArrayList<Memo> getTrainSet(String memoBaseId, int size) {
		SQLiteDatabase db = null;

		float unknownToKnwonRatio = 0.8f;
		int unknown = Math.max((int) (unknownToKnwonRatio * size), 1);
		int known = Math.max((int) ((1 - unknownToKnwonRatio) * size), 1);

		try {
			MemoBase memoBase = null;
			ArrayList<Memo> memos = new ArrayList<Memo>();

			db = getDatabase();

			try {
				memoBase = new MemoBaseAdapter(super.getContext()).get(memoBaseId);
			} catch (IOException ex) {
				return null;
			}

			if (memoBase == null) {
				return null;
			}

			// For performance reasons get all records at once, instead by
			// calling many times MemoAdapter;
			Cursor cursor;
			String query;

			// Get Known
			query = "SELECT "
					+ "	M.MemoId M_MemoId, M.Created M_Created, M.LastReviewed M_LastReviewed, M.Displayed M_Displayed, M.CorrectAnsweredWordA M_CorrectAnsweredWordA, M.CorrectAnsweredWordB M_CorrectAnsweredWordB, M.Active M_Active, "
					+ "	B.MemoBaseId B_MemoBaseId, B.Name B_Name, B.Created B_Created, B.Active B_Active, "
					+ "	WA.WordId WA_WordId, WA.LanguageIso639 WA_LanguageIso639, WA.Word WA_Word, "
					+ "	WB.WordId WB_WordId, WB.LanguageIso639 WB_LanguageIso639, WB.Word WB_Word "
					+ "FROM Memos  AS M " + "JOIN MemoBases AS B ON M.MemoBaseId = B.MemoBaseId "
					+ "JOIN Words AS WA ON M.WordAId = WA.WordId " + "JOIN Words AS WB ON M.WordBId = WB.WordId "
					+ "WHERE M.MemoBaseId = ? AND M.Displayed > 0 " + "ORDER BY (M.CorrectAnsweredWordA + M.CorrectAnsweredWordB)/M.Displayed DESC "
					+ "LIMIT ?";

			cursor = db.rawQuery(query, new String[] { memoBaseId, Integer.toString(known) });

			while (cursor.moveToNext()) {

				Word wordA = new Word();
				wordA.setWord(DatabaseHelper.getString(cursor, "WA_Word"));
				wordA.setLanguage(Language.parse(DatabaseHelper.getString(cursor, "WA_LanguageIso639")));
				wordA.setWordId(DatabaseHelper.getString(cursor, "WA_WordId"));

				Word wordB = new Word();
				wordB.setWord(DatabaseHelper.getString(cursor, "WB_Word"));
				wordB.setLanguage(Language.parse(DatabaseHelper.getString(cursor, "WB_LanguageIso639")));
				wordB.setWordId(DatabaseHelper.getString(cursor, "WB_WordId"));

				Memo memo = new Memo();
				memo.setMemoId(DatabaseHelper.getString(cursor, "M_MemoId"));
				memo.setActive(DatabaseHelper.getBoolean(cursor, "M_Active"));
				memo.setCorrectAnsweredWordA(DatabaseHelper.getInt(cursor, "M_CorrectAnsweredWordA")); memo.setCorrectAnsweredWordB(DatabaseHelper.getInt(cursor, "M_CorrectAnsweredWordB"));
				memo.setCreated(DatabaseHelper.getDate(cursor, "M_Created"));
				memo.setDisplayed(DatabaseHelper.getInt(cursor, "M_Displayed"));
				memo.setLastReviewed(DatabaseHelper.getDate(cursor, "M_LastReviewed"));
				memo.setMemoBase(memoBase);
				memo.setMemoBaseId(memoBase.getMemoBaseId());
				memo.setWordA(wordA);
				memo.setWordB(wordB);

				memos.add(memo);
			}

			// Get Unknown
			// Use trick on M.Displayed so it is always bigger than zero
			query = "SELECT "
					+ "	M.MemoId M_MemoId, M.Created M_Created, M.LastReviewed M_LastReviewed, M.Displayed M_Displayed, M.CorrectAnsweredWordA M_CorrectAnsweredWordA, M.CorrectAnsweredWordB M_CorrectAnsweredWordB, M.Active M_Active, "
					+ "	B.MemoBaseId B_MemoBaseId, B.Name B_Name, B.Created B_Created, B.Active B_Active, "
					+ "	WA.WordId WA_WordId, WA.LanguageIso639 WA_LanguageIso639, WA.Word WA_Word, "
					+ "	WB.WordId WB_WordId, WB.LanguageIso639 WB_LanguageIso639, WB.Word WB_Word "
					+ "FROM Memos  AS M " + "JOIN MemoBases AS B ON M.MemoBaseId = B.MemoBaseId "
					+ "JOIN Words AS WA ON M.WordAId = WA.WordId " + "JOIN Words AS WB ON M.WordBId = WB.WordId "
					+ "WHERE M.MemoBaseId = ? " + "ORDER BY (M.CorrectAnsweredWordA + M.CorrectAnsweredWordB)/(M.Displayed+1) ASC " + "LIMIT ?";

			cursor = db.rawQuery(query, new String[] { memoBaseId, Integer.toString(unknown) });

			while (cursor.moveToNext()) {

				Word wordA = new Word();
				wordA.setWord(DatabaseHelper.getString(cursor, "WA_Word"));
				wordA.setLanguage(Language.parse(DatabaseHelper.getString(cursor, "WA_LanguageIso639")));
				wordA.setWordId(DatabaseHelper.getString(cursor, "WA_WordId"));

				Word wordB = new Word();
				wordB.setWord(DatabaseHelper.getString(cursor, "WB_Word"));
				wordB.setLanguage(Language.parse(DatabaseHelper.getString(cursor, "WB_LanguageIso639")));
				wordB.setWordId(DatabaseHelper.getString(cursor, "WB_WordId"));

				Memo memo = new Memo();
				memo.setMemoId(DatabaseHelper.getString(cursor, "M_MemoId"));
				memo.setActive(DatabaseHelper.getBoolean(cursor, "M_Active"));
				memo.setCorrectAnsweredWordA(DatabaseHelper.getInt(cursor, "M_CorrectAnsweredWordA"));
				memo.setCorrectAnsweredWordB(DatabaseHelper.getInt(cursor, "M_CorrectAnsweredWordB"));
				memo.setCreated(DatabaseHelper.getDate(cursor, "M_Created"));
				memo.setDisplayed(DatabaseHelper.getInt(cursor, "M_Displayed"));
				memo.setLastReviewed(DatabaseHelper.getDate(cursor, "M_LastReviewed"));
				memo.setMemoBase(memoBase);
				memo.setMemoBaseId(memoBase.getMemoBaseId());
				memo.setWordA(wordA);
				memo.setWordB(wordB);

				memos.add(memo);
			}

			return memos;
		} finally {
			if (db != null) {
				close();
			}
		}
	}

	public int update(Memo memo) {
		SQLiteDatabase db = null;
		invalidateCache();

		try {
			db = getDatabase();
			WordAdapter wordAdapter = null;

			try {
				wordAdapter = new WordAdapter(super.getContext());
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (wordAdapter == null) {
				return 0;
			}

			if (wordAdapter.update(memo.getWordA()) == 0) {
				return 0;
			}
			if (wordAdapter.update(memo.getWordB()) == 0) {
				return 0;
			}

			return db.update(TableName, createValues(memo), "MemoId = ?", new String[] { memo.getMemoId() });

		} finally {
			if (db != null) {
				close();
			}
		}

	}

	public void delete(String memoId) {

		SQLiteDatabase db = null;
		invalidateCache();

		try {
			db = getDatabase();

			Memo memo = get(memoId);
			WordAdapter wordAdapter = new WordAdapter(getContext());

			if (memo.getWordAId() != null) {
				wordAdapter.delete(memo.getWordAId());
			}

			if (memo.getWordBId() != null) {
				wordAdapter.delete(memo.getWordBId());
			}

			db.delete(TableName, "MemoId" + "=?", new String[] { memoId });

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (db != null) {
				close();
			}
		}
	}

	private ContentValues createValues(Memo memo) {
		ContentValues values = new ContentValues();

		values.put("MemoId", memo.getMemoId());
		values.put("MemoBaseId", memo.getMemoBaseId());
		values.put("WordAId", memo.getWordAId());
		values.put("WordBId", memo.getWordBId());
		values.put("Created", DateHelper.toNormalizedString(memo.getCreated()));
		values.put("LastReviewed", DateHelper.toNormalizedString(memo.getLastReviewed()));
		values.put("Displayed", memo.getDisplayed());
		values.put("CorrectAnsweredWordA", memo.getCorrectAnsweredWordA());
		values.put("CorrectAnsweredWordB", memo.getCorrectAnsweredWordB());
		values.put("Active", memo.getActive());

		return values;
	}

	private String buildSort(Sort sort, Order order) {
		String query = " ORDER BY";

		switch (sort) {
		case WordA:
			query += " WA_Word";
			break;
		case WordB:
			query += " WB_Word";
			break;
		case CreatedDate:
			query += " M_Created";
			break;
		case ReviewedDate:
			query += " M_ReviewedDate";
			break;
		case Displayed:
			query += " M_Displayed";
			break;
		case CorrectAnsweredWordA:
			query += " M_CorrectAnsweredWordA";
			break;
		case CorrectAnsweredWordB:
			query += " M_CorrectAnsweredWordB";
			break;
		}

		switch (order) {
		case ASC:
			query += " ASC";
			break;
		case DESC:
			query += " DESC";
			break;
		}

		return query;
	}

	@Override
	protected void onInvalidateCache() {
		super.onInvalidateCache();

		m_memoCache.clear();
		m_memoListCache.clear();
	}

}
