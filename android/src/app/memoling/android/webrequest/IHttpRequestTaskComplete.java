package app.memoling.android.webrequest;

public interface IHttpRequestTaskComplete {

	public void onHttpRequestTaskComplete(String response);
	
	public void onHttpRequestTimeout(Exception ex);
	
}
