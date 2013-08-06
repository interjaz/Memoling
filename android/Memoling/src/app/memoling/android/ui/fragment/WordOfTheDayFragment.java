package app.memoling.android.ui.fragment;

import java.util.Random;
import java.util.UUID;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.Spinner;
import app.memoling.android.R;
import app.memoling.android.adapter.WordOfTheDayAdapter;
import app.memoling.android.entity.Language;
import app.memoling.android.entity.WordOfTheDay;
import app.memoling.android.ui.ApplicationFragment;
import app.memoling.android.ui.ResourceManager;
import app.memoling.android.ui.adapter.ModifiableComplexTextAdapter;
import app.memoling.android.ui.control.LanguageSpinner;
import app.memoling.android.ui.view.WordOfTheDayModeView;
import app.memoling.android.ui.view.WordOfTheDayView;
import app.memoling.android.wordoftheday.AlarmReceiver;
import app.memoling.android.wordoftheday.DispatcherService;
import app.memoling.android.wordoftheday.WordOfTheDayMode;
import app.memoling.android.wordoftheday.provider.Provider;
import app.memoling.android.wordoftheday.provider.ProviderAdapter;

public class WordOfTheDayFragment extends ApplicationFragment {

	public static final String MemoBaseId = "MemoBaseId";

	private CheckBox m_chbEnabled;
	private LanguageSpinner m_spLanguageFrom;
	private LanguageSpinner m_spLanguageTo;
	private CheckBox m_chbLanguageFrom;

	private Spinner m_spMode;
	private ModifiableComplexTextAdapter<WordOfTheDayModeView> m_spModeAdapter;

	private Spinner m_spSource;
	private ModifiableComplexTextAdapter<WordOfTheDayView> m_spSourceAdapter;

	private Button m_btnTest;

	private String m_memoBaseId;

	private LinearLayout m_layMode;
	private LinearLayout m_laySource;
	private LinearLayout m_layFrom;
	private LinearLayout m_layFromLanguage;
	private LinearLayout m_layTo;
	private LinearLayout m_layTest;

	private SpModeEventHandler m_spModeEventHandler;
	private ChbLanguageFromEventHandler m_chbLanguageFromEventHandler;

	private WordOfTheDayAdapter m_wordOfTheDayAdapter;
	private WordOfTheDay m_wordOfTheDay;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View contentView = super.onCreateView(inflater, container, savedInstanceState,
				inflater.inflate(R.layout.fragment_wordoftheday, container, false));
		setTitle(getActivity().getString(R.string.wordoftheday_title));

		ResourceManager resources = getResourceManager();
		Typeface thinFont = resources.getThinFont();
		Typeface condensedFont = resources.getCondensedFont();

		m_chbEnabled = (CheckBox) contentView.findViewById(R.id.wordoftheday_chbEnable);
		m_chbEnabled.setOnCheckedChangeListener(new ChbEnabledEventHandler());
		m_chbLanguageFrom = (CheckBox) contentView.findViewById(R.id.wordoftheday_chbLanguageFrom);
		m_chbLanguageFromEventHandler = new ChbLanguageFromEventHandler();
		m_chbLanguageFrom.setOnCheckedChangeListener(m_chbLanguageFromEventHandler);
		m_spLanguageFrom = (LanguageSpinner) contentView.findViewById(R.id.wordofthelist_spLanguageFrom);
		m_spLanguageTo = (LanguageSpinner) contentView.findViewById(R.id.wordofthelist_spLanguageTo);

		m_spMode = (Spinner) contentView.findViewById(R.id.wordoftheday_spMode);
		m_spModeAdapter = new ModifiableComplexTextAdapter<WordOfTheDayModeView>(getActivity(),
				R.layout.adapter_textdropdown, new int[] { R.id.textView1 }, new Typeface[] { thinFont });
		m_spMode.setAdapter(m_spModeAdapter);
		m_spModeEventHandler = new SpModeEventHandler();
		m_spMode.setOnItemSelectedListener(m_spModeEventHandler);

