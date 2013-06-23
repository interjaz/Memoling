package app.memoling.android.entity;

import org.json.JSONException;
import org.json.JSONObject;

public class MemoBaseGenre {

	private String m_memoBaseGenreId;
	private String m_genre;
	
	public String getMemoBaseGenreId() {  return m_memoBaseGenreId; }
	public void setMemoBaseGenreId(String memoBaseGenreId) { m_memoBaseGenreId = memoBaseGenreId; }

	public String getGenre() {  return m_genre; }
	public void setGenre(String genre) { m_genre = genre; }

	public MemoBaseGenre() {
		
	}
	
	public MemoBaseGenre(String memoBaseGenreId, String genre) {
		m_memoBaseGenreId = memoBaseGenreId;
		m_genre = genre;
	}

	public String serialize() throws JSONException {
		JSONObject json = new JSONObject();

		json.put("m_memoBaseGenreId", m_memoBaseGenreId);
		json.put("m_genre", m_genre);

		return json.toString();
	}

	public MemoBaseGenre deserialize(JSONObject json) throws JSONException {

		m_memoBaseGenreId = json.getString("m_memoBaseGenreId");
		m_genre = json.getString("m_genre");

		return this;
	}

}
