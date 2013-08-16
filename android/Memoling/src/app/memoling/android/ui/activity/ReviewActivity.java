package app.memoling.android.ui.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import app.memoling.android.R;
import app.memoling.android.adapter.MemoAdapter;
import app.memoling.android.adapter.MemoAdapter.Sort;
import app.memoling.android.adapter.MemoSentenceAdapter;
import app.memoling.android.db.DatabaseHelper.Order;
import app.memoling.android.entity.Language;
import app.memoling.android.entity.Memo;
import app.memoling.android.entity.MemoSentence;
import app.memoling.android.entity.Word;
import app.memoling.android.helper.SentenceProvider;
import app.memoling.android.helper.SentenceProvider.IGetManyComplete;
import app.memoling.android.helper.VoiceInputHelper;
import app.memoling.android.preference.Preferences;
import app.memoling.android.ui.AdActivity;
import app.memoling.android.ui.ResourceManager;
import app.memoling.android.webservice.WsSentences.IGetComplete;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;

public class ReviewActivity extends AdActivity {

	public final static String MemoBaseId = "MemoBaseId";
	public final static String NotificationId = "NotificationId";
	public final static String RepeatAll = "RepeatAll";
	public final static String Mode = "Mode";

	public final static int WordMode = 1;
	public final static int SentenceMode = 2;

	// TODO: Implement description mode
	public final static int DescriptionMode = 4;

	public static int m_trainingSetSize;

	private final static int InvalidNotificationId = -1;

	public final static int VoiceInputRequestCode = 1;

	private static ResourceManager m_resources;

	private TextView m_lblResult;
	private TextView m_txtMemo1;
	private EditText m_txtMemo2;
	private TextView m_lblLang1;
	private TextView m_lblLang2;

	private Random m_random;

	private ArrayList<Memo> m_memos;
	private Memo m_memo;
	private String m_toGuess;
	private Language m_toGuessLanguage;
	private int m_toGuessItem;
	private ArrayList<MemoSentence> m_sentences;

	private MemoAdapter m_memoAdapter;
	private int m_currentMemo;
	private int m_uiCurrentMemo;
	private boolean m_answerCorrect;

	private Animation m_fadeIn;
	private Animation m_fadeInAnswer;
	private Animation m_fadeOutAnswer;

	InputMethodManager m_inputManager;

	private Handler m_handler = new Handler();
	private float m_uiProgress = 0;
	Runnable m_progressRunner;

	private enum Access {
		Random, AllA, AllB,
	}

	private Access m_access;

