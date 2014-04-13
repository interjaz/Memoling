package app.memoling.android.anki.entity;

public class AnkiIndexStat {

	// Database columns
	
	// database column: rowid
	private int m_rowId;
	
	// database column: tbl
	private String m_table;
	
	// database column: idx
	private String m_index;
	
	// database column: stat
	private String m_statistics;

	public int getRowId() {
		return m_rowId;
	}

	public void setRowId(int m_rowId) {
		this.m_rowId = m_rowId;
	}

	public String getTable() {
		return m_table;
	}

	public void setTable(String m_table) {
		this.m_table = m_table;
	}

	public String getIndex() {
		return m_index;
	}

	public void setIndex(String m_index) {
		this.m_index = m_index;
	}

	public String getStatistics() {
		return m_statistics;
	}

	public void setStatistics(String m_statistics) {
		this.m_statistics = m_statistics;
	}
}
