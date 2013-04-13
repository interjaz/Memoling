package com.interjaz.db;

import java.io.IOException;
import java.util.Hashtable;
import java.util.concurrent.atomic.AtomicLong;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public abstract class SqliteAdapter {

	protected static final String Tag = "SqliteAdapter";

	private SqliteHelper m_sqliteHelper;

	private static Hashtable<String, Integer> m_tableHandle = new Hashtable<String, Integer>();

	private static AtomicLong m_globalSyncId = new AtomicLong();

	private static long m_localSyncId = 0;

	public SqliteAdapter(Context context, String databaseName, int version) throws IOException {
		m_sqliteHelper = new SqliteHelper(context, databaseName, version);

		synchronized (m_tableHandle) {
			if (!m_tableHandle.containsKey(databaseName)) {
				m_tableHandle.put(databaseName, Integer.valueOf(0));
			}
		}
	}

	protected SQLiteDatabase getDatabase() {
		synchronized (m_tableHandle) {
			String key = m_sqliteHelper.getDbName();

			Integer opened = m_tableHandle.get(key);
			m_tableHandle.remove(key);
			m_tableHandle.put(key, Integer.valueOf(opened.intValue() + 1));
		}

		return m_sqliteHelper.getDatabase();
	}

	protected String getDatabaseName() {
		return m_sqliteHelper.getDbName();
	}

	protected Context getContext() {
		return m_sqliteHelper.getContext();
	}

	public final void close() {

		boolean close = false;

		synchronized (m_tableHandle) {
			String key = m_sqliteHelper.getDbName();

			Integer opened = m_tableHandle.get(key);
			m_tableHandle.remove(key);
			m_tableHandle.put(key, Integer.valueOf(opened.intValue() - 1));

			if (opened.intValue() == 1) {
				close = true;
			}

			if (close) {
				m_sqliteHelper.close();
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

}