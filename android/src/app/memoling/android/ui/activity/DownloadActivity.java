package app.memoling.android.ui.activity;

import java.util.ArrayList;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
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
import app.memoling.android.entity.PublishedMemoBase;
import app.memoling.android.ui.GestureAdActivity;
import app.memoling.android.ui.ResourceManager;
import app.memoling.android.ui.adapter.ModifiableComplexTextAdapter;
import app.memoling.android.ui.adapter.ScrollableModifiableComplexTextAdapter;
import app.memoling.android.ui.adapter.ScrollableModifiableComplexTextAdapter.OnScrollFinishedListener;
import app.memoling.android.ui.view.LanguageView;
import app.memoling.android.ui.view.MemoBaseGenreView;
import app.memoling.android.ui.view.MemoPreviewView;
import app.memoling.android.ui.view.PublishedSearchView;
import app.memoling.android.webservice.WsPublishedLibraries;
import app.memoling.android.webservice.WsPublishedLibraries.IDownloadComplete;
import app.memoling.android.webservice.WsPublishedLibraries.IPreviewComplete;
import app.memoling.android.webservice.WsPublishedLibraries.ISearchComplete;

public class DownloadActivity extends GestureAdActivity implements ISearchComplete, OnScrollFinishedListener {

	private ResourceManager m_resources;

	private EditText m_txtPhrase;
	private Spinner m_cbxGenre;
	private Spinner m_cbxLanguageA;
	private Spinner m_cbxLanguageB;
	private ListView m_lstPublished;
	private Button m_btnSearch;
	
	private LinearLayout m_layPreview;
	private ListView m_lstPreview;
	private Button m_btnClose;
	private Button m_btnDownload;
	private TextView m_lblDescription;
	
	private ModifiableComplexTextAdapter<MemoBaseGenreView> m_genreAdapter;
	private ModifiableComplexTextAdapter<LanguageView> m_languageAAdapter;
	private ModifiableComplexTextAdapter<LanguageView> m_languageBAdapter;
	private ScrollableModifiableComplexTextAdapter<PublishedSearchView> m_publishedAdapter;
	private ModifiableComplexTextAdapter<MemoPreviewView> m_previewAdapter;
	
	private MemoBaseGenreAdapter m_genreDataAdapter;
	private MemoBaseAdapter m_memoBaseDataAdapter;
	
	private WsPublishedLibraries m_wsPublished;
	
