package app.memoling.android.entity;

import org.json.JSONException;
import org.json.JSONObject;

public class WiktionaryInfo {

	private String m_wiktionaryInfoId;
	private String m_name;
	private String m_description;
	private Language m_language;
	private int m_version;
	private String m_downloadUrl;
	private long m_downloadSize;
	private long m_realSize;

	public String getWiktionaryInfoId() {
		return m_wiktionaryInfoId;
	}

	public void setWiktionaryInfoId(String wiktionaryInfoId) {
		m_wiktionaryInfoId = wiktionaryInfoId;
	}

	public String getName() {
		return m_name;
	}

	public void setName(String name) {
		m_name = name;
	}

	public String getDescription() {
		return m_description;
	}

	public void setDescription(String description) {
		m_description = description;
	}

	public Language getLanguage() {
		return m_language;
	}

	public void setLanguage(Language language) {
		m_language = language;
	}

	public int getVersion() {
		return m_version;
	}

	public void setVersion(int version) {
		m_version = version;
	}

	public String getDownloadUrl() {
		return m_downloadUrl;
	}

	public void setDownloadUrl(String donwloadUrl) {
		m_downloadUrl = donwloadUrl;
	}

	public long getDownloadSize() {
		return m_downloadSize;
	}

	public void setDownloadSize(long downloadSize) {
		m_downloadSize = downloadSize;
	}

	public long getRealSize() {
		return m_realSize;
	}

	public void setRealSize(long realSize) {
		m_realSize = realSize;
	}

	public String serialize() throws JSONException {
		JSONObject json = new JSONObject();

		json.put("m_wiktionaryInfoId", m_wiktionaryInfoId);
		json.put("m_name", m_name);
		json.put("m_description", m_description);
		json.put("m_language", m_language.getCode());
		json.put("m_version", m_version);
		json.put("m_donwloadUrl", m_downloadUrl);
		json.put("m_downloadSize", m_downloadSize);
		json.put("m_realSize", m_realSize);

		return json.toString();
	}

	public WiktionaryInfo deserialize(JSONObject json) throws JSONException {

		m_wiktionaryInfoId = json.getString("m_wiktionaryInfoId");
		m_name = json.getString("m_name");
		m_description = json.getString("m_description");
		m_language = Language.parse(json.getString("m_language"));
		m_version = json.getInt("m_version");
		m_downloadUrl = json.getString("m_donwloadUrl");
		m_downloadSize = json.getLong("m_downloadSize");
		m_realSize = json.getLong("m_realSize");

		return this;
	}

}
