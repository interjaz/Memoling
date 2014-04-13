package app.memoling.android.entity;

import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import app.memoling.android.sync.cloud.ISyncEntity;

public class Word implements ISyncEntity {
	private String m_wordId;
	private String m_word;
	private Language m_language;
	private String m_description;

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

	public String getDescription() {
		return m_description;
	}

	public void setDescription(String description) {
		m_description = description;
	}

	public Word() {
	}

	public Word(String word) {
		m_word = word;
		m_description = "";
	}

	public Word(String wordId, String word, Language language) {
		m_wordId = wordId;
		m_word = word;
		m_language = language;
		m_description = "";
	}

	public Word(Word word) {
		if (word == null) {
			throw new RuntimeException("Cannot copy null object");
		}

		if (word.m_wordId != null) {
			m_wordId = new String(word.m_wordId);
		}
		if (word.m_word != null) {
			m_word = new String(word.m_word);
		}
		if (word.m_language != null) {
			m_language = Language.values()[word.m_language.ordinal()];
		}
		if (word.m_description != null) {
			m_description = new String(word.m_description);
		}
	}

	public JSONObject serialize() throws JSONException {
		JSONObject json = new JSONObject();

		json.put("m_wordId", m_wordId);
		json.put("m_word", m_word);
		json.put("m_language", m_language);
		json.put("m_description", m_description);

		return json;
	}

	public Word deserialize(JSONObject json) throws JSONException {

		m_wordId = json.getString("m_wordId");
		m_word = json.getString("m_word");
		m_language = Language.parse(json.getString("m_language"));
		m_description = json.getString("m_description");

		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((m_language == null) ? 0 : m_language.hashCode());
		result = prime * result + ((m_word == null) ? 0 : m_word.hashCode());
		result = prime * result + ((m_wordId == null) ? 0 : m_wordId.hashCode());
		result = prime * result + ((m_description == null) ? 0 : m_description.hashCode());
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
		if (m_description == null) {
			if (other.m_description != null)
				return false;
		} else if (!m_description.equals(other.m_description))
			return false;

		return true;
	}

	@Override
	public JSONObject encodeEntity() throws JSONException {
		JSONObject json = new JSONObject();

		json.put("wordId", m_wordId);
		json.put("word", m_word);
		json.put("languageIso639", m_language);
		json.put("description", m_description);

		return json;
	}

	@Override
	public ISyncEntity decodeEntity(JSONObject json) throws JSONException {
		
		m_wordId = json.getString("wordId");
		m_word = json.getString("word");
		m_language = Language.parse(json.getString("languageIso639"));
		m_description = json.getString("description");
		
		return this;
	}

}
