package app.memoling.android.anki.entity;

import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AnkiDeckConfiguration {

	// JSON fields
	
	// JSON field : name 
	private String name;
	
	// JSON field : replayq
	private boolean replayq;
	
	// JSON field : lapse
	private AnkiLapseOptions lapseOptions;
	
	// JSON field : rev
	private AnkiReviewOptions reviewOptions;
	
	// JSON field : timer
	private int timer;
	
	// JSON field : dyn
	private boolean dynamic;
	
	// JSON field : maxTaken
	private int maxTaken;
	
	// JSON field : usn
	private int universalSerialNumber;
	
	// JSON field : new
	private AnkiNewOptions newOptions;
	
	// JSON field : autoplay
	private boolean autoplay;
	
	// JSON field : id
	private Date id;
	
	// JSON field : mod
	private Date lastModification;
	
	private class AnkiLapseOptions {
		// JSON fields
		
		// JSON field : leechFails
		private int leechFails;
		
		// JSON field : minInt
		private int minInterval;
		
		// JSON field : leechAction
		private int leechAction;
		
		// JSON field : delays
		private int[] delays;
		
		// JSON field : mult
		private double mult;

		public int getLeechFails() {
			return leechFails;
		}

		public void setLeechFails(int leechFails) {
			this.leechFails = leechFails;
		}

		public int getMinInterval() {
			return minInterval;
		}

		public void setMinInterval(int minInterval) {
			this.minInterval = minInterval;
		}

		public int getLeechAction() {
			return leechAction;
		}

		public void setLeechAction(int leechAction) {
			this.leechAction = leechAction;
		}

		public int[] getDelays() {
			return delays;
		}

		public void setDelays(int[] delays) {
			this.delays = delays;
		}

		public double getMult() {
			return mult;
		}

		public void setMult(double mult) {
			this.mult = mult;
		}
	}
			
	public AnkiDeckConfiguration deserialize(JSONObject newObject) {
		// TODO Auto-generated method stub
		return null;
	}

	private class AnkiReviewOptions {
		// JSON fields
		
		// JSON field : perDay
		private int perDay;
		
		// JSON field : ivlFct
		private double intervalFactor;
		
		// JSON field : maxIvl
		private int maxInterval;
		
		// JSON field : minSpace
		private int minSpace;
		
		// JSON field : ease4
		private double ease4;
		
		// JSON field : bury
		private boolean bury;
		
		// JSON field : fuzz
		private double fuzz;

		public int getPerDay() {
			return perDay;
		}

		public void setPerDay(int perDay) {
			this.perDay = perDay;
		}

		public double getIntervalFactor() {
			return intervalFactor;
		}

		public void setIntervalFactor(double intervalFactor) {
			this.intervalFactor = intervalFactor;
		}

		public int getMaxInterval() {
			return maxInterval;
		}

		public void setMaxInterval(int maxInterval) {
			this.maxInterval = maxInterval;
		}

		public int getMinSpace() {
			return minSpace;
		}

		public void setMinSpace(int minSpace) {
			this.minSpace = minSpace;
		}

		public double getEase4() {
			return ease4;
		}

		public void setEase4(double ease4) {
			this.ease4 = ease4;
		}

		public boolean isBury() {
			return bury;
		}

		public void setBury(boolean bury) {
			this.bury = bury;
		}

		public double getFuzz() {
			return fuzz;
		}

		public void setFuzz(double fuzz) {
			this.fuzz = fuzz;
		}
		
		public String serialize() throws JSONException {
			JSONObject json = new JSONObject();
			
			json.put("perDay", perDay);
			json.put("intervalFactor", intervalFactor);
			json.put("maxInterval", maxInterval);
			json.put("minSpace", minSpace);
			json.put("ease4", ease4);
			json.put("bury", bury);
			json.put("fuzz", fuzz);
			
			return json.toString();
		}
		
		public AnkiReviewOptions deserialize(JSONObject json) throws JSONException {
			
			perDay = json.getInt("perDay");
			intervalFactor = json.getDouble("intervalFactor");
			maxInterval = json.getInt("maxInterval");
			minSpace = json.getInt("minSpace");
			ease4 = json.getDouble("ease4");
			bury = json.getBoolean("bury");
			fuzz = json.getDouble("fuzz");
			
			return this;
		}
	}
	
	private class AnkiNewOptions {
		// JSON fields
		
		// JSON field : perDay
		private int perDay;
		
		// JSON field : delays
		private int[] delays;
		
		// JSON field : separate
		private boolean separate;
		
		// JSON field : ints
		private int[] ints;
		
		// JSON field : initialFactor
		private int initialFactor;
		
		// JSON field : bury
		private boolean bury;
		
		// JSON field : order
		private int order;

		public int getPerDay() {
			return perDay;
		}

		public void setPerDay(int perDay) {
			this.perDay = perDay;
		}

		public int[] getDelays() {
			return delays;
		}

		public void setDelays(int[] delays) {
			this.delays = delays;
		}

		public boolean isSeparate() {
			return separate;
		}

		public void setSeparate(boolean separate) {
			this.separate = separate;
		}

		public int[] getInts() {
			return ints;
		}

		public void setInts(int[] ints) {
			this.ints = ints;
		}

		public int getInitialFactor() {
			return initialFactor;
		}

		public void setInitialFactor(int initialFactor) {
			this.initialFactor = initialFactor;
		}

		public boolean isBury() {
			return bury;
		}

		public void setBury(boolean bury) {
			this.bury = bury;
		}

		public int getOrder() {
			return order;
		}

		public void setOrder(int order) {
			this.order = order;
		}
		
		public String serialize() throws JSONException {
			JSONObject json = new JSONObject();
			
			json.put("perDay", perDay);
			
			if(delays != null) {
				JSONArray array = new JSONArray();
				for(int i : delays) {
					array.put(delays[i]);
				}
				json.put("delays", array);
			}
			
			json.put("separate", separate);
			
			if(ints != null) {
				JSONArray array = new JSONArray();
				for(int i : ints) {
					array.put(ints[i]);
				}
				json.put("ints", array);
			}
				
			json.put("initialFactor", initialFactor);
			json.put("bury", bury);
			json.put("order", order);
			
			return json.toString();
		}
		
		public AnkiNewOptions deserialize(JSONObject json) throws JSONException {
			
			perDay = json.getInt("perDay");
			
			if(json.has("delays")) {
				JSONArray array = json.getJSONArray("delays");
				delays = new int[array.length()];
				for(int i=0;i<array.length();i++) {
					delays[i] = array.getInt(i);
				}
			}
			
			separate = json.getBoolean("separate"); 

			if(json.has("ints")) {
				JSONArray array = json.getJSONArray("ints");
				ints = new int[array.length()];
				for(int i=0;i<array.length();i++) {
					ints[i] = array.getInt(i);
				}
			}
			
			initialFactor = json.getInt("initialFactor");
			bury = json.getBoolean("bury");
			order = json.getInt("order");
			
			return this;
		}
	}
}
