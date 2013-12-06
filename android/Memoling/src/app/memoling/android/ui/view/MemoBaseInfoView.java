package app.memoling.android.ui.view;

import java.util.ArrayList;

import app.memoling.android.entity.Language;
import app.memoling.android.entity.MemoBaseInfo;
import app.memoling.android.helper.DateHelper;
import app.memoling.android.helper.Helper;
import app.memoling.android.ui.adapter.IActive;
import app.memoling.android.ui.adapter.IGet;

public class MemoBaseInfoView implements IGet<String>, IActive {

	private MemoBaseInfo m_memoBaseInfo;

	public MemoBaseInfoView(MemoBaseInfo memoBaseInfo) {
		m_memoBaseInfo = memoBaseInfo;
	}

	@Override
	public String get(int index) {

		switch (index) {
		case 0:
			return m_memoBaseInfo.getMemoBase().getName();
		case 1:
			return Integer.valueOf(m_memoBaseInfo.getNoAllMemos()).toString();
		case 2:
			return languagesToString();
		default:
			return "";
		}

	}
	
	private String languagesToString() 
	{
		StringBuilder sb = new StringBuilder();

		for(Language lang : m_memoBaseInfo.getLanguages()) {
			sb.append(lang);
			sb.append(", ");
		}
		
		if(sb.length() > 0) {
			sb.setLength(sb.length()-2);
		}
		
		return sb.toString();
	}

	public static ArrayList<MemoBaseInfoView> getAll(ArrayList<MemoBaseInfo> memoBaseInfos) {
		ArrayList<MemoBaseInfoView> views = new ArrayList<MemoBaseInfoView>();

		for (MemoBaseInfo memoBaseInfo : memoBaseInfos) {
			views.add(new MemoBaseInfoView(memoBaseInfo));
		}

		return views;
	}

	public MemoBaseInfo getMemoBaseInfo() {
		return m_memoBaseInfo;
	}

	@Override
	public boolean isActive() {
		return m_memoBaseInfo.getMemoBase().getActive();
	}
}
