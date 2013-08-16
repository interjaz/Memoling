package app.memoling.android.entity;

import org.json.JSONException;
import org.json.JSONObject;

public class MemoSentence {

	private String m_memoSentenceId;
	private String m_memoId;
	
	private String m_originalSentence;
	private String m_translatedSentence;
	
	private Language m_translatedLanguage;
	private Language m_originalLanguage;
	
	public String getMemoSentenceId() {  return m_memoSentenceId; }
	public void setMemoSentenceId(String memoSentenceId) { m_memoSentenceId = memoSentenceId; }
	
	public String getMemoId() {  return m_memoId; }
	public void setMemoId(String memoId) { m_memoId = memoId; }

	public String getOriginalSentence() {  return m_originalSentence; }
	public void setOriginalSentence(String originalSentence) { m_originalSentence = originalSentence; }

	public String getTranslatedSentence() {  return m_translatedSentence; }
	public void setTranslatedSentence(String translatedSentence) { m_translatedSentence = translatedSentence; }


	public Language getTranslatedLanguage() {  return m_translatedLanguage; }
	public void setTranslatedLanguage(Language translatedLanguage) { m_translatedLanguage = translatedLanguage; }

	public Language getOriginalLanguage() {  return m_originalLanguage; }
	public void setOriginalLanguage(Language originalLanguage) { m_originalLanguage = originalLanguage; }

	public String serialize() throws JSONException {
		JSONObject json = new JSONObject();

		json.put("m_memoSentenceId", m_memoSentenceId);
		json.put("m_memoId", m_memoId);
		json.put("m_originalSentence", m_originalSentence);
		json.put("m_translatedSentence", m_translatedSentence);
		json.put("m_translatedLanguage", m_translatedLanguage.getCode());
		json.put("m_originalLanguage", m_originalLanguage.getCode());

		return json.toString();
	}

	public MemoSentence deserialize(JSONObject json) throws JSONException {

		m_memoSentenceId = json.getString("m_memoSentenceId");
		m_memoId = json.getString("m_memoId");
		m_originalSentence = json.getString("m_originalSentence");
		m_translatedSentence = json.getString("m_translatedSentence");
		m_translatedLanguage = Language.parse(json.getString("m_translatedLanguage"));
		m_originalLanguage =  Language.parse(json.getString("m_originalLanguage"));

		return this;
	}

}
