package app.memoling.android.test;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import app.memoling.android.R;
import app.memoling.android.R.id;
import app.memoling.android.R.layout;
import app.memoling.android.ui.control.LanguageSpinner;

public class UiTesFragment extends Fragment {

	Button m_button;
	LanguageSpinner m_languageSpinner;
	AutoCompleteTextView m_autoCompleteTextView;
	

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View contentView = inflater.inflate(R.layout.fragment_uitest, container, false);

		m_button = (Button) contentView.findViewById(R.id.button1);
		m_languageSpinner = (LanguageSpinner) contentView.findViewById(R.id.languageSpinner1);
		m_languageSpinner.bindData();
		registerForContextMenu(m_button);
		
		m_autoCompleteTextView = (AutoCompleteTextView) contentView.findViewById(R.id.autoCompleteTextView1);
		m_autoCompleteTextView.setAdapter(new ArrayAdapter<String>(inflater.getContext(), R.layout.adapter_textdropdown, new String[] {
				"aaa", "aaaab" }));
		
		return contentView;
	}


	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		/*super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.menu.memolist_list, menu);
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

		MenuItem item = menu.findItem(R.id.memolist_menu_list_activate);
		item.setTitle(R.string.memolist_ctxmenu_deactivate);
		item.setTitle(R.string.memolist_ctxmenu_activate);*/
	}
}
