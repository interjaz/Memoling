package app.memoling.android.supermemo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import app.memoling.android.sync.cloud.ISyncEntity;

public class MemoReviewLog implements ISyncEntity {

	// Database columns
	
	// primary key: memoReviewLogId
	private String memoReviewLogId;
	
	// database column: cid
	// external key: memoId
	private String memoId;
	
	// database column: ease
	//	5 - perfect response
	//	4 - correct response after a hesitation
	//	3 - correct response recalled with serious difficulty
	//	2 - incorrect response; where the correct one seemed easy to recall
	//	1 - incorrect response; the correct one remembered
	//	0 - complete blackout.
	private int responseResult;
	
	// database column: ivl
	// new interval
	private int newInterval;
	
	// database column: lastIvl
	// old interval taken into computation of new interval
	private int oldInterval;
	
	// database column: factor
	// factor of difficulty
	private int difficultyFactor;
	
	// database column: time
	// amount of time user was thinking about the answer
	private int responseTime;
	
	// database column: type
	// type of card that was reviewed
	// 0 - not learned
	// 1 - repeated
	// 2 - forgotten and again repeated
	private int type;
	
	public MemoReviewLog(String memoId) {
		this.memoReviewLogId = UUID.randomUUID().toString();
		this.memoId = memoId;
	}
	
	public MemoReviewLog() {}

	public String getMemoReviewLogId() {
		return memoReviewLogId;
	}
	public void setMemoReviewLogId(String memoReviewLogId) {
		this.memoReviewLogId = memoReviewLogId;
	}
	public String getMemoId() {
		return memoId;
	}
	public void setMemoId(String memoId) {
		this.memoId = memoId;
	}
	public int getResponseResult() {
		return responseResult;
	}
	public void setResponseResult(int responseResult) {
		this.responseResult = responseResult;
	}
	public int getNewInterval() {
		return newInterval;
	}
	public void setNewInterval(int newInterval) {
		this.newInterval = newInterval;
	}
	public int getOldInterval() {
		return oldInterval;
	}
	public void setOldInterval(int oldInterval) {
		this.oldInterval = oldInterval;
	}
	public int getDifficultyFactor() {
		return difficultyFactor;
	}
	public void setDifficultyFactor(int difficultyFactor) {
		this.difficultyFactor = difficultyFactor;
	}
	public int getResponseTime() {
		return responseTime;
	}
	public void setResponseTime(int responseTime) {
		this.responseTime = responseTime;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}

	public static JSONArray serializeList(List<MemoReviewLog> memoReviewLogs) throws JSONException {
		JSONArray array = new JSONArray();
		
		if(memoReviewLogs == null) {
			return array;
		}
		
		for(MemoReviewLog memoReviewLog : memoReviewLogs) {
			array.put(memoReviewLog.serialize());
		}
		
		return array;
	}
	
	public static List<MemoReviewLog> deserializeList(JSONArray memoReviewLogs) throws JSONException {
		List<MemoReviewLog> array = new ArrayList<MemoReviewLog>();
		
		if(memoReviewLogs == null) {
			return array;
		}
		
		for(int i=0;i<memoReviewLogs.length();i++) {
			array.add(new MemoReviewLog().deserialize(memoReviewLogs.getJSONObject(i)));
		}
		
		return array;
	}
	
	public JSONObject serialize() throws JSONException {
		JSONObject json = new JSONObject();
		
		json.put("memoReviewLogId", memoReviewLogId);
		json.put("memoId", memoId);
		json.put("responseResult", responseResult);
		json.put("newInterval", newInterval);
		json.put("oldInterval", oldInterval);
		json.put("difficultyFactor", difficultyFactor);
		json.put("responseTime", responseTime);
		json.put("type", type);		
		return json;
	}
	
	public MemoReviewLog deserialize(JSONObject json) throws JSONException {
		memoReviewLogId = json.getString("memoReviewLogId");
		memoId = json.getString("memoId");
		responseResult = json.getInt("responseResult");
		newInterval = json.getInt("newInterval");
		oldInterval = json.getInt("oldInterval");
		difficultyFactor = json.getInt("difficultyFactor");
		responseTime = json.getInt("responseTime");
		type = json.getInt("type");
		return this;
	}

	@Override
	public JSONObject encodeEntity() throws JSONException {
		JSONObject json = new JSONObject();
		
		json.put("memoReviewLogId", memoReviewLogId);
		json.put("memoId", memoId);
		json.put("responseResult", responseResult);
		json.put("newInterval", newInterval);
		json.put("oldInterval", oldInterval);
		json.put("difficultyFactor", difficultyFactor);
		json.put("responseTime", responseTime);
		json.put("type", type);		
		return json;
	}

	@Override
	public ISyncEntity decodeEntity(JSONObject json) throws JSONException {
		memoReviewLogId = json.getString("memoReviewLogId");
		memoId = json.getString("memoId");
		responseResult = json.getInt("responseResult");
		newInterval = json.getInt("newInterval");
		oldInterval = json.getInt("oldInterval");
		difficultyFactor = json.getInt("difficultyFactor");
		responseTime = json.getInt("responseTime");
		type = json.getInt("type");
		return this;
	}
}
