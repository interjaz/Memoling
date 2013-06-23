package app.memoling.android.schedule;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import app.memoling.android.Config;
import app.memoling.android.Schedule;
import app.memoling.android.helper.DateHelper;

public class Scheduler {

	public final static String TasksExtra = "tasks";

	private static ArrayList<BaseSchedule> m_schedule;

	public static BaseSchedule getSchedule(String memoBaseId) {
		ArrayList<BaseSchedule> schedules = getSchedules();
		for (int i = 0; i < schedules.size(); i++) {
			if (schedules.get(i).getMemoBaseId().equals(memoBaseId)) {
				return schedules.get(i);
			}
		}

		return new BaseSchedule(memoBaseId, new ArrayList<Schedule>());
	}

	public static ArrayList<BaseSchedule> getSchedules() {
		if (m_schedule == null) {
			m_schedule = load();
		}

		return m_schedule;
	}

	public static void updateSchedule(BaseSchedule baseSchedule) {

		// Update baseScheudle
		boolean updated = false;
		ArrayList<BaseSchedule> schedules = getSchedules();
		try {
			for (int i = 0; i < schedules.size(); i++) {
				if (schedules.get(i).getMemoBaseId().equals(baseSchedule.getMemoBaseId())) {
					schedules.get(i).setSchedule(baseSchedule.getSchedule());
					updated = true;
					break;
				}
			}
			if (!updated) {
				schedules.add(baseSchedule);
			}
		} catch (Exception ex) {
			schedules.clear();
			schedules.add(baseSchedule);
			clear();
		}

		// Save data
		save(schedules);
	}

	public static void updateAlarm(Context context) {

		ScheduleTasks tasks = getNextTasks();
		if (tasks.size() == 0) {
			return;
		}

		try {
			Intent timerIntent = new Intent(context, AlarmReceiver.class);
			timerIntent.putExtra(TasksExtra, tasks.serialize());
			PendingIntent intent = PendingIntent.getBroadcast(context, 0, timerIntent,
					PendingIntent.FLAG_CANCEL_CURRENT);

			long due = new Date().getTime() + tasks.get(0).millsToTask();
			long dueUTC = due + Calendar.getInstance().get(Calendar.ZONE_OFFSET);
			
			AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			alarmManager.set(AlarmManager.RTC, dueUTC, intent);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static ScheduleTasks getNextTasks() {

		ArrayList<ScheduleTask> futureTasks = new ArrayList<ScheduleTask>();
		ArrayList<ScheduleTask> futureTasksCandidates = new ArrayList<ScheduleTask>();
		final int daysInAWeek = 7;

		Calendar calendar = Calendar.getInstance();
		ScheduleTask now = new ScheduleTask();
		now.day = DateHelper.normalizeCalendarDays(calendar.get(Calendar.DAY_OF_WEEK));
		now.hours = calendar.get(Calendar.HOUR_OF_DAY);
		now.minutes = calendar.get(Calendar.MINUTE);
		ScheduleTask earliestTask = null;

		// find all tasks after today which is not in current task list
		ArrayList<BaseSchedule> schedules = getSchedules();

		for (int i = 0; i < schedules.size(); i++) {
			BaseSchedule baseSchedule = schedules.get(i);
			ArrayList<Schedule> scheduleList = baseSchedule.getSchedule();

			// Look through each schedule list
			for (int j = 0; j < scheduleList.size(); j++) {
				Schedule schedule = scheduleList.get(j);

				boolean scheduleDays[] = schedule.getDays();
				// Look for next suitable days
				for (int k = 0; k < scheduleDays.length; k++) {
					int startFromToday = (now.day + k) % daysInAWeek;
					if (scheduleDays[startFromToday]) {
						// Check hours only for today date
						if (k != 0 || (schedule.getHours() >= now.hours && schedule.getMinutes() > now.minutes)) {
							ScheduleTask task = ScheduleTask.create(schedule, baseSchedule.getMemoBaseId(),
									startFromToday);

							long minutesToCurrent = now.millsToTask(task) / (60 * 1000L);

							if (earliestTask == null
									|| (minutesToCurrent < (now.millsToTask(earliestTask) / (60 * 1000L)))) {
								earliestTask = task;
								futureTasksCandidates.add(task);
							}

						}
					}
				}
			}
		}

		// refine - remove older then earliest
		for (int i = 0; i < futureTasksCandidates.size(); i++) {
			long minutesToEarliest = futureTasksCandidates.get(i).millsToTask(earliestTask) / (60 * 1000L);
			if (minutesToEarliest == 0) {
				futureTasks.add(futureTasksCandidates.get(i));
			}
		}

		return new ScheduleTasks(futureTasks);
	}

	private static ArrayList<BaseSchedule> load() {

		File file = new File(Config.AppPath + "/schedule.list");
		FileReader reader = null;
		try {
			if (!file.exists()) {
				new File(Config.AppPath).mkdirs();
				file.createNewFile();
			}

			reader = new FileReader(file);
			StringBuilder sb = new StringBuilder();
			char buff[] = new char[1000];
			int charRead;
			while (true) {
				charRead = reader.read(buff);
				if (charRead == -1) {
					break;
				}
				sb.append(buff, 0, charRead);
			}

			return BaseSchedule.deserializeList(sb.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
			return new ArrayList<BaseSchedule>();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	private static boolean save(ArrayList<BaseSchedule> list) {

		File file = new File(Config.AppPath + "/schedule.list");
		FileWriter writer = null;
		try {
			writer = new FileWriter(file);
			writer.write(BaseSchedule.serializeList(list));
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		return true;
	}

	private static void clear() {
		File file = new File(Config.AppPath + "/schedule.list");
		if (file.exists()) {
			file.delete();
		}
	}
}
