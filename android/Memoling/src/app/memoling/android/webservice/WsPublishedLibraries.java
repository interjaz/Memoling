package app.memoling.android.webservice;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import app.memoling.android.Config;
import app.memoling.android.entity.PublishedMemo;
import app.memoling.android.entity.PublishedMemoBase;
import app.memoling.android.helper.AppLog;
import app.memoling.android.webrequest.HttpGetRequestTask;
import app.memoling.android.webrequest.HttpPostRequestTask;
import app.memoling.android.webrequest.IHttpRequestTaskComplete;

public class WsPublishedLibraries {

	private static final String WsUrl = Config.WsUrlRoot + "/PublishedLibraries.php";
	private static final int m_timeout = 8000;

	public interface IIndexComplete {
		void onIndexComplete(List<PublishedMemoBase> headers);
	}

	public static void index(int page, final IIndexComplete onComplete) {

		try {

			URI WsUri = new URI(WsUrl + "?action=index?page=" + Integer.toString(page));

			new HttpPostRequestTask(WsUri, new IHttpRequestTaskComplete() {

				@Override
				public void onHttpRequestTaskComplete(String response) {
					try {
						JSONArray array = new JSONArray(response);

						List<PublishedMemoBase> published = new ArrayList<PublishedMemoBase>();
						for (int i = 0; i < array.length(); i++) {
							published.add(CanonicalConverter.parsePublishedMemoBase(array.getJSONObject(i)));
						}

						onComplete.onIndexComplete(published);

					} catch (JSONException ex) {
						onComplete.onIndexComplete(null);
					}
				}

				@Override
				public void onHttpRequestTimeout(Exception ex) {
					onComplete.onIndexComplete(null);
				}

			}, null, m_timeout).execute();

		} catch (URISyntaxException e) {
			// should not happen
		}
	}

	public interface ISearchComplete {
		void onSearchComplete(List<PublishedMemoBase> headers);
	}

	public static void search(String keyword, String generId, String languageA, String languageB, int page,
			final ISearchComplete onComplete) {
		try {

			String htmlKeyword = URLEncoder.encode(keyword, "utf-8");
			String uri = String.format(Locale.US,
					"%s?action=search&keyword=%s&genreId=%s&languageAIso639=%s&languageBIso639=%s&page=%d", WsUrl,
					htmlKeyword, generId, languageA, languageB, page);
			URI WsUri = new URI(uri);

			new HttpGetRequestTask(WsUri, new IHttpRequestTaskComplete() {

				@Override
				public void onHttpRequestTaskComplete(String response) {
					try {
						JSONArray array = new JSONArray(response);

						List<PublishedMemoBase> published = new ArrayList<PublishedMemoBase>();
						for (int i = 0; i < array.length(); i++) {
							published.add(CanonicalConverter.parsePublishedMemoBase(array.getJSONObject(i)));
						}

						onComplete.onSearchComplete(published);

					} catch (JSONException ex) {
						onComplete.onSearchComplete(null);
					}
				}

				@Override
				public void onHttpRequestTimeout(Exception ex) {
					onComplete.onSearchComplete(null);
				}

			}, null, m_timeout).execute();

		} catch (Exception ex) {
			AppLog.e("WsPublishLibraries", "search", ex);
		}
	}

	public interface IPreviewComplete {
		void onPreviewComplete(PublishedMemoBase preview);
	}

	public static void preview(String publishedMemoBaseId, final IPreviewComplete onComplete) {

		try {

			URI WsUri = new URI(WsUrl + "?action=preview&id=" + publishedMemoBaseId);

			new HttpGetRequestTask(WsUri, new IHttpRequestTaskComplete() {

				@Override
				public void onHttpRequestTaskComplete(String response) {
					try {
						JSONObject json = new JSONObject(response);

						PublishedMemoBase published = new PublishedMemoBase();
						published = CanonicalConverter.parsePublishedMemoBase(json);

						onComplete.onPreviewComplete(published);

					} catch (JSONException ex) {
						onComplete.onPreviewComplete(null);
					}
				}

				@Override
				public void onHttpRequestTimeout(Exception ex) {
					onComplete.onPreviewComplete(null);
				}

			}, null, m_timeout).execute();

		} catch (URISyntaxException e) {
			// should not happen
		}
	}

	public interface IDownloadComplete {
		void onDownloadComplete(PublishedMemoBase published);
	}

	public static void download(String publishedMemoBaseId, final IDownloadComplete onComplete) {

		try {

			URI WsUri = new URI(WsUrl + "?action=download&id=" + publishedMemoBaseId);

			new HttpGetRequestTask(WsUri, new IHttpRequestTaskComplete() {

				@Override
				public void onHttpRequestTaskComplete(String response) {
					try {
						JSONObject json = new JSONObject(response);

						PublishedMemoBase published = new PublishedMemoBase();
						published = CanonicalConverter.parsePublishedMemoBase(json);

						onComplete.onDownloadComplete(published);

					} catch (JSONException ex) {
						onComplete.onDownloadComplete(null);
					}
				}

				@Override
				public void onHttpRequestTimeout(Exception ex) {
					onComplete.onDownloadComplete(null);
				}

			}, null, m_timeout).execute();

		} catch (URISyntaxException e) {
			// should not happen
		}
	}

	public interface IUploadComplete {
		void onUploadComplete(boolean result);
	}

	public static void upload(PublishedMemoBase publishedMemoBase, final IUploadComplete onComplete) {

		try {

			URI WsUri = new URI(WsUrl + "?action=upload");
			JSONObject json = CanonicalConverter.publishedMemoBaseToWsJson(publishedMemoBase);
			BasicNameValuePair post = new BasicNameValuePair("library", json.toString());

			new HttpPostRequestTask(WsUri, new IHttpRequestTaskComplete() {

				@Override
				public void onHttpRequestTaskComplete(String response) {
					onComplete.onUploadComplete(Boolean.parseBoolean(response));
				}

				@Override
				public void onHttpRequestTimeout(Exception ex) {
					onComplete.onUploadComplete(false);
				}

			}, null, m_timeout, "UTF-8").execute(post);

		} catch (URISyntaxException e) {
			// should not happen
		}
	}

	public interface IUploadMemoShareComplete {
		void onUploadMemoShareComplete(boolean result, String shortUrl);
	}

	public static void uploadMemoShare(PublishedMemo publishedMemo, final IUploadMemoShareComplete onComplete) {

		try {

			URI WsUri = new URI(WsUrl + "?action=uploadMemoShare");
			JSONObject json = CanonicalConverter.publishedMemoToWsJson(publishedMemo);
			BasicNameValuePair post = new BasicNameValuePair("memo", json.toString());

			new HttpPostRequestTask(WsUri, new IHttpRequestTaskComplete() {

				@Override
				public void onHttpRequestTaskComplete(String response) {
					if (response.equals("null")) {
						onComplete.onUploadMemoShareComplete(false, null);
					} else {
						try {
							JSONObject urlShortcut = new JSONObject(response);
							onComplete.onUploadMemoShareComplete(true, urlShortcut.getString("shortcut"));
						} catch (JSONException ex) {
							onComplete.onUploadMemoShareComplete(false, null);
						}
					}
				}

				@Override
				public void onHttpRequestTimeout(Exception ex) {
					onComplete.onUploadMemoShareComplete(false, null);
				}

			}, null, m_timeout, "UTF-8").execute(post);

		} catch (URISyntaxException e) {
			// should not happen
		}
	}

}
