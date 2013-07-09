package app.memoling.android.ui.activity;

import java.util.ArrayList;
import java.util.Hashtable;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
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
import app.memoling.android.helper.Preferences;
import app.memoling.android.thread.WorkerThread;
import app.memoling.android.translator.ITranslateComplete;
import app.memoling.android.translator.Translator;
import app.memoling.android.translator.TranslatorResult;
import app.memoling.android.ui.GestureAdActivity;
import app.memoling.android.ui.ResourceManager;
import app.memoling.android.ui.adapter.ModifiableComplexTextAdapter;
import app.memoling.android.ui.adapter.ScrollableModifiableComplexTextAdapter;
import app.memoling.android.ui.adapter.ScrollableModifiableComplexTextAdapter.OnScrollFinishedListener;
import app.memoling.android.ui.view.LanguageView;
import app.memoling.android.ui.view.MemoView;
import app.memoling.android.ui.view.TranslatedView;
import app.memoling.android.wordlist.IWordsFindComplete;
import app.memoling.android.wordlist.WordsFindResult;
import app.memoling.android.wordlist.WordsFinder;

public class MemoListActivity extends GestureAdActivity implements ITranslateComplete, IWordsFindComplete {

	public static String MemoBaseId = "MemoBaseId";

	private ResourceManager m_resources;
	private Preferences m_preferences;

	private Button m_btnLibraries;
	private Button m_btnOptions;
	private Button m_btnTraining;

	private Button m_btnSave;
	private AutoCompleteTextView m_txtAdd;
	private EditText m_txtAddTranslated;
	private ListView m_lstWords;

	private ScrollableModifiableComplexTextAdapter<TranslatedView> m_suggestionAdapter;
	private WordsFinder m_wordsFinder;
	private DelayedLookup m_lastLookup;
	private int m_delayedLookupDelay = 500;
	private ModifiableComplexTextAdapter<MemoView> m_wordsAdapter;

	private Spinner m_spLanguageFrom;
	private ModifiableComplexTextAdapter<LanguageView> m_spLanguageFromAdapter;
	private Spinner m_spLanguageTo;
	private ModifiableComplexTextAdapter<LanguageView> m_spLanguageToAdapter;
	private Button m_btnLanguageSwap;

	private MemoAdapter m_memoAdapter;
	private ArrayList<Memo> m_memos;
	private String m_memoBaseId;

	private LinearLayout m_layWhatsNew;
	private TextView m_lblWhatsNewTitle;
	private TextView m_lblWhatsNewContent;
	private Button m_btnWhatsNew;

	private String m_saveErrorMessage;

	private MemoView m_selectedItem;

	private Pair<Language, Language> m_prefferedLanguages;

	private boolean m_backPressed;
	private String m_tapCloseString;

	private int m_lastSearchLimit = 0;
	private int m_searchLimit = 10;

