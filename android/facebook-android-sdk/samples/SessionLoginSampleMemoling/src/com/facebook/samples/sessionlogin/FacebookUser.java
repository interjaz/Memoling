package com.facebook.samples.sessionlogin;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.model.GraphUser;

public class FacebookUser {
	
	private String m_id;
	private String m_name;
	private String m_firstName;
	private String m_lastName;
	private String m_link;
	private String m_username;
	private FacebookLocation m_hometown;
	private FacebookLocation m_location;
	// Not used
	// private String m_education;
	private String m_gender;
	private String m_timezone;
	private String m_locale;
	private boolean m_verified;
	private String m_updatedTime;

	public FacebookUser() {
		
	}
	
	public FacebookUser(GraphUser user) throws JSONException {
		facebookDeserialize(user.getInnerJSONObject());
	}
	
	public String getId() {
		return "01_test";
		//return m_id;
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

	public String getFirstName() {
		return m_firstName;
	}

	public void setFirstName(String firstName) {
		m_firstName = firstName;
	}

	public String getLastName() {
		return m_lastName;
	}

	public void setLastName(String lastName) {
		m_lastName = lastName;
	}

	public String getLink() {
		return m_link;
	}

	public void setLink(String link) {
		m_link = link;
	}

	public String getUsername() {
		return m_username;
	}

	public void setUsername(String username) {
		m_username = username;
	}

	public FacebookLocation getHometown() {
		return m_hometown;
	}

	public void setHometown(FacebookLocation hometown) {
		m_hometown = hometown;
	}

	public FacebookLocation getLocation() {
		return m_location;
	}

	public void setLocation(FacebookLocation location) {
		m_location = location;
	}

	public String getGender() {
		return m_gender;
	}

	public void setGender(String gender) {
		m_gender = gender;
	}

	public String getTimezone() {
		return m_timezone;
	}

	public void setTimezone(String timezone) {
		m_timezone = timezone;
	}

	public String getLocale() {
		return m_locale;
	}

	public void setLocale(String locale) {
		m_locale = locale;
	}

	public boolean getVerified() {
		return m_verified;
	}

	public void setVerified(boolean verified) {
		m_verified = verified;
	}

	public String getUpdatedTime() {
		return m_updatedTime;
	}

	public void setUpdatedTime(String updatedTime) {
		m_updatedTime = updatedTime;
	}

	public String serialize() throws JSONException {
		JSONObject json = new JSONObject();

		json.put("m_id", m_id);
		json.put("m_name", m_name);
		json.put("m_firstName", m_firstName);
		json.put("m_lastName", m_lastName);
		json.put("m_link", m_link);
		json.put("m_username", m_username);
		json.put("m_hometown", m_hometown.serialize());
		json.put("m_location", m_location.serialize());
		json.put("m_gender", m_gender);
		json.put("m_timezone", m_timezone);
		json.put("m_locale", m_locale);
		json.put("m_verified", m_verified);
		json.put("m_updatedTime", m_updatedTime);

		return json.toString();
	}

	public FacebookUser deserialize(JSONObject json) throws JSONException {

		m_id = json.getString("m_id");
		m_name = json.getString("m_name");
		m_firstName = json.getString("m_firstName");
		m_lastName = json.getString("m_lastName");
		m_link = json.getString("m_link");
		m_username = json.getString("m_username");
		FacebookLocation hometown = new FacebookLocation();
		hometown.deserialize(json.getJSONObject("m_hometown"));
		m_hometown = hometown;
		FacebookLocation location = new FacebookLocation();
		location.deserialize(json.getJSONObject("m_location"));
		m_location = location;
		m_gender = json.getString("m_gender");
		m_timezone = json.getString("m_timezone");
		m_locale = json.getString("m_locale");
		m_verified = json.getBoolean("m_verified");
		m_updatedTime = json.getString("m_updatedTime");

		return this;
	}
	
	public FacebookUser facebookDeserialize(JSONObject json) throws JSONException {

		m_id = json.getString("id");
		m_name = json.getString("name");
		m_firstName = json.getString("first_name");
		m_lastName = json.getString("last_name");
		m_link = json.getString("link");
		m_username = json.getString("username");
		FacebookLocation hometown = new FacebookLocation();
		hometown.facebookDeserialize(json.getJSONObject("hometown"));
		m_hometown = hometown;
		FacebookLocation location = new FacebookLocation();
		location.facebookDeserialize(json.getJSONObject("location"));
		m_location = location;
		m_gender = json.getString("gender");
		m_timezone = json.getString("timezone");
		m_locale = json.getString("locale");
		m_verified = json.getBoolean("verified");
		m_updatedTime = json.getString("updated_time");

		return this;
	}

	
}
