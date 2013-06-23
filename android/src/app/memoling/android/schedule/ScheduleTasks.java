package app.memoling.android.schedule;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ScheduleTasks extends ArrayList<ScheduleTask> {

	private static final long serialVersionUID = 5940621743965110625L;

	private final static String TASK_ARRAY = "task_array";

	public ScheduleTasks() {
	}

	public ScheduleTasks(ArrayList<ScheduleTask> tasks) {
		this.clear();
		for (ScheduleTask task : tasks) {
			this.add(task);
		}
	}

	public String serialize() throws JSONException {
		JSONObject json = new JSONObject();
		JSONArray array = new JSONArray();

		for (ScheduleTask task : this) {
			array.put(task.serialize());
		}
		json.put(TASK_ARRAY, array);

		return json.toString();
	}

	public static ScheduleTasks deserialize(String serialized) throws JSONException {
		JSONObject json = new JSONObject(serialized);
		ScheduleTasks object = new ScheduleTasks();

		JSONArray array = json.getJSONArray(TASK_ARRAY);

		for (int i = 0; i < array.length(); i++) {
			object.add(ScheduleTask.deserialize(array.getString(i)));
		}

		return object;
	}

}
