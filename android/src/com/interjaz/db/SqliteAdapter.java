package com.interjaz.db;

import java.io.IOException;
import java.util.Hashtable;
import java.util.concurrent.atomic.AtomicLong;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public abstract class SqliteAdapter {

	protected static final String Tag = "SqliteAdapter";

	private static Hashtable<String, TableHandle> m_tableHandle = new Hashtable<String, TableHandle>();

	private static AtomicLong m_globalSyncId = new AtomicLong();

	private static long m_localSyncId = 0;

	private String m_databaseName;

	public SqliteAdapter(Context context, String databaseName, int version) throws IOException {
		m_databaseName = databaseName;
		
		synchronized (m_tableHandle) {			
			if (!m_tableHandle.containsKey(databaseName)) {
				TableHandle handle = new TableHandle();
				handle.ActiveConnections = 0;
				handle.SqliteHelper = new SqliteHelper(context, databaseName, version);
				
				m_tableHandle.put(databaseName, handle);
			}
		}
	}

	protected SQLiteDatabase getDatabase() {
		TableHandle handle;
		synchronized (m_tableHandle) {
			handle = m_tableHandle.get(m_databaseName);
			handle.ActiveConnections += 1;
		}

		return handle.SqliteHelper.getDatabase();
	}

	protected String getDatabaseName() {
		return m_databaseName;
	}

	protected Context getContext() {
		return m_tableHandle.get(m_databaseName).SqliteHelper.getContext();
	}

	public final void close() {
		boolean close = false;

		synchronized (m_tableHandle) {

			TableHandle handle = m_tableHandle.get(m_databaseName);
			handle.ActiveConnections -= 1;

			if (handle.ActiveConnections == 0) {
				close = true;
			}

			if (close) {
				handle.SqliteHelper.close();
			}
		}
	}

	protected final void invalidateCache() {
		onInvalidateCache();
	}

	protected void onInvalidateCache() {
		m_localSyncId = m_globalSyncId.incrementAndGet();
	}

	protected final boolean inSync() {
		return m_localSyncId == m_globalSyncId.get();
	}
	
	private static class TableHandle {
		public int ActiveConnections;
		public SqliteHelper SqliteHelper; 
	}

}