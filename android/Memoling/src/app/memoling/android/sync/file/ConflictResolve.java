package app.memoling.android.sync.file;


public class ConflictResolve {
	public final static int NoConflict = 0;
	public final static int TakeNewer = 1;
	public final static int TakeFile = 2;
	public final static int TakeDatabase = 4;
	public final static int Stop = 8;
	public final static int ForAll = 16;

	private int m_value;

	public boolean hasFlag(int flag) {
		return (m_value & flag) == flag;
	}

	public void setFlags(int flag) {
		m_value |= flag;
	}

	public void clear() {
		m_value = NoConflict;
	}

	public interface OnConflictResolve<T> {
		ConflictResolve onConflict(T internal, T external);
	}
	
	public interface OnConflictResolveHaltable<T> {
		void onConflict(T internal, T external, final Object waitObject, final ConflictResolve resolve);
	}

}

