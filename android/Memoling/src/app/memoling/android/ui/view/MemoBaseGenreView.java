package app.memoling.android.ui.view;

import java.util.ArrayList;
import java.util.List;

import app.memoling.android.entity.MemoBaseGenre;
import app.memoling.android.ui.adapter.IGet;

public class MemoBaseGenreView implements IGet<String> {

	private static MemoBaseGenreView m_empty;
	
	private MemoBaseGenre m_genre;
	private String m_name;
	
	@Override
	public String get(int index) {
		return m_name;
	}
	
	public MemoBaseGenreView() {
		
	}
	
	public MemoBaseGenreView(MemoBaseGenre genre) {
		m_genre = genre;
		m_name = genre.getGenre();
	}
	
	public MemoBaseGenre getGenre() {
		return m_genre;
	}
	
	public void setGenre(MemoBaseGenre genre) {
		m_genre = genre;
	}

	public static List<MemoBaseGenreView> getAll(List<MemoBaseGenre> genres) {
		List<MemoBaseGenreView> views = new ArrayList<MemoBaseGenreView>();
		
		for(MemoBaseGenre genre : genres) {
			views.add(new MemoBaseGenreView(genre));
		}
		
		return views;
	}
	
	public static final MemoBaseGenreView empty() {
		if(m_empty != null) {
			return m_empty;
		}
		
		m_empty = new MemoBaseGenreView();
		m_empty.m_name = "";
		
		return m_empty;
	}
	
}
