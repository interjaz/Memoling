package app.memoling.android.webservice;

import java.net.URI;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.util.Base64;
import app.memoling.android.Config;
import app.memoling.android.entity.SyncClient;
import app.memoling.android.helper.AppLog;
import app.memoling.android.webrequest.HttpPostRequestTask;
import app.memoling.android.webrequest.IHttpRequestTaskComplete;

public class WsSync {

	private final static String WsUrl = Config.WsUrlRoot + "/Sync.php";
	private final static int m_timeout = 60000;

	public interface ISyncRequestResult {
		void syncCompleted(byte[] gzipPackage);
	}
	
	public interface IRegisterRequestResult {
		void registerCompleted(Boolean success);
	}

	public static void registerRequest(SyncClient syncClient, final IRegisterRequestResult onComplete) {
		
		try {

			URI WsUri = new URI(WsUrl + "?action=register");
			String jsonSyncClient = syncClient.encode();
			NameValuePair postPackage = new BasicNameValuePair("syncClient", jsonSyncClient);

			new HttpPostRequestTask(WsUri, new IHttpRequestTaskComplete() {

				@Override
				public void onHttpRequestTaskComplete(String response) {
					try {
						boolean success = response.equalsIgnoreCase("true");
						onComplete.registerCompleted(success);
					} catch(Exception ex) {
						onComplete.registerCompleted(false);
					}
				}

				@Override
				public void onHttpRequestTimeout(Exception ex) {
					AppLog.e("SyncService", "register", ex);
					onComplete.registerCompleted(null);
				}

			}, null, m_timeout).execute(postPackage);

		} catch (Exception ex) {
			AppLog.e("SyncService", "sync", ex);
			onComplete.registerCompleted(null);
		}
	}

	//TODO: (1) Implement proper byte[] transmission not naive base 64
	//TODO: (2) When doing (1) make sure one thread is used so in SyncService thread switching could be avoided
	public static void syncRequest(byte[] gzipPackage, final ISyncRequestResult onComplete) {
		
		try {

			URI WsUri = new URI(WsUrl + "?action=syncBase64");
			String base64Package = Base64.encodeToString(gzipPackage, Base64.DEFAULT);
			NameValuePair postPackage = new BasicNameValuePair("syncPackage", base64Package);

			new HttpPostRequestTask(WsUri, new IHttpRequestTaskComplete() {

				@Override
				public void onHttpRequestTaskComplete(String response) {
					try {
						byte[] responsePackage = Base64.decode(response, Base64.DEFAULT);
						onComplete.syncCompleted(responsePackage);
					} catch(Exception ex) {
						onComplete.syncCompleted(null);
					}
				}

				@Override
				public void onHttpRequestTimeout(Exception ex) {
					AppLog.e("SyncService", "sync", ex);
					onComplete.syncCompleted(null);
				}

			}, null, m_timeout).execute(postPackage);

		} catch (Exception ex) {
			AppLog.e("SyncService", "sync", ex);
			onComplete.syncCompleted(null);
		}
	}
	
}
