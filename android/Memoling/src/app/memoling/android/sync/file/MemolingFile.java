package app.memoling.android.sync.file;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import app.memoling.android.entity.MemoBase;

public class MemolingFile {

	private String m_version = "1.0";

	public String getVersion() {
		return m_version;
	}

	public void setVersion(String version) {
		m_version = version;
	}

	private ArrayList<MemoBase> m_memoBases;

	public ArrayList<MemoBase> getMemoBases() {
		return m_memoBases;
	}

	public void setMemoBases(ArrayList<MemoBase> memoBases) {
		m_memoBases = memoBases;
	}

	public JSONObject serialize() throws JSONException {
		JSONObject json = new JSONObject();
		JSONArray array = new JSONArray();

		json.put("m_version", m_version);

		for (MemoBase lib : m_memoBases) {
			array.put(lib.serialize());
		}

		json.put("m_memoBases", array);

		return json;
	}

	public MemolingFile deserialize(JSONObject json) throws JSONException {
		m_memoBases = new ArrayList<MemoBase>();

		m_version = json.getString("m_version");
		JSONArray array = json.getJSONArray("m_memoBases");

		for (int i = 0; i < array.length(); i++) {
			m_memoBases.add(new MemoBase().deserialize(array.getJSONObject(i)));
		}

		return this;
	}

	public static MemolingFile parseFile(String path) throws IOException, JSONException {

		FileReader reader = new FileReader(path);
		StringBuilder sb = new StringBuilder();

		char[] buffer = new char[256];
		int read = 0;
		while ((read = reader.read(buffer, 0, buffer.length)) != -1) {
			sb.append(buffer, 0, read);
		}

		MemolingFile memolingFile = new MemolingFile().deserialize(new JSONObject(sb.toString()));

		reader.close();

		return memolingFile;
	}
}