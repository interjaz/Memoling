package app.memoling.android.helper;

import java.util.Date;

import android.util.Log;
import app.memoling.android.Config;

public class AppLog {

	public static void w(String tag, String msg) {
		tag = prepareTag(tag);
		msg = prepareMsg(msg);

		if (outToLogCat()) {
			Log.w(tag, msg);
		}
	}

	public static void w(String tag, String msg, Throwable tr) {
		tag = prepareTag(tag);
		msg = prepareMsg(msg);

		if (outToLogCat()) {
			Log.w(tag, msg, tr);
		}
	}

	public static void e(String tag, String msg) {
		tag = prepareTag(tag);
		msg = prepareMsg(msg);

		if (outToLogCat()) {
			Log.e(tag, msg);
		}
	}

	public static void e(String tag, String msg, Throwable tr) {
		tag = prepareTag(tag);
		msg = prepareMsg(msg);

		if (outToLogCat()) {
			Log.e(tag, msg, tr);
		}
	}

	public static void v(String tag, String msg) {
		tag = prepareTag(tag);
		msg = prepareMsg(msg);

		if (outToLogCat()) {
			Log.v(tag, msg);
		}
	}

	public static void v(String tag, String msg, Throwable tr) {
		tag = prepareTag(tag);
		msg = prepareMsg(msg);

		if (outToLogCat()) {
			Log.v(tag, msg, tr);
		}
	}

	private static String prepareTag(String tag) {
		return "Memoling " + tag;
	}

	private static String prepareMsg(String msg) {
		return "[" + DateHelper.toNormalizedString(new Date()) + "] " + msg;
	}

	private static boolean outToLogCat() {
		if (Config.Debug) {
			return true;
		}

		return false;
	}
}
