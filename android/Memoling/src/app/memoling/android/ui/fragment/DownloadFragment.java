package app.memoling.android.ui.fragment;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Typeface;
import android.os.Bundle;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import app.memoling.android.R;
import app.memoling.android.adapter.MemoBaseAdapter;
import app.memoling.android.adapter.MemoBaseGenreAdapter;
import app.memoling.android.entity.Language;
import app.memoling.android.entity.PublishedMemoBase;
import app.memoling.android.ui.ApplicationFragment;
import app.memoling.android.ui.ResourceManager;
import app.memoling.android.ui.adapter.ModifiableComplexTextAdapter;
import app.memoling.android.ui.adapter.ScrollableModifiableComplexTextAdapter;
import app.memoling.android.ui.adapter.ScrollableModifiableComplexTextAdapter.OnScrollFinishedListener;
import app.memoling.android.ui.control.LanguageSpinner;
import app.memoling.android.ui.view.LanguageView;
import app.memoling.android.ui.view.MemoBaseGenreView;
import app.memoling.android.ui.view.MemoPreviewView;
import app.memoling.android.ui.view.PublishedSearchView;
import app.memoling.android.webservice.WsPublishedLibraries;
import app.memoling.android.webservice.WsPublishedLibraries.IDownloadComplete;
import app.memoling.android.webservice.WsPublishedLibraries.IPreviewComplete;
import app.memoling.android.webservice.WsPublishedLibraries.ISearchComplete;

public class DownloadFragment extends ApplicationFragment implements ISearchComplete, OnScrollFinishedListener {

	private EditText m_txtPhrase;
	private Spinner m_cbxGenre;
	private LanguageSpinner m_spLanguageA;
	private LanguageSpinner m_spLanguageB;
	private ListView m_lstPublished;
	private Button m_btnSearch;

	private LinearLayout m_layPreview;
	private ListView m_lstPreview;
	private TextView m_lblDescription;
	private BtnDownloadEventHandler m_btnDownloadEventHandler;

	private ModifiableComplexTextAdapter<MemoBaseGenreView> m_genreAdapter;
	private ScrollableModifiableComplexTextAdapter<PublishedSearchView> m_publishedAdapter;
	private ModifiableComplexTextAdapter<MemoPreviewView> m_previewAdapter;

	private MemoBaseGenreAdapter m_genreDataAdapter;
	private MemoBaseAdapter m_memoBaseDataAdapter;

	private int m_page = 0;
	private boolean m_noMoreLibraries = false;
	private String m_lastPreviedPublishedMemoBaseId;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View contentView = super.onCreateView(inflater, container, savedInstanceState,
				inflater.inflate(R.layout.fragment_download, container, false));
		setTitle(getActivity().getString(R.string.download_title));

		ResourceManager resources = getResourceManager();
		Typeface thinFont = resources.getLightFont();
		Typeface blackFont = resources.getBlackFont();

		m_txtPhrase = (EditText) contentView.findViewById(R.id.download_txtPhrase);
		resources.setFont(m_txtPhrase, thinFont);

		m_btnSearch = (Button) contentView.findViewById(R.id.download_btnSearch);
		m_btnSearch.setOnClickListener(new BtnSearchEventHandler());
		resources.setFont(m_btnSearch, thinFont);

		m_cbxGenre = (Spinner) contentView.findViewById(R.id.download_cbxGenre);
		m_genreAdapter = new ModifiableComplexTextAdapter<MemoBaseGenreView>(getActivity(),
				R.layout.adapter_textdropdown, new int[] { R.id.memo_lblLang }, new Typeface[] { thinFont });
		m_cbxGenre.setAdapter(m_genreAdapter);

		m_spLanguageA = (LanguageSpinner) contentView.findViewById(R.id.download_spLanguageA);
		m_spLanguageB = (LanguageSpinner) contentView.findViewById(R.id.download_spLanguageB);

