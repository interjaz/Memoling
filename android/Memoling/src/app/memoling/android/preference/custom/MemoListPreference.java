package app.memoling.android.preference.custom;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MemoListPreference {

	private String m_memoBaseId;
	private String m_languageFromCode;
	private String m_languageToCode;

	public String getMemoBaseId() {
		return m_memoBaseId;
	}

	public void setMemoBaseId(String memoBaseId) {
		m_memoBaseId = memoBaseId;
	}

	public String getLanguageFromCode() {
		return m_languageFromCode;
	}

	public void setLanguageFromCode(String languageFromCode) {
		m_languageFromCode = languageFromCode;
	}

	public String getLanguageToCode() {
		return m_languageToCode;
	}

	public void setLanguageToCode(String languageToCode) {
		m_languageToCode = languageToCode;
	}

	public String serialize() throws JSONException {
		JSONObject json = new JSONObject();

		json.put("m_memoBaseId", m_memoBaseId);
		json.put("m_languageFromCode", m_languageFromCode);
		json.put("m_languageToCode", m_languageToCode);

		return json.toString();
	}

	public MemoListPreference deserialize(JSONObject json) throws JSONException {

		m_memoBaseId = json.getString("m_memoBaseId");
		m_languageFromCode = json.getString("m_languageFromCode");
		m_languageToCode = json.getString("m_languageToCode");

		return this;
	}

	public static String serializeList(ArrayList<MemoListPreference> object) throws JSONException {
		JSONArray array = new JSONArray();

		for (MemoListPreference preference : object) {
			array.put(preference.serialize());
		}

		return array.toString();
	}

	public static ArrayList<MemoListPreference> deserializeList(String object) throws JSONException {
		ArrayList<MemoListPreference> data = new ArrayList<MemoListPreference>();

		if (object == null) {
			return data;
		}

		JSONArray array = new JSONArray(object);
		for (int i = 0; i < array.length(); i++) {
			data.add(new MemoListPreference().deserialize(new JSONObject(array.getString(i))));
		}

		return data;
	}
}
