package app.memoling.android.ui.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import app.memoling.android.R;
import app.memoling.android.adapter.MemoAdapter;
import app.memoling.android.adapter.MemoBaseAdapter;
import app.memoling.android.db.DatabaseHelper.Order;
import app.memoling.android.db.SqliteUpdater;
import app.memoling.android.entity.Language;
import app.memoling.android.entity.Memo;
import app.memoling.android.entity.Word;
import app.memoling.android.helper.AppLog;
import app.memoling.android.helper.Helper;
import app.memoling.android.helper.Lazy;
import app.memoling.android.helper.ShareHelper;
import app.memoling.android.helper.VoiceInputHelper;
import app.memoling.android.preference.custom.MemoListPreference;
import app.memoling.android.thread.WorkerThread;
import app.memoling.android.translator.ITranslatorComplete;
import app.memoling.android.translator.Translator;
import app.memoling.android.translator.TranslatorResult;
import app.memoling.android.ui.ApplicationActivity;
import app.memoling.android.ui.ApplicationFragment;
import app.memoling.android.ui.FacebookFragment;
import app.memoling.android.ui.ResourceManager;
import app.memoling.android.ui.activity.PreferenceLegacyActivity;
import app.memoling.android.ui.activity.ReviewActivity;
import app.memoling.android.ui.adapter.DrawerAdapter;
import app.memoling.android.ui.adapter.ModifiableInjectableAdapter;
import app.memoling.android.ui.adapter.ScrollableModifiableComplexTextAdapter;
import app.memoling.android.ui.adapter.ScrollableModifiableComplexTextAdapter.OnScrollFinishedListener;
import app.memoling.android.ui.control.LanguageSpinner;
import app.memoling.android.ui.view.DrawerView;
import app.memoling.android.ui.view.LanguageView;
import app.memoling.android.ui.view.MemoView;
import app.memoling.android.ui.view.TranslatedView;
import app.memoling.android.wordlist.IWordsFindComplete;
import app.memoling.android.wordlist.WordsFindResult;
import app.memoling.android.wordlist.WordsFinder;

import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;

public class MemoListFragment extends FacebookFragment implements ITranslatorComplete, IWordsFindComplete {

	public final static String MemoBaseId = "MemoBaseId";
	public final static String NotificationId = "NotificationId";
	public final static String MemoId = "MemoId";
	private final static int InvalidNotificationId = -1;

	public final static int VoiceInputRequestCode = 1;

	private Button m_btnSave;
	private EditText m_txtAdd;
	private EditText m_txtAddTranslated;
	private ListView m_lstWords;

	private ModifiableInjectableAdapter<TranslatedView> m_suggestionAdapter;
	private WordsFinder m_wordsFinder;
	private DelayedLookup m_lastLookup;
	private int m_delayedLookupDelay = 500;
	private ModifiableInjectableAdapter<MemoView> m_wordsAdapter;

	private ListView m_lstSuggestions;

	private LanguageSpinner m_spLanguageFrom;
	private LanguageSpinner m_spLanguageTo;
	private Button m_btnLanguageSwap;

	private MemoAdapter m_memoAdapter;
	private ArrayList<Memo> m_memos;
	private ArrayList<MemoView> m_memosViews;
	private String m_memoBaseId;

	private LinearLayout m_layWhatsNew;
	private TextView m_lblWhatsNewTitle;
	private TextView m_lblWhatsNewContent;
	private Button m_btnWhatsNew;

	private String m_saveErrorMessage;

	private MemoView m_selectedItem;

	private int m_lastSearchLimit = 0;
	private int m_searchLimit = 10;

	private FragmentState m_fragmentState;

	private ShareHelper m_shareHelper;

	private SuggestionComparator m_suggestionComparator;

	//
	// Base class implementation
	//

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View contentView = super.onCreateView(inflater, container, savedInstanceState,
				inflater.inflate(R.layout.fragment_memolist, container, false));

		ResourceManager resources = getResourceManager();
		Typeface thinFont = resources.getLightFont();
		Typeface blackFont = resources.getBlackFont();

		// Language spinners
		m_spLanguageFrom = (LanguageSpinner) contentView.findViewById(R.id.memolist_spLanguageFrom);
		m_spLanguageTo = (LanguageSpinner) contentView.findViewById(R.id.memolist_spLanguageTo);

		m_btnLanguageSwap = (Button) contentView.findViewById(R.id.memolist_btnLanguageSwap);
		resources.setFont(m_btnLanguageSwap, thinFont);
		m_btnLanguageSwap.setOnClickListener(new BtnLanguageSwap());

		// Error message
		m_saveErrorMessage = getString(R.string.memolist_saveErrorMessage);

