package app.memoling.android.ui.view;

import java.text.DateFormatSymbols;

import android.annotation.SuppressLint;
import android.content.Context;
import app.memoling.android.entity.Schedule;
import app.memoling.android.ui.adapter.IGet;

public class ScheduleView implements IGet<String> {

	private Schedule m_schedule;
	private static String[] m_days = new DateFormatSymbols().getShortWeekdays();
	
	public Schedule getSchedule() { 
		return m_schedule;
	}
	
	@SuppressLint("DefaultLocale")
	@Override
	public String get(int index) {
		
		if(index == 0) {
			return String.format("%02d:%02d", m_schedule.getHours(), m_schedule.getMinutes());
		}
		else {
			StringBuilder sb = new StringBuilder();
			boolean[] days = m_schedule.getDays();
			int daysInWeek = 7;
			for(int i=0;i<daysInWeek;i++) {
				if(days[i]) {
					// Sunday corresponds to one, Monday to two, in application 0 is Monday
					int day = i+2;
					day = day == 8 ? 1 : day;
					sb.append(m_days[day] + ", ");
				}
			}
			// Remove ', '
			if(sb.length() > 0) {
				sb.setLength(sb.length()-2);
			}
			
			return sb.toString();
		}
		
	}
	
	public ScheduleView(Schedule schedule, Context context) {
		m_schedule = schedule;
	}

}
