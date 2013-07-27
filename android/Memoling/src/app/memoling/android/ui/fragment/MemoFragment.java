package app.memoling.android.ui.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import app.memoling.android.R;
import app.memoling.android.adapter.DictionaryAdapter;
import app.memoling.android.adapter.MemoAdapter;
import app.memoling.android.adapter.ThesaurusAdapter;
import app.memoling.android.entity.Language;
import app.memoling.android.entity.Memo;
import app.memoling.android.entity.Word;
import app.memoling.android.helper.DateHelper;
import app.memoling.android.helper.Helper;
import app.memoling.android.translator.ITranslateComplete;
import app.memoling.android.translator.Translator;
import app.memoling.android.translator.TranslatorResult;
import app.memoling.android.ui.ApplicationFragment;
import app.memoling.android.ui.ResourceManager;
import app.memoling.android.ui.control.LanguageSpinner;
import app.memoling.android.ui.view.LanguageView;

public class MemoFragment extends ApplicationFragment implements OnEditorActionListener, ITranslateComplete {

	public final static String MemoId = "MemoId";

	private LanguageSpinner m_spLanguageA;
	private LanguageSpinner m_spLanguageB;
	private EditText m_txtWordA;
	private EditText m_txtWordB;
	private CheckBox m_chbEnabled;
	private TextView m_lblCreated;
	private TextView m_lblLastReviewed;
	private TextView m_lblDisplayed;
	private TextView m_lblCorrectAnswered;
	private TextView m_lblTitle;
	private ImageButton m_btnSearchWordA;
	private ImageButton m_btnCopyWordA;
	private ImageButton m_btnCopyWordB;
	private Button m_btnThesaurusWordA;
	private Button m_btnDictionaryWordA;
	private Button m_btnThesaurusWordB;
	private Button m_btnDictionaryWordB;
	private FrameLayout m_layWebSearch;
	private WebView m_webSearch;

	private MemoAdapter m_memoAdapter;
	private Memo m_memo;

	@SuppressLint("SetJavaScriptEnabled")
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View contentView = super.onCreateView(inflater, container, savedInstanceState,
				inflater.inflate(R.layout.fragment_memo, container, false));
		setTitle(getActivity().getString(R.string.memo_title));

		ResourceManager resources = getResourceManager();
		Typeface thinFont = resources.getThinFont();
		Typeface condensedFont = resources.getCondensedFont();

		m_lblTitle = (TextView) contentView.findViewById(R.id.memo_lblTitle);
		m_lblTitle.setTypeface(thinFont);

		m_spLanguageA = (LanguageSpinner) contentView.findViewById(R.id.memo_spLangA);
		m_spLanguageB = (LanguageSpinner) contentView.findViewById(R.id.memo_spLangB);

		m_btnSearchWordA = (ImageButton) contentView.findViewById(R.id.memo_btnSearchWordA);
		m_btnSearchWordA.setOnClickListener(new BtnSearchWordAEventHandler());

		m_txtWordA = (EditText) contentView.findViewById(R.id.memo_txtWordA);
		m_txtWordA.setOnEditorActionListener(this);
		m_txtWordA.setTypeface(thinFont);

		m_btnCopyWordB = (ImageButton) contentView.findViewById(R.id.memo_btnCopyWordB);
		m_btnCopyWordB.setOnClickListener(new BtnCopyWordEventHandler(m_txtWordB));

		m_btnCopyWordA = (ImageButton) contentView.findViewById(R.id.memo_btnCopyWordA);
		m_btnCopyWordA.setOnClickListener(new BtnCopyWordEventHandler(m_txtWordA));

		m_txtWordB = (EditText) contentView.findViewById(R.id.memo_txtWordB);
		m_txtWordB.setOnEditorActionListener(this);
		m_txtWordB.setTypeface(thinFont);

		m_chbEnabled = (CheckBox) contentView.findViewById(R.id.memo_chbEnabled);
		m_chbEnabled.setTypeface(thinFont);

		m_lblCreated = (TextView) contentView.findViewById(R.id.memo_lblCreated);
		m_lblCreated.setTypeface(thinFont);

		m_lblLastReviewed = (TextView) contentView.findViewById(R.id.memo_lblLastReviewed);
		m_lblLastReviewed.setTypeface(thinFont);

