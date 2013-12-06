package app.memoling.android.entity;

import org.json.JSONException;
import org.json.JSONObject;

public class WordOfTheDayWidget {

	private int m_widgetId;
	private String m_memoBaseId;
	
	public int getWidgetId() {  return m_widgetId; }
	public void setWidgetId(int widgetId) { m_widgetId = widgetId; }

	public String getMemoBaseId() {  return m_memoBaseId; }
	public void setMemoBaseId(String memoBaseId) { m_memoBaseId = memoBaseId; }

	public String serialize() throws JSONException {
		JSONObject json = new JSONObject();

		json.put("m_widgetId", m_widgetId);
		json.put("m_memoBaseId", m_memoBaseId);

		return json.toString();
	}

	public WordOfTheDayWidget deserialize(JSONObject json) throws JSONException {

		m_widgetId = json.getInt("m_widgetId");
		m_memoBaseId = json.getString("m_memoBaseId");

		return this;
	}

}
