package app.memoling.android.anki;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import app.memoling.android.R;
import app.memoling.android.entity.Language;
import app.memoling.android.ui.ResourceManager;
import app.memoling.android.ui.control.LanguageSpinner;
import app.memoling.android.ui.view.LanguageView;

public class AnkiLanguageSelectionActivity extends Activity{

	private LanguageSpinner m_spLanguageFrom;
	private LanguageSpinner m_spLanguageTo;
	private Button m_btnLanguageSwap;
	private Button m_btnConfirm;
	private Button m_btnCancel;
	
	//R.string.ankiImporter_ctxmenu_languagesSelectionTitle
	
	//
	// Base class implementation
	//
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_anki_languages_selection);
		
		ResourceManager resources = new ResourceManager(this);
		Typeface thinFont = resources.getLightFont();
		
		// Language spinners
		m_spLanguageFrom = (LanguageSpinner) findViewById(R.id.ankiLanguageSelection_spLanguageFrom);
		m_spLanguageTo = (LanguageSpinner) findViewById(R.id.ankiLanguageSelection_spLanguageTo);

		// Swap button
		m_btnLanguageSwap = (Button) findViewById(R.id.ankiLanguageSelection_btnLanguageSwap);
		resources.setFont(m_btnLanguageSwap, thinFont);
		m_btnLanguageSwap.setOnClickListener(new BtnLanguageSwap());
		
		// Confirm button
		m_btnConfirm = (Button) findViewById(R.id.ankiLanguageSelection_btnConfirm);
		m_btnConfirm.setOnClickListener(new BtnConfirmEventHandler());
		m_btnConfirm.setTypeface(thinFont);
		
		// Cancel button
		m_btnCancel = (Button) findViewById(R.id.ankiLanguageSelection_btnCancel);
		m_btnCancel.setOnClickListener(new BtnCancelEventHandler());
		m_btnCancel.setTypeface(thinFont);
		
		m_spLanguageFrom.loadData(this);
		m_spLanguageTo.loadData(this);
	}
		
	//
	// Event Handlers
	//
	
	private class BtnConfirmEventHandler implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Language fromLang = ((LanguageView) m_spLanguageFrom.getSelectedItem()).getLanguage();
			Language toLang = ((LanguageView) m_spLanguageTo.getSelectedItem()).getLanguage();
			
		}
		
	}
	
	private class BtnCancelEventHandler implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}	
	
	private class BtnLanguageSwap implements OnClickListener {

		@Override
		public void onClick(View button) {
			int from = m_spLanguageFrom.getSelectedItemPosition();
			int to = m_spLanguageTo.getSelectedItemPosition();
			m_spLanguageTo.setSelection(from);
			m_spLanguageFrom.setSelection(to);
			
		}
	}
	
	//
	// Methods
	//


}
