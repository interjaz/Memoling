package app.memoling.android.schedule;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import app.memoling.android.R;
import app.memoling.android.adapter.MemoBaseAdapter;
import app.memoling.android.adapter.ScheduleAdapter;
import app.memoling.android.entity.Schedule;
import app.memoling.android.helper.AppLog;
import app.memoling.android.ui.activity.ReviewActivity;

public class AlarmReceiver extends BroadcastReceiver {

	private static int m_notificationId = 0;

	@Override
	public void onReceive(Context context, Intent intent) {

		try {

			if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
				onReceiveBoot(context, intent);
			} else {
				onReceiveAlarm(context, intent);
			}

		} catch (Exception ex) {
			AppLog.e("AlarReceiver", "onReceive", ex);
		}

	}

	private void onReceiveBoot(Context context, Intent intent) {
		updateAlarm(context);
	}

	private void onReceiveAlarm(Context context, Intent intent) throws Exception {

		MemoBaseAdapter memoBaseAdapter = new MemoBaseAdapter(context);
		ScheduleAdapter scheduleAdapter = new ScheduleAdapter(context);

		List<Schedule> schedules = scheduleAdapter.getOnTime(new Date());

		// Get ones ones without MemoBase
		List<Schedule> schedulesToDelete = new ArrayList<Schedule>();
		for(Schedule schedule : schedules) {
			if(memoBaseAdapter.get(schedule.getMemoBaseId()) == null) {
				schedulesToDelete.add(schedule);
			}
		}
		
		// Remove old ones
		for(Schedule schedule : schedulesToDelete) {
			scheduleAdapter.deleteForMemoBaseId(schedule.getMemoBaseId());
			schedules.remove(schedule);
		}
				
		// Schedule rest
		for (int i = 0; i < schedules.size(); i++) {

			String memoBaseId = schedules.get(i).getMemoBaseId();

			NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
					.setSmallIcon(R.drawable.ic_notification).setContentTitle("Memoling");

			if (memoBaseAdapter != null) {
				builder.setContentText(memoBaseAdapter.get(memoBaseId).getName());
			}

			Intent reviewIntent = new Intent(context, ReviewActivity.class);
			reviewIntent.putExtra(ReviewActivity.MemoBaseId, memoBaseId);
			reviewIntent.putExtra(ReviewActivity.NotificationId, m_notificationId);

			PendingIntent pendigIntent = PendingIntent.getActivity(context, m_notificationId, reviewIntent,
					PendingIntent.FLAG_UPDATE_CURRENT);
			builder.setContentIntent(pendigIntent);

			NotificationManager mNotificationManager = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			mNotificationManager.notify(m_notificationId, builder.build());
			m_notificationId++;
		}

		updateAlarm(context);
	}

	public static void updateAlarm(Context context) {

		ScheduleAdapter adapter = new ScheduleAdapter(context);
		Schedule schedule = adapter.getNextFirstSchedule();
		if (schedule == null) {
			return;
		}

		try {
			Intent timerIntent = new Intent(context, AlarmReceiver.class);
			PendingIntent intent = PendingIntent.getBroadcast(context, 0, timerIntent,
					PendingIntent.FLAG_CANCEL_CURRENT);

			long millsToTask = 0;
			int safeStop = 10;
			for(int i=0;i<safeStop+1;i++) {
				millsToTask = schedule.millsToTask();
				
				if(millsToTask != 0) {
					break;
				}
				
				Thread.sleep(1000);
				
				if(i == safeStop) {
					throw new RuntimeException("Memoling - sheduler got in wierd state");
				}
			}
			
			long due = new Date().getTime() + millsToTask;
			long dueUTC = due + Calendar.getInstance().get(Calendar.ZONE_OFFSET);

			AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			alarmManager.set(AlarmManager.RTC, dueUTC, intent);
		} catch (Exception ex) {
			AppLog.e("Scheduler", "updateAlarm", ex);
		}
	}
}