	//
	// Base class implementation
	//

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_memolist);
		onCreate_Ads();

		m_resources = new ResourceManager(this);
		m_preferences = new Preferences(this);

		// Navigation buttons
		m_btnLibraries = (Button) findViewById(R.id.memolist_btnLibraries);
		m_btnLibraries.setOnClickListener(new BtnLibrariesEventHandler());
		m_btnOptions = (Button) findViewById(R.id.memolist_btnOptions);
		m_btnOptions.setOnClickListener(new BtnOptionsEventHandler());
		m_btnTraining = (Button) findViewById(R.id.memolist_btnTraining);
		m_btnTraining.setOnClickListener(new BtnTrainingEventHandler());
		m_resources.setFont(m_btnLibraries, m_resources.getThinFont());
		m_resources.setFont(m_btnOptions, m_resources.getThinFont());
		m_resources.setFont(m_btnTraining, m_resources.getThinFont());

		// Language spinners
		m_spLanguageFrom = (Spinner) findViewById(R.id.memolist_spLanguageFrom);
		m_spLanguageFromAdapter = new ModifiableComplexTextAdapter<LanguageView>(this, R.layout.adapter_textdropdown,
				new int[] { R.id.textView1 }, new Typeface[] { m_resources.getThinFont() });
		m_spLanguageFrom.setAdapter(m_spLanguageFromAdapter);
		m_spLanguageFromAdapter.addAll(LanguageView.getAll());

		m_spLanguageTo = (Spinner) findViewById(R.id.memolist_spLanguageTo);
		m_spLanguageToAdapter = new ModifiableComplexTextAdapter<LanguageView>(this, R.layout.adapter_textdropdown,
				new int[] { R.id.textView1 }, new Typeface[] { m_resources.getThinFont() });
		m_spLanguageTo.setAdapter(m_spLanguageToAdapter);
		m_spLanguageToAdapter.addAll(LanguageView.getAll());

		m_btnLanguageSwap = (Button) findViewById(R.id.memolist_btnLanguageSwap);
		m_resources.setFont(m_btnLanguageSwap, m_resources.getThinFont());
		m_btnLanguageSwap.setOnClickListener(new BtnLanguageSwap());

		// Error message
		m_saveErrorMessage = getString(R.string.memolist_saveErrorMessage);

		// Save button
		m_btnSave = (Button) findViewById(R.id.memolist_btnSave);
		m_btnSave.setOnClickListener(new BtnSaveEventHanlder());
		m_btnSave.setTypeface(m_resources.getThinFont());

		// AutoCompleteTextView
		m_txtAdd = (AutoCompleteTextView) findViewById(R.id.memolist_txtAddMemo);
		TxtAddTextEventHandler txtAddTextEventHandler = new TxtAddTextEventHandler();
		m_suggestionAdapter = new ScrollableModifiableComplexTextAdapter<TranslatedView>(this,
				R.layout.adapter_memolist_suggestion, new int[] { R.id.memolist_suggestion_txtWord,
						R.id.memolist_suggestion_txtTranslation }, new Typeface[] { m_resources.getThinFont(),
						m_resources.getThinFont() });
		m_suggestionAdapter.setOnScrollListener(txtAddTextEventHandler);
		m_txtAdd.setAdapter(m_suggestionAdapter);
		m_txtAdd.addTextChangedListener(txtAddTextEventHandler);
		m_txtAdd.setOnItemClickListener(txtAddTextEventHandler);
		m_txtAdd.setTypeface(m_resources.getThinFont());

		m_txtAddTranslated = (EditText) findViewById(R.id.memolist_txtAddMemoTranslated);
		m_txtAddTranslated.setTypeface(m_resources.getThinFont());

		// List View
		m_lstWords = (ListView) findViewById(R.id.memolist_list);
		m_wordsAdapter = new ModifiableComplexTextAdapter<MemoView>(this, R.layout.adapter_memolist_listview,
				new int[] { R.id.memolist_listview_txtLanguages, R.id.memolist_listview_txtAddDate,
						R.id.memolist_listview_lastReview, R.id.memolist_listview_txtOriginal,
						R.id.memolist_listview_txtTranslate }, new Typeface[] { m_resources.getThinFont(),
						m_resources.getThinFont(), m_resources.getThinFont(), m_resources.getThinFont(),
						m_resources.getThinFont() }, true);
		m_lstWords.setAdapter(m_wordsAdapter);
		m_lstWords.setOnItemClickListener(new LstWordsEventHandler());
		registerForContextMenu(m_lstWords);
		m_lstWords.setOnTouchListener(this);
		m_lstWords.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		m_lstWords.setStackFromBottom(true);

		// Word finder
		m_wordsFinder = new WordsFinder(this);

		// What's new
		m_layWhatsNew = (LinearLayout) findViewById(R.id.memolist_layWhatsNew);
		m_lblWhatsNewTitle = (TextView) findViewById(R.id.memolist_lblWhatsNewTitle);
		m_resources.setFont(m_lblWhatsNewTitle, m_resources.getThinFont());
		m_lblWhatsNewContent = (TextView) findViewById(R.id.memolist_lblWhatsNewContent);
		m_resources.setFont(m_lblWhatsNewContent, m_resources.getThinFont());
		m_btnWhatsNew = (Button) findViewById(R.id.memolist_btnWhatsNew);
		m_btnWhatsNew.setOnClickListener(new BtnWhatsNewEventHandler());
		m_resources.setFont(m_btnWhatsNew, m_resources.getThinFont());

		m_resources.setFont(R.id.textView1, m_resources.getCondensedFont());
		m_resources.setFont(R.id.textView2, m_resources.getCondensedFont());
		m_resources.setFont(R.layout.adapter_memolist_listview, R.id.textView1, m_resources.getCondensedFont());
		m_resources.setFont(R.layout.adapter_memolist_listview, R.id.textView2, m_resources.getCondensedFont());

		m_tapCloseString = getString(R.string.memobaselist_tap_close);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_memolist, menu);
		return true;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.menu.memolist_list, menu);
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		
		m_selectedItem = m_wordsAdapter.getItem(info.position);
		MenuItem item = menu.findItem(R.id.memolist_menu_list_activate);
		if(m_selectedItem.getMemo().getActive()) {
			item.setTitle(R.string.memolist_ctxmenu_deactivate);
		} else {
			item.setTitle(R.string.memolist_ctxmenu_activate);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		Memo memo;
		
		switch (item.getItemId()) {
		case R.id.memolist_menu_list_activate:
			memo = m_selectedItem.getMemo();
			memo.setActive(!memo.getActive());
			m_memoAdapter.update(memo);
			m_wordsAdapter.notifyDataSetChanged();
			break;
		case R.id.memolist_menu_list_delete:

			// Show warning
			Context ctx = MemoListActivity.this;

			new AlertDialog.Builder(ctx)
					.setTitle(ctx.getString(R.string.memolist_ctxmenu_deleteTitle))
					.setMessage(ctx.getString(R.string.memolist_ctxmenu_deleteQuestion))
					.setPositiveButton(ctx.getString(R.string.memolist_ctxmenu_deleteYes),
							new android.content.DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									m_memoAdapter.delete(m_selectedItem.getMemo()
											.getMemoId());
									bindData(m_memoBaseId);
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
		}

		return super.onContextItemSelected(item);
	}

	@Override
	public void onStart() {
		super.onStart();

		// If it is first open after install / update
		if (Helper.isFirstStart(this)) {
			updateApp();
			return;
		}

		onStartContinue();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
	}

	//
	// Interfaces implementation
	//

	@Override
	public synchronized void onTranslateComplete(TranslatorResult result) {
		String entry = m_txtAdd.getText().toString();

		if (result.AutoCompleteWord.getWord().startsWith(entry)) {

			for (Word word : result.TranslatedSuggestions) {

				TranslatedView searchedView = new TranslatedView(result.AutoCompleteWord);
				TranslatedView wordView = new TranslatedView(result.AutoCompleteWord, word);

				int wordPosition = m_suggestionAdapter.getPosition(searchedView);

				if (wordPosition == -1) {
					m_suggestionAdapter.add(wordView);
				} else {
					m_suggestionAdapter.remove(searchedView);
					m_suggestionAdapter.insert(wordView, wordPosition);
				}
			}

			invalidateTxtAddDropdown();
		}
	}

	@Override
	public synchronized void onWordsFindComplete(WordsFindResult result) {

		String currentStr = m_txtAdd.getText().toString();

		if (!currentStr.startsWith(result.Searched.getWord())) {
			return;
		}

		if (m_lastSearchLimit == m_searchLimit) {
			// Clear if it is a new word that we are looking for
			m_suggestionAdapter.clear();
		}

		Language fromLang = ((LanguageView) m_spLanguageFrom.getSelectedItem()).getLanguage();
		Language toLang = ((LanguageView) m_spLanguageTo.getSelectedItem()).getLanguage();

		if (result.Result.size() == 0) {
			new Translator(new Word(currentStr.trim()), fromLang, toLang, this);
		} else {
			ArrayList<TranslatedView> tViews = new ArrayList<TranslatedView>(result.Result.size());
			for (Word word : result.Result) {
				tViews.add(new TranslatedView(word));
			}
			m_suggestionAdapter.addAll(tViews);
			for (int i = 0; i < result.Result.size(); i++) {
				new Translator(result.Result.get(i), fromLang, toLang, this);
			}
		}

		invalidateTxtAddDropdown();

	}

	//
	// Event Handlers
	//

	private class BtnLibrariesEventHandler implements OnClickListener {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(MemoListActivity.this, MemoBaseListActivity.class);
			startActivity(intent);
		}
	}

	private class BtnOptionsEventHandler implements OnClickListener {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(MemoListActivity.this, MemoBaseActivity.class);
			intent.putExtra(MemoBaseActivity.MemoBaseId, m_memoBaseId);
			startActivity(intent);
		}
	}

	private class BtnTrainingEventHandler implements OnClickListener {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(MemoListActivity.this, ReviewActivity.class);
			intent.putExtra(ReviewActivity.MemoBaseId, m_memoBaseId);
			startActivity(intent);
		}
	}

	private class BtnSaveEventHanlder implements OnClickListener {

		@Override
		public void onClick(View view) {

			String fromWord = m_txtAdd.getText().toString();
			String toWord = m_txtAddTranslated.getText().toString();

			if (Helper.nullOrWhitespace(fromWord)) {
				Toast.makeText(MemoListActivity.this, getString(R.string.memolist_missingWords), Toast.LENGTH_SHORT)
						.show();
				return;
			}

			Word original = new Word(fromWord);
			original.setLanguage(((LanguageView) m_spLanguageFrom.getSelectedItem()).getLanguage());
			Word translation = new Word(toWord);
			translation.setLanguage(((LanguageView) m_spLanguageTo.getSelectedItem()).getLanguage());

			Memo memo = new Memo(original, translation, m_memoBaseId);

			if (m_memoAdapter.add(memo) == -1) {
				Toast.makeText(MemoListActivity.this, m_saveErrorMessage, Toast.LENGTH_SHORT).show();
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
				Helper.setFirstStartSuccessful(MemoListActivity.this);
			} else {
				Toast.makeText(MemoListActivity.this, R.string.memolist_updateCrash, Toast.LENGTH_LONG).show();
			}
			onStartContinue();
		}

	}

	private class BtnLanguageSwap implements OnClickListener {

		@Override
		public void onClick(View button) {
			int from = m_spLanguageFrom.getSelectedItemPosition();
			int to = m_spLanguageTo.getSelectedItemPosition();
			m_spLanguageTo.setSelection(from);
			m_spLanguageFrom.setSelection(to);
			invalidateTxtAddDropdown();
		}
	}

	private class TxtAddTextEventHandler implements TextWatcher, OnItemClickListener, OnScrollFinishedListener {

		@Override
		public void afterTextChanged(Editable s) {
			if (s.toString().indexOf(TranslatedView.Separator) != -1) {
				String[] data = m_txtAdd.getText().toString().split(TranslatedView.Separator);
				m_txtAdd.setText(data[0]);
				m_txtAddTranslated.setText(data[1]);
				m_txtAdd.dismissDropDown();
				return;
			}

			String entry = s.toString();
			if (entry.length() > 2) {

				if (m_lastLookup != null) {
					m_lastLookup.cancel(true);
				}

				m_lastLookup = new DelayedLookup();
				m_lastLookup.execute(entry);
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

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			if (m_lastLookup != null) {
				m_lastLookup.cancel(true);
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
						((LanguageView) m_spLanguageFrom.getSelectedItem()).getLanguage(), MemoListActivity.this,
						m_lastSearchLimit, m_searchLimit);
				m_lastSearchLimit += m_searchLimit;
			}
		}
	}

	private class LstWordsEventHandler implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

			Memo memo = m_wordsAdapter.getItem(position).getMemo();

			Intent memoEditIntent = new Intent(MemoListActivity.this, MemoActivity.class);
			memoEditIntent.putExtra(MemoActivity.MemoId, memo.getMemoId());

			startActivity(memoEditIntent);
		}
	}

	//
	// Methods
	//

	private void updateApp() {
		PackageInfo pkg = Helper.getPackage(this);
		String title = String.format(getString(R.string.memolist_whatsNewTitle), pkg.versionName);
		m_lblWhatsNewTitle.setText(title);
		m_layWhatsNew.setVisibility(View.VISIBLE);
		SqliteUpdater.update(this);
	}

	private void onStartContinue() {
		Intent intent = getIntent();
		MemoBaseAdapter memoBaseAdapter = new MemoBaseAdapter(this);

		// Get selected
		m_memoBaseId = intent.getStringExtra(MemoBaseId);

		if (m_memoBaseId == null) {
			// Open last opened one
			m_memoBaseId = m_preferences.get(MemoListActivity.class.getName() + MemoBaseId);
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

		m_preferences.set(MemoListActivity.class.getName() + MemoBaseId, m_memoBaseId);
		bindData(m_memoBaseId);
	}

	private void bindData(String memoBaseId) {

		m_wordsAdapter.clear();

		m_memoAdapter = new MemoAdapter(this);
		m_memos = m_memoAdapter.getAll(memoBaseId, MemoAdapter.Sort.CreatedDate, Order.ASC);

		if (m_memos == null) {
			return;
		}

		m_wordsAdapter.addAll(MemoView.getAll(m_memos));
		Pair<Language, Language> languages = getPrefferedLanguages();
		m_spLanguageFrom.setSelection(languages.first.getPosition());
		m_spLanguageTo.setSelection(languages.second.getPosition());
	}

	private Pair<Language, Language> getPrefferedLanguages() {

		if (m_prefferedLanguages != null) {
			return m_prefferedLanguages;
		}

		Hashtable<Language, Integer> from = new Hashtable<Language, Integer>();
		Hashtable<Language, Integer> to = new Hashtable<Language, Integer>();

		// Make a list of frequency

		for (Memo memo : m_memos) {
			Language fLang = memo.getWordA().getLanguage();
			Language tLang = memo.getWordB().getLanguage();

			if (from.contains(fLang)) {
				from.put(fLang, Integer.valueOf(from.remove(fLang).intValue() + 1));
			} else {
				from.put(fLang, Integer.valueOf(1));
			}

			if (to.contains(tLang)) {
				to.put(tLang, Integer.valueOf(to.remove(tLang).intValue() + 1));
			} else {
				to.put(tLang, Integer.valueOf(1));
			}
		}

		// Find max
		int fMax = -1;
		Language fLangMax = Language.Unsupported;
		int tMax = -1;
		Language tLangMax = Language.Unsupported;

		for (Language lang : from.keySet()) {
			int val = from.get(lang).intValue();
			if (val > fMax) {
				fMax = val;
				fLangMax = lang;
			}
		}

		for (Language lang : to.keySet()) {
			int val = to.get(lang).intValue();
			if (val > tMax) {
				tMax = val;
				tLangMax = lang;
			}
		}

		m_prefferedLanguages = new Pair<Language, Language>(fLangMax, tLangMax);
		return m_prefferedLanguages;
	}

	private synchronized void invalidateTxtAddDropdown() {
		m_suggestionAdapter.notifyDataSetChanged();
		m_txtAdd.showDropDown();
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
			} catch (InterruptedException e) {
				return null;
			}
			m_lastWord = new Word(word[0]);

			m_wordsFinder.findWordsStartingWith(m_lastWord,
					((LanguageView) m_spLanguageFrom.getSelectedItem()).getLanguage(), MemoListActivity.this,
					m_lastSearchLimit, m_searchLimit);

			m_lastSearchLimit += m_searchLimit;
			return null;
		}

		public Word getLastWord() {
			return m_lastWord;
		}

	}

	@Override
	public void onBackPressed() {
		if (m_backPressed) {
			finish();
		} else {
			Toast.makeText(this, m_tapCloseString, Toast.LENGTH_SHORT).show();
			m_backPressed = true;

			// Clear m_backPressed after one second
			new WorkerThread<Void, Void, Boolean>() {

				@Override
				protected Boolean doInBackground(Void... params) {
					Thread.currentThread().setName("CloseAppThread");

					try {
						Thread.sleep(2000);
						return Boolean.valueOf(false);
					} catch (InterruptedException e) {
						// It is OK to get interrupted
						return Boolean.valueOf(true);
					}
				}

				@Override
				protected void onPostExecute(Boolean result) {
					super.onPostExecute(result);
					m_backPressed = result;
				}
			}.execute();
		}
	}

	@Override
	public boolean onSwipeRightToLeft() {
		Intent intent = new Intent(MemoListActivity.this, MemoBaseListActivity.class);
		startActivity(intent);
		return super.onSwipeRightToLeft();
	}

}
