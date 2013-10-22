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
		int i = 0;
		
		add("1112bb89-9a19-4f44-af85-45fa2113e739", strGenres[i++]);
		add("1b2d882f-05d8-4722-ab07-7eb3314f6825", strGenres[i++]);
		add("1c2ecf59-fe83-49da-bcf7-f2ba8dcf7f28", strGenres[i++]);
		add("31220b2f-5e69-4c45-a825-9c9ce01ae903", strGenres[i++]);
		add("5299cda6-549f-4fbe-b3be-dbefc52cda1d", strGenres[i++]);
		add("5aaf1226-754e-4f5b-8212-4520b597ab93", strGenres[i++]);
		add("5e4d61c6-f2f8-49f5-b107-4d2b860a2d33", strGenres[i++]);
		add("73bb6c6a-cb78-4f3f-ae0e-4f8f0bbea0f6", strGenres[i++]);
		add("baa8e680-afb0-4894-a972-cca05c4e916e", strGenres[i++]);
		add("c5980cc0-0c52-47a0-a0f4-e248004c797e", strGenres[i++]);
		add("ef6e7e6c-2173-463a-9e38-414dd1260d85", strGenres[i++]);
		add("f431839e-db72-4473-bddc-a9a3fffbc2ae", strGenres[i++]);
		
	}
	
	public MemoBaseGenreAdapter(Context context, boolean persistant) {
		this(context);
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
