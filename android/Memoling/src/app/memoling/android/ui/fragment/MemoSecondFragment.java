package app.memoling.android.ui.fragment;

import java.lang.ref.WeakReference;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import app.memoling.android.entity.Memo;

public class MemoSecondFragment extends MemoWordFragment {

	private static WeakReference<MemoSecondFragment> m_self;

	@SuppressLint("SetJavaScriptEnabled")
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = super.onCreateView(false, inflater, container, savedInstanceState);
		m_self = new WeakReference<MemoSecondFragment>(this);
		return view;
	}

	private void rebind(Memo memo) {
		if (memo != null && m_txtWord != null) {
			m_memo = memo;
			m_txtWord.setText(m_memo.getWordB().getWord());
		}
	}

	// TODO: Make it nice
	public static void notifyDataChange(Memo memo) {
		if (m_self != null && m_self.get() != null) {
			m_self.get().rebind(memo);
		}
	}
}
