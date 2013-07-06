package app.memoling.android.ui.view;

import android.content.Context;
import app.memoling.android.R;
import app.memoling.android.entity.Schedule;
import app.memoling.android.ui.adapter.IGet;

public class ScheduleView implements IGet<String> {

	private Schedule m_schedule;
	private Context m_context;
	
	public Schedule getSchedule() { 
		return m_schedule;
	}
	
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
					switch(i){
					case 0:
						sb.append(m_context.getString(R.string.schedule_monday_l2) + ", ");
						break;
					case 1:
						sb.append(m_context.getString(R.string.schedule_tuesday_l2) + ", ");
						break;
					case 2:
						sb.append(m_context.getString(R.string.schedule_wednesday_l2) + ", ");
						break;
					case 3:
						sb.append(m_context.getString(R.string.schedule_thursday_l2) + ", ");
						break;
					case 4:
						sb.append(m_context.getString(R.string.schedule_friday_l2) + ", ");
						break;
					case 5:
						sb.append(m_context.getString(R.string.schedule_saturday_l2) + ", ");
						break;
					case 6:
						sb.append(m_context.getString(R.string.schedule_sunday_l2) + ", ");
						break;
					}
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
		m_context = context;
	}

}
