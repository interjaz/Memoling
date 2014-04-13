package app.memoling.android.sync.cloud;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import app.memoling.android.entity.SyncAction;

public class SyncPackage {
	
	private long m_serverTimestamp;
	private String m_syncClientId;
	private List<SyncAction> m_syncActions;
	private Map<String,JSONObject> m_clientObjects;
	private Map<String,JSONObject> m_serverObjects;
	
	public long getServerTimestamp() {  return m_serverTimestamp; }
	public void setServerTimestamp(long serverTimestamp) { m_serverTimestamp = serverTimestamp; }

	public String getSyncClientId() {  return m_syncClientId; }
	public void setSyncClientId(String syncClientId) { m_syncClientId = syncClientId; }

	public List<SyncAction> getSyncActions() {  return m_syncActions; }
	public void setSyncActions(List<SyncAction> syncActions) { m_syncActions = syncActions; }
	
	public Map<String,JSONObject> getClientObjects() {  return m_clientObjects; }
	public void setClientObjects(HashMap<String,JSONObject> clientObjects) { m_clientObjects = clientObjects; }

	public Map<String,JSONObject> getServerObjects() {  return m_serverObjects; }
	public void setServerObjects(HashMap<String,JSONObject> serverObjects) { m_serverObjects = serverObjects; }

	public SyncPackage() {
		m_serverTimestamp = System.currentTimeMillis()/1000L;
	}
	
	public String serialize() throws JSONException {
		JSONObject json = new JSONObject();

		JSONArray array;
		json.put("m_serverTimestamp", m_serverTimestamp);
		json.put("m_syncClientId", m_syncClientId);
		
		array = new JSONArray();
		for(SyncAction action : m_syncActions) {
			array.put(action.serialize());
		}
		json.put("m_syncActions", array);

		return json.toString();
	}

	public SyncPackage deserialize(JSONObject json) throws JSONException {

		m_serverTimestamp = json.getLong("m_serverTimestamp");
		m_syncClientId = json.getString("m_syncClientId");

		JSONArray array = json.getJSONArray("m_syncActions");
		ArrayList<SyncAction> actions = new ArrayList<SyncAction>();
		for(int i=0;i<array.length();i++) {
			SyncAction action = new SyncAction();
			action.deserialize(array.getJSONObject(i));
			actions.add(action);
		}

		return this;
	}

	/**
	 * Normalized version
	 * @return
	 * @throws JSONException
	 */
	public String encode() throws JSONException {
		JSONObject json = new JSONObject();

		JSONArray array;
		json.put("serverTimestamp", m_serverTimestamp);
		json.put("syncClientId", m_syncClientId);
		
		array = new JSONArray();
		for(SyncAction action : m_syncActions) {
			array.put(action.encode());
		}
		json.put("syncActions", array);
		json.put("syncObjects", new JSONObject(m_clientObjects));

		return json.toString();
	}

	/**
	 * Normalized version
	 * @param json
	 * @return
	 * @throws JSONException
	 */
	public SyncPackage decode(JSONObject json) throws JSONException {

		m_serverTimestamp = json.getLong("serverTimestamp");
		m_syncClientId = json.getString("syncClientId");

		JSONArray array = json.getJSONArray("syncActions");
		ArrayList<SyncAction> actions = new ArrayList<SyncAction>();
		for(int i=0;i<array.length();i++) {
			SyncAction action = new SyncAction();
			action.setSyncPackageContext(this);
			action.decode(array.getJSONObject(i));
			actions.add(action);
		}
		m_syncActions = actions;
		
		JSONObject syncObjects = json.getJSONObject("syncObjects");
		HashMap<String,JSONObject> serverObjects = new HashMap<String,JSONObject>();
		@SuppressWarnings("unchecked")
		Iterator<String> keys = syncObjects.keys();
		while(keys.hasNext()) {
			String key = keys.next();
			serverObjects.put(key, syncObjects.getJSONObject(key));
		}
		
		m_serverObjects = serverObjects;

		return this;
	}
}
