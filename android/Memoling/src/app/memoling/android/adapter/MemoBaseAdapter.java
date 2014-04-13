package app.memoling.android.adapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.UUID;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import app.memoling.android.db.DatabaseHelper;
import app.memoling.android.db.SqliteAdapter;
import app.memoling.android.entity.Language;
import app.memoling.android.entity.Memo;
import app.memoling.android.entity.MemoBase;
import app.memoling.android.entity.MemoBaseInfo;
import app.memoling.android.entity.SyncAction;
import app.memoling.android.helper.DateHelper;
import app.memoling.android.sync.cloud.ISyncAdapter;
import app.memoling.android.sync.cloud.ISyncEntity;

public class MemoBaseAdapter extends SqliteAdapter implements ISyncAdapter {

	public final static String TableName = "MemoBases";


	public MemoBaseAdapter(Context context) {
		super(context);
	}

	public MemoBaseAdapter(Context context, boolean persistant) {
		super(context, persistant);
	}
	
	public static MemoBase bindMemoBase(Cursor cursor) {
		return MemoBaseAdapter.bindMemoBase(cursor, "");
	}
	
	public static MemoBase bindMemoBase(Cursor cursor, String prefix) {
		MemoBase memoBase = new MemoBase();
		memoBase.setActive(DatabaseHelper.getBoolean(cursor, prefix + "Active"));
		memoBase.setCreated(DatabaseHelper.getDate(cursor, prefix + "Created"));
		memoBase.setMemoBaseId(DatabaseHelper.getString(cursor, prefix + "MemoBaseId"));
		memoBase.setName(DatabaseHelper.getString(cursor, prefix + "Name"));
		
		return memoBase;
	}

	private static ContentValues createValues(MemoBase base) {
		ContentValues values = new ContentValues();
		values.put("MemoBaseId", base.getMemoBaseId());
		values.put("Name", base.getName());
		values.put("Created", DateHelper.toNormalizedString(base.getCreated()));
		values.put("Active", base.getActive());
		return values;
	}

	public MemoBase get(String memoBaseId) {
		try {
			return get(getDatabase(), memoBaseId);
		} finally {
			closeDatabase();
		}
	}

	public ArrayList<MemoBase> getAll() {
		try {
			return MemoBaseAdapter.getAll(getDatabase());
		} finally {
			closeDatabase();
		}
	}

	public void insert(MemoBase memoBase, String syncClientId) throws SQLiteException {
		try {
			MemoBaseAdapter.insert(getDatabase(), memoBase, syncClientId);
		} finally {
			closeDatabase();
		}
	}

	public void update(MemoBase memoBase, String syncClientId) throws SQLiteException {
		try {
			MemoBaseAdapter.update(getDatabase(), memoBase, syncClientId);
		} finally {
			closeDatabase();
		}
	}

	public void delete(String memoBaseId, String syncClientId) throws SQLiteException {
		try {
			MemoBaseAdapter.delete(getDatabase(), memoBaseId, syncClientId);
		} finally {
			closeDatabase();
		}
	}
	
	public static ArrayList<MemoBase> getAll(SQLiteDatabase db) {
		Cursor cursor = null;

		try {
			List<MemoBase> memoBases = new ArrayList<MemoBase>();

			String query = "SELECT "
					+ "	B.MemoBaseId B_MemoBaseId, B.Name B_Name, B.Created B_Created, B.Active B_Active "
					+ "FROM MemoBases  AS B";

			cursor = db.rawQuery(query, null);

			while (cursor.moveToNext()) {
				MemoBase memoBase = MemoBaseAdapter.bindMemoBase(cursor, "B_");
				memoBases.add(memoBase);
			}

			return memoBases;

		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
	}

	public MemoBaseInfo getMemoBaseInfo(String memoBaseId) {
		SQLiteDatabase db = null;
		Cursor cursor = null;

		try {
			MemoBaseInfo memoBaseInfo = null;
			MemoBase memoBase = null;

			db = getDatabase();

			String query = "SELECT " 
					+ "B.MemoBaseId B_MemoBaseId, B.Name B_Name, B.Created B_Created, "
					+ "B.Active B_Active, COUNT(M.MemoBaseId) NoAll, SUM(M.Active) NoActive, "
					+ "MAX(M.LastReviewed) AS LastReviewed " 
					+ "FROM MemoBases  AS B "
					+ "OUTER LEFT JOIN Memos AS M ON M.MemoBaseId = B.MemoBaseId " 
					+ "WHERE B.MemoBaseId = ?";

			cursor = db.rawQuery(query, new String[] { memoBaseId });

			if (cursor.moveToFirst()) {
				memoBaseInfo = new MemoBaseInfo();

				memoBase = MemoBaseAdapter.bindMemoBase(cursor, "B_");
				
				memoBaseInfo.setMemoBase(memoBase);
				memoBaseInfo.setLastReviewed(DatabaseHelper.optDate(cursor, "LastReviewed", new Date()));
				memoBaseInfo.setNoActiveMemos(DatabaseHelper.optInt(cursor, "NoActive", 0));
				memoBaseInfo.setNoAllMemos(DatabaseHelper.optInt(cursor, "NoAll", 0));
			
				query = "SELECT LanguageIso639 FROM( " 
						+"SELECT DISTINCT coalesce(MB.MemoBaseId, MA.MemoBaseId) AS MemoBaseID, W.LanguageIso639 "
						+"FROM Words AS W "
						+"OUTER LEFT JOIN Memos AS MA ON MA.WordAId = W.WordId " 
						+"OUTER LEFT JOIN Memos AS MB ON MB.WordBId = W.WordId "
						+") "
						+"WHERE MemoBaseId = ?";
				
				cursor.close();
				cursor = db.rawQuery(query, new String[] { memoBaseId });
	
				HashSet<Language> languages = new HashSet<Language>();
				while (cursor.moveToNext()) {
					languages.add(Language.parse(DatabaseHelper.getString(cursor, "LanguageIso639")));
				} 
				Language[] arrayLanguages = new Language[languages.size()];
				arrayLanguages = languages.toArray(arrayLanguages);
				memoBaseInfo.setLanguages(arrayLanguages);
			}

			return memoBaseInfo;
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}

			closeDatabase();
		}
	}
	
