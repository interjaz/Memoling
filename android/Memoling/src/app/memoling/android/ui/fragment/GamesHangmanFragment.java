package app.memoling.android.ui.fragment;

import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import app.memoling.android.R;
import app.memoling.android.adapter.MemoAdapter;
import app.memoling.android.adapter.WordListAdapter;
import app.memoling.android.entity.Language;
import app.memoling.android.entity.Memo;
import app.memoling.android.helper.Helper;
import app.memoling.android.ui.ApplicationFragment;
import app.memoling.android.ui.ResourceManager;
import app.memoling.android.ui.adapter.ModifiableComplexTextAdapter;
import app.memoling.android.ui.view.GamesSourceView;

public class GamesHangmanFragment extends ApplicationFragment implements TextWatcher, OnItemSelectedListener {

	private LinearLayout m_layLettersL1;
	private LinearLayout m_layLettersL2;
	private LinearLayout m_layLettersL3;
	private LinearLayout m_layLettersL4;
	private LinearLayout m_layLettersL5;
	private LinearLayout m_layUsedLettersL1;
	private LinearLayout m_layUsedLettersL2;
	private TextView[] m_txtLetters;
	private ImageView[] m_imgHangman;
	private EditText m_txtLetter;
	private TextView m_lblResult;
	private TextView m_lblScore;
	private Spinner m_spSource;

	private String m_word = "desktop";
	private int m_originalOrientation;
	private int m_state = 0;
	private int m_lettersToGuess;
	private ArrayList<Character> m_letters;
	private ModifiableComplexTextAdapter<GamesSourceView> m_adapter;

	Typeface m_thinFont;

	private Animation m_fadeIn;
	private Animation m_fadeOut;

	private int m_userScore = 0;
	private int m_cpuScore = 0;
	
	private boolean m_lost = false;

	private Random m_random = new Random();

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View contentView = super.onCreateView(inflater, container, savedInstanceState,
				inflater.inflate(R.layout.games_hangman, container, false));
		setTitle(getActivity().getString(R.string.memolist_hangman));

		m_originalOrientation = getActivity().getRequestedOrientation();
		getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		ResourceManager resources = getResourceManager();
		m_thinFont = resources.getLightFont();

		m_layLettersL1 = (LinearLayout) contentView.findViewById(R.id.hangman_layLettersL1);
		m_layLettersL2 = (LinearLayout) contentView.findViewById(R.id.hangman_layLettersL2);
		m_layLettersL3 = (LinearLayout) contentView.findViewById(R.id.hangman_layLettersL3);
		m_layLettersL4 = (LinearLayout) contentView.findViewById(R.id.hangman_layLettersL4);
		m_layLettersL5 = (LinearLayout) contentView.findViewById(R.id.hangman_layLettersL5);

		m_layUsedLettersL1 = (LinearLayout) contentView.findViewById(R.id.hangman_layUsedLettersL1);
		m_layUsedLettersL2 = (LinearLayout) contentView.findViewById(R.id.hangman_layUsedLettersL2);

		m_imgHangman = new ImageView[11];
		m_imgHangman[0] = (ImageView) contentView.findViewById(R.id.hangman_img1);
		m_imgHangman[1] = (ImageView) contentView.findViewById(R.id.hangman_img2);
		m_imgHangman[2] = (ImageView) contentView.findViewById(R.id.hangman_img3);
		m_imgHangman[3] = (ImageView) contentView.findViewById(R.id.hangman_img4);
		m_imgHangman[4] = (ImageView) contentView.findViewById(R.id.hangman_img5);
		m_imgHangman[5] = (ImageView) contentView.findViewById(R.id.hangman_img6);
		m_imgHangman[6] = (ImageView) contentView.findViewById(R.id.hangman_img7);
		m_imgHangman[7] = (ImageView) contentView.findViewById(R.id.hangman_img8);
		m_imgHangman[8] = (ImageView) contentView.findViewById(R.id.hangman_img9);
		m_imgHangman[9] = (ImageView) contentView.findViewById(R.id.hangman_img10);
		m_imgHangman[10] = (ImageView) contentView.findViewById(R.id.hangman_img11);

