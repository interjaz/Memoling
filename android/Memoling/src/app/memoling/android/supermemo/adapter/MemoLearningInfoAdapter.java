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
import app.memoling.android.supermemo.entity.MemoLearningInfo;
import app.memoling.android.sync.cloud.ISyncAdapter;
import app.memoling.android.sync.cloud.ISyncEntity;

public class MemoLearningInfoAdapter extends SqliteAdapter implements
		ISyncAdapter {

	public final static String TableName = "MemoLearningInfos";
	
	public MemoLearningInfoAdapter(Context context) {
		super(context);
	}

	public static MemoLearningInfo bindMemoLearningInfo(Cursor cursor) {
		return MemoLearningInfoAdapter.bindMemoLearningInfo(cursor, "");
	}

	public static MemoLearningInfo bindMemoLearningInfo(Cursor cursor, String prefix) {
		MemoLearningInfo memoLearningInfo = new MemoLearningInfo();

		memoLearningInfo.setMemoLearningInfoId(DatabaseHelper.getString(cursor, prefix + "MemoLearningInfoId"));
		memoLearningInfo.setMemoId(DatabaseHelper.getString(cursor, prefix + "MemoId"));
		memoLearningInfo.setOrder(DatabaseHelper.getInt(cursor, prefix + "Order"));
		memoLearningInfo.setType(DatabaseHelper.getInt(cursor, prefix + "Type"));
		memoLearningInfo.setQueue(DatabaseHelper.getInt(cursor, prefix + "Queue"));
		memoLearningInfo.setDue(DatabaseHelper.getInt(cursor, prefix + "Due"));
		memoLearningInfo.setInterval(DatabaseHelper.getInt(cursor, prefix + "Interval"));
		memoLearningInfo.setDifficulty(DatabaseHelper.getInt(cursor, prefix + "Difficulty"));
		memoLearningInfo.setNumberAllAnswers(DatabaseHelper.getInt(cursor, prefix + "NumberAllAnswers"));
		memoLearningInfo.setNumberWrongAnswers(DatabaseHelper.getInt(cursor, prefix + "NumberWrongAnswers"));
		memoLearningInfo.setLeft(DatabaseHelper.getInt(cursor, prefix + "Left"));
		memoLearningInfo.setOdue(DatabaseHelper.getInt(cursor, prefix + "Odue"));
		memoLearningInfo.setOdid(DatabaseHelper.getInt(cursor, prefix + "Odid"));
		memoLearningInfo.setFlags(DatabaseHelper.getInt(cursor, prefix + "Flags"));
		memoLearningInfo.setData(DatabaseHelper.getString(cursor, prefix + "Data"));
		
		return memoLearningInfo;
	}
	
	private static ContentValues createValues(MemoLearningInfo memoLearningInfo) {
		ContentValues values = new ContentValues();

		values.put("MemoLearningInfoId", memoLearningInfo.getMemoLearningInfoId());
		values.put("MemoId", memoLearningInfo.getMemoId());
		values.put("Order", memoLearningInfo.getOrder());
		values.put("Type", memoLearningInfo.getType());
		values.put("Queue", memoLearningInfo.getQueue());
		values.put("Due", memoLearningInfo.getDue());
		values.put("Interval", memoLearningInfo.getInterval());
		values.put("Difficulty", memoLearningInfo.getDifficulty());
		values.put("NumberAllAnswers", memoLearningInfo.getNumberAllAnswers());
		values.put("NumberWrongAnswers", memoLearningInfo.getNumberWrongAnswers());
		values.put("Left", memoLearningInfo.getLeft());
		values.put("Odue", memoLearningInfo.getOdue());
		values.put("Odid", memoLearningInfo.getOdid());
		values.put("Flags", memoLearningInfo.getFlags());
		values.put("Data", memoLearningInfo.getData());
		
		return values;
	}
	
	public MemoLearningInfo get(String memoLearningInfoId) {
		try {
			return MemoLearningInfoAdapter.get(getDatabase(), memoLearningInfoId);
		} finally {
			closeDatabase();
		}
	}
	
	public MemoLearningInfo getDeep(String memoLearningInfoId) {
		try {
			return MemoLearningInfoAdapter.getDeep(getDatabase(), memoLearningInfoId);
		} finally {
			closeDatabase();
		}
	}
	
	public void insert(MemoLearningInfo memoLearningInfo, String syncClientId) throws SQLiteException {
		try {
			MemoLearningInfoAdapter.insert(getDatabase(), memoLearningInfo, syncClientId);
		} finally {
			closeDatabase();
		}
	}
	
	public void update(MemoLearningInfo memoLearningInfo, String syncClientId) throws SQLiteException {
		try {
			MemoLearningInfoAdapter.update(getDatabase(), memoLearningInfo, syncClientId);
		} finally {
			closeDatabase();
		}
	}
	
	public void delete(String memoLearningInfoId, String syncClientId) throws SQLiteException {
		try {
			MemoLearningInfoAdapter.delete(getDatabase(), memoLearningInfoId, syncClientId);
		} finally {
			closeDatabase();
		}
	}
	
	public static MemoLearningInfo get(SQLiteDatabase db, String memoLearningInfoId) {
		String sql = "SELECT * FROM MemoLearningInfos WHERE MemoLearningInfoId = ?";
		Cursor cursor = null;
		
		try {
			cursor = db.rawQuery(sql, new String[] { memoLearningInfoId });
			if(cursor.moveToNext()) {
				MemoLearningInfo memoLearningInfo = MemoLearningInfoAdapter.bindMemoLearningInfo(cursor);
				return memoLearningInfo;
			}			
		} finally {
			if(cursor != null) {
				cursor.close();
			}
		}
		return null;
	}
	
	public static MemoLearningInfo getDeep(SQLiteDatabase db, String memoLearningInfoId) {	
		
		String query = "SELECT "
				+ "M.MemoLearningInfoId M_MemoLearningInfoId, M.MemoId M_MemoId, M.Order M_Order, M.Type M_Type, " 
				+ "M.Queue M_Queue, M.Due M_Due, M.Interval M_Interval, M.Difficulty M_Difficulty, "
				+ "M.NumberAllAnswers M_NumberAllAnswers, M.NumberWrongAnswers M_NumberWrongAnswers, " 
				+ "M.Left M_Left, M.Odue M_Odue, M.Odid M_Odid, M.Flags M_Flags, M.Data M_Data "
				+ "FROM MemoLearningInfos AS M " + "WHERE M.MemoLearningInfoId = ?";

		Cursor cursor = null;
		try {
			 cursor = db.rawQuery(query, new String[] { memoLearningInfoId });
			if (cursor.moveToNext()) {
				MemoLearningInfo memoLearningInfo = MemoLearningInfoAdapter.bindMemoLearningInfo(cursor, "M_");

				return memoLearningInfo;
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return null;
	}
	
	public static void insert(SQLiteDatabase db, MemoLearningInfo memoLearningInfo, String syncClientId) throws SQLiteException {
		SyncAction syncAction = new SyncAction();
		syncAction.setAction(SyncAction.ACTION_INSERT);
		syncAction.setPrimaryKey(memoLearningInfo.getMemoLearningInfoId());
		syncAction.setTable(TableName);
		syncAction.setSyncClientId(syncClientId);
		
		MemoLearningInfoAdapter.insertDbSync(db, memoLearningInfo, syncAction);
	}
	
	public static void update(SQLiteDatabase db, MemoLearningInfo memoLearningInfo, String syncClientId) throws SQLiteException {
		List<String> updateColumns = new ArrayList<String>();
		MemoLearningInfo dbMemoLearningInfo = MemoLearningInfoAdapter.get(db, memoLearningInfo.getMemoLearningInfoId());
		
		if(dbMemoLearningInfo.getOrder() != memoLearningInfo.getOrder()) {
			updateColumns.add("Order");
		}
		
		if(dbMemoLearningInfo.getType() != memoLearningInfo.getType()) {
			updateColumns.add("Type");
		}
		
		if(dbMemoLearningInfo.getQueue() != memoLearningInfo.getQueue()) {
			updateColumns.add("Queue");
		}
		
		if(dbMemoLearningInfo.getDue() != memoLearningInfo.getDue()) {
			updateColumns.add("Due");
		}
		
		if(dbMemoLearningInfo.getInterval() != memoLearningInfo.getInterval()) {
			updateColumns.add("Interval");
		}
		
		if(dbMemoLearningInfo.getDifficulty() != memoLearningInfo.getDifficulty()) {
			updateColumns.add("Difficulty");
		}
		
		if(dbMemoLearningInfo.getNumberAllAnswers() != memoLearningInfo.getNumberAllAnswers()) {
			updateColumns.add("NumberAllAnswers");
		}
		
		if(dbMemoLearningInfo.getNumberWrongAnswers() != memoLearningInfo.getNumberWrongAnswers()) {
			updateColumns.add("NumberWrongAnswers");
		}
		
		if(dbMemoLearningInfo.getLeft() != memoLearningInfo.getLeft()) {
			updateColumns.add("Left");
		}
		
		if(dbMemoLearningInfo.getOdue() != memoLearningInfo.getOdue()) {
			updateColumns.add("Odue");
		}
		
		if(dbMemoLearningInfo.getOdid() != memoLearningInfo.getOdid()) {
			updateColumns.add("Odid");
		}
		
		if(dbMemoLearningInfo.getFlags() != memoLearningInfo.getFlags()) {
			updateColumns.add("Flags");
		}
		
		if(dbMemoLearningInfo.getData() != memoLearningInfo.getData()) {
			updateColumns.add("Data");
		}
		
		for(String updateColumn : updateColumns) {
			SyncAction syncAction = new SyncAction();
			syncAction.setAction(SyncAction.ACTION_UPDATE);
			syncAction.setPrimaryKey(memoLearningInfo.getMemoLearningInfoId());
			syncAction.setTable(TableName);
			syncAction.setSyncClientId(syncClientId);
			syncAction.setUpdateColumn(updateColumn);
			
			MemoLearningInfoAdapter.updateDbSync(db, memoLearningInfo, syncAction);
		}
	}
	
	public static void delete(SQLiteDatabase db, String memoMemoLearningInfoId, String syncClientId) {
		SyncAction syncAction = new SyncAction();
		syncAction.setAction(SyncAction.ACTION_DELETE);
		syncAction.setPrimaryKey(memoMemoLearningInfoId);
		syncAction.setTable(TableName);
		syncAction.setSyncClientId(syncClientId);
		
		MemoLearningInfoAdapter.deleteDbSync(db, memoMemoLearningInfoId, syncAction);
	}
	
	
	
	@Override
	public ISyncEntity decodeEntity(JSONObject json) throws JSONException {
		MemoLearningInfo memoLearningInfo = new MemoLearningInfo();
		memoLearningInfo.decodeEntity(json);
		return memoLearningInfo;
	}

	@Override
	public ISyncEntity getEntity(String primaryKey) {
		return MemoLearningInfoAdapter.get(this.getDatabase(),primaryKey);
	}

	@Override
	public void insertEntity(SQLiteDatabase db, ISyncEntity object, SyncAction action) throws SQLiteException {
		MemoLearningInfoAdapter.insertDbSync(db, (MemoLearningInfo)object, action);
	}

	@Override
	public void deleteEntity(SQLiteDatabase db, String primaryKey, SyncAction action) throws SQLiteException {
		MemoLearningInfoAdapter.deleteDbSync(db, primaryKey, action);
	}

	@Override
	public void updateEntity(SQLiteDatabase db, ISyncEntity object, SyncAction action) throws SQLiteException {
		MemoLearningInfoAdapter.updateDbSync(db, (MemoLearningInfo)object, action);
	}

	@Override
	public void buildInitialSync(SQLiteDatabase db, String syncClientId)
			throws SQLiteException {
		String sql = "SELECT MemoLearningInfoId FROM MemoLearningInfos";
		
		Cursor cursor = null;
		try {
			cursor = db.rawQuery(sql, null);
			while(cursor.moveToNext()) {
				SyncAction syncAction = new SyncAction();
				syncAction.setAction(SyncAction.ACTION_INSERT);
				syncAction.setTable(TableName);
				syncAction.setPrimaryKey(DatabaseHelper.getString(cursor, "MemoLearningInfoId"));
				syncAction.setSyncClientId(syncClientId);
				
				SyncActionAdapter.insert(db, syncAction);
			}
		} finally {
			if(cursor != null) {
				cursor.close();
			}
		}
	}

	private static void insertDbSync(SQLiteDatabase db, MemoLearningInfo memoLearningInfo, SyncAction action) {
		db.beginTransaction();

		try {
			ContentValues values = createValues(memoLearningInfo);
			db.insertOrThrow(TableName, null, values);
			SyncActionAdapter.insertAction(db, action);
			
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}	
	
	private static void updateDbSync(SQLiteDatabase db, MemoLearningInfo memoLearningInfo, SyncAction action) {
		db.beginTransaction();
		
		try {
			ContentValues values = new ContentValues();
			String column = action.getUpdateColumn();
			
			if(column.equals("Order")) {
				values.put("Order", memoLearningInfo.getOrder());
			} else if(column.equals("Type")) {
				values.put("Type", memoLearningInfo.getType());
			} else if(column.equals("Queue")) {
				values.put("Queue", memoLearningInfo.getQueue());
			} else if(column.equals("Due")) {
				values.put("Due", memoLearningInfo.getDue());
			} else if(column.equals("Interval")) {
				values.put("Interval", memoLearningInfo.getInterval());
			} else if(column.equals("Difficulty")) {
				values.put("Difficulty", memoLearningInfo.getDifficulty());
			} else if(column.equals("NumberAllAnswers")) {
				values.put("NumberAllAnswers", memoLearningInfo.getNumberAllAnswers());
			} else if(column.equals("NumberWrongAnswers")) {
				values.put("NumberWrongAnswers", memoLearningInfo.getNumberWrongAnswers());
			} else if(column.equals("Left")) {
				values.put("Left", memoLearningInfo.getLeft());
			} else if(column.equals("Odue")) {
				values.put("Odue", memoLearningInfo.getOdue());
			} else if(column.equals("Odid")) {
				values.put("Odid", memoLearningInfo.getOdid());
			} else if(column.equals("Flags")) {
				values.put("Flags", memoLearningInfo.getFlags());
			} else if(column.equals("Data")) {
				values.put("Data", memoLearningInfo.getData());
			}
			
			if(values.size() > 0) {
				db.update(TableName, values, "MemoLearningInfoId = ?", new String[] { memoLearningInfo.getMemoLearningInfoId() });
				SyncActionAdapter.updateAction(db, action);
			}			
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}
	
	private static void deleteDbSync(SQLiteDatabase db, String memoLearningInfoId, SyncAction action) {
		db.beginTransaction();		
		
		try {
			MemoLearningInfo memoLearningInfo = MemoLearningInfoAdapter.get(db, memoLearningInfoId);
			if(memoLearningInfo != null) {
				db.delete(TableName, "MemoLearningInfoId = ?", new String[] { memoLearningInfoId });
				SyncActionAdapter.deleteAction(db, action);
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}	
}