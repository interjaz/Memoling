package com.interjaz.facebook;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.interjaz.helper.Preferences;

public class FacebookUser {

	private String m_id;
	private String m_name;
	private String m_firstName;
	private String m_lastName;
	private String m_link;
	private String m_username;
	private String m_hometown;
	private FacebookLocation m_location;
	// Not used
	// private String m_education;
	private String m_gender;
	private String m_timezone;
	private String m_locale;
	private String m_verified;
	private String m_updatedTime;

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

	public String getHometown() {
		return m_hometown;
	}

	public void setHometown(String hometown) {
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

	public String getVerified() {
		return m_verified;
	}

	public void setVerified(String verified) {
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
		json.put("m_hometown", m_hometown);
		json.put("m_location", m_location.serialize());
		json.put("m_gender", m_gender);
		json.put("m_timezone", m_timezone);
		json.put("m_locale", m_locale);
		json.put("m_verified", m_verified);
		json.put("m_updatedTime", m_updatedTime);

		return json.toString();
	}

	public FacebookUser deserialize(String serialized) throws JSONException {
		JSONObject json = new JSONObject(serialized);
		FacebookUser object = new FacebookUser();

		object.m_id = json.getString("m_id");
		object.m_name = json.getString("m_name");
		object.m_firstName = json.getString("m_firstName");
		object.m_lastName = json.getString("m_lastName");
		object.m_link = json.getString("m_link");
		object.m_username = json.getString("m_username");
		object.m_hometown = json.getString("m_hometown");
		FacebookLocation location = new FacebookLocation();
		location.deserialize(json.getString("m_location"));
		object.m_location = location;
		object.m_gender = json.getString("m_gender");
		object.m_timezone = json.getString("m_timezone");
		object.m_locale = json.getString("m_locale");
		object.m_verified = json.getString("m_verified");
		object.m_updatedTime = json.getString("m_updatedTime");

		return object;
	}

	private static FacebookUser m_user;
	private final static String FacebookUser = "FacebookUser";

	public static FacebookUser read(Context context) {
		if (m_user != null) {
			return m_user;
		}

		Preferences preferences = new Preferences(context);
		String json = preferences.get(FacebookUser);

		try {
			m_user = new FacebookUser();
			m_user.deserialize(json);
		} catch (JSONException ex) {
			m_user = null;
		}

		return m_user;
	}
	
	public static FacebookUser read() {
		return m_user;
	}
	
	public static void save(FacebookUser user, Context context) {
		try {
			Preferences preferences = new Preferences(context);
			preferences.set(FacebookUser, user.serialize());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
