package app.memoling.android.ui.view;

import java.util.ArrayList;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import app.memoling.android.R;
import app.memoling.android.entity.Memo;
import app.memoling.android.ui.ResourceManager;
import app.memoling.android.ui.adapter.IInject;

public class MemoView implements IInject {

	private Memo m_memo;

	public MemoView(Memo memo) {
		m_memo = memo;
	}

	@Override
	public void injcet(View view, ResourceManager resources) {

		ViewHolder holder = (ViewHolder)view.getTag();
		if(view.getTag() == null) {
			holder = new ViewHolder(view, resources);
		}
		
		int correct = m_memo.getCorrectAnsweredWordA() + m_memo.getCorrectAnsweredWordB();
		int incorrect = m_memo.getDisplayed() - correct;
		incorrect = incorrect == 0 ? 1 : incorrect;

		setProgressWeight(holder.m_progressBar, correct);
		setProgressWeight(holder.m_progressReminder, incorrect);
		
		holder.m_original.setText(m_memo.getWordA().getWord());
		holder.m_translate.setText(m_memo.getWordB().getWord());
		holder.m_languageA.setText(m_memo.getWordA().getLanguage().getCode());
		holder.m_languageB.setText(m_memo.getWordB().getLanguage().getCode());
		
		if (!m_memo.getActive()) {
			holder.m_original.setPaintFlags(holder.m_original.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
			holder.m_translate.setPaintFlags(holder.m_translate.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
			holder.m_languageA.setPaintFlags(holder.m_languageA.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
			holder.m_languageB.setPaintFlags(holder.m_languageB.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
		} else {
			holder.m_original.setPaintFlags(holder.m_original.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
			holder.m_translate.setPaintFlags(holder.m_translate.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
			holder.m_languageA.setPaintFlags(holder.m_languageA.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
			holder.m_languageB.setPaintFlags(holder.m_languageB.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
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
	
	
	private static class ViewHolder {
		
		public View m_progressBar;
		public View m_progressReminder;
		public TextView m_original;
		public TextView m_translate;
		public TextView m_languageA;
		public TextView m_languageB;
		
		public ViewHolder(View view, ResourceManager resources) {

			m_progressBar = view.findViewById(R.id.memolist_listview_progressBar);
			m_progressReminder = view.findViewById(R.id.memolist_listview_progressReminder);
			m_original = (TextView)view.findViewById(R.id.memolist_listview_txtOriginal);
			m_translate = (TextView)view.findViewById(R.id.memolist_listview_txtTranslate);
			m_languageA = (TextView)view.findViewById(R.id.memolist_listview_txtLanguageA);
			m_languageB = (TextView)view.findViewById(R.id.memolist_listview_txtLanguageB);

			Typeface thinFont = resources.getLightFont();
			Typeface blackFont = resources.getBlackFont();
			
			resources.setFont(m_original, blackFont);
			resources.setFont(m_translate, thinFont);
			resources.setFont(m_languageA, blackFont);
			resources.setFont(m_languageB, thinFont);
			
			view.setTag(this);
		}
	
	}
}
