package app.memoling.android.sync.cloud;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import app.memoling.android.entity.SyncAction;

public interface ISyncAdapter {

	ISyncEntity decodeEntity(JSONObject json) throws JSONException;
	ISyncEntity getEntity(String primaryKey);
	
	void insertEntity(SQLiteDatabase db, ISyncEntity object, SyncAction action) throws SQLiteException;
	void deleteEntity(SQLiteDatabase db, String primaryKey, SyncAction action) throws SQLiteException;
	void updateEntity(SQLiteDatabase db, ISyncEntity object, SyncAction action) throws SQLiteException;
	
	void buildInitialSync(SQLiteDatabase db, String syncClientId) throws SQLiteException;
}
