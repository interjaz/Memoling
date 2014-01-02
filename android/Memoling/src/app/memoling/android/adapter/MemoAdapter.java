package app.memoling.android.adapter;

import java.util.ArrayList;
import java.util.Random;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import app.memoling.android.db.DatabaseHelper;
import app.memoling.android.db.DatabaseHelper.Order;
import app.memoling.android.db.SqliteAdapter;
import app.memoling.android.entity.Language;
import app.memoling.android.entity.Memo;
import app.memoling.android.entity.MemoBase;
import app.memoling.android.entity.Word;
import app.memoling.android.helper.CacheHelper;
import app.memoling.android.helper.DateHelper;

public class MemoAdapter extends SqliteAdapter {

	public final static String TableName = "Memos";

	private static int m_memoCacheSize = 10;
	private static CacheHelper<String, Memo> m_memoCache = new CacheHelper<String, Memo>(m_memoCacheSize);

	private static int m_memoListCacheSize = 5;
	private static CacheHelper<String, ArrayList<Memo>> m_memoListCache = new CacheHelper<String, ArrayList<Memo>>(
			m_memoListCacheSize);

	public enum Sort {
		WordA, WordB, CreatedDate, ReviewedDate, Displayed, CorrectAnsweredWordA, CorrectAnsweredWordB
	}

	public MemoAdapter(Context context) {
		super(context);
	}

	public MemoAdapter(Context context, boolean persistant) {
		super(context, persistant);
	}

	public long add(Memo memo) {
		SQLiteDatabase db = null;
		db = getDatabase();

		try {
			return add(this, db, memo);
		} finally {
			closeDatabase();
		}
	}

	public static long add(SqliteAdapter adapter, SQLiteDatabase db, Memo memo) {
		adapter.invalidateGlobalCache();

		if (WordAdapter.add(adapter, db, memo.getWordA()) == DatabaseHelper.Error) {
			return DatabaseHelper.Error;
		}
		if (WordAdapter.add(adapter, db, memo.getWordB()) == DatabaseHelper.Error) {
			return DatabaseHelper.Error;
		}

		ContentValues values = createValues(memo);
		return db.insert(TableName, null, values);
	}

	public Memo get(String memoId) {

		if (inSync() && m_memoCache.containsKey(memoId)) {
			return m_memoCache.get(memoId);
		}
		
		SQLiteDatabase db = null;

		try {
			db = getDatabase();
			return get(this, db, memoId);

		} finally {
			closeDatabase();
		}
	}

	public static Memo get(SqliteAdapter adapter, SQLiteDatabase db, String memoId) {

		if (!adapter.inSync()) {
			adapter.invalidateLocalCache();
		}

		if (m_memoCache.containsKey(memoId)) {
			return m_memoCache.get(memoId);
		}

		String query = "SELECT "
				+ "	M.MemoId M_MemoId, M.Created M_Created, M.LastReviewed M_LastReviewed, M.Displayed M_Displayed, M.CorrectAnsweredWordA M_CorrectAnsweredWordA, M.CorrectAnsweredWordB M_CorrectAnsweredWordB, M.Active M_Active, "
				+ "	B.MemoBaseId B_MemoBaseId, B.Name B_Name, B.Created B_Created, B.Active B_Active, "
				+ "	WA.WordId WA_WordId, WA.LanguageIso639 WA_LanguageIso639, WA.Word WA_Word, WA.Description WA_Description, "
				+ "	WB.WordId WB_WordId, WB.LanguageIso639 WB_LanguageIso639, WB.Word WB_Word, WB.Description WB_Description "
				+ "FROM Memos  AS M " + "JOIN MemoBases AS B ON M.MemoBaseId = B.MemoBaseId "
				+ "JOIN Words AS WA ON M.WordAId = WA.WordId " + "JOIN Words AS WB ON M.WordBId = WB.WordId "
				+ "WHERE M.MemoId = ?";

		Cursor cursor = db.rawQuery(query, new String[] { memoId });

		try {
			if (cursor.moveToNext()) {

				Word wordA = new Word();
				wordA.setWord(DatabaseHelper.getString(cursor, "WA_Word"));
				wordA.setLanguage(Language.parse(DatabaseHelper.getString(cursor, "WA_LanguageIso639")));
				wordA.setWordId(DatabaseHelper.getString(cursor, "WA_WordId"));
				wordA.setDescription(DatabaseHelper.getString(cursor, "WA_Description"));

				Word wordB = new Word();
				wordB.setWord(DatabaseHelper.getString(cursor, "WB_Word"));
				wordB.setLanguage(Language.parse(DatabaseHelper.getString(cursor, "WB_LanguageIso639")));
				wordB.setWordId(DatabaseHelper.getString(cursor, "WB_WordId"));
				wordB.setDescription(DatabaseHelper.getString(cursor, "WB_Description"));

				MemoBase memoBase = new MemoBase();
				memoBase.setActive(DatabaseHelper.getBoolean(cursor, "B_Active"));
				memoBase.setCreated(DatabaseHelper.getDate(cursor, "B_Created"));
				memoBase.setMemoBaseId(DatabaseHelper.getString(cursor, "B_MemoBaseId"));
				memoBase.setName(DatabaseHelper.getString(cursor, "B_Name"));

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

				m_memoCache.put(memoId, memo);

				return memo;
			}
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}

		return null;
	}

