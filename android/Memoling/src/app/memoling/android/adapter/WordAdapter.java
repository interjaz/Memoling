package app.memoling.android.adapter;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import app.memoling.android.db.DatabaseHelper;
import app.memoling.android.db.SqliteAdapter;
import app.memoling.android.entity.Language;
import app.memoling.android.entity.SyncAction;
import app.memoling.android.entity.Word;
import app.memoling.android.sync.cloud.ISyncAdapter;
import app.memoling.android.sync.cloud.ISyncEntity;

public class WordAdapter extends SqliteAdapter implements ISyncAdapter {

	public final static String TableName = "Words";

	public WordAdapter(Context context) {
		super(context);
	}

	public static Word bindWord(Cursor cursor) {
		return WordAdapter.bindWord(cursor, "");
	}
	
	public static Word bindWord(Cursor cursor, String prefix) {
		Word word = new Word();
		
		word.setWordId(DatabaseHelper.getString(cursor, prefix + "WordId"));
		word.setDescription(DatabaseHelper.getString(cursor, prefix + "Description"));
		word.setLanguage(Language.parse(DatabaseHelper.getString(cursor, prefix + "LanguageIso639")));
		word.setWord(DatabaseHelper.getString(cursor, prefix + "Word"));
		
		return word;
	}
	
	private static ContentValues createValues(Word word) {
		ContentValues values = new ContentValues();
		values.put("WordId", word.getWordId());
		values.put("Word", word.getWord());
		values.put("LanguageIso639", word.getLanguage().getCode());
		values.put("Description", word.getDescription());
		return values;
	}

	public Word get(String wordId) {
		try {
			return WordAdapter.get(getDatabase(), wordId);
		} finally {
			closeDatabase();
		}
	}
	
	public void insert(Word word, String syncClientId) throws SQLiteException {
		try {
			WordAdapter.insert(getDatabase(), word, syncClientId);
		} finally {
			closeDatabase();
		}
	}
	
	public void update(Word word, String syncClientId) throws SQLiteException {
		try {
			WordAdapter.update(getDatabase(), word, syncClientId);
		} finally {
			closeDatabase();
		}
	}
	
	public void delete(String wordId, String syncClientId) throws SQLiteException {
		try {
			WordAdapter.delete(getDatabase(), wordId, syncClientId);
		} finally {
			closeDatabase();
		}
	}
	
	public static Word get(SQLiteDatabase db, String wordId) {
		String sql = "SELECT WordId, Word, LanguageIso639, Description FROM Words WHERE WordId = ?";
		Cursor cursor = null;
		
		try {
			cursor = db.rawQuery(sql, new String[] { wordId });
			if(cursor.moveToNext()) {
				Word word = WordAdapter.bindWord(cursor);
				return word;
			}
		} finally {
			if(cursor != null) {
				cursor.close();
			}
		}
		
		return null;
	}
	
	public static void insert(SQLiteDatabase db, Word word, String syncClientId) throws SQLiteException {
		SyncAction syncAction = new SyncAction();
		syncAction.setAction(SyncAction.ACTION_INSERT);
		syncAction.setPrimaryKey(word.getWordId());
		syncAction.setTable(TableName);
		syncAction.setSyncClientId(syncClientId);
		
		WordAdapter.insertDbSync(db, word, syncAction);
	}

	public static void update(SQLiteDatabase db, Word word, String syncClientId) throws SQLiteException {
		
		ArrayList<String> updateColumns = new ArrayList<String>();
		Word dbWord = WordAdapter.get(db, word.getWordId());
		
		if(!dbWord.getWord().equals(word.getWord())) {
			updateColumns.add("Word");
		}
		
		if(!dbWord.getDescription().equals(word.getDescription())) {
			updateColumns.add("Description");
		}
		
		if(!dbWord.getLanguage().equals(word.getLanguage())) {
			updateColumns.add("LanguageIso639");
		}
		
		for(String updateColumn : updateColumns) {
			SyncAction syncAction = new SyncAction();
			syncAction.setAction(SyncAction.ACTION_UPDATE);
			syncAction.setPrimaryKey(word.getWordId());
			syncAction.setTable(TableName);
			syncAction.setSyncClientId(syncClientId);
			syncAction.setUpdateColumn(updateColumn);
			
			WordAdapter.updateDbSync(db, word, syncAction);
		}
		
	}
	
