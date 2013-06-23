package app.memoling.android.db;

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
	private int m_version;
	private Context m_context;

	public SqliteAdapter(Context context, String databaseName, int version) {
		m_databaseName = databaseName;
		m_version = version;
		m_context = context;
	}

	protected SQLiteDatabase getDatabase() {
		try {
			TableHandle handle;
			synchronized (m_tableHandle) {
				handle = m_tableHandle.get(m_databaseName);
				if (handle == null) {
					handle = open();
				}
				handle.ActiveConnections += 1;
			}

			
			
			return handle.SqliteHelper.getDatabase();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	protected String getDatabaseName() {
		return m_databaseName;
	}

	protected int getVersion() {
		return m_version;
	}

	protected Context getContext() {
		return m_context;
	}

	private final TableHandle open() throws IOException {
		synchronized (m_tableHandle) {
			if (!m_tableHandle.containsKey(m_databaseName)) {

				TableHandle handle = new TableHandle();
				handle.ActiveConnections = 0;
				handle.SqliteHelper = new SqliteHelper(m_context, m_databaseName, m_version);

				m_tableHandle.put(m_databaseName, handle);
				return handle;
			}
		}
		
		return null;
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
				m_tableHandle.remove(m_databaseName);
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