package app.memoling.android.wiktionary;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.widget.Toast;
import app.memoling.android.Config;
import app.memoling.android.R;
import app.memoling.android.adapter.WikiDefinitionAdapter;
import app.memoling.android.adapter.WikiSynonymAdapter;
import app.memoling.android.adapter.WikiTranslationAdapter;
import app.memoling.android.adapter.WikiTranslationMeaningAdapter;
import app.memoling.android.helper.AppLog;
import app.memoling.android.preference.Preferences;
import app.memoling.android.thread.WorkerThread;

public class WiktionaryProvider {

	private Context m_context;

	private DownloadManager m_downloadManager;

	private String m_wiktionaryId;
	private String m_url;
	private File m_wikiDb;
	private File m_pkg;
	private File m_tmp;

	public WiktionaryProvider(Context context, String wiktionaryId, String url) {
		m_context = context;
		m_wiktionaryId = wiktionaryId;
		m_url = url;

		// Files
		m_wikiDb = new File(Config.AppPath + "/db/Wiktionary.sqlite");
		m_tmp = new File(Config.AppPath + "/wiki_tmp/" + wiktionaryId + ".sqlite");
		m_pkg = new File(Config.AppPath + "/wiki_tmp/" + m_wiktionaryId + ".sqlite.gz");

		if (!m_tmp.exists()) {
			try {
				m_tmp.createNewFile();
			} catch (IOException ex) {
				AppLog.e("WiktionaryProvider.WiktionaryProvider", "Failed to create a tmp file", ex);
			}
		}
	}

	public String getWikitionaryId() {
		return m_wiktionaryId;
	}

	public long download() {
		Uri uri = Uri.parse(m_url);
		String title = m_context.getString(R.string.wiktionary_providerDownloading);
		String description = "";

		if (m_pkg.exists()) {
			m_pkg.delete();
		}

		m_downloadManager = (DownloadManager) m_context.getSystemService(Context.DOWNLOAD_SERVICE);
		DownloadManager.Request request = new DownloadManager.Request(uri).setDescription(description)
				.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI).setAllowedOverRoaming(false)
				.setDestinationUri(Uri.fromFile(m_pkg)).setTitle(title);

		return m_downloadManager.enqueue(request);
	}

	public void install() {

		new WorkerThread<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {
				try {

					// Extract
					extract();

					// TODO: use merge
					replace();
					// merge();

					// Clean
					clean();

					// Reindex
					reindex();

					return true;

				} catch (Exception ex) {
					AppLog.e("WiktionaryProvider.install", "Failed to install", ex);
					return false;
				}
			}

			@Override
			protected void onPostExecute(Boolean result) {

				int strResource;

				if (result == true) {
					strResource = R.string.wiktionary_providerSuccess;
				} else {
					strResource = R.string.wiktionary_providerFailure;
				}

				Toast.makeText(m_context, strResource, Toast.LENGTH_SHORT).show();
				WiktionaryProviderService.finish(m_context, m_wiktionaryId);
			}

		}.execute();

	}

	private void extract() {

		BufferedInputStream is = null;
		BufferedOutputStream os = null;
		try {
			is = new BufferedInputStream(new FileInputStream(m_pkg));
			os = new BufferedOutputStream(new FileOutputStream(m_tmp));
			GZIPInputStream zis = new GZIPInputStream(new BufferedInputStream(is));
			try {
				byte[] buffer = new byte[4096];
				int read = 0;
				while ((read = zis.read(buffer, 0, buffer.length)) > 0) {
					os.write(buffer, 0, read);
				}
			} finally {
				zis.close();
			}
		} catch (IOException ex) {
			AppLog.e("WiktionaryProvider.extract", "Exception while extracting", ex);
		} finally {
			try {
				if (is != null) {
					is.close();
				}
				if (os != null) {
					os.close();
				}
			} catch (IOException ex) {
				AppLog.e("WiktionaryProvider.extract", "Exception while closing", ex);
			}
		}
	}

	private void replace() {

		if (m_wikiDb.exists()) {
			m_wikiDb.delete();
		}

		m_tmp.renameTo(m_wikiDb);
		m_tmp = null;
	}

	private void merge() {
		if (!m_wikiDb.exists()) {
			m_tmp.renameTo(m_wikiDb);
			m_tmp = null;
			return;
		}

		WiktionaryDb db = new WiktionaryDb(m_context);

		// Attach db
		db.getDatabase().execSQL("ATTACH DATABASE ? AS DB2", new String[] { m_tmp.getAbsolutePath() });

		// Copy tables
		db.getDatabase().execSQL("INSERT INTO wiki_Translations SELECT * FROM DB2.wiki_Translations");
		db.getDatabase().execSQL("INSERT INTO wiki_TranslationMeanings SELECT * FROM DB2.wiki_TranslationMeanings");
		db.getDatabase().execSQL("INSERT INTO wiki_Definitions SELECT * FROM DB2.wiki_Definitions");
		db.getDatabase().execSQL("INSERT INTO wiki_Synonyms SELECT * FROM DB2.wiki_Synonyms");

		db.closeDatabase();
	}

	private void clean() {
		m_pkg.delete();
		if (m_tmp != null) {
			m_tmp.delete();
		}
	}

	private void reindex() {

		new WikiTranslationAdapter(m_context).createIndexes();
		new WikiSynonymAdapter(m_context).createIndexes();
		new WikiDefinitionAdapter(m_context).createIndexes();
		new WikiTranslationMeaningAdapter(m_context).createIndexes();

	}
}
