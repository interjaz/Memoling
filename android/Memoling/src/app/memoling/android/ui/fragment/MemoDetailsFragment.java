package app.memoling.android.ui.fragment;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import app.memoling.android.R;
import app.memoling.android.entity.Memo;
import app.memoling.android.entity.MemoSentence;
import app.memoling.android.entity.QuizletDefinition;
import app.memoling.android.helper.DateHelper;
import app.memoling.android.ui.ResourceManager;
import app.memoling.android.ui.fragment.MemoFragment.IMemoPagerFragment;

public class MemoDetailsFragment extends Fragment implements IMemoPagerFragment {

	private CheckBox m_chbEnabled;
	private TextView m_lblCreated;
	private TextView m_lblLastReviewed;
	private TextView m_lblDisplayed;
	private TextView m_lblCorrectAnswered;
	private Memo m_memo;
	private Runnable m_memoDelayedRunnable;
	private Runnable m_sentenceDelayedRunnable;

	@SuppressLint("SetJavaScriptEnabled")
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View contentView = inflater.inflate(R.layout.memo_details, container, false);

		ResourceManager resources = new ResourceManager(getActivity());
		Typeface thinFont = resources.getThinFont();

		m_chbEnabled = (CheckBox) contentView.findViewById(R.id.memo_chbEnabled);
		m_chbEnabled.setTypeface(thinFont);
		m_chbEnabled.setOnCheckedChangeListener(new ChbEnabledEventHandler());

		m_lblCreated = (TextView) contentView.findViewById(R.id.memo_lblCreated);
		m_lblCreated.setTypeface(thinFont);

		m_lblLastReviewed = (TextView) contentView.findViewById(R.id.memo_lblLastReviewed);
		m_lblLastReviewed.setTypeface(thinFont);

		m_lblDisplayed = (TextView) contentView.findViewById(R.id.memo_lblDisplayed);
		m_lblDisplayed.setTypeface(thinFont);

		m_lblCorrectAnswered = (TextView) contentView.findViewById(R.id.memo_lblCorrectAnswered);
		m_lblCorrectAnswered.setTypeface(thinFont);

		resources.setFont(contentView, R.id.textView3, thinFont);
		resources.setFont(contentView, R.id.downloadlink_lblDefinitionALabel, thinFont);
		resources.setFont(contentView, R.id.textView5, thinFont);
		resources.setFont(contentView, R.id.textView6, thinFont);

		return contentView;
	}

	@Override
	public boolean onBackPressed() {
		return true;
	}

	@Override
	public void onStart() {
		super.onStart();

		if (m_memoDelayedRunnable != null) {
			m_memoDelayedRunnable.run();
		}

		if (m_sentenceDelayedRunnable != null) {
			m_sentenceDelayedRunnable.run();
		}
	}

	@Override
	public void setMemo(Memo memo) {
		m_memo = memo;

		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				m_lblCorrectAnswered.setText(Integer.toString(m_memo.getCorrectAnsweredWordA()
						+ m_memo.getCorrectAnsweredWordB()));
				m_lblDisplayed.setText(Integer.valueOf(m_memo.getDisplayed()).toString());
				m_lblCreated.setText(DateHelper.toUiDate(m_memo.getCreated()));
				m_lblLastReviewed.setText(DateHelper.toUiDate(m_memo.getLastReviewed()));

				m_chbEnabled.setChecked(m_memo.getActive());
			};
		};

		if (getActivity() != null && m_lblCorrectAnswered != null) {
			runnable.run();
		}
		m_sentenceDelayedRunnable = runnable;
	}

	private class ChbEnabledEventHandler implements OnCheckedChangeListener {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if (m_memo != null) {
				m_memo.setActive(isChecked);
			}
		}
	}

	@Override
	public int getPosition() {
		return 2;
	}

	@Override
	public void setTatoeba(ArrayList<MemoSentence> memoSentences) {
		// No implementation
	}

	@Override
	public void setQuizlet(ArrayList<QuizletDefinition> definitions) {	
		// No implementation	
	}
}
