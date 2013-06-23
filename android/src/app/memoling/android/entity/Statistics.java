package app.memoling.android.entity;

import org.json.JSONException;
import org.json.JSONObject;

import app.memoling.android.Language;

public class Statistics {


	private int m_totalMemos;
	private int m_librariesCount;
	private Language m_mostPopularLanguage;
	private int m_totalRepetitions;
	private double m_averagePerformance;
	private Memo m_mostRepeatedMemo;
	private Memo m_leastRepeatedMemo;
	private Word m_longestWord;
	private Word m_shortestWord;

	public int getTotalMemos() {  return m_totalMemos; }
	public void setTotalMemos(int totalMemos) { m_totalMemos = totalMemos; }

	public int getLibrariesCount() {  return m_librariesCount; }
	public void setLibrariesCount(int librariesCount) { m_librariesCount = librariesCount; }

	public Language getMostPopularLanguage() {  return m_mostPopularLanguage; }
	public void setMostPopularLanguage(Language mostPopularLanguage) { m_mostPopularLanguage = mostPopularLanguage; }

	public int getTotalRepetitions() {  return m_totalRepetitions; }
	public void setTotalRepetitions(int totalRepetitions) { m_totalRepetitions = totalRepetitions; }

	public double getAveragePerformance() {  return m_averagePerformance; }
	public void setAveragePerformance(double averagePerformance) { m_averagePerformance = averagePerformance; }

	public Memo getMostRepeatedMemo() {  return m_mostRepeatedMemo; }
	public void setMostRepeatedMemo(Memo mostRepeatedMemo) { m_mostRepeatedMemo = mostRepeatedMemo; }

	public Memo getLeastRepeatedMemo() {  return m_leastRepeatedMemo; }
	public void setLeastRepeatedMemo(Memo leastRepeatedMemo) { m_leastRepeatedMemo = leastRepeatedMemo; }

	public Word getLongestWord() {  return m_longestWord; }
	public void setLongestWord(Word longestWord) { m_longestWord = longestWord; }

	public Word getShortestWord() {  return m_shortestWord; }
	public void setShortestWord(Word shortestWord) { m_shortestWord = shortestWord; }

	public JSONObject serialize() throws JSONException {
		JSONObject json = new JSONObject();

		json.put("m_totalMemos", m_totalMemos);
		json.put("m_librariesCount", m_librariesCount);
		json.put("m_mostPopularLanguage", m_mostPopularLanguage);
		json.put("m_totalRepetitions", m_totalRepetitions);
		json.put("m_averagePerformance", m_averagePerformance);
		json.put("m_mostRepeatedMemo", m_mostRepeatedMemo.serialize());
		json.put("m_leastRepeatedMemo", m_leastRepeatedMemo.serialize());
		json.put("m_longestWord", m_longestWord.serialize());
		json.put("m_shortestWord", m_shortestWord.serialize());

		return json;
	}

	public Statistics deserialize(JSONObject json) throws JSONException {
		Statistics object = new Statistics();

		m_totalMemos = json.getInt("m_totalMemos");
		m_librariesCount = json.getInt("m_librariesCount");
		m_mostPopularLanguage = Language.parse(json.getString("m_mostPopularLanguage"));
		m_totalRepetitions = json.getInt("m_totalRepetitions");
		m_averagePerformance = json.getDouble("m_averagePerformance");
		m_mostRepeatedMemo = new Memo().deserialize(json.getJSONObject("m_mostRepeatedMemo"));
		m_leastRepeatedMemo = new Memo().deserialize(json.getJSONObject("m_leastRepeatedMemo"));
		m_longestWord = new Word().deserialize(json.getJSONObject("m_longestWord"));
		m_shortestWord = new Word().deserialize(json.getJSONObject("m_shortestWord"));

		return object;
	}
	
}
