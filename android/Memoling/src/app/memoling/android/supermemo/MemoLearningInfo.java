package app.memoling.android.supermemo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import app.memoling.android.sync.cloud.ISyncEntity;

public class MemoLearningInfo implements ISyncEntity {

	// Database columns
	
	// primary key - memoLearningInfoId
	private String memoLearningInfoId;
	
	// database column: id
	// external key - memoId
	private String memoId;
	
	// database column: ord
	private int order;
	
	// database column: type
	// 0 - not learned
	// 1 - repeated
	// 2 - forgotten and again repeated
	private int type;
	
	// database column: queue
	private int queue;
	
	// database column: due
	private int due;
	
	// database column: ivl
	// -60 stands for 1 minute
	// -600 stands for 10 minutes
	// positive values stands for days
	private int interval;
	
	// database column: factor
	private int difficulty;
	
	// database column: reps
	private int numberAllAnswers;
	
	// database column: lapses
	private int numberWrongAnswers;
	
	// database column: left
	private int left;
	
	// database column: odue
	private int odue;
	
	// database column: odid
	private int odid;
	
	// database column: flags
	private int flags;
	
	// database column: data
	private String data;
	
	public MemoLearningInfo(String memoId) {
		this.memoLearningInfoId = UUID.randomUUID().toString();
		this.memoId = memoId;
	}
	
	public MemoLearningInfo() {}

	public String getMemoLearningInfoId() {
		return memoLearningInfoId;
	}

	public void setMemoLearningInfoId(String memoLearningInfoId) {
		this.memoLearningInfoId = memoLearningInfoId;
	}

	public String getMemoId() {
		return memoId;
	}

	public void setMemoId(String memoId) {
		this.memoId = memoId;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getQueue() {
		return queue;
	}

	public void setQueue(int queue) {
		this.queue = queue;
	}

	public int getDue() {
		return due;
	}

	public void setDue(int due) {
		this.due = due;
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public int getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
	}

	public int getNumberAllAnswers() {
		return numberAllAnswers;
	}

	public void setNumberAllAnswers(int numberAllAnswers) {
		this.numberAllAnswers = numberAllAnswers;
	}

	public int getNumberWrongAnswers() {
		return numberWrongAnswers;
	}

	public void setNumberWrongAnswers(int numberWrongAnswers) {
		this.numberWrongAnswers = numberWrongAnswers;
	}

	public int getLeft() {
		return left;
	}

	public void setLeft(int left) {
		this.left = left;
	}

	public int getOdue() {
		return odue;
	}

	public void setOdue(int odue) {
		this.odue = odue;
	}

	public int getOdid() {
		return odid;
	}

	public void setOdid(int odid) {
		this.odid = odid;
	}

	public int getFlags() {
		return flags;
	}

	public void setFlags(int flags) {
		this.flags = flags;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
	
	public static JSONArray serializeList(List<MemoLearningInfo> memoLearningInfos) throws JSONException {
		JSONArray array = new JSONArray();
		
		if(memoLearningInfos == null) {
			return array;
		}
		
		for(MemoLearningInfo memoLearningInfo : memoLearningInfos) {
			array.put(memoLearningInfo.serialize());
		}
		
		return array;
	}
	
	public static List<MemoLearningInfo> deserializeList(JSONArray memoLearningInfos) throws JSONException {
		List<MemoLearningInfo> array = new ArrayList<MemoLearningInfo>();
		
		if(memoLearningInfos == null) {
			return array;
		}
		
		for(int i=0;i<memoLearningInfos.length();i++) {
			array.add(new MemoLearningInfo().deserialize(memoLearningInfos.getJSONObject(i)));
		}
		
		return array;
	}
	
	public JSONObject serialize() throws JSONException {
		JSONObject json = new JSONObject();
		
		json.put("memoLearningInfoId", memoLearningInfoId);
		json.put("memoId", memoId);
		json.put("order", order);
		json.put("type", type);	
		json.put("queue", queue);	
		json.put("due", due);	
		json.put("interval", interval);	
		json.put("difficulty", difficulty);	
		json.put("numberAllAnswers", numberAllAnswers);	
		json.put("numberWrongAnswers", numberWrongAnswers);	
		json.put("left", left);	
		json.put("odue", odue);	
		json.put("odid", odid);	
		json.put("flags", flags);	
		json.put("data", data);	
	
		return json;
	}
	
	public MemoLearningInfo deserialize(JSONObject json) throws JSONException {
		memoLearningInfoId = json.getString("memoLearningInfoId");
		memoId = json.getString("memoId");
		order = json.getInt("order");
		type = json.getInt("type");
		queue = json.getInt("queue");
		due = json.getInt("due");
		interval = json.getInt("interval");
		difficulty = json.getInt("difficulty");
		numberAllAnswers = json.getInt("numberAllAnswers");
		numberWrongAnswers = json.getInt("numberWrongAnswers");
		left = json.getInt("left");
		odue = json.getInt("odue");
		odid = json.getInt("odid");
		flags = json.getInt("flags");
		data = json.getString("data");
		
		return this;
	}
	
	@Override
	public JSONObject encodeEntity() throws JSONException {
		JSONObject json = new JSONObject();
		
		json.put("memoLearningInfoId", memoLearningInfoId);
		json.put("memoId", memoId);
		json.put("order", order);	
		json.put("type", type);	
		json.put("queue", queue);	
		json.put("due", due);	
		json.put("interval", interval);	
		json.put("difficulty", difficulty);	
		json.put("numberAllAnswers", numberAllAnswers);	
		json.put("numberWrongAnswers", numberWrongAnswers);	
		json.put("left", left);	
		json.put("odue", odue);	
		json.put("odid", odid);	
		json.put("flags", flags);	
		json.put("data", data);	
		
		return json;
	}

	@Override
	public ISyncEntity decodeEntity(JSONObject json) throws JSONException {
		memoLearningInfoId = json.getString("memoLearningInfoId");
		memoId = json.getString("memoId");
		order = json.getInt("order");
		type = json.getInt("type");
		queue = json.getInt("queue");
		due = json.getInt("due");
		interval = json.getInt("interval");
		difficulty = json.getInt("difficulty");
		numberAllAnswers = json.getInt("numberAllAnswers");
		numberWrongAnswers = json.getInt("numberWrongAnswers");
		left = json.getInt("left");
		odue = json.getInt("odue");
		odid = json.getInt("odid");
		flags = json.getInt("flags");
		data = json.getString("data");
		
		return this;
	}
}