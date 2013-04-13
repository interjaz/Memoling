package com.interjaz;

import org.json.JSONArray;
import org.json.JSONObject;

public class Schedule {

	private int m_hours;
	private int m_minutes;

	private boolean[] m_days = new boolean[7];

	public int getHours() {
		return m_hours;
	}

	public int getMinutes() {
		return m_minutes;
	}

	public boolean[] getDays() {
		return m_days;
	}

	public void setHours(int hours) {
		m_hours = hours;
	}

	public void setMinutes(int minutes) {
		m_minutes = minutes;
	}

	public void setDays(boolean[] days) {
		m_days = days;
	}
	
	public String serialize() {
		JSONObject json = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		try { 
			json.put("Hours", m_hours);
			json.put("Minutes", m_minutes);
			
			for(int i=0;i<m_days.length;i++) {
				jsonArray.put(m_days[i]);
			}
			
			json.put("Days", jsonArray);			
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		
		return json.toString();
	}
	
	public static Schedule deserialize(String data) {
		Schedule schedule = new Schedule();
		try {
			JSONObject json = new JSONObject(data);
			JSONArray jsonArray = json.getJSONArray("Days");
			
			schedule.setHours(json.getInt("Hours"));
			schedule.setMinutes(json.getInt("Minutes"));
			
			boolean days[] = new boolean[7];
			for(int i=0;i<jsonArray.length();i++) {
				days[i] = jsonArray.getBoolean(i);
			}
			
			schedule.setDays(days);
			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
		return schedule;
	}
}
