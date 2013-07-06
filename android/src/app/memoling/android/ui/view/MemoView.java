package app.memoling.android.ui.view;

import java.util.ArrayList;

import app.memoling.android.entity.Memo;
import app.memoling.android.helper.DateHelper;
import app.memoling.android.ui.adapter.IActive;
import app.memoling.android.ui.adapter.IGet;

public class MemoView implements IGet<String>, IActive {

	private Memo m_memo;

	public MemoView(Memo memo) {
		m_memo = memo;
	}

	@Override
	public String get(int index) {

		if (index == 0) {
			return m_memo.getWordA().getLanguage().getCode() + "-" + m_memo.getWordB().getLanguage().getCode();
		} else if (index == 1) {
			return DateHelper.toUiDate(m_memo.getCreated());
		} else if (index == 2) {
			return DateHelper.toUiDate(m_memo.getLastReviewed());
		} else if (index == 3) {
			return m_memo.getWordA().getWord();
		} else if (index == 4) {
			return m_memo.getWordB().getWord();
		}

		return "";
	}

	public static ArrayList<MemoView> getAll(ArrayList<Memo> memos) {
		ArrayList<MemoView> memoViews = new ArrayList<MemoView>();

		for (Memo memo : memos) {
			memoViews.add(new MemoView(memo));
		}

		return memoViews;
	}

	public Memo getMemo() {
		return m_memo;
	}

	@Override
	public boolean isActive() {
		return m_memo.getActive();
	}
}