	public static void delete(SQLiteDatabase db, String wordId, String syncClientId) throws SQLiteException {
		SyncAction syncAction = new SyncAction();
		syncAction.setAction(SyncAction.ACTION_DELETE);
		syncAction.setPrimaryKey(wordId);
		syncAction.setTable(TableName);
		syncAction.setSyncClientId(syncClientId);
		
		WordAdapter.deleteDbSync(db, wordId, syncAction);
	}
	
	@Override
	public ISyncEntity decodeEntity(JSONObject json) throws JSONException {
		Word word = new Word();
		word.decodeEntity(json);		
		return word;
	}

	@Override
	public ISyncEntity getEntity(String primaryKey) {
		return WordAdapter.get(this.getDatabase(), primaryKey);
	}

	@Override
	public void insertEntity(SQLiteDatabase db, ISyncEntity object, SyncAction syncAction) throws SQLiteException {
		WordAdapter.insertDbSync(db, (Word)object, syncAction);
	}

	@Override
	public void deleteEntity(SQLiteDatabase db, String primaryKey, SyncAction syncAction) throws SQLiteException {
		WordAdapter.deleteDbSync(db, primaryKey, syncAction);
	}

	@Override
	public void updateEntity(SQLiteDatabase db, ISyncEntity object, SyncAction syncAction) throws SQLiteException {
		WordAdapter.updateDbSync(db, (Word)object, syncAction);
	}
	
	@Override
	public void buildInitialSync(SQLiteDatabase db, String syncClientId) {
		String sql = "SELECT WordId FROM Words";
		
		Cursor cursor = null;
		try {
			cursor = db.rawQuery(sql, null);
			while(cursor.moveToNext()) {
				SyncAction syncAction = new SyncAction();
				syncAction.setAction(SyncAction.ACTION_INSERT);
				syncAction.setTable(TableName);
				syncAction.setPrimaryKey(DatabaseHelper.getString(cursor, "WordId"));
				syncAction.setSyncClientId(syncClientId);
				
				SyncActionAdapter.insert(db, syncAction);
			}
		} finally {
			if(cursor != null) {
				cursor.close();
			}
		}
	}
	
	private static void insertDbSync(SQLiteDatabase db, Word word, SyncAction syncAction) throws SQLiteException {
		db.beginTransaction();

		try {
			ContentValues values = createValues(word);
			
			db.insertOrThrow(TableName, null, values);
			SyncActionAdapter.insertAction(db, syncAction);
			
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}
	
	private static void updateDbSync(SQLiteDatabase db, Word word, SyncAction syncAction) throws SQLiteException {
		db.beginTransaction();
		
		try {
			ContentValues values = new ContentValues();
			String column = syncAction.getUpdateColumn();
			
			if(column.equals("Word")) {
				values.put("Word", word.getWord());
			} else if(column.equals("LanguageIso639")) {
				values.put("LanguageISo639", word.getLanguage().getCode());
			} else if(column.equals("Description")) {
				values.put("Description", word.getDescription());
			} 

			if(values.size() > 0) {
				db.update(TableName, values, "WordId = ?", new String[] { word.getWordId() });
				SyncActionAdapter.updateAction(db, syncAction);
			}
			
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}
	
	private static void deleteDbSync(SQLiteDatabase db, String wordId, SyncAction syncAction) throws SQLiteException {
		db.beginTransaction();
		
		try {

			db.delete(TableName, "WordId = ?", new String[] { wordId });
			SyncActionAdapter.deleteAction(db, syncAction);
			
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}
}
