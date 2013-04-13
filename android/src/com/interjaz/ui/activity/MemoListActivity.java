package com.interjaz.ui.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.interjaz.Language;
import com.interjaz.R;
import com.interjaz.db.Order;
import com.interjaz.entity.Memo;
import com.interjaz.entity.MemoAdapter;
import com.interjaz.entity.MemoBaseAdapter;
import com.interjaz.entity.Word;
import com.interjaz.helper.Helper;
import com.interjaz.helper.Preferences;
import com.interjaz.translator.ITranslateComplete;
import com.interjaz.translator.Translator;
import com.interjaz.translator.TranslatorResult;
import com.interjaz.ui.GestureActivity;
import com.interjaz.ui.ResourceManager;
import com.interjaz.ui.adapter.ModifiableComplexTextAdapter;
import com.interjaz.ui.view.LanguageView;
import com.interjaz.ui.view.MemoView;
import com.interjaz.ui.view.TranslatedView;
import com.interjaz.wordlist.IWordsFindComplete;
import com.interjaz.wordlist.WordsFindResult;
import com.interjaz.wordlist.WordsFinder;

public class MemoListActivity extends GestureActivity implements ITranslateComplete, IWordsFindComplete {

	public static String MemoBaseId = "MemoBaseId";

	private ResourceManager m_resources;
	private Preferences m_preferences;

	private Button m_btnLibraries;
	private Button m_btnOptions;
	private Button m_btnTraining;	
	
	private Button m_btnSave;
	private AutoCompleteTextView m_txtAdd;
	private ListView m_lstWords;

	private ModifiableComplexTextAdapter<TranslatedView> m_suggestionAdapter;
	private WordsFinder m_wordsFinder;
	private TxtAddTextEventHandler m_TxtAddTextEventHandler;
	private DelayedLookup m_lastLookup;
	private int m_delayedLookupDelay = 500;
	private ModifiableComplexTextAdapter<MemoView> m_wordsAdapter;

	private Spinner m_spLanguageFrom;
	private ModifiableComplexTextAdapter<LanguageView> m_spLanguageFromAdapter;
	private Spinner m_spLanguageTo;
	private ModifiableComplexTextAdapter<LanguageView> m_spLanguageToAdapter;

	private MemoAdapter m_memoAdapter;
	private ArrayList<Memo> m_memos;
	private String m_memoBaseId;

	private String m_saveErrorMessage;

	private int m_selectedItemPosition;

	private final static int[] m_autoitemResources = new int[] { R.id.memolist_suggestion_txtWord,
			R.id.memolist_suggestion_txtTranslation };
	private final static int[] m_adapter_memo_content_listview_resources = new int[] {
			R.id.memolist_listview_txtAddDate, R.id.memolist_listview_lastReview, R.id.memolist_listview_txtOriginal,
			R.id.memolist_listview_txtTranslate };

	private final static int[] m_adapter_language = new int[] { R.id.textView1 };

	private Pair<Language, Language> m_prefferedLanguages;


	private boolean m_backPressed;
	private String m_tapCloseString;
	