		m_lblDisplayed = (TextView) contentView.findViewById(R.id.memo_lblDisplayed);
		m_lblDisplayed.setTypeface(thinFont);

		m_lblCorrectAnswered = (TextView) contentView.findViewById(R.id.memo_lblCorrectAnswered);
		m_lblCorrectAnswered.setTypeface(thinFont);

		m_webSearch = (WebView) contentView.findViewById(R.id.memo_webSearch);
		m_layWebSearch = (FrameLayout) contentView.findViewById(R.id.memo_layWebSearch);
		m_webSearch.getSettings().setJavaScriptEnabled(true);
		m_webSearch.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return false;
			}
		});

		m_btnThesaurusWordA = (Button) contentView.findViewById(R.id.memo_btnThesaurusWordA);
		m_btnThesaurusWordA.setOnClickListener(new BtnThesaurusEventHandler(m_txtWordA, m_spLanguageA));
		m_btnThesaurusWordB = (Button) contentView.findViewById(R.id.memo_btnThesaurusWordB);
		m_btnThesaurusWordB.setOnClickListener(new BtnThesaurusEventHandler(m_txtWordB, m_spLanguageB));
		m_btnDictionaryWordA = (Button) contentView.findViewById(R.id.memo_btnDictionaryWordA);
		m_btnDictionaryWordA.setOnClickListener(new BtnDictionaryEventHandler(m_txtWordA, m_spLanguageA));
		m_btnDictionaryWordB = (Button) contentView.findViewById(R.id.memo_btnDictionaryWordB);
		m_btnDictionaryWordB.setOnClickListener(new BtnDictionaryEventHandler(m_txtWordB, m_spLanguageB));

		m_spLanguageA.setOnItemSelectedListener(new SpLanguageEventHandler(m_spLanguageA, m_btnThesaurusWordA,
				m_btnDictionaryWordA));
		m_spLanguageB.setOnItemSelectedListener(new SpLanguageEventHandler(m_spLanguageB, m_btnThesaurusWordB,
				m_btnDictionaryWordB));

		resources.setFont(m_btnThesaurusWordA, thinFont);
		resources.setFont(m_btnThesaurusWordB, thinFont);
		resources.setFont(m_btnDictionaryWordA, thinFont);
		resources.setFont(m_btnDictionaryWordB, thinFont);

		m_memoAdapter = new MemoAdapter(getActivity());

		resources.setFont(contentView, R.id.textView1, condensedFont);
		resources.setFont(contentView, R.id.textView2, condensedFont);
		resources.setFont(contentView, R.id.textView3, condensedFont);
		resources.setFont(contentView, R.id.textView4, condensedFont);
		resources.setFont(contentView, R.id.textView5, condensedFont);
		resources.setFont(contentView, R.id.textView6, condensedFont);
		resources.setFont(contentView, R.id.memo_lblWordA, condensedFont);
		resources.setFont(contentView, R.id.memo_lblWordB, condensedFont);

		return contentView;
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
	public boolean onBackPressed() {
		if (m_layWebSearch.getVisibility() == View.VISIBLE) {
			m_layWebSearch.setVisibility(View.GONE);
			return false;
		}

		return true;
	}

	private class BtnSearchWordAEventHandler implements OnClickListener {
		@Override
		public void onClick(View v) {
			Word from = new Word(m_txtWordA.getText().toString().trim());
			Language fromLang = ((LanguageView) m_spLanguageA.getSelectedItem()).getLanguage();
			Language toLang = ((LanguageView) m_spLanguageB.getSelectedItem()).getLanguage();
			Toast.makeText(getActivity(), R.string.memo_selectTranslationProgress, Toast.LENGTH_SHORT).show();
			new Translator(from, fromLang, toLang, MemoFragment.this);
		}
	}

	private class BtnCopyWordEventHandler implements OnClickListener {

		private EditText m_editText;

		public BtnCopyWordEventHandler(EditText view) {
			m_editText = view;
		}

		@Override
		public void onClick(View v) {
			Helper.copyToClipboard(getActivity(), m_editText.getText().toString());
			Toast.makeText(getActivity(), R.string.memo_copied, Toast.LENGTH_SHORT).show();
		}
	}

	private class BtnThesaurusEventHandler implements OnClickListener {

		private EditText m_editText;
		private LanguageSpinner m_languageSpinner;

		public BtnThesaurusEventHandler(EditText editText, LanguageSpinner spinner) {
			m_editText = editText;
			m_languageSpinner = spinner;
		}

		@Override
		public void onClick(View v) {
			String searchUrl = String.format(ThesaurusAdapter
					.get(m_languageSpinner.getSelectedLanguage().getLanguage()), m_editText.getText().toString());

			m_layWebSearch.setVisibility(View.VISIBLE);
			m_webSearch.loadUrl(searchUrl);
		}
	}

	private class BtnDictionaryEventHandler implements OnClickListener {

		private EditText m_editText;
		private LanguageSpinner m_languageSpinner;

		public BtnDictionaryEventHandler(EditText editText, LanguageSpinner spinner) {
			m_editText = editText;
			m_languageSpinner = spinner;
		}

		@Override
		public void onClick(View v) {
			String searchUrl = String.format(DictionaryAdapter.get(m_languageSpinner.getSelectedLanguage()
					.getLanguage()), m_editText.getText().toString());

			m_layWebSearch.setVisibility(View.VISIBLE);
			m_webSearch.loadUrl(searchUrl);
		}
	}

	private class SpLanguageEventHandler implements OnItemSelectedListener {

		private LanguageSpinner m_languageSpinner;
		private Button m_btnThesaurus;
		private Button m_btnDictionary;

		public SpLanguageEventHandler(LanguageSpinner spinner, Button thesaurus, Button dictionary) {
			m_languageSpinner = spinner;
			m_btnThesaurus = thesaurus;
			m_btnDictionary = dictionary;
		}

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			Language language = ((LanguageView) m_languageSpinner.getItemAtPosition(position)).getLanguage();

			m_btnThesaurus.setEnabled(ThesaurusAdapter.isSupported(language));
			m_btnDictionary.setEnabled(ThesaurusAdapter.isSupported(language));
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// Nothing
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

		m_spLanguageA.bindData();
		m_spLanguageA.setSelection(m_memo.getWordA().getLanguage());
		m_spLanguageB.bindData();
		m_spLanguageB.setSelection(m_memo.getWordB().getLanguage());

		m_btnThesaurusWordA.setEnabled(ThesaurusAdapter.isSupported(m_memo.getWordA().getLanguage()));
		m_btnDictionaryWordA.setEnabled(ThesaurusAdapter.isSupported(m_memo.getWordA().getLanguage()));
		m_btnThesaurusWordB.setEnabled(ThesaurusAdapter.isSupported(m_memo.getWordB().getLanguage()));
		m_btnDictionaryWordB.setEnabled(ThesaurusAdapter.isSupported(m_memo.getWordB().getLanguage()));

		m_chbEnabled.setChecked(m_memo.getActive());
	}

	@Override
	public void onTranslateComplete(TranslatorResult result) {

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		String wordB = m_txtWordB.getText().toString();
		int size = result.TranslatedSuggestions.size();
		size = wordB.equals("") ? size : size + 1;

		final CharSequence[] words = new CharSequence[size];

		int i = 0;
		if (!wordB.equals("")) {
			words[i++] = wordB;
		}

		for (Word word : result.TranslatedSuggestions) {
			words[i++] = word.getWord();
		}

		builder.setTitle(getString(R.string.memo_selectTranslationTitle)).setCancelable(true)
				.setItems(words, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						m_txtWordB.setText(words[which]);
					}

				}).create().show();

	}

	@Override
	protected void onDataBind(Bundle savedInstanceState) {
		String memoId = getArguments().getString(MemoId);
		bindData(memoId);
	}

	@Override
	public void onDestroyView() {
		saveAndExit();
		super.onDestroyView();
	}

	private void saveAndExit() {
		m_memo.getWordA().setWord(m_txtWordA.getText().toString());
		m_memo.getWordB().setWord(m_txtWordB.getText().toString());
		m_memo.setActive(m_chbEnabled.isChecked());
		m_memo.getWordA().setLanguage(((LanguageView) m_spLanguageA.getSelectedItem()).getLanguage());
		m_memo.getWordB().setLanguage(((LanguageView) m_spLanguageB.getSelectedItem()).getLanguage());

		m_memoAdapter.update(m_memo);
	}
}
