package app.memoling.android.ui.fragment;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import app.memoling.android.R;
import app.memoling.android.adapter.DictionaryAdapter;
import app.memoling.android.adapter.ThesaurusAdapter;
import app.memoling.android.entity.Language;
import app.memoling.android.entity.Memo;
import app.memoling.android.entity.MemoSentence;
import app.memoling.android.entity.QuizletDefinition;
import app.memoling.android.entity.Word;
import app.memoling.android.helper.Helper;
import app.memoling.android.helper.TextToSpeechHelper;
import app.memoling.android.translator.ITranslateComplete;
import app.memoling.android.translator.Translator;
import app.memoling.android.translator.TranslatorResult;
import app.memoling.android.ui.ResourceManager;
import app.memoling.android.ui.fragment.MemoFragment.IMemoPagerFragment;

public class MemoWordFragment extends Fragment implements OnEditorActionListener, ITranslateComplete,
		IMemoPagerFragment {

	private ScrollView m_layScrollView;
	private TextView m_txtLanguage;
	private EditText m_txtWord;
	private EditText m_txtDescriptionMemoling;
	private TextView m_lblDescriptionQuizlet;
	private TextView m_lblSentencesTatoeba;
	private TextView m_lblSentencesQuizlet;
	private ImageButton m_btnSearchWord;
	private ImageButton m_btnCopyWord;
	private ImageButton m_btnSpeech;
	private Button m_btnThesaurusWord;
	private Button m_btnDictionaryWord;
	private FrameLayout m_layWebSearch;
	private WebView m_webSearch;

	private Memo m_memo;
	private Runnable m_memoDelayedRunnable;
	private Runnable m_sentenceDelayedRunnable;
	private Runnable m_quizletDefinitionsRunnable;

	private boolean m_isWordA;

	private TextToSpeechHelper m_textToSpeechHelper;

	public View onCreateView(boolean isWordA, LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View contentView = inflater.inflate(R.layout.memo_word, container, false);

		m_isWordA = isWordA;

		ResourceManager resources = new ResourceManager(getActivity());
		Typeface thinFont = resources.getLightFont();

		m_layScrollView = (ScrollView) contentView.findViewById(R.id.memo_layScrollView);
		
		m_txtLanguage = (TextView) contentView.findViewById(R.id.memo_txtLang);

		m_btnSearchWord = (ImageButton) contentView.findViewById(R.id.memo_btnSearchWord);
		m_btnSearchWord.setOnClickListener(new BtnSearchWordAEventHandler());

		m_txtWord = (EditText) contentView.findViewById(R.id.memo_txtWord);
		m_txtWord.setOnEditorActionListener(this);
		m_txtWord.setTypeface(thinFont);
		m_txtWord.addTextChangedListener(new TxtWordAEventHandler());

		m_btnCopyWord = (ImageButton) contentView.findViewById(R.id.memo_btnCopyWord);
		m_btnCopyWord.setOnClickListener(new BtnCopyWordEventHandler());

		m_btnSpeech = (ImageButton) contentView.findViewById(R.id.memo_btnSpeech);
		m_btnSpeech.setOnClickListener(new BtnSpeechEventHandler());

		m_txtDescriptionMemoling = (EditText) contentView.findViewById(R.id.memo_txtDescriptionMemoling);
		m_txtDescriptionMemoling.addTextChangedListener(new TxtDescriptionEventHandler());

		m_lblDescriptionQuizlet = (TextView) contentView.findViewById(R.id.memo_lblDescriptionQuizlet);

		m_lblSentencesTatoeba = (TextView) contentView.findViewById(R.id.memo_lblSentencesTatoeba);
		m_lblSentencesQuizlet = (TextView) contentView.findViewById(R.id.memo_lblSentencesQuizlet);

		m_webSearch = (WebView) contentView.findViewById(R.id.memo_webSearch);
		m_layWebSearch = (FrameLayout) contentView.findViewById(R.id.memo_layWebSearch);
		m_webSearch.getSettings().setJavaScriptEnabled(true);
		m_webSearch.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return false;
			}
		});

		m_btnThesaurusWord = (Button) contentView.findViewById(R.id.memo_btnThesaurusWord);
		m_btnThesaurusWord.setOnClickListener(new BtnThesaurusEventHandler());
		m_btnDictionaryWord = (Button) contentView.findViewById(R.id.memo_btnDictionaryWord);
		m_btnDictionaryWord.setOnClickListener(new BtnDictionaryEventHandler());

		TabHost tabHost = null;
		TabSpec tabSpec = null;
		// Definition tabs
		tabHost = (TabHost) contentView.findViewById(R.id.memo_tabDefinitions);
		tabHost.setup();

		tabSpec = tabHost.newTabSpec("TAB0");
		tabSpec.setContent(R.id.memo_tabDefinitionMemoling);
		tabSpec.setIndicator(getString(R.string.memo_definitionsMemoling));
		tabHost.addTab(tabSpec);

		tabSpec = tabHost.newTabSpec("TAB1");
		tabSpec.setContent(R.id.memo_tabDefinitionQuizlet);
		tabSpec.setIndicator(getString(R.string.memo_definitionsQuizlet));
		tabHost.addTab(tabSpec);

		// Sentence tabs
		tabHost = (TabHost) contentView.findViewById(R.id.memo_tabSentences);
		tabHost.setup();

		tabSpec = tabHost.newTabSpec("TAB0");
		tabSpec.setContent(R.id.memo_tabSentencesTatoeba);
		tabSpec.setIndicator(getString(R.string.memo_sentencesTatoeba));
		tabHost.addTab(tabSpec);

		tabSpec = tabHost.newTabSpec("TAB1");
		tabSpec.setContent(R.id.memo_tabSentencesQuizlet);
		tabSpec.setIndicator(getString(R.string.memo_sentencesQuizlet));
		tabHost.addTab(tabSpec);

		resources.setFont(m_btnThesaurusWord, thinFont);
		resources.setFont(m_btnDictionaryWord, thinFont);

		resources.setFont(contentView, R.id.memo_lblLang, thinFont);
		resources.setFont(contentView, R.id.textView1, thinFont);
		resources.setFont(contentView, R.id.textView2, thinFont);
		resources.setFont(contentView, R.id.memo_txtDescriptionMemoling, thinFont);
		resources.setFont(contentView, R.id.memo_lblDescriptionQuizlet, thinFont);
		resources.setFont(contentView, R.id.memo_lblSentencesTatoeba, thinFont);
		resources.setFont(contentView, R.id.memo_lblSentencesQuizlet, thinFont);

		resources.setFont(contentView, R.id.memo_lblWord, thinFont);
		resources.setFont(contentView, R.id.memo_txtWord, thinFont);
		resources.setFont(contentView, R.id.memo_lblLang, thinFont);
		resources.setFont(contentView, R.id.memo_txtLang, thinFont);

		if (m_isWordA) {
			m_btnSearchWord.setVisibility(View.VISIBLE);
			((TextView) contentView.findViewById(R.id.memo_lblWord)).setText(R.string.memo_lblWordA);
			((TextView) contentView.findViewById(R.id.memo_lblLang)).setText(R.string.memo_lblLangA);
		} else {
			m_btnSearchWord.setVisibility(View.GONE);
			((TextView) contentView.findViewById(R.id.memo_lblWord)).setText(R.string.memo_lblWordB);
			((TextView) contentView.findViewById(R.id.memo_lblLang)).setText(R.string.memo_lblLangB);
		}

		m_textToSpeechHelper = new TextToSpeechHelper(getActivity());
		
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

	public boolean onBackPressed() {
		if (m_layWebSearch != null && m_layWebSearch.getVisibility() == View.VISIBLE) {
			m_layWebSearch.setVisibility(View.GONE);
			return false;
		}

		return true;
	}

	private class BtnSearchWordAEventHandler implements OnClickListener {
		@Override
		public void onClick(View v) {
			Word from = new Word(m_txtWord.getText().toString().trim());
			Language fromLang = m_memo.getWordA().getLanguage();
			Language toLang = m_memo.getWordB().getLanguage();
			Toast.makeText(getActivity(), R.string.memo_selectTranslationProgress, Toast.LENGTH_SHORT).show();
			new Translator(from, fromLang, toLang, MemoWordFragment.this);
		}
	}

	private class BtnCopyWordEventHandler implements OnClickListener {

		@Override
		public void onClick(View v) {
			Helper.copyToClipboard(getActivity(), m_txtWord.getText().toString());
			Toast.makeText(getActivity(), R.string.memo_copied, Toast.LENGTH_SHORT).show();
		}
	}

	private class BtnSpeechEventHandler implements OnClickListener {

		@Override
		public void onClick(View v) {
			m_textToSpeechHelper.readText(getWord().getWord(), getWord().getLanguage());
		}
	}

	private class BtnThesaurusEventHandler implements OnClickListener {

		@Override
		public void onClick(View v) {
			String searchUrl = String.format(ThesaurusAdapter.get(getWord().getLanguage()), m_txtWord.getText()
					.toString());

			m_layWebSearch.setVisibility(View.VISIBLE);
			m_webSearch.loadUrl(searchUrl);
		}
	}

	private class BtnDictionaryEventHandler implements OnClickListener {

		@Override
		public void onClick(View v) {
			String searchUrl = String.format(DictionaryAdapter.get(getWord().getLanguage()), m_txtWord.getText()
					.toString());

			m_layWebSearch.setVisibility(View.VISIBLE);
			m_webSearch.loadUrl(searchUrl);
		}
	}

	@Override
	public void onTranslateComplete(TranslatorResult result) {

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		String wordB = m_memo.getWordB().getWord();
		int size = result.TranslatedSuggestions.size();
		size = wordB.equals("") ? size : size + 1;

		final CharSequence[] words = new CharSequence[size];

		int i = 0;
		if (!wordB.equals("")) {
			words[i++] = wordB;
		}

		for (Word word : result.TranslatedSuggestions) {
			words[i++] = word.getWord().toLowerCase();
		}

		builder.setTitle(getString(R.string.memo_selectTranslationTitle)).setCancelable(true)
				.setItems(words, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						m_memo.getWordB().setWord(words[which].toString());
					}

				}).create().show();

	}

	@Override
	public void onResume() {
		super.onResume();

		if (m_memoDelayedRunnable != null) {
			m_memoDelayedRunnable.run();
		}

		if (m_sentenceDelayedRunnable != null) {
			m_sentenceDelayedRunnable.run();
		}

		if (m_quizletDefinitionsRunnable != null) {
			m_quizletDefinitionsRunnable.run();
		}
	}

	@Override
	public void setMemo(Memo memo) {
		m_memo = memo;

		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				m_txtDescriptionMemoling.setText(getWord().getDescription());
				m_txtWord.setText(getWord().getWord());
				m_txtWord.clearFocus();

				m_btnThesaurusWord.setEnabled(ThesaurusAdapter.isSupported(getWord().getLanguage()));
				m_btnDictionaryWord.setEnabled(ThesaurusAdapter.isSupported(getWord().getLanguage()));
				m_txtLanguage.setText(getWord().getLanguage().getName());

				m_btnThesaurusWord.setEnabled(ThesaurusAdapter.isSupported(getWord().getLanguage()));
				m_btnDictionaryWord.setEnabled(ThesaurusAdapter.isSupported(getWord().getLanguage()));

				m_layScrollView.fullScroll(ScrollView.FOCUS_UP);
			};
		};

		if (getActivity() != null && m_lblSentencesTatoeba != null) {
			runnable.run();
		}
		m_memoDelayedRunnable = runnable;
	}

	@Override
	public void setTatoeba(final ArrayList<MemoSentence> memoSentences) {

		Runnable runnable = new Runnable() {

			@Override
			public void run() {

				if (memoSentences == null || memoSentences.size() == 0) {
					m_lblSentencesTatoeba.setText(R.string.memo_notFound);
					return;
				}

				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < memoSentences.size(); i++) {
					MemoSentence memoSentence = memoSentences.get(i);
					sb.append(String.format("%d. %s\n", i + 1, memoSentence.getOriginalSentence()));
				}

				m_lblSentencesTatoeba.setText(sb.toString());

			}

		};

		if (getActivity() != null && m_lblSentencesTatoeba != null) {
			runnable.run();
		}

		// Set it anyway, this due to 'nice' behaviour of fragmentpageadapter
		m_sentenceDelayedRunnable = runnable;
	}

	public void setQuizlet(final ArrayList<QuizletDefinition> definitions) {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				if (definitions == null || definitions.size() == 0) {
					m_lblDescriptionQuizlet.setText(R.string.memo_notFound);
					m_lblSentencesQuizlet.setText(R.string.memo_notFound);
					return;
				}

				StringBuilder sbDesc = new StringBuilder();
				StringBuilder sbSent = new StringBuilder();
				int i = 0;
				for (QuizletDefinition definition : definitions) {
					i++;
					sbDesc.append(String.format("%d. %s - %s\n", i, definition.getSpeechPart(),
							Html.fromHtml(definition.getDefinition())));

					int j = 0;
					for (String sentence : definition.getExamples()) {
						j++;
						sbSent.append(String.format("%d.%d. %s\n", i, j, Html.fromHtml(sentence)));
					}
				}

				m_lblDescriptionQuizlet.setText(sbDesc.toString());

				if (sbSent.length() == 0) {
					m_lblSentencesQuizlet.setText(R.string.memo_notFound);
				} else {
					m_lblSentencesQuizlet.setText(Html.fromHtml(sbSent.toString()));
				}
			}
		};

		if (getActivity() != null && m_lblSentencesQuizlet != null) {
			runnable.run();
		}

		// Set it anyway, this due to 'nice' behaviour of fragmentpageadapter
		m_quizletDefinitionsRunnable = runnable;
	}

	private class TxtDescriptionEventHandler implements TextWatcher {

		@Override
		public void afterTextChanged(Editable s) {
			if (m_memo != null) {
				getWord().setDescription(s.toString());
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {

		}

	}

	private class TxtWordAEventHandler implements TextWatcher {

		@Override
		public void afterTextChanged(Editable s) {
			if (m_memo != null) {
				getWord().setWord(s.toString());
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {

		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Do not call parent - inside view pager
		// super.onActivityResult(requestCode, resultCode, data);

		m_textToSpeechHelper.onActivityResult(requestCode, resultCode, data);
	}

	public Word getWord() {
		if (m_isWordA) {
			return m_memo.getWordA();
		} else {
			return m_memo.getWordB();
		}
	}

	@Override
	public int getPosition() {
		if (m_isWordA) {
			return 0;
		} else {
			return 1;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (m_textToSpeechHelper != null) {
			m_textToSpeechHelper.shutdown();
		}
	}

}