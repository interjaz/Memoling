package com.interjaz.ui.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.interjaz.IActive;
import com.interjaz.IGet;

public class ModifiableComplexTextAdapter<T extends IGet<String>> extends ModifiableAdapter<T> {

	private int[] m_innerResources;
	private Typeface[] m_fonts;
	private boolean m_alternateBackground;
	
	public ModifiableComplexTextAdapter(Context context, int resourceId, int[] innerResourceIds) {
		super(context, resourceId);
		m_innerResources = innerResourceIds;
	}

	public ModifiableComplexTextAdapter(Context context, int resourceId, int[] innerResourceIds, Typeface[] fonts) {
		super(context, resourceId);
		m_innerResources = innerResourceIds;
		m_fonts = fonts;
	}

	public ModifiableComplexTextAdapter(Context context, int resourceId, int[] innerResourceIds, Typeface[] fonts, boolean alternateBackground) {
		super(context, resourceId);
		m_innerResources = innerResourceIds;
		m_fonts = fonts;
		m_alternateBackground = alternateBackground;
	}

	public ModifiableComplexTextAdapter(Context context, int resourceId, int[] innerResourceIds, ArrayList<T> data) {
		super(context, resourceId, data);
		m_innerResources = innerResourceIds;
	}

	@Override
	protected View getViewDefinition(int position, View convertView, ViewGroup parent) {
		View view;

		if (convertView == null) {
			view = m_inflater.inflate(m_resourceId, parent, false);
		} else {
			view = convertView;
		}

		try {
			
			T object = getItem(position);
			for (int i = 0; i < m_innerResources.length; i++) {

				TextView text = (TextView) view.findViewById(m_innerResources[i]);

				if (m_fonts != null) {
					text.setTypeface(m_fonts[i]);
				}

				if (object.get(i) instanceof CharSequence) {
					text.setText((CharSequence) object.get(i));
				} else {
					text.setText(object.get(i).toString());
				}

				if (object instanceof IActive) {
					boolean active = ((IActive) object).isActive();
					if (!active) {
						text.setPaintFlags(text.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
					}
				}
			}

			if (m_alternateBackground && position % 2 == 1) {
				if (view != null) {
					Drawable background = view.getBackground();
					if (background != null) {
						view.getBackground().setAlpha(127);
					} else {
						view.setBackgroundColor(0x44000000);
					}
				}
			}

		} catch (ClassCastException e) {
			Log.e("ArrayAdapter", "You must supply a resource ID for a TextView");
			throw new IllegalStateException("ArrayAdapter requires the resource ID to be a TextView", e);
		}

		return view;
	}
}