		m_spSource = (Spinner) contentView.findViewById(R.id.wordoftheday_spSource);
		m_spSourceAdapter = new ModifiableComplexTextAdapter<WordOfTheDayView>(getActivity(),
				R.layout.adapter_textdropdown, new int[] { R.id.textView1 }, new Typeface[] { thinFont });
		m_spSource.setAdapter(m_spSourceAdapter);

		m_btnTest = (Button) contentView.findViewById(R.id.wordoftheday_btnTest);
		m_btnTest.setOnClickListener(new BtnTestEventHandler());

		m_layMode = (LinearLayout) contentView.findViewById(R.id.wordotheday_layMode);
		m_laySource = (LinearLayout) contentView.findViewById(R.id.wordotheday_laySource);
		m_layFrom = (LinearLayout) contentView.findViewById(R.id.wordotheday_layFrom);
		m_layFromLanguage = (LinearLayout) contentView.findViewById(R.id.wordotheday_layFromLanguage);
		m_layTo = (LinearLayout) contentView.findViewById(R.id.wordotheday_layTo);
		m_layTest = (LinearLayout) contentView.findViewById(R.id.wordotheday_layTest);

		resources.setFont(contentView, R.id.wordoftheday_lblEnable, thinFont);
		resources.setFont(contentView, R.id.wordoftheday_lblLanguageFrom, thinFont);
		resources.setFont(contentView, R.id.wordoftheday_lblLanguageTo, thinFont);
		resources.setFont(contentView, R.id.wordoftheday_lblSource, thinFont);
		resources.setFont(contentView, R.id.wordoftheday_lblSourceHint, thinFont);
		resources.setFont(contentView, R.id.wordoftheday_lblMode, thinFont);
		resources.setFont(contentView, R.id.wordoftheday_btnTest, thinFont);

