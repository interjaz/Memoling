package com.interjaz.ui.activity;

import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.interjaz.R;
import com.interjaz.entity.Memo;
import com.interjaz.entity.MemoAdapter;
import com.interjaz.helper.DateHelper;
import com.interjaz.ui.GestureActivity;
import com.interjaz.ui.ResourceManager;
import com.interjaz.ui.adapter.ModifiableComplexTextAdapter;
import com.interjaz.ui.view.LanguageView;

public class MemoActivity extends GestureActivity implements OnEditorActionListener {

	public final static String MemoId = "MemoId";

	private ResourceManager m_resources;

	private Spinner m_spLanguageA;
	private ModifiableComplexTextAdapter<LanguageView> m_spLanguageAAdapter;
	private Spinner m_spLanguageB;
	private ModifiableComplexTextAdapter<LanguageView> m_spLanguageBAdapter;
	private Button m_btnSave;
	private EditText m_txtWordA;
	private EditText m_txtWordB;
	private CheckBox m_chbEnabled;
	private TextView m_lblCreated;
	private TextView m_lblLastReviewed;
	private TextView m_lblDisplayed;
	private TextView m_lblCorrectAnswered;
	private TextView m_lblTitle;
	private RelativeLayout m_laySaving;

	private MemoAdapter m_memoAdapter;
	private Memo m_memo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_memo);
		m_resources = new ResourceManager(this);

		m_lblTitle = (TextView) findViewById(R.id.memo_lblTitle);
		m_lblTitle.setTypeface(m_resources.getThinFont());

		m_spLanguageA = (Spinner) findViewById(R.id.memo_spLangA);
		m_spLanguageAAdapter = new ModifiableComplexTextAdapter<LanguageView>(this, R.layout.adapter_textdropdown,
				new int[] { R.id.textView1 }, new Typeface[] { m_resources.getThinFont() });
		m_spLanguageA.setAdapter(m_spLanguageAAdapter);

		m_spLanguageB = (Spinner) findViewById(R.id.memo_spLangB);
		m_spLanguageBAdapter = new ModifiableComplexTextAdapter<LanguageView>(this, R.layout.adapter_textdropdown,
				new int[] { R.id.textView1 }, new Typeface[] { m_resources.getThinFont() });
		m_spLanguageB.setAdapter(m_spLanguageBAdapter);

		m_btnSave = (Button) findViewById(R.id.memo_btnSave);
		m_btnSave.setOnClickListener(new BtnSaveEventHandler());
		m_btnSave.setTypeface(m_resources.getThinFont());

		m_txtWordA = (EditText) findViewById(R.id.memo_txtWordA);
		m_txtWordA.setOnEditorActionListener(this);
		m_txtWordA.setTypeface(m_resources.getThinFont());

		m_txtWordB = (EditText) findViewById(R.id.memo_txtWordB);
		m_txtWordB.setOnEditorActionListener(this);
		m_txtWordB.setTypeface(m_resources.getThinFont());

		m_chbEnabled = (CheckBox) findViewById(R.id.memo_chbEnabled);
		m_chbEnabled.setTypeface(m_resources.getThinFont());

		m_lblCreated = (TextView) findViewById(R.id.memo_lblCreated);
		m_lblCreated.setTypeface(m_resources.getThinFont());

		m_lblLastReviewed = (TextView) findViewById(R.id.memo_lblLastReviewed);
		m_lblLastReviewed.setTypeface(m_resources.getThinFont());

		m_lblDisplayed = (TextView) findViewById(R.id.memo_lblDisplayed);
		m_lblDisplayed.setTypeface(m_resources.getThinFont());

		m_lblCorrectAnswered = (TextView) findViewById(R.id.memo_lblCorrectAnswered);
		m_lblCorrectAnswered.setTypeface(m_resources.getThinFont());

		m_laySaving = (RelativeLayout) findViewById(R.id.memo_laySaving);

		try {
			m_memoAdapter = new MemoAdapter(this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		m_resources.setFont(R.id.textView1, m_resources.getCondensedFont());
		m_resources.setFont(R.id.textView2, m_resources.getCondensedFont());
		m_resources.setFont(R.id.textView3, m_resources.getCondensedFont());
		m_resources.setFont(R.id.textView4, m_resources.getCondensedFont());
		m_resources.setFont(R.id.textView5, m_resources.getCondensedFont());
		m_resources.setFont(R.id.textView6, m_resources.getCondensedFont());
		m_resources.setFont(R.id.memo_lblWordA, m_resources.getCondensedFont());
		m_resources.setFont(R.id.memo_lblWordB, m_resources.getCondensedFont());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (actionId == EditorInfo.IME_ACTION_DONE) {
			InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			return true;
		}
		return false;
	}

	@Override
	public void onStart() {
		super.onStart();

		Intent intent = getIntent();
		String memoId = intent.getStringExtra(MemoId);
		bindData(memoId);
	}

	private class BtnSaveEventHandler implements OnClickListener {

		@Override
		public void onClick(View view) {

			m_laySaving.setVisibility(View.VISIBLE);

			m_memo.getWordA().setWord(m_txtWordA.getText().toString());
			m_memo.getWordB().setWord(m_txtWordB.getText().toString());
			m_memo.setActive(m_chbEnabled.isChecked());
			m_memo.getWordA().setLanguage(((LanguageView) m_spLanguageA.getSelectedItem()).getLanguage());
			m_memo.getWordB().setLanguage(((LanguageView) m_spLanguageB.getSelectedItem()).getLanguage());

			m_memoAdapter.update(m_memo);

			finish();
		}

	}

	private void bindData(String memoId) {
		m_memo = m_memoAdapter.get(memoId);

		m_lblTitle.setText(m_memo.getWordA().getWord() + " : " + m_memo.getWordB().getWord());

		m_lblCorrectAnswered.setText(Integer.toString(m_memo.getCorrectAnsweredWordA()
				+ m_memo.getCorrectAnsweredWordB()));
		m_lblDisplayed.setText(Integer.valueOf(m_memo.getDisplayed()).toString());
		m_lblCreated.setText(DateHelper.toUiDate(m_memo.getCreated()));
		m_lblLastReviewed.setText(DateHelper.toUiDate(m_memo.getLastReviewed()));
		m_txtWordA.setText(m_memo.getWordA().getWord());
		m_txtWordB.setText(m_memo.getWordB().getWord());

		m_spLanguageAAdapter.addAll(LanguageView.getAll());
		m_spLanguageA.setSelection(m_memo.getWordA().getLanguage().getPosition());
		m_spLanguageBAdapter.addAll(LanguageView.getAll());
		m_spLanguageB.setSelection(m_memo.getWordB().getLanguage().getPosition());

		m_chbEnabled.setChecked(m_memo.getActive());
	}

	@Override
	public boolean onSwipeRightToLeft() {
		finish();
		return false;
	}
}