		m_lstPublished = (ListView) contentView.findViewById(R.id.download_lstPublished);
		m_publishedAdapter = new ScrollableModifiableComplexTextAdapter<PublishedSearchView>(getActivity(),
				R.layout.adapter_download_publishedview, new int[] { R.id.download_published_lblName,
						R.id.download_published_lblGenre, R.id.download_published_lblLanguageA,
						R.id.download_published_lblLanguageB, R.id.download_published_lblMemos,
						R.id.download_published_lblDownloads }, new Typeface[] { thinFont, thinFont, blackFont,
						thinFont, thinFont, thinFont }, false);
		m_publishedAdapter.setOnScrollListener(this);
		m_lstPublished.setAdapter(m_publishedAdapter);
		// m_lstPublished.setOnTouchListener(this);
		m_lstPublished.setOnItemClickListener(new LstPublishedEventHandler());

		resources.setFont(contentView, R.id.memo_lblLang, thinFont);
		resources.setFont(contentView, R.id.textView1, thinFont);

		m_genreDataAdapter = new MemoBaseGenreAdapter(getActivity());
		m_memoBaseDataAdapter = new MemoBaseAdapter(getActivity());

		// Preview dialog
		m_layPreview = (LinearLayout) inflater.inflate(R.layout.download_preview, null);

		m_lblDescription = (TextView) m_layPreview.findViewById(R.id.download_lblDescription);
		resources.setFont(m_lblDescription, thinFont);

		m_lstPreview = (ListView) m_layPreview.findViewById(R.id.download_lstPreview);
		m_previewAdapter = new ModifiableComplexTextAdapter<MemoPreviewView>(getActivity(),
				R.layout.adapter_download_previewview, new int[] { R.id.download_preview_lblWordA,
						R.id.download_preview_lblWordB, R.id.download_preview_lblLanguageA,
						R.id.download_preview_lblLanguageB }, new Typeface[] { blackFont, thinFont, blackFont,
						thinFont, thinFont });
		m_lstPreview.setAdapter(m_previewAdapter);

		resources.setFont(R.layout.adapter_download_publishedview, R.id.textView1, thinFont);
		resources.setFont(m_layPreview, R.id.textView1, thinFont);

		m_btnDownloadEventHandler = new BtnDownloadEventHandler();
		
		bindData();

		return contentView;
	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// // Inflate the menu; this adds items to the action bar if it is present.
	// getMenuInflater().inflate(R.menu.activity_download, menu);
	// return true;
	// }
	//

	private void bindData() {

		// Bind languages
		m_spLanguageA.bindData(getActivity());
		m_spLanguageA.setSelection(Language.Unsupported);
		m_spLanguageB.bindData(getActivity());
		m_spLanguageB.setSelection(Language.Unsupported);

		// Bind genres
		m_genreAdapter.clear();
		ArrayList<MemoBaseGenreView> genres = MemoBaseGenreView.getAll(m_genreDataAdapter.getAll());
		genres.add(0, MemoBaseGenreView.empty());
		m_genreAdapter.addAll(genres);

		search();
	}

	private class BtnSearchEventHandler implements OnClickListener {

		@Override
		public void onClick(View view) {
			m_page = 0;
			m_publishedAdapter.clear();
			m_noMoreLibraries = false;
			search();
		}

	}

	private class BtnDownloadEventHandler implements DialogInterface.OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			m_layPreview.setVisibility(View.GONE);
			Toast.makeText(getActivity(), R.string.download_download_startDownload, Toast.LENGTH_SHORT).show();

