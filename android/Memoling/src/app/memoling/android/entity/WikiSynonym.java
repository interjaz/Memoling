package app.memoling.android.entity;

import org.json.JSONException;
import org.json.JSONObject;


public class WikiSynonym {

	private String m_expressionA;
	private String m_expressionB;
	private Language m_language;

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

	public Language getLanguage() {
		return m_language;
	}

	public void setLanguage(Language language) {
		m_language = language;
	}

	public String serialize() throws JSONException {
		JSONObject json = new JSONObject();

		json.put("m_expressionA", m_expressionA);
		json.put("m_expressionB", m_expressionB);
		json.put("m_language", m_language.getCode());

		return json.toString();
	}

	public WikiSynonym deserialize(JSONObject json) throws JSONException {

		m_expressionA = json.getString("m_expressionA");
		m_expressionB = json.getString("m_expressionB");
		m_language = Language.parse(json.getString("m_language"));

		return this;
	}

}
