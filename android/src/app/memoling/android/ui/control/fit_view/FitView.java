package app.memoling.android.ui.control.fit_view;

import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.util.TypedValue;
import android.view.View.MeasureSpec;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FitView<T extends TextView> {

	private T m_view;
	private int m_orientation;
	private Rect m_bounds;
	private Pair m_pair;
	private Paint mTestPaint;
	
	public FitView(T view, int orientation) {
		m_view = view;
		m_orientation = orientation;
		mTestPaint = new Paint();
		mTestPaint.set(m_view.getPaint());
		m_bounds = new Rect();
		m_pair = new Pair();
		// max size defaults to the initially specified text size unless it
		// is too small
	}

	/*
	 * Re size the font so the specified text fits in the text box assuming the
	 * text box is the specified width.
	 */
	private void refitText(String text, int textWidth, int textHeight) {
		if (textWidth <= 0)
			return;
		
		int taget = 0;

		if (m_orientation == LinearLayout.HORIZONTAL) {
			taget = textWidth - m_view.getPaddingLeft() - m_view.getPaddingRight();
		} else {
			taget = textHeight - m_view.getPaddingTop() - m_view.getPaddingBottom();
		}

		float hi = 500;
		float lo = 2;
		final float threshold = 0.5f; // How close we have to be

		mTestPaint.set(m_view.getPaint());

		while ((hi - lo) > threshold) {
			float size = (hi + lo) / 2;
			mTestPaint.setTextSize(size);
			mTestPaint.getTextBounds(text, 0, text.length(), m_bounds);

			int current;
			if (m_orientation == LinearLayout.HORIZONTAL) {
				current = m_bounds.width();
			} else {
				current = m_bounds.height();
			}

			if (current >= taget) {
				hi = size; // too big
			} else {
				lo = size; // too small
			}
		}
		// Use lo so that we undershoot rather than overshoot
		m_view.setTextSize(TypedValue.COMPLEX_UNIT_PX, lo);
	}

	public Pair onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		
		int width = 0;
		int height = 0;
		if (m_orientation == LinearLayout.HORIZONTAL) {
			width = MeasureSpec.getSize(widthMeasureSpec);
			height = m_view.getMeasuredHeight();
		} else {
			width = m_view.getMeasuredWidth();
			height = MeasureSpec.getSize(heightMeasureSpec);
		}
		refitText(m_view.getText().toString(), width, height);
		// setMeasuredDimension
		m_pair.first = width;
		m_pair.second = height;
		return m_pair;
	}

	public void onTextChanged(final CharSequence text, final int start, final int before, final int after) {
		//refitText(text.toString(), m_view.getWidth(), m_view.getHeight());
	}

	public void onSizeChanged(int w, int h, int oldw, int oldh) {
		
		if (w != oldw) {
			refitText(m_view.getText().toString(), w, h);
		}
	}

	public static class Pair {
		public int first;
		public int second;
	}
}
