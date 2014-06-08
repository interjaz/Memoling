package app.memoling.android.webservice;

import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import app.memoling.android.Config;
import app.memoling.android.entity.Language;
import app.memoling.android.entity.MemoSentence;
import app.memoling.android.helper.AppLog;
import app.memoling.android.webrequest.HttpGetRequestTask;
import app.memoling.android.webrequest.IHttpRequestTaskComplete;

public class WsSentences {

	private static final String WsUrl = Config.WsUrlRoot + "/Sentences.php";
	private static final int m_timeout = 10000;

	public interface IGetComplete {
		void getComplete(List<MemoSentence> memoSentences);
	}

	public static void get(String word, Language from, Language to, final IGetComplete onComplete) {
		try {

			String htmlWord = URLEncoder.encode(word, "utf-8");

			URI WsUri = new URI(WsUrl + "?action=get&word=" + htmlWord + "&from=" + from.getCode() + "&to="
					+ to.getCode());

			new HttpGetRequestTask(WsUri, new IHttpRequestTaskComplete() {

				@Override
				public void onHttpRequestTaskComplete(String response) {
					
					if(response == null) {
						onComplete.getComplete(null);
						return;
					}
					
					try {
						List<MemoSentence> sentences = new ArrayList<MemoSentence>();
						JSONArray array = new JSONArray(response);

						for (int i = 0; i < array.length(); i++) {
							MemoSentence sentence = CanonicalConverter.parseMemoSentence(array.getJSONObject(i));
							sentences.add(sentence);
						}

						onComplete.getComplete(sentences);

					} catch (JSONException ex) {
						AppLog.e("WsSentence", "get", ex);
						onComplete.getComplete(null);
					} catch(Exception ex) {
						AppLog.e("WsSentence", "get - uknown", ex);
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