	public Memo getRandom(String memoBaseId) {

		SQLiteDatabase db = null;

		try {
			db = getDatabase();
			return getRandom(this, db, memoBaseId);

		} finally {
			closeDatabase();
		}
	}

	public static Memo getRandom(SqliteAdapter adapter, SQLiteDatabase db, String memoBaseId) {

		String query = "SELECT "
				+ "	M.MemoId M_MemoId, M.Created M_Created, M.LastReviewed M_LastReviewed, M.Displayed M_Displayed, M.CorrectAnsweredWordA M_CorrectAnsweredWordA, M.CorrectAnsweredWordB M_CorrectAnsweredWordB, M.Active M_Active, "
				+ "	B.MemoBaseId B_MemoBaseId, B.Name B_Name, B.Created B_Created, B.Active B_Active, "
				+ "	WA.WordId WA_WordId, WA.LanguageIso639 WA_LanguageIso639, WA.Word WA_Word, WA.Description WA_Description, "
				+ "	WB.WordId WB_WordId, WB.LanguageIso639 WB_LanguageIso639, WB.Word WB_Word, WB.Description WB_Description "
				+ "FROM Memos  AS M " + "JOIN MemoBases AS B ON M.MemoBaseId = B.MemoBaseId "
				+ "JOIN Words AS WA ON M.WordAId = WA.WordId " 
				+ "JOIN Words AS WB ON M.WordBId = WB.WordId "
				+ "WHERE B.MemoBaseId = ? "
				+ "ORDER BY RANDOM() "
				+ "LIMIT 1";

		Cursor cursor = db.rawQuery(query, new String[] { memoBaseId });

		try {
			if (cursor.moveToNext()) {

				Word wordA = new Word();
				wordA.setWord(DatabaseHelper.getString(cursor, "WA_Word"));
				wordA.setLanguage(Language.parse(DatabaseHelper.getString(cursor, "WA_LanguageIso639")));
				wordA.setWordId(DatabaseHelper.getString(cursor, "WA_WordId"));
				wordA.setDescription(DatabaseHelper.getString(cursor, "WA_Description"));

				Word wordB = new Word();
				wordB.setWord(DatabaseHelper.getString(cursor, "WB_Word"));
				wordB.setLanguage(Language.parse(DatabaseHelper.getString(cursor, "WB_LanguageIso639")));
				wordB.setWordId(DatabaseHelper.getString(cursor, "WB_WordId"));
				wordB.setDescription(DatabaseHelper.getString(cursor, "WB_Description"));

				MemoBase memoBase = new MemoBase();
				memoBase.setActive(DatabaseHelper.getBoolean(cursor, "B_Active"));
				memoBase.setCreated(DatabaseHelper.getDate(cursor, "B_Created"));
				memoBase.setMemoBaseId(DatabaseHelper.getString(cursor, "B_MemoBaseId"));
				memoBase.setName(DatabaseHelper.getString(cursor, "B_Name"));

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

				return memo;
			}
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}

		return null;
	}

