package app.memoling.android.ui.view;

import java.util.ArrayList;
import java.util.List;

import app.memoling.android.entity.Memo;
import app.memoling.android.ui.adapter.IGet;

public class MemoPreviewView implements IGet<String> {

	private Memo m_memo;

	public MemoPreviewView(Memo memo) {
		m_memo = memo;
	}

	@Override
	public String get(int index) {
		switch (index) {
		case 0:
			return m_memo.getWordA().getWord();
		case 1:
			return m_memo.getWordB().getWord();
		case 2:
			return m_memo.getWordA().getLanguage().toString();
		case 3:
			return m_memo.getWordB().getLanguage().toString();
		}

		return null;
	}

	public static List<MemoPreviewView> getAll(List<Memo> memos) {
		List<MemoPreviewView> list = new ArrayList<MemoPreviewView>();

		for (Memo memo : memos) {
			list.add(new MemoPreviewView(memo));
		}

		return list;
	}

}
