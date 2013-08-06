package app.memoling.android.wordoftheday.provider;

import app.memoling.android.entity.Language;

public class Provider {

	public static class WordWithDescription {
		public String m_description = "";
		public String m_word;

		public WordWithDescription(String word, String description) {
			m_word = word;
			m_description = description == null ? "" : description;
		}

		public String getWord() {
			return m_word;
		}

		public String getDescription() {
			return m_description;
		}
	}

	public static interface PostProcessor {
		WordWithDescription process(Object raw);
	}

	private final int m_id;
	private final String m_owner;
	private final String m_uri;
	private final ResourceType m_resourceType;
	private final String m_root;
	private final Language m_baseLanguage;
	private Language m_preTranslateToLanguage;
	private Language m_translateToLanguage;
	private final PostProcessor m_postProcessor;

	public static enum ResourceType {
		RSS, ATOM, JSON, XML, DB
	}

	public Provider(int id, Language baseLanguage, String owner, ResourceType type, String uri, String root,
			PostProcessor postProcessor) {
		m_id = id;
		m_baseLanguage = baseLanguage;
		m_owner = owner;
		m_uri = uri;
		m_resourceType = type;
		m_root = root;
		m_postProcessor = postProcessor;
	}

	public int getId() {
		return m_id;
	}

	public String getUri() {
		return m_uri;
	}

	public ResourceType getResourceType() {
		return m_resourceType;
	}

	public String getRoot() {
		return m_root;
	}

	public Language getBaseLanguage() {
		return m_baseLanguage;
	}

	public Language getTranslateToLanguage() {
		return m_translateToLanguage;
	}

	public PostProcessor getPostProcessor() {
		return m_postProcessor;
	}

	public String getOwner() {
		return m_owner;
	}

	public void setTranslateToLanguage(Language language) {
		m_translateToLanguage = language;
	}

	public Language getPreTranslateToLanguage() {
		return m_preTranslateToLanguage;
	}

	public void setPreTranslateToLanguage(Language language) {
		m_preTranslateToLanguage = language;
	}
}
