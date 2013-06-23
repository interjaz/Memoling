package app.memoling.android;

import android.os.Environment;

public class Config {
	
	public final static String AppPath = Environment.getExternalStorageDirectory() + "/memoling";
	public final static String WsUrlRoot = "http://192.168.1.70:8080/webservice";

	public final static int MinNumberOfMemosForUpload = 20;
}
