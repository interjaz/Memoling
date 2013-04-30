package com.interjaz.webrequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.net.URI;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.util.Log;

import com.interjaz.WorkerThread;

public class HttpGetRequestTask extends WorkerThread<Void, Void, String> {

	private IHttpRequestTaskComplete m_onHttpRequestTaskComplete;
	private URI m_uri;
	private ArrayList<NameValuePair> m_headers;
	private int m_timeout;

	public HttpGetRequestTask(URI uri, IHttpRequestTaskComplete onHttpRequestTaskComplete, int timeout) {
		this(uri, onHttpRequestTaskComplete, null, timeout);
	}

	public HttpGetRequestTask(URI uri, IHttpRequestTaskComplete onHttpRequestTaskComplete,
			ArrayList<NameValuePair> headers, int timeout) {
		m_uri = uri;
		m_onHttpRequestTaskComplete = onHttpRequestTaskComplete;
		m_headers = headers;
		m_timeout = timeout;
	}

	@Override
	protected String doInBackground(Void... params) {

		try {
			HttpParams httpParameters = new BasicHttpParams();
			// Set the timeout in milliseconds until a connection is
			// established.
			// The default value is zero, that means the timeout is not used.
			int timeoutConnection = m_timeout;
			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
			// Set the default socket timeout (SO_TIMEOUT)
			// in milliseconds which is the timeout for waiting for data.
			int timeoutSocket = m_timeout;
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

			HttpClient httpclient = new DefaultHttpClient(httpParameters);
			HttpGet request = new HttpGet(m_uri);
			HttpResponse response;
			
			if (m_headers != null && m_headers.size() > 0) {
				for (NameValuePair header : m_headers) {
					request.setHeader(header.getName(), header.getValue());
				}
			}

			response = httpclient.execute(request);
			StatusLine statusLine = response.getStatusLine();
			if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				response.getEntity().writeTo(out);
				out.close();
				return out.toString();
			} else {
				// Closes the connection.
				response.getEntity().getContent().close();
				throw new IOException(statusLine.getReasonPhrase());
			}
		} catch (ConnectTimeoutException ex) {
			m_onHttpRequestTaskComplete.onHttpRequestTimeout(ex);
		} catch (SocketException ex) {
			m_onHttpRequestTaskComplete.onHttpRequestTimeout(ex);
		} catch (Exception ex) {
			Log.e("HttpRequestTaskException", ex.toString());
		}

		return null;
	}

	@Override
	protected void onPostExecute(String response) {
		if (response == null) {
			return;
		}
		
		m_onHttpRequestTaskComplete.onHttpRequestTaskComplete(response);
	}

}
