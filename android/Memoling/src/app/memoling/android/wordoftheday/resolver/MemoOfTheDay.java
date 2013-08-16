package app.memoling.android.wordoftheday.resolver;

import org.json.JSONException;
import org.json.JSONObject;

import app.memoling.android.entity.Memo;
import app.memoling.android.entity.Word;

public class MemoOfTheDay extends Memo {

	private int m_providerId;
	
	public int getProviderId() {
		return m_providerId;
	}
	
	public void setProviderId(int providerId) {
		m_providerId = providerId;
	}

	public MemoOfTheDay() {
	}
	
	public MemoOfTheDay(Word wordA, Word wordB, String memoId) {
		super(wordA,wordB,memoId);
	}
	
	@Override
	public JSONObject serialize() throws JSONException {
		JSONObject object = super.serialize();
		object.put("m_providerId", m_providerId);
		return object;
	}

	@Override
	public MemoOfTheDay deserialize(JSONObject json) throws JSONException {
		m_providerId = json.getInt("m_providerId");
		super.deserialize(json);
		return this;
	}
	
	
	
}
