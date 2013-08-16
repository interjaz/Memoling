package app.memoling.android.webservice;

import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
		void getComplete(ArrayList<MemoSentence> memoSentences);
	}

	public void get(String word, Language from, Language to, final IGetComplete onComplete) {
		try {

			String htmlWord = URLEncoder.encode(word, "utf-8");

			URI WsUri = new URI(WsUrl + "?action=get&word=" + htmlWord + "&from=" + from.getCode() + "&to="
					+ to.getCode());

			new HttpGetRequestTask(WsUri, new IHttpRequestTaskComplete() {

				@Override
				public void onHttpRequestTaskComplete(String response) {
					try {
						JSONArray array = new JSONArray(response);

						ArrayList<MemoSentence> sentences = new ArrayList<MemoSentence>();
						for (int i = 0; i < array.length(); i++) {
							MemoSentence sentence = parseMemoSentence(array.getJSONObject(i));
							sentences.add(sentence);
						}

						onComplete.getComplete(sentences);

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
	
	private static MemoSentence parseMemoSentence(JSONObject json) {

		try {

			MemoSentence sentence = new MemoSentence();

			JSONObject original = json.getJSONObject("original");
			JSONObject translated = json.getJSONObject("translated");

			sentence.setMemoSentenceId(UUID.randomUUID().toString());

			sentence.setOriginalSentence(original.getString("sentence"));
			sentence.setOriginalLanguage(Language.parse(original.getString("languageIso639")));

			sentence.setTranslatedSentence(translated.getString("sentence"));
			sentence.setTranslatedLanguage(Language.parse(translated.getString("languageIso639")));

			return sentence;

		} catch (Exception ex) {
			AppLog.e("WsSentcence", "parseMemoSentence", ex);
			return null;
		}
	}

}
