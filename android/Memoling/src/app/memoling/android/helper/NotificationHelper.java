package app.memoling.android.helper;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;

public class NotificationHelper {

	private int m_icon;
	private String m_text;
	private Context m_context;
	private String m_title;
	private String m_info;
	private PendingIntent m_intent;
	
	public Notification createNotification(Context context, int icon, String title, String text, PendingIntent intent) {
		 return createNotification(context, icon, title, text, null, intent);
	}
	
	public Notification createNotification(Context context, int icon, String title, String text, String info, PendingIntent intent) {
		
		m_context = context;
		m_icon = icon;
		m_text = text;
		m_intent = intent;
		m_title = title;
		m_info = info;
		
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			return createNotificaiton_v16();
		} else {
			return createNotification_v10();
		}
	}
	
	public static void cancel(Context context, int notificationId) {
		((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE))
				.cancel(notificationId);
	}
	
	public static void show(Context context, int id, Notification notification) {
		((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(id, notification);
	}
	
	@SuppressWarnings("deprecation")
	private Notification createNotification_v10() {

		Notification notification = new Notification();
		notification.icon = m_icon;
		notification.tickerText = m_title;;
		notification.setLatestEventInfo(m_context, m_title, m_text, m_intent);
		
		return notification;
	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private Notification createNotificaiton_v16() {
		
		Notification.Builder notification = new Notification.Builder(m_context)
		.setSmallIcon(m_icon)
		.setContentTitle(m_title)
		.setContentText(m_text)
		.setContentIntent(m_intent)
		.setTicker(m_text);
		
		if(m_info != null) {
			notification = notification.setContentInfo(m_info);
		}
		
		return notification.build();
		
	}
	
}
