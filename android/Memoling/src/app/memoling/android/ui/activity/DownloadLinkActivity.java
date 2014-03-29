package app.memoling.android.ui.activity;

import java.util.List;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import app.memoling.android.R;
import app.memoling.android.adapter.MemoAdapter;
import app.memoling.android.adapter.MemoBaseAdapter;
import app.memoling.android.entity.Memo;
import app.memoling.android.entity.MemoBase;
import app.memoling.android.ui.AdActivity;
import app.memoling.android.ui.ApplicationActivity;
import app.memoling.android.ui.ResourceManager;
import app.memoling.android.ui.adapter.ModifiableComplexTextAdapter;
import app.memoling.android.ui.fragment.MemoListFragment;
import app.memoling.android.ui.view.MemoBaseNameView;
import app.memoling.android.webservice.WsShare;
import app.memoling.android.webservice.WsShare.IDiscoverShareResult;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;

public class DownloadLinkActivity extends AdActivity {

	private ResourceManager m_resources;
	private Object m_share; 
	
	private Spinner m_lstMemoBases;
	private ModifiableComplexTextAdapter<MemoBaseNameView> m_lstMemoBasesAdapter;
	
	// Memo
	private TextView m_lblWordA;
	private TextView m_lblWordB;
	private TextView m_lblDescriptionA;
	private TextView m_lblDescriptionB;
	private TextView m_lblDescriptionALabel;
	private TextView m_lblDescriptionBLabel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_downloadlink);
		onCreate_Ads();

		ResourceManager resources = new ResourceManager(this);
		m_resources = resources;
		Typeface thinFont = m_resources.getLightFont();

		setProgressBarIndeterminateVisibility(true);
		
		m_lstMemoBases = (Spinner)findViewById(R.id.downloadlink_lstMemoBases); 

		m_lstMemoBasesAdapter = new ModifiableComplexTextAdapter<MemoBaseNameView>(this, R.layout.adapter_downloadlink_memobase,
				new int[] { R.id.textView1 }, new Typeface[] { resources.getLightFont() });
		
		m_lstMemoBases.setAdapter(m_lstMemoBasesAdapter);
		
		m_lblWordA = (TextView)findViewById(R.id.downloadlink_lblWordA);
		m_lblWordB = (TextView)findViewById(R.id.downloadlink_lblWordB);
		m_lblDescriptionA = (TextView)findViewById(R.id.downloadlink_lblDefinitionA);
		m_lblDescriptionB = (TextView)findViewById(R.id.downloadlink_lblDefinitionB);
		m_lblDescriptionALabel = (TextView)findViewById(R.id.downloadlink_lblDefinitionALabel);
		m_lblDescriptionBLabel = (TextView)findViewById(R.id.downloadlink_lblDefinitionBLabel);
		
		resources.setFont(R.id.textView1, thinFont);
		resources.setFont(R.id.textView2, thinFont);
		resources.setFont(R.id.textView3, thinFont);
		resources.setFont(R.id.textView4, thinFont);
		resources.setFont(R.id.textView5, thinFont);
		resources.setFont(R.id.downloadlink_lblDefinitionALabel, thinFont);
		resources.setFont(R.id.downloadlink_lblDefinitionBLabel, thinFont);
		resources.setFont(R.id.downloadlink_lblWordA, thinFont);
		resources.setFont(R.id.downloadlink_lblWordB, thinFont);
		resources.setFont(R.id.downloadlink_lblDefinitionA, thinFont);
		resources.setFont(R.id.downloadlink_lblDefinitionB, thinFont);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		MenuItem save = menu.add(1, 0, Menu.NONE, getString(R.string.downloadlink_save));
		save.setIcon(R.drawable.ic_save);
		save.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		MenuItem delete = menu.add(1, 1, Menu.NONE, getString(R.string.downloadlink_delete));
		delete.setIcon(R.drawable.ic_delete);
		delete.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		Intent intent = new Intent(this, ApplicationActivity.class);
		
		if (item.getItemId() == 0) {
			// Save
			String memoBaseId = save();
			intent.putExtra(MemoListFragment.MemoBaseId, memoBaseId);
			
			Toast.makeText(this, getString(R.string.wordofthedayreceiver_saved), Toast.LENGTH_SHORT).show();
		} else if (item.getItemId() == 1) {
			// Delete
			Toast.makeText(this, getString(R.string.wordofthedayreceiver_deleted), Toast.LENGTH_SHORT).show();
		}

		// Start memoling
		startActivity(intent);
		finish();

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onStart() {
		super.onStart();

		// Bind MemoBases
		MemoBaseAdapter adapter = new MemoBaseAdapter(this);
		List<MemoBase> memoBases = adapter.getAll();
		m_lstMemoBasesAdapter.addAll(MemoBaseNameView.getAll(memoBases));
		
		
		// Download data
		
		Intent intent = getIntent();

		if (intent == null || intent.getAction() != Intent.ACTION_VIEW) {
			// This should not happen

			// return;
		}

		Uri uri = intent.getData();
		if (uri == null) {
			// This should not happen
			Toast.makeText(this, R.string.downloadlink_errorUrlMalformed, Toast.LENGTH_SHORT).show();

			// return;
		}

		// Download json
		WsShare.discoverShare(uri.toString() + "&format=json", new IDiscoverShareResult() {
			@Override
			public void discoverShareResult(Object result) {
				setProgressBarIndeterminateVisibility(false);
				m_share = result;
				DownloadLinkActivity.this.handleShare();
			}
		});
	}

	private void handleShare() {
		if (m_share == null) {
			Toast.makeText(this, R.string.downloadlink_errorDownload, Toast.LENGTH_SHORT).show();
			// error
			return;
		}

		if (m_share instanceof Memo) {
			handleMemo((Memo) m_share);
			return;
		}

		if (m_share instanceof MemoBase) {
			handleMemoBase((MemoBase) m_share);
			return;
		}
	}

	private void handleMemo(Memo memo) {
		String wordA = memo.getWordA().getWord();
		String wordB = memo.getWordB().getWord();
		String descriptionA = memo.getWordA().getDescription();
		String descriptionB = memo.getWordB().getDescription();
		
		m_lblWordA.setText(wordA);
		m_lblWordB.setText(wordB);
		m_lblDescriptionA.setText(descriptionA);
		m_lblDescriptionB.setText(descriptionB);
		
		if(descriptionA != null && !descriptionA.equals("")) {
			m_lblDescriptionALabel.setVisibility(View.VISIBLE);
			m_lblDescriptionA.setVisibility(View.VISIBLE);
		}
		
		if(descriptionB != null && !descriptionB.equals("")) {
			m_lblDescriptionBLabel.setVisibility(View.VISIBLE);
			m_lblDescriptionB.setVisibility(View.VISIBLE);
		}
	}

	private void handleMemoBase(MemoBase memoBase) {

	}

	private String save() {
		
		if(m_share == null) {
			return null;
		}
		
		MemoBase selectedMemoBase = m_lstMemoBasesAdapter.getItem(m_lstMemoBases.getSelectedItemPosition()).getMemoBase();
		
		if(m_share instanceof Memo) {
			Memo memo = (Memo)m_share;
			memo.setMemoBaseId(selectedMemoBase.getMemoBaseId());
			MemoAdapter adapter = new MemoAdapter(this);
			adapter.add(memo);
			
		} else if(m_share instanceof MemoBase) {
			
		}
		
		return selectedMemoBase.getMemoBaseId();
	}

}
