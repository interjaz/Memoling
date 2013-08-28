package app.memoling.android.ui.fragment;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
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
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import app.memoling.android.R;
import app.memoling.android.adapter.DictionaryAdapter;
import app.memoling.android.adapter.ThesaurusAdapter;
import app.memoling.android.entity.Language;
import app.memoling.android.entity.Memo;
import app.memoling.android.entity.MemoSentence;
import app.memoling.android.entity.Word;
import app.memoling.android.helper.Helper;
import app.memoling.android.translator.ITranslateComplete;
import app.memoling.android.translator.Translator;
import app.memoling.android.translator.TranslatorResult;
import app.memoling.android.ui.ResourceManager;
import app.memoling.android.ui.fragment.MemoFragment.IMemoPagerFragment;

public class MemoFirstFragment extends Fragment implements OnEditorActionListener, ITranslateComplete,
		IMemoPagerFragment {

	private TextView m_lblLanguageA;
	private EditText m_txtWordA;
	private EditText m_txtDescription;
	private TextView m_lblSentences;
	private ImageButton m_btnSearchWord;
	private ImageButton m_btnCopyWord;
	private Button m_btnThesaurusWord;
	private Button m_btnDictionaryWord;
	private FrameLayout m_layWebSearch;
	private WebView m_webSearch;

	private Memo m_memo;
	private Runnable m_memoDelayedRunnable;
	private Runnable m_sentenceDelayedRunnable;

	@SuppressLint("SetJavaScriptEnabled")
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View contentView = inflater.inflate(R.layout.memo_first, container, false);

		ResourceManager resources = new ResourceManager(getActivity());
		Typeface thinFont = resources.getThinFont();

		m_lblLanguageA = (TextView) contentView.findViewById(R.id.memo_lblLangA);

		m_btnSearchWord = (ImageButton) contentView.findViewById(R.id.memo_btnSearchWordA);
		m_btnSearchWord.setOnClickListener(new BtnSearchWordAEventHandler());

		m_txtWordA = (EditText) contentView.findViewById(R.id.memo_txtWordA);
		m_txtWordA.setOnEditorActionListener(this);
		m_txtWordA.setTypeface(thinFont);
		m_txtWordA.addTextChangedListener(new TxtWordAEventHandler());

		m_btnCopyWord = (ImageButton) contentView.findViewById(R.id.memo_btnCopyWordA);
		m_btnCopyWord.setOnClickListener(new BtnCopyWordEventHandler());

		m_txtDescription = (EditText) contentView.findViewById(R.id.memo_txtDescription);
		m_txtDescription.addTextChangedListener(new TxtDescriptionEventHandler());
		m_lblSentences = (TextView) contentView.findViewById(R.id.memo_lblSentences);

		m_webSearch = (WebView) contentView.findViewById(R.id.memo_webSearch);
		m_layWebSearch = (FrameLayout) contentView.findViewById(R.id.memo_layWebSearch);
		m_webSearch.getSettings().setJavaScriptEnabled(true);
		m_webSearch.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return false;
			}
		});

		m_btnThesaurusWord = (Button) contentView.findViewById(R.id.memo_btnThesaurusWordA);
		m_btnThesaurusWord.setOnClickListener(new BtnThesaurusEventHandler());
		m_btnDictionaryWord = (Button) contentView.findViewById(R.id.memo_btnDictionaryWordA);
		m_btnDictionaryWord.setOnClickListener(new BtnDictionaryEventHandler());

		resources.setFont(m_btnThesaurusWord, thinFont);
		resources.setFont(m_btnDictionaryWord, thinFont);

		resources.setFont(contentView, R.id.textView1, thinFont);
		resources.setFont(contentView, R.id.textView2, thinFont);
		resources.setFont(contentView, R.id.textView3, thinFont);
		resources.setFont(contentView, R.id.textView4, thinFont);
		resources.setFont(contentView, R.id.memo_lblWordA, thinFont);
		resources.setFont(contentView, R.id.memo_txtDescription, thinFont);
		resources.setFont(contentView, R.id.memo_lblSentences, thinFont);
		resources.setFont(contentView, R.id.memo_lblLangA, thinFont);
		
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
			Word from = new Word(m_txtWordA.getText().toString().trim());
			Language fromLang = m_memo.getWordA().getLanguage();
			Language toLang = m_memo.getWordB().getLanguage();
			Toast.makeText(getActivity(), R.string.memo_selectTranslationProgress, Toast.LENGTH_SHORT).show();
			new Translator(from, fromLang, toLang, MemoFirstFragment.this);
		}
	}

	private class BtnCopyWordEventHandler implements OnClickListener {

		@Override
		public void onClick(View v) {
			Helper.copyToClipboard(getActivity(), m_txtWordA.getText().toString());
			Toast.makeText(getActivity(), R.string.memo_copied, Toast.LENGTH_SHORT).show();
		}
	}

	private class BtnThesaurusEventHandler implements OnClickListener {

		@Override
		public void onClick(View v) {
			String searchUrl = String.format(ThesaurusAdapter.get(m_memo.getWordA().getLanguage()), m_txtWordA
					.getText().toString());

			m_layWebSearch.setVisibility(View.VISIBLE);
			m_webSearch.loadUrl(searchUrl);
		}
	}

	private class BtnDictionaryEventHandler implements OnClickListener {

		@Override
		public void onClick(View v) {
			String searchUrl = String.format(DictionaryAdapter.get(m_memo.getWordA().getLanguage()), m_txtWordA
					.getText().toString());

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
	}

	@Override
	public void setMemo(Memo memo) {
		m_memo = memo;

		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				m_txtWordA.setText(m_memo.getWordA().getWord());
				m_txtDescription.setText(m_memo.getWordA().getDescription());

				m_btnThesaurusWord.setEnabled(ThesaurusAdapter.isSupported(m_memo.getWordA().getLanguage()));
				m_btnDictionaryWord.setEnabled(ThesaurusAdapter.isSupported(m_memo.getWordA().getLanguage()));
				m_lblLanguageA.setText(m_memo.getWordA().getLanguage().getName());

				m_btnThesaurusWord.setEnabled(ThesaurusAdapter.isSupported(m_memo.getWordA().getLanguage()));
				m_btnDictionaryWord.setEnabled(ThesaurusAdapter.isSupported(m_memo.getWordA().getLanguage()));
			};
		};

		if (getActivity() != null && m_lblSentences != null) {
			runnable.run();
		}
		m_memoDelayedRunnable = runnable;
	}

	@Override
	public void setSentences(final ArrayList<MemoSentence> memoSentences) {

		Runnable runnable = new Runnable() {

			@Override
			public void run() {

				if (memoSentences == null || memoSentences.size() == 0) {
					m_lblSentences.setText(R.string.memo_sentencesNoSentences);
					return;
				}

				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < memoSentences.size(); i++) {
					MemoSentence memoSentence = memoSentences.get(i);
					sb.append(String.format("%d. %s\n", i + 1, memoSentence.getOriginalSentence()));
				}

				m_lblSentences.setText(sb.toString());

			}

		};

		if (getActivity() != null && m_lblSentences != null) {
			runnable.run();
		}

		// Set it anyway, this due to 'nice' behaviour of fragmentpageadapter
		m_sentenceDelayedRunnable = runnable;
	}

	private class TxtDescriptionEventHandler implements TextWatcher {

		@Override
		public void afterTextChanged(Editable s) {
			if (m_memo != null) {
				m_memo.getWordA().setDescription(s.toString());
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
				m_memo.getWordA().setWord(s.toString());
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
	public int getPosition() {
		return 0;
	}

}
