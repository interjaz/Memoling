package com.facebook.samples.sessionlogin;

import android.os.Environment;

public class Config {

	//
	// Environment
	//	
	public final static boolean Debug = true;
	public final static String AppPath = Environment.getExternalStorageDirectory() + "/memoling";
	
	public final static String DatabaseName = "TranslateMemo";
	public final static int DatabaseVersion = 4;
	
	//
	// Webservices
	//
	public final static int MinNumberOfMemosForUpload = 20;
	
	// Live
	//public final static String WsUrlRoot = "http://ec2-79-125-102-130.eu-west-1.compute.amazonaws.com/memoling/webservice";
	// Dev
	 public final static String WsUrlRoot = "http://192.168.1.67:8080/v2/webservice";
	

	//
	// Translator
	//	
	public final static String BingTranslateClientId = "TranslateMemo";
	public final static String BingTranslateClientSecret = "87dfyqGFVxbTVyrqlo2YV+AF2An3gBIH2eOIgI8UPs4=";

	//
	// Facebook
	//
	public final static String FacebookApplicationId = "620366261326135";
	
	public final static String FacebookRedirectUri = "http://memoling.com";
	
	//
	// Ads
	//
	public final static boolean EnableAds = false;
	public final static String AdUnitId = "a151d4995a52331";
	public final static String AdTestDeviceId = "1D422D9852675CECF96B500DB4EC80F3";
		
	//
	// Quizlet
	//
	
	public final static String QuizletClientId = "neETbMxZ8R";
	public final static String QuizletClientSecret = "HtBPGOHimWdpUmmHy3VQIQ";
}
