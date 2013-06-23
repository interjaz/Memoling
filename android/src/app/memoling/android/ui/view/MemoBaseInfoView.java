package app.memoling.android.ui.view;

import java.util.ArrayList;

import app.memoling.android.IActive;
import app.memoling.android.IGet;
import app.memoling.android.entity.MemoBaseInfo;
import app.memoling.android.helper.DateHelper;

public class MemoBaseInfoView implements IGet<String>, IActive {

	private MemoBaseInfo m_memoBaseInfo;

	public MemoBaseInfoView(MemoBaseInfo memoBaseInfo) {
		m_memoBaseInfo = memoBaseInfo;
	}

	@Override
	public String get(int index) {

		switch (index) {
		case 0:
			return DateHelper.toUiDate(m_memoBaseInfo.getMemoBase().getCreated());
		case 1:

			return Integer.valueOf(m_memoBaseInfo.getNoAllMemos()).toString();
		case 2:
			return m_memoBaseInfo.getMemoBase().getName();
		case 3:
			return m_memoBaseInfo.getMemoBase().getActive() ? "1" : "0";
		default:
			return "";
		}

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
