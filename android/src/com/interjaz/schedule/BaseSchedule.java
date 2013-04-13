package com.interjaz.schedule;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.interjaz.Schedule;

public class BaseSchedule {

	private String m_memoBaseId;

	public String getMemoBaseId() {
		return m_memoBaseId;
	}

	public void setMemoBaseId(String memoBaseId) {
		m_memoBaseId = memoBaseId;
	}

	private ArrayList<Schedule> m_schedule;

	public ArrayList<Schedule> getSchedule() {
		return m_schedule;
	}

	public void setSchedule(ArrayList<Schedule> schedule) {
		m_schedule = schedule;
	}

	public BaseSchedule() {

	}

	public BaseSchedule(String memoBaseId, ArrayList<Schedule> schedule) {
		m_memoBaseId = memoBaseId;
		m_schedule = schedule;
	}

	public String serialize() {
		JSONObject json = new JSONObject();
		JSONArray jsonArray = new JSONArray();

		try {
			json.put("MemoBaseId", m_memoBaseId);

			for (int i = 0; i < m_schedule.size(); i++) {
				jsonArray.put(m_schedule.get(i).serialize());
			}

			json.put("Schedule", jsonArray);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return json.toString();
	}

	public static BaseSchedule deserialize(String data) {
		BaseSchedule base = new BaseSchedule();

		try {
			JSONObject json = new JSONObject(data);
			JSONArray jsonArray = json.getJSONArray("Schedule");
			ArrayList<Schedule> schedule = new ArrayList<Schedule>();

			base.setMemoBaseId(json.getString("MemoBaseId"));

			for (int i = 0; i < jsonArray.length(); i++) {
				schedule.add(Schedule.deserialize(jsonArray.getString(i)));
			}

			base.setSchedule(schedule);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return base;
	}

	public static String serializeList(ArrayList<BaseSchedule> list) {
		JSONObject json = new JSONObject();
		JSONArray jsonArray = new JSONArray();

		for (int i = 0; i < list.size(); i++) {
			jsonArray.put(list.get(i).serialize());
		}
		try {
			json.put("List", jsonArray);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return json.toString();
	}

	public static ArrayList<BaseSchedule> deserializeList(String data) {
		ArrayList<BaseSchedule> list = new ArrayList<BaseSchedule>();

		try {
			JSONObject json = new JSONObject(data);
			JSONArray jsonArray = json.getJSONArray("List");

			for (int i = 0; i < jsonArray.length(); i++) {
				list.add(BaseSchedule.deserialize(jsonArray.getString(i)));
			}
		} catch (Exception ex) {
			ex.toString();
		}

		return list;
	}
}
