package app.memoling.android.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import app.memoling.android.R;

public class FbLoginActivity extends Activity {

	private final static String AppId = "620366261326135";
	
	public final static int CredentialsRequest = 0;
	public final static String AccessToken = "access_token";
	public final static String ExpiresIn = "expires_in";
	
	private WebView m_webView;
	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fblogin);
		
		
		String clientId = AppId;
		String redirectUrl = "https://www.facebook.com/connect/login_success.html";
		String scope = "";
		String responseType = "token";
		String facebookLoginUrl = buildLoginUrl(clientId, redirectUrl, scope, responseType);
		
		m_webView = (WebView)findViewById(R.id.fblogin_web);
		m_webView.getSettings().setJavaScriptEnabled(true);
		m_webView.getSettings().setSavePassword(false);
		m_webView.setVerticalScrollBarEnabled(false);
		m_webView.setHorizontalScrollBarEnabled(false);
		m_webView.setWebViewClient(new FbWebClient());

		m_webView.loadUrl(facebookLoginUrl);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	private static String buildLoginUrl(String clientId, String redirectUrl, String scope, String responseType) {
		return String.format("https://www.facebook.com/dialog/oauth?" +
				"client_id=%s&redirect_uri=%s&scope=%s&response_type=%s", clientId, redirectUrl, scope, responseType);
	}
	
	private class FbWebClient extends WebViewClient {
		
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			parseToken(url);
			return false;
		}

	}
	
	
	private void parseToken(String url) {
		
		if(url.contains(AccessToken)) {
			// Parse fragment as query string
			Uri uri=Uri.parse(url.replace('#', '?'));	
			String token = uri.getQueryParameter(AccessToken);
			long expiresIn = Long.parseLong(uri.getQueryParameter(ExpiresIn));
			
			Intent data = new Intent();
			data.putExtra(AccessToken, token);
			data.putExtra(ExpiresIn, expiresIn);
			
			this.setResult(RESULT_OK, data);
			finish();
		}
		
	}
	
}
