package app.memoling.android.anki.entity;

public class AnkiGrave {

	// Database columns
	
	// database column: id
	private int m_graveId;
	
	// database column: usn
	private int m_universalSerialNumber;
	
	// database column: oid
	private long m_oid;
	
	// database column: type
	// 0 - not learned
	// 1 - repeated
	// 2 - forgotten and again repeated
	private int m_type;

	public int getGraveId() {
		return m_graveId;
	}

	public void setGraveId(int m_graveId) {
		this.m_graveId = m_graveId;
	}

	public int getUniversalSerialNumber() {
		return m_universalSerialNumber;
	}

	public void setUniversalSerialNumber(int m_universalSerialNumber) {
		this.m_universalSerialNumber = m_universalSerialNumber;
	}

	public long getOid() {
		return m_oid;
	}

	public void setOid(long m_oid) {
		this.m_oid = m_oid;
	}

	public int getType() {
		return m_type;
	}

	public void setType(int m_type) {
		this.m_type = m_type;
	}
}
