package app.memoling.android.ui.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import app.memoling.android.R;
import app.memoling.android.ui.adapter.IGet;

public class GamesSourceView implements IGet<String> {

	private int m_value;
	private String m_string;
	
	@Override
	public String get(int index) {
		return m_string;
	}
	
	public int getValue() {
		return m_value;
	}

	public GamesSourceView(int value, String str) {
		m_string = str;
		m_value = value;
	}
	
	public static List<GamesSourceView> getSourceViewsWithMemos(Context ctx) {
		String[] input = ctx.getResources().getStringArray(R.array.games_source);
		List<GamesSourceView> views = new ArrayList<GamesSourceView>();
		
		for(int i=0;i<input.length;i++) {
			views.add(new GamesSourceView(i, input[i]));
		}
		
		return views;
	}
	
	public static List<GamesSourceView> getSourceViews(Context ctx) {
		String[] input = ctx.getResources().getStringArray(R.array.games_source);
		List<GamesSourceView> views = new ArrayList<GamesSourceView>();
		
		// Here we take assumption that first is my memos
		for(int i=1;i<input.length;i++) {
			views.add(new GamesSourceView(i-1, input[i]));
		}
		
		return views;
	}
	
	
}
