package app.memoling.android.supermemo.adapter;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import app.memoling.android.adapter.SyncActionAdapter;
import app.memoling.android.db.DatabaseHelper;
import app.memoling.android.db.SqliteAdapter;
import app.memoling.android.entity.SyncAction;
import app.memoling.android.supermemo.entity.MemoReviewLog;
import app.memoling.android.sync.cloud.ISyncAdapter;
import app.memoling.android.sync.cloud.ISyncEntity;

public class MemoReviewLogAdapter extends SqliteAdapter implements ISyncAdapter {

	public final static String TableName = "MemoReviewLogs";
	
	public MemoReviewLogAdapter(Context context) {
		super(context);
	}
	
	public static MemoReviewLog bindMemoReviewLog(Cursor cursor) {
		return MemoReviewLogAdapter.bindMemoReviewLog(cursor, "");
	}
	
	public static MemoReviewLog bindMemoReviewLog(Cursor cursor, String prefix) {
		MemoReviewLog memoReviewLog = new MemoReviewLog();

		memoReviewLog.setMemoReviewLogId(DatabaseHelper.getString(cursor, prefix + "MemoReviewLogId"));
		memoReviewLog.setMemoId(DatabaseHelper.getString(cursor, prefix + "MemoId"));
		memoReviewLog.setResponseResult(DatabaseHelper.getInt(cursor, prefix + "ResponseResult"));
		memoReviewLog.setNewInterval(DatabaseHelper.getInt(cursor, prefix + "NewInterval"));
		memoReviewLog.setOldInterval(DatabaseHelper.getInt(cursor, prefix + "OldInterval"));
		memoReviewLog.setDifficultyFactor(DatabaseHelper.getInt(cursor, prefix + "DifficultyFactor"));
		memoReviewLog.setResponseTime(DatabaseHelper.getInt(cursor, prefix + "ResponseTime"));
		memoReviewLog.setType(DatabaseHelper.getInt(cursor, prefix + "Type"));
		
		return memoReviewLog;
	}

	private static ContentValues createValues(MemoReviewLog memoReviewLog) {
		ContentValues values = new ContentValues();

		values.put("MemoReviewLogId", memoReviewLog.getMemoReviewLogId());
		values.put("MemoId", memoReviewLog.getMemoId());
		values.put("ResponseResult", memoReviewLog.getResponseResult());
		values.put("NewInterval", memoReviewLog.getNewInterval());
		values.put("OldInterval", memoReviewLog.getOldInterval());
		values.put("DifficultyFactor", memoReviewLog.getDifficultyFactor());
		values.put("ResponseTime", memoReviewLog.getResponseTime());
		values.put("Type", memoReviewLog.getType());

		return values;
	}
	
	public MemoReviewLog get(String memoReviewLogId) {
		try {
			return MemoReviewLogAdapter.get(getDatabase(), memoReviewLogId);
		} finally {
			closeDatabase();
		}
	}

	public MemoReviewLog getDeep(String memoReviewLogId) {
		try {
			return MemoReviewLogAdapter.getDeep(getDatabase(), memoReviewLogId);
		} finally {
			closeDatabase();
		}
	}
	
	public void insert(MemoReviewLog memoReviewLog, String syncClientId) throws SQLiteException {
		try {
			MemoReviewLogAdapter.insert(getDatabase(), memoReviewLog, syncClientId);
		} finally {
			closeDatabase();
		}
	}
	
	public void update(MemoReviewLog memoReviewLog, String syncClientId) throws SQLiteException {
		try {
			MemoReviewLogAdapter.update(getDatabase(), memoReviewLog, syncClientId);
		} finally {
			closeDatabase();
		}
	}
	
	public void delete(String memoReviewLogId, String syncClientId) throws SQLiteException {
		try {
			MemoReviewLogAdapter.delete(getDatabase(), memoReviewLogId, syncClientId);
		} finally {
			closeDatabase();
		}
	}
	