		m_lblResult = (TextView) contentView.findViewById(R.id.hangman_lblResult);
		m_lblScore = (TextView) contentView.findViewById(R.id.hangman_lblScore);
		m_lblScore.setText(Integer.valueOf(m_userScore) + " : " + Integer.valueOf(m_cpuScore));

		m_spSource = (Spinner) contentView.findViewById(R.id.hangman_spSource);
		m_adapter = new ModifiableComplexTextAdapter<GamesSourceView>(getActivity(), R.layout.adapter_textdropdown,
				new int[] { R.id.memo_lblLang }, new Typeface[] { m_thinFont });
		m_adapter.addAll(GamesSourceView.getSourceViewsWithMemos(getActivity()));
		m_spSource.setAdapter(m_adapter);

		m_spSource.setOnItemSelectedListener(this);

		m_txtLetter = (EditText) contentView.findViewById(R.id.hangman_txtLetter);
		m_txtLetter.addTextChangedListener(this);

		resources.setFont(m_txtLetter, m_thinFont);
		resources.setFont(m_lblScore, m_thinFont);
		resources.setFont(m_lblResult, m_thinFont);
		resources.setFont(contentView, R.id.memo_lblLang, m_thinFont);
		resources.setFont(contentView, R.id.textView1, m_thinFont);

		hideHangman();

		m_letters = new ArrayList<Character>();

		// Set Animations
		m_fadeIn = new AlphaAnimation(0.0f, 1.0f);
		m_fadeIn.setDuration(1000);
		m_fadeIn.setAnimationListener(new FadeInEventHandler());
		m_fadeOut = new AlphaAnimation(1.0f, 0.0f);
		m_fadeOut.setDuration(1000);
		m_fadeOut.setAnimationListener(new FadeOutEventHandler());

