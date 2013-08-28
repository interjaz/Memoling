package app.memoling.android.crossword;

import java.util.Locale;

public class Point {
	public int y;
	public int x;

	public Point(int y, int x) {
		this.y = y;
		this.x = x;
	}

	public boolean isVaild() {
		return y != Integer.MAX_VALUE && x != Integer.MAX_VALUE;
	}

	@Override
	public String toString() {
		return String.format(Locale.US, "y=%d,x=%d", y, x);
	}
}