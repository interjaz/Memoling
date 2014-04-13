package app.memoling.android.anki.entity;

import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AnkiDeck {

	// JSON fields
	
	// JSON field : desc 
	private String m_description;
	
	// JSON field : name
	private String m_name;
	
	// JSON field : extendRev
	private int m_extendRev;
	
	// JSON field : usn
	private int m_universalSerialNumber;
	
	// JSON field : collapsed
	private boolean m_collapsed;
	
	// JSON field : newToday
	private int[] m_newToday;
	
	// JSON field : timeToday;
	private int[] m_timeToday;
	
	// JSON field : dyn
	private int m_dyn;
	
	// JSON field : extendNew
	private int m_extendNew;
	
	// JSON field : conf
	private int m_configuration;
	
	// JSON field : revToday
	private int[] m_reviewToday;
	
	// JSON field : lrnToday
	private int[] m_learnToday;
	
	// JSON field : id
	private Date m_deckId;
	
	// JSON field : mod
	private Date m_lastModification;

	public String getDescription() {  return m_description; }

	public void setDescription(String description) { m_description = description; }

	public String getName() {  return m_name; }
	
	public void setName(String name) { m_name = name; }

	public int getExtendRev() {  return m_extendRev; }

	public void setExtendRev(int extendRev) { m_extendRev = extendRev; }

	public int getUniversalSerialNumber() {  return m_universalSerialNumber; }

	public void setUniversalSerialNumber(int universalSerialNumber) { m_universalSerialNumber = universalSerialNumber; }

	public boolean getCollapsed() {  return m_collapsed; }

	public void setCollapsed(boolean collapsed) { m_collapsed = collapsed; }

	public int[] getNewToday() {  return m_newToday; }

	public void setNewToday(int[] newToday) { m_newToday = newToday; }

	public int[] getTimeToday() {  return m_timeToday; }

	public void setTimeToday(int[] timeToday) { m_timeToday = timeToday; }
	
	public int getDyn() {  return m_dyn; }
	
	public void setDyn(int dyn) { m_dyn = dyn; }

	public int getExtendNew() {  return m_extendNew; }

	public void setExtendNew(int extendNew) { m_extendNew = extendNew; }

	public int getConfiguration() {  return m_configuration; }

	public void setConfiguration(int configuration) { m_configuration = configuration; }

	public int[] getReviewToday() {  return m_reviewToday; }

	public void setReviewToday(int[] reviewToday) { m_reviewToday = reviewToday; }

	public int[] getLearnToday() {  return m_learnToday; }

	public void setLearnToday(int[] learnToday) { m_learnToday = learnToday; }

	public Date getDeckId() {  return m_deckId; }

	public void setDeckId(Date deckId) { m_deckId = deckId; }

	public Date getLastModification() {  return m_lastModification; }

	public void setLastModification(Date lastModification) { m_lastModification = lastModification; }

	public String serialize() throws JSONException {
		JSONObject json = new JSONObject();

		json.put("desc", m_description);
		json.put("name", m_name);
		json.put("extendRev", m_extendRev);
		json.put("usn", m_universalSerialNumber);
		json.put("collapsed", m_collapsed);
		
		if(m_newToday != null) {
			JSONArray array = new JSONArray();
			for(int i : m_newToday) {
				array.put(m_newToday[i]);
			}
			json.put("newToday", array);
		}
		
		if(m_timeToday != null) {
			JSONArray array = new JSONArray();
			for(int i : m_timeToday) {
				array.put(m_timeToday[i]);
			}
			json.put("timeToday", array);
		}
		
		json.put("dyn", m_dyn);
		json.put("extendNew", m_extendNew);
		json.put("conf", m_configuration);
		
		if(m_reviewToday != null) {
			JSONArray array = new JSONArray();
			for(int i : m_reviewToday) {
				array.put(m_reviewToday[i]);
			}
			json.put("reviewToday", array);
		}
		
		if(m_learnToday != null) {
			JSONArray array = new JSONArray();
			for(int i : m_learnToday) {
				array.put(m_learnToday[i]);
			}
			json.put("learnToday", array);
		}
		
		json.put("id", m_deckId.getTime());
		json.put("mod", m_lastModification.getTime());

		return json.toString();
	}
	
	public AnkiDeck deserialize(JSONObject json) throws JSONException {

		m_description = json.getString("desc");
		m_name = json.getString("name");
		m_extendRev = json.getInt("extendRev");
		m_universalSerialNumber = json.getInt("usn");
		m_collapsed = json.getBoolean("collapsed");
		
		if(json.has("newToday")) {
			JSONArray array = json.getJSONArray("newToday");
			m_newToday = new int[array.length()];
			for(int i=0;i<array.length();i++) {
				m_newToday[i] = array.getInt(i);
			}
		}
		
		if(json.has("timeToday")) {
			JSONArray array = json.getJSONArray("timeToday");
			m_timeToday = new int[array.length()];
			for(int i=0;i<array.length();i++) {
				m_timeToday[i] = array.getInt(i);
			}
		}
		
		m_dyn = json.getInt("dyn");
		m_extendNew = json.getInt("extendNew");
		m_configuration = json.getInt("conf");
		
		if(json.has("reviewToday")) {
			JSONArray array = json.getJSONArray("reviewToday");
			m_reviewToday = new int[array.length()];
			for(int i=0;i<array.length();i++) {
				m_reviewToday[i] = array.getInt(i);
			}
		}
	
		if(json.has("learnToday")) {
			JSONArray array = json.getJSONArray("learnToday");
			m_learnToday = new int[array.length()];
			for(int i=0;i<array.length();i++) {
				m_learnToday[i] = array.getInt(i);
			}
		}
		
		m_deckId = new Date(json.getLong("id"));
		m_lastModification = new Date(json.getLong("mod"));
		
		return this;
	}
}
