package app.memoling.android.adapter;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import app.memoling.android.db.DatabaseHelper;
import app.memoling.android.db.SqliteAdapter;
import app.memoling.android.entity.SyncClient;

public class SyncClientAdapter extends SqliteAdapter {

	private final static String TableName = "SyncClients";
	
	private static String m_currentSyncClientId;
	
	public SyncClientAdapter(Context context) {
		super(context);
	}

	public static SyncClient bindSyncClient(Cursor cursor) {
		SyncClient syncClient = new SyncClient();
		
		syncClient.setDescription(DatabaseHelper.getString(cursor, "Description"));
		syncClient.setFacebookUserId(DatabaseHelper.getString(cursor, "FacebookUserId"));
		syncClient.setSyncClientId(DatabaseHelper.getString(cursor, "SyncClientId"));
		syncClient.setLastSyncServerTimestamp(DatabaseHelper.getLong(cursor, "LastSyncServerTimestamp"));
		
		return syncClient;
	}
	
	private static ContentValues createValues(SyncClient syncClient) {
		ContentValues values = new ContentValues();
		
		values.put("SyncClientId", syncClient.getSyncClientId());
		values.put("FacebookUserId", syncClient.getFacebookUserId());
		values.put("Description", syncClient.getDescription());
		values.put("LastSyncServerTimestamp", syncClient.getLastSyncServerTimestamp());
		
		return values;
	}
	
	public void insert(SyncClient syncClient) throws SQLiteException {
		try {
			SyncClientAdapter.insert(getDatabase(), syncClient);
		} finally {
			closeDatabase();
		}
	}
	
	public void changeId(String oldId, String newId) throws SQLiteException {
		try {
			SyncClientAdapter.changeId(getDatabase(), oldId, newId);
		} finally {
			closeDatabase();
		}
	}
	
	public void delete(String syncClientId) throws SQLiteException {
		try {
			SyncClientAdapter.delete(getDatabase(), syncClientId);
		} finally {
			closeDatabase();
		}
	}
	
	public static SyncClient get(SQLiteDatabase db, String syncClientId) {
		String sql = "SELECT * FROM SyncClients WHERE SyncClientId = ?";
		Cursor cursor = null;
		
		try {
			cursor = db.rawQuery(sql, new String[] { syncClientId });
			if(cursor.moveToNext()) {
				SyncClient syncClient = SyncClientAdapter.bindSyncClient(cursor);
				return syncClient;
			}
			
		} finally {
			if(cursor != null) {
				cursor.close();
			}
		}
		
		return null;
	}
	
	public static ArrayList<SyncClient> getAll(SQLiteDatabase db) {
		String sql = "SELECT * FROM SyncClients";
		ArrayList<SyncClient> syncClients = new ArrayList<SyncClient>();
		
		Cursor cursor = null;
		
		try {
			cursor = db.rawQuery(sql, null);
			while(cursor.moveToNext()) {
				SyncClient syncClient = SyncClientAdapter.bindSyncClient(cursor);
				syncClients.add(syncClient);
			}
			
			return syncClients;
		} finally {
			if(cursor != null) {
				cursor.close();
			}
		}
	}
	
	public void resetCurrentClient() {
		try {
			SyncClientAdapter.resetCurrentClient(getDatabase());
		} finally {
			closeDatabase();
		}
	}
	
	public static void resetCurrentClient(SQLiteDatabase db) {
		m_currentSyncClientId = null;
		SyncClientAdapter.getCurrentSyncClientId(db);
	}
	
	public String getCurrentSyncClientId() {
		if(m_currentSyncClientId != null) {
			return m_currentSyncClientId;
		}
		
		try {
			return SyncClientAdapter.getCurrentSyncClientId(getDatabase());
		} finally {
			closeDatabase();
		}
	}
	
	public static String getCurrentSyncClientId(SQLiteDatabase db) {
		if(m_currentSyncClientId != null) {
			return m_currentSyncClientId;
		}
		
		ArrayList<SyncClient> syncClients = SyncClientAdapter.getAll(db);
		// For now only one client
		if(syncClients.size() > 0) {
			m_currentSyncClientId = syncClients.get(0).getSyncClientId();
		}
		
		return m_currentSyncClientId;
	}
	
	public SyncClient getCurrentSyncClient() {
		try {
			return SyncClientAdapter.getCurrentSyncClient(getDatabase());
		} finally {
			closeDatabase();
		}
	}
	
	
	public static SyncClient getCurrentSyncClient(SQLiteDatabase db) {
		ArrayList<SyncClient> syncClients = SyncClientAdapter.getAll(db);
		// For now only one client
		if(syncClients.size() > 0) {
			return syncClients.get(0);
		}
		
		return null;
	}
	
	public static void insert(SQLiteDatabase db, SyncClient syncClient) {
		ContentValues values = SyncClientAdapter.createValues(syncClient);
		db.insertOrThrow(TableName, null, values);
	}

	public static void changeId(SQLiteDatabase db, String oldId, String newId) {
		db.execSQL("UPDATE " + TableName + " SET SyncClientId = '" + newId + "' WHERE SyncClientId = '" + oldId + "'");
	}
	
	public static void update(SQLiteDatabase db, SyncClient syncClient) {
		ContentValues values = SyncClientAdapter.createValues(syncClient);
		db.update(TableName, values, "SyncClientId = ?", new String[] { syncClient.getSyncClientId() });
	}
	
	public static void delete(SQLiteDatabase db, String syncClientId) throws SQLiteException {
		db.delete(TableName, "SyncClientId = ?", new String[] { syncClientId });
	}
	
	public static void updateLastSyncServerTimestamp(SQLiteDatabase db, String syncClientId) throws SQLiteException {
		ContentValues values = new ContentValues();
		values.put("LastSyncServerTimestamp", System.currentTimeMillis()/1000L);
		db.update(TableName, values, "SyncClientId = ?", new String[] { syncClientId });
	}
}
