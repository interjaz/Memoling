package app.memoling.android.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import app.memoling.android.db.DatabaseHelper;
import app.memoling.android.db.DatabaseHelper.Order;
import app.memoling.android.db.SqliteAdapter;
import app.memoling.android.entity.Memo;
import app.memoling.android.entity.MemoBase;
import app.memoling.android.entity.SyncAction;
import app.memoling.android.entity.Word;
import app.memoling.android.helper.DateHelper;
import app.memoling.android.sync.cloud.ISyncAdapter;
import app.memoling.android.sync.cloud.ISyncEntity;

public class MemoAdapter extends SqliteAdapter implements ISyncAdapter {

	public final static String TableName = "Memos";

	public enum Sort {
		WordA, WordB, CreatedDate, ReviewedDate, Displayed, CorrectAnsweredWordA, CorrectAnsweredWordB
	}

	public MemoAdapter(Context context) {
		super(context);
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

	public static Memo bindMemo(Cursor cursor) {
		return MemoAdapter.bindMemo(cursor, "");
	}
	
	public static Memo bindMemo(Cursor cursor, String prefix) {
		Memo memo = new Memo();

		memo.setMemoId(DatabaseHelper.getString(cursor, prefix + "MemoId"));
		memo.setActive(DatabaseHelper.getBoolean(cursor, prefix + "Active"));
		memo.setCorrectAnsweredWordA(DatabaseHelper.getInt(cursor, prefix + "CorrectAnsweredWordA"));
		memo.setCorrectAnsweredWordB(DatabaseHelper.getInt(cursor, prefix + "CorrectAnsweredWordB"));
		memo.setCreated(DatabaseHelper.getDate(cursor, prefix + "Created"));
		memo.setDisplayed(DatabaseHelper.getInt(cursor, prefix + "Displayed"));
		memo.setLastReviewed(DatabaseHelper.getDate(cursor, prefix + "LastReviewed"));
		memo.setMemoBaseId(DatabaseHelper.getString(cursor, prefix + "MemoBaseId"));
		memo.setWordAId(DatabaseHelper.getString(cursor, prefix + "WordAId"));
		memo.setWordBId(DatabaseHelper.getString(cursor, prefix + "WordBId"));
		
		return memo;
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
	
	public Memo get(String memoId) {
		try {
			return MemoAdapter.get(getDatabase(), memoId);
		} finally {
			closeDatabase();
		}
	}

	public Memo getDeep(String memoId) {
		try {
			return MemoAdapter.getDeep(getDatabase(), memoId);
		} finally {
			closeDatabase();
		}
	}
	
	public List<Memo> getAllDeep(String memoBaseId, Sort sort, Order order) {
		try {
			return getAllDeep(getDatabase(), memoBaseId, sort, order);
		} finally {
			closeDatabase();
		}
	}
	
	public Memo getRandomDeep(String memoBaseId) {
		try {
			return getRandomDeep(getDatabase(), memoBaseId);

		} finally {
			closeDatabase();
		}
	}
	
	public void insert(Memo memo, String syncClientId) throws SQLiteException {
		try {
			MemoAdapter.insert(getDatabase(), memo, syncClientId);
		} finally {
			closeDatabase();
		}
	}
	
	public void update(Memo memo, String syncClientId) throws SQLiteException {
		try {
			MemoAdapter.update(getDatabase(), memo, syncClientId);
		} finally {
			closeDatabase();
		}
	}
	
	public void delete(String memoId, String syncClientId) throws SQLiteException {
		try {
			MemoAdapter.delete(getDatabase(), memoId, syncClientId);
		} finally {
			closeDatabase();
		}
	}
	
	public static Memo get(SQLiteDatabase db, String memoId) {
		String sql = "SELECT * FROM Memos WHERE MemoId = ?";
		Cursor cursor = null;
		
		try {
			cursor = db.rawQuery(sql, new String[] { memoId });
			if(cursor.moveToNext()) {
				Memo memo = MemoAdapter.bindMemo(cursor);
				return memo;
			}
			
		} finally {
			if(cursor != null) {
				cursor.close();
			}
		}
		
		return null;
	}
	
	public static ArrayList<Memo> getAll(SQLiteDatabase db, String memoBaseId) {
		String sql = "SELECT * FROM Memos WHERE MemoBaseId = ?";
		ArrayList<Memo> memos = new ArrayList<Memo>();
		Cursor cursor = null;
		
		try {
			cursor = db.rawQuery(sql, new String[] { memoBaseId });
			while(cursor.moveToNext()) {
				Memo memo = MemoAdapter.bindMemo(cursor);
				memos.add(memo);
			}
			
		} finally {
			if(cursor != null) {
				cursor.close();
			}
		}
		
		return memos;
	}

	public static Memo getDeep(SQLiteDatabase db, String memoId) {

		String query = "SELECT "
				+ "	M.MemoId M_MemoId, M.MemoBaseId M_MemoBaseId, M.Created M_Created, M.LastReviewed M_LastReviewed, M.Displayed M_Displayed, M.CorrectAnsweredWordA M_CorrectAnsweredWordA, M.CorrectAnsweredWordB M_CorrectAnsweredWordB, M.Active M_Active, M.WordAId M_WordAId, M.WordBId M_WordBId, "
				+ "	B.MemoBaseId B_MemoBaseId, B.Name B_Name, B.Created B_Created, B.Active B_Active, "
				+ "	WA.WordId WA_WordId, WA.LanguageIso639 WA_LanguageIso639, WA.Word WA_Word, WA.Description WA_Description, "
				+ "	WB.WordId WB_WordId, WB.LanguageIso639 WB_LanguageIso639, WB.Word WB_Word, WB.Description WB_Description "
				+ "FROM Memos  AS M " + "JOIN MemoBases AS B ON M.MemoBaseId = B.MemoBaseId "
				+ "JOIN Words AS WA ON M.WordAId = WA.WordId " + "JOIN Words AS WB ON M.WordBId = WB.WordId "
				+ "WHERE M.MemoId = ?";

		Cursor cursor = null;
		try {
			 cursor = db.rawQuery(query, new String[] { memoId });
			if (cursor.moveToNext()) {

				Word wordA = WordAdapter.bindWord(cursor, "WA_");
				Word wordB = WordAdapter.bindWord(cursor, "WB_");
				MemoBase memoBase = MemoBaseAdapter.bindMemoBase(cursor, "B_");
				Memo memo = MemoAdapter.bindMemo(cursor, "M_");
				
				memo.setMemoBase(memoBase);
				memo.setWordA(wordA);
				memo.setWordB(wordB);

				return memo;
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return null;
	}

	public static Memo getRandomDeep(SQLiteDatabase db, String memoBaseId) {

		String query = "SELECT "
				+ "	M.MemoId M_MemoId, M.MemoBaseId M_MemoBaseId, M.Created M_Created, M.LastReviewed M_LastReviewed, M.Displayed M_Displayed, M.CorrectAnsweredWordA M_CorrectAnsweredWordA, M.CorrectAnsweredWordB M_CorrectAnsweredWordB, M.Active M_Active, M.WordAId M_WordAId, M.WordBId M_WordBId, "
				+ "	B.MemoBaseId B_MemoBaseId, B.Name B_Name, B.Created B_Created, B.Active B_Active, "
				+ "	WA.WordId WA_WordId, WA.LanguageIso639 WA_LanguageIso639, WA.Word WA_Word, WA.Description WA_Description, "
				+ "	WB.WordId WB_WordId, WB.LanguageIso639 WB_LanguageIso639, WB.Word WB_Word, WB.Description WB_Description "
				+ "FROM Memos  AS M " + "JOIN MemoBases AS B ON M.MemoBaseId = B.MemoBaseId "
				+ "JOIN Words AS WA ON M.WordAId = WA.WordId " 
				+ "JOIN Words AS WB ON M.WordBId = WB.WordId "
				+ "WHERE B.MemoBaseId = ? "
				+ "ORDER BY RANDOM() "
				+ "LIMIT 1";

		Cursor cursor = null;

		try {
			cursor = db.rawQuery(query, new String[] { memoBaseId });
			if (cursor.moveToNext()) {

				Word wordA = WordAdapter.bindWord(cursor, "WA_");
				Word wordB = WordAdapter.bindWord(cursor, "WB_");
				MemoBase memoBase = MemoBaseAdapter.bindMemoBase(cursor, "B_");
				Memo memo = MemoAdapter.bindMemo(cursor, "M_");
				
				memo.setMemoBase(memoBase);
				memo.setWordA(wordA);
				memo.setWordB(wordB);

				return memo;
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return null;
	}

	public static List<Memo> getAllDeep(SQLiteDatabase db, String memoBaseId, Sort sort,
			Order order) {

		MemoBase memoBase = null;
		List<Memo> memos = new ArrayList<Memo>();

		memoBase = MemoBaseAdapter.get(db, memoBaseId);

		if (memoBase == null) {
			return null;
		}

		// For performance reasons get all records at once, instead by
		// calling many times MemoAdapter;
		String query = "SELECT "
				+ "	M.MemoId M_MemoId, M.MemoBaseId M_MemoBaseId, M.Created M_Created, M.LastReviewed M_LastReviewed, M.Displayed M_Displayed, M.CorrectAnsweredWordA M_CorrectAnsweredWordA, M.CorrectAnsweredWordB M_CorrectAnsweredWordB, M.Active M_Active, M.WordAId M_WordAId, M.WordBId M_WordBId, "
				+ "	WA.WordId WA_WordId, WA.LanguageIso639 WA_LanguageIso639, WA.Word WA_Word, WA.Description WA_Description, "
				+ "	WB.WordId WB_WordId, WB.LanguageIso639 WB_LanguageIso639, WB.Word WB_Word, WB.Description WB_Description "
				+ "FROM Memos  AS M " + "JOIN MemoBases AS B ON M.MemoBaseId = B.MemoBaseId "
				+ "JOIN Words AS WA ON M.WordAId = WA.WordId " + "JOIN Words AS WB ON M.WordBId = WB.WordId "
				+ "WHERE M.MemoBaseId = ?";
		query += buildSort(sort, order);

		Cursor cursor = null;

		try {
			cursor = db.rawQuery(query, new String[] { memoBaseId });
			while (cursor.moveToNext()) {

				Word wordA = WordAdapter.bindWord(cursor, "WA_");
				Word wordB = WordAdapter.bindWord(cursor, "WB_");
				Memo memo = MemoAdapter.bindMemo(cursor, "M_");
				
				memo.setMemoBase(memoBase);
				memo.setWordA(wordA);
				memo.setWordB(wordB);

				memos.add(memo);
			}

			memoBase.setMemos(memos);

		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return memos;
	}

	public List<Memo> getTrainSet(String memoBaseId, int size) {
		SQLiteDatabase db = null;
		Cursor cursor = null;

		float unknownToKnwonRatio = 0.8f;
		int unknown = Math.max((int) (unknownToKnwonRatio * size), 1);
		int known = size - unknown;

		try {
			MemoBase memoBase = null;
			List<Memo> memos = new ArrayList<Memo>();

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
					+ "	M.MemoId M_MemoId, M.MemoBaseId M_MemoBaseId, M.Created M_Created, M.LastReviewed M_LastReviewed, M.Displayed M_Displayed, M.CorrectAnsweredWordA M_CorrectAnsweredWordA, M.CorrectAnsweredWordB M_CorrectAnsweredWordB, M.Active M_Active, M.WordAId M_WordAId, M.WordBId M_WordBId, "
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

				Word wordA = WordAdapter.bindWord(cursor, "WA_");
				Word wordB = WordAdapter.bindWord(cursor, "WB_");
				Memo memo = MemoAdapter.bindMemo(cursor, "M_");
				
				memo.setMemoBase(memoBase);
				memo.setWordA(wordA);
				memo.setWordB(wordB);

				memos.add(memo);
				foundMemos += "'" + memo.getMemoId() + "',";
			}
			
			cursor.close();

			if (foundMemos.length() > 0) {
				// Remove last colon
				foundMemos = foundMemos.substring(0, foundMemos.length() - 1);
			}

			// Get Unknown
			query = "SELECT "
					+ "	M.MemoId M_MemoId, M.MemoBaseId M_MemoBaseId, M.Created M_Created, M.LastReviewed M_LastReviewed, M.Displayed M_Displayed, M.CorrectAnsweredWordA M_CorrectAnsweredWordA, M.CorrectAnsweredWordB M_CorrectAnsweredWordB, M.Active M_Active, M.WordAId M_WordAId, M.WordBId M_WordBId, "
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

				Word wordA = WordAdapter.bindWord(cursor, "WA_");
				Word wordB = WordAdapter.bindWord(cursor, "WB_");
				Memo memo = MemoAdapter.bindMemo(cursor, "M_");
				
				memo.setMemoBase(memoBase);
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
			if (cursor != null) {
				cursor.close();
			}
			closeDatabase();
		}
	}
	
	public static void insert(SQLiteDatabase db, Memo memo, String syncClientId) throws SQLiteException {
		SyncAction syncAction = new SyncAction();
		syncAction.setAction(SyncAction.ACTION_INSERT);
		syncAction.setPrimaryKey(memo.getMemoId());
		syncAction.setTable(TableName);
		syncAction.setSyncClientId(syncClientId);
		
		MemoAdapter.insertDbSync(db, memo, syncAction);
	}

	public static void update(SQLiteDatabase db, Memo memo, String syncClientId) throws SQLiteException {
		ArrayList<String> updateColumns = new ArrayList<String>();
		Memo dbMemo = MemoAdapter.get(db, memo.getMemoId());

		if(!dbMemo.getLastReviewed().equals(memo.getLastReviewed())) {
			updateColumns.add("LastReviewed");
		}
		
		if(dbMemo.getDisplayed() != memo.getDisplayed()) {
			updateColumns.add("Displayed");
		}

		if(dbMemo.getCorrectAnsweredWordA() != memo.getCorrectAnsweredWordA()) {
			updateColumns.add("CorrectAnsweredWordA");
		}
		
		if(dbMemo.getCorrectAnsweredWordB() != memo.getCorrectAnsweredWordB()) {
			updateColumns.add("CorrectAnsweredWordB");
		}
		
		if(dbMemo.getActive() != memo.getActive()) {
			updateColumns.add("Active");
		}
		
		for(String updateColumn : updateColumns) {
			SyncAction syncAction = new SyncAction();
			syncAction.setAction(SyncAction.ACTION_UPDATE);
			syncAction.setPrimaryKey(memo.getMemoId());
			syncAction.setTable(TableName);
			syncAction.setSyncClientId(syncClientId);
			syncAction.setUpdateColumn(updateColumn);
			
			MemoAdapter.updateDbSync(db, memo, syncAction);
		}
		
	}

	public static void delete(SQLiteDatabase db, String memoId, String syncClientId) {
		SyncAction syncAction = new SyncAction();
		syncAction.setAction(SyncAction.ACTION_DELETE);
		syncAction.setPrimaryKey(memoId);
		syncAction.setTable(TableName);
		syncAction.setSyncClientId(syncClientId);
		
		MemoAdapter.deleteDbSync(db, memoId, syncAction);
	}

	@Override
	public ISyncEntity decodeEntity(JSONObject json) throws JSONException {
		Memo memo = new Memo();
		memo.decodeEntity(json);		
		return memo;
	}

	@Override
	public ISyncEntity getEntity(String primaryKey) {
		return MemoAdapter.get(this.getDatabase(), primaryKey);
	}

	@Override
	public void insertEntity(SQLiteDatabase db, ISyncEntity object, SyncAction syncAction) throws SQLiteException {
		MemoAdapter.insertDbSync(db, (Memo)object, syncAction);
	}

	@Override
	public void deleteEntity(SQLiteDatabase db, String primaryKey, SyncAction syncAction) throws SQLiteException {
		MemoAdapter.deleteDbSync(db, primaryKey, syncAction);
	}

	@Override
	public void updateEntity(SQLiteDatabase db, ISyncEntity object, SyncAction syncAction) throws SQLiteException {
		MemoAdapter.updateDbSync(db, (Memo)object, syncAction);
	}
	
	@Override
	public void buildInitialSync(SQLiteDatabase db, String syncClientId) {
		String sql = "SELECT MemoId FROM Memos";
		
		Cursor cursor = null;
		try {
			cursor = db.rawQuery(sql, null);
			while(cursor.moveToNext()) {
				SyncAction syncAction = new SyncAction();
				syncAction.setAction(SyncAction.ACTION_INSERT);
				syncAction.setTable(TableName);
				syncAction.setPrimaryKey(DatabaseHelper.getString(cursor, "MemoId"));
				syncAction.setSyncClientId(syncClientId);
				
				SyncActionAdapter.insert(db, syncAction);
			}
		} finally {
			if(cursor != null) {
				cursor.close();
			}
		}
	}
	
	private static void insertDbSync(SQLiteDatabase db, Memo memo, SyncAction syncAction) throws SQLiteException {
		db.beginTransaction();

		try {
			ContentValues values = createValues(memo);
			
			if(memo.getWordA() != null && memo.getWordB() != null) {
				WordAdapter.insert(db, memo.getWordA(), syncAction.getSyncClientId());
				WordAdapter.insert(db, memo.getWordB(), syncAction.getSyncClientId());
			} 
			
			db.insertOrThrow(TableName, null, values);
			SyncActionAdapter.insertAction(db, syncAction);
			
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}
	
	private static void updateDbSync(SQLiteDatabase db, Memo memo, SyncAction syncAction) throws SQLiteException {
		db.beginTransaction();
		
		try {
			ContentValues values = new ContentValues();
			String column = syncAction.getUpdateColumn();
			
			if(column.equals("LastReviewed")) {
				values.put("LastReviewed", DateHelper.toNormalizedString(memo.getLastReviewed()));
			} else if(column.equals("Displayed")) {
				values.put("Displayed", memo.getDisplayed());
			} else if(column.equals("CorrectAnsweredWordA")) {
				values.put("CorrectAnsweredWordA", memo.getCorrectAnsweredWordA());
			} else if(column.equals("CorrectAnsweredWordB")) {
				values.put("CorrectAnsweredWordB", memo.getCorrectAnsweredWordB());
			} else if(column.equals("Active")) {
				values.put("Active", memo.getActive());
			}
			
			if(values.size() > 0) {
				db.update(TableName, values, "MemoId = ?", new String[] { memo.getMemoId() });
				SyncActionAdapter.updateAction(db, syncAction);
			}
			
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}
	
	private static void deleteDbSync(SQLiteDatabase db, String memoId, SyncAction syncAction) throws SQLiteException {
		db.beginTransaction();
		
		try {

			Memo memo = MemoAdapter.get(db, memoId);
			
			if(memo != null) {
			
				db.delete(TableName, "MemoId = ?", new String[] { memoId });
				SyncActionAdapter.deleteAction(db, syncAction);
			
				WordAdapter.delete(db, memo.getWordAId(), null);
				SyncActionAdapter.removeAction(db, WordAdapter.TableName, memo.getWordAId());
				
				WordAdapter.delete(db, memo.getWordBId(), null);
				SyncActionAdapter.removeAction(db, WordAdapter.TableName, memo.getWordBId());
			}
			
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}
	
}
