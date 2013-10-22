package app.memoling.android.quizlet.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class QuizletSetHeader {

	private int m_id;
	private String m_url;
	private String m_title;
	private String m_createdBy;
	private int m_termCount;
	private int m_createdDate;
	private int m_modifiedDate;
	private boolean m_hasImages;
	private String[] m_subjects;
	private String m_visibility;
	private String m_editable;
	private boolean m_hasAccess;
	private String m_description;
	
	public int getId() {  return m_id; }
	public void setId(int id) { m_id = id; }

	public String getUrl() {  return m_url; }
	public void setUrl(String url) { m_url = url; }

	public String getTitle() {  return m_title; }
	public void setTitle(String title) { m_title = title; }

	public String getCreatedBy() {  return m_createdBy; }
	public void setCreatedBy(String createdBy) { m_createdBy = createdBy; }

	public int getTermCount() {  return m_termCount; }
	public void setTermCount(int termCount) { m_termCount = termCount; }

	public int getCreatedDate() {  return m_createdDate; }
	public void setCreatedDate(int createdDate) { m_createdDate = createdDate; }

	public int getModifiedDate() {  return m_modifiedDate; }
	public void setModifiedDate(int modifiedDate) { m_modifiedDate = modifiedDate; }

	public boolean getHasImages() {  return m_hasImages; }
	public void setHasImages(boolean hasImages) { m_hasImages = hasImages; }

	public String[] getSubjects() {  return m_subjects; }
	public void setSubjects(String[] subjects) { m_subjects = subjects; }

	public String getVisibility() {  return m_visibility; }
	public void setVisibility(String visibility) { m_visibility = visibility; }

	public String getEditable() {  return m_editable; }
	public void setEditable(String editable) { m_editable = editable; }

	public boolean getHasAccess() {  return m_hasAccess; }
	public void setHasAccess(boolean hasAccess) { m_hasAccess = hasAccess; }

	public String getDescription() {  return m_description; }
	public void setDescription(String description) { m_description = description; }

	/**
	 * This is Quizlet specific serialization
	 */
	public String serialize() throws JSONException {
		JSONObject json = new JSONObject();

		json.put("id", m_id);
		json.put("url", m_url);
		json.put("title", m_title);
		json.put("created_by", m_createdBy);
		json.put("term_count", m_termCount);
		json.put("created_date", m_createdDate);
		json.put("modified_date", m_modifiedDate);
		json.put("has_images", m_hasImages);
		
		JSONArray array = new JSONArray();
		for(int i=0;i<m_subjects.length;i++) {
			array.put(m_subjects[i]);
		}
		
		json.put("subjects", array);
		json.put("visibility", m_visibility);
		json.put("editable", m_editable);
		json.put("has_access", m_hasAccess);
		json.put("description", m_description);

		return json.toString();
	}

	/**
	 * This is Quizlet specific deserialization
	 */
	public QuizletSetHeader deserialize(JSONObject json) throws JSONException {

		m_id = json.getInt("id");
		m_url = json.getString("url");
		m_title = json.getString("title");
		m_createdBy = json.getString("created_by");
		m_termCount = json.getInt("term_count");
		m_createdDate = json.getInt("created_date");
		m_modifiedDate = json.getInt("modified_date");
		m_hasImages = json.getBoolean("has_images");
		
		JSONArray array = json.getJSONArray("subjects");
		m_subjects = new String[array.length()];
		for(int i=0;i<array.length();i++) {
			m_subjects[i] = array.getString(i);
		}
		
		m_visibility = json.getString("visibility");
		m_editable = json.getString("editable");
		m_hasAccess = json.getBoolean("has_access");
		m_description = json.getString("description");

		return this;
	}

}
