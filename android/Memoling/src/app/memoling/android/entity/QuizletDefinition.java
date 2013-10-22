package app.memoling.android.entity;

import java.util.ArrayList;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class QuizletDefinition {

	private String m_quizletDefinitionId;
	private String m_word;
	private String m_speechPart;
	private String m_definition;
	private ArrayList<String> m_examples;
	private boolean m_isOfficial;

	public String getQuizletDefinitionId() {
		return m_quizletDefinitionId;
	}

	public void setQuizletDefinitionId(String quizletDefinitionId) {
		m_quizletDefinitionId = quizletDefinitionId;
	}
	
	public String getWord() {
		return m_word;
	}

	public void setWord(String word) {
		m_word = word;
	}

	public String getSpeechPart() {
		return m_speechPart;
	}

	public void setSpeechPart(String speechPart) {
		m_speechPart = speechPart;
	}

	public String getDefinition() {
		return m_definition;
	}

	public void setDefinition(String definition) {
		m_definition = definition;
	}

	public ArrayList<String> getExamples() {
		return m_examples;
	}

	public void setExamples(ArrayList<String> examples) {
		m_examples = examples;
	}

	public boolean getIsOfficial() {
		return m_isOfficial;
	}

	public void setIsOfficial(boolean isOfficial) {
		m_isOfficial = isOfficial;
	}

	public QuizletDefinition() {
		
	}
	
	public QuizletDefinition(boolean isOfficial) {
		m_isOfficial = isOfficial;
	}

	/**
	 * This is Quizlet specific serialization
	 */
	public String serialize() throws JSONException {
		JSONObject json = new JSONObject();

		json.put("word", m_word);
		json.put("speechPart", m_speechPart);
		json.put("definition", m_definition);

		JSONArray array = new JSONArray();
		for (String example : m_examples) {
			array.put(example);
		}
		json.put("examples", array);

		json.put("is_official", m_isOfficial);
		json.put("quizlet_definition_id", m_quizletDefinitionId);
		
		return json.toString();
	}

	/**
	 * This is Quizlet specific deserialization
	 */
	public QuizletDefinition deserialize(JSONObject json) throws JSONException {

		m_word = json.getString("word");
		m_speechPart = json.getString("speech_part");
		m_definition = json.getString("definition");
		
		JSONArray array = json.getJSONArray("examples");
		m_examples = new ArrayList<String>();
		for (int i = 0; i < array.length(); i++) {
			m_examples.add(array.getString(i));
		}
		
		// Memoling specific
		
		if(json.has("is_official")) {
			m_isOfficial = json.optBoolean("is_official");
		}
		
		if(json.has("quizlet_definition_id")) {
			m_quizletDefinitionId = json.optString("quizlet_definition_id");
		} else {
			m_quizletDefinitionId = UUID.randomUUID().toString();
		}
		
		return this;
	}

}
