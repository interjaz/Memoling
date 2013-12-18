package app.memoling.android.ui.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.os.Bundle;
import app.memoling.android.R;
import app.memoling.android.wiktionary.WiktionaryProviderService;

public class WiktionaryDownloadManagerActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wiktionarydownloadmanager);

		new AlertDialog.Builder(this).setTitle(R.string.wiktionarydownloadmanager_dialogTitle)
				.setNegativeButton(getString(R.string.wiktionarydownloadmanager_dialogYes), new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						DownloadManager manager = (DownloadManager) WiktionaryDownloadManagerActivity.this
								.getSystemService(Context.DOWNLOAD_SERVICE);

						DownloadManager.Query query = new DownloadManager.Query()
								.setFilterByStatus(DownloadManager.STATUS_PENDING | DownloadManager.STATUS_RUNNING
										| DownloadManager.STATUS_PAUSED);
						Cursor c = manager.query(query);

						ArrayList<Long> toStop = new ArrayList<Long>();
						while (c.moveToNext()) {
							int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_ID);
							toStop.add(c.getLong(columnIndex));
						}

						for (Long downloadId : toStop) {
							manager.remove(downloadId);
						}

						WiktionaryProviderService.clearPendingDownloads();
						WiktionaryDownloadManagerActivity.this.finish();
					}

				}).setPositiveButton(getString(R.string.wiktionarydownloadmanager_dialogNo), new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						WiktionaryDownloadManagerActivity.this.finish();
					}

				}).setCancelable(true).setOnCancelListener(new OnCancelListener() {

					@Override
					public void onCancel(DialogInterface dialog) {
						WiktionaryDownloadManagerActivity.this.finish();
					}

				}).create().show();
	}

}
