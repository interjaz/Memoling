package app.memoling.android.db;

import java.io.IOException;
import java.util.Hashtable;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import app.memoling.android.Config;

public abstract class SqliteAdapter {

	protected static final String Tag = "SqliteAdapter";

	private static Hashtable<String, TableHandle> m_tableHandle = new Hashtable<String, TableHandle>();

	private String m_databaseName;
	private int m_version;
	private Context m_context;

	private boolean m_persistant;

	public SqliteAdapter(Context context) {
		this(context, false);
	}

	public SqliteAdapter(Context context, boolean persistant) {
		m_context = context;
		m_databaseName = Config.DatabaseName;
		m_version = Config.DatabaseVersion;
		m_persistant = persistant;
	}

	public SQLiteDatabase getDatabase() {
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

	public final void closeDatabase() {
		boolean close = false;

		synchronized (m_tableHandle) {

			TableHandle handle = m_tableHandle.get(m_databaseName);
			handle.ActiveConnections -= 1;

			if (handle.ActiveConnections == 0) {
				close = true;
			}

			if (close && !m_persistant) {
				close(handle);
			}
		}
	}

	public String getDatabaseName() {
		return m_databaseName;
	}

	public int getVersion() {
		return m_version;
	}

	public Context getContext() {
		return m_context;
	}

	/**
	 * Call this if inSync return false - to drop local caches
	 */
	public final void invalidateLocalCache() {
		onInvalidateLocalCache();
	}

	/**
	 * Call this method when altering database data
	 */
	public final void invalidateGlobalCache() {
		onInvalidateGlobalCache();
	}
	
	protected void onInvalidateLocalCache() {
		SqliteSync.localSyncTokenSet(this.getClass(), SqliteSync.globalSyncToken());
	}
	
	protected void onInvalidateGlobalCache() {
		SqliteSync.localSyncTokenSet(this.getClass(), SqliteSync.globalSyncTokenGetAndIncrement());
	}

	public final boolean inSync() {
		return SqliteSync.localSyncToken(this.getClass()) == SqliteSync.globalSyncToken();
	}

	public final void closePersistant() {
		synchronized (m_tableHandle) {
			TableHandle handle = m_tableHandle.get(m_databaseName);
			if (handle != null) {
				close(handle);
			}
		}
	}

	private final TableHandle open() throws IOException {
		synchronized (m_tableHandle) {
			TableHandle handle = new TableHandle();
			handle.ActiveConnections = 0;
			handle.SqliteHelper = new SqliteProvider(m_context, m_databaseName, m_version);

			m_tableHandle.put(m_databaseName, handle);
			return handle;
		}
	}

	private final void close(TableHandle handle) {
		synchronized (m_tableHandle) {
			handle.SqliteHelper.close();
			m_tableHandle.remove(m_databaseName);
		}
	}

	private static class TableHandle {
		public int ActiveConnections;
		public SqliteProvider SqliteHelper;
	}

}