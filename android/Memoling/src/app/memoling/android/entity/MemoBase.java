package app.memoling.android.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MemoBase {

	private String m_memoBaseId;
	private String m_name;
	private Date m_created;
	private boolean m_active;

	private List<Memo> m_memos;
	
	public String getMemoBaseId() {
		return m_memoBaseId;
	}

	public void setMemoBaseId(String memoBaseId) {
		m_memoBaseId = memoBaseId;
	}

	public String getName() {
		return m_name;
	}

	public void setName(String name) {
		m_name = name;
	}

	public Date getCreated() {
		return m_created;
	}

	public void setCreated(Date created) {
		m_created = created;
	}

	public boolean getActive() {
		return m_active;
	}

	public void setActive(boolean active) {
		m_active = active;
	}
	
	public List<Memo> getMemos() {
		return m_memos;
	}

	public void setMemos(List<Memo> memos) {
		m_memos = memos;
	}
	
	public JSONObject serialize() throws JSONException {
		JSONObject json = new JSONObject();

		json.put("m_memoBaseId", m_memoBaseId);
		json.put("m_name", m_name);
		json.put("m_created", m_created.getTime());
		json.put("m_active", m_active);

		if(m_memos != null) {
			JSONArray array = new JSONArray();
			for(Memo memo : m_memos) {
				array.put(memo.serialize());
			}
			json.put("m_memos", array);
		}
		
		return json;
	}

	public MemoBase deserialize(JSONObject json) throws JSONException {

		m_memoBaseId = json.getString("m_memoBaseId");
		m_name = json.getString("m_name");
		m_created = new Date(json.getLong("m_created"));
		m_active = json.getBoolean("m_active");

		if(json.has("m_memos")) {
			JSONArray array = json.getJSONArray("m_memos");
			m_memos = new ArrayList<Memo>();
			for(int i=0;i<array.length();i++) {
				m_memos.add(new Memo().deserialize(array.getJSONObject(i)));
			}
		}
		
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (m_active ? 1231 : 1237);
		result = prime * result + ((m_created == null) ? 0 : m_created.hashCode());
		result = prime * result + ((m_memoBaseId == null) ? 0 : m_memoBaseId.hashCode());
		result = prime * result + ((m_name == null) ? 0 : m_name.hashCode());
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
		MemoBase other = (MemoBase) obj;
		if (m_active != other.m_active)
			return false;
		if (m_created == null) {
			if (other.m_created != null)
				return false;
		} else if (!m_created.equals(other.m_created))
			return false;
		if (m_memoBaseId == null) {
			if (other.m_memoBaseId != null)
				return false;
		} else if (!m_memoBaseId.equals(other.m_memoBaseId))
			return false;
		if (m_name == null) {
			if (other.m_name != null)
				return false;
		} else if (!m_name.equals(other.m_name))
			return false;
		return true;
	}
}
