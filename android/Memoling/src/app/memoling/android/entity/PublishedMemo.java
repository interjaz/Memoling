package app.memoling.android.entity;

import org.json.JSONException;
import org.json.JSONObject;

import app.memoling.android.facebook.FacebookUser;

public class PublishedMemo {

	private String m_publishedMemoId;
	private String m_memoId;
	private String m_facebookUserId;
	
	private Memo m_memo;
	private FacebookUser m_user;
	
	public String getPublishedMemoId() {  return m_publishedMemoId; }
	public void setPublishedMemoId(String publishedMemoId) { m_publishedMemoId = publishedMemoId; }

	public String getMemoId() {  return m_memoId; }
	public void setMemoId(String memoId) { m_memoId = memoId; }

	public String getFacebookUserId() {  return m_facebookUserId; }
	public void setFacebookUserId(String facebookUserId) { m_facebookUserId = facebookUserId; }

	public Memo getMemo() {  return m_memo; }
	public void setMemo(Memo memo) { m_memo = memo; }

	public FacebookUser getUser() {  return m_user; }
	public void setUser(FacebookUser user) { m_user = user; }

	public String serialize() throws JSONException {
		JSONObject json = new JSONObject();

		json.put("m_publishedMemoId", m_publishedMemoId);
		json.put("m_memoId", m_memoId);
		json.put("m_facebookUserId", m_facebookUserId);
		json.put("m_memo", m_memo.serialize());
		json.put("m_user", m_user.serialize());

		return json.toString();
	}

	public PublishedMemo deserialize(JSONObject json) throws JSONException {

		m_publishedMemoId = json.getString("m_publishedMemoId");
		m_memoId = json.getString("m_memoId");
		m_facebookUserId = json.getString("m_facebookUserId");
		m_memo = new Memo().deserialize(json.getJSONObject("m_memo"));
		m_user = new FacebookUser().deserialize(json.getJSONObject("m_user"));

		return this;
	}

	
	
	
}
