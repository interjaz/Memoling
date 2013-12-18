package app.memoling.android.wiktionary;

import java.util.Hashtable;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;
import app.memoling.android.R;

public class WiktionaryProviderService extends Service {

	private static final String WiktionaryId = "WiktionaryId";
	private static final String WiktionaryUrl = "WiktionaryUrl";
	private static final String Step = "Step";
	private static final int Unknown = -1;
	private static final int Download = 0;
	private static final int Install = 1;
	private static final int Finish = 2;

	private WiktionaryProvider m_wiktionaryProvider;

	private static Hashtable<Long, String> m_pendingDownloads = new Hashtable<Long, String>();

	public static void download(Context context, String wiktionaryId, String wiktionaryUrl) {
		Intent intent = new Intent(context, WiktionaryProviderService.class);
		intent.putExtra(WiktionaryId, wiktionaryId);
		intent.putExtra(WiktionaryUrl, wiktionaryUrl);
		intent.putExtra(Step, Download);
		context.startService(intent);
	}

	public static void install(Context context, long downloadId) {
		Intent intent = new Intent(context, WiktionaryProviderService.class);
		String wiktionaryId = m_pendingDownloads.get(downloadId);

		if (wiktionaryId == null) {
			// Bug: https://code.google.com/p/android/issues/detail?id=18462
			return;
		}

		intent.putExtra(WiktionaryId, wiktionaryId);
		intent.putExtra(Step, Install);

		m_pendingDownloads.remove(downloadId);
		context.startService(intent);
	}

	public static void finish(Context context, String wiktionaryId) {
		Intent intent = new Intent(context, WiktionaryProviderService.class);
		intent.putExtra(WiktionaryId, wiktionaryId);
		intent.putExtra(Step, Finish);
		context.startService(intent);
	}

	public static void clearPendingDownloads() {
		m_pendingDownloads.clear();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		if (intent != null && intent.hasExtra(WiktionaryId)) {
			String wiktionaryId = intent.getStringExtra(WiktionaryId);
			String wiktionaryUrl = intent.getStringExtra(WiktionaryUrl);
			int step = intent.getIntExtra(Step, Unknown);
			m_wiktionaryProvider = new WiktionaryProvider(this, wiktionaryId, wiktionaryUrl);

			if (step == Download) {
				download();
			} else if (step == Install) {
				install();
			} else if (step == Finish) {
				finish();
			}

		}

		return Service.START_STICKY;
	}

	private void download() {
		if (m_pendingDownloads.size() > 0) {
			Toast.makeText(this, R.string.wiktionary_providerError_alreadyDownloading, Toast.LENGTH_SHORT).show();
			return;
		}

		long downloadId = m_wiktionaryProvider.download();
		m_pendingDownloads.put(downloadId, m_wiktionaryProvider.getWikitionaryId());
	}

	private void install() {
		m_wiktionaryProvider.install();
	}

	private void finish() {
		stopSelf();
	}

}