	public static MemoBase get(SQLiteDatabase db, String memoBaseId) {
		
		String sql = "SELECT * FROM MemoBases WHERE MemoBaseId = ?";
		Cursor cursor = db.rawQuery(sql, new String[] { memoBaseId });
		
		try {
			
			if(cursor.moveToNext()) {
				MemoBase memoBase = MemoBaseAdapter.bindMemoBase(cursor);
				return memoBase;
			}
			
		} finally {
			cursor.close();
		}
		
		return null;
	}
	
	public static void insert(SQLiteDatabase db, MemoBase memoBase, String syncClientId) throws SQLiteException {
		SyncAction syncAction = new SyncAction();
		syncAction.setAction(SyncAction.ACTION_INSERT);
		syncAction.setPrimaryKey(memoBase.getMemoBaseId());
		syncAction.setTable(TableName);
		syncAction.setSyncClientId(syncClientId);
		
		MemoBaseAdapter.insertDbSync(db, memoBase, syncAction);
	}
	
	public static void update(SQLiteDatabase db, MemoBase memoBase, String syncClientId) throws SQLiteException {
		
		ArrayList<String> updateColumns = new ArrayList<String>();
		MemoBase dbMemoBase = MemoBaseAdapter.get(db, memoBase.getMemoBaseId());
		

		if(!dbMemoBase.getName().equals(memoBase.getName())) {
			updateColumns.add("Name");
		}
		
		if(dbMemoBase.getActive() == memoBase.getActive()) {
			updateColumns.add("Active");
		}
		
		for(String updateColumn : updateColumns) {
			SyncAction syncAction = new SyncAction();
			syncAction.setAction(SyncAction.ACTION_UPDATE);
			syncAction.setPrimaryKey(memoBase.getMemoBaseId());
			syncAction.setTable(TableName);
			syncAction.setSyncClientId(syncClientId);
			syncAction.setUpdateColumn(updateColumn);
			
			MemoBaseAdapter.updateDbSync(db, memoBase, syncAction);
		}
	}
	
	public static void delete(SQLiteDatabase db, String memoBaseId, String syncClientId) throws SQLiteException {
		SyncAction syncAction = new SyncAction();
		syncAction.setAction(SyncAction.ACTION_DELETE);
		syncAction.setPrimaryKey(memoBaseId);
		syncAction.setTable(TableName);
		syncAction.setSyncClientId(syncClientId);
		
		MemoBaseAdapter.deleteDbSync(db, memoBaseId, syncAction);
	}
	
	@Override
	public ISyncEntity decodeEntity(JSONObject json) throws JSONException {
		MemoBase memoBase = new MemoBase();
		memoBase.decodeEntity(json);		
		return memoBase;
	}

	@Override
	public ISyncEntity getEntity(String primaryKey) {
		return MemoBaseAdapter.get(this.getDatabase(), primaryKey);
	}

	@Override
	public void insertEntity(SQLiteDatabase db, ISyncEntity object, SyncAction syncAction) throws SQLiteException {
		MemoBaseAdapter.insertDbSync(db, (MemoBase)object, syncAction);
	}

	@Override
	public void deleteEntity(SQLiteDatabase db, String primaryKey, SyncAction syncAction) throws SQLiteException {
		MemoBaseAdapter.deleteDbSync(db, primaryKey, syncAction);
	}

