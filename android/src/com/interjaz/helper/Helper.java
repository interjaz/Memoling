package com.interjaz.helper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
}
