package app.memoling.android.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import app.memoling.android.db.DatabaseHelper;
import app.memoling.android.db.SqliteAdapter;
import app.memoling.android.entity.SyncAction;
import app.memoling.android.helper.AppLog;
import app.memoling.android.sync.cloud.ISyncAdapter;
import app.memoling.android.sync.cloud.ISyncEntity;
import app.memoling.android.sync.cloud.SyncPackage;


public class SyncActionAdapter extends SqliteAdapter {

	public final static String TableName = "SyncActions";
	
	private Map<String,ISyncAdapter> m_adapterCache;
	
	public SyncActionAdapter(Context context) {
		super(context);
		
		m_adapterCache = new HashMap<String,ISyncAdapter>();
		
		m_adapterCache.put("Words", new WordAdapter(context));
		m_adapterCache.put("Memos", new MemoAdapter(context));
		m_adapterCache.put("MemoBases", new MemoBaseAdapter(context));
	}
	
	public boolean syncClient(List<SyncAction> syncServerActions) {

		SQLiteDatabase db = getDatabase();
		db.beginTransaction();
		
		try {
		
			// Remove old actions
			SyncActionAdapter.removeActions(db);
			
			// If new item is created and updated afterwards, only update is sent
			// however, multiple updates may be sent, but only one insert is allowed.
			List<String> updateInsertContext = new ArrayList<String>();
			
			// Sync all actions
			for(SyncAction syncAction : syncServerActions) {
				ISyncAdapter adapter = getAdapter(syncAction);
				
				switch(syncAction.getAction()) {
					case SyncAction.ACTION_DELETE:
						// One action per key
						adapter.deleteEntity(db, syncAction.getPrimaryKey(), syncAction);
						break;
					case SyncAction.ACTION_INSERT:
						// One action per key
						ISyncEntity insertObject = syncAction.getServerObject(adapter);
						adapter.insertEntity(db, insertObject, syncAction);
						break;
					case SyncAction.ACTION_UPDATE:
						// Multiple actions per key
						boolean exists = updateInsertContext.contains(syncAction.getTable() + syncAction.getPrimaryKey())
							|| adapter.getEntity(syncAction.getPrimaryKey()) != null;
						ISyncEntity updateObject = syncAction.getServerObject(adapter);
						
						if(!exists) {
							adapter.insertEntity(db, updateObject, syncAction);
							updateInsertContext.add(syncAction.getTable() + syncAction.getPrimaryKey());
						} else {
							adapter.updateEntity(db, updateObject, syncAction);	
						}
						
						break;
				}
				
			}

			// Remove new actions (from server)
			SyncActionAdapter.removeActions(db);
	
			// Update last sync timestamp
			SyncClientAdapter.updateLastSyncServerTimestamp(db, SyncClientAdapter.getCurrentSyncClientId(db));
			
			db.setTransactionSuccessful();
			
			return true;
		} catch(Exception ex) {
			AppLog.e("SyncActionAdapter", "syncClient - Sync Error", ex);
			return false;
		} finally {
			db.endTransaction();
		}

	}
	
	public SyncPackage syncServer(List<SyncAction> syncActions) {
		SyncPackage syncPackage = new SyncPackage();
		
		syncPackage.setSyncActions(syncActions);
		HashMap<String, JSONObject> clientObjects = new HashMap<String, JSONObject>();
		int index = 0;
		
		// Get all actions
		for(SyncAction syncAction : syncActions) {
			// Add objects if not deleted 
			if(syncAction.getAction() != SyncAction.ACTION_DELETE) {
				ISyncAdapter syncAdapter = getAdapter(syncAction);
				ISyncEntity clientObject = syncAdapter.getEntity(syncAction.getPrimaryKey());
				syncAction.setSyncObjectId(Integer.toString(index));
				try {
					clientObjects.put(Integer.toString(index), clientObject.encodeEntity());
				} catch(JSONException ex) {
					AppLog.e("SyncActionAdapter", "SyncServer: Parse Exception", ex);
				}
			}
			index++;
		}
		
		syncPackage.setClientObjects(clientObjects);
		
		return syncPackage;
	}
	
