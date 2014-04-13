package app.memoling.android.db;

import java.util.Date;

import android.database.Cursor;
import app.memoling.android.helper.DateHelper;

public class DatabaseHelper {

	public static float getFloat(Cursor cursor, String columnName) {
		return cursor.getFloat(cursor.getColumnIndex(columnName));
	}

	public static float optFloat(Cursor cursor, String columnName, float defaultValue) {
		try {
			return cursor.getFloat(cursor.getColumnIndex(columnName));
		} catch (Exception ex) {
			return defaultValue;
		}
	}

	public static double getDouble(Cursor cursor, String columnName) {
		return cursor.getDouble(cursor.getColumnIndex(columnName));
	}

	public static double optDouble(Cursor cursor, String columnName, double defaultValue) {
		try {
			return cursor.getDouble(cursor.getColumnIndex(columnName));
		} catch (Exception ex) {
			return defaultValue;
		}
	}

	public static String getString(Cursor cursor, String columnName) {
		return cursor.getString(cursor.getColumnIndex(columnName));
	}

	public static int getInt(Cursor cursor, String columnName) {
		return cursor.getInt(cursor.getColumnIndex(columnName));
	}

	public static int optInt(Cursor cursor, String columnName, int defaultValue) {
		try {
			return cursor.getInt(cursor.getColumnIndex(columnName));
		} catch (Exception ex) {
			return defaultValue;
		}
	}
	
	public static long getLong(Cursor cursor, String columnName) {
		return cursor.getLong(cursor.getColumnIndex(columnName));
	}

	public static long optLong(Cursor cursor, String columnName, long defaultValue) {
		try {
			return cursor.getLong(cursor.getColumnIndex(columnName));
		} catch (Exception ex) {
			return defaultValue;
		}
	}

	public static long getLong(Cursor cursor, String columnName) {
		return cursor.getLong(cursor.getColumnIndex(columnName));
	}

	public static long optLong(Cursor cursor, String columnName, long defaultValue) {
		try {
			return cursor.getLong(cursor.getColumnIndex(columnName));
		} catch (Exception ex) {
			return defaultValue;
		}
	}
	
	public static Date getDate(Cursor cursor, String columnName) {
		return DateHelper.fromNormalizedString(cursor.getString(cursor.getColumnIndex(columnName)));
	}

	public static Date optDate(Cursor cursor, String columnName, Date defaultValue) {
		try {
			return DateHelper.fromNormalizedString(cursor.getString(cursor.getColumnIndex(columnName)));
		} catch (Exception ex) {
			return defaultValue;
		}
	}

	public static boolean getBoolean(Cursor cursor, String columnName) {
		return cursor.getInt(cursor.getColumnIndex(columnName)) == 1;
	}

	public static boolean optBoolean(Cursor cursor, String columnName, boolean defaultValue) {
		try {
			return cursor.getInt(cursor.getColumnIndex(columnName)) == 1;
		} catch (Exception ex) {
			return defaultValue;
		}
	}
	
	public final static long Error = -1;
	
	public static enum Order {
		ASC,
		DESC,
	}
}