	public ArrayList<Memo> getAll(String memoBaseId, Sort sort, Order order) {
		SQLiteDatabase db = null;

		String cacheKey = memoBaseId + sort.toString() + order.toString();

		if (inSync() && m_memoListCache.containsKey(cacheKey)) {
			return m_memoListCache.get(cacheKey);
		}

		try {
			db = getDatabase();
			return getAll(this, db, memoBaseId, sort, order);
		} finally {
			closeDatabase();
		}
	}

	public static ArrayList<Memo> getAll(SqliteAdapter adapter, SQLiteDatabase db, String memoBaseId, Sort sort,
			Order order) {

		String cacheKey = memoBaseId + sort.toString() + order.toString();

		if (adapter.inSync() && m_memoListCache.containsKey(cacheKey)) {
			return m_memoListCache.get(cacheKey);
		}

		MemoBase memoBase = null;
		ArrayList<Memo> memos = new ArrayList<Memo>();

		memoBase = MemoBaseAdapter.get(adapter, db, memoBaseId);

		if (memoBase == null) {
			return null;
		}

		// For performance reasons get all records at once, instead by
		// calling many times MemoAdapter;
		String query = "SELECT "
				+ "	M.MemoId M_MemoId, M.Created M_Created, M.LastReviewed M_LastReviewed, M.Displayed M_Displayed, M.CorrectAnsweredWordA M_CorrectAnsweredWordA, M.CorrectAnsweredWordB M_CorrectAnsweredWordB, M.Active M_Active, "
				+ "	WA.WordId WA_WordId, WA.LanguageIso639 WA_LanguageIso639, WA.Word WA_Word, WA.Description WA_Description, "
				+ "	WB.WordId WB_WordId, WB.LanguageIso639 WB_LanguageIso639, WB.Word WB_Word, WB.Description WB_Description "
				+ "FROM Memos  AS M " + "JOIN MemoBases AS B ON M.MemoBaseId = B.MemoBaseId "
				+ "JOIN Words AS WA ON M.WordAId = WA.WordId " + "JOIN Words AS WB ON M.WordBId = WB.WordId "
				+ "WHERE M.MemoBaseId = ?";
		query += buildSort(sort, order);

		Cursor cursor = db.rawQuery(query, new String[] { memoBaseId });

		try {
			while (cursor.moveToNext()) {

				Word wordA = new Word();
				wordA.setWord(DatabaseHelper.getString(cursor, "WA_Word"));
				wordA.setLanguage(Language.parse(DatabaseHelper.getString(cursor, "WA_LanguageIso639")));
				wordA.setWordId(DatabaseHelper.getString(cursor, "WA_WordId"));
				wordA.setDescription(DatabaseHelper.getString(cursor, "WA_Description"));

				Word wordB = new Word();
				wordB.setWord(DatabaseHelper.getString(cursor, "WB_Word"));
				wordB.setLanguage(Language.parse(DatabaseHelper.getString(cursor, "WB_LanguageIso639")));
				wordB.setWordId(DatabaseHelper.getString(cursor, "WB_WordId"));
				wordB.setDescription(DatabaseHelper.getString(cursor, "WB_Description"));

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

			if (memos.size() > 0) {
				m_memoListCache.put(cacheKey, memos);
			}

			memoBase.setMemos(memos);

		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}

		return memos;
	}

	public ArrayList<Memo> getTrainSet(String memoBaseId, int size) {
		SQLiteDatabase db = null;
		Cursor cursor = null;

		float unknownToKnwonRatio = 0.8f;
		int unknown = Math.max((int) (unknownToKnwonRatio * size), 1);
		int known = size - unknown;

		try {
			MemoBase memoBase = null;
			ArrayList<Memo> memos = new ArrayList<Memo>();

			db = getDatabase();

			memoBase = new MemoBaseAdapter(super.getContext()).get(memoBaseId);

			if (memoBase == null) {
				return null;
			}

			// For performance reasons get all records at once, instead by
			// calling many times MemoAdapter;
			String query;

			String foundMemos = "";

			// Get Known
			query = "SELECT "
					+ "	M.MemoId M_MemoId, M.Created M_Created, M.LastReviewed M_LastReviewed, M.Displayed M_Displayed, M.CorrectAnsweredWordA M_CorrectAnsweredWordA, M.CorrectAnsweredWordB M_CorrectAnsweredWordB, M.Active M_Active, "
					+ "	B.MemoBaseId B_MemoBaseId, B.Name B_Name, B.Created B_Created, B.Active B_Active, "
					+ "	WA.WordId WA_WordId, WA.LanguageIso639 WA_LanguageIso639, WA.Word WA_Word, WA.Description WA_Description, "
					+ "	WB.WordId WB_WordId, WB.LanguageIso639 WB_LanguageIso639, WB.Word WB_Word, WB.Description WB_Description "
					+ "FROM Memos  AS M " + "JOIN MemoBases AS B ON M.MemoBaseId = B.MemoBaseId "
					+ "JOIN Words AS WA ON M.WordAId = WA.WordId " + "JOIN Words AS WB ON M.WordBId = WB.WordId "
					+ "WHERE M.MemoBaseId = ? AND " + "M.Active = 1 AND "
					+ "((M.CorrectAnsweredWordA + M.CorrectAnsweredWordB)*1.0)/(M.Displayed+1) > 0.6 "
					+ "ORDER BY M_LastReviewed " + "LIMIT ?";

			cursor = db.rawQuery(query, new String[] { memoBaseId, Integer.toString(known) });

			while (cursor.moveToNext()) {

				Word wordA = new Word();
				wordA.setWord(DatabaseHelper.getString(cursor, "WA_Word"));
				wordA.setLanguage(Language.parse(DatabaseHelper.getString(cursor, "WA_LanguageIso639")));
				wordA.setWordId(DatabaseHelper.getString(cursor, "WA_WordId"));
				wordA.setDescription(DatabaseHelper.getString(cursor, "WA_Description"));

				Word wordB = new Word();
				wordB.setWord(DatabaseHelper.getString(cursor, "WB_Word"));
				wordB.setLanguage(Language.parse(DatabaseHelper.getString(cursor, "WB_LanguageIso639")));
				wordB.setWordId(DatabaseHelper.getString(cursor, "WB_WordId"));
				wordB.setDescription(DatabaseHelper.getString(cursor, "WB_Description"));

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
				foundMemos += "'" + memo.getMemoId() + "',";
			}

			if (foundMemos.length() > 0) {
				// Remove last colon
				foundMemos = foundMemos.substring(0, foundMemos.length() - 1);
			}

			// Get Unknown
			query = "SELECT "
					+ "	M.MemoId M_MemoId, M.Created M_Created, M.LastReviewed M_LastReviewed, M.Displayed M_Displayed, M.CorrectAnsweredWordA M_CorrectAnsweredWordA, M.CorrectAnsweredWordB M_CorrectAnsweredWordB, M.Active M_Active, "
					+ "	B.MemoBaseId B_MemoBaseId, B.Name B_Name, B.Created B_Created, B.Active B_Active, "
					+ "	WA.WordId WA_WordId, WA.LanguageIso639 WA_LanguageIso639, WA.Word WA_Word, WA.Description WA_Description, "
					+ "	WB.WordId WB_WordId, WB.LanguageIso639 WB_LanguageIso639, WB.Word WB_Word, WB.Description WB_Description "
					+ "FROM Memos  AS M " + "JOIN MemoBases AS B ON M.MemoBaseId = B.MemoBaseId "
					+ "JOIN Words AS WA ON M.WordAId = WA.WordId " + "JOIN Words AS WB ON M.WordBId = WB.WordId "
					+ "WHERE M.MemoBaseId = ? AND " + "M.Active = 1 AND " + "M_MemoId NOT IN (" + foundMemos + ") "
					+ "ORDER BY ((M.CorrectAnsweredWordA + M.CorrectAnsweredWordB)*1.0)/(M.Displayed+1) ASC, RANDOM() "
					+ "LIMIT ?";

			// If not found 'known' number try fill with unknown
			unknown += Math.max(known - memos.size(), 0);

			cursor = db.rawQuery(query, new String[] { memoBaseId, Integer.toString(unknown) });

			while (cursor.moveToNext()) {

				Word wordA = new Word();
				wordA.setWord(DatabaseHelper.getString(cursor, "WA_Word"));
				wordA.setLanguage(Language.parse(DatabaseHelper.getString(cursor, "WA_LanguageIso639")));
				wordA.setWordId(DatabaseHelper.getString(cursor, "WA_WordId"));
				wordA.setDescription(DatabaseHelper.getString(cursor, "WA_Description"));

				Word wordB = new Word();
				wordB.setWord(DatabaseHelper.getString(cursor, "WB_Word"));
				wordB.setLanguage(Language.parse(DatabaseHelper.getString(cursor, "WB_LanguageIso639")));
				wordB.setWordId(DatabaseHelper.getString(cursor, "WB_WordId"));
				wordB.setDescription(DatabaseHelper.getString(cursor, "WB_Description"));

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

			// Shuffle array
			Random rand = new Random();
			size = memos.size();
			for (int i = 0; i < size * 10; i++) {
				int from = rand.nextInt(size);
				int to = rand.nextInt(size);
				Memo tmp = memos.get(from);
				memos.set(from, memos.get(to));
				memos.set(to, tmp);
			}

			return memos;
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
			closeDatabase();
		}
	}

	public int update(Memo memo) {
		SQLiteDatabase db = null;
		invalidateGlobalCache();

		try {
			db = getDatabase();
			WordAdapter wordAdapter;

			wordAdapter = new WordAdapter(super.getContext());

			if (wordAdapter.update(memo.getWordA()) == 0) {
				return 0;
			}
			if (wordAdapter.update(memo.getWordB()) == 0) {
				return 0;
			}

			return db.update(TableName, createValues(memo), "MemoId = ?", new String[] { memo.getMemoId() });

		} finally {
			closeDatabase();
		}

	}

	public void delete(String memoId) {

		SQLiteDatabase db = null;
		try {
			db = getDatabase();
			delete(this, db, memoId);
		} finally {
			closeDatabase();
		}

	}

	public static void delete(SqliteAdapter adapter, SQLiteDatabase db, String memoId) {
		adapter.invalidateGlobalCache();

		Memo memo = MemoAdapter.get(adapter, db, memoId);

		if (memo.getWordAId() != null) {
			WordAdapter.delete(adapter, db, memo.getWordAId());
		}

		if (memo.getWordBId() != null) {
			WordAdapter.delete(adapter, db, memo.getWordBId());
		}

		db.delete(TableName, "MemoId" + "=?", new String[] { memoId });
	}

	private static ContentValues createValues(Memo memo) {
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

	private static String buildSort(Sort sort, Order order) {
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
	protected void onInvalidateLocalCache() {
		super.onInvalidateLocalCache();

		m_memoCache.clear();
		m_memoListCache.clear();
	}
	
	@Override
	protected void onInvalidateGlobalCache() {
		super.onInvalidateGlobalCache();

		m_memoCache.clear();
		m_memoListCache.clear();
	}


}
