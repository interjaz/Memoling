package com.interjaz.ui.view;

import com.interjaz.IGet;
import com.interjaz.Schedule;

public class ScheduleView implements IGet<String> {

	private Schedule m_schedule;
	
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
						sb.append("Mo, ");
						break;
					case 1:
						sb.append("Tu, ");
						break;
					case 2:
						sb.append("We, ");
						break;
					case 3:
						sb.append("Th, ");
						break;
					case 4:
						sb.append("Fr, ");
						break;
					case 5:
						sb.append("Sa, ");
						break;
					case 6:
						sb.append("Su, ");
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
	
	public ScheduleView(Schedule schedule) {
		m_schedule = schedule;
	}

}
