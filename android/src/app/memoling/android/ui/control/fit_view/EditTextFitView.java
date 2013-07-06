package app.memoling.android.ui.control.fit_view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

public class EditTextFitView extends EditText {
	private FitView<EditText> m_fitView;

	public EditTextFitView(Context context) {
		super(context);
		m_fitView = new FitView<EditText>(this, 0);
	}

	public EditTextFitView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		int orientation = 0;
		String strOrientation = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "orientation");
		if(strOrientation != null) {
			if(strOrientation.equals("1")) {
				orientation = 1;
			}
		} 
		
		m_fitView = new FitView<EditText>(this, orientation);
	}

	@SuppressLint("WrongCall")
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (m_fitView != null) {
			FitView.Pair pair = m_fitView.onMeasure(widthMeasureSpec, heightMeasureSpec);
			this.setMeasuredDimension(pair.first, pair.second);
		}
	}

	@Override
	protected void onTextChanged(final CharSequence text, final int start, final int before, final int after) {
		if (m_fitView != null) {
			m_fitView.onTextChanged(text, start, before, after);
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		if (m_fitView != null) {
			m_fitView.onSizeChanged(w, h, oldw, oldh);
		}
	}
}