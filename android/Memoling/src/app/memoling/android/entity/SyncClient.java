package app.memoling.android.entity;

import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import app.memoling.android.facebook.FacebookUser;
import app.memoling.android.helper.Helper;

public class SyncClient {

	private String m_syncClientId;
	private String m_facebookUserId;
	private String m_description;
	private long m_lastSyncServerTimestamp;
	
	public String getSyncClientId() {  return m_syncClientId; }
	public void setSyncClientId(String syncClientId) { m_syncClientId = syncClientId; }

	public String getFacebookUserId() {  return m_facebookUserId; }
	public void setFacebookUserId(String facebookUserId) { m_facebookUserId = facebookUserId; }

	public String getDescription() {  return m_description; }
	public void setDescription(String description) { m_description = description; }
	
	public long getLastSyncServerTimestamp() {  return m_lastSyncServerTimestamp; }
	public void setLastSyncServerTimestamp(long lastSyncServerTimestamp) { m_lastSyncServerTimestamp = lastSyncServerTimestamp; }

	public String serialize() throws JSONException {
		JSONObject json = new JSONObject();

		json.put("m_syncClientId", m_syncClientId);
		json.put("m_facebookUserId", m_facebookUserId);
		json.put("m_description", m_description);
		json.put("m_lastSyncServerTimestamp", m_lastSyncServerTimestamp);

		return json.toString();
	}

	public SyncClient deserialize(JSONObject json) throws JSONException {

		m_syncClientId = json.getString("m_syncClientId");
		m_facebookUserId = json.getString("m_facebookUserId");
		m_description = json.getString("m_description");
		m_lastSyncServerTimestamp = json.getLong("m_lastSyncServerTimestamp");

		return this;
	}
	
	public String encode() throws JSONException {
		JSONObject json = new JSONObject();

		json.put("syncClientId", m_syncClientId);
		json.put("facebookUserId", m_facebookUserId);
		json.put("description", m_description);

		return json.toString();
	}

	public static SyncClient newSyncClient(FacebookUser user) {
		SyncClient client = new SyncClient();
		client.setFacebookUserId(user.getId());
		client.setLastSyncServerTimestamp(0);
		client.setDescription(Helper.getDeviceName());
		client.setSyncClientId(UUID.randomUUID().toString());
		
		return client;
	}
	
}
