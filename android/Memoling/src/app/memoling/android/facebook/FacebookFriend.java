package app.memoling.android.facebook;

import org.json.JSONException;
import org.json.JSONObject;

public class FacebookFriend {
	private String m_id;
	private String m_name;
	private String m_picSquare;

	public String getId() {
		return m_id;
	}

	public void setId(String id) {
		m_id = id;
	}

	public String getName() {
		return m_name;
	}

	public void setName(String name) {
		m_name = name;
	}

	public String getPicSquare() {
		return m_picSquare;
	}

	public void setPicSquare(String picSquare) {
		m_picSquare = picSquare;
	}

	/**
	 * Facebook serialization
	 */
	public String serialize() throws JSONException {
		JSONObject json = new JSONObject();

		json.put("uid", m_id);
		json.put("name", m_name);
		json.put("pic_square", m_picSquare);

		return json.toString();
	}

	/**
	 * Facebook deserialization
	 */
	public FacebookFriend deserialize(JSONObject json) throws JSONException {

		m_id = json.getString("uid");
		m_name = json.getString("name");
		m_picSquare = json.optString("pic_square");

		return this;
	}

}