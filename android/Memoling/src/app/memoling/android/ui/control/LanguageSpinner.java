package app.memoling.android.ui.control;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import app.memoling.android.R;
import app.memoling.android.entity.Language;
import app.memoling.android.preference.Preferences;
import app.memoling.android.ui.ResourceManager;
import app.memoling.android.ui.adapter.ModifiableComplexTextAdapter;
import app.memoling.android.ui.view.LanguageView;

public class LanguageSpinner extends Spinner implements AdapterView.OnItemSelectedListener {

	ModifiableComplexTextAdapter<LanguageView> m_spLanguageAdapter;
	private Preferences m_preferences;
	private List<LanguageView> m_laguageViews;
	
	private boolean m_loaded;
	
	public LanguageSpinner(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		if(isInEditMode()) {
			return;
		}

		ResourceManager resources = new ResourceManager(context);
		Typeface font = resources.getLightFont();
		if(attrs.getAttributeBooleanValue("http://schemas.android.com/apk/res/app.memoling.android", "bold", false)) {
			font = resources.getBlackFont();
		}
		
		int adapterLayout = R.layout.adapter_textdropdown_dark;
		if(attrs.getAttributeBooleanValue("http://schemas.android.com/apk/res/app.memoling.android", "light_theme", false)) {
			adapterLayout = R.layout.adapter_textdropdown;
		}
		
		
		m_preferences = new Preferences(context);

		m_spLanguageAdapter = new ModifiableComplexTextAdapter<LanguageView>(context, adapterLayout,
				new int[] { R.id.memo_lblLang }, new Typeface[] { font });
		
		setAdapter(m_spLanguageAdapter);
		setOnItemSelectedListener(this);
	}
	
	public void loadData(Context context) {
		String languagePreferences = m_preferences.getLanguagePreferences();
		m_laguageViews = new ArrayList<LanguageView>();
		
		if(languagePreferences != null) {
			String[] codes = languagePreferences.split(",");
			Language[] langs = new Language[codes.length];
			LanguageView[] ordered = new LanguageView[codes.length];
			
			for(int i=0;i<codes.length;i++) {
				langs[i] = Language.parse(codes[i]);
			}
			
			for(Language lang : Language.values()) {
				boolean isOrdered = false;
				for(int i=0;i<langs.length;i++) {
					if(langs[i] == lang) {
						ordered[i] = new LanguageView(lang, context);
						break;
					}
				}
				if(isOrdered) {
					continue;
				}
				
				m_laguageViews.add(new LanguageView(lang, context));
			}
			
			for(int i=0;i<ordered.length;i++) {
				m_laguageViews.add(i, ordered[i]);
			}
		} else {
			for(Language lang : Language.values()) {
				m_laguageViews.add(new LanguageView(lang, context));
			}
		}
		
		m_loaded = true;
	}

	public void bindData(Context context) {
		if(!m_loaded) {
			loadData(context);
		}

		m_spLanguageAdapter.clear();
		m_spLanguageAdapter.addAll(m_laguageViews);	
	}

	public void setSelection(Language view) {
		for(int i=0;i<m_spLanguageAdapter.getCount();i++) {
			if(m_spLanguageAdapter.getItem(i).getLanguage().getCode().equals(view.getCode()))
			{
				this.setSelection(i);
				return;
			}
		}
	}
	
	public boolean isNotSelected() {
		return this.getSelectedItemPosition() == AdapterView.INVALID_POSITION; 
	}
	
	public boolean isLoaded() {
		return m_loaded;
	}
	
	public LanguageView getSelectedLanguage() {
		return m_spLanguageAdapter.getItem(this.getSelectedItemPosition());
	}
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		String langCode = m_spLanguageAdapter.getItem(position).getLanguage().getCode();

		String languagePreferences = m_preferences.getLanguagePreferences();
		languagePreferences = reorderPreferences(langCode, languagePreferences);
		m_preferences.setLanguagePreferences(languagePreferences);
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}

	// This a valid format: a,b,c,d
	private String reorderPreferences(String langCode, String preferences) {

		if(langCode == Language.Unsupported.getCode()) {
			return preferences;
		}
		
		// If no preferences yet
		if (preferences == null) {
			return langCode;
		}

		int selectedLangPos = preferences.indexOf(langCode);

		// Add if not exists
		if (selectedLangPos == -1) {
			preferences = langCode + "," + preferences;
			return preferences;
		}

		// Move from existing position to top
		int codeLen = langCode.length();
		// Check if ',' needs to be removed
		if (preferences.length() < selectedLangPos + codeLen + 1) {
			// Last language
			if (selectedLangPos != 0) {
				selectedLangPos -= 1; // delete last ','
			}
			String rest = preferences.substring(0, selectedLangPos);
			if(rest.length() > 0) {
				rest = "," + rest;
			}
			preferences = langCode + rest;
		} else {
			preferences = langCode + "," + preferences.substring(0, selectedLangPos)
					+ preferences.substring(selectedLangPos+codeLen+1, preferences.length());
		}

		return preferences;
	}
}
