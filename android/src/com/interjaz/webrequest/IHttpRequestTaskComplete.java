package com.interjaz.webrequest;

public interface IHttpRequestTaskComplete {

	public void onHttpRequestTaskComplete(String response);
	
	public void onHttpRequestTimeout(Exception ex);
	
}
