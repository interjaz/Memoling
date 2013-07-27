package app.memoling.android.schedule;

import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import app.memoling.android.entity.Schedule;
import app.memoling.android.helper.DateHelper;

public class ScheduleTask {

	public String memoBaseId;
	public int hours;
	public int minutes;
	public int day;

	public static ScheduleTask create(Schedule schedule, String memoBaseId, int day) {
		ScheduleTask task = new ScheduleTask();
		task.memoBaseId = memoBaseId;
		task.hours = schedule.getHours();
		task.minutes = schedule.getMinutes();
		task.day = day;

		return task;
	}

	public long millsToTask(ScheduleTask task) {

		int d = task.day - day;
		if (d < 0) {
			d += 7;
		}
		int h = task.hours - hours;
		int m = task.minutes - minutes;

		return ((d * 24 * 60 + h * 60 + m) * 60 - Calendar.getInstance().get(Calendar.SECOND)) * 1000L;
	}

	public long millsToTask() {
		Calendar calendar = Calendar.getInstance();
		ScheduleTask now = new ScheduleTask();
		now.day = DateHelper.normalizeCalendarDays(calendar.get(Calendar.DAY_OF_WEEK));
		now.hours = calendar.get(Calendar.HOUR_OF_DAY);
		now.minutes = calendar.get(Calendar.MINUTE);
		return now.millsToTask(this);
	}

	public String serialize() throws JSONException {
		JSONObject json = new JSONObject();

		json.put("m_memoBaseId", memoBaseId);
		json.put("m_hours", hours);
		json.put("m_minutes", minutes);
		json.put("m_day", day);

		return json.toString();
	}

	public static ScheduleTask deserialize(String serialized) throws JSONException {
		JSONObject json = new JSONObject(serialized);
		ScheduleTask object = new ScheduleTask();

		object.memoBaseId = json.getString("m_memoBaseId");
		object.hours = json.getInt("m_hours");
		object.minutes = json.getInt("m_minutes");
		object.day = json.getInt("m_day");

		return object;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + day;
		result = prime * result + hours;
		result = prime * result + minutes;
		result = prime * result + memoBaseId.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ScheduleTask other = (ScheduleTask) obj;
		if (!memoBaseId.equals(other.memoBaseId))
			return false;
		if (day != other.day)
			return false;
		if (hours != other.hours)
			return false;
		if (minutes != other.minutes)
			return false;
		return true;
	}

}