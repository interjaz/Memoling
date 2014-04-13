package app.memoling.android.entity;

import java.util.Date;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import app.memoling.android.sync.cloud.ISyncEntity;

public class Memo implements ISyncEntity {

	// Database columns
	private String m_memoId;
	private String m_memoBaseId;
	private String m_wordAId;
	private String m_wordBId;
	private Date m_created;
	private Date m_lastReviewed;
	private int m_displayed;
	private int m_correctAnsweredWordA;
	private int m_correctAnsweredWordB;
	private boolean m_active;
	// Extra columns
	private Word m_wordA;
	private Word m_wordB;
	private MemoBase m_memoBase;

	public String getMemoId() {
		return m_memoId;
	}

	public void setMemoId(String memoId) {
		m_memoId = memoId;
	}

	public String getMemoBaseId() {
		return m_memoBaseId;
	}

	public void setMemoBaseId(String memoBaseId) {
		m_memoBaseId = memoBaseId;
	}

	public String getWordAId() {
		return m_wordAId;
	}

	public void setWordAId(String wordAId) {
		m_wordAId = wordAId;
	}

	public String getWordBId() {
		return m_wordBId;
	}

	public void setWordBId(String wordBId) {
		m_wordBId = wordBId;
	}

	public Date getCreated() {
		return m_created;
	}

	public void setCreated(Date created) {
		m_created = created;
	}

	public Date getLastReviewed() {
		return m_lastReviewed;
	}

	public void setLastReviewed(Date lastReviewed) {
		m_lastReviewed = lastReviewed;
	}

	public int getDisplayed() {
		return m_displayed;
	}

	public void setDisplayed(int displayed) {
		m_displayed = displayed;
	}

	public int getCorrectAnsweredWordA() {
		return m_correctAnsweredWordA;
	}

	public void setCorrectAnsweredWordA(int correctAnsweredWordA) {
		m_correctAnsweredWordA = correctAnsweredWordA;
	}

	public int getCorrectAnsweredWordB() {
		return m_correctAnsweredWordB;
	}

	public void setCorrectAnsweredWordB(int correctAnsweredWordB) {
		m_correctAnsweredWordB = correctAnsweredWordB;
	}

	public boolean getActive() {
		return m_active;
	}

	public void setActive(boolean active) {
		m_active = active;
	}

	public Word getWordA() {
		return m_wordA;
	}

	public void setWordA(Word wordA) {
		m_wordA = wordA;
		if (wordA != null) {
			m_wordAId = wordA.getWordId();
		}
	}

	public Word getWordB() {
		return m_wordB;
	}

	public void setWordB(Word wordB) {
		m_wordB = wordB;
		if (wordB != null) {
			m_wordBId = wordB.getWordId();
		}
	}

	public MemoBase getMemoBase() {
		return m_memoBase;
	}

	public void setMemoBase(MemoBase memoBase) {
		m_memoBase = memoBase;
	}

	public Memo() {
	}

	public Memo(Word wordA, Word wordB, String memoBaseId) {
		m_memoId = UUID.randomUUID().toString();
		m_memoBaseId = memoBaseId;
		m_wordA = wordA;
		m_wordAId = wordA.getWordId();
		m_wordB = wordB;
		m_wordBId = wordB.getWordId();
		m_created = new Date();
		m_lastReviewed = new Date();
		m_displayed = 0;
		m_correctAnsweredWordA = 0;
		m_correctAnsweredWordB = 0;
		m_active = true;
	}

	public JSONObject serialize() throws JSONException {
		JSONObject json = new JSONObject();

		json.put("m_memoId", m_memoId);
		json.put("m_memoBaseId", m_memoBaseId);
		json.put("m_wordAId", m_wordAId);
		json.put("m_wordBId", m_wordBId);
		json.put("m_created", m_created.getTime());
		json.put("m_lastReviewed", m_lastReviewed.getTime());
		json.put("m_displayed", m_displayed);
		json.put("m_correctAnsweredWordA", m_correctAnsweredWordA);
		json.put("m_correctAnsweredWordB", m_correctAnsweredWordB);
		json.put("m_active", m_active);

		if (m_wordA != null) {
			json.put("m_wordA", m_wordA.serialize());
		}
		if (m_wordB != null) {
			json.put("m_wordB", m_wordB.serialize());
		}

		return json;
	}

