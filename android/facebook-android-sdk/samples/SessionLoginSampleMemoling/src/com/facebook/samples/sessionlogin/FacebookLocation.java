package com.facebook.samples.sessionlogin;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.model.GraphLocation;

public class FacebookLocation {
	private String m_id;
	private String m_name;

	public FacebookLocation() {
		
	}
	
	public FacebookLocation(GraphLocation location) throws JSONException {
		facebookDeserialize(location.getInnerJSONObject());
	}
	
	public String getId() {  return m_id; }
	public void setId(String id) { m_id = id; }

	public String getName() {  return m_name; }
	public void setName(String name) { m_name = name; }

	public JSONObject serialize() throws JSONException {
		JSONObject json = new JSONObject();

		json.put("m_id", m_id);
		json.put("m_name", m_name);

		return json;
	}

	public FacebookLocation deserialize(JSONObject json) throws JSONException {

		this.m_id = json.getString("m_id");
		this.m_name = json.getString("m_name");

		return this;
	}
	
	public FacebookLocation facebookDeserialize(JSONObject json) throws JSONException {

		this.m_id = json.getString("id");
		this.m_name = json.getString("name");

		return this;
	}

}
