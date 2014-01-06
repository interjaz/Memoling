package app.memoling.android.helper;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;

public class NotificationHelper {

	private int m_icon;
	private String m_text;
	private Context m_context;
	private String m_title;
	private PendingIntent m_intent;
	
	public Notification createNotification(Context context, int icon, String title, String text, PendingIntent intent) {
		
		m_context = context;
		m_icon = icon;
		m_text = text;
		m_intent = intent;
		m_title = title;
		
		if(Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
			return createNotificaiton_v11();
		} else {
			return createNotification_v10();
		}
	}
	
	@SuppressWarnings("deprecation")
	private Notification createNotification_v10() {

		Notification notification = new Notification();
		notification.icon = m_icon;
		notification.tickerText = m_title;;
		notification.setLatestEventInfo(m_context, m_title, m_text, m_intent);
		
		return notification;
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private Notification createNotificaiton_v11() {
		
		return new Notification.Builder(m_context)
		.setSmallIcon(m_icon)
		.setContentTitle(m_title)
		.setContentText(m_text)
		.setContentIntent(m_intent)
		.setTicker(m_text).build();
		
	}
	
}
