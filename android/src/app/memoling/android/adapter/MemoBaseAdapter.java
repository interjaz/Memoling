package app.memoling.android.adapter;

import java.util.ArrayList;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import app.memoling.android.adapter.MemoAdapter.Sort;
import app.memoling.android.db.DatabaseHelper;
import app.memoling.android.db.DatabaseHelper.Order;
import app.memoling.android.db.SqliteAdapter;
import app.memoling.android.entity.Memo;
import app.memoling.android.entity.MemoBase;
import app.memoling.android.entity.MemoBaseInfo;
import app.memoling.android.helper.CacheHelper;
import app.memoling.android.helper.DateHelper;

public class MemoBaseAdapter extends SqliteAdapter {

	public final static String TableName = "MemoBases";

	private final static int m_memoBaseCacheSize = 10;
	private static CacheHelper<String, MemoBase> m_memoBaseCache = new CacheHelper<String, MemoBase>(
			m_memoBaseCacheSize);

	private final static int m_memoBaseInfoCacheSize = 10;
	private static CacheHelper<String, MemoBaseInfo> m_memoBaseInfoCache = new CacheHelper<String, MemoBaseInfo>(
			m_memoBaseInfoCacheSize);

	private final static int m_memoBaseListCacheSize = 2;
	private static CacheHelper<String, ArrayList<MemoBase>> m_memoBaseListCache = new CacheHelper<String, ArrayList<MemoBase>>(
			m_memoBaseListCacheSize);

	public MemoBaseAdapter(Context context) {
		super(context);
	}

	public MemoBaseAdapter(Context context, boolean persistant) {
		super(context, persistant);
	}

	public long add(MemoBase memoBase) {
		SQLiteDatabase db = null;
		invalidateCache();

		try {
			db = getDatabase();

			ContentValues values = createValues(memoBase);
			return db.insert(TableName, null, values);
		} finally {
			closeDatabase();
		}
	}

	public boolean addDeep(MemoBase memoBase) {
		SQLiteDatabase db = null;
		invalidateCache();

		try {
			db = getDatabase();
			db.beginTransaction();

			long success = 0;

			ContentValues values = createValues(memoBase);
			success = db.insert(TableName, null, values);

			if (success != -1) {
				for (Memo memo : memoBase.getMemos()) {
					success = MemoAdapter.add(this, db, memo);
					if (success == DatabaseHelper.Error) {
						break;
					}
				}
			}

			if (success != -1) {
				db.setTransactionSuccessful();
				return true;
			}

			return false;
		} finally {
			db.endTransaction();
			closeDatabase();
		}
	}

	public MemoBase get(String memoBaseId) {
		SQLiteDatabase db = null;

		if (!inSync()) {
			invalidateCache();
		}

		if (m_memoBaseCache.containsKey(memoBaseId)) {
			return m_memoBaseCache.get(memoBaseId);
		}

		try {
			db = getDatabase();

			return get(this, db, memoBaseId);
		} finally {
			closeDatabase();
		}
	}

	public static MemoBase get(SqliteAdapter adapter, SQLiteDatabase db, String memoBaseId) {

		if (!adapter.inSync()) {
			adapter.invalidateCache();
		}

		if (m_memoBaseCache.containsKey(memoBaseId)) {
			return m_memoBaseCache.get(memoBaseId);
		}

		MemoBase memoBase = null;

		String query = "SELECT " + "	B.MemoBaseId B_MemoBaseId, B.Name B_Name, B.Created B_Created, B.Active B_Active "
				+ "FROM MemoBases  AS B " + "WHERE B.MemoBaseId = ?";

		Cursor cursor = db.rawQuery(query, new String[] { memoBaseId });

		if (cursor.moveToFirst()) {
			memoBase = new MemoBase();
			memoBase.setActive(DatabaseHelper.getBoolean(cursor, "B_Active"));
			memoBase.setCreated(DatabaseHelper.getDate(cursor, "B_Created"));
			memoBase.setMemoBaseId(DatabaseHelper.getString(cursor, "B_MemoBaseId"));
			memoBase.setName(DatabaseHelper.getString(cursor, "B_Name"));
		}
		if (memoBase != null) {
			m_memoBaseCache.put(memoBaseId, memoBase);
		}

		return memoBase;
	}

