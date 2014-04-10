package app.memoling.android.entity;

import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import app.memoling.android.sync.cloud.ISyncAdapter;
import app.memoling.android.sync.cloud.ISyncEntity;
import app.memoling.android.sync.cloud.SyncPackage;

public class SyncAction {

	public final static int ACTION_DELETE = -1;
	public final static int ACTION_UPDATE = 0;
	public final static int ACTION_INSERT = 1;
	
	private String m_syncActionId;
	private String m_syncClientId;
	private String m_table;
	private String m_primaryKey;
	private int m_action;
	private long m_serverTimestamp;
	private String m_updateColumn;
	private String m_syncObjectId;
	
	private SyncPackage m_syncPackageContext;
	
	public String getSyncActionId() {  return m_syncActionId; }
	public void setSyncActionId(String syncActionId) { m_syncActionId = syncActionId; }

	public String getSyncClientId() {  return m_syncClientId; }
	public void setSyncClientId(String syncClientId) { m_syncClientId = syncClientId; }

	public String getTable() {  return m_table; }
	public void setTable(String table) { m_table = table; }

	public String getPrimaryKey() {  return m_primaryKey; }
	public void setPrimaryKey(String primaryKey) { m_primaryKey = primaryKey; }

	public int getAction() {  return m_action; }
	public void setAction(int action) { m_action = action; }

	public long getServerTimestamp() {  return m_serverTimestamp; }
	public void setServerTimestamp(long serverTimestamp) { m_serverTimestamp = serverTimestamp; }

	public String getUpdateColumn() {  return m_updateColumn; }
	public void setUpdateColumn(String updateColumn) { m_updateColumn = updateColumn; }

	public String getSyncObjectId() {  return m_syncObjectId; }
	public void setSyncObjectId(String syncObjectId) { m_syncObjectId = syncObjectId; }

	public void setSyncPackageContext(SyncPackage syncPackage) { m_syncPackageContext = syncPackage; }
	
	public SyncAction() {
		m_syncActionId = UUID.randomUUID().toString();
		m_serverTimestamp = System.currentTimeMillis()/1000L;
	}
	
	public String serialize() throws JSONException {
		JSONObject json = new JSONObject();

		json.put("m_syncActionId", m_syncActionId);
		json.put("m_syncClientId", m_syncClientId);
		json.put("m_table", m_table);
		json.put("m_primaryKey", m_primaryKey);
		json.put("m_action", m_action);
		json.put("m_serverTimestamp", m_serverTimestamp);
		json.put("m_updateColumn", m_updateColumn);
		json.put("m_syncObjectId", m_syncObjectId);

		return json.toString();
	}

	public SyncAction deserialize(JSONObject json) throws JSONException {

		m_syncActionId = json.getString("m_syncActionId");
		m_syncClientId = json.getString("m_syncClientId");
		m_table = json.getString("m_table");
		m_primaryKey = json.getString("m_primaryKey");
		m_action = json.getInt("m_action");
		m_serverTimestamp = json.getLong("m_serverTimestamp");
		m_updateColumn = json.getString("m_updateColumn");
		m_syncObjectId = json.getString("m_syncObjectId");

		return this;
	}

	/**
	 * Normalized version
	 * @return
	 * @throws JSONException
	 */
	public JSONObject encode() throws JSONException {
		JSONObject json = new JSONObject();

		// This one is redundant (it is already in package)
		//json.put("syncClientId", m_syncClientId);
		
		json.put("syncActionId", m_syncActionId);
		json.put("table", m_table);
		json.put("primaryKey", m_primaryKey);
		json.put("action", m_action);
		json.put("serverTimestamp", m_serverTimestamp);
		json.put("updateColumn", m_updateColumn);
		json.put("syncObjectId", m_syncObjectId);

		return json;
	}
	
	public SyncAction decode(JSONObject json) throws JSONException {

		// This one is redundant (it is already in package)
		// m_syncClientId = json.getString("syncClientId");
		m_syncClientId = m_syncPackageContext.getSyncClientId();
		
		m_syncActionId = json.getString("syncActionId");
		m_table = json.getString("table");
		m_primaryKey = json.getString("primaryKey");
		m_action = json.getInt("action");
		m_serverTimestamp = json.getLong("serverTimestamp");
		m_updateColumn = json.getString("updateColumn");
		m_syncObjectId = json.getString("syncObjectId");

		return this;
	}
	
	public ISyncEntity getServerObject(ISyncAdapter adapter) throws JSONException {
		JSONObject encodedObject = m_syncPackageContext.getServerObjects().get(m_syncObjectId);
		ISyncEntity entity = adapter.decodeEntity(encodedObject);
		return entity;
	}
}
