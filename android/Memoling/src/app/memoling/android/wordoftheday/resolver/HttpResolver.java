package app.memoling.android.wordoftheday.resolver;

import java.net.URI;

import android.content.Context;
import app.memoling.android.helper.AppLog;
import app.memoling.android.webrequest.HttpGetRequestTask;
import app.memoling.android.webrequest.IHttpRequestTaskComplete;
import app.memoling.android.wordoftheday.provider.Provider;

public abstract class HttpResolver extends ResolverBase {

	private int m_timeout = 15000;
	
	public HttpResolver(Context context, Provider provider) {
		super(context, provider);
	}

	@Override
	protected void fetchRaw() {

		URI uri = null;
		try {
			uri = new URI(m_provider.getUri());
		} catch (Exception ex) {
			AppLog.e("WordOfADayProvider", "fetch URISyntaxException", ex);
			onHttpRequestTimeout(ex);
		}

		new HttpGetRequestTask(uri, new IHttpRequestTaskComplete() {

			@Override
			public void onHttpRequestTaskComplete(String response) {
				HttpResolver.this.onHttpRequestTaskComplete(response);
			}

			@Override
			public void onHttpRequestTimeout(Exception ex) {
				AppLog.e("WordOfADayProvider", "fetch TimeoutException", ex);
				HttpResolver.this.onHttpRequestTimeout(ex);
			}

		}, m_timeout).execute();
	}

	protected void onHttpRequestTaskComplete(String response) {
		onFetchRawComplete(response);
	}

	protected abstract void onHttpRequestTimeout(Exception ex);
}
