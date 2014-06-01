package app.memoling.android.adapter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import app.memoling.android.db.SqliteAdapter;

public class DbFixAdapter extends SqliteAdapter {

	public DbFixAdapter(Context context) {
		super(context);
	}
	
	public void removeOrphans() {
		SQLiteDatabase db = getDatabase();
		try {
			db.beginTransaction();
			
			removeMemoOrphans(db);
			removeWordOrphans(db);
			
			removeSyncActionOrphans(db);
			
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
			closeDatabase();
		}
	}
	
	private void removeMemoOrphans(SQLiteDatabase db) {
		
		String query = "DELETE FROM Memos WHERE MemoId IN ( " +
						"	SELECT M.MemoId FROM Memos AS M " + 
						"	LEFT OUTER JOIN MemoBases AS MB ON M.MemoBaseId = MB.MemoBaseId " +
						"	WHERE MB.MemoBaseId IS NULL "+
						")";

		db.execSQL(query);
	}
	
	private void removeWordOrphans(SQLiteDatabase db) {
		
		String query = "DELETE FROM Words WHERE WordId IN ( " +
						"	SELECT W.WordId FROM Words AS W " + 
						"	LEFT OUTER JOIN Memos AS MA ON MA.WordAId = W.WordId " +
						"	LEFT OUTER JOIN Memos AS MB ON MB.WordBId = W.WordId "  +
        				"	WHERE MA.MemoId IS NULL AND MB.MemoId IS NULL " +
        				")";

		db.execSQL(query);
	}
	
	private void removeSyncActionOrphans(SQLiteDatabase db) {
		
		String query = "";
		
		query =  "DELETE FROM SyncActions WHERE SyncActionId IN (                      " +
				"	SELECT SA.SyncActionId FROM SyncActions AS SA                     " +
				"	LEFT OUTER JOIN MemoBases AS M ON SA.PrimaryKey = M.MemoBaseId    " +
				"	WHERE [Table] = 'MemoBases' AND M.MemoBaseId IS NULL AND SA.Action != -1  " +
				")                                                                    ";
		db.execSQL(query);

		query = "DELETE FROM SyncActions WHERE SyncActionId IN (                      " +
				"	SELECT SA.SyncActionId FROM SyncActions AS SA                     " +
				"	LEFT OUTER JOIN Memos AS M ON SA.PrimaryKey = M.MemoId            " +
				"	WHERE [Table] = 'Memos' AND M.MemoId IS NULL AND SA.Action != -1  " +
				")                                                                    ";
		db.execSQL(query);
				
		query = "DELETE FROM SyncActions WHERE SyncActionId IN (                      " +
				"	SELECT SA.SyncActionId FROM SyncActions AS SA                     " +
				"	LEFT OUTER JOIN Words AS W ON SA.PrimaryKey = W.WordId            " +
				"	WHERE [Table] = 'Words' AND W.WordId IS NULL AND SA.Action != -1  " +
				")                                                                    ";
		db.execSQL(query);					
	}
}
