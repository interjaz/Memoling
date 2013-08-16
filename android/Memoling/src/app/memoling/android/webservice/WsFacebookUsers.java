package app.memoling.android.webservice;

import java.net.URI;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import app.memoling.android.Config;
import app.memoling.android.facebook.FacebookUser;
import app.memoling.android.helper.AppLog;
import app.memoling.android.webrequest.HttpPostRequestTask;
import app.memoling.android.webrequest.IHttpRequestTaskComplete;

public class WsFacebookUsers {

	private static final String WsUrl = Config.WsUrlRoot + "/FacebookUsers.php";
	private static final int m_timeout = 8000;

	public interface ILoginComplete {
		void onLoginComplete(boolean result);
	}
	
	public void login(FacebookUser user, final ILoginComplete onComplete) {

		try {
			JSONObject jsonUser = new JSONObject();
			jsonUser.put("facebookUserId", user.getId());

			jsonUser.put("name", user.getName());
			jsonUser.put("firstName", user.getFirstName());
			jsonUser.put("lastName", user.getLastName());
			jsonUser.put("link", user.getLink());
			jsonUser.put("username", user.getUsername());

			JSONObject jsonHometown = new JSONObject();
			jsonHometown.put("facebookLocationId", user.getHometown().getId());
			jsonHometown.put("name", user.getHometown().getName());
			jsonUser.put("hometown", jsonHometown);
			
			jsonUser.put("locationId", user.getLocation().getId());
			jsonUser.put("gender", user.getGender());
			jsonUser.put("timezone", user.getTimezone());
			jsonUser.put("locale", user.getLocale());
			jsonUser.put("updatedTime", user.getUpdatedTime());
			jsonUser.put("verified", user.getVerified());

			JSONObject jsonLocation = new JSONObject();
			jsonLocation.put("facebookLocationId", user.getLocation().getId());
			jsonLocation.put("name", user.getLocation().getName());

			jsonUser.put("location", jsonLocation);
			NameValuePair postUser = new BasicNameValuePair("user", jsonUser.toString());

			URI WsUri = new URI(WsUrl + "?action=login");
			
			new HttpPostRequestTask(WsUri, new IHttpRequestTaskComplete() {

				@Override
				public void onHttpRequestTaskComplete(String response) {
					boolean result = Boolean.parseBoolean(response);
					onComplete.onLoginComplete(result);
				}

				@Override
				public void onHttpRequestTimeout(Exception ex) {
					onComplete.onLoginComplete(false);
				}
				
			}, null, m_timeout, "UTF-8").execute(postUser);

		} catch (Exception ex) {
			AppLog.e("WsFacebookUsrs", "login", ex);
		}
	}

}
