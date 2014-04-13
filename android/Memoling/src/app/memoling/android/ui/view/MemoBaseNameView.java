package app.memoling.android.ui.view;

import java.util.ArrayList;
import java.util.List;

import app.memoling.android.entity.MemoBase;
import app.memoling.android.ui.adapter.IGet;

public class MemoBaseNameView implements IGet<String> {

	private MemoBase m_memoBase;

	@Override
	public String get(int index) {
		return m_memoBase.getName();
	}

	public MemoBase getMemoBase() {
		return m_memoBase;
	}

	public MemoBaseNameView(MemoBase memoBase) {
		m_memoBase = memoBase;
	}

	public static List<MemoBaseNameView> getAll(List<MemoBase> memoBases) {
		List<MemoBaseNameView> views = new ArrayList<MemoBaseNameView>();
		for (MemoBase memoBase : memoBases) {
			views.add(new MemoBaseNameView(memoBase));
		}

		return views;
	}

}
