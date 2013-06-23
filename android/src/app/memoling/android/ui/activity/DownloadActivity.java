package app.memoling.android.ui.activity;

import java.util.ArrayList;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import app.memoling.android.R;
import app.memoling.android.adapter.MemoBaseGenreAdapter;
import app.memoling.android.entity.PublishedMemoBase;
import app.memoling.android.ui.GestureActivity;
import app.memoling.android.ui.ResourceManager;
import app.memoling.android.ui.adapter.ModifiableComplexTextAdapter;
import app.memoling.android.ui.adapter.ScrollableModifiableComplexTextAdapter;
import app.memoling.android.ui.adapter.ScrollableModifiableComplexTextAdapter.OnScrollFinishedListener;
import app.memoling.android.ui.view.LanguageView;
import app.memoling.android.ui.view.MemoBaseGenreView;
import app.memoling.android.ui.view.PublishedSearchView;
import app.memoling.android.webservice.WsPublishedLibraries;
import app.memoling.android.webservice.WsPublishedLibraries.ISearchComplete;

public class DownloadActivity extends GestureActivity implements ISearchComplete, OnScrollFinishedListener {

	private ResourceManager m_resources;

	private EditText m_txtPhrase;
	private Spinner m_cbxGenre;
	private Spinner m_cbxLanguageA;
	private Spinner m_cbxLanguageB;
	private ListView m_lstPublished;
	private Button m_btnSearch;
	
	private ModifiableComplexTextAdapter<MemoBaseGenreView> m_genreAdapter;
	private ModifiableComplexTextAdapter<LanguageView> m_languageAAdapter;
	private ModifiableComplexTextAdapter<LanguageView> m_languageBAdapter;
	private ScrollableModifiableComplexTextAdapter<PublishedSearchView> m_publishedAdapter;

	private MemoBaseGenreAdapter m_genreDataAdapter;
	
	private WsPublishedLibraries m_wsPublished;
	
	private int m_page = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_download);
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
		
		m_resources.setFont(R.layout.adapter_download_publishedview, R.id.textView1, m_resources.getCondensedFont());
		m_resources.setFont(R.layout.adapter_download_publishedview, R.id.textView2, m_resources.getCondensedFont());
		m_resources.setFont(R.layout.adapter_download_publishedview, R.id.textView3, m_resources.getCondensedFont());
		m_resources.setFont(R.layout.adapter_download_publishedview, R.id.textView4, m_resources.getCondensedFont());
		
		m_resources.setFont(R.id.textView1, m_resources.getCondensedFont());
		m_resources.setFont(R.id.textView2, m_resources.getCondensedFont());
		m_resources.setFont(R.id.textView3, m_resources.getCondensedFont());
		m_resources.setFont(R.id.textView4, m_resources.getCondensedFont());
		
		m_genreDataAdapter = new MemoBaseGenreAdapter(this);
		
		m_wsPublished = new WsPublishedLibraries();
		
		bindData();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_download, menu);
		return true;
	}

	private void bindData() {
		
		// Bind languages
		m_languageAAdapter.clear();
		ArrayList<LanguageView> languagesA = LanguageView.getAll();
		languagesA.set(0, LanguageView.empty());
		m_languageAAdapter.addAll(languagesA);
		
		m_languageBAdapter.clear();
		ArrayList<LanguageView> languagesB = LanguageView.getAll();
		languagesA.set(0, LanguageView.empty());
		m_languageBAdapter.addAll(languagesB);
		
		// Bind genres
		m_genreAdapter.clear();
		ArrayList<MemoBaseGenreView> genres = MemoBaseGenreView.getAll(m_genreDataAdapter.getAll());
		genres.set(0, MemoBaseGenreView.empty());
		m_genreAdapter.addAll(genres);
		
		search();
	}
	
	private class BtnSearchEventHandler implements OnClickListener {

		@Override
		public void onClick(View view) {
			m_page = 0;
			m_publishedAdapter.clear();
			search();
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

		if(yPosition == ScrollableModifiableComplexTextAdapter.Y_BOTTOM) {
			Toast.makeText(DownloadActivity.this, R.string.download_search_loading, Toast.LENGTH_SHORT).show();
			m_page++;
			search();
		}
		
	}
}