	public void buildInitialSync() throws SQLiteException {
		
		// Add every record as insert in order MemoBase,Word,Memo
		// Order is determines by foreign key constraints

		try {
			SQLiteDatabase db = getDatabase();
			String syncClientId = SyncClientAdapter.getCurrentSyncClientId(db);
			
			// Clear all actions in the database 
			// Old ones might be due to unsuccessful initial syncs
			SyncActionAdapter.removeActions(db);

			m_adapterCache.get("MemoBases").buildInitialSync(db, syncClientId);
			m_adapterCache.get("Words").buildInitialSync(db, syncClientId);
			m_adapterCache.get("Memos").buildInitialSync(db, syncClientId);
		} finally {
			closeDatabase();
		}
	}
	
	private ISyncAdapter getAdapter(SyncAction syncAction) {
		
		String table = syncAction.getTable();
		
		if(table.equals("Words")) {
			return m_adapterCache.get("Words");
		} else if(table.equals("Memos")) {
			return m_adapterCache.get("Memos");
		} else if(table.equals("MemoBases")) {
			return m_adapterCache.get("MemoBases");
		}
		
		return null;
	}
	
	public static void insertAction(SQLiteDatabase db, SyncAction syncAction) {
		if(syncAction.getSyncClientId() == null) {
			// Not tracked record
			return;
		}
		
		db.beginTransaction();
		try {
			List<SyncAction> existingRecords = SyncActionAdapter.getSimilar(db, syncAction.getSyncClientId(), syncAction.getTable(), syncAction.getPrimaryKey(), syncAction.getUpdateColumn());
			
			// Delete all existing records and add insert action
			for(SyncAction existingRecord : existingRecords) {
				SyncActionAdapter.delete(db, existingRecord.getSyncActionId());
			}
			
			ContentValues values = createValues(syncAction);
			db.insertOrThrow(TableName, null, values);
			
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}	
	}
	