	private int m_page = 0;
	private boolean m_noMoreLibraries = false;
	private String m_lastPreviedPublishedMemoBaseId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_download);
		onCreate_Ads();
		
		m_resources = new ResourceManager(this);
		
		m_txtPhrase = (EditText)findViewById(R.id.download_txtPhrase);
		m_resources.setFont(m_txtPhrase, m_resources.getThinFont());
		
		m_btnSearch = (Button)findViewById(R.id.download_btnSearch);
		m_btnSearch.setOnClickListener(new BtnSearchEventHandler());
		m_resources.setFont(m_btnSearch, m_resources.getThinFont());
		
		m_cbxGenre = (Spinner)findViewById(R.id.download_cbxGenre);
		m_genreAdapter = new ModifiableComplexTextAdapter<MemoBaseGenreView>(this, 
				R.layout.adapter_textdropdown, new int[] { R.id.textView1 }, 
				new Typeface[] { m_resources.getThinFont() } );  
		m_cbxGenre.setAdapter(m_genreAdapter);
		
		m_cbxLanguageA = (Spinner)findViewById(R.id.download_cbxLanguageA);
		m_languageAAdapter = new ModifiableComplexTextAdapter<LanguageView>(this, 
				R.layout.adapter_textdropdown, new int[] { R.id.textView1 }, 
				new Typeface[] { m_resources.getThinFont() } );  
		m_cbxLanguageA.setAdapter(m_languageAAdapter);
		
		m_cbxLanguageB = (Spinner)findViewById(R.id.download_cbxLanguageB);
		m_languageBAdapter = new ModifiableComplexTextAdapter<LanguageView>(this, 
				R.layout.adapter_textdropdown, new int[] { R.id.textView1 }, 
				new Typeface[] { m_resources.getThinFont() } );  
		m_cbxLanguageB.setAdapter(m_languageBAdapter);
		
		m_lstPublished = (ListView)findViewById(R.id.download_lstPublished);
		m_publishedAdapter = new ScrollableModifiableComplexTextAdapter<PublishedSearchView>(this,
				R.layout.adapter_download_publishedview, new int [] {
				R.id.download_published_lblName, R.id.download_published_lblGenre,
				R.id.download_published_lblLanguage, R.id.download_published_lblMemos,
				R.id.download_published_lblDownloads }, new Typeface[] {
				m_resources.getThinFont(), m_resources.getThinFont(),
				m_resources.getThinFont(), m_resources.getThinFont(),
				m_resources.getThinFont() });
		m_publishedAdapter.setOnScrollListener(this);
		m_lstPublished.setAdapter(m_publishedAdapter);
		m_lstPublished.setOnTouchListener(this);
		m_lstPublished.setOnItemClickListener(new LstPublishedEventHandler());
		
		m_resources.setFont(R.layout.adapter_download_publishedview, R.id.textView1, m_resources.getCondensedFont());
		m_resources.setFont(R.layout.adapter_download_publishedview, R.id.textView2, m_resources.getCondensedFont());
		m_resources.setFont(R.layout.adapter_download_publishedview, R.id.textView3, m_resources.getCondensedFont());
		m_resources.setFont(R.layout.adapter_download_publishedview, R.id.textView4, m_resources.getCondensedFont());
		
		m_layPreview = (LinearLayout)findViewById(R.id.download_layPreview);
		
		m_lblDescription = (TextView)findViewById(R.id.download_lblDescription);
		m_resources.setFont(m_lblDescription, m_resources.getCondensedFont());
		
		m_lstPreview = (ListView)findViewById(R.id.download_lstPreview);
		m_previewAdapter = new ModifiableComplexTextAdapter<MemoPreviewView>(this, 
				R.layout.adapter_download_previewview, new int[] {
				R.id.download_preview_lblWordA,
				R.id.download_preview_lblWordB,
				R.id.download_preview_lblLanguage }, new Typeface[] {
				m_resources.getThinFont(), m_resources.getThinFont(),
				m_resources.getCondensedFont()
				});
		m_lstPreview.setAdapter(m_previewAdapter);

		m_btnClose = (Button)findViewById(R.id.download_btnClose);
		m_btnClose.setOnClickListener(new BtnCloseEventHandler());
		m_resources.setFont(m_btnClose, m_resources.getThinFont());
		
		m_btnDownload = (Button)findViewById(R.id.download_btnDownload);
		m_btnDownload.setOnClickListener(new BtnDownloadEventHandler());
		m_resources.setFont(m_btnDownload, m_resources.getThinFont());
		
		m_resources.setFont(R.layout.adapter_download_previewview, R.id.textView1, m_resources.getCondensedFont());
		
		m_resources.setFont(R.id.textView1, m_resources.getCondensedFont());
		m_resources.setFont(R.id.textView2, m_resources.getCondensedFont());
		m_resources.setFont(R.id.textView3, m_resources.getCondensedFont());
		m_resources.setFont(R.id.textView4, m_resources.getCondensedFont());
		m_resources.setFont(R.id.textView5, m_resources.getThinFont());
		
		m_genreDataAdapter = new MemoBaseGenreAdapter(this);
		m_memoBaseDataAdapter = new MemoBaseAdapter(this);
			
		m_wsPublished = new WsPublishedLibraries();
		
		bindData();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_download, menu);
		return true;
	}
	
	@Override
	public void onBackPressed() {
		if(m_layPreview.getVisibility() == View.VISIBLE) {
			m_layPreview.setVisibility(View.GONE);
			return;
		}
		
		finish();
	}

	private void bindData() {
		
		// Bind languages
		m_languageAAdapter.clear();
		ArrayList<LanguageView> languagesA = LanguageView.getAll();
		languagesA.set(0, LanguageView.empty());
		m_languageAAdapter.addAll(languagesA);
		
		m_languageBAdapter.clear();
		ArrayList<LanguageView> languagesB = LanguageView.getAll();
		languagesA.add(0, LanguageView.empty());
		m_languageBAdapter.addAll(languagesB);
		
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
	
	private class BtnCloseEventHandler implements OnClickListener {

		@Override
		public void onClick(View view) {
			m_layPreview.setVisibility(View.GONE);
		}
		
	}
	
	private class BtnDownloadEventHandler implements OnClickListener {

		@Override
		public void onClick(View view) {
			m_layPreview.setVisibility(View.GONE);
			Toast.makeText(DownloadActivity.this, R.string.download_download_startDownload, Toast.LENGTH_SHORT).show();
			
			m_wsPublished.download(m_lastPreviedPublishedMemoBaseId, new IDownloadComplete() {
				@Override
				public void onDownloadComplete(PublishedMemoBase published) {
					if(published == null) {
						Toast.makeText(DownloadActivity.this, R.string.download_download_errorDownload, Toast.LENGTH_SHORT).show();
						return;
					}
					
					Toast.makeText(DownloadActivity.this, R.string.download_download_completedDownload, Toast.LENGTH_SHORT).show();
					
					m_memoBaseDataAdapter.addDeep(published.getMemoBase());

					Toast.makeText(DownloadActivity.this, R.string.download_download_finished, Toast.LENGTH_SHORT).show();
				}				
			});			
		}
		
	}
	
	// TODO: Change this to dialog - remove on back press also
	private class LstPublishedEventHandler implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?>  parent, View view, int position, long id) {
			m_previewAdapter.clear();
			m_layPreview.setVisibility(View.VISIBLE);
			Toast.makeText(DownloadActivity.this, R.string.download_preview_download, Toast.LENGTH_SHORT).show();
			
			m_lastPreviedPublishedMemoBaseId = m_publishedAdapter.getItem(position).getPublishedMemoBase().getPublishedMemoBaseId();
			m_wsPublished.preview(m_lastPreviedPublishedMemoBaseId, 
					new IPreviewComplete() {
				@Override
				public void onPreviewComplete(PublishedMemoBase preview) {
					if(preview == null) {
						Toast.makeText(DownloadActivity.this, R.string.download_preview_error, Toast.LENGTH_SHORT).show();
						return;
					}
					
					m_lblDescription.setText(preview.getDescription());
					m_previewAdapter.addAll(MemoPreviewView.getAll(preview.getMemoBase().getMemos()));
				}
			});
		}
		
	}
	
	private void search() {
		String keyword = m_txtPhrase.getText().toString();
		
		MemoBaseGenreView genre = MemoBaseGenreView.empty();	
		int genrePos = m_cbxGenre.getSelectedItemPosition();	
		if(genrePos != AdapterView.INVALID_POSITION) {
			genre = m_genreAdapter.getItem(genrePos);
		}
		
		LanguageView langA = LanguageView.empty();	
		int langAPos = m_cbxLanguageA.getSelectedItemPosition();	
		if(langAPos != AdapterView.INVALID_POSITION) {
			langA = m_languageAAdapter.getItem(langAPos);
		} 
		
		LanguageView langB = LanguageView.empty();		
		int langBPos = m_cbxLanguageB.getSelectedItemPosition();
		if(langBPos != AdapterView.INVALID_POSITION) {
			langB = m_languageAAdapter.getItem(langBPos);
		} 
		
		String strGenre = "";
		String strLangA = "";
		String strLangB = "";
		
		if(genre != MemoBaseGenreView.empty()) {
			strGenre = genre.getGenre().getMemoBaseGenreId();
		}
		
		if(langA != LanguageView.empty()) {
			strLangA = langA.getLanguage().getCode();
		}
		
		if(langB != LanguageView.empty()) {
			strLangB = langB.getLanguage().getCode();
		}
		
		m_wsPublished.search(keyword, strGenre, strLangA, strLangB, m_page, this);
	}

	@Override
	public void onSearchComplete(ArrayList<PublishedMemoBase> headers) {
		if(headers == null) {
			Toast.makeText(this, R.string.download_search_error, Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(headers.size() == 0) {
			Toast.makeText(this, R.string.download_search_noMore, Toast.LENGTH_SHORT).show();
			m_noMoreLibraries = true;
			return;
		}
		
		m_publishedAdapter.addAll(PublishedSearchView.getAll(headers, m_genreDataAdapter));
	
	}
	

	@Override
	public boolean onSwipeRightToLeft() {
		finish();
		return false;
	}

	@Override
	public void onScrollFinished(float x, float y, int yPosition) {

		if(m_noMoreLibraries) {
			return;
		}
		
		if(yPosition == ScrollableModifiableComplexTextAdapter.Y_BOTTOM) {
			Toast.makeText(DownloadActivity.this, R.string.download_search_loading, Toast.LENGTH_SHORT).show();
			m_page++;
			search();
		}
		
	}
}