		// Save button
		m_btnSave = (Button) contentView.findViewById(R.id.memolist_btnSave);
		m_btnSave.setOnClickListener(new BtnSaveEventHanlder());
		m_btnSave.setTypeface(thinFont);

		// AutoCompleteTextView
		m_txtAdd = (EditText) contentView.findViewById(R.id.memolist_txtAddMemo);
		TxtAddTextEventHandler txtAddTextEventHandler = new TxtAddTextEventHandler();
		m_txtAdd.addTextChangedListener(txtAddTextEventHandler);
		m_txtAdd.setTypeface(thinFont);

		m_txtAddTranslated = (EditText) contentView.findViewById(R.id.memolist_txtAddMemoTranslated);
		m_txtAddTranslated.setTypeface(thinFont);

		m_lstSuggestions = (ListView) contentView.findViewById(R.id.memolist_lstSuggestions);
		LstSuggestionEventHandler lstSuggestionEventHandler = new LstSuggestionEventHandler();
		m_suggestionAdapter = new ModifiableInjectableAdapter<TranslatedView>(getActivity(),
				R.layout.adapter_memolist_suggestion, resources, false);
		// m_suggestionAdapter.setOnScrollListener(lstSuggestionEventHandler);
		m_lstSuggestions.setAdapter(m_suggestionAdapter);
		m_lstSuggestions.setOnItemClickListener(lstSuggestionEventHandler);

		// List View
		m_lstWords = (ListView) contentView.findViewById(R.id.memolist_list);
		m_wordsAdapter = new ModifiableInjectableAdapter<MemoView>(getActivity(), R.layout.adapter_memolist_listview,
				resources, false);
		m_lstWords.setAdapter(m_wordsAdapter);
		m_lstWords.setOnItemClickListener(new LstWordsEventHandler());
		registerForContextMenu(m_lstWords);
		m_lstWords.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		m_lstWords.setStackFromBottom(true);

		// Word finder
		m_wordsFinder = new WordsFinder(getActivity());

		// What's new
		m_layWhatsNew = (LinearLayout) contentView.findViewById(R.id.memolist_layWhatsNew);
		m_lblWhatsNewTitle = (TextView) contentView.findViewById(R.id.memolist_lblWhatsNewTitle);
		resources.setFont(m_lblWhatsNewTitle, thinFont);
		m_lblWhatsNewContent = (TextView) contentView.findViewById(R.id.memolist_lblWhatsNewContent);
		resources.setFont(m_lblWhatsNewContent, thinFont);
		m_btnWhatsNew = (Button) contentView.findViewById(R.id.memolist_btnWhatsNew);
		m_btnWhatsNew.setOnClickListener(new BtnWhatsNewEventHandler());
		resources.setFont(m_btnWhatsNew, thinFont);

		m_fragmentState = (m_fragmentState == null) ? new FragmentState().fromBundle(savedInstanceState)
				: m_fragmentState;

		// Share Helper
		m_shareHelper = new ShareHelper(this, false);

		// Look for send action
		Intent sendIntent = getActivity().getIntent();
		if (sendIntent != null) {
			String action = sendIntent.getAction();
			String type = sendIntent.getType();
			if (Intent.ACTION_SEND.equals(action) && type != null) {
				if ("text/plain".equals(type)) {
					String sharedText = sendIntent.getStringExtra(Intent.EXTRA_TEXT);
					if (sharedText != null) {
						m_txtAdd.setText(sharedText);
					}
				}
			}
		}

		m_suggestionComparator = new SuggestionComparator();