	public static void deleteAction(SQLiteDatabase db, SyncAction syncAction) {
		if(syncAction.getSyncClientId() == null) {
			// Not tracked record
			return;
		}
	
		db.beginTransaction();
		try {
			List<SyncAction> existingRecords = SyncActionAdapter.getSimilar(db, syncAction.getSyncClientId(), syncAction.getTable(), syncAction.getPrimaryKey(), syncAction.getUpdateColumn());
			
			// Delete all existing records and add delete action
			for(SyncAction existingRecord : existingRecords) {
				SyncActionAdapter.delete(db, existingRecord.getSyncActionId());
			}
			
			ContentValues values = createValues(syncAction);
			db.insertOrThrow(TableName, null, values);
			
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}
	
	public static void updateAction(SQLiteDatabase db, SyncAction syncAction) {
		if(syncAction.getSyncClientId() == null) {
			// Not tracked record
			return;
		}
	
		db.beginTransaction();
		try {
			List<SyncAction> existingRecords = SyncActionAdapter.getSimilar(db, syncAction.getSyncClientId(), syncAction.getTable(), syncAction.getPrimaryKey(), syncAction.getUpdateColumn());
			
			// Delete all existing records and add update action
			for(SyncAction existingRecord : existingRecords) {
				SyncActionAdapter.delete(db, existingRecord.getSyncActionId());
			}
			
			ContentValues values = createValues(syncAction);
			db.insertOrThrow(TableName, null, values);
			
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}
	
	public static void removeAction(SQLiteDatabase db, String table, String primaryKey) {		
		db.beginTransaction();
		try {
			db.delete(TableName, "\"Table\" = ? AND PrimaryKey = ?", new String[] { table, primaryKey });
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}
	
	public List<SyncAction> get(String syncClientId, long serverTimestamp) {
		return SyncActionAdapter.get(getDatabase(), syncClientId, serverTimestamp);
	}
	
	public static List<SyncAction> get(SQLiteDatabase db, String syncClientId, long serverTimestamp) {
		Cursor cursor = null;
		ArrayList<SyncAction> syncActions = new ArrayList<SyncAction>();
		
		String sql = "SELECT * FROM SyncActions WHERE SyncClientId = ? AND ServerTimestamp >= ? ORDER BY ServerTimestamp";
		
		try {
			cursor = db.rawQuery(sql, new String[] { syncClientId, Long.toString(serverTimestamp) });
			
			while(cursor.moveToNext()) {
				SyncAction syncAction = SyncActionAdapter.bindSyncAction(cursor);
				syncActions.add(syncAction);
			}
		}
		finally {
			if(cursor != null) {
				cursor.close();
			}
		}
		
		return syncActions;
	}
	
	public List<SyncAction> getAll() {
		try {
			return SyncActionAdapter.getAll(getDatabase());
		} finally {
			closeDatabase();
		}
	}
	
	public static List<SyncAction> getAll(SQLiteDatabase db) {
		ArrayList<SyncAction> syncActions = new ArrayList<SyncAction>();
		Cursor cursor = null;
		
		// Get all actions in right order
		try {
			String sql = "SELECT * FROM SyncActions ORDER BY ServerTimestamp DESC";
			cursor = db.rawQuery(sql, null);
			while(cursor.moveToNext()) {
				SyncAction syncAction = bindSyncAction(cursor);
				syncActions.add(syncAction);
			}
			
			return syncActions;
		} finally {
			if(cursor != null) {
				cursor.close();
			}
		}
	}
	
	public void insert(SyncAction syncAction) {
		SyncActionAdapter.insert(getDatabase(), syncAction);
	}
	
	public static void insert(SQLiteDatabase db, SyncAction syncAction) {
		db.beginTransaction();
		
		try {
			ContentValues values = SyncActionAdapter.createValues(syncAction);
			db.insertOrThrow(TableName, null, values);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}
	
	public void delete(String syncActionId) {
		try {
			SyncActionAdapter.delete(getDatabase(), syncActionId);
		} finally {
			closeDatabase();
		}
		
	}

	private static void delete(SQLiteDatabase db, String syncActionId) {
		db.delete(TableName, "SyncActionId = ?", new String[] { syncActionId });
	}
	
	private static void removeActions(SQLiteDatabase db) {
		db.delete(TableName, null, null);
	}
	
	private static List<SyncAction> getSimilar(SQLiteDatabase db, String syncClientId, String table, String primaryKey, String updateColumn) {
		
		String sql = "SELECT * FROM SyncActions WHERE SyncClientId = ? AND \"Table\" = ? AND PrimaryKey = ?";
		int argsCount = 3;
		
		if(updateColumn != null) {
			sql += " AND (UpdateColumn = ? OR UpdateColumn IS NULL)";
			argsCount++;
		}

		String[] args = new String[argsCount];
		args[0] = syncClientId;
		args[1] = table;
		args[2] = primaryKey;
		if(argsCount > 3) {
			args[3] = updateColumn;
		}
		
		sql += " ORDER BY ServerTimestamp DESC";
		
		Cursor cursor = null;
		try {
			
			cursor = db.rawQuery(sql, args);
			ArrayList<SyncAction> syncActions = new ArrayList<SyncAction>();
			while(cursor.moveToNext()) {
				SyncAction syncAction = SyncActionAdapter.bindSyncAction(cursor);
				syncActions.add(syncAction);
			}
			
			return syncActions;
		} finally {
			if(cursor != null) {
				cursor.close();
			}
		}
	}
	
	private static SyncAction bindSyncAction(Cursor cursor) {
		SyncAction syncAction = new SyncAction();
		
		syncAction.setAction(DatabaseHelper.getInt(cursor, "Action"));
		syncAction.setPrimaryKey(DatabaseHelper.getString(cursor, "PrimaryKey"));
		syncAction.setServerTimestamp(DatabaseHelper.getLong(cursor, "ServerTimestamp"));
		syncAction.setSyncClientId(DatabaseHelper.getString(cursor, "SyncClientId"));
		syncAction.setTable(DatabaseHelper.getString(cursor, "Table"));
		syncAction.setUpdateColumn(DatabaseHelper.getString(cursor, "UpdateColumn"));
		syncAction.setSyncActionId(DatabaseHelper.getString(cursor, "SyncActionId"));
		
		return syncAction;
	}
	
	private static ContentValues createValues(SyncAction syncAction) {
		ContentValues values = new ContentValues();
		values.put("Action", syncAction.getAction());
		values.put("PrimaryKey", syncAction.getPrimaryKey());
		values.put("ServerTimestamp", syncAction.getServerTimestamp());
		values.put("SyncClientId", syncAction.getSyncClientId());
		values.put("\"Table\"", syncAction.getTable());
		values.put("UpdateColumn", syncAction.getUpdateColumn());
		values.put("SyncClientId", syncAction.getSyncClientId());
		values.put("SyncActionId", syncAction.getSyncActionId());
		return values;
	}
}
