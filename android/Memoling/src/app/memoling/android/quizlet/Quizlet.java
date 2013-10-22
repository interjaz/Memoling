package app.memoling.android.quizlet;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import app.memoling.android.Config;
import app.memoling.android.helper.AppLog;
import app.memoling.android.quizlet.data.DefinitionSearchResult;
import app.memoling.android.quizlet.data.SetSearchResult;
import app.memoling.android.webrequest.HttpGetRequestTask;
import app.memoling.android.webrequest.IHttpRequestTaskComplete;

public class Quizlet {

	private final static String m_baseUrl = "https://api.quizlet.com/2.0/";
	private final static int m_perPage = 10;
	private final static int m_timeout = 10000;

	public static interface IQuizletSetSearchResult {
		public void setSearchResult(SetSearchResult results);
	}
	
	public static interface IQuizletDefinitionSearchResult {
		public void definitionSearchResult(DefinitionSearchResult results);
	}

	public static void  setSearch(String title, String term, int page, final IQuizletSetSearchResult result) {
		String url = m_baseUrl + "search/sets?" + getAuthPart() + "&" + getPagePart(page);

		try {
			if (title != null) {
				url = "&q=" + URLEncoder.encode(title, "UTF-8");
			}

			if (term != null) {
				url = "&term=" + URLEncoder.encode(term, "UTF-8");
			}

			URI uri = new URI(url);

			new HttpGetRequestTask(uri, new IHttpRequestTaskComplete() {

				@Override
				public void onHttpRequestTaskComplete(String response) {
					if (response == null || response == "") {
						result.setSearchResult(null);
						return;
					}

					try {
						JSONObject json = new JSONObject(response);
						SetSearchResult data = new SetSearchResult().deserialize(json);
						result.setSearchResult(data);
					} catch (JSONException ex) {
						AppLog.e("Quizlet", "setSearch - JSONException", ex);
						result.setSearchResult(null);
					}
				}

				@Override
				public void onHttpRequestTimeout(Exception ex) {
					result.setSearchResult(null);
				}

			}, m_timeout).execute();

		} catch (URISyntaxException ex) {
			AppLog.e("Quizlet", "setSearch - Uri", ex);
			result.setSearchResult(null);
		} catch (UnsupportedEncodingException ex) {
			AppLog.e("Quizlet", "setSearch - URLEncoder", ex);
			result.setSearchResult(null);
		}
	}

	public static void definitionSearch(String expression, int page, final IQuizletDefinitionSearchResult result) {
		String url = m_baseUrl + "search/definitions?" + getAuthPart() + "&" + getPagePart(page);

		try {
			url += "&q=" + URLEncoder.encode(expression, "UTF-8");
			URI uri = new URI(url);

			new HttpGetRequestTask(uri, new IHttpRequestTaskComplete() {

				@Override
				public void onHttpRequestTaskComplete(String response) {
					if (response == null || response == "") {
						result.definitionSearchResult(null);
						return;
					}

					try {
						JSONObject json = new JSONObject(response);
						DefinitionSearchResult data = new DefinitionSearchResult().deserialize(json);
						result.definitionSearchResult(data);
					} catch (JSONException ex) {
						AppLog.e("Quizlet", "definitionSearch - JSONException", ex);
						result.definitionSearchResult(null);
					}
				}

				@Override
				public void onHttpRequestTimeout(Exception ex) {
					result.definitionSearchResult(null);
				}

			}, m_timeout).execute();

		} catch (URISyntaxException ex) {
			AppLog.e("Quizlet", "definitionSearch - Uri", ex);
		} catch (UnsupportedEncodingException ex) {
			AppLog.e("Quizlet", "definitionSearch - URLEncoder", ex);
		}
	}

	private static String getAuthPart() {
		return "client_id=" + Config.QuizletClientId;
	}

	private static String getPagePart(int page) {
		return "page=" + Integer.toString(page) + "&per_page=" + Integer.toString(m_perPage);
	}
}
