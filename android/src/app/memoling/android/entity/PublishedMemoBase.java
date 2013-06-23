package app.memoling.android.entity;

import org.json.JSONException;
import org.json.JSONObject;

import app.memoling.android.Language;
import app.memoling.android.facebook.FacebookUser;

public class PublishedMemoBase {

	private String m_publishedMemoBaseId;
	private String m_facebookUserId;
	private String m_memoBaseId;
	private String m_memoBaseGenreId;
	private String m_description;
	private int m_downloads;
	private Language m_primaryLanguageAIso639;
	private Language m_primaryLanguageBIso639;
	
	private FacebookUser m_facebookUser;
	private MemoBase m_memoBase;
	private MemoBaseGenre m_memoBaseGenre;
	
	// Helper properties
	private int m_memosCount;
	
	public String getPublishedMemoBaseId() {  return m_publishedMemoBaseId; }
	public void setPublishedMemoBaseId(String publishedMemoBaseId) { m_publishedMemoBaseId = publishedMemoBaseId; }

	public String getFacebookUserId() {  return m_facebookUserId; }
	public void setFacebookUserId(String facebookUserId) { m_facebookUserId = facebookUserId; }

	public String getMemoBaseId() {  return m_memoBaseId; }
	public void setMemoBaseId(String memoBaseId) { m_memoBaseId = memoBaseId; }

	public String getMemoBaseGenreId() {  return m_memoBaseGenreId; }
	public void setMemoBaseGenreId(String memoBaseGenreId) { m_memoBaseGenreId = memoBaseGenreId; }

	public String getDescription() {  return m_description; }
	public void setDescription(String description) { m_description = description; }

	public int getDownloads() {return m_downloads; }
	public void setDownloads(int downloads) { m_downloads = downloads; }

	public Language getPrimaryLanguageAIso639() { return m_primaryLanguageAIso639; }
	public void setPrimaryLanguageAIso639(Language language) { m_primaryLanguageAIso639 = language; }

	public Language getPrimaryLanguageBIso639() { return m_primaryLanguageBIso639; }
	public void setPrimaryLanguageBIso639(Language language) { m_primaryLanguageBIso639 = language; }
	
	public FacebookUser getFacebookUser() {  return m_facebookUser; }
	public void setFacebookUser(FacebookUser facebookUser) { m_facebookUser = facebookUser; }

	public MemoBase getMemoBase() {  return m_memoBase; }
	public void setMemoBase(MemoBase memoBase) { m_memoBase = memoBase; }

	public MemoBaseGenre getMemoBaseGenre() {  return m_memoBaseGenre; }
	public void setMemoBaseGenre(MemoBaseGenre memoBaseGenre) { m_memoBaseGenre = memoBaseGenre; }

	public int getMemosCount() { return m_memosCount; }
	public void setMemosCount(int memos) { m_memosCount = memos; }
	
	public String serialize() throws JSONException {
		JSONObject json = new JSONObject();

		json.put("m_publishedMemoBaseId", m_publishedMemoBaseId);
		json.put("m_facebookUserId", m_facebookUserId);
		json.put("m_memoBaseId", m_memoBaseId);
		json.put("m_memoBaseGenreId", m_memoBaseGenreId);
		json.put("m_description", m_description);
		json.put("m_facebookUser", m_facebookUser.serialize());
		json.put("m_memoBase", m_memoBase.serialize());
		json.put("m_memoBaseGenre", m_memoBaseGenre);

		return json.toString();
	}

	public PublishedMemoBase deserialize(JSONObject json) throws JSONException {

		m_publishedMemoBaseId = json.getString("m_publishedMemoBaseId");
		m_facebookUserId = json.getString("m_facebookUserId");
		m_memoBaseId = json.getString("m_memoBaseId");
		m_memoBaseGenreId = json.getString("m_memoBaseGenreId");
		m_description = json.getString("m_description");
		m_facebookUser = new FacebookUser().deserialize(json.getJSONObject("m_facebookUser"));
		m_memoBase = new MemoBase().deserialize(json.getJSONObject("m_memoBase"));
		m_memoBaseGenre = new MemoBaseGenre().deserialize(json.getJSONObject("m_memoBaseGenre"));

		return this;
	}
}
