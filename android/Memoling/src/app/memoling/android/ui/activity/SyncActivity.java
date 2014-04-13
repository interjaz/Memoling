package app.memoling.android.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import app.memoling.android.R;
import app.memoling.android.sync.cloud.SyncService;
import app.memoling.android.sync.cloud.SyncService.ISyncProgress;
import app.memoling.android.sync.cloud.SyncService.SyncState;
import app.memoling.android.ui.ResourceManager;

public class SyncActivity extends Activity implements ISyncProgress {

	public final static int SyncOk = 0;
	public final static int SyncError = -1;
	
	private TextView m_txtProgress; 
	private ResourceManager m_resources;
	private View m_vwProgressBar;
	private View m_vwProgressReminder;
	
	private boolean m_disableBack = true;
	
	private int m_progress;
	private final static int TotalProgress = 9;
	
	public static void start(Context context) {
		Intent intent = new Intent(context, SyncActivity.class);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sync);
	
		m_resources = new ResourceManager(this);
		Typeface thinFont = m_resources.getLightFont();
		Typeface blackFont = m_resources.getBlackFont();
		
		m_txtProgress = (TextView)findViewById(R.id.sync_txtProgress);
		
		m_vwProgressBar = findViewById(R.id.sync_vwProgressBar);
		m_vwProgressReminder = findViewById(R.id.sync_vwProgressReminder);
		
		m_resources.setFont(R.id.textView1, blackFont);
		m_resources.setFont(m_txtProgress, thinFont);
		
		m_progress = 0;
		
		SyncService.sync(this, this);
	}

	@Override
	public void onSyncProgress(SyncState state, Bundle data) {

		String strState = "";
		double kb;
		int actions;
		
		switch (state) {
		case Starting:
			strState = getString(R.string.sync_stateStarting);
			m_progress = 1;
			break;
		case Authorizing:
			strState = getString(R.string.sync_stateAuthorizing);
			m_progress = 2;
			break;
		case BuildingFirstSync:
			strState = getString(R.string.sync_stateBuildingFirstSync);
			m_progress = 3;
			break;
		case GeneratingPackage:
			strState = getString(R.string.sync_stateGeneratingPackage);
			m_progress = 4;
			break;
		case CompressingPackage:
			strState = getString(R.string.sync_stateCompressingPackage);
			actions = data.getInt(SyncService.ProgressDataSyncActions);
			strState = String.format(strState, actions);
			m_progress = 5;
			break;
		case SendingPackage:
			strState = getString(R.string.sync_stateSendingPackage);
			kb = data.getLong(SyncService.ProgressDataPackageSize) / 1024.0;
			strState = String.format(strState, kb);
			m_progress = 6;
			break;
		case DecompressingPackage:
			strState = getString(R.string.sync_stateDecompressingPackage);
			kb = data.getLong(SyncService.ProgressDataPackageSize) / 1024.0;
			strState = String.format(strState, kb);
			m_progress = 7;
			break;
		case ApplyingPackage:
			strState = getString(R.string.sync_stateApplyingPackage);
			actions = data.getInt(SyncService.ProgressDataSyncActions);
			strState = String.format(strState, actions);
			m_progress = 8;
			break;
		case Completed:
			strState = getString(R.string.sync_stateCompleted);
			m_progress = 9;
			m_disableBack = false;
			setResult(SyncOk);
			finish();
			break;
		case AlreadyInProgress:
			strState = getString(R.string.sync_stateAlreadyInProgress);
			m_progress = TotalProgress;
			m_disableBack = false;
			break;
		case NoConnectionError:
			strState = getString(R.string.sync_stateNoConnectionError);
			m_progress = TotalProgress;
			m_disableBack = false;
			break;
		case NoClientError:
			strState = getString(R.string.sync_stateNoClientError);
			m_progress = TotalProgress;
			m_disableBack = false;
			break;
		case NoAccountError:
			strState = getString(R.string.sync_stateNoAccountError);
			break;
		case DataMalformed:
			strState = getString(R.string.sync_stateDataMalformed);
			m_progress = TotalProgress;
			m_disableBack = false;
			break;
		case ClientError:
			strState = getString(R.string.sync_stateClientError);
			m_progress = TotalProgress;
			m_disableBack = false;
			break;
		case ServerError:
			strState = getString(R.string.sync_stateServerError);
			m_progress = TotalProgress;
			m_disableBack = false;
			break;
		case ProcessingError:
			strState = getString(R.string.sync_stateProcessingError);
			m_progress = TotalProgress;
			m_disableBack = false;
			break;
		}
		
		m_txtProgress.setText(strState);
		updateProgress();
	}

	private void updateProgress() {
		int total = TotalProgress;
		
		LayoutParams layoutParams = (LayoutParams)m_vwProgressBar.getLayoutParams();
		layoutParams.weight = m_progress;
		m_vwProgressBar.setLayoutParams(layoutParams);
		
		layoutParams = (LayoutParams)m_vwProgressReminder.getLayoutParams();
		layoutParams.weight = total - m_progress;
		m_vwProgressReminder.setLayoutParams(layoutParams);
	}

	@Override
	public void onBackPressed() {
		if(!m_disableBack) {
			setResult(SyncError);
			finish();
		}
	}
	
	
	
}
