package app.memoling.android.ui.view;

import java.util.ArrayList;

import android.graphics.Paint;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import app.memoling.android.R;
import app.memoling.android.entity.Memo;
import app.memoling.android.ui.ResourceManager;
import app.memoling.android.ui.adapter.IActive;
import app.memoling.android.ui.adapter.IInject;

public class MemoView implements IInject {

	private Memo m_memo;

	public MemoView(Memo memo) {
		m_memo = memo;
	}

	@Override
	public void injcet(View view, ResourceManager resources) {
		View progressBar = view.findViewById(R.id.memolist_listview_progressBar);
		View progressReminder = view.findViewById(R.id.memolist_listview_progressReminder);
		TextView original = (TextView)view.findViewById(R.id.memolist_listview_txtOriginal);
		TextView translate = (TextView)view.findViewById(R.id.memolist_listview_txtTranslate);
		TextView language = (TextView)view.findViewById(R.id.memolist_listview_txtLanguages);
		
		resources.setFont(original, resources.getThinFont());
		resources.setFont(translate, resources.getThinFont());
		resources.setFont(language, resources.getThinFont());
		
		int correct = m_memo.getCorrectAnsweredWordA() + m_memo.getCorrectAnsweredWordB();
		int incorrect = m_memo.getDisplayed() - correct;

		setProgressWeight(progressBar, correct);
		setProgressWeight(progressReminder, incorrect);
		
		original.setText(m_memo.getWordA().getWord());
		translate.setText(m_memo.getWordB().getWord());
		language.setText(m_memo.getWordA().getLanguage().getCode() + "\n" + m_memo.getWordB().getLanguage().getCode());
		
		if (!m_memo.getActive()) {
			original.setPaintFlags(original.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
			translate.setPaintFlags(translate.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
			language.setPaintFlags(language.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
		} else {
			original.setPaintFlags(original.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
			translate.setPaintFlags(translate.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
			language.setPaintFlags(language.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
		}
	}
	
	private void setProgressWeight(View view, int weight) {
		view.setLayoutParams(new LinearLayout.LayoutParams(0,
				LayoutParams.MATCH_PARENT, weight));
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
}
