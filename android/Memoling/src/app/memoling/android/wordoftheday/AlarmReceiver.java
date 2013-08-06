package app.memoling.android.wordoftheday;

import java.util.Calendar;
import java.util.Date;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import app.memoling.android.helper.AppLog;
import app.memoling.android.preference.Preferences;
import app.memoling.android.preference.custom.WordOfTheDayTime;

public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			onReceiveBoot(context, intent);
		} else {
			onReceiveAlarm(context, intent);
		}

		updateAlarm(context);
	}

	private void onReceiveBoot(Context context, Intent intent) {
	}

	private void onReceiveAlarm(Context context, Intent intent) {
		Intent serviceIntent = new Intent(context, DispatcherService.class);
		context.startService(serviceIntent);
	}

	// Run everyday on preferred hour and minutes
	public static void updateAlarm(Context context) {

		Preferences pref = new Preferences(context);
		WordOfTheDayTime prefTime = pref.getWordOfTheDayTime();
		int prefHour = prefTime.getHours();
		int prefMinutes = prefTime.getMinutes();

		updateAlarm(context, prefHour, prefMinutes);
	}

	// Run everyday on preferred hour and minutes
	public static void updateAlarm(Context context, int hours, int minutes) {

		// Current date localTime
		Calendar localTime = Calendar.getInstance();
		int localHour = localTime.get(Calendar.HOUR_OF_DAY);
		int localMinutes = localTime.get(Calendar.MINUTE);
		int localSeconds = localTime.get(Calendar.SECOND);

		long schedule = 1000L * ((60L * 60 * (hours - localHour) + (60L * (minutes - localMinutes))) - localSeconds);
		if (schedule < 0) {
			// Schedule tomorrow
			schedule += 1000 * 24 * 60 * 60L; // Add day
		}

		// Delay to remove multiple notifications problem
		try {
			Thread.sleep(1000);
		} catch (InterruptedException ex) {
			// Should not happen
		}

		try {
			Intent timerIntent = new Intent(context, AlarmReceiver.class);
			PendingIntent intent = PendingIntent.getBroadcast(context, 0, timerIntent,
					PendingIntent.FLAG_CANCEL_CURRENT);

			long due = new Date().getTime() + schedule;
			long dueUTC = due + Calendar.getInstance().get(Calendar.ZONE_OFFSET);

			AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			alarmManager.set(AlarmManager.RTC, dueUTC, intent);
		} catch (Exception ex) {
			AppLog.e("AlarmReceiver", "updateAlarm", ex);
		}
	}
}
