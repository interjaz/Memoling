package app.memoling.android.anki.entity;

import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AnkiConfiguration {

	// JSON fields
	
	// JSON field : nextPos
	private int m_nextPos;
	
	// JSON field : estTimes
	private boolean m_estTimes;
	
	// JSON field : activeDecks
	private Date[] m_activeDecks;
	
	// JSON field : sortType
	private String m_sortType;
	
	// JSON field : timeLim
	private int m_timeLimit;
	
	// JSON field : sortBackwards
	private int m_sortBackwards;
	
	// JSON field : addToCur
	private boolean m_addToCur;
	
	// JSON field : curDeck
	private Date m_curDeck; 
	
	// JSON field : newBury
	private boolean m_newBury;
	
	// JSON field : lastUnburied
	private int m_lastUnburied;
	
	// JSON field : collapseTime
	private int m_collapseTime;
	
	// JSON field : activeCols
	private String[] m_activeCols;
	
	// JSON field : dueCounts
	private boolean m_dueCounts;
	
	// JSON field : curModel
	private Date m_curModel;
	
	// JSON field : newSpread
	private int m_newSpread;

	public int getNextPos() {  return m_nextPos; }

	public void setNextPos(int nextPos) { m_nextPos = nextPos; }

	public boolean getEstTimes() {  return m_estTimes; }

	public void setEstTimes(boolean estTimes) { m_estTimes = estTimes; }

	public Date[] getActiveDecks() {  return m_activeDecks; }

	public void setActiveDecks(Date[] activeDecks) { m_activeDecks = activeDecks; }

	public String getSortType() {  return m_sortType; }

	public void setSortType(String sortType) { m_sortType = sortType; }

	public int getTimeLimit() {  return m_timeLimit; }

	public void setTimeLimit(int timeLimit) { m_timeLimit = timeLimit; }

	public int getSortBackwards() {  return m_sortBackwards; }

	public void setSortBackwards(int sortBackwards) { m_sortBackwards = sortBackwards; }

	public boolean getAddToCur() {  return m_addToCur; }

	public void setAddToCur(boolean addTocur) { m_addToCur = addTocur; }

	public Date getCurDeck() {  return m_curDeck; }

	public void setCurDeck(Date curDeck) { m_curDeck = curDeck; }

	public boolean getNewBury() {  return m_newBury; }
	
	public void setNewBury(boolean newBury) { m_newBury = newBury; }

	public int getLastUnburied() {  return m_lastUnburied; }
	
	public void setLastUnburied(int lastUnburied) { m_lastUnburied = lastUnburied; }
	
	public int getCollapseTime() {  return m_collapseTime; }

	public void setCollapseTime(int collapseTime) { m_collapseTime = collapseTime; }

	public String[] getActiveCols() {  return m_activeCols; }
	
	public void setActiveCols(String[] activeCols) { m_activeCols = activeCols; }
	
	public boolean isDueCounts() { return m_dueCounts; }

	public void setDueCounts(boolean m_dueCounts) {	this.m_dueCounts = m_dueCounts; }

	public Date getCurModel() {  return m_curModel; }

	public void setCurModel(Date curModel) { m_curModel = curModel; }

	public int getNewSpread() {  return m_newSpread; }

	public void setNewSpread(int newSpread) { m_newSpread = newSpread; }
	
	public String serialize() throws JSONException {
		JSONObject json = new JSONObject();

		json.put("nextPos", m_nextPos);
		json.put("estTimes", m_estTimes);
		
		if(m_activeDecks != null) {
			JSONArray array = new JSONArray();
			for (int i = 0; i < m_activeDecks.length; i++) {
				array.put(m_activeDecks[i].getTime());
			}
			json.put("activeDecks", array);
		}
		
		json.put("sortType", m_sortType);
		json.put("timeLim", m_timeLimit);
		json.put("sortBackwards", m_sortBackwards);
		json.put("addToCur", m_addToCur);
		json.put("curDeck", m_curDeck.getTime());
		json.put("newBury", m_newBury);
		json.put("lastUnburied", m_lastUnburied);
		json.put("collapseTime", m_collapseTime);
		
		if(m_activeCols != null) {
			JSONArray array = new JSONArray();
			for (int i = 0; i < m_activeCols.length; i++) {
				array.put(m_activeCols[i]);
			}
			json.put("activeCols", array);
		}
		
		json.put("dueCounts", m_dueCounts);
		json.put("curModel", m_curModel.getTime());
		json.put("newSpread", m_newSpread);

		return json.toString();
	}

	public AnkiConfiguration deserialize(JSONObject json) throws JSONException {

		m_nextPos = json.getInt("nextPos");
		m_estTimes = json.getBoolean("estTimes");
		
		if(json.has("activeDecks")) {
			JSONArray array = json.getJSONArray("activeDecks");
			m_activeDecks = new Date[array.length()];
			for(int i=0;i<array.length();i++) {
				m_activeDecks[i] = new Date(array.getLong(i));
			}
		}
		
		m_sortType = json.getString("sortType");
		m_timeLimit = json.getInt("timeLim");
		m_sortBackwards = json.getInt("sortBackwards");
		m_addToCur = json.getBoolean("addToCur");
		m_curDeck = new Date(json.getLong("curDeck"));
		m_newBury = json.getBoolean("newBury");
		m_lastUnburied = json.getInt("lastUnburied");
		m_collapseTime = json.getInt("collapseTime");
		
		if(json.has("activeCols")) {
			JSONArray array = json.getJSONArray("activeCols");
			m_activeCols = new String[array.length()];
			for(int i=0;i<array.length();i++) {
				m_activeCols[i] = array.getString(i);
			}
		}
		
		m_dueCounts = json.getBoolean("dueCounts");
		m_curModel = new Date(json.getLong("curModel"));
		m_newSpread = json.getInt("newSpread");

		return this;
	}
}