		return contentView;
	}

	@Override
	protected boolean onCreateOptionsMenu() {

		// Create the search view
		SearchView searchView = new SearchView(((ApplicationActivity) getActivity()).getSupportActionBar()
				.getThemedContext());
		// searchView.setQueryHint(getActivity().getString(R.string.memolist_search));
		searchView.setQueryHint("Search");
		searchView.setOnQueryTextListener(new SearchEventHandler());

		MenuItem item;

		item = createMenuItem(0, "Search").setIcon(R.drawable.abs__ic_search).setActionView(searchView);
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

		item = createMenuItem(1, "Details").setIcon(R.drawable.ic_details);
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		if (VoiceInputHelper.isSupported(getActivity())) {
			item = createMenuItem(2, "Voice").setIcon(R.drawable.ic_voice_search);
			item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Override so drawer is no shown
		if (item.getItemId() == 0) {
			return false;
		} else if (item.getItemId() == 1) {
			openDetails();
			return false;
		} else if (item.getItemId() == 2) {
			voiceSearch();
			return false;
		}
		return true;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

		getActivity().getMenuInflater().inflate(R.menu.memolist_list, menu);
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

		m_selectedItem = m_wordsAdapter.getItem(info.position);
		android.view.MenuItem item = menu.findItem(R.id.memolist_menu_list_activate);
		if (m_selectedItem.getMemo().getActive()) {
			item.setTitle(R.string.memolist_ctxmenu_deactivate);
		} else {
			item.setTitle(R.string.memolist_ctxmenu_activate);
		}

	}

	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
		final Memo memo = m_selectedItem.getMemo();

		switch (item.getItemId()) {
		case R.id.memolist_menu_list_activate:
			memo.setActive(!memo.getActive());
			m_memoAdapter.update(memo);
			m_wordsAdapter.notifyDataSetChanged();
			break;
		case R.id.memolist_menu_list_delete:

			// Show warning
			Context ctx = MemoListFragment.this.getActivity();

			new AlertDialog.Builder(ctx)
					.setTitle(ctx.getString(R.string.memolist_ctxmenu_deleteTitle))
					.setMessage(ctx.getString(R.string.memolist_ctxmenu_deleteQuestion))
					.setPositiveButton(ctx.getString(R.string.memolist_ctxmenu_deleteYes),
							new android.content.DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									m_memoAdapter.delete(memo.getMemoId());
									bindData();
								}
							})
					.setNegativeButton(ctx.getString(R.string.memobaselist_ctxmenu_deleteNo),
							new android.content.DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// Do nothing
								}
							}).setIcon(R.drawable.ic_dialog_alert_holo_dark).create().show();

			break;
		case R.id.memolist_menu_list_shareapplication:
			m_shareHelper.shareApplication(memo.getMemoId());
			break;
		case R.id.memolist_menu_list_sharefacebook:
			m_shareHelper.shareFacebook(memo.getMemoId());
			break;
		}

		return super.onContextItemSelected(item);
	}

	//
	// Interfaces implementation
	//

	@SuppressLint("DefaultLocale")
	@Override
	public synchronized void onTranslatorComplete(TranslatorResult result) {
		String entry = m_txtAdd.getText().toString().trim().toLowerCase();

		boolean modified = false;
		for (int i = 0; i < result.Translated.size(); i++) {

			Word original = result.Originals.get(i);
			Word translated = result.Translated.get(i);

			if (original.getWord().startsWith(entry)) {

				original.setWord(original.getWord().toLowerCase());
				translated.setWord(translated.getWord().toLowerCase());

				String strPending = getString(R.string.memolist_pendingText);
				TranslatedView translatedView = new TranslatedView(original, translated, result.Source);

				int wordPosition = removeSuggestion(strPending, original);

				if (wordPosition == -1) {
					m_suggestionAdapter.add(translatedView);
				} else {
					m_suggestionAdapter.remove(wordPosition);
					m_suggestionAdapter.insert(translatedView, wordPosition);
				}

				modified = true;
			}
		}

		if (modified) {
			sortSuggestions();
		}
	}

	@Override
	public synchronized void onWordsFindComplete(WordsFindResult result) {

		String currentStr = m_txtAdd.getText().toString();

		// Get last word
		String[] currentStrWords = currentStr.split(" ");
		String currentStrWord = currentStrWords[currentStrWords.length - 1];

		if (!currentStrWord.startsWith(result.Searched.getWord())) {
			return;
		}

		if (m_lastSearchLimit == m_searchLimit) {
			// Clear if it is a new word that we are looking for
			m_suggestionAdapter.clear();
		}

		Language fromLang = ((LanguageView) m_spLanguageFrom.getSelectedItem()).getLanguage();
		Language toLang = ((LanguageView) m_spLanguageTo.getSelectedItem()).getLanguage();

		if (result.Result.size() == 0) {
			new Translator(getActivity(), new Word(currentStr.trim().toLowerCase()), fromLang, toLang, this);
		} else {
			ArrayList<TranslatedView> tViews = new ArrayList<TranslatedView>(result.Result.size());

			// Rebuild string
			StringBuilder currentStrBase = new StringBuilder();
			for (int i = 0; i < currentStrWords.length - 1; i++) {
				currentStrBase.append(currentStrWords[i]);
				currentStrBase.append(" ");
			}

			// Local copy
			ArrayList<Word> words = new ArrayList<Word>();
			for (Word w : result.Result) {
				words.add(new Word(w));
			}

			for (Word word : words) {
				word.setWord((currentStrBase.toString() + word.getWord()).toLowerCase());
				tViews.add(TranslatedView.PendingView(word, getString(R.string.memolist_pendingText)));
			}

			m_suggestionAdapter.addAll(tViews);

			if (fromLang != toLang) {
				for (int i = 0; i < result.Result.size(); i++) {
					result.Result.get(i).setWord(currentStrBase + result.Result.get(i).getWord().toLowerCase());
					new Translator(getActivity(), result.Result.get(i), fromLang, toLang, this);
				}
			}
		}

	}

	//
	// Event Handlers
	//

	private class BtnSaveEventHanlder implements OnClickListener {

		@Override
		public void onClick(View view) {

			String fromWord = m_txtAdd.getText().toString();
			String toWord = m_txtAddTranslated.getText().toString();

			if (Helper.nullOrWhitespace(fromWord)) {
				Toast.makeText(getActivity(), getString(R.string.memolist_missingWords), Toast.LENGTH_SHORT).show();
				return;
			}

			Word original = new Word(fromWord);
			original.setLanguage(((LanguageView) m_spLanguageFrom.getSelectedItem()).getLanguage());
			Word translation = new Word(toWord);
			translation.setLanguage(((LanguageView) m_spLanguageTo.getSelectedItem()).getLanguage());

			Memo memo = new Memo(original, translation, m_memoBaseId);

			if (m_memoAdapter.add(memo) == -1) {
				Toast.makeText(getActivity(), m_saveErrorMessage, Toast.LENGTH_SHORT).show();
				return;
			}

			m_txtAdd.setText("");
			m_txtAddTranslated.setText("");

			m_wordsAdapter.add(new MemoView(memo));
			m_wordsAdapter.notifyDataSetChanged();
		}

	}

	private class BtnWhatsNewEventHandler implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			m_layWhatsNew.setVisibility(View.GONE);
			if (SqliteUpdater.updateSuccessfully()) {
				Helper.setFirstStartSuccessful(getActivity());
			} else {
				Toast.makeText(getActivity(), R.string.memolist_updateCrash, Toast.LENGTH_LONG).show();
			}
			onStartContinue(getArguments());
		}

	}

	private class BtnLanguageSwap implements OnClickListener {

		@Override
		public void onClick(View button) {
			int from = m_spLanguageFrom.getSelectedItemPosition();
			int to = m_spLanguageTo.getSelectedItemPosition();
			m_spLanguageTo.setSelection(from);
			m_spLanguageFrom.setSelection(to);
			
			m_lastLookup = new DelayedLookup();
			m_lastLookup.execute(m_txtAdd.getText().toString());
		}
	}

	private class TxtAddTextEventHandler implements TextWatcher {

		@Override
		public void afterTextChanged(Editable s) {

			if (s.length() == 0) {
				m_txtAddTranslated.setText("");
				m_txtAddTranslated.setEnabled(false);
				m_btnSave.setEnabled(false);
			} else {
				m_txtAddTranslated.setEnabled(true);
				m_btnSave.setEnabled(true);
			}

			if (s.length() > 1) {

				if (m_lastLookup != null) {
					m_lastLookup.cancel(true);
				}

				m_lastLookup = new DelayedLookup();
				m_lastLookup.execute(s.toString());

				m_lstWords.setVisibility(View.GONE);
				m_lstSuggestions.setVisibility(View.VISIBLE);

			} else {
				m_lstWords.setVisibility(View.VISIBLE);
				m_lstSuggestions.setVisibility(View.GONE);
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

	private class LstSuggestionEventHandler implements OnItemClickListener, OnScrollFinishedListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			if (m_lastLookup != null) {
				m_lastLookup.cancel(true);
			}

			TranslatedView selectedTranslation = m_suggestionAdapter.getItem(position);
			Word from = selectedTranslation.from();
			Word to = selectedTranslation.to();

			if (from != null) {
				m_txtAdd.setText(from.getWord());
				m_txtAdd.setSelection(from.getWord().length());
			}
			if (to != null) {
				m_txtAddTranslated.setText(to.getWord());
			}
		}

		@Override
		public void onScrollFinished(float x, float y, int yPosition) {

			if (yPosition != ScrollableModifiableComplexTextAdapter.Y_BOTTOM) {
				return;
			}

			int minYScrollToLoadNewWords = -10;
			if (y < minYScrollToLoadNewWords) {
				m_wordsFinder.findWordsStartingWith(m_lastLookup.getLastWord(),
						((LanguageView) m_spLanguageFrom.getSelectedItem()).getLanguage(), MemoListFragment.this,
						m_lastSearchLimit, m_searchLimit);
				m_lastSearchLimit += m_searchLimit;
			}
		}
	}

	private class LstWordsEventHandler implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

			Memo memo = m_wordsAdapter.getItem(position).getMemo();

			ApplicationFragment fragment = new MemoFragment();
			Bundle bundle = new Bundle();
			bundle.putString(MemoFragment.MemoId, memo.getMemoId());
			fragment.setArguments(bundle);

			startFragment(fragment);
		}
	}

	private class SearchEventHandler implements SearchView.OnQueryTextListener {

		@Override
		public boolean onQueryTextSubmit(String query) {
			return false;
		}

		@Override
		public boolean onQueryTextChange(String newText) {
			m_wordsAdapter.clear();
			m_wordsAdapter.addAll(filter(newText));
			return false;
		}

		@SuppressLint("DefaultLocale")
		private ArrayList<MemoView> filter(String filter) {
			ArrayList<MemoView> filtered = new ArrayList<MemoView>();

			if (filtered.equals("")) {
				return m_memosViews;
			}

			filter = filter.toLowerCase();

			for (MemoView view : m_memosViews) {
				if (view.getMemo().getWordA().getWord().toLowerCase().startsWith(filter)
						|| view.getMemo().getWordB().getWord().toLowerCase().startsWith(filter)) {
					filtered.add(view);
				}
			}

			return filtered;
		}
	}

	//
	// Methods
	//

	private void updateApp() {
		PackageInfo pkg = Helper.getPackage(getActivity());
		String title = String.format(getString(R.string.memolist_whatsNewTitle), pkg.versionName);
		m_lblWhatsNewTitle.setText(title);
		m_layWhatsNew.setVisibility(View.VISIBLE);
		SqliteUpdater.update(getActivity());
	}

	private void onStartContinue(Bundle data) {
		bindData();
	}

	private void bindData() {

		setSupportProgress(0f);
		updateSupportProgress(0.5f);

		new AsyncTask<Void, Void, Void>() {
			private Helper.Pair<Language, Language> preferedLangs;
			private String title;

			@Override
			protected Void doInBackground(Void... params) {
				try {
					MemoBaseAdapter memoBaseAdapter = new MemoBaseAdapter(getActivity());

					if (m_memoBaseId == null) {
						// Open last opened one
						m_memoBaseId = getPreferences().getLastMemoBaseId();
					}

					// Check if exists
					if (m_memoBaseId == null || memoBaseAdapter.get(m_memoBaseId) == null) {
						// Open any one
						try {
							m_memoBaseId = memoBaseAdapter.getAll().get(0).getMemoBaseId();
						} catch (Exception ex) {
							AppLog.e("MemoListActivity", "onStartContinuse", ex);
						}
					}

					getPreferences().setLastMemoBaseId(m_memoBaseId);

					title = memoBaseAdapter.get(m_memoBaseId).getName();

					m_spLanguageFrom.loadData();
					m_spLanguageTo.loadData();

					m_memoAdapter = new MemoAdapter(getActivity());
					m_memos = m_memoAdapter.getAll(m_memoBaseId, MemoAdapter.Sort.CreatedDate, Order.ASC);

					updateSupportProgress(0.7f);

					if (m_memos == null) {
						return null;
					}

					m_memosViews = MemoView.getAll(m_memos);

					updateSupportProgress(0.8f);

					preferedLangs = getPreferedLanguages();
				} catch (Exception ex) {
					// This sometimes happens when debugging
					AppLog.e("MemoListFragment.bindData.AsyncTask", "Unknwon Exception", ex);
				}
				return null;
			}

			protected void onPostExecute(Void result) {
				if (getActivity() == null) {
					return;
				}

				updateSupportProgress(0.9f);

				setTitle(title);

				m_wordsAdapter.clear();
				m_wordsAdapter.addAll(m_memosViews);

				m_spLanguageFrom.bindData();
				m_spLanguageTo.bindData();
				m_spLanguageFrom.setSelection(preferedLangs.first);
				m_spLanguageTo.setSelection(preferedLangs.second);

				updateSupportProgress(1f);

				m_fragmentState.restore();
			}

		}.execute();
	}

	private synchronized int removeSuggestion(String strPending, Word original) {

		for (int i = 0; i < m_suggestionAdapter.getCount(); i++) {
			TranslatedView view = m_suggestionAdapter.getItem(i);
			if (view.source().equals(strPending) && view.from().getWord().equals(original.getWord())) {
				return i;
			}
		}

		return -1;
	}

	private synchronized void sortSuggestions() {
		ArrayList<TranslatedView> views = new ArrayList<TranslatedView>();
		for (int i = 0; i < m_suggestionAdapter.getCount(); i++) {
			views.add(m_suggestionAdapter.getItem(i));
		}

		Collections.sort(views, m_suggestionComparator);

		m_suggestionAdapter.clear(false);
		m_suggestionAdapter.addAll(views);
	}

	private class SuggestionComparator implements Comparator<TranslatedView> {

		@Override
		public int compare(TranslatedView lhs, TranslatedView rhs) {
			return lhs.from().getWord().compareTo(rhs.from().getWord());
		}

	}

	private Helper.Pair<Language, Language> getPreferedLanguages() {

		MemoListPreference pref = getPreferences().getMemoListPreference(m_memoBaseId);
		if (pref != null) {
			return new Helper.Pair<Language, Language>(Language.parse(pref.getLanguageFromCode()), Language.parse(pref
					.getLanguageToCode()));
		}

		ArrayList<Helper.Pair<Language, Integer>> languages = new ArrayList<Helper.Pair<Language, Integer>>();

		// Make a list of frequency

		for (Memo memo : m_memos) {
			Language fLang = memo.getWordA().getLanguage();
			Language tLang = memo.getWordB().getLanguage();

			boolean found = false;
			for (int i = 0; i < languages.size(); i++) {
				Helper.Pair<Language, Integer> item = languages.get(i);
				if (item.first.getCode() == fLang.getCode()) {
					item.second = Integer.valueOf(item.second.intValue() + 1);
					found = true;
					break;
				}
			}
			if (!found) {
				languages.add(new Helper.Pair<Language, Integer>(fLang, Integer.valueOf(1)));
			}

			found = false;
			for (int i = 0; i < languages.size(); i++) {
				Helper.Pair<Language, Integer> item = languages.get(i);
				if (item.first.getCode() == tLang.getCode()) {
					item.second = Integer.valueOf(item.second.intValue() + 1);
					found = true;
					break;
				}
			}
			if (!found) {
				languages.add(new Helper.Pair<Language, Integer>(tLang, Integer.valueOf(1)));
			}
		}

		// Find max
		int fMax = -1;
		int sMax = -1;
		Language fLangMax = Language.Unsupported;
		Language sLangMax = Language.Unsupported;

		if (languages.size() == 1) {
			fLangMax = languages.get(0).first;
			sLangMax = languages.get(0).first;
		} else {
			for (Helper.Pair<Language, Integer> lang : languages) {
				int val = lang.second.intValue();
				if (val > fMax) {
					sMax = fMax;
					fMax = val;
					sLangMax = fLangMax;
					fLangMax = lang.first;
				} else if (val > sMax) {
					sMax = val;
					sLangMax = lang.first;
				}
			}
		}

		return new Helper.Pair<Language, Language>(fLangMax, sLangMax);
	}

	private void openDetails() {

		ApplicationFragment fragment = new MemoBaseFragment();
		Bundle bundle = new Bundle();
		bundle.putString(MemoBaseFragment.MemoBaseId, m_memoBaseId);
		fragment.setArguments(bundle);
		startFragment(fragment);
	}

	private void openScheduler() {
		ApplicationFragment fragment = new SchedulerFragment();
		Bundle bundle = new Bundle();
		bundle.putString(SchedulerFragment.MemoBaseId, m_memoBaseId);
		fragment.setArguments(bundle);

		startFragment(fragment);
	}

	private void voiceSearch() {
		Intent intent = VoiceInputHelper.buildIntent(m_spLanguageFrom.getSelectedLanguage().getLanguage());
		getActivity().startActivityForResult(intent, VoiceInputRequestCode);
	}

	//
	// Internal classes
	//

	private class DelayedLookup extends WorkerThread<String, Void, Void> {

		private Word m_lastWord;

		@Override
		protected Void doInBackground(String... word) {
			Thread.currentThread().setName("DelayedLookup");

			m_lastSearchLimit = 0;

			try {
				Thread.sleep(m_delayedLookupDelay);

				// Still initializing (probably called from share intent)
				if (m_wordsFinder == null) {
					Thread.sleep(1000); // Sleep some more
				}

			} catch (InterruptedException e) {
				return null;
			}

			// Look only for the last word
			String[] strWords = word[0].split(" ");
			String strWord = strWords[strWords.length - 1];
			m_lastWord = new Word(strWord);

			// This can be null if rotated
			if (m_spLanguageFrom.getSelectedItem() != null) {

				m_wordsFinder.findWordsStartingWith(m_lastWord,
						((LanguageView) m_spLanguageFrom.getSelectedItem()).getLanguage(), MemoListFragment.this,
						m_lastSearchLimit, m_searchLimit);

				m_lastSearchLimit += m_searchLimit;
			}

			return null;
		}

		public Word getLastWord() {
			return m_lastWord;
		}

	}

	@Override
	protected void onPopulateDrawer(DrawerAdapter drawer) {

		drawer.addGroup(new DrawerView(R.drawable.ic_libraries, R.string.memolist_goToLibraries,
				new Lazy<ApplicationFragment>() {
					public ApplicationFragment create() {
						ApplicationFragment fragment = new MemoBaseListFragment();
						return fragment;
					}
				}));

		drawer.addGroup(new DrawerView(R.drawable.ic_details, R.string.memolist_details,
				new DrawerView.OnClickListener() {
					@Override
					public void onClick(DrawerView v) {
						openDetails();
					}
				}));

		drawer.addGroup(new DrawerView(R.drawable.ic_voice_search, R.string.memolist_voiceSearch,
				new DrawerView.OnClickListener() {
					@Override
					public void onClick(DrawerView v) {
						voiceSearch();
					}
				}));

		int reviewGroupId = drawer.addGroup(new DrawerView(R.drawable.ic_train_group, R.string.memolist_train));

		drawer.addChild(reviewGroupId, new DrawerView(R.drawable.ic_training, R.string.memolist_startTraining,
				new DrawerView.OnClickListener() {
					@Override
					public void onClick(DrawerView v) {
						Intent intent = new Intent(getActivity(), ReviewActivity.class);
						intent.putExtra(ReviewActivity.MemoBaseId, m_memoBaseId);
						intent.putExtra(ReviewActivity.Mode, ReviewActivity.WordMode);
						startActivity(intent);
					}
				}));

		drawer.addChild(reviewGroupId, new DrawerView(R.drawable.ic_repeat_all, R.string.memolist_repeatAll,
				new DrawerView.OnClickListener() {
					@Override
					public void onClick(DrawerView v) {
						Intent intent = new Intent(getActivity(), ReviewActivity.class);
						intent.putExtra(ReviewActivity.MemoBaseId, m_memoBaseId);
						intent.putExtra(ReviewActivity.RepeatAll, true);
						intent.putExtra(ReviewActivity.Mode, ReviewActivity.WordMode);
						startActivity(intent);
					}
				}));

		drawer.addChild(reviewGroupId, new DrawerView(R.drawable.ic_training_sentences,
				R.string.memolist_sentencesTraining, new DrawerView.OnClickListener() {
					@Override
					public void onClick(DrawerView v) {
						Intent intent = new Intent(getActivity(), ReviewActivity.class);
						intent.putExtra(ReviewActivity.MemoBaseId, m_memoBaseId);
						intent.putExtra(ReviewActivity.Mode, ReviewActivity.SentenceMode);
						startActivity(intent);
					}
				}));

		drawer.addChild(reviewGroupId, new DrawerView(R.drawable.ic_training_audio, R.string.memolist_audioTraining,
				new DrawerView.OnClickListener() {
					@Override
					public void onClick(DrawerView v) {
						Intent intent = new Intent(getActivity(), ReviewActivity.class);
						intent.putExtra(ReviewActivity.MemoBaseId, m_memoBaseId);
						intent.putExtra(ReviewActivity.Mode, ReviewActivity.AudioMode);
						startActivity(intent);
					}
				}));

		drawer.addChild(reviewGroupId, new DrawerView(R.drawable.ic_scheduler, R.string.memolist_openScheduler,
				new DrawerView.OnClickListener() {
					@Override
					public void onClick(DrawerView v) {
						openScheduler();
					}
				}));

		int gamesGroupId = drawer.addGroup(new DrawerView(R.drawable.ic_game, R.string.memolist_games));

		drawer.addChild(gamesGroupId, new DrawerView(R.drawable.ic_hangman, R.string.memolist_hangman,
				new Lazy<ApplicationFragment>() {
					public ApplicationFragment create() {
						ApplicationFragment fragment = new GamesHangmanFragment();
						return fragment;
					}
				}));

		drawer.addChild(gamesGroupId, new DrawerView(R.drawable.ic_crossword, R.string.memolist_crossword,
				new Lazy<ApplicationFragment>() {
					public ApplicationFragment create() {
						ApplicationFragment fragment = new GamesCrosswordFragment();
						return fragment;
					}
				}));

		drawer.addChild(gamesGroupId, new DrawerView(R.drawable.ic_findword, R.string.memolist_findword,
				new Lazy<ApplicationFragment>() {
					public ApplicationFragment create() {
						ApplicationFragment fragment = new GamesFindwordFragment();
						return fragment;
					}
				}));

		int moreGroupId = drawer.addGroup(new DrawerView(R.drawable.ic_more, R.string.memolist_more));

		drawer.addChild(moreGroupId, new DrawerView(R.drawable.ic_wordoftheday, R.string.memolist_wordOfTheDay,
				new Lazy<ApplicationFragment>() {
					public ApplicationFragment create() {
						ApplicationFragment fragment = new WordOfTheDayFragment();
						Bundle bundle = new Bundle();
						bundle.putString(WordOfTheDayFragment.MemoBaseId, m_memoBaseId);
						fragment.setArguments(bundle);
						return fragment;
					}
				}));

		drawer.addChild(moreGroupId, new DrawerView(R.drawable.ic_wiktionary, R.string.memolist_wiktionary,
				new Lazy<ApplicationFragment>() {
					public ApplicationFragment create() {
						ApplicationFragment fragment = new WiktionaryFragment();
						return fragment;
					}
				}));

		drawer.addChild(moreGroupId, new DrawerView(R.drawable.ic_statistics, R.string.memobaselist_setting_statistics,
				new Lazy<ApplicationFragment>() {
					public ApplicationFragment create() {
						ApplicationFragment fragment = new StatisticsFragment();
						return fragment;
					}
				}));

		drawer.addChild(moreGroupId, new DrawerView(R.drawable.ic_preferences, R.string.memolist_settings,
				new DrawerView.OnClickListener() {
					@Override
					public void onClick(DrawerView v) {
						Intent intent = new Intent(getActivity(), PreferenceLegacyActivity.class);
						startActivity(intent);
					}
				}));

		drawer.addChild(moreGroupId, new DrawerView(R.drawable.ic_info, R.string.memobaselist_setting_about,
				new Lazy<ApplicationFragment>() {
					public ApplicationFragment create() {
						ApplicationFragment fragment = new AboutFragment();
						return fragment;
					}
				}));
	}

	@Override
	protected void onDataBind(Bundle savedInstanceState) {
		// If it is first open after install / update
		if (Helper.isFirstStart(this.getActivity())) {
			updateApp();
			return;
		}

		Intent intent = getActivity().getIntent();
		if (intent != null) {
			if (intent.hasExtra(NotificationId)) {
				int notificationId = intent.getIntExtra(NotificationId, InvalidNotificationId);
				if (notificationId != InvalidNotificationId) {
					((NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE))
							.cancel(notificationId);
				}
			}

			if (intent.hasExtra(MemoBaseId)) {
				m_memoBaseId = intent.getStringExtra(MemoBaseId);
				intent.removeExtra(MemoBaseId);
			}

			// Go to MemoFragemnt
			if (intent.hasExtra(MemoId)) {
				String memoId = intent.getStringExtra(MemoId);
				intent.removeExtra(MemoId);

				ApplicationFragment fragment = new MemoFragment();
				Bundle bundle = new Bundle();
				bundle.putString(MemoFragment.MemoId, memoId);
				fragment.setArguments(bundle);
				startFragment(fragment);
			}
		}

		onStartContinue(getArguments());
	}

	@Override
	public void onFragmentResult(Bundle result) {
		if (result != null) {
			m_memoBaseId = result.getString(MemoBaseId);

		} else {
			m_memoBaseId = null;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (data != null) {
			String voice = VoiceInputHelper.getData(data.getExtras());
			if (voice != null) {
				m_txtAdd.setText(voice);
				m_txtAdd.setSelection(voice.length());
			}
		}
	}

	@Override
	public void onPause() {
		try {
			MemoListPreference pref = new MemoListPreference();
			pref.setMemoBaseId(m_memoBaseId);
			pref.setLanguageFromCode(m_spLanguageFrom.getSelectedLanguage().getLanguage().getCode());
			pref.setLanguageToCode(m_spLanguageTo.getSelectedLanguage().getLanguage().getCode());
			getPreferences().setMemoListPreference(pref);
			m_fragmentState.save();
		} catch (Exception ex) {
			AppLog.e("MemoListFragment.onDestroy", "Failed to destroy properyl", ex);
		}
		super.onPause();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// This is activity wide not fragment wide
		if (m_fragmentState != null) {
			m_fragmentState.save();
			m_fragmentState.addToBundle(outState);
		}
		super.onSaveInstanceState(outState);
	}

	// Something is wrong with retainInstance since it is not restored correctly
	// This is class is trying to fix this issue (listView is not restored a
	// correct position)
	private class FragmentState {
		public int lstWordsTop;
		public int lstWordsIndex;
		private boolean isSaved;

		public void save() {
			lstWordsIndex = m_lstWords.getFirstVisiblePosition();
			View v = m_lstWords.getChildAt(0);
			lstWordsTop = (v == null) ? 0 : v.getTop();
			isSaved = true;
		}

		public void restore() {
			if (!isSaved) {
				return;
			}

			return;
			// m_lstWords.setSelectionFromTop(lstWordsIndex, lstWordsTop);
		}

		public void addToBundle(Bundle bundle) {
			bundle.putBoolean("mlf_isSaved", isSaved);
			bundle.putInt("mlf_lstWordsTop", lstWordsTop);
			bundle.putInt("mlf_lstWordsIndex", lstWordsIndex);
		}

		public FragmentState fromBundle(Bundle bundle) {
			if (bundle == null || !bundle.containsKey("mlf_isSaved")) {
				return this;
			}

			isSaved = bundle.getBoolean("mlf_isSaved");
			lstWordsTop = bundle.getInt("mlf_lstWordsTop");
			lstWordsIndex = bundle.getInt("mlf_lstWordsIndex");

			return this;
		}
	}

}