	//
	// Base class implementation
	//

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_memolist);

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
				m_adapter_language, new Typeface[] { m_resources.getThinFont() });
		m_spLanguageFrom.setAdapter(m_spLanguageFromAdapter);
		m_spLanguageFromAdapter.addAll(LanguageView.getAll());

		m_spLanguageTo = (Spinner) findViewById(R.id.memolist_spLanguageTo);
		m_spLanguageToAdapter = new ModifiableComplexTextAdapter<LanguageView>(this, R.layout.adapter_textdropdown,
				m_adapter_language, new Typeface[] { m_resources.getThinFont() });
		m_spLanguageTo.setAdapter(m_spLanguageToAdapter);
		m_spLanguageToAdapter.addAll(LanguageView.getAll());

		// Error message
		m_saveErrorMessage = getString(R.string.memolist_saveErrorMessage);

		// Save button
		m_btnSave = (Button) findViewById(R.id.memolist_btnSave);
		m_btnSave.setOnClickListener(new BtnSaveEventHanlder());
		m_btnSave.setTypeface(m_resources.getThinFont());

		// AutoCompleteTextView
		m_txtAdd = (AutoCompleteTextView) findViewById(R.id.memolist_txtAddMemo);
		m_suggestionAdapter = new ModifiableComplexTextAdapter<TranslatedView>(this,
				R.layout.adapter_memolist_suggestion, m_autoitemResources, new Typeface[] { m_resources.getThinFont(),
						m_resources.getThinFont() });
		m_txtAdd.setAdapter(m_suggestionAdapter);
		m_TxtAddTextEventHandler = new TxtAddTextEventHandler();
		m_txtAdd.addTextChangedListener(m_TxtAddTextEventHandler);
		m_txtAdd.setOnItemClickListener(m_TxtAddTextEventHandler);
		m_txtAdd.setTypeface(m_resources.getThinFont());

		// List View
		m_lstWords = (ListView) findViewById(R.id.memolist_list);
		m_wordsAdapter = new ModifiableComplexTextAdapter<MemoView>(this, R.layout.adapter_memolist_listview,
				m_adapter_memo_content_listview_resources, new Typeface[] { m_resources.getThinFont(),
						m_resources.getThinFont(), m_resources.getThinFont(), m_resources.getThinFont() }, true);
		m_lstWords.setAdapter(m_wordsAdapter);
		m_lstWords.setOnItemClickListener(new LstWordsEventHandler());
		registerForContextMenu(m_lstWords);
		m_lstWords.setOnTouchListener(this);

		// Word finder
		m_wordsFinder = new WordsFinder(this);

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
		m_selectedItemPosition = (int) info.position;
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		switch (item.getItemId()) {
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
									m_memoAdapter.delete(m_wordsAdapter.getItem(m_selectedItemPosition).getMemo()
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
		Intent intent = getIntent();

		// Open selected
		m_memoBaseId = intent.getStringExtra(MemoBaseId);		
		
		if (m_memoBaseId == null) {
			// Open last opened one
			m_memoBaseId = m_preferences.get(MemoListActivity.class.getName() + MemoBaseId);
			if (m_memoBaseId == null) {
				// Open any one
				try {
					m_memoBaseId = new MemoBaseAdapter(this).getAll().get(0).getMemoBaseId();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}

		m_preferences.set(MemoListActivity.class.getName() + MemoBaseId, m_memoBaseId);
		bindData(m_memoBaseId);
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

		m_suggestionAdapter.clear();

		Language fromLang = ((LanguageView) m_spLanguageFrom.getSelectedItem()).getLanguage();
		Language toLang = ((LanguageView) m_spLanguageTo.getSelectedItem()).getLanguage();

		if (result.Result.size() == 0) {
			new Translator(new Word(currentStr.trim()), fromLang, toLang, this);
		} else {
			for (Word word : result.Result) {
				m_suggestionAdapter.add(new TranslatedView(word));
				new Translator(word, fromLang, toLang, this);
			}
		}

		invalidateTxtAddDropdown();
	}

	//
	// Event Handlers
	//
	
	protected class BtnLibrariesEventHandler implements OnClickListener {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(MemoListActivity.this, MemoBaseListActivity.class);
			startActivity(intent);
		}
	}
	
	protected class BtnOptionsEventHandler implements OnClickListener {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(MemoListActivity.this, MemoBaseActivity.class);
			intent.putExtra(MemoBaseActivity.MemoBaseId, m_memoBaseId);
			startActivity(intent);			
		}
	}
	
	protected class BtnTrainingEventHandler implements OnClickListener {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(MemoListActivity.this, ReviewActivity.class);
			intent.putExtra(ReviewActivity.MemoBaseId, m_memoBaseId);
			startActivity(intent);						
		}
	}

	protected class BtnSaveEventHanlder implements OnClickListener {

		@Override
		public void onClick(View view) {

			String[] entry = m_txtAdd.getText().toString().split("\\" + TranslatedView.Separator, -1);

			if (entry.length != 2 || Helper.nullOrWhitespace(entry[1])) {
				Toast.makeText(MemoListActivity.this, getString(R.string.memolist_missingWords), Toast.LENGTH_SHORT)
						.show();
				return;
			}

			Word original = new Word(entry[0]);
			original.setLanguage(((LanguageView) m_spLanguageFrom.getSelectedItem()).getLanguage());
			Word translation = new Word("");
			if (entry.length > 1) {
				translation.setWord(entry[1]);
				translation.setLanguage(((LanguageView) m_spLanguageTo.getSelectedItem()).getLanguage());
			}

			Memo memo = new Memo(original, translation, m_memoBaseId);

			if (m_memoAdapter.add(memo) == -1) {
				Toast.makeText(MemoListActivity.this, m_saveErrorMessage, Toast.LENGTH_SHORT).show();
			}

			m_wordsAdapter.add(new MemoView(memo));
			m_wordsAdapter.notifyDataSetChanged();
		}

	}

	private class TxtAddTextEventHandler implements TextWatcher, OnItemClickListener {
		@Override
		public void afterTextChanged(Editable s) {
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

	private void bindData(String memoBaseId) {

		try {
			m_memoAdapter = new MemoAdapter(this);
			m_memos = m_memoAdapter.getAll(memoBaseId, MemoAdapter.Sort.CreatedDate, Order.ASC);
			m_wordsAdapter.clear();
			m_wordsAdapter.addAll(MemoView.getAll(m_memos));
			Pair<Language, Language> languages = getPrefferedLanguages();
			m_spLanguageFrom.setSelection(languages.first.getPosition());
			m_spLanguageTo.setSelection(languages.second.getPosition());

		} catch (IOException e) {
			e.printStackTrace();
		}
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

		// Show dropdown
		Editable entry = m_txtAdd.getText();
		m_txtAdd.removeTextChangedListener(m_TxtAddTextEventHandler);
		m_txtAdd.setText(entry.toString());
		m_txtAdd.setSelection(m_txtAdd.length());
		m_txtAdd.addTextChangedListener(m_TxtAddTextEventHandler);
	}

	//
	// Internal classes
	//

	private class DelayedLookup extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... word) {

			try {
				Thread.sleep(m_delayedLookupDelay);
			} catch (InterruptedException e) {
				return null;
			}

			m_wordsFinder.findWordsStartingWith(new Word(word[0]),
					((LanguageView) m_spLanguageFrom.getSelectedItem()).getLanguage(), MemoListActivity.this);

			return null;
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
			new AsyncTask<Void, Void, Boolean>() {

				@Override
				protected Boolean doInBackground(Void... params) {
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
