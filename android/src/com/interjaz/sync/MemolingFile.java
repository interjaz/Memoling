package com.interjaz.sync;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MemolingFile {

	public String version = "1.0";

	public ArrayList<Library> libraries;

	public JSONObject serialize() throws JSONException {
		JSONObject json = new JSONObject();
		JSONArray array = new JSONArray();

		json.put("version", version);

		for (Library lib : libraries) {
			array.put(lib.serialize());
		}

		json.put("libraries", array);

		return json;
	}

	public MemolingFile deserialize(JSONObject json) throws JSONException {
		libraries = new ArrayList<Library>();

		version = json.getString("version");
		JSONArray array = json.getJSONArray("libraries");

		for (int i = 0; i < array.length(); i++) {
			libraries.add(new Library().deserialize(array.getJSONObject(i)));
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