package app.memoling.android.adapter;

import java.util.ArrayList;

import android.content.Context;
import app.memoling.android.R;
import app.memoling.android.entity.MemoBaseGenre;

public class MemoBaseGenreAdapter {

	public static ArrayList<MemoBaseGenre> m_genres;
		
	public MemoBaseGenreAdapter(Context context) {
		
		if(m_genres != null) {
			return;
		}
		
		m_genres = new ArrayList<MemoBaseGenre>();

		String[] strGenres = context.getResources().getStringArray(R.array.memobasegenres);
		
		add("1112bb89-9a19-4f44-af85-45fa2113e739", strGenres[0]);
		add("1b2d882f-05d8-4722-ab07-7eb3314f6825", strGenres[1]);
		add("5299cda6-549f-4fbe-b3be-dbefc52cda1d", strGenres[2]);
		add("5aaf1226-754e-4f5b-8212-4520b597ab93", strGenres[3]);
		add("ef6e7e6c-2173-463a-9e38-414dd1260d85", strGenres[4]);
		add("f431839e-db72-4473-bddc-a9a3fffbc2ae", strGenres[5]);
		
	}
	
	private void add(String gid, String str) {
		m_genres.add(new MemoBaseGenre(gid, str));
	}
	
	public ArrayList<MemoBaseGenre> getAll() {
		return m_genres;
	}

	public MemoBaseGenre get(String memoBaseGenreId) {
		for(MemoBaseGenre genre : getAll()) {
			if(genre.getMemoBaseGenreId().equals(memoBaseGenreId)) {
				return genre;
			}
		}
		
		return null;
	}
	
}
