package com.interjaz.webrequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;

public class HttpGetRequestTask extends AsyncTask<Void, Void, String> {

	private IHttpRequestTaskComplete m_onHttpRequestTaskComplete;
	private URI m_uri;
	private ArrayList<NameValuePair> m_headers;

	public HttpGetRequestTask(URI uri,
			IHttpRequestTaskComplete onHttpRequestTaskComplete) {
		this(uri, onHttpRequestTaskComplete, null);
	}

	public HttpGetRequestTask(URI uri,
			IHttpRequestTaskComplete onHttpRequestTaskComplete,
			ArrayList<NameValuePair> headers) {
		m_uri = uri;
		m_onHttpRequestTaskComplete = onHttpRequestTaskComplete;
		m_headers = headers;
	}

	@Override
	protected String doInBackground(Void... params) {

		try {
			HttpClient httpclient = new DefaultHttpClient();
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
		} catch (Exception e) {
			Log.e("HttpRequestTaskException", e.toString());
		}

		return null;
	}

	@Override
	protected void onPostExecute(String response) {
		m_onHttpRequestTaskComplete.onHttpRequestTaskComplete(response);

	}

}
