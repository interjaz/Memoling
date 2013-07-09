package app.memoling.android.helper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;

public class Helper {

	public static String AppRoot = Environment.getExternalStorageDirectory() + "/memoling";

	public static <T> T coalesce(T notNull, T otherwise) {
		if (notNull != null) {
			return notNull;
		} else {
			return otherwise;
		}
	}

	public static boolean nullOrWhitespace(String s) {
		if (s == null) {
			return true;
		}

		return s.trim().length() == 0;
	}

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public static int getActivityWidth(Activity activity) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2) {
			return activity.getWindowManager().getDefaultDisplay().getWidth();
		} else {
			Point size = new Point();
			activity.getWindowManager().getDefaultDisplay().getSize(size);
			return size.x;
		}
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public static int getActivityHeight(Activity activity) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2) {
			return activity.getWindowManager().getDefaultDisplay().getHeight();
		} else {
			Point size = new Point();
			activity.getWindowManager().getDefaultDisplay().getSize(size);
			return size.y;
		}
	}

	public static View getActivityRootView(Activity activity) {
		return ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
	}

	public static String getPathFromIntent(Context context, Intent intent) {
		Uri uri = intent.getData();
		if ("content".equalsIgnoreCase(uri.getScheme())) {
			String[] projection = { "_data" };
			Cursor cursor = null;

			try {
				cursor = context.getContentResolver().query(uri, projection, null, null, null);
				int column_index = cursor.getColumnIndexOrThrow("_data");
				if (cursor.moveToFirst()) {
					return cursor.getString(column_index);
				}
			} catch (Exception e) {
				// Eat it
			}
		} else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	}

	public static boolean apkInstalled(Context context, String uri) {
		PackageManager pm = context.getPackageManager();
		try {
			pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
			return true;
		} catch (PackageManager.NameNotFoundException e) {
			return false;
		}
	}

	public static Bitmap invertBitmapColors(Bitmap src) {
		Bitmap output = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());
		int A, R, G, B;
		int pixelColor;
		int height = src.getHeight();
		int width = src.getWidth();

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				pixelColor = src.getPixel(x, y);
				A = Color.alpha(pixelColor);

				R = 255 - Color.red(pixelColor);
				G = 255 - Color.green(pixelColor);
				B = 255 - Color.blue(pixelColor);

				output.setPixel(x, y, Color.argb(A, R, G, B));
			}
		}

		return output;
	}

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public static void copyToClipboard(Context context, String text) {
		int sdk = android.os.Build.VERSION.SDK_INT;
		if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
			android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context
					.getSystemService(Context.CLIPBOARD_SERVICE);
			clipboard.setText(text);
		} else {
			String label;
			if (text.length() > 5) {
				label = text.substring(0, 5) + "...";
			} else {
				label = text;
			}

			android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context
					.getSystemService(Context.CLIPBOARD_SERVICE);
			android.content.ClipData clip = android.content.ClipData.newPlainText(label, text);
			clipboard.setPrimaryClip(clip);
		}
	}

	// This is helpful if received UTF-8 may be broken
	// On Android v.10 Bing Translate does not work without this function being
	// applied
	public static String removeBom(String str) {

		byte[] isBom = str.substring(0, 1).getBytes();
		if (isBom.length == 3 && isBom[0] == (byte) 0xEF && isBom[1] == (byte) 0xBB && isBom[2] == (byte) 0xBF) {
			return str.substring(1);
		}

		return str;
	}

	public static class Profile {
		private long start;

		public void start() {
			start = System.currentTimeMillis();
		}

		public void stop() {
			long last = System.currentTimeMillis() - start;
			AppLog.e(":: PROFILE ::", Integer.toString((int) last));
		}

		public void restart() {
			stop();
			start();
		}
	}

	public static PackageInfo getPackage(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			// Should not happen
			return null;
		}
	}

	public static boolean isFirstStart(Context context) {
		Preferences preferences = new Preferences(context);
		String lastVersion = preferences.get("VERSION");
		if (getPackage(context).versionName.equals(lastVersion)) {
			return false;
		}

		return true;
	}

	public static void setFirstStartSuccessful(Context context) {
		Preferences preferences = new Preferences(context);
		preferences.set("VERSION", getPackage(context).versionName);
	}
}
