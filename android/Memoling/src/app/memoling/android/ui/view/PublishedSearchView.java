package app.memoling.android.ui.view;

import java.util.ArrayList;

import app.memoling.android.adapter.MemoBaseGenreAdapter;
import app.memoling.android.entity.PublishedMemoBase;
import app.memoling.android.ui.adapter.IGet;

public class PublishedSearchView implements IGet<String> {

	private PublishedMemoBase m_published;
	
	public PublishedSearchView(PublishedMemoBase published, MemoBaseGenreAdapter adapter) {
		
		m_published = published;
		m_published.setMemoBaseGenre(adapter.get(m_published.getMemoBaseGenreId()));
	}
	
	@Override
	public String get(int index) {
		switch(index) {
		case 0:
			return m_published.getMemoBase().getName();
		case 1:
			return m_published.getMemoBaseGenre().getGenre();
		case 2:
			return m_published.getPrimaryLanguageAIso639().getCode() + "-" + m_published.getPrimaryLanguageBIso639().getCode();
		case 3:
			return Integer.toString(m_published.getMemosCount());
		case 4:
			return Integer.toString(m_published.getDownloads());
		}
		
		return null;
	}

	public PublishedMemoBase getPublishedMemoBase() {
		return m_published;
	}
	
	public static ArrayList<PublishedSearchView> getAll(ArrayList<PublishedMemoBase> headers, MemoBaseGenreAdapter adapter) {
		ArrayList<PublishedSearchView> list = new ArrayList<PublishedSearchView>();
		
		for(PublishedMemoBase header : headers) {
			list.add(new PublishedSearchView(header, adapter));
		}
		
		return list;
	}

}
