package app.memoling.android.entity;

import org.json.JSONException;
import org.json.JSONObject;

public class WikiTranslationMeaning {

	private int m_meaningId;
	private String m_meaning;
	
	public int getMeaningId() {  return m_meaningId; }
	public void setMeaningId(int meaningId) { m_meaningId = meaningId; }

	public String getMeaning() {  return m_meaning; }
	public void setMeaning(String meaning) { m_meaning = meaning; }

	public WikiTranslationMeaning() {
	}
	
	public WikiTranslationMeaning(int meaningId, String meaning) {
		m_meaningId = meaningId;
		m_meaning = meaning;
	}
	
	public String serialize() throws JSONException {
		JSONObject json = new JSONObject();

		json.put("m_meaningId", m_meaningId);
		json.put("m_meaning", m_meaning);

		return json.toString();
	}

	public WikiTranslationMeaning deserialize(JSONObject json) throws JSONException {

		m_meaningId = json.getInt("m_meaningId");
		m_meaning = json.getString("m_meaning");

		return this;
	}
	
}
