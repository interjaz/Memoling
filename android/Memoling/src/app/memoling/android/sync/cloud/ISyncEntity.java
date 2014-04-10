package app.memoling.android.sync.cloud;

import org.json.JSONException;
import org.json.JSONObject;

public interface ISyncEntity {

	JSONObject encodeEntity() throws JSONException;
	ISyncEntity decodeEntity(JSONObject string) throws JSONException;
	
}