			WsPublishedLibraries.download(m_lastPreviedPublishedMemoBaseId, new IDownloadComplete() {
				@Override
				public void onDownloadComplete(PublishedMemoBase published) {
					if (published == null) {
						Toast.makeText(getActivity(), R.string.download_download_errorDownload, Toast.LENGTH_SHORT)
								.show();
						return;
					}

					Toast.makeText(getActivity(), R.string.download_download_completedDownload, Toast.LENGTH_SHORT)
							.show();

					m_memoBaseDataAdapter.addDeep(published.getMemoBase());

					Toast.makeText(getActivity(), R.string.download_download_finished, Toast.LENGTH_SHORT).show();
				}
			});

		}

	}

	private class LstPublishedEventHandler implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			m_previewAdapter.clear();
			m_layPreview.setVisibility(View.VISIBLE);
			Toast.makeText(getActivity(), R.string.download_preview_download, Toast.LENGTH_SHORT).show();

			m_lastPreviedPublishedMemoBaseId = m_publishedAdapter.getItem(position).getPublishedMemoBase()
					.getPublishedMemoBaseId();
			WsPublishedLibraries.preview(m_lastPreviedPublishedMemoBaseId, new IPreviewComplete() {
				@Override
				public void onPreviewComplete(PublishedMemoBase preview) {
					if (preview == null) {
						Toast.makeText(getActivity(), R.string.download_preview_error, Toast.LENGTH_SHORT).show();
						return;
					}

					m_lblDescription.setText(preview.getDescription());
					m_previewAdapter.addAll(MemoPreviewView.getAll(preview.getMemoBase().getMemos()));
				}
			});

			AlertDialog.Builder builder = new AlertDialog.Builder(DownloadFragment.this.getActivity());

			builder.setView(m_layPreview)
					.setPositiveButton(getString(R.string.download_download), m_btnDownloadEventHandler)
					.setNegativeButton(getString(R.string.download_close), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							((ViewGroup) m_layPreview.getParent()).removeView(m_layPreview);
						}
					}).setCancelable(true).setOnCancelListener(new OnCancelListener() {

						@Override
						public void onCancel(DialogInterface dialog) {
							((ViewGroup) m_layPreview.getParent()).removeView(m_layPreview);
						}
					}).create().show();
		}

	}

	private void search() {
		String keyword = m_txtPhrase.getText().toString();

		MemoBaseGenreView genre = MemoBaseGenreView.empty();
		int genrePos = m_cbxGenre.getSelectedItemPosition();
		if (genrePos != AdapterView.INVALID_POSITION) {
			genre = m_genreAdapter.getItem(genrePos);
		}

		LanguageView langA = LanguageView.empty();
		if (!m_spLanguageA.isNotSelected()) {
			langA = m_spLanguageA.getSelectedLanguage();
		}

		LanguageView langB = LanguageView.empty();
		if (!m_spLanguageB.isNotSelected()) {
			langB = m_spLanguageB.getSelectedLanguage();
		}

		String strGenre = "";
		String strLangA = "";
		String strLangB = "";

		if (genre != MemoBaseGenreView.empty()) {
			strGenre = genre.getGenre().getMemoBaseGenreId();
		}

		if (langA != LanguageView.empty()) {
			strLangA = langA.getLanguage().getCode();
		}

		if (langB != LanguageView.empty()) {
			strLangB = langB.getLanguage().getCode();
		}

		WsPublishedLibraries.search(keyword, strGenre, strLangA, strLangB, m_page, this);
	}

	@Override
	public void onSearchComplete(ArrayList<PublishedMemoBase> headers) {
		if (headers == null) {
			Toast.makeText(getActivity(), R.string.download_search_error, Toast.LENGTH_SHORT).show();
			return;
		}

		if (headers.size() == 0) {
			Toast.makeText(getActivity(), R.string.download_search_noMore, Toast.LENGTH_SHORT).show();
			m_noMoreLibraries = true;
			return;
		}

		m_publishedAdapter.addAll(PublishedSearchView.getAll(headers, m_genreDataAdapter));

	}

	@Override
	public void onScrollFinished(float x, float y, int yPosition) {

		if (m_noMoreLibraries) {
			return;
		}

		if (yPosition == ScrollableModifiableComplexTextAdapter.Y_BOTTOM) {
			Toast.makeText(getActivity(), R.string.download_search_loading, Toast.LENGTH_SHORT).show();
			m_page++;
			search();
		}

	}

	@Override
	protected void onDataBind(Bundle savedInstanceState) {
	}
}
