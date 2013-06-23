package app.memoling.android.entity;

import java.util.Date;

public class MemoBaseInfo {

	private MemoBase m_memoBase;
	public MemoBase getMemoBase() {  return m_memoBase; }
	public void setMemoBase(MemoBase memoBase) { m_memoBase = memoBase; }

	private int m_noAllMemos;
	public int getNoAllMemos() {  return m_noAllMemos; }
	public void setNoAllMemos(int noAllMemeos) { m_noAllMemos = noAllMemeos; }

	private int m_noActiveMemos;
	public int getNoActiveMemos() {  return m_noActiveMemos; }
	public void setNoActiveMemos(int noActiveMemos) { m_noActiveMemos = noActiveMemos; }

	private Date m_lastReviewed;
	public Date getLastReviewed() {  return m_lastReviewed; }
	public void setLastReviewed(Date lastReviewed) { m_lastReviewed = lastReviewed; }

	
}
