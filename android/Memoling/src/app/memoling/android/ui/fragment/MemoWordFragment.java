package app.memoling.android.ui.fragment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import app.memoling.android.R;
import app.memoling.android.adapter.DictionaryAdapter;
import app.memoling.android.adapter.ThesaurusAdapter;
import app.memoling.android.adapter.WikiDefinitionAdapter;
import app.memoling.android.adapter.WikiSynonymAdapter;
import app.memoling.android.adapter.WikiTranslationAdapter;
import app.memoling.android.audio.TextToSpeechHelper;
import app.memoling.android.entity.Language;
import app.memoling.android.entity.Memo;
import app.memoling.android.entity.MemoSentence;
import app.memoling.android.entity.QuizletDefinition;
import app.memoling.android.entity.WikiDefinition;
import app.memoling.android.entity.WikiSynonym;
import app.memoling.android.entity.WikiTranslation;
import app.memoling.android.entity.Word;
import app.memoling.android.helper.Helper;
import app.memoling.android.thread.WorkerThread;
import app.memoling.android.translator.IAllTranslatorComplete;
import app.memoling.android.translator.Translator;
import app.memoling.android.translator.TranslatorResult;
import app.memoling.android.ui.ResourceManager;
import app.memoling.android.ui.fragment.MemoFragment.IMemoPagerFragment;
import app.memoling.android.wiktionary.WiktionaryDb;

