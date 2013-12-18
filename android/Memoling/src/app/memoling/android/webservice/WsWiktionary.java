package app.memoling.android.webservice;

import java.net.URI;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import app.memoling.android.Config;
import app.memoling.android.entity.MemoSentence;
import app.memoling.android.entity.WiktionaryInfo;
import app.memoling.android.helper.AppLog;
import app.memoling.android.webrequest.HttpGetRequestTask;
import app.memoling.android.webrequest.IHttpRequestTaskComplete;

public class WsWiktionary {

	private static final String WsUrl = Config.WsUrlRoot + "/Wiktionary.php";
	private static final int m_timeout = 8000;

	public static interface IGetComplete {
		void getComplete(ArrayList<WiktionaryInfo> wiktionaryInfos);
	}

	public void get(final IGetComplete onComplete) {
		
		try {

			URI WsUri = new URI(WsUrl + "?action=get");

			new HttpGetRequestTask(WsUri, new IHttpRequestTaskComplete() {

				@Override
				public void onHttpRequestTaskComplete(String response) {
					try {
						JSONArray array = new JSONArray(response);

						ArrayList<WiktionaryInfo> wiktionaryInfos = new ArrayList<WiktionaryInfo>();
						for (int i = 0; i < array.length(); i++) {
							WiktionaryInfo wiktionaryInfo = CanonicalConverter.parseWiktionaryInfo(array
									.getJSONObject(i));
							wiktionaryInfos.add(wiktionaryInfo);
						}

						onComplete.getComplete(wiktionaryInfos);

					} catch (JSONException ex) {
						AppLog.e("WsSentence", "get", ex);
						onComplete.getComplete(null);
					}
				}

				@Override
				public void onHttpRequestTimeout(Exception ex) {
					AppLog.e("WsSentence", "get", ex);
					onComplete.getComplete(null);
				}

			}, null, m_timeout).execute();

		} catch (Exception ex) {
			AppLog.e("WsSentences", "get", ex);
			onComplete.getComplete(null);
		}
	}
}
