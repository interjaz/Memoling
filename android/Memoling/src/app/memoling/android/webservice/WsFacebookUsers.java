package app.memoling.android.webservice;

import java.net.URI;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import app.memoling.android.Config;
import app.memoling.android.facebook.FacebookUser;
import app.memoling.android.helper.AppLog;
import app.memoling.android.webrequest.HttpPostRequestTask;
import app.memoling.android.webrequest.IHttpRequestTaskComplete;

public class WsFacebookUsers {

	private static final String WsUrl = Config.WsUrlRoot + "/FacebookUsers.php";
	private static final int m_timeout = 8000;

	public interface ILoginComplete {
		void onLoginComplete(Boolean result);
	}
	
	public static void login(FacebookUser user, final ILoginComplete onComplete) {

		try {
			
			String jsonUser = CanonicalConverter.facebookUserToWsJson(user).toString();
			NameValuePair postUser = new BasicNameValuePair("user", jsonUser);

			URI WsUri = new URI(WsUrl + "?action=login");
			
			new HttpPostRequestTask(WsUri, new IHttpRequestTaskComplete() {

				@Override
				public void onHttpRequestTaskComplete(String response) {
					boolean result = Boolean.parseBoolean(response);
					onComplete.onLoginComplete(result);
				}

				@Override
				public void onHttpRequestTimeout(Exception ex) {
					onComplete.onLoginComplete(null);
				}
				
			}, null, m_timeout, "UTF-8").execute(postUser);

		} catch (Exception ex) {
			AppLog.e("WsFacebookUsrs", "login", ex);
		}
	}

}
