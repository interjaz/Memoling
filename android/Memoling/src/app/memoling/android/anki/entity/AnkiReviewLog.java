package app.memoling.android.anki.entity;

public class AnkiReviewLog {

	// Database columns
	
	// database column: id
	private long m_reviewLogId;
	
	// database column: cid
	private long m_cardId;
	
	// database column: usn
	// used for synchronization
	private int m_universalSerialNumber;
	
	// database column: ease
	private int m_ease;
	
	// database column: ivl
	// -60 stands for 1 minute
	// -600 stands for 10 minutes
	// positive values stands for days
	private int m_interval;
	
	// database column: lastIvl
	// -60 stands for 1 minute
	// -600 stands for 10 minutes
	// positive values stands for days
	private int m_lastInterval;
	
	// database column: factor
	private int m_difficulty;
	
	// database column: time
	private int m_time;
	
	// database column: type
	// 0 - not learned
	// 1 - repeated
	// 2 - forgotten and again repeated
	private int m_type;

	public long getReviewLogId() {
		return m_reviewLogId;
	}

	public void setReviewLogId(long m_reviewLogId) {
		this.m_reviewLogId = m_reviewLogId;
	}

	public long getCardId() {
		return m_cardId;
	}

	public void setCardId(long m_cardId) {
		this.m_cardId = m_cardId;
	}

	public int getUniversalSerialNumber() {
		return m_universalSerialNumber;
	}

	public void setUniversalSerialNumber(int m_universalSerialNumber) {
		this.m_universalSerialNumber = m_universalSerialNumber;
	}

	public int getEase() {
		return m_ease;
	}

	public void setEase(int m_ease) {
		this.m_ease = m_ease;
	}

	public int getInterval() {
		return m_interval;
	}

	public void setInterval(int m_interval) {
		this.m_interval = m_interval;
	}

	public int getLastInterval() {
		return m_lastInterval;
	}

	public void setLastInterval(int m_lastInterval) {
		this.m_lastInterval = m_lastInterval;
	}

	public int getDifficulty() {
		return m_difficulty;
	}

	public void setDifficulty(int m_difficulty) {
		this.m_difficulty = m_difficulty;
	}

	public int getTime() {
		return m_time;
	}

	public void setTime(int m_time) {
		this.m_time = m_time;
	}

	public int getType() {
		return m_type;
	}

	public void setType(int m_type) {
		this.m_type = m_type;
	}
}
