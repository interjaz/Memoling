package app.memoling.android.ui.adapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import app.memoling.android.helper.AppLog;


public class ModifiableComplexTextAdapter<T extends IGet<String>> extends ModifiableAdapter<T> {

	private int[] m_innerResources;
	private Typeface[] m_fonts;
	private boolean m_alternateBackground;
	private Drawable m_alternateBackgroundDrawable = new ColorDrawable(0x44000000);
	private boolean m_originalBackgroundSet;
	private Drawable m_originalBackgroundDrawable;

	public ModifiableComplexTextAdapter(Context context, int resourceId, int[] innerResourceIds) {
		super(context, resourceId);
		m_innerResources = innerResourceIds;
	}

	public ModifiableComplexTextAdapter(Context context, int resourceId, int[] innerResourceIds, Typeface[] fonts) {
		super(context, resourceId);
		m_innerResources = innerResourceIds;
		m_fonts = fonts;
	}

	public ModifiableComplexTextAdapter(Context context, int resourceId, int[] innerResourceIds, Typeface[] fonts,
			boolean alternateBackground) {
		super(context, resourceId);
		m_innerResources = innerResourceIds;
		m_fonts = fonts;
		m_alternateBackground = alternateBackground;
	}

	public ModifiableComplexTextAdapter(Context context, int resourceId, int[] innerResourceIds, ArrayList<T> data) {
		super(context, resourceId, data);
		m_innerResources = innerResourceIds;
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
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
					} else {
						text.setPaintFlags(text.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
					}
				}
			}

			if (!m_originalBackgroundSet && position % 2 == 0) {
				m_originalBackgroundDrawable = view.getBackground();
				m_originalBackgroundSet = true;
			}

			if (m_alternateBackground) {
				if (position % 2 == 1) {
					if (view != null) {
						Drawable background = view.getBackground();
						if (background != null && background != m_alternateBackgroundDrawable) {
							view.getBackground().setAlpha(127);
						} else {
							if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
								view.setBackgroundDrawable(m_alternateBackgroundDrawable);
							} else {
								view.setBackground(m_alternateBackgroundDrawable);
							}
						}
					}
				} else {
					if (view != null) {
						Drawable background = view.getBackground();
						if (background != null && background != m_originalBackgroundDrawable) {
							view.getBackground().setAlpha(255);
							// This need to be done as well
							if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
								view.setBackgroundDrawable(m_originalBackgroundDrawable);
							} else {
								view.setBackground(m_originalBackgroundDrawable);
							}
						}
					}
				}
			}

		} catch (ClassCastException e) {
			AppLog.e("ArrayAdapter", "You must supply a resource ID for a TextView");
			throw new IllegalStateException("ArrayAdapter requires the resource ID to be a TextView", e);
		}

		return view;
	}
}
