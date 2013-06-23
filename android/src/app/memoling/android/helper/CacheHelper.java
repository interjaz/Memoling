package app.memoling.android.helper;

import java.util.ArrayList;
import java.util.Hashtable;

public final class CacheHelper<K, V> {

	private Hashtable<K, V> m_hashtable;
	private ArrayList<K> m_keyList;
	private int m_size;

	public CacheHelper(int size) {
		m_hashtable = new Hashtable<K, V>(size);
		m_keyList = new ArrayList<K>(size);
		m_size = size;
	}

	public synchronized V put(K key, V value) {
		m_hashtable.put(key, value);
		m_keyList.add(key);

		if (size() >= m_size) {
			K toDelete = m_keyList.get(0);
			m_hashtable.remove(toDelete);
			m_keyList.remove(toDelete);
		}

		return value;
	}

	public synchronized int size() {
		return m_keyList.size();
	}

	public synchronized boolean containsKey(K key) {
		return m_keyList.contains(key);
	}
	
	public synchronized boolean containsValue(V value) {
		return m_hashtable.containsValue(value);
	}
	
	public synchronized V get(K key) {
		// Reorder, make it less prone for deletion.
		m_keyList.remove(key);
		m_keyList.add(key);
		
		return m_hashtable.get(key);
	}
	
	public synchronized V remove(K key) {
		m_keyList.remove(key);
		return m_hashtable.remove(key);
	}
	
	public synchronized void clear() {
		m_keyList.clear();
		m_hashtable.clear();
	}
}
