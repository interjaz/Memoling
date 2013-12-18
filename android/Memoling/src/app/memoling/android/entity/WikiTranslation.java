package app.memoling.android.entity;

import org.json.JSONException;
import org.json.JSONObject;


public class WikiTranslation {

	private String m_expressionA;
	private String m_expressionB;
	private Language m_languageA;
	private Language m_languageB;
	private int m_meaningId;
	private WikiTranslationMeaning m_wikiTranslationMeaning;

	public String getExpressionA() {
		return m_expressionA;
	}

	public void setExpressionA(String expressionA) {
		m_expressionA = expressionA;
	}

	public String getExpressionB() {
		return m_expressionB;
	}

	public void setExpressionB(String expressionB) {
		m_expressionB = expressionB;
	}

	public Language getLanguageA() {
		return m_languageA;
	}

	public void setLanguageA(Language languageA) {
		m_languageA = languageA;
	}

	public Language getLanguageB() {
		return m_languageB;
	}

	public void setLanguageB(Language languageB) {
		m_languageB = languageB;
	}

	public int getMeaningId() {
		return m_meaningId;
	}

	public void setMeaningId(int meaningId) {
		m_meaningId = meaningId;
	}

	public WikiTranslationMeaning getWikiTranslationMeaning() {
		return m_wikiTranslationMeaning;
	}

	public void setWikiTranslationMeaning(WikiTranslationMeaning wikiTranslationMeaning) {
		m_wikiTranslationMeaning = wikiTranslationMeaning;
	}

	public String serialize() throws JSONException {
		JSONObject json = new JSONObject();

		json.put("m_expressionA", m_expressionA);
		json.put("m_expressionB", m_expressionB);
		json.put("m_languageA", m_languageA.getCode());
		json.put("m_languageB", m_languageB.getCode());
		json.put("m_meaningId", m_meaningId);

		return json.toString();
	}

	public WikiTranslation deserialize(JSONObject json) throws JSONException {

		m_expressionA = json.getString("m_expressionA");
		m_expressionB = json.getString("m_expressionB");
		m_languageA = Language.parse(json.getString("m_languageA"));
		m_languageB = Language.parse(json.getString("m_languageB"));
		m_meaningId = json.getInt("m_meaningId");

		return this;
	}

}
