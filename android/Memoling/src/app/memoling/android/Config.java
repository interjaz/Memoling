package app.memoling.android;

import android.os.Environment;

public class Config {

	public final static String AppPath = Environment.getExternalStorageDirectory() + "/memoling";
	
	// Live
	public final static String WsUrlRoot = "http://ec2-79-125-102-130.eu-west-1.compute.amazonaws.com/memoling/webservice";
	// Dev
//	public final static String WsUrlRoot = "http://192.168.1.73:8080/memoling_service/webservice";
	
	
	public final static int MinNumberOfMemosForUpload = 20;

	public final static String DatabaseName = "TranslateMemo";
	public final static int DatabaseVersion = 3;

	public final static boolean Debug = false;
	
	public final static boolean EnableAds = true;
	public final static String AdUnitId = "a151d4995a52331";
	public final static String AdTestDeviceId = "1D422D9852675CECF96B500DB4EC80F3";
}
