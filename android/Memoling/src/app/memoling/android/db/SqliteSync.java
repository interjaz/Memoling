package app.memoling.android.db;

import java.util.Hashtable;
import java.util.concurrent.atomic.AtomicLong;

public class SqliteSync {

	private static AtomicLong m_globalSyncToken = new AtomicLong();
	
	private static class MutableLong {
		// Set to value smaller than zero, so initially it will be always out of sync.
		public long value = -1;
	}
	
	private static Hashtable<Class<?>, MutableLong> m_localSyncToken = new Hashtable<Class<?>, MutableLong>();
	
	public static long globalSyncToken() {
		return m_globalSyncToken.get();
	}
	
	public static long globalSyncTokenGetAndIncrement() {
		return m_globalSyncToken.incrementAndGet();
	}
	
	private static MutableLong localSync(Class<?> type) {
		synchronized(m_localSyncToken) {
			if(!m_localSyncToken.containsKey(type)) {
				m_localSyncToken.put(type, new MutableLong());
			}
		}
		
		return m_localSyncToken.get(type);
	}
	
	public static long localSyncToken(Class<?> type) {
		return localSync(type).value;
	}
	
	public static void localSyncTokenSet(Class<?> type, long value) {
		localSync(type).value = value;
	}
}