	@Override
	public void updateEntity(SQLiteDatabase db, ISyncEntity object, SyncAction syncAction) throws SQLiteException {
		MemoBaseAdapter.updateDbSync(db, (MemoBase)object, syncAction);
	}
	@Override
	public void buildInitialSync(SQLiteDatabase db, String syncClientId) {
		replaceAllIdsDeep(db);
		
		String sql = "SELECT MemoBaseId FROM MemoBases";
		
		Cursor cursor = null;
		try {
			cursor = db.rawQuery(sql, null);
			while(cursor.moveToNext()) {
				SyncAction syncAction = new SyncAction();
				syncAction.setAction(SyncAction.ACTION_INSERT);
				syncAction.setTable(TableName);
				syncAction.setPrimaryKey(DatabaseHelper.getString(cursor, "MemoBaseId"));
				syncAction.setSyncClientId(syncClientId);
				
				SyncActionAdapter.insert(db, syncAction);
			}
		} finally {
			if(cursor != null) {
				cursor.close();
			}
		}
	}
	
	private static void insertDbSync(SQLiteDatabase db, MemoBase memoBase, SyncAction syncAction) throws SQLiteException {
		db.beginTransaction();

		try {
			ContentValues values = createValues(memoBase);
			
			if(memoBase.getMemos() != null) {
				for(Memo memo : memoBase.getMemos()) {
					MemoAdapter.insert(db, memo, syncAction.getSyncClientId());
				}
			}
			
			db.insertOrThrow(TableName, null, values);
			SyncActionAdapter.insertAction(db, syncAction);
			
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}
	
	private static void updateDbSync(SQLiteDatabase db, MemoBase memoBase, SyncAction syncAction) throws SQLiteException {
		db.beginTransaction();
		
		try {
			ContentValues values = new ContentValues();
			String column = syncAction.getUpdateColumn();
			
			if(column.equals("Name")) {
				values.put("Name", memoBase.getName());
			} else if(column.equals("Active")) {
				values.put("Active", memoBase.getActive());
			}

			if(values.size() > 0) {
				db.update(TableName, values, "MemoBaseId = ?", new String[] { memoBase.getMemoBaseId() });
				SyncActionAdapter.updateAction(db, syncAction);
			}
			
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}
	
	private static void deleteDbSync(SQLiteDatabase db, String memoBaseId, SyncAction syncAction) throws SQLiteException {
		db.beginTransaction();
		
		try {

			ArrayList<Memo> memos = MemoAdapter.getAll(db, memoBaseId);
			
			for(Memo memo : memos) {
				MemoAdapter.delete(db, memo.getMemoId(), null);
				SyncActionAdapter.removeAction(db, MemoAdapter.TableName, memo.getMemoId());
			}
			
			
			db.delete(TableName, "MemoBaseId = ?", new String[] { memoBaseId });
			SyncActionAdapter.deleteAction(db, syncAction);
			
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}
	
	private static void replaceAllIdsDeep(SQLiteDatabase db) throws SQLiteException {
		// This operation is part of initial sync. 
		// Do not record any sync actions
		
		db.beginTransaction();
		
		try {
			ArrayList<MemoBase> memoBases = MemoBaseAdapter.getAll(db);
			for(MemoBase memoBase : memoBases) {
				String oldMemoBaseId = memoBase.getMemoBaseId();
				String newMemoBaseId = UUID.randomUUID().toString();
				
				ArrayList<Memo> memos = MemoAdapter.getAll(db, oldMemoBaseId);
				for(Memo memo : memos) {
					String oldMemoId = memo.getMemoId();
					String newMemoId = UUID.randomUUID().toString();
	
					String oldWordAId = memo.getWordAId();
					String newWordAId = UUID.randomUUID().toString();
					
					String oldWordBId = memo.getWordBId();
					String newWordBId = UUID.randomUUID().toString();

					// Update words
					ContentValues vWordA = new ContentValues();
					vWordA.put("WordId", newWordAId);
					db.update(WordAdapter.TableName, vWordA, "WordId = ?", new String[] { oldWordAId });
					
					ContentValues vWordB = new ContentValues();
					vWordB.put("WordId", newWordBId);
					db.update(WordAdapter.TableName, vWordB, "WordId = ?", new String[] { oldWordBId });
					
					// Update Memo
					ContentValues vMemo = new ContentValues();
					vMemo.put("WordAId", newWordAId);
					vMemo.put("WordBId", newWordBId);
					vMemo.put("MemoId", newMemoId);
					vMemo.put("MemoBaseId", newMemoBaseId);
					
					db.update(MemoAdapter.TableName, vMemo, "MemoId = ?", new String[] { oldMemoId });
				}
				
				// Update MemoBase
				ContentValues vMemoBase = new ContentValues();
				vMemoBase.put("MemoBaseId", newMemoBaseId);
				db.update(MemoBaseAdapter.TableName, vMemoBase, "MemoBaseId = ?", new String[] { oldMemoBaseId });
			}
		
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}
}