public class MemoWordFragment extends Fragment implements
		OnEditorActionListener, IAllTranslatorComplete, IMemoPagerFragment {

	private ScrollView m_layScrollView;
	private TextView m_txtLanguage;
	protected EditText m_txtWord;
	private EditText m_txtDescriptionMemoling;
	private TextView m_lblDescriptionQuizlet;
	private WebView m_vwDescriptionWiktionary;
	private TextView m_lblSentencesTatoeba;
	private TextView m_lblSentencesQuizlet;
	private TextView m_txtSynonyms;
	private FrameLayout m_layWebSearch;
	private WebView m_webSearch;

	private RelativeLayout m_laySynonyms;
	private RelativeLayout m_layDefinitionQuizlet;
	private RelativeLayout m_layDefinitionWiki;
	private RelativeLayout m_laySentencesTatoeba;
	private RelativeLayout m_laySentencesQuizlet;

	protected Memo m_memo;
	private Runnable m_memoDelayedRunnable;
	private Runnable m_sentenceDelayedRunnable;
	private Runnable m_quizletDefinitionsRunnable;

	private boolean m_isWordA;
	
	private TextToSpeechHelper m_textToSpeechHelper;

	@SuppressLint("SetJavaScriptEnabled")
	public View onCreateView(boolean isWordA, LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		View contentView = inflater.inflate(R.layout.memo_word, container,
				false);

		m_isWordA = isWordA;

		ResourceManager resources = new ResourceManager(getActivity());
		Typeface thinFont = resources.getLightFont();

		m_layScrollView = (ScrollView) contentView
				.findViewById(R.id.memo_layScrollView);

		m_txtLanguage = (TextView) contentView.findViewById(R.id.memo_txtLang);


		m_txtWord = (EditText) contentView.findViewById(R.id.memo_txtWord);
		m_txtWord.setOnEditorActionListener(this);
		m_txtWord.setTypeface(thinFont);
		m_txtWord.addTextChangedListener(new TxtWordAEventHandler());

		m_txtDescriptionMemoling = (EditText) contentView
				.findViewById(R.id.memo_txtDescriptionMemoling);
		m_txtDescriptionMemoling
				.addTextChangedListener(new TxtDescriptionEventHandler());

		m_layDefinitionQuizlet = (RelativeLayout) contentView.findViewById(R.id.memo_layDefinitionQuzilet);
		m_lblDescriptionQuizlet = (TextView) contentView
				.findViewById(R.id.memo_lblDescriptionQuizlet);
		m_layDefinitionWiki = (RelativeLayout) contentView.findViewById(R.id.memo_layDefinitionWiki);
		m_vwDescriptionWiktionary = (WebView) contentView
				.findViewById(R.id.memo_vwDefinitionWiktionary);

		m_laySentencesTatoeba = (RelativeLayout) contentView.findViewById(R.id.memo_laySentenceTatoeba);
		m_lblSentencesTatoeba = (TextView) contentView
				.findViewById(R.id.memo_lblSentencesTatoeba);
		m_laySentencesQuizlet = (RelativeLayout) contentView.findViewById(R.id.memo_laySentenceQuizlet);
		m_lblSentencesQuizlet = (TextView) contentView
				.findViewById(R.id.memo_lblSentencesQuizlet);

		m_laySynonyms = (RelativeLayout) contentView
				.findViewById(R.id.memo_laySynonyms);
		m_txtSynonyms = (TextView) contentView
				.findViewById(R.id.memo_txtSynonyms);

		m_webSearch = (WebView) contentView.findViewById(R.id.memo_webSearch);
		m_layWebSearch = (FrameLayout) contentView
				.findViewById(R.id.memo_layWebSearch);
		m_webSearch.getSettings().setJavaScriptEnabled(true);
		m_webSearch.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return false;
			}
		});

		resources.setFont(contentView, R.id.textView1, thinFont);
		resources.setFont(contentView, R.id.textView2, thinFont);
		resources.setFont(contentView, R.id.textView3, thinFont);
		resources.setFont(contentView, R.id.textView4, thinFont);
		resources.setFont(contentView, R.id.textView5, thinFont);
		resources.setFont(contentView, R.id.textView6, thinFont);

		resources.setFont(contentView, R.id.memo_txtDescriptionMemoling,
				thinFont);
		resources.setFont(contentView, R.id.memo_lblDescriptionQuizlet,
				thinFont);
		resources.setFont(contentView, R.id.memo_lblSentencesTatoeba, thinFont);
		resources.setFont(contentView, R.id.memo_lblSentencesQuizlet, thinFont);
		resources.setFont(contentView, R.id.memo_txtSynonyms, thinFont);

		resources.setFont(contentView, R.id.memo_lblWord, thinFont);
		resources.setFont(contentView, R.id.memo_txtWord, thinFont);
		resources.setFont(contentView, R.id.memo_txtLang, thinFont);

		if (m_isWordA) {
			((TextView) contentView.findViewById(R.id.memo_lblWord))
					.setText(R.string.memo_lblWordA);
		} else {
			((TextView) contentView.findViewById(R.id.memo_lblWord))
					.setText(R.string.memo_lblWordB);
		}

		m_textToSpeechHelper = new TextToSpeechHelper(getActivity());
		
		return contentView;
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (actionId == EditorInfo.IME_ACTION_DONE) {
			InputMethodManager imm = (InputMethodManager) v.getContext()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			return true;
		}
		return false;
	}

	public boolean onBackPressed() {
		if (m_layWebSearch != null
				&& m_layWebSearch.getVisibility() == View.VISIBLE) {
			m_layWebSearch.setVisibility(View.GONE);
			return false;
		}

		return true;
	}

	private void searchWord() {
		Word from = new Word(m_txtWord.getText().toString().trim());
		Language fromLang = m_memo.getWordA().getLanguage();
		Language toLang = m_memo.getWordB().getLanguage();
		Toast.makeText(getActivity(),
				R.string.memo_selectTranslationProgress, Toast.LENGTH_SHORT)
				.show();
		new Translator(getActivity(), from, fromLang, toLang, null,
				MemoWordFragment.this);
	
	}

	private void copyWord() {
		Helper.copyToClipboard(getActivity(), m_txtWord.getText()
				.toString());
		Toast.makeText(getActivity(), R.string.memo_copied,
				Toast.LENGTH_SHORT).show();
	}

	private void playWord() {
		m_textToSpeechHelper.readText(getWord().getWord(), getWord()
				.getLanguage());
	}

	private void openThesaurus() {
		String searchUrl = String.format(ThesaurusAdapter.get(getWord()
				.getLanguage()), m_txtWord.getText().toString());

		m_layWebSearch.setVisibility(View.VISIBLE);
		m_webSearch.loadUrl(searchUrl);
	}

	private void openDictionary() {
		String searchUrl = String.format(DictionaryAdapter.get(getWord()
				.getLanguage()), m_txtWord.getText().toString());

		m_layWebSearch.setVisibility(View.VISIBLE);
		m_webSearch.loadUrl(searchUrl);
	}

	@Override
	public void onAllTranslatorComplete(List<TranslatorResult> results) {

		if (results == null) {
			Toast.makeText(getActivity(), R.string.memo_notFound,
					Toast.LENGTH_SHORT).show();
			return;
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		//String wordB = m_memo.getWordB().getWord();

		List<CharSequence> words = new ArrayList<CharSequence>();

		for (TranslatorResult result : results) {
			for (Word word : result.Translated) {
				if (!word.getWord().equals("")
						&& !words.contains(word.getWord())) {
					words.add(word.getWord().toLowerCase(Locale.getDefault()));
				}
			}
		}

		final CharSequence[] arrayWords = words.toArray(new CharSequence[words
				.size()]);

		builder.setTitle(getString(R.string.memo_selectTranslationTitle))
				.setCancelable(true)
				.setItems(arrayWords, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						m_memo.getWordB().setWord(arrayWords[which].toString());
						MemoSecondFragment.notifyDataChange(m_memo);
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

				m_txtLanguage.setText(getWord().getLanguage().getName(
						getActivity()));

				setMemoWiktionary();

				m_layScrollView.fullScroll(ScrollView.FOCUS_UP);
			};
		};

		if (getActivity() != null && m_lblSentencesTatoeba != null) {
			runnable.run();
		}
		m_memoDelayedRunnable = runnable;
	}

	private void setMemoWiktionary() {

		new WorkerThread<Void, Void, Void>() {

			private String m_synonyms;

			@Override
			protected Void doInBackground(Void... params) {

				WikiSynonymAdapter synonymAdapter = new WikiSynonymAdapter(
						getActivity());
				List<WikiSynonym> wikiSynonyms = synonymAdapter.get(
						getWord().getWord(), getWord().getLanguage());

				HashSet<String> synonyms = new HashSet<String>();
				if (wikiSynonyms != null) {
					for (WikiSynonym synonym : wikiSynonyms) {
						synonyms.add(synonym.getExpressionB());
					}

					StringBuilder sb = new StringBuilder();
					int i = 1;
					if (synonyms.size() > 0) {
						for (String synonym : synonyms) {
							sb.append(String.format("%d. %s\n", i++, synonym));
						}
						sb.setLength(sb.length() - 2);
						m_synonyms = sb.toString();
					}
				}

				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				
				if (m_txtSynonyms == null || m_laySynonyms == null || getActivity() == null) {
					// Closed before onPostExecute has been called
					return;
				}

				if (m_synonyms != null) {
					m_txtSynonyms.setText(m_synonyms);
				} else {
					m_txtSynonyms.setText(R.string.memo_notFound);
				}
			}

		}.execute();

		// Definitions
		new WorkerThread<Void, Void, Void>() {

			private HashSet<String> m_meanings;
			private List<WikiDefinition> m_wikiDefinitions;

			@Override
			protected Void doInBackground(Void... params) {

				WikiTranslationAdapter wikiTranslationAdapter = new WikiTranslationAdapter(
						getActivity());
				List<WikiTranslation> wikiTranslations = wikiTranslationAdapter
						.get(getWord().getWord(), getWord().getLanguage());

				m_meanings = new HashSet<String>();
				if (wikiTranslations != null) {
					for (WikiTranslation translation : wikiTranslations) {
						if (translation.getWikiTranslationMeaning() != null)
							m_meanings.add(translation
									.getWikiTranslationMeaning().getMeaning());
					}
				}

				WikiDefinitionAdapter wikiDefinitionAdapter = new WikiDefinitionAdapter(
						getActivity());
				m_wikiDefinitions = wikiDefinitionAdapter.get(getWord()
						.getWord(), getWord().getLanguage());

				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				StringBuilder wikiDefBuilder = new StringBuilder();
				String styles = "<style type=\"text/css\">@font-face { font-family: 'light-font'; src: url('Roboto-Light.ttf');} html, body { margin: 0px; padding: 0px; } html { padding-top: 10px; } body { font-size: 0.8em; font-family: 'light-font'; }</style>\n";
				wikiDefBuilder.append(styles);

				if (m_meanings.size() > 0) {
					wikiDefBuilder.append("<ol style=\"padding-left:25px;\">");

					for (String meaning : m_meanings) {
						wikiDefBuilder.append("<li>" + meaning + "</li>");
					}

					wikiDefBuilder.append("</ol>");
				}
				
				for(WikiDefinition definition : m_wikiDefinitions) {
					wikiDefBuilder.append(definition.getHtmlDefinition());
				}

				if (m_vwDescriptionWiktionary == null || getActivity() == null) {
					// Closed before onPostExecute has been called
					return;
				}

				if (m_wikiDefinitions.size() == 0 && m_meanings.size() == 0) {
					wikiDefBuilder.append(getString(R.string.memo_notFound));
				}

				m_vwDescriptionWiktionary.loadDataWithBaseURL(
						"file:///android_asset/", wikiDefBuilder.toString(),
						"text/html", "utf-8", null);
				m_vwDescriptionWiktionary.setBackgroundColor(getActivity()
						.getResources().getColor(R.color.content_background));
			}

		}.execute();

	}

	@Override
	public void setTatoeba(final List<MemoSentence> memoSentences) {

		Runnable runnable = new Runnable() {

			@Override
			public void run() {

				if(getActivity() == null) {
					return;
				}
				
				if (memoSentences == null || memoSentences.size() == 0) {
					m_lblSentencesTatoeba.setText(R.string.memo_notFound);
					return;
				}

				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < memoSentences.size(); i++) {
					MemoSentence memoSentence = memoSentences.get(i);
					sb.append(String.format("%2d. %s%s", i + 1,
							memoSentence.getOriginalSentence(),
							i != memoSentences.size() - 1 ? "\n" : ""));
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

	public void setQuizlet(final List<QuizletDefinition> definitions) {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				
				if(getActivity() == null) {
					return;
				}
				
				if(definitions == null) {
					m_lblDescriptionQuizlet.setText(R.string.memo_notFound);
					m_lblSentencesQuizlet.setText(R.string.memo_notFound);
					return;
				}
				
				StringBuilder sbDesc = new StringBuilder();
				StringBuilder sbSent = new StringBuilder();

				for (int i = 0; i < definitions.size(); i++) {
					QuizletDefinition definition = definitions.get(i);
					sbDesc.append(String.format("%d. %s - %s%s", i + 1,
							definition.getSpeechPart(),
							Html.fromHtml(definition.getDefinition()),
							i != definitions.size() - 1 ? "\n" : ""));

					for (int j = 0; j < definition.getExamples().size(); j++) {
						String sentence = definition.getExamples().get(j);
						sbSent.append(String.format("%d.%d. %s%s", i + 1,
								j + 1, Html.fromHtml(sentence), j != definition
										.getExamples().size() - 1 ? "<br/>"
										: ""));
					}
				}

				if(sbDesc.length() == 0) {
					m_lblDescriptionQuizlet.setText(R.string.memo_notFound);
				} else {
					m_lblDescriptionQuizlet.setText(sbDesc.toString());
				}

				if (sbSent.length() == 0) {
					m_lblSentencesQuizlet.setText(R.string.memo_notFound);
				} else {
					m_lblSentencesQuizlet.setText(Html.fromHtml(sbSent
							.toString()));
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
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

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
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

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
	
	public void updateDrawer() {
		MemoFragment parent = (MemoFragment)getParentFragment();
		
		boolean thesaurus = ThesaurusAdapter
				.isSupported(getWord().getLanguage());

		boolean dictionary = ThesaurusAdapter
				.isSupported(getWord().getLanguage());

		parent.updateDrawer(m_isWordA, thesaurus, dictionary, true, true);
	}

	@Override
	public void onDrawerItemSelected(int drawerAction) {

		if(drawerAction == MemoFragment.DrawerActionAudio) {
			playWord();
		} else if(drawerAction == MemoFragment.DrawerActionCopy) {
			copyWord();
		} else if(drawerAction == MemoFragment.DrawerActionDictionary) {
			openDictionary();
		} else if(drawerAction == MemoFragment.DrawerActionRefresh) {
			searchWord();
		} else if(drawerAction == MemoFragment.DrawerActionThesaurus) {
			openThesaurus();
		}
		
	}

}
