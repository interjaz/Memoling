package app.memoling.android.entity;

import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import app.memoling.android.Language;

public class Word {
	private String m_wordId;
	private String m_word;
	private Language m_language;

	public String getWordId() {
		if (m_wordId == null) {
			m_wordId = UUID.randomUUID().toString();
		}
		return m_wordId;
	}

	public void setWordId(String wordId) {
		m_wordId = wordId;
	}

	public String getWord() {
		return m_word;
	}

	public void setWord(String word) {
		m_word = word;
	}

	public Language getLanguage() {
		return m_language;
	}

	public void setLanguage(Language language) {
		m_language = language;
	}

	public Word() {
	}

	public Word(String word) {
		m_word = word;
	}

	public Word(String wordId, String word, Language language) {
		m_wordId = wordId;
		m_word = word;
		m_language = language;
	}

	public JSONObject serialize() throws JSONException {
		JSONObject json = new JSONObject();

		json.put("m_wordId", m_wordId);
		json.put("m_word", m_word);
		json.put("m_language", m_language);

		return json;
	}

	public Word deserialize(JSONObject json) throws JSONException {

		m_wordId = json.getString("m_wordId");
		m_word = json.getString("m_word");
		m_language = Language.parse(json.getString("m_language"));

		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((m_language == null) ? 0 : m_language.hashCode());
		result = prime * result + ((m_word == null) ? 0 : m_word.hashCode());
		result = prime * result + ((m_wordId == null) ? 0 : m_wordId.hashCode());
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
		Word other = (Word) obj;
		if (m_language != other.m_language)
			return false;
		if (m_word == null) {
			if (other.m_word != null)
				return false;
		} else if (!m_word.equals(other.m_word))
			return false;
		if (m_wordId == null) {
			if (other.m_wordId != null)
				return false;
		} else if (!m_wordId.equals(other.m_wordId))
			return false;
		return true;
	}

}