	public Memo deserialize(JSONObject json) throws JSONException {

		m_memoId = json.getString("m_memoId");
		m_memoBaseId = json.getString("m_memoBaseId");
		m_wordAId = json.getString("m_wordAId");
		m_wordBId = json.getString("m_wordBId");
		m_created = new Date(json.getLong("m_created"));
		m_lastReviewed = new Date(json.getLong("m_lastReviewed"));
		m_displayed = json.getInt("m_displayed");
		m_correctAnsweredWordA = json.getInt("m_correctAnsweredWordA");
		m_correctAnsweredWordB = json.getInt("m_correctAnsweredWordB");
		m_active = json.getBoolean("m_active");
		m_wordB = new Word().deserialize(json.getJSONObject("m_wordB"));

		JSONObject extra;
		extra = json.optJSONObject("m_wordA");
		if (extra != null) {
			m_wordA = new Word().deserialize(extra);
		}
		extra = json.optJSONObject("m_wordB");
		if (extra != null) {
			m_wordB = new Word().deserialize(extra);
		}

		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (m_active ? 1231 : 1237);
		result = prime * result + m_correctAnsweredWordA;
		result = prime * result + m_correctAnsweredWordB;
		result = prime * result + ((m_created == null) ? 0 : m_created.hashCode());
		result = prime * result + m_displayed;
		result = prime * result + ((m_lastReviewed == null) ? 0 : m_lastReviewed.hashCode());
		result = prime * result + ((m_memoBase == null) ? 0 : m_memoBase.hashCode());
		result = prime * result + ((m_memoBaseId == null) ? 0 : m_memoBaseId.hashCode());
		result = prime * result + ((m_memoId == null) ? 0 : m_memoId.hashCode());
		result = prime * result + ((m_wordA == null) ? 0 : m_wordA.hashCode());
		result = prime * result + ((m_wordAId == null) ? 0 : m_wordAId.hashCode());
		result = prime * result + ((m_wordB == null) ? 0 : m_wordB.hashCode());
		result = prime * result + ((m_wordBId == null) ? 0 : m_wordBId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Memo other = (Memo) obj;
		if (m_active != other.m_active)
			return false;
		if (m_correctAnsweredWordA != other.m_correctAnsweredWordA)
			return false;
		if (m_correctAnsweredWordB != other.m_correctAnsweredWordB)
			return false;
		if (m_created == null) {
			if (other.m_created != null)
				return false;
		} else if (!m_created.equals(other.m_created))
			return false;
		if (m_displayed != other.m_displayed)
			return false;
		if (m_lastReviewed == null) {
			if (other.m_lastReviewed != null)
				return false;
		} else if (!m_lastReviewed.equals(other.m_lastReviewed))
			return false;
		if (m_memoBase == null) {
			if (other.m_memoBase != null)
				return false;
		} else if (!m_memoBase.equals(other.m_memoBase))
			return false;
		if (m_memoBaseId == null) {
			if (other.m_memoBaseId != null)
				return false;
		} else if (!m_memoBaseId.equals(other.m_memoBaseId))
			return false;
		if (m_memoId == null) {
			if (other.m_memoId != null)
				return false;
		} else if (!m_memoId.equals(other.m_memoId))
			return false;
		if (m_wordA == null) {
			if (other.m_wordA != null)
				return false;
		} else if (!m_wordA.equals(other.m_wordA))
			return false;
		if (m_wordAId == null) {
			if (other.m_wordAId != null)
				return false;
		} else if (!m_wordAId.equals(other.m_wordAId))
			return false;
		if (m_wordB == null) {
			if (other.m_wordB != null)
				return false;
		} else if (!m_wordB.equals(other.m_wordB))
			return false;
		if (m_wordBId == null) {
			if (other.m_wordBId != null)
				return false;
		} else if (!m_wordBId.equals(other.m_wordBId))
			return false;
		return true;
	}

	@Override
	public JSONObject encodeEntity() throws JSONException {

		JSONObject json = new JSONObject();

		json.put("memoId", m_memoId);
		json.put("memoBaseId", m_memoBaseId);
		json.put("wordAId", m_wordAId);
		json.put("wordBId", m_wordBId);
		json.put("created", m_created.getTime()/1000L);
		json.put("lastReviewed", m_lastReviewed.getTime()/1000L);
		json.put("displayed", m_displayed);
		json.put("correctAnsweredWordA", m_correctAnsweredWordA);
		json.put("correctAnsweredWordB", m_correctAnsweredWordB);
		json.put("active", m_active);
		
		return json;
	}

	@Override
	public ISyncEntity decodeEntity(JSONObject json) throws JSONException {
		
		m_memoId = json.getString("memoId");
		m_memoBaseId = json.getString("memoBaseId");
		m_wordAId = json.getString("wordAId");
		m_wordBId = json.getString("wordBId");
		m_created = new Date(json.getLong("created")*1000L);
		m_lastReviewed = new Date(json.getLong("lastReviewed")*1000L);
		m_displayed = json.getInt("displayed");
		m_correctAnsweredWordA = json.getInt("correctAnsweredWordA");
		m_correctAnsweredWordB = json.getInt("correctAnsweredWordB");
		m_active = json.getBoolean("active");

		return this;
	}

}
