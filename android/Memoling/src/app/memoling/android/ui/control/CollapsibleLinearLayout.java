package app.memoling.android.ui.control;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

public class CollapsibleLinearLayout extends LinearLayout {
	
	private boolean m_hideStarted = false;
	private boolean m_showStarted = false;
	private boolean m_hidden = false;
	
	public CollapsibleLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void hide() {
		if(m_hideStarted || m_hidden) {
			return;
		}
		
		final int height = this.getMeasuredHeight();
		final android.view.ViewGroup.LayoutParams params = this.getLayoutParams();
		
		Animation animation = new Animation() {
			
			@Override
			protected void applyTransformation(float interpolatedTime, Transformation t) {	
				m_hideStarted = true;
				
				params.height = height - (int)(height * interpolatedTime);
				CollapsibleLinearLayout.this.requestLayout();
				
				if(interpolatedTime == 1.0f) {
					m_hidden = true;
					m_hideStarted = false;
				}
			}

	        @Override
	        public boolean willChangeBounds() {
	            return true;
	        }
		};
		this.clearAnimation();
		this.setAnimation(animation);
		
		animation.setDuration(1000);
		animation.setFillAfter(true);
		animation.start();
	}

	public void show() {
		if(m_showStarted || !m_hidden) {
			return;
		}
		
	    this.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		final android.view.ViewGroup.LayoutParams params = this.getLayoutParams();
	    final int height = this.getMeasuredHeight();

		Animation animation = new Animation() {
						
			@Override
			protected void applyTransformation(float interpolatedTime, Transformation t) {		
				m_showStarted = true;
				
				params.height = interpolatedTime == 1
	                    ? LayoutParams.WRAP_CONTENT
                        : (int)(height * interpolatedTime);
				CollapsibleLinearLayout.this.requestLayout();
				
				if(interpolatedTime == 1.0f) {			
					m_hidden = false;
					m_showStarted = false;		
				}
			}

	        @Override
	        public boolean willChangeBounds() {
	            return true;
	        }
		};

		this.clearAnimation();
		this.setAnimation(animation);
		
		animation.setDuration(1000);
		animation.setFillAfter(true);
		animation.startNow();
	}

}
