package app.memoling.android.entity;

import org.json.JSONException;
import org.json.JSONObject;

import app.memoling.android.wiktionary.WiktionaryMark;

public class WikiDefinition {

	private String m_expression;
	private Language m_language;
	private String m_partOfSpeech;
	private String m_definition;

	public String getExpression() {
		return m_expression;
	}

	public void setExpression(String expression) {
		m_expression = expression;
	}

	public Language getLanguage() {
		return m_language;
	}

	public void setLanguage(Language language) {
		m_language = language;
	}

	public String getPartOfSpeech() {
		return m_partOfSpeech;
	}

	public void setPartOfSpeech(String partOfSpeech) {
		m_partOfSpeech = partOfSpeech;
	}

	public String getDefinition() {
		return m_definition;
	}

	public void setDefinition(String definition) {
		m_definition = definition;
	}

	public String serialize() throws JSONException {
		JSONObject json = new JSONObject();

		json.put("m_expression", m_expression);
		json.put("m_language", m_language.getCode());
		json.put("m_partOfSpeech", m_partOfSpeech);
		json.put("m_definition", m_definition);

		return json.toString();
	}

	public WikiDefinition deserialize(JSONObject json) throws JSONException {

		m_expression = json.getString("m_expression");
		m_language = Language.parse(json.getString("m_language"));
		m_partOfSpeech = json.getString("m_partOfSpeech");
		m_definition = json.getString("m_definition");

		return this;
	}

	public String getHtmlDefinition() {
		return WiktionaryMark.toHtml(m_definition);
	}
}
