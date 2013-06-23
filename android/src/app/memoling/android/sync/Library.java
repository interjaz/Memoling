package app.memoling.android.sync;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import app.memoling.android.entity.Memo;
import app.memoling.android.entity.MemoBase;

public class Library extends MemoBase {
	public ArrayList<Memo> memos;

	public Library() {

	}

	public Library(MemoBase base) {
		super.setActive(base.getActive());
		super.setCreated(base.getCreated());
		super.setMemoBaseId(base.getMemoBaseId());
		super.setName(base.getName());
	}

	public JSONObject serialize() throws JSONException {
		JSONObject json = new JSONObject();
		JSONArray array = new JSONArray();

		json.put("memoBase", super.serialize());

		for (Memo memo : memos) {
			array.put(memo.serialize());
		}

		json.put("memos", array);

		return json;
	}

	public Library deserialize(JSONObject json) throws JSONException {

		super.deserialize(json.getJSONObject("memoBase"));
		JSONArray array = json.getJSONArray("memos");
		for (int i = 0; i < array.length(); i++) {
			memos.add(new Memo().deserialize(array.getJSONObject(i)));
		}

		return this;
	}

}