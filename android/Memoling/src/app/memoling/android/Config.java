package app.memoling.android;

import java.util.Date;

import android.os.Environment;

public class Config {

	//
	// Environment
	//	
	public final static boolean Debug = true;
	public final static String AppPath = Environment.getExternalStorageDirectory() + "/memoling";
	
	public final static String DatabaseName = "TranslateMemo";
	public final static int DatabaseVersion = 5;
	
	//
	// Webservices
	//
	public final static int MinNumberOfMemosForUpload = 20;
	
	// Live
	public final static String WsUrlRoot = "http://memoling.com/webservice";
	// Dev
	//public final static String WsUrlRoot = "http://192.168.1.67:8080/v2/webservice";
	

	//
	// Translatoruruchomiony
	//	
	public final static String BingTranslateClientId = "TranslateMemo";
	public final static String BingTranslateClientSecret = "87dfyqGFVxbTVyrqlo2YV+AF2An3gBIH2eOIgI8UPs4=";

	//
	// Facebook
	//
	
	// This has been moved to Meta-Data in AndroidManifest.xml
	public final static String FacebookApplicationId = null;
	
	public final static String FacebookRedirectUri = "http://memoling.com";
	
	//
	// Ads
	//
	//public final static boolean EnableAds = true;
	private final static long EnableAdsFirstOfApril = 2396310400000L;
	public static boolean EnableAds = new Date().after(new Date(EnableAdsFirstOfApril));
	public final static String AdUnitId = "a151d4995a52331";
	public final static String AdTestDeviceId = "1D422D9852675CECF96B500DB4EC80F3";
		
	//
	// Quizlet
	//
	
	public final static String QuizletClientId = "neETbMxZ8R";
	public final static String QuizletClientSecret = "HtBPGOHimWdpUmmHy3VQIQ";
}
