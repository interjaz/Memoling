package com.interjaz.ui.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ListView;

import com.interjaz.IGet;

public class ScrollableModifiableComplexTextAdapter<T extends IGet<String>> extends ModifiableComplexTextAdapter<T>
		implements OnTouchListener {

	private ViewGroup m_parentView;
	private MotionEvent m_lastEvent;
	private OnScrollFinishedListener m_scrollListener;

	public final static int Y_UNKNOWN = 0;
	public final static int Y_TOP = 1;
	public final static int Y_BOTTOM = 2;

	public interface OnScrollFinishedListener {
		void onScrollFinished(float x, float y, int yPosition);
	}

	public ScrollableModifiableComplexTextAdapter(Context context, int resourceId, int[] innerResourceIds) {
		super(context, resourceId, innerResourceIds);
	}

	public ScrollableModifiableComplexTextAdapter(Context context, int resourceId, int[] innerResourceIds,
			Typeface[] fonts) {
		super(context, resourceId, innerResourceIds, fonts);
	}

	public ScrollableModifiableComplexTextAdapter(Context context, int resourceId, int[] innerResourceIds,
			Typeface[] fonts, boolean alternateBackground) {
		super(context, resourceId, innerResourceIds, fonts, alternateBackground);
	}

	public ScrollableModifiableComplexTextAdapter(Context context, int resourceId, int[] innerResourceIds,
			ArrayList<T> data) {
		super(context, resourceId, innerResourceIds, data);
	}

	@Override
	protected View getViewDefinition(int position, View convertView, ViewGroup parent) {

		if (m_parentView != parent) {
			if(m_parentView != null) {
				m_parentView.setOnTouchListener(null);
			}
			m_parentView = parent;
			m_parentView.setOnTouchListener(this);
		}

		return super.getViewDefinition(position, convertView, parent);
	}

	public void setOnScrollListener(OnScrollFinishedListener listener) {
		m_scrollListener = listener;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		if (event.getActionMasked() == MotionEvent.ACTION_UP) {
			onScrollFinished(v, event);
		}
		m_lastEvent = event;

		return false;
	}

	private void onScrollFinished(View v, MotionEvent event) {

		if (m_lastEvent == null) {
			return;
		}

		int pointerCount = event.getPointerCount();
		if (m_lastEvent.getActionMasked() != MotionEvent.ACTION_MOVE && pointerCount > 0) {
			float yMove = event.getY(0);
			float xMove = event.getX(0);

			int yPosition = Y_UNKNOWN;
			ListView lv = (ListView) v;
	
			
			if (lv != null) {
				if (lv.getLastVisiblePosition() == lv.getAdapter().getCount() - 1
						&& lv.getChildAt(lv.getChildCount() - 1).getBottom() <= lv.getHeight()) {
					yPosition = Y_BOTTOM;
				}
			}

			if (m_scrollListener != null) {
				m_scrollListener.onScrollFinished(xMove, yMove, yPosition);
			}
		}

	}
}
