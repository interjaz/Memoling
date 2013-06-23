package app.memoling.android.schedule;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import app.memoling.android.R;
import app.memoling.android.adapter.MemoBaseAdapter;
import app.memoling.android.ui.activity.ReviewActivity;

public class AlarmReceiver extends BroadcastReceiver {

	private static int m_notificationId = 0;

	@Override
	public void onReceive(Context context, Intent intent) {

		try {
			String taskId = intent.getStringExtra(Scheduler.TasksExtra);
			
			if(taskId == null) {
				onReceiveBoot(context, intent);
			} else {
				onReceiveAlarm(context, intent, taskId);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	
	private void onReceiveBoot(Context context, Intent intent) {
		Scheduler.updateAlarm(context);
	}
	
	private void onReceiveAlarm(Context context, Intent intent, String taskId) throws Exception {
		
		MemoBaseAdapter memoBaseAdapter = null;		
		memoBaseAdapter = new MemoBaseAdapter(context);

		ScheduleTasks tasks = ScheduleTasks.deserialize(taskId);

		for (int i = 0; i < tasks.size(); i++) {

			String memoBaseId = tasks.get(i).memoBaseId;

			NotificationCompat.Builder builder = new NotificationCompat.Builder(context).setSmallIcon(
					R.drawable.ic_notification).setContentTitle("Memoling");

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

		Scheduler.updateAlarm(context);
	}
}
