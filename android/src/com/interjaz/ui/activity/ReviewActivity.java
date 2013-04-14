package com.interjaz.ui.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.interjaz.R;
import com.interjaz.WorkerThread;
import com.interjaz.entity.Memo;
import com.interjaz.entity.MemoAdapter;
import com.interjaz.entity.Word;
import com.interjaz.ui.ResourceManager;

public class ReviewActivity extends Activity implements OnEditorActionListener {

	public final static String MemoBaseId = "MemoBaseId";
	public final static String NotificationId = "NotificationId";
	public static int DefaultTrainSetSize = 10;

	private final static int InvalidNotificationId = -1;

	private static ResourceManager m_resources;

	private TextView m_lblResult;
	private TextView m_txtMemo1;
	private EditText m_txtMemo2;
	private TextView m_lblLang1;
	private TextView m_lblLang2;
	private TextView m_lblTotal;
	private Button m_btnNext;

	private Random m_random;

	private ArrayList<Memo> m_memos;
	private Memo m_memo;
	private Word m_origWord;
	private MemoAdapter m_memoAdapter;
	private int m_currentMemo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_review);

		m_resources = new ResourceManager(this);

		m_lblResult = (TextView) findViewById(R.id.review_lblResult);
		m_lblResult.setTypeface(m_resources.getThinFont());
		m_txtMemo1 = (TextView) findViewById(R.id.review_txtMemo1);
		m_txtMemo1.setTypeface(m_resources.getThinFont());
		m_txtMemo2 = (EditText) findViewById(R.id.review_txtMemo2);
		m_txtMemo2.setTypeface(m_resources.getThinFont());
		m_txtMemo2.setOnEditorActionListener(this);
		m_lblLang1 = (TextView) findViewById(R.id.review_lblMemo1Lang);
		m_lblLang1.setTypeface(m_resources.getThinFont());
		m_lblLang2 = (TextView) findViewById(R.id.review_lblMemo2Lang);
		m_lblLang2.setTypeface(m_resources.getThinFont());
		m_lblTotal = (TextView) findViewById(R.id.review_lblTotal);
		m_lblTotal.setTypeface(m_resources.getThinFont());
		m_btnNext = (Button) findViewById(R.id.review_btnNext);
		m_btnNext.setOnClickListener(new BtnNextEventHandler());
		m_btnNext.setTypeface(m_resources.getThinFont());

		m_random = new Random();

		try {
			m_memoAdapter = new MemoAdapter(this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Set Fonts
		m_resources.setFont(R.id.textView1, m_resources.getCondensedFont());
		m_resources.setFont(R.id.textView2, m_resources.getCondensedFont());
		m_resources.setFont(R.id.textView3, m_resources.getCondensedFont());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_review, menu);
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

	public void newMemo() {
		Memo memo = m_memos.get(m_currentMemo);
		m_memo = memo;
		m_txtMemo1.setTextColor(0x00000000);
		m_txtMemo2.setTextColor(0x00000000);
		m_txtMemo2.setText("");

		Word visible;
		Word toGuess;

		if (m_random.nextFloat() > 0.5f) {
			visible = memo.getWordA();
			toGuess = memo.getWordB();
		} else {
			visible = memo.getWordB();
			toGuess = memo.getWordA();
		}

		m_origWord = toGuess;

		m_txtMemo1.setText(visible.getWord());
		m_lblLang1.setText(visible.getLanguage().getName());
		m_lblLang2.setText(toGuess.getLanguage().getName());
		animateNew();
	}

	@Override
	public void onStart() {
		super.onStart();

		Intent intent = getIntent();
		String memoBaseId = intent.getStringExtra(MemoBaseId);

		int notificationId = intent.getIntExtra(NotificationId, InvalidNotificationId);
		if (notificationId != InvalidNotificationId) {
			((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(notificationId);
		}

		bindData(memoBaseId);
		newMemo();
	}

	private void bindData(String memoBaseId) {
		m_memos = m_memoAdapter.getTrainSet(memoBaseId, DefaultTrainSetSize);
		m_currentMemo = 0;
		updateTotal();
	}

	private class BtnNextEventHandler implements OnClickListener {

		@Override
		public void onClick(View v) {

			String original = m_origWord.getWord().trim().toLowerCase(Locale.US);
			String user = m_txtMemo2.getText().toString().trim().toLowerCase(Locale.US);

			boolean match = user.equals(original);

			m_memo.setDisplayed(m_memo.getDisplayed() + 1);
			m_memo.setLastReviewed(new Date());
			if (match) {
				if (m_memo.getWordA().equals(m_origWord)) {
					m_memo.setCorrectAnsweredWordA(m_memo.getCorrectAnsweredWordA() + 1);
				} else {
					m_memo.setCorrectAnsweredWordB(m_memo.getCorrectAnsweredWordB() + 1);
				}
			}
			m_memoAdapter.update(m_memo);

			animateVanish();
			animateAnswer(match);
		}

	}

	private void updateTotal() {
		m_lblTotal.setText(Integer.toString(m_currentMemo + 1) + " / " + Integer.toString(m_memos.size()));

	}

	private void animateNew() {
		new NewMemoAnimation().execute();
	}

	private class NewMemoAnimation extends WorkerThread<Void, Float, Void> {

		@Override
		protected Void doInBackground(Void... params) {

			for (int i = 0; i < 500; i++) {
				publishProgress(i / 500.0f);
				SystemClock.sleep(1);
			}

			return null;
		}

		@Override
		protected void onProgressUpdate(Float... values) {
			super.onProgressUpdate(values);
			int color = ((int) (0xff * values[0].floatValue())) << (0x18);
			m_txtMemo1.setTextColor(color + 0x00ffffff);
			m_lblLang1.setTextColor(color + 0x00ffffff);
			m_txtMemo2.setTextColor(color + 0x00ffffff);
			m_lblLang2.setTextColor(color + 0x00ffffff);
		}

	}

	private void animateAnswer(boolean correct) {
		if (correct) {
			m_lblResult.setText(R.string.review_lblCorrect);
		} else {
			m_lblResult.setText(R.string.review_lblIncorrect);
		}
		new TxtResultAnimation().execute();
	}

	private class TxtResultAnimation extends WorkerThread<Void, Float, Void> {

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			m_lblResult.setVisibility(View.GONE);

			m_currentMemo++;
			if (m_currentMemo < m_memos.size()) {
				newMemo();
				updateTotal();
			} else {
				// TODO: Go back to previous activity, or close activity.
				setResult(Activity.RESULT_OK);
				finish();
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			m_lblResult.setVisibility(View.VISIBLE);
		}

		@Override
		protected Void doInBackground(Void... voids) {

			SystemClock.sleep(500);
			for (int i = 0; i <= 400; i++) {
				publishProgress(Float.valueOf(i / 400.0f));
				SystemClock.sleep(1);
			}
			SystemClock.sleep(200);
			for (int i = 200; i >= 0; i--) {
				publishProgress(Float.valueOf(i / 200.0f));
				SystemClock.sleep(1);
			}

			return null;
		}

		@Override
		protected void onProgressUpdate(Float... values) {
			super.onProgressUpdate(values);
			int color = ((int) (0xff * values[0].floatValue())) << (0x18);
			m_lblResult.setTextColor(color + 0x00FFFFFF);
			m_lblResult.setBackgroundColor(color + 0x00222222);

		}

	}

	private void animateVanish() {
		new TxtVanishAnimation().execute();
	}

	private class TxtVanishAnimation extends WorkerThread<Void, Float, Void> {

		@Override
		protected Void doInBackground(Void... params) {

			for (int i = 500; i >= 0; i--) {
				publishProgress(i / 500.0f);
				SystemClock.sleep(1);
			}

			return null;
		}

		@Override
		protected void onProgressUpdate(Float... values) {
			super.onProgressUpdate(values);
			int color = ((int) (0xff * values[0].floatValue())) << (0x18);
			m_txtMemo1.setTextColor(color + 0x00ffffff);
			m_lblLang1.setTextColor(color + 0x00ffffff);
			m_txtMemo2.setTextColor(color + 0x00ffffff);
			m_lblLang2.setTextColor(color + 0x00ffffff);

		}
	}
}