		return contentView;
	}

	@Override
	protected void onDataBind(Bundle savedInstanceState) {
		int sourcePos = -1;
		int providerId = -1;
		int modePos = -1;

		m_spLanguageFrom.bindData();
		m_spLanguageTo.bindData();

		m_memoBaseId = getArguments().getString(MemoBaseId);

		m_wordOfTheDayAdapter = new WordOfTheDayAdapter(getActivity());
		m_wordOfTheDay = m_wordOfTheDayAdapter.getByMemoBaseId(m_memoBaseId);

		if (m_wordOfTheDay != null) {
			m_chbEnabled.setChecked(true);
			modePos = m_wordOfTheDay.getMode().ordinal();
			providerId = m_wordOfTheDay.getProviderId();
			if (m_wordOfTheDay.getPreLanguageFrom() != null) {
				m_chbLanguageFrom.setChecked(true);
				m_spLanguageFrom.setSelection(m_wordOfTheDay.getPreLanguageFrom());
			}
			m_spLanguageTo.setSelection(m_wordOfTheDay.getLanguageTo());
		}

		m_spModeAdapter.clear();
		int i = 0;
		for (String str : getActivity().getResources().getStringArray(R.array.wordoftheday_modes)) {
			m_spModeAdapter.add(new WordOfTheDayModeView(i++, str));
		}
		if (modePos != -1) {
			m_spMode.setSelection(modePos);
		}

		m_spSourceAdapter.clear();
		i = 0;
		for (Provider provider : new ProviderAdapter().getAll()) {
			m_spSourceAdapter.add(new WordOfTheDayView(provider));
			if (providerId == provider.getId()) {
				sourcePos = i;
			}
			i++;
		}
		if (sourcePos != -1) {
			m_spSource.setSelection(sourcePos);
		}

	}

	private class ChbEnabledEventHandler implements OnCheckedChangeListener {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if (isChecked) {
				m_layMode.setVisibility(View.VISIBLE);
				m_spModeEventHandler.onItemSelected(null, null, m_spMode.getSelectedItemPosition(), 0L);
			} else {
				m_layMode.setVisibility(View.GONE);
				m_laySource.setVisibility(View.GONE);
				m_layFrom.setVisibility(View.GONE);
				m_layFromLanguage.setVisibility(View.GONE);
				m_layTo.setVisibility(View.GONE);
				m_layTest.setVisibility(View.GONE);
			}
		}
	}

	private class ChbLanguageFromEventHandler implements OnCheckedChangeListener {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if (isChecked) {
				m_layFromLanguage.setVisibility(View.VISIBLE);
			} else {
				m_layFromLanguage.setVisibility(View.GONE);
			}
		}
	}

	private class SpModeEventHandler implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			if (position == WordOfTheDayMode.LibraryOnly.ordinal()) {
				m_laySource.setVisibility(View.GONE);
				m_layFrom.setVisibility(View.GONE);
				m_layFromLanguage.setVisibility(View.GONE);
				m_layTo.setVisibility(View.GONE);
			} else {
				m_laySource.setVisibility(View.VISIBLE);
				m_layFrom.setVisibility(View.VISIBLE);
				m_layTo.setVisibility(View.VISIBLE);
				m_chbLanguageFromEventHandler.onCheckedChanged(null, m_chbLanguageFrom.isChecked());
			}
			m_layTest.setVisibility(View.VISIBLE);
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}
	}

	private class BtnTestEventHandler implements OnClickListener {

		private Random random = new Random();

		@Override
		public void onClick(View v) {

			Language to = m_spLanguageTo.getSelectedLanguage().getLanguage();
			Language from = null;
			if (m_chbLanguageFrom.isChecked()) {
				from = m_spLanguageFrom.getSelectedLanguage().getLanguage();
			}

			DispatcherService.dispatch(getActivity(), m_spModeAdapter.getItem(m_spMode.getSelectedItemPosition())
					.getMode(), random, m_memoBaseId, m_spSourceAdapter.getItem(m_spSource.getSelectedItemPosition())
					.getProvider().getId(), from, to);

			m_btnTest.setEnabled(false);
			m_btnTest.postDelayed(new Runnable() {

				@Override
				public void run() {
					m_btnTest.setEnabled(true);
				}

			}, 5000);
		}

	}

	@Override
	public void onDetach() {
		save();
		super.onDetach();
	}

	private void save() {
		if (m_chbEnabled.isChecked()) {
			boolean create = false;
			if (m_wordOfTheDay == null) {
				m_wordOfTheDay = new WordOfTheDay();
				m_wordOfTheDay.setWordOfTheDayId(UUID.randomUUID().toString());
				create = true;
			}

			m_wordOfTheDay.setLanguageTo(m_spLanguageTo.getSelectedLanguage().getLanguage());
			m_wordOfTheDay.setMemoBaseId(m_memoBaseId);
			m_wordOfTheDay.setMode(m_spModeAdapter.getItem(m_spMode.getSelectedItemPosition()).getMode());
			if (m_chbLanguageFrom.isChecked()) {
				m_wordOfTheDay.setPreLanguageFrom(m_spLanguageFrom.getSelectedLanguage().getLanguage());
			} else {
				m_wordOfTheDay.setPreLanguageFrom(null);
			}
			m_wordOfTheDay.setProviderId(m_spSourceAdapter.getItem(m_spSource.getSelectedItemPosition()).getProvider()
					.getId());

			if (create) {
				m_wordOfTheDayAdapter.add(m_wordOfTheDay);
			} else {
				m_wordOfTheDayAdapter.update(m_wordOfTheDay);
			}

		} else {
			// Delete
			if (m_wordOfTheDay != null) {
				m_wordOfTheDayAdapter.delete(m_wordOfTheDay.getWordOfTheDayId());
			}
		}
		
		AlarmReceiver.updateAlarm(getActivity());
	}

}