		return contentView;
	}

	@Override
	protected void onDataBind(Bundle savedInstanceState) {
		// This is going to be called anyway by spinner
		//newGame();
	}

	private void showHangman(int progress) {

		if (progress > m_imgHangman.length) {
			return;
		}

		for (int i = 0; i < progress; i++) {
			m_imgHangman[i].setVisibility(View.VISIBLE);
		}
	}

	private void hideHangman() {
		for (int i = 0; i < m_imgHangman.length; i++) {
			m_imgHangman[i].setVisibility(View.INVISIBLE);
		}
	}

	private void hideKeyboard() {
		InputMethodManager keyboard = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		keyboard.hideSoftInputFromWindow(m_txtLetter.getWindowToken(), 0);
	}

	@Override
	public void afterTextChanged(Editable s) {
		String letter = s.toString();
		if (s.length() == 0) {
			return;
		}

		m_txtLetter.setText("");

		char c = letter.charAt(letter.length() - 1);
		if (m_letters.contains(Character.valueOf(c))) {
			hideKeyboard();
			return;
		}

		m_letters.add(c);

		if (m_word.contains(letter)) {

			for (int i = 0; i < m_word.length(); i++) {
				if (m_word.charAt(i) == letter.charAt(0)) {
					m_txtLetters[i].setText(" " + Character.toString(c) + " ");
					m_lettersToGuess--;
				}
			}

			if (m_lettersToGuess == 0) {
				endGame(true);
			}

		} else {
			LinearLayout lay = m_layUsedLettersL1;
			if (m_state > 8) {
				lay = m_layUsedLettersL2;
			}
			int txtSize = Helper.dipToPixels(getActivity(), 15);
			int txtHorizontalPadding = Helper.dipToPixels(getActivity(), 5);
			TextView tv = new TextView(getActivity());
			tv.setText(" " + Character.toString(c) + " ");
			tv.setTextSize(txtSize);
			tv.setPadding(txtHorizontalPadding, 0, txtHorizontalPadding, 0);
			tv.setPaintFlags(tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
			getResourceManager().setFont(tv, m_thinFont);
			lay.addView(tv);

			showHangman(++m_state);

			if (m_state == m_imgHangman.length) {
				endGame(false);
			}
		}
		hideKeyboard();
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}

	@Override
	public void onDestroyView() {
		getActivity().setRequestedOrientation(m_originalOrientation);
		super.onDestroyView();
	}

	private void newGame() {
		
		// Get new word
		getWordForNewGame();

		// Render
		renderNewGame();
	}

	private void getWordForNewGame() {
		MemoAdapter memoAdapter = new MemoAdapter(getActivity());
		WordListAdapter wordAdapter = new WordListAdapter(getActivity());
		String word;

		int value = m_adapter.getItem(m_spSource.getSelectedItemPosition()).getValue();
		switch (value) {
		case 0:
		default:
			// Current library
			Memo memo = memoAdapter.getRandom(getPreferences().getLastMemoBaseId());
			if (m_random.nextBoolean()) {
				word = memo.getWordA().getWord();
			} else {
				word = memo.getWordB().getWord();
			}
			break;
		case 1:
			// EN
			word = wordAdapter.getRandom(Language.EN);
			break;
		case 2:
			// ES
			word = wordAdapter.getRandom(Language.ES);
			break;
		case 3:
			// DE
			word = wordAdapter.getRandom(Language.DE);
			break;
		case 4:
			// FR
			word = wordAdapter.getRandom(Language.FR);
			break;
		case 5:
			// IT
			word = wordAdapter.getRandom(Language.IT);
			break;
		case 6:
			// PL
			word = wordAdapter.getRandom(Language.PL);
			break;
		}

		m_word = word;
	}

	private void renderNewGame() {	
		hideHangman();
		m_lblScore.setText(Integer.valueOf(m_userScore) + " : " + Integer.valueOf(m_cpuScore));

		m_state = 0;
		m_lettersToGuess = m_word.length();
		m_letters.clear();
		m_layUsedLettersL1.removeAllViews();
		m_layUsedLettersL2.removeAllViews();
		m_layLettersL1.removeAllViews();
		m_layLettersL2.removeAllViews();
		m_layLettersL3.removeAllViews();
		m_layLettersL4.removeAllViews();
		m_layLettersL5.removeAllViews();

		m_txtLetters = new TextView[m_word.length()];
		int minDpi = 11;
		int maxDpi = 20;
		float dpi = Math.max(minDpi, maxDpi -  m_word.length()*1.2f); // max 8, so 20 - 8*1.2 ~ 11
		int txtSize = Helper.dipToPixels(getActivity(), dpi);
		int txtHorizontalPadding = Helper.dipToPixels(getActivity(), 5);
		for (int i = 0; i < m_word.length(); i++) {
			TextView tv = new TextView(getActivity());
			tv.setText("   ");
			tv.setTextSize(txtSize);
			tv.setPadding(txtHorizontalPadding, 0, txtHorizontalPadding, 0);
			tv.setPaintFlags(tv.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
			getResourceManager().setFont(tv, m_thinFont);
			m_txtLetters[i] = tv;
			if(i < 8) {
				m_layLettersL1.addView(tv);
			} else if(i < 16) {
				m_layLettersL2.addView(tv);
			} else if(i < 24) {
				m_layLettersL3.addView(tv);
			} else if(i < 32) {
				m_layLettersL4.addView(tv);
			} else if(i < 40) {
				m_layLettersL5.addView(tv);
			}
		}
	}

	private void endGame(boolean userWon) {
		if (userWon) {
			m_lblResult.setText(R.string.games_win);
			m_userScore++;
			m_lost = false;
		} else {
			m_lblResult.setText(getString(R.string.games_lost) + " - " + m_word);
			m_cpuScore++;
			m_lost = true;
		}

		m_lblResult.startAnimation(m_fadeIn);
	}

	private class FadeInEventHandler implements AnimationListener {

		@Override
		public void onAnimationEnd(Animation animation) {
			if(m_lost) {
				try {
					Thread.sleep(1000);
				} catch(InterruptedException ex) {
					// OK
				}
			}
			m_lblResult.startAnimation(m_fadeOut);
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}

		@Override
		public void onAnimationStart(Animation animation) {
		}

	}

	private class FadeOutEventHandler implements AnimationListener {

		@Override
		public void onAnimationEnd(Animation animation) {
			newGame();
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}

		@Override
		public void onAnimationStart(Animation animation) {
		}

	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		newGame();
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}
}