	public static MemoReviewLog get(SQLiteDatabase db, String memoReviewLogId) {
		String sql = "SELECT * FROM MemoReviewLogs WHERE MemoReviewLogId = ?";
		Cursor cursor = null;
		
		try {
			cursor = db.rawQuery(sql, new String[] { memoReviewLogId });
			if(cursor.moveToNext()) {
				MemoReviewLog memoReviewLog = MemoReviewLogAdapter.bindMemoReviewLog(cursor);
				return memoReviewLog;
			}			
		} finally {
			if(cursor != null) {
				cursor.close();
			}
		}
		return null;
	}
	
	public static MemoReviewLog getDeep(SQLiteDatabase db, String memoReviewLogId) {	
		
		String query = "SELECT "
				+ "	M.MemoReviewLogId M_MemoReviewLogId, M.MemoId M_MemoId, M.ResponseResult M_ResponseResult, M.NewInterval M_NewInterval, M.OldInterval M_OldInterval, M.DifficultyFactor M_DifficultyFactor, M.ResponseTime M_ResponseTime, M.Type M_Type, "
				+ "FROM MemoReviewLogs AS M " + "WHERE M.MemoReviewLogId = ?";

		Cursor cursor = null;
		try {
			 cursor = db.rawQuery(query, new String[] { memoReviewLogId });
			if (cursor.moveToNext()) {
				MemoReviewLog memoReviewLog = MemoReviewLogAdapter.bindMemoReviewLog(cursor, "M_");

				return memoReviewLog;
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return null;
	}

	public static void insert(SQLiteDatabase db, MemoReviewLog memoReviewLog, String syncClientId) throws SQLiteException {
		SyncAction syncAction = new SyncAction();
		syncAction.setAction(SyncAction.ACTION_INSERT);
		syncAction.setPrimaryKey(memoReviewLog.getMemoReviewLogId());
		syncAction.setTable(TableName);
		syncAction.setSyncClientId(syncClientId);
		
		MemoReviewLogAdapter.insertDbSync(db, memoReviewLog, syncAction);
	}
	
	public static void update(SQLiteDatabase db, MemoReviewLog memoReviewLog, String syncClientId) throws SQLiteException {
		List<String> updateColumns = new ArrayList<String>();
		MemoReviewLog dbMemoReviewLog = MemoReviewLogAdapter.get(db, memoReviewLog.getMemoReviewLogId());

		if(dbMemoReviewLog.getResponseResult() != memoReviewLog.getResponseResult()) {
			updateColumns.add("ResponseResult");
		}
		
		if(dbMemoReviewLog.getNewInterval() != memoReviewLog.getNewInterval()) {
			updateColumns.add("NewInterval");
		}

		if(dbMemoReviewLog.getOldInterval() != memoReviewLog.getOldInterval()) {
			updateColumns.add("OldInterval");
		}
		
		if(dbMemoReviewLog.getDifficultyFactor() != memoReviewLog.getDifficultyFactor()) {
			updateColumns.add("DifficultyFactor");
		}
		
		if(dbMemoReviewLog.getResponseTime() != memoReviewLog.getResponseTime()) {
			updateColumns.add("ResponseTime");
		}
		
		if(dbMemoReviewLog.getType() != memoReviewLog.getType()) {
			updateColumns.add("Type");
		}
		
		for(String updateColumn : updateColumns) {
			SyncAction syncAction = new SyncAction();
			syncAction.setAction(SyncAction.ACTION_UPDATE);
			syncAction.setPrimaryKey(memoReviewLog.getMemoReviewLogId());
			syncAction.setTable(TableName);
			syncAction.setSyncClientId(syncClientId);
			syncAction.setUpdateColumn(updateColumn);
			
			MemoReviewLogAdapter.updateDbSync(db, memoReviewLog, syncAction);
		}
	}

	public static void delete(SQLiteDatabase db, String memoReviewLogId, String syncClientId) {
		SyncAction syncAction = new SyncAction();
		syncAction.setAction(SyncAction.ACTION_DELETE);
		syncAction.setPrimaryKey(memoReviewLogId);
		syncAction.setTable(TableName);
		syncAction.setSyncClientId(syncClientId);
		
		MemoReviewLogAdapter.deleteDbSync(db, memoReviewLogId, syncAction);
	}
	
	@Override
	public ISyncEntity decodeEntity(JSONObject json) throws JSONException {
		MemoReviewLog memoReviewLog = new MemoReviewLog();
		memoReviewLog.decodeEntity(json);
		return memoReviewLog;
	}

	@Override
	public ISyncEntity getEntity(String primaryKey) {
		return MemoReviewLogAdapter.get(this.getDatabase(),primaryKey);
	}

	@Override
	public void insertEntity(SQLiteDatabase db, ISyncEntity object, SyncAction action) throws SQLiteException {
		MemoReviewLogAdapter.insertDbSync(db, (MemoReviewLog)object, action);
	}

	@Override
	public void deleteEntity(SQLiteDatabase db, String primaryKey, SyncAction action) throws SQLiteException {
		MemoReviewLogAdapter.deleteDbSync(db, primaryKey, action);
	}

	@Override
	public void updateEntity(SQLiteDatabase db, ISyncEntity object, SyncAction action) throws SQLiteException {
		MemoReviewLogAdapter.updateDbSync(db, (MemoReviewLog)object, action);
	}

	@Override
	public void buildInitialSync(SQLiteDatabase db, String syncClientId) throws SQLiteException {
		String sql = "SELECT MemoReviewLogId FROM MemoReviewLogs";
		
		Cursor cursor = null;
		try {
			cursor = db.rawQuery(sql, null);
			while(cursor.moveToNext()) {
				SyncAction syncAction = new SyncAction();
				syncAction.setAction(SyncAction.ACTION_INSERT);
				syncAction.setTable(TableName);
				syncAction.setPrimaryKey(DatabaseHelper.getString(cursor, "MemoReviewLogId"));
				syncAction.setSyncClientId(syncClientId);
				
				SyncActionAdapter.insert(db, syncAction);
			}
		} finally {
			if(cursor != null) {
				cursor.close();
			}
		}
	}
	
	private static void insertDbSync(SQLiteDatabase db, MemoReviewLog memoReviewLog, SyncAction action) {
		db.beginTransaction();

		try {
			ContentValues values = createValues(memoReviewLog);
			db.insertOrThrow(TableName, null, values);
			SyncActionAdapter.insertAction(db, action);
			
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}	
	
	private static void updateDbSync(SQLiteDatabase db, MemoReviewLog memoReviewLog, SyncAction action) {
		db.beginTransaction();
		
		try {
			ContentValues values = new ContentValues();
			String column = action.getUpdateColumn();
			
			if(column.equals("ResponseResult")) {
				values.put("ResponseResult", memoReviewLog.getResponseResult());
			} else if(column.equals("NewInterval")) {
				values.put("NewInterval", memoReviewLog.getNewInterval());
			} else if(column.equals("OldInterval")) {
				values.put("OldInterval", memoReviewLog.getOldInterval());
			} else if(column.equals("DifficultyFactor")) {
				values.put("DifficultyFactor", memoReviewLog.getDifficultyFactor());
			} else if(column.equals("ResponseTime")) {
				values.put("ResponseTime", memoReviewLog.getResponseTime());
			} else if(column.equals("Type")) {
				values.put("Type", memoReviewLog.getType());
			}
			
			if(values.size() > 0) {
				db.update(TableName, values, "MemoReviewLogId = ?", new String[] { memoReviewLog.getMemoReviewLogId() });
				SyncActionAdapter.updateAction(db, action);
			}			
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}
	
	private static void deleteDbSync(SQLiteDatabase db, String memoReviewLogId, SyncAction action) {
		db.beginTransaction();		
		
		try {
			MemoReviewLog memoReviewLog = MemoReviewLogAdapter.get(db, memoReviewLogId);
			if(memoReviewLog != null) {
				db.delete(TableName, "MemoReviewLogId = ?", new String[] { memoReviewLogId });
				SyncActionAdapter.deleteAction(db, action);
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}	
}