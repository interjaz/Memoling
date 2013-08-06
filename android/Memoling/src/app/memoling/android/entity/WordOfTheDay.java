package app.memoling.android.entity;

import org.json.JSONException;
import org.json.JSONObject;

import app.memoling.android.wordoftheday.WordOfTheDayMode;

public class WordOfTheDay {

	private String m_wordOfTheDayId;
	private String m_memoBaseId;
	private WordOfTheDayMode m_mode;
	private int m_providerId;
	private Language m_preLanguageFrom;
	private Language m_languageTo;

	public String getWordOfTheDayId() {
		return m_wordOfTheDayId;
	}

	public void setWordOfTheDayId(String wordOfTheDayId) {
		m_wordOfTheDayId = wordOfTheDayId;
	}

	public String getMemoBaseId() {
		return m_memoBaseId;
	}

	public void setMemoBaseId(String memoBaseId) {
		m_memoBaseId = memoBaseId;
	}

	public WordOfTheDayMode getMode() {
		return m_mode;
	}

	public void setMode(WordOfTheDayMode mode) {
		m_mode = mode;
	}

	public int getProviderId() {
		return m_providerId;
	}

	public void setProviderId(int providerId) {
		m_providerId = providerId;
	}

	public Language getPreLanguageFrom() {
		return m_preLanguageFrom;
	}

	public void setPreLanguageFrom(Language preLanguageFrom) {
		m_preLanguageFrom = preLanguageFrom;
	}

	public Language getLanguageTo() {
		return m_languageTo;
	}

	public void setLanguageTo(Language languageTo) {
		m_languageTo = languageTo;
	}

	public JSONObject serialize() throws JSONException {
		JSONObject json = new JSONObject();

		json.put("m_wordOfTheDayId", m_wordOfTheDayId);
		json.put("m_memoBaseId", m_memoBaseId);
		json.put("m_mode", m_mode.ordinal());
		json.put("m_providerId", m_providerId);
		json.put("m_preLanguageFrom", m_preLanguageFrom.getCode());
		json.put("m_languageTo", m_languageTo.getCode());

		return json;
	}

	public WordOfTheDay deserialize(JSONObject json) throws JSONException {

		m_wordOfTheDayId = json.getString("m_wordOfTheDayId");
		m_memoBaseId = json.getString("m_memoBaseId");
		m_mode = WordOfTheDayMode.values()[json.getInt("m_mode")];
		m_providerId = json.getInt("m_providerId");
		m_preLanguageFrom = Language.parse(json.getString("m_preLanguageFrom"));
		m_languageTo = Language.parse(json.getString("m_languageTo"));

		return this;
	}

}
