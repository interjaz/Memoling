package app.memoling.android.anki.entity;

import java.util.Date;

public class AnkiCard {

	// Database columns
	
	// database column: id
	private Date m_cardId;
	
	// database column: nid
	private Date m_noteId;
	
	// database column: did
	private Date m_deckId;
	
	// database column: ord
	private int m_ord;
	
	// database column: mod
	// stands for date of last modification
	private Date m_lastModification;
	
	// database column: usn
	// used for synchronization
	private int m_universalSerialNumber;
	
	// database column: type
	// 0 - not learned
	// 1 - repeated
	// 2 - forgotten and again repeated
	private int m_type;
	
	// database column: queue
	private int m_queue;
	
	// database column: due
	private int m_due;
	
	// database column: ivl
	// -60 stands for 1 minute
	// -600 stands for 10 minutes
	// positive values stands for days
	private int m_interval;
	
	// database column: factor
	private int m_difficulty;
	
	// database column: reps
	private int m_numberAllAnswers;
	
	// database column: lapses
	private int m_numberWrongAnswers;
	
	// database column: left
	private int m_left;
	
	// database column: odue
	private int m_odue;
	
	// database column: odid
	private int m_odid;
	
	// database column: flags
	private int m_flags;
	
	// database column: data
	private String m_data;

	public Date getCardId() {
		return m_cardId;
	}

	public void setCardId(Date m_cardId) {
		this.m_cardId = m_cardId;
	}

	public Date getNoteId() {
		return m_noteId;
	}

	public void setNoteId(Date m_noteId) {
		this.m_noteId = m_noteId;
	}

	public Date getDeckId() {
		return m_deckId;
	}

	public void setDeckId(Date m_deckId) {
		this.m_deckId = m_deckId;
	}

	public int getOrd() {
		return m_ord;
	}

	public void setOrd(int m_ord) {
		this.m_ord = m_ord;
	}

	public Date getLastModification() {
		return m_lastModification;
	}

	public void setLastModification(Date m_lastModification) {
		this.m_lastModification = m_lastModification;
	}

	public int getUniversalSerialNumber() {
		return m_universalSerialNumber;
	}

	public void setUniversalSerialNumber(int m_universalSerialNumber) {
		this.m_universalSerialNumber = m_universalSerialNumber;
	}

	public int getType() {
		return m_type;
	}

	public void setType(int m_type) {
		this.m_type = m_type;
	}

	public int getQueue() {
		return m_queue;
	}

	public void setQueue(int m_queue) {
		this.m_queue = m_queue;
	}

	public int getDue() {
		return m_due;
	}

	public void setDue(int m_due) {
		this.m_due = m_due;
	}

	public int getInterval() {
		return m_interval;
	}

	public void setInterval(int m_interval) {
		this.m_interval = m_interval;
	}

	public int getDifficulty() {
		return m_difficulty;
	}

	public void setDifficulty(int m_difficulty) {
		this.m_difficulty = m_difficulty;
	}

	public int getNumberAllAnswers() {
		return m_numberAllAnswers;
	}

	public void setNumberAllAnswers(int m_numberAllAnswers) {
		this.m_numberAllAnswers = m_numberAllAnswers;
	}

	public int getNumberWrongAnswers() {
		return m_numberWrongAnswers;
	}

	public void setNumberWrongAnswers(int m_numberWrongAnswers) {
		this.m_numberWrongAnswers = m_numberWrongAnswers;
	}

	public int getLeft() {
		return m_left;
	}

	public void setLeft(int m_left) {
		this.m_left = m_left;
	}

	public int getOdue() {
		return m_odue;
	}

	public void setOdue(int m_odue) {
		this.m_odue = m_odue;
	}

	public int getOdid() {
		return m_odid;
	}

	public void setOdid(int m_odid) {
		this.m_odid = m_odid;
	}

	public int getFlags() {
		return m_flags;
	}

	public void setFlags(int m_flags) {
		this.m_flags = m_flags;
	}

	public String getData() {
		return m_data;
	}

	public void setData(String m_data) {
		this.m_data = m_data;
	}
}
