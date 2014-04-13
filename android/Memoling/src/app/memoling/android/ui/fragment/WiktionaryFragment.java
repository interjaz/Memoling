package app.memoling.android.ui.fragment;

import java.util.List;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import app.memoling.android.R;
import app.memoling.android.adapter.WikiDefinitionAdapter;
import app.memoling.android.adapter.WikiSynonymAdapter;
import app.memoling.android.adapter.WikiTranslationAdapter;
import app.memoling.android.adapter.WikiTranslationMeaningAdapter;
import app.memoling.android.entity.WiktionaryInfo;
import app.memoling.android.ui.ApplicationFragment;
import app.memoling.android.ui.ResourceManager;
import app.memoling.android.ui.adapter.ModifiableComplexTextAdapter;
import app.memoling.android.ui.view.WiktionaryInfoView;
import app.memoling.android.webservice.WsWiktionary;
import app.memoling.android.webservice.WsWiktionary.IGetComplete;
import app.memoling.android.wiktionary.WiktionaryDb;
import app.memoling.android.wiktionary.WiktionaryProviderService;

public class WiktionaryFragment extends ApplicationFragment {

	private ModifiableComplexTextAdapter<WiktionaryInfoView> m_wiktionaryAdapter;
	private ListView m_lstInstall;

	private Button m_btnUninstall;
	private TextView m_lblIsOk;
	private TextView m_lblSize;
	private TextView m_lblInstalled;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View contentView = super.onCreateView(inflater, container, savedInstanceState,
				inflater.inflate(R.layout.fragment_wiktionary, container, false));
		setTitle(getActivity().getString(R.string.wiktionary_title));

		ResourceManager resources = getResourceManager();
		Typeface thinFont = resources.getLightFont();
		Typeface blackFont = resources.getBlackFont();

		m_wiktionaryAdapter = new ModifiableComplexTextAdapter<WiktionaryInfoView>(getActivity(),
				R.layout.adapter_wiktionary, new int[] { R.id.memolist_wiktionary_lblName,
						R.id.memolist_wiktionary_lblDescription, R.id.memolist_wiktionary_lblDownloadSize,
						R.id.memolist_wiktionary_lblRealSize, R.id.memolist_wiktionary_lblLanguage }, new Typeface[] {
						thinFont, thinFont, thinFont, thinFont, blackFont }, false);

		m_lstInstall = (ListView) contentView.findViewById(R.id.wiktionary_lstInstall);
		m_lstInstall.setAdapter(m_wiktionaryAdapter);
		m_lstInstall.setOnItemClickListener(new LstInstallEventHandler());

		m_btnUninstall = (Button) contentView.findViewById(R.id.wiktionary_btnUninstall);
		m_btnUninstall.setOnClickListener(new BtnUninstallEventHandler());
		
		m_lblInstalled = (TextView) contentView.findViewById(R.id.wiktionary_lblInstalled);
		m_lblIsOk = (TextView) contentView.findViewById(R.id.wiktionary_lblIsOk);
		m_lblSize = (TextView) contentView.findViewById(R.id.wiktionary_lblSize);

		resources.setFont(contentView, R.id.textView1, thinFont);
		resources.setFont(contentView, R.id.textView2, thinFont);
		resources.setFont(contentView, R.id.textView3, thinFont);
		resources.setFont(contentView, R.id.wiktionary_lblInstalled, thinFont);
		resources.setFont(contentView, R.id.wiktionary_btnUninstall, thinFont);
		resources.setFont(contentView, R.id.wiktionary_lblIsOk, thinFont);
		resources.setFont(contentView, R.id.wiktionary_lblSize, thinFont);
		
		return contentView;
	}

	@Override
	protected void onDataBind(Bundle savedInstanceState) {
		m_wiktionaryAdapter.clear();
		
		new WsWiktionary().get(new IGetComplete() {

			@Override
			public void getComplete(List<WiktionaryInfo> wiktionaryInfos) {

				if (wiktionaryInfos == null) {
					Toast.makeText(getActivity(), R.string.memolist_wiktionary_getError, Toast.LENGTH_SHORT).show();
					return;
				}

				m_wiktionaryAdapter.addAll(WiktionaryInfoView.getAll(wiktionaryInfos));
			}

		});
		
		performCheck();
	}
	
	private void performCheck() {


		boolean isInstalled = WiktionaryDb.isAvailable();
		long size = 0;

		if (isInstalled) {
			size = WiktionaryDb.getSize();
			boolean isOk =
					new WikiTranslationAdapter(getActivity()).isOk() &&
					new WikiDefinitionAdapter(getActivity()).isOk() &&
					new WikiSynonymAdapter(getActivity()).isOk() &&
					new WikiTranslationMeaningAdapter(getActivity()).isOk();

			m_lblIsOk.setText(getString(isOk ? R.string.wiktionary_ok : R.string.wiktionary_error));
		} else {
			m_lblIsOk.setText(getString(R.string.wiktionary_notInstalled));
		}

		float sizeMb = (float) Math.ceil(size / (1024.0 * 1024.0) * 100) / 100.0f;

		m_lblSize.setText(Float.toString(sizeMb) + " MB");
		m_lblInstalled
				.setText(getString(isInstalled ? R.string.wiktionary_installed : R.string.wiktionary_notInstalled));
	}
	
	private class LstInstallEventHandler implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int which, long arg3) {

			Toast.makeText(getActivity(), R.string.memolist_wiktionary_downloadStarted, Toast.LENGTH_SHORT).show();

			WiktionaryInfo wiktionaryInfo = m_wiktionaryAdapter.getItem(which).get();
			WiktionaryProviderService.download(getActivity(), wiktionaryInfo.getWiktionaryInfoId(),
					wiktionaryInfo.getDownloadUrl());

		}

	}

	private class BtnUninstallEventHandler implements OnClickListener {
		@Override
		public void onClick(View v) {
			if(WiktionaryDb.isAvailable()) {
				WiktionaryDb.delete();
				performCheck();
			}
			
		}		
	}

}
