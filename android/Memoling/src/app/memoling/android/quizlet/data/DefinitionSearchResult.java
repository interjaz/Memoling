package app.memoling.android.quizlet.data;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import app.memoling.android.entity.QuizletDefinition;

public class DefinitionSearchResult {

	private ArrayList<QuizletDefinition> m_definitions;

	public ArrayList<QuizletDefinition> getDefinitions() {
		return m_definitions;
	}
	
	/**
	 * This is Quizlet specific deserialization
	 */
	public DefinitionSearchResult deserialize(JSONObject json) throws JSONException {
		m_definitions = new ArrayList<QuizletDefinition>();
		
		JSONArray offical = json.optJSONArray("official_definitions");
		if(offical != null) {
			for(int i=0;i<offical.length();i++) {
				JSONObject object = offical.getJSONObject(i);
				m_definitions.add(new QuizletDefinition(true).deserialize(object));
			}
		}
		
		JSONArray user = json.optJSONArray("user_definitions");
		if(user != null) {
			for(int i=0;i<user.length();i++) {
				JSONObject object = user.getJSONObject(i);
				m_definitions.add(new QuizletDefinition(false).deserialize(object));
			}
		}
		
		return this;
	}
}