	public ArrayList<MemoBase> getAll() {
		SQLiteDatabase db = null;

		if (!inSync()) {
			invalidateCache();
		}

		if (m_memoBaseListCache.containsKey("all")) {
			return m_memoBaseListCache.get("all");
		}

		try {
			ArrayList<MemoBase> memoBases = new ArrayList<MemoBase>();

			db = getDatabase();

			String query = "SELECT "
					+ "	B.MemoBaseId B_MemoBaseId, B.Name B_Name, B.Created B_Created, B.Active B_Active "
					+ "FROM MemoBases  AS B";

			Cursor cursor = db.rawQuery(query, null);

			while (cursor.moveToNext()) {
				MemoBase memoBase = new MemoBase();

				memoBase.setActive(DatabaseHelper.getBoolean(cursor, "B_Active"));
				memoBase.setCreated(DatabaseHelper.getDate(cursor, "B_Created"));
				memoBase.setMemoBaseId(DatabaseHelper.getString(cursor, "B_MemoBaseId"));
				memoBase.setName(DatabaseHelper.getString(cursor, "B_Name"));

				memoBases.add(memoBase);
			}

			if (memoBases.size() > 0) {
				m_memoBaseListCache.put("all", memoBases);
			}

			return memoBases;

		} finally {
			closeDatabase();
		}
	}

	public MemoBaseInfo getMemoBaseInfo(String memoBaseId) {
		SQLiteDatabase db = null;

		if (!inSync()) {
			invalidateCache();
		}

		if (m_memoBaseInfoCache.containsKey(memoBaseId)) {
			return m_memoBaseInfoCache.get(memoBaseId);
		}

		try {
			MemoBaseInfo memoBaseInfo = null;
			MemoBase memoBase = null;

			db = getDatabase();

			String query = "SELECT " + "B.MemoBaseId B_MemoBaseId, B.Name B_Name, B.Created B_Created, "
					+ "B.Active B_Active, COUNT(M.MemoBaseId) NoAll, SUM(M.Active) NoActive, "
					+ "MAX(M.LastReviewed) AS LastReviewed " + "FROM MemoBases  AS B "
					+ "OUTER LEFT JOIN Memos AS M ON M.MemoBaseId = B.MemoBaseId " + "WHERE B.MemoBaseId = ?";

			Cursor cursor = db.rawQuery(query, new String[] { memoBaseId });

			if (cursor.moveToFirst()) {
				memoBase = new MemoBase();
				memoBaseInfo = new MemoBaseInfo();

				memoBase.setActive(DatabaseHelper.getBoolean(cursor, "B_Active"));
				memoBase.setCreated(DatabaseHelper.getDate(cursor, "B_Created"));
				memoBase.setMemoBaseId(DatabaseHelper.getString(cursor, "B_MemoBaseId"));
				memoBase.setName(DatabaseHelper.getString(cursor, "B_Name"));

				memoBaseInfo.setMemoBase(memoBase);
				memoBaseInfo.setLastReviewed(DatabaseHelper.optDate(cursor, "LastReviewed", new Date()));
				memoBaseInfo.setNoActiveMemos(DatabaseHelper.optInt(cursor, "NoActive", 0));
				memoBaseInfo.setNoAllMemos(DatabaseHelper.optInt(cursor, "NoAll", 0));
			}
			if (memoBaseInfo != null) {
				m_memoBaseInfoCache.put(memoBaseId, memoBaseInfo);
			}

			return memoBaseInfo;
		} finally {
			closeDatabase();
		}
	}

	public void delete(String memoBaseId) {
		SQLiteDatabase db = null;
		invalidateCache();

		try {
			db = getDatabase();

			// Delete all children
			MemoAdapter memoAdapter = new MemoAdapter(getContext());
			ArrayList<Memo> memoList = memoAdapter.getAll(memoBaseId, Sort.CreatedDate, Order.ASC);

			if (memoList != null) {
				for (int i = 0; i < memoList.size(); i++) {
					memoAdapter.delete(memoList.get(i).getMemoId());
				}
			}

			db.delete(TableName, "MemoBaseId" + "=?", new String[] { memoBaseId });

		} finally {
			closeDatabase();
		}
	}

	public int update(MemoBase memoBase) {
		SQLiteDatabase db = null;
		invalidateCache();

		try {
			db = getDatabase();
			ContentValues values = createValues(memoBase);
			return db.update(TableName, values, "MemoBaseId =?", new String[] { memoBase.getMemoBaseId() });
		} finally {
			closeDatabase();
		}
	}

	private static ContentValues createValues(MemoBase base) {
		ContentValues values = new ContentValues();
		values.put("MemoBaseId", base.getMemoBaseId());
		values.put("Name", base.getName());
		values.put("Created", DateHelper.toNormalizedString(base.getCreated()));
		values.put("Active", base.getActive());
		return values;
	}

	@Override
	protected void onInvalidateCache() {
		super.onInvalidateCache();

		m_memoBaseCache.clear();
		m_memoBaseInfoCache.clear();
		m_memoBaseListCache.clear();
	}
}
