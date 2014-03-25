package app.memoling.android.anki.entity;

import java.util.Date;

public class AnkiNote {

	// Database columns
	
	// database column: id
	private Date m_noteId;
	
	// database column: guid
	private String m_guid;
	
	// database column: mid
	private Date m_mid;
	
	// database column: mod
	private Date m_lastModification;
	
	// database column: usn
	// used for synchronization
	private int m_universalSerialNumber;
	
	// database column: tags
	private String m_tags;
	
	// database column: flds
	private String m_flds;
	
	// database column: sfld
	private String m_sfld;
	
	// database column: csum
	private long m_checksum;
	
	// database column: flags
	private int m_flags;
	
	// database column: data
	private String m_data;

	public Date getNoteId() {
		return m_noteId;
	}

	public void setNoteId(Date m_noteId) {
		this.m_noteId = m_noteId;
	}

	public String getGuid() {
		return m_guid;
	}

	public void setGuid(String m_guid) {
		this.m_guid = m_guid;
	}

	public Date getMid() {
		return m_mid;
	}

	public void setMid(Date m_mid) {
		this.m_mid = m_mid;
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

	public String getTags() {
		return m_tags;
	}

	public void setTags(String m_tags) {
		this.m_tags = m_tags;
	}

	public String getFlds() {
		return m_flds;
	}

	public void setFlds(String m_flds) {
		this.m_flds = m_flds;
	}

	public String getSfld() {
		return m_sfld;
	}

	public void setSfld(String m_sfld) {
		this.m_sfld = m_sfld;
	}

	public long getChecksum() {
		return m_checksum;
	}

	public void setChecksum(long m_checksum) {
		this.m_checksum = m_checksum;
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
