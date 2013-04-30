package com.interjaz.facebook;

import org.json.JSONException;
import org.json.JSONObject;

public class FacebookLocation {
	private String m_id;
	private String m_name;
	public String getId() {  return m_id; }
	public void setId(String id) { m_id = id; }

	public String getName() {  return m_name; }
	public void setName(String name) { m_name = name; }

	public String serialize() throws JSONException {
		JSONObject json = new JSONObject();

		json.put("m_id", m_id);
		json.put("m_name", m_name);

		return json.toString();
	}

	public FacebookLocation deserialize(String serialized) throws JSONException {
		JSONObject json = new JSONObject(serialized);
		FacebookLocation object = new FacebookLocation();

		object.m_id = json.getString("m_id");
		object.m_name = json.getString("m_name");

		return object;
	}

}
