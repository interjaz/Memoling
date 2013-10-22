package app.memoling.android.webservice;

import java.net.URI;
import java.net.URISyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import app.memoling.android.webrequest.HttpGetRequestTask;
import app.memoling.android.webrequest.IHttpRequestTaskComplete;

public class WsShare {

	private final static int m_timeout = 8000;

	public static interface IDiscoverShareResult {
		void discoverShareResult(Object result);
	}

	public static void discoverShare(String url, final IDiscoverShareResult result) {

		try {
			URI uri = new URI(url);

			new HttpGetRequestTask(uri, new IHttpRequestTaskComplete() {

				@Override
				public void onHttpRequestTaskComplete(String response) {

					if (response == null || response.equals("")) {
						result.discoverShareResult(null);
						return;
					}

					Object object = discoverObject(response);
					result.discoverShareResult(object);
				}

				@Override
				public void onHttpRequestTimeout(Exception ex) {
					result.discoverShareResult(null);
				}

			}, m_timeout).execute();
		} catch (URISyntaxException ex) {

		}
	}

	public static Object discoverObject(String raw) {

		try {
			JSONObject json = new JSONObject(raw);

			Object object = null;

			JSONObject memo = json.optJSONObject("memo");
			if(memo !=  null) {
				object = CanonicalConverter.parseMemo(memo);
				return object;
			}

			JSONObject memoBase = json.optJSONObject("memoBase");
			if(memoBase != null) {
				object = CanonicalConverter.parseMemoBase(memoBase);
				return object;
			}

		} catch (JSONException ex) {
			// Should not happen
		}

		return null;

	}
}
