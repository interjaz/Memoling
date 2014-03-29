package app.memoling.android.adapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import app.memoling.android.db.DatabaseHelper;
import app.memoling.android.db.SqliteAdapter;
import app.memoling.android.entity.Schedule;
import app.memoling.android.helper.AppLog;
import app.memoling.android.helper.DateHelper;

public class ScheduleAdapter extends SqliteAdapter {

	public ScheduleAdapter(Context context) {
		super(context);
	}

	public List<Schedule> getByMemoBaseId(String memoBaseId) {

		SQLiteDatabase db = null;

		try {
			db = getDatabase();
			return getByMemoBaseId(this, db, memoBaseId);

		} finally {
			closeDatabase();
		}
	}

	public static List<Schedule> getByMemoBaseId(SqliteAdapter adapter, SQLiteDatabase db, String memoBaseId) {

		String query = "SELECT ScheduleId, MemoBaseId, Hours, Minutes, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday "
				+ "FROM Schedules " + "WHERE MemoBaseId = ? ";

		Cursor cursor = db.rawQuery(query, new String[] { memoBaseId });

		try {
			List<Schedule> schedules = new ArrayList<Schedule>();

			while (cursor.moveToNext()) {
				Schedule schedule = new Schedule();

				boolean[] days = new boolean[7];
				days[0] = DatabaseHelper.getBoolean(cursor, "Monday");
				days[1] = DatabaseHelper.getBoolean(cursor, "Tuesday");
				days[2] = DatabaseHelper.getBoolean(cursor, "Wednesday");
				days[3] = DatabaseHelper.getBoolean(cursor, "Thursday");
				days[4] = DatabaseHelper.getBoolean(cursor, "Friday");
				days[5] = DatabaseHelper.getBoolean(cursor, "Saturday");
				days[6] = DatabaseHelper.getBoolean(cursor, "Sunday");

				schedule.setDays(days);
				schedule.setHours(DatabaseHelper.getInt(cursor, "Hours"));
				schedule.setMinutes(DatabaseHelper.getInt(cursor, "Minutes"));
				schedule.setMemoBaseId(DatabaseHelper.getString(cursor, "MemoBaseId"));
				schedule.setScheduleId(DatabaseHelper.getString(cursor, "ScheduleId"));

				schedules.add(schedule);
			}

			return schedules;
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
	}

	public List<Schedule> getOnTime(Date date) {

		SQLiteDatabase db = null;

		try {
			db = getDatabase();
			return getOnTime(this, db, date);

		} finally {
			closeDatabase();
		}
	}

	public static List<Schedule> getOnTime(SqliteAdapter adapter, SQLiteDatabase db, Date date) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int hours = calendar.get(Calendar.HOUR_OF_DAY);
		int minutes = calendar.get(Calendar.MINUTE);
		int day = DateHelper.normalizeCalendarDays(calendar.get(Calendar.DAY_OF_WEEK));

		String query = "SELECT ScheduleId, MemoBaseID, Hours, Minutes, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday "
				+ "FROM Schedules " + "WHERE Hours = ? AND Minutes = ? ";

		switch (day) {
		case 0:
			query += " AND Monday = 1";
			break;
		case 1:
			query += " AND Tuesday = 1";
			break;
		case 2:
			query += " AND Wednesday = 1";
			break;
		case 3:
			query += " AND Thursday = 1";
			break;
		case 4:
			query += " AND Friday = 1";
			break;
		case 5:
			query += " AND Saturday = 1";
			break;
		case 6:
			query += " AND Sunday = 1";
			break;
		}

		Cursor cursor = db.rawQuery(query, new String[] { Integer.toString(hours), Integer.toString(minutes) });

		try {
			List<Schedule> schedules = new ArrayList<Schedule>();

			while (cursor.moveToNext()) {
				Schedule schedule = new Schedule();

				boolean[] days = new boolean[7];
				days[0] = DatabaseHelper.getBoolean(cursor, "Monday");
				days[1] = DatabaseHelper.getBoolean(cursor, "Tuesday");
				days[2] = DatabaseHelper.getBoolean(cursor, "Wednesday");
				days[3] = DatabaseHelper.getBoolean(cursor, "Thursday");
				days[4] = DatabaseHelper.getBoolean(cursor, "Friday");
				days[5] = DatabaseHelper.getBoolean(cursor, "Saturday");
				days[6] = DatabaseHelper.getBoolean(cursor, "Sunday");

				schedule.setDays(days);
				schedule.setHours(DatabaseHelper.getInt(cursor, "Hours"));
				schedule.setMinutes(DatabaseHelper.getInt(cursor, "Minutes"));
				schedule.setMemoBaseId(DatabaseHelper.getString(cursor, "MemoBaseId"));
				schedule.setScheduleId(DatabaseHelper.getString(cursor, "ScheduleId"));

				schedules.add(schedule);
			}
			
			return schedules;
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
	}

	public Schedule getNextFirstSchedule() {

		SQLiteDatabase db = null;

		try {
			db = getDatabase();
			return getNextFirstSchedule(this, db);

		} finally {
			closeDatabase();
		}
	}

	public static Schedule getNextFirstSchedule(SqliteAdapter adapter, SQLiteDatabase db) {

		String order = "ORDER BY ";
		Calendar calendar = Calendar.getInstance();

		int today = DateHelper.normalizeCalendarDays(calendar.get(Calendar.DAY_OF_WEEK));
		for (int i = today; i < today + 7; i++) {
			switch (i % 7) {
			case 0:
				order += "Monday,";
				break;
			case 1:
				order += "Tuesday,";
				break;
			case 2:
				order += "Wednesday,";
				break;
			case 3:
				order += "Thursday,";
				break;
			case 4:
				order += "Friday,";
				break;
			case 5:
				order += "Saturday,";
				break;
			case 6:
				order += "Sunday,";
				break;
			}
		}
		order += " Hours, Minutes ";

		String query = "SELECT ScheduleId, MemoBaseID, Hours, Minutes, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday "
				+ "FROM Schedules " + order;

		Cursor cursor = db.rawQuery(query, null);
		try {
			List<Schedule> schedules = new ArrayList<Schedule>();
			while (cursor.moveToNext()) {
				Schedule schedule = new Schedule();

				boolean[] days = new boolean[7];
				days[0] = DatabaseHelper.getBoolean(cursor, "Monday");
				days[1] = DatabaseHelper.getBoolean(cursor, "Tuesday");
				days[2] = DatabaseHelper.getBoolean(cursor, "Wednesday");
				days[3] = DatabaseHelper.getBoolean(cursor, "Thursday");
				days[4] = DatabaseHelper.getBoolean(cursor, "Friday");
				days[5] = DatabaseHelper.getBoolean(cursor, "Saturday");
				days[6] = DatabaseHelper.getBoolean(cursor, "Sunday");

				schedule.setDays(days);
				schedule.setHours(DatabaseHelper.getInt(cursor, "Hours"));
				schedule.setMinutes(DatabaseHelper.getInt(cursor, "Minutes"));
				schedule.setMemoBaseId(DatabaseHelper.getString(cursor, "MemoBaseId"));
				schedule.setScheduleId(DatabaseHelper.getString(cursor, "ScheduleId"));

				schedules.add(schedule);
			}

			int hours = calendar.get(Calendar.HOUR_OF_DAY);
			int minutes = calendar.get(Calendar.MINUTE);

			for (int i = 0; i < schedules.size(); i++) {
				Schedule schedule = schedules.get(i);

				// Look first for next not the first one
				if (schedule.getDays()[today]) {
					if ((schedule.getHours() > hours)
							|| (schedule.getHours() == hours && schedule.getMinutes() > minutes)) {
						return schedule;
					}
				} else {
					return schedule;
				}
			}

			// Case of non or one schedule which is today and now.
			if (schedules.size() == 1) {
				return schedules.get(0);
			}

			return null;
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
	}

	public void updateAllForMemoBase(List<Schedule> schedules, String memoBaseId) {
		SQLiteDatabase db = null;

		try {
			db = getDatabase();
			db.beginTransaction();

			// Clear all previous ones
			db.delete("Schedules", "MemoBaseId = ?", new String[] { memoBaseId });

			// Add all new ones
			for (Schedule schedule : schedules) {
				ContentValues values = createValues(schedule);
				long result = db.insert("Schedules", null, values);
				if (result == -1L) {
					throw new Exception("Failed to add schedule: " + schedule.serialize());
				}
			}

			db.setTransactionSuccessful();
		} catch (Exception ex) {
			AppLog.e("ScheduleAdapter", "updateAllFromMemoBase - failed to update", ex);
		} finally {
			if (db != null) {
				db.endTransaction();
			}
		}
	}
	
	public void deleteForMemoBaseId(String memoBaseId) {
		SQLiteDatabase db = null;

		try {
			db = getDatabase();
			db.delete("Schedules", "MemoBaseId = ?", new String[] { memoBaseId });			
		} catch (Exception ex) {
			AppLog.e("ScheduleAdapter", "deleteForMemoBaseId - failed to delete", ex);
		} finally {
			if (db != null) {
				db.endTransaction();
			}
		}
	}

	private static ContentValues createValues(Schedule schedule) {
		ContentValues values = new ContentValues();

		boolean[] days = schedule.getDays();

		values.put("ScheduleId", schedule.getScheduleId());
		values.put("MemoBaseId", schedule.getMemoBaseId());
		values.put("Hours", schedule.getHours());
		values.put("Minutes", schedule.getMinutes());
		values.put("Monday", days[0]);
		values.put("Tuesday", days[1]);
		values.put("Wednesday", days[2]);
		values.put("Thursday", days[3]);
		values.put("Friday", days[4]);
		values.put("Saturday", days[5]);
		values.put("Sunday", days[6]);

		return values;
	}
}