	private int m_mode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_PROGRESS);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_review);
		onCreate_Ads();

		// Show back arrow
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		m_resources = new ResourceManager(this);
		m_trainingSetSize = new Preferences(this).getLearningSetSize();

		m_lblResult = (TextView) findViewById(R.id.review_lblResult);
		m_lblResult.setTypeface(m_resources.getThinFont());

		m_txtMemo1 = (TextView) findViewById(R.id.review_txtMemo1);
		m_txtMemo1.setTypeface(m_resources.getThinFont());
		m_txtMemo2 = (EditText) findViewById(R.id.review_txtMemo2);
		m_txtMemo2.setTypeface(m_resources.getThinFont());
		TextMemo2EventHandler txtMemo2EventHandler = new TextMemo2EventHandler();
		m_txtMemo2.setOnEditorActionListener(txtMemo2EventHandler);
		m_txtMemo2.addTextChangedListener(txtMemo2EventHandler);

		m_lblLang1 = (TextView) findViewById(R.id.review_lblMemo1Lang);
		m_lblLang1.setTypeface(m_resources.getThinFont());

		m_lblLang2 = (TextView) findViewById(R.id.review_lblMemo2Lang);
		m_lblLang2.setTypeface(m_resources.getThinFont());

		m_inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		m_random = new Random();

		m_memoAdapter = new MemoAdapter(this);

		// Set Fonts
		m_resources.setFont(R.id.textView1, m_resources.getThinFont());
		m_resources.setFont(R.id.textView2, m_resources.getThinFont());

		// Set Animations
		m_fadeIn = new AlphaAnimation(0.0f, 1.0f);
		m_fadeIn.setDuration(1000);
		m_fadeInAnswer = new AlphaAnimation(0.0f, 1.0f);
		m_fadeInAnswer.setDuration(1000);
		m_fadeInAnswer.setAnimationListener(new FadeInAnswerEventHandler());
		m_fadeOutAnswer = new AlphaAnimation(1.0f, 0.0f);
		m_fadeOutAnswer.setDuration(1000);
		m_fadeOutAnswer.setAnimationListener(new FadeOutAnswerEventHandler());

		getSherlock().setProgressBarIndeterminateVisibility(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		if (VoiceInputHelper.isSupported(this)) {
			MenuItem item = menu.add(1, 1, Menu.NONE, "Voice");
			item.setIcon(R.drawable.ic_voice_search);
			item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		}

		MenuItem submit = menu.add(1, 0, Menu.NONE, "Submit");
		submit.setIcon(R.drawable.ic_submit);
		submit.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == 0) {
			submitAnswer();
			return false;
		} else if (item.getItemId() == 1) {
			Intent intent = VoiceInputHelper.buildIntent(m_toGuessLanguage);
			startActivityForResult(intent, VoiceInputRequestCode);
			return false;
		} else {
			finish();
		}

		return super.onOptionsItemSelected(item);
	}

	public void newMemo() {
		if (m_memos.size() == 0) {
			return;
		}

		Memo memo = m_memos.get(m_currentMemo);
		m_memo = memo;

		Word visible = null;
		Word toGuess = null;

		if (m_access == Access.Random) {
			if (m_random.nextBoolean()) {
				visible = memo.getWordA();
				toGuess = memo.getWordB();
				m_toGuessItem = 1;
			} else {
				visible = memo.getWordB();
				toGuess = memo.getWordA();
				m_toGuessItem = 0;
			}
		} else if (m_access == Access.AllA) {
			visible = memo.getWordB();
			toGuess = memo.getWordA();
			m_toGuessItem = 0;
		} else if (m_access == Access.AllB) {
			visible = memo.getWordA();
			toGuess = memo.getWordB();
			m_toGuessItem = 1;
		}

		m_toGuessLanguage = toGuess.getLanguage();

		if ((m_mode & SentenceMode) == SentenceMode && (m_mode & WordMode) == WordMode) {

			if (m_random.nextBoolean()) {
				SentenceMode(visible, toGuess);
			} else {
				WordMode(visible, toGuess);
			}

		} else if ((m_mode & SentenceMode) == SentenceMode) {
			SentenceMode(visible, toGuess);
		} else if ((m_mode & WordMode) == WordMode) {
			WordMode(visible, toGuess);
		}

		m_lblLang1.setText(visible.getLanguage().getName());
		m_lblLang2.setText(toGuess.getLanguage().getName());
		animateNew();

		new Runnable() {
			@Override
			public void run() {
				m_uiProgress += 0.02;
				float target = (float) m_uiCurrentMemo / m_trainingSetSize;

				// Normalize our progress along the progress bar's scale
				int progress = (int) ((Window.PROGRESS_END - Window.PROGRESS_START) * m_uiProgress);
				setSupportProgress(progress);

				if (m_uiProgress < target) {
					m_handler.postDelayed(this, 50);
				}
			}
		}.run();
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

		boolean repeatAll = false;
		if (intent.hasExtra(RepeatAll)) {
			repeatAll = true;
		}

		m_mode = intent.getIntExtra(Mode, WordMode);

		bindData(memoBaseId, repeatAll);

		if ((m_mode & SentenceMode) == SentenceMode) {

			SentenceProvider.getSentences(this, m_memos, new IGetManyComplete() {

				@Override
				public void onComplete(ArrayList<Pair<Memo, ArrayList<MemoSentence>>> result) {
					m_handler.post(new Runnable() {
						@Override
						public void run() {
							newMemo();
							getSherlock().setProgressBarIndeterminateVisibility(false);
						}
					});
				}

			});

		} else {
			newMemo();
			getSherlock().setProgressBarIndeterminateVisibility(false);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (data != null) {
			String voice = VoiceInputHelper.getData(data.getExtras());
			if (voice != null) {
				m_txtMemo2.getText().insert(m_txtMemo2.getSelectionStart(), voice);
			}
		}
	}

	private void bindData(String memoBaseId, boolean repeatAll) {

		if (!repeatAll) {
			m_memos = m_memoAdapter.getTrainSet(memoBaseId, m_trainingSetSize);
			m_access = Access.Random;
			m_trainingSetSize = m_memos.size();
		} else {
			m_memos = m_memoAdapter.getAll(memoBaseId, Sort.CreatedDate, Order.ASC);
			m_access = Access.AllA;
			m_trainingSetSize = m_memos.size() * 2;
		}
		m_currentMemo = 0;
		m_uiCurrentMemo = 0;

		if (m_memos.size() == 0) {
			Toast.makeText(this, R.string.review_noMemos, Toast.LENGTH_SHORT).show();
			finish();
			return;
		}

		if((m_mode & SentenceMode) == SentenceMode) {
			// look for memos with same language
			for(Memo memo : m_memos) {
				if(memo.getWordA().getLanguage() == memo.getWordB().getLanguage()) {
					Toast.makeText(this, R.string.review_sentenceNotSupported, Toast.LENGTH_LONG).show();
					finish();
					return;
				}
			}
		}
		
	}

	private void submitAnswer() {
		String original = m_toGuess.trim().toLowerCase(Locale.US);
		String user = m_txtMemo2.getText().toString().trim().toLowerCase(Locale.US);

		m_answerCorrect = user.equals(original);

		m_memo.setDisplayed(m_memo.getDisplayed() + 1);
		m_memo.setLastReviewed(new Date());
		if (m_answerCorrect) {
			if (m_toGuessItem == 0) {
				m_memo.setCorrectAnsweredWordA(m_memo.getCorrectAnsweredWordA() + 1);
			} else {
				m_memo.setCorrectAnsweredWordB(m_memo.getCorrectAnsweredWordB() + 1);
			}
		}
		m_memoAdapter.update(m_memo);

		animateAnswer(m_toGuess.trim());
	}

	private void SentenceMode(final Word a, final Word b) {
		MemoSentenceAdapter adapter = new MemoSentenceAdapter(this);
		ArrayList<MemoSentence> sentences = adapter.getSentences(b.getWord(), b.getLanguage(), a.getLanguage());

		final Runnable delayed = new Runnable() {

			@Override
			public void run() {

				MemoSentence sentence = m_sentences.get(m_random.nextInt(m_sentences.size()));

				int pos = sentence.getOriginalSentence().toLowerCase(Locale.US)
						.indexOf(b.getWord().toLowerCase(Locale.US));
				String strSentence = sentence.getOriginalSentence().replaceAll("(?i)" + b.getWord(), "");

				m_txtMemo1.setText(sentence.getTranslatedSentence());
				m_txtMemo2.setText(strSentence);

				if (pos != -1) {
					m_txtMemo2.setSelection(pos);
				}
				m_toGuess = sentence.getOriginalSentence();
			}

		};

		if (sentences.size() == 0) {
			getSherlock().setProgressBarIndeterminateVisibility(true);

			SentenceProvider.getSentences(ReviewActivity.this, b.getWord(), m_memo.getMemoId(), b.getLanguage(),
					a.getLanguage(), new IGetComplete() {

						@Override
						public void getComplete(ArrayList<MemoSentence> memoSentences) {
							getSherlock().setProgressBarIndeterminateVisibility(false);

							if (memoSentences == null || memoSentences.size() == 0) {
								WordMode(a, b);
								Toast.makeText(ReviewActivity.this, R.string.review_sentencesNotFound,
										Toast.LENGTH_SHORT).show();
								return;
							}

							m_sentences = memoSentences;
							delayed.run();
						}

					});
		} else {
			m_sentences = sentences;
			delayed.run();
		}

	}

	private void WordMode(Word a, Word b) {
		m_txtMemo1.setText(a.getWord());
		m_txtMemo2.setText("");
		m_toGuess = b.getWord();
	}

	private class TextMemo2EventHandler implements OnEditorActionListener, TextWatcher {

		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if (actionId == EditorInfo.IME_ACTION_DONE) {
				InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(
						Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				submitAnswer();
				return true;
			}

			return false;
		}

		@Override
		public void afterTextChanged(Editable s) {
			if (s.toString().contains("\n")) {
				submitAnswer();
				m_inputManager.hideSoftInputFromWindow(m_txtMemo2.getWindowToken(), 0);
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			// Nothing
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// Nothing
		}
	}

	private class FadeInAnswerEventHandler implements AnimationListener {

		@Override
		public void onAnimationEnd(Animation arg0) {

			if (!m_answerCorrect) {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// Do nothing
				}
			}

			m_fadeOutAnswer.reset();
			m_fadeOutAnswer.start();
			m_lblResult.clearAnimation();
			m_lblResult.setAnimation(m_fadeOutAnswer);
			m_txtMemo1.clearAnimation();
			m_txtMemo1.setAnimation(m_fadeOutAnswer);
			m_lblLang1.clearAnimation();
			m_lblLang1.setAnimation(m_fadeOutAnswer);
			m_txtMemo2.clearAnimation();
			m_txtMemo2.setAnimation(m_fadeOutAnswer);
			m_lblLang2.clearAnimation();
			m_lblLang2.setAnimation(m_fadeOutAnswer);
		}

		@Override
		public void onAnimationRepeat(Animation arg0) {
		}

		@Override
		public void onAnimationStart(Animation arg0) {
		}
	}

	private class FadeOutAnswerEventHandler implements AnimationListener {
		@Override
		public void onAnimationEnd(Animation animation) {
			m_currentMemo++;
			m_uiCurrentMemo++;
			if (m_currentMemo < m_memos.size()) {
				newMemo();
			} else {
				if (m_access == Access.Random || m_access == Access.AllB) {
					setResult(Activity.RESULT_OK);
					finish();
				} else {
					m_access = Access.AllB;
					m_currentMemo = 0;
					newMemo();
				}
			}
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}

		@Override
		public void onAnimationStart(Animation animation) {
		}
	}

	private void animateNew() {
		m_fadeIn.reset();
		m_fadeIn.start();
		m_txtMemo1.setAnimation(m_fadeIn);
		m_lblLang1.setAnimation(m_fadeIn);
		m_txtMemo2.setAnimation(m_fadeIn);
		m_lblLang2.setAnimation(m_fadeIn);
	}

	private void animateAnswer(String correct) {
		if (m_answerCorrect) {
			m_lblResult.setText(R.string.review_lblCorrect);
		} else {
			String strResult = String.format(getString(R.string.review_lblIncorrect), correct);
			m_lblResult.setText(strResult);
		}

		m_fadeInAnswer.reset();
		m_lblResult.startAnimation(m_fadeInAnswer);
	}
}
