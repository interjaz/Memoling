package app.memoling.android.wiktionary;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.Toast;
import app.memoling.android.R;
import app.memoling.android.ui.activity.WiktionaryDownloadManagerActivity;

public class WiktionaryProviderReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		String action = intent.getAction();
				
		if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
			long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);

			DownloadManager.Query query = new DownloadManager.Query();
			query.setFilterById(downloadId);

			DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
			Cursor c = manager.query(query);

			if (c.moveToFirst()) {
				int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
				if (c.getInt(columnIndex) == DownloadManager.STATUS_SUCCESSFUL) {
					WiktionaryProviderService.install(context, downloadId);
				} else {
					Toast.makeText(context, R.string.wiktionary_providerError_downloadError, Toast.LENGTH_SHORT).show();
				}

			}

		}

		if (DownloadManager.ACTION_NOTIFICATION_CLICKED.equals(action)) {			
			Intent downloadManagerActivity = new Intent(context, WiktionaryDownloadManagerActivity.class);
			downloadManagerActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(downloadManagerActivity);
		}
	}

}
