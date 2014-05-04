package app.memoling.android.ui.control;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import app.memoling.android.R;

public class CancellableEditText extends EditText implements OnTouchListener  {
	
	private Drawable m_drawable; 
    private int m_padding = 10;
    
    boolean m_drawableSet = false;
	
	public CancellableEditText(Context context, AttributeSet attrs) {
		super(context, attrs);

		if(isInEditMode()) {
			return;
		}

		m_drawable = getResources().getDrawable(R.drawable.ic_clear_search_api_holo);
		m_drawable.setBounds(0,0,m_drawable.getMinimumWidth(), m_drawable.getMinimumHeight());
		
		setOnTouchListener(this);
		addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				if(s.length() > 0) {
					setCompoundDrawables(null, null, m_drawable, null);
				} else {
					setCompoundDrawables(null, null, null, null);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}
		});
	}

	
	
	@Override
    public boolean onTouch(final View v, final MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN && m_drawable != null) {
            final int x = (int) event.getX();
            final int y = (int) event.getY();
            final Rect bounds = m_drawable.getBounds();
            if (x >= (v.getRight() - bounds.width() - m_padding) && x <= (v.getRight() - v.getPaddingRight() + m_padding)
                    && y >= (v.getPaddingTop() - m_padding) && y <= (v.getHeight() - v.getPaddingBottom()) + m_padding) {
                return onDrawableTouch(event);
            }
        }
        return false;
    }

	boolean onDrawableTouch(MotionEvent event) {
		this.setText("");
		return true;
	}


	
}
