package app.memoling.android.entity;

import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import app.memoling.android.helper.DateHelper;

public class Schedule {

	private String m_scheduleId;
	private String m_memoBaseId;
	private int m_hours;
	private int m_minutes;
	private boolean[] m_days = new boolean[7];

	public String getMemoBaseId() {
		return m_memoBaseId;
	}

	public void setMemoBaseId(String memoBaseId) {
		m_memoBaseId = memoBaseId;
	}

	public String getScheduleId() {
		return m_scheduleId;
	}

	public void setScheduleId(String scheduleId) {
		m_scheduleId = scheduleId;
	}

	public int getHours() {
		return m_hours;
	}

	public int getMinutes() {
		return m_minutes;
	}

	public boolean[] getDays() {
		return m_days;
	}

	public int getNextDay() {
		int now = DateHelper.normalizeCalendarDays(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
		int nowHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		int nowMinutes = Calendar.getInstance().get(Calendar.MINUTE);

		for (int i = now; i < now + 7; i++) {
			if (m_days[i % 7]) {

				boolean isEarlierToday = getHours() < nowHour || (getHours() == nowHour && getMinutes() < nowMinutes);

				if (i == now && isEarlierToday) {
					continue;
				}

				return i;
			}
		}

		return -1;
	}

	public void setHours(int hours) {
		m_hours = hours;
	}

	public void setMinutes(int minutes) {
		m_minutes = minutes;
	}

	public void setDays(boolean[] days) {
		m_days = days;
	}

	public long millsToTask(Schedule schedule) {

		int d = schedule.getNextDay() - getNextDay();
		int h = schedule.getHours() - getHours();
		int m = schedule.getMinutes() - getMinutes();

		if(d == 0 && h == 0 && m == 0) {
			return 0L;
		}
		
		return ((d * 24 * 60 + h * 60 + m) * 60 - Calendar.getInstance().get(Calendar.SECOND)) * 1000L;
	}

	public long millsToTask() {
		Calendar calendar = Calendar.getInstance();
		Schedule now = new Schedule();
		boolean[] days = new boolean[7];
		days[DateHelper.normalizeCalendarDays(calendar.get(Calendar.DAY_OF_WEEK))] = true;
		now.setDays(days);
		now.setHours(calendar.get(Calendar.HOUR_OF_DAY));
		now.setMinutes(calendar.get(Calendar.MINUTE));
		return now.millsToTask(this);
	}

	public JSONObject serialize() throws JSONException {
		JSONObject json = new JSONObject();
		JSONArray jsonArray = new JSONArray();

		json.put("m_scheduleId", m_scheduleId);
		json.put("m_memoBaseId", m_memoBaseId);
		json.put("m_hours", m_hours);
		json.put("m_minutes", m_minutes);

		for (int i = 0; i < m_days.length; i++) {
			jsonArray.put(m_days[i]);
		}

		json.put("m_days", jsonArray);

		return json;
	}

	public Schedule deserialize(JSONObject json) throws JSONException {

		JSONArray jsonArray = json.getJSONArray("m_days");

		m_scheduleId = json.getString("m_scheduleId");
		m_memoBaseId = json.getString("m_memoBaseId");
		m_hours = json.getInt("m_hours");
		m_minutes = json.getInt("m_minutes");

		boolean days[] = new boolean[7];
		for (int i = 0; i < jsonArray.length(); i++) {
			days[i] = jsonArray.getBoolean(i);
		}

		m_days = days;

		return this;
	}
}
