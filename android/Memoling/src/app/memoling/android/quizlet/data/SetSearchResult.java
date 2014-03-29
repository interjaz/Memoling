package app.memoling.android.quizlet.data;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SetSearchResult {

	private int m_totalResults;
	private int m_totalPages;
	private int m_page;
	private int m_imageSetCount;
	private List<QuizletSetHeader> m_sets;
	
	public int getTotalResults() {  return m_totalResults; }
	public void setTotalResults(int totalResults) { m_totalResults = totalResults; }

	public int getTotalPages() {  return m_totalPages; }
	public void setTotalPages(int totalPages) { m_totalPages = totalPages; }

	public int getPage() {  return m_page; }
	public void setPage(int page) { m_page = page; }

	public int getImageSetCount() {  return m_imageSetCount; }
	public void setImageSetCount(int imageSetCount) { m_imageSetCount = imageSetCount; }

	public List<QuizletSetHeader> getSets() {  return m_sets; }
	public void setSets(ArrayList<QuizletSetHeader> sets) { m_sets = sets; }


	/**
	 * This is Quizlet specific serialization
	 */
	public String serialize() throws JSONException {
		JSONObject json = new JSONObject();

		json.put("total_results", m_totalResults);
		json.put("total_pages", m_totalPages);
		json.put("page", m_page);
		json.put("image_set_count", m_imageSetCount);
		
		JSONArray array = new JSONArray();
		for(int i=0;i<m_sets.size();i++) {
			array.put(m_sets.get(i).serialize());
		}
		
		json.put("sets", array);

		return json.toString();
	}

	/**
	 * This is Quizlet specific deserialization
	 */
	public SetSearchResult deserialize(JSONObject json) throws JSONException {

		m_totalResults = json.getInt("total_results");
		m_totalPages = json.getInt("total_pages");
		m_page = json.getInt("page");
		m_imageSetCount = json.getInt("image_set_count");
		JSONArray array = json.getJSONArray("sets");
		m_sets = new ArrayList<QuizletSetHeader>();
		for(int i=0;i<array.length();i++) {
			m_sets.add(new QuizletSetHeader().deserialize(array.getJSONObject(i)));
		}

		return this;
	}
}
