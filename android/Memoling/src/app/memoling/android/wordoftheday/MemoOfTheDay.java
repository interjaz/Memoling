package app.memoling.android.wordoftheday;

import org.json.JSONException;
import org.json.JSONObject;

import app.memoling.android.entity.Memo;
import app.memoling.android.entity.Word;

public class MemoOfTheDay extends Memo {

	public String m_descriptionFrom;
	public String m_descriptionTo;
	public int m_wordOfTheDayId;

	public String getDescriptionFrom() {
		return m_descriptionFrom;
	}

	public void setDescriptionFrom(String description) {
		m_descriptionFrom = description;
	}

	public String getDescriptionTo() {
		return m_descriptionTo;
	}

	public void setDescriptionTo(String description) {
		m_descriptionTo = description;
	}


	public int getWordOfTheDayId() {
		return m_wordOfTheDayId;
	}

	public void setWordOfTheDayId(int wordOfTheDayId) {
		m_wordOfTheDayId = wordOfTheDayId;
	}
	
	public MemoOfTheDay() {
	}
	
	public MemoOfTheDay(Word wordA, Word wordB, String memoBaseId) {
		super(wordA, wordB, memoBaseId);		
	}
	
	public JSONObject serialize() throws JSONException {
		JSONObject json = new JSONObject();

		json.put("m_descriptionFrom", m_descriptionFrom);
		json.put("m_descriptionTo", m_descriptionTo);
		json.put("m_wordOfTheDayId", m_wordOfTheDayId);
		json.put("super", super.serialize());

		return json;
	}

	public MemoOfTheDay deserialize(JSONObject json) throws JSONException {

		super.deserialize(json.getJSONObject("super"));
		m_descriptionFrom = json.getString("m_descriptionFrom");
		m_descriptionTo = json.getString("m_descriptionTo");
		m_wordOfTheDayId = json.getInt("m_wordOfTheDayId");

		return this;
	}
}
