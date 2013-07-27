package app.memoling.android.helper;

public abstract class Lazy<T> {
	
	private T m_value;
	
	protected abstract T create();
	
	public T getValue() {
		if(m_value == null) {
			m_value = create();
		}
		
		return m_value;
	}
}